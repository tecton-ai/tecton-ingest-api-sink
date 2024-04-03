package com.tecton.ingestclient.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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

/**
 * Processor to handle SinkRecord collections in batches.
 */
public class BatchRecordProcessor implements IRecordProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(BatchRecordProcessor.class);

  private final TectonHttpSinkConnectorConfig config;
  private final IRecordConverter tectonConverter;
  private final TectonHttpClient httpClient;
  private final ErrantRecordReporter reporter;

  /**
   * Constructs a {@code BatchRecordProcessor} with the given dependencies.
   *
   * @param httpClient The client to use for HTTP operations.
   * @param reporter The errant record reporter.
   * @param config Configuration for the processor.
   */
  public BatchRecordProcessor(TectonHttpClient httpClient, ErrantRecordReporter reporter,
      TectonHttpSinkConnectorConfig config) {
    this.config = config;
    this.httpClient = httpClient;
    this.reporter = reporter;
    this.tectonConverter = new TectonRecordConverter(config);
  }

  /**
   * Processes a collection of SinkRecords, transforms them to Tecton API requests, and sends them.
   *
   * @param records Collection of SinkRecords to be processed.
   */
  @Override
  public void processRecords(Collection<SinkRecord> records) {
    List<TectonApiRequest> batchRequests = partitionIntoBatches(records).stream()
        .map(this::prepareBatchRequest).collect(Collectors.toList());

    processBatchRequests(batchRequests);
  }

  /**
   * Performs any cleanup operations. This typically involves closing resources or connections.
   */
  @Override
  public void close() {
    tectonConverter.close();
  }

  /**
   * Partitions the collection of SinkRecords into batches.
   */
  private List<List<SinkRecord>> partitionIntoBatches(Collection<SinkRecord> records) {
    final int batchSize = config.batchMaxSize;
    List<SinkRecord> recordList = new ArrayList<>(records);

    return IntStream.range(0, (recordList.size() + batchSize - 1) / batchSize).mapToObj(
        i -> recordList.subList(i * batchSize, Math.min(recordList.size(), (i + 1) * batchSize)))
        .collect(Collectors.toList());
  }

  /**
   * Transforms a batch of SinkRecords into a Tecton API request.
   */
  private TectonApiRequest prepareBatchRequest(List<SinkRecord> batch) {
    TectonApiRequest.Builder requestBuilder = new TectonApiRequest.Builder()
        .workspaceName(config.workspaceName).dryRun(config.dryRunEnabled);

    batch.forEach(record -> processSingleRecord(record, requestBuilder));
    return requestBuilder.build();
  }

  /**
   * Processes an individual SinkRecord.
   */
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

  /**
   * Validates and processes a TectonRecord.
   */
  private void validateAndProcessRecordData(SinkRecord record, TectonRecord tectonRecord,
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

  /**
   * Logs the processed record data if loggingEventDataEnabled is enabled. Intended for debugging
   * purposes and could expose sensitive information to the logs.
   */
  private void logProcessedRecord(TectonRecord tectonRecord) {
    if (config.loggingEventDataEnabled) {
      LOG.debug("Processed Tecton Record: {}", tectonRecord);
    }
  }

  /**
   * Derive the PushSourceName from the source topic if one is not already configured. Note the
   * PushSource(s) must be pre-configured in Tecton.
   */
  private String resolvePushSourceName(SinkRecord record) {
    return Optional.ofNullable(config.pushSourceName).filter(name -> !name.trim().isEmpty())
        .orElseGet(() -> {
          LOG.debug("pushSourceName is not set in config, using the topic name: {}",
              record.topic());
          return record.topic();
        });
  }

  /**
   * Processes the batch requests synchronously or asynchronously depending on configuration.
   */
  private void processBatchRequests(List<TectonApiRequest> batchRequests) {
    if (config.httpAsyncEnabled) {
      List<CompletableFuture<TectonApiResponse>> futures = httpClient.sendAsyncBatch(batchRequests);
      futures.forEach(this::handleFuture);
    } else {
      batchRequests.forEach(httpClient::sendSync);
    }
  }

  /**
   * Handles asynchronous response exception.
   */
  private void handleFuture(CompletableFuture<TectonApiResponse> future) {
    future.exceptionally(ex -> {
      LOG.error("Error sending batch to Tecton", ex);
      return null;
    });
  }

  /**
   * Report failed records to the Kafka Connect ErrantRecordReporter if configured.
   */
  private void reportErrantRecord(SinkRecord record, Exception e) {
    if (reporter != null) {
      reporter.report(record, e);
    } else {
      throw new ConnectException("Error processing record", e);
    }
  }

  /**
   * Log detailed Sink Record information. Beware, logs record value which may contain sensitive
   * information.
   */
  private void logDetailedRecordInfo(SinkRecord record) {
    LOG.debug(
        "Detailed record info: Topic: {}, Partition: {}, Offset: {}, Key: {}, Value: {}, Timestamp: {}, Headers: {}",
        record.topic(), record.kafkaPartition(), record.kafkaOffset(), record.key(), record.value(),
        record.timestamp() != null ? new Date(record.timestamp()) : "null", record.headers());
  }
}
