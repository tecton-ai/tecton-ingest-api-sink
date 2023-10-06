package com.tecton.ingestclient.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.sink.ErrantRecordReporter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tecton.ingestclient.client.TectonApiRequest;
import com.tecton.ingestclient.client.TectonApiResponse;
import com.tecton.ingestclient.client.TectonHttpClient;
import com.tecton.ingestclient.converter.IRecordConverter;
import com.tecton.ingestclient.converter.TectonRecord;
import com.tecton.ingestclient.converter.TectonRecordConverter;
import com.tecton.kafka.connect.TectonHttpSinkConnectorConfig;


public class BatchRecordProcessor implements IRecordProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(BatchRecordProcessor.class);

    private final TectonHttpSinkConnectorConfig config;
    private final IRecordConverter tectonConverter;
    private final TectonHttpClient httpClient;
    private final ErrantRecordReporter reporter;

    public BatchRecordProcessor(
            TectonHttpClient httpClient, 
            ErrantRecordReporter reporter, 
            TectonHttpSinkConnectorConfig config) {
        this.config = config;
        this.httpClient = httpClient;
        this.reporter = reporter;
        this.tectonConverter = new TectonRecordConverter(config);
    }

    @Override
    public void processRecords(Collection<SinkRecord> records) {
        List<TectonApiRequest> batchRequests = partitionIntoBatches(records).stream()
            .map(this::prepareBatchRequest)
            .collect(Collectors.toList());

        processBatchRequests(batchRequests);
    }

    @Override
    public void close() {
        if (tectonConverter != null) {
            tectonConverter.close();
        }
    }

    private List<List<SinkRecord>> partitionIntoBatches(Collection<SinkRecord> records) {
        final int batchSize = config.batchMaxSize;
        List<SinkRecord> recordList = new ArrayList<>(records);

        return IntStream.range(0, (recordList.size() + batchSize - 1) / batchSize)
            .mapToObj(i -> recordList.subList(i * batchSize, 
                    Math.min(recordList.size(), (i + 1) * batchSize)))
            .collect(Collectors.toList());
    }

    private TectonApiRequest prepareBatchRequest(List<SinkRecord> batch) {
        TectonApiRequest.Builder requestBuilder = new TectonApiRequest.Builder()
            .workspaceName(config.workspaceName)
            .dryRun(config.dryRunEnabled);

        batch.forEach(record -> processSingleRecord(record, requestBuilder));
        return requestBuilder.build();
    }

    private void processSingleRecord(SinkRecord record, TectonApiRequest.Builder requestBuilder) {
        if (config.loggingEventDataEnabled) {
            logDetailedRecordInfo(record);
        }

        try {
            TectonRecord tectonRecord = tectonConverter.convert(record);
            validateAndProcessRecordData(record, tectonRecord, requestBuilder);
        } catch (Exception e) {
            reportErrantRecord(record, e);
        }
    }

    private void validateAndProcessRecordData(
            SinkRecord record,
            TectonRecord tectonRecord,
            TectonApiRequest.Builder requestBuilder) {
        if (!tectonRecord.isValid()) {
            LOG.warn("Invalid or empty record skipped from topic: {}", record.topic());
            reportErrantRecord(record, new DataException("Invalid TectonRecord."));
            return;
        }

        logProcessedRecord(tectonRecord);

        String pushSourceName = resolvePushSourceName(record);
        TectonApiRequest.RecordWrapper wrappedRecord = 
            new TectonApiRequest.RecordWrapper(pushSourceName, tectonRecord);

        requestBuilder.addRecord(wrappedRecord);
    }

    private void logProcessedRecord(TectonRecord tectonRecord) {
        if (config.loggingEventDataEnabled) {
            LOG.debug("Processed Tecton Record: {}", tectonRecord);
        }
    }

    private String resolvePushSourceName(SinkRecord record) {
        return Optional.ofNullable(config.pushSourceName)
            .filter(name -> !name.trim().isEmpty())
            .orElseGet(() -> {
                LOG.debug("pushSourceName is not set in config, using the topic name: {}", record.topic());
                return record.topic();
            });
    }

    private void processBatchRequests(List<TectonApiRequest> batchRequests) {
      if (config.httpAsyncEnabled) {
          List<CompletableFuture<TectonApiResponse>> futures = httpClient.sendAsyncBatch(batchRequests);
          futures.forEach(this::handleFuture);
      } else {
          batchRequests.forEach(httpClient::sendSync);
      }
    }

    private void handleFuture(CompletableFuture<TectonApiResponse> future) {
        future.exceptionally(ex -> {
            LOG.error("Error sending batch to Tecton", ex);
            return null;
        });
    }

    private void reportErrantRecord(SinkRecord record, Exception e) {
        if (reporter != null) {
            reporter.report(record, e);
        } else {
            throw new ConnectException("Error processing record", e);
        }
    }

    private void logDetailedRecordInfo(SinkRecord record) {
        LOG.debug(
            "Detailed record info: Topic: {}, Partition: {}, Offset: {}, Key: {}, Value: {}, Timestamp: {}, Headers: {}",
            record.topic(),
            record.kafkaPartition(),
            record.kafkaOffset(),
            record.key(),
            record.value(),
            record.timestamp() != null ? new Date(record.timestamp()) : "null",
            record.headers());
    }
}

