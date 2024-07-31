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
import org.apache.kafka.connect.errors.RetriableException;
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
  private final Optional<ErrantRecordReporter> reporter;

  /**
   * Constructs a {@code BatchRecordProcessor} with the given dependencies.
   *
   * @param httpClient The client to use for HTTP operations.
   * @param reporter The errant record reporter.
   * @param config Configuration for the processor.
   */
  public BatchRecordProcessor(final TectonHttpClient httpClient,
      final ErrantRecordReporter reporter, final TectonHttpSinkConnectorConfig config) {
    this.config = config;
    this.httpClient = httpClient;
    this.reporter = Optional.ofNullable(reporter);
    this.tectonConverter = new TectonRecordConverter(config);
  }

  /**
   * Processes a collection of SinkRecords, transforms them to Tecton API requests, and sends them.
   *
   * @param records Collection of SinkRecords to be processed.
   * @throws ConnectException if a non-retriable error occurs.
   * @throws RetriableException if a retriable error occurs.
   */
  @Override
  public void processRecords(final Collection<SinkRecord> records)
      throws ConnectException, RetriableException {
    final List<SinkRecord> validRecords = new ArrayList<>();

    for (SinkRecord record : records) {
      try {
        processSingleRecord(record, validRecords);
      } catch (Exception e) {
        reportErrantRecord(record, e);
      }
    }

    final List<TectonApiRequest> batchRequests = partitionIntoBatches(validRecords).stream()
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
   *
   * @param records Collection of SinkRecords to be partitioned.
   * @return List of batches.
   */
  private List<List<SinkRecord>> partitionIntoBatches(final List<SinkRecord> records) {
    final int batchSize = config.batchMaxSize;
    LOG.debug("Partitioning records into batches with max size: {}", batchSize);

    final List<SinkRecord> recordList = new ArrayList<>(records);

    return IntStream.range(0, (recordList.size() + batchSize - 1) / batchSize).mapToObj(i -> {
      int start = i * batchSize;
      int end = Math.min(recordList.size(), (i + 1) * batchSize);
      LOG.debug("Creating batch from index {} to {}", start, end);
      return recordList.subList(start, end);
    }).collect(Collectors.toList());
  }

  /**
   * Transforms a batch of SinkRecords into a Tecton API request.
   *
   * @param batch Batch of SinkRecords.
   * @return TectonApiRequest.
   */
  private TectonApiRequest prepareBatchRequest(final List<SinkRecord> batch) {
    LOG.debug("Preparing batch request with size: {}", batch.size());

    final TectonApiRequest.Builder requestBuilder = new TectonApiRequest.Builder()
        .workspaceName(config.workspaceName).dryRun(config.dryRunEnabled);

    for (SinkRecord record : batch) {
      TectonRecord tectonRecord = tectonConverter.convert(record);
      logProcessedRecord(tectonRecord);
      String pushSourceName = resolvePushSourceName(record);
      TectonApiRequest.RecordWrapper wrappedRecord =
          new TectonApiRequest.RecordWrapper(pushSourceName, tectonRecord);

      requestBuilder.addRecord(wrappedRecord);
    }

    return requestBuilder.build();
  }

  /**
   * Processes a single SinkRecord, converts it, and adds it to the list of valid records if valid.
   *
   * @param record The SinkRecord to process.
   * @param validRecords The list of valid records to add to if valid.
   * @throws Exception If an error occurs during processing.
   */
  private void processSingleRecord(final SinkRecord record, final List<SinkRecord> validRecords)
      throws Exception {
    logSinkRecord(record);
    TectonRecord tectonRecord = tectonConverter.convert(record);
    validateAndAddRecord(record, tectonRecord, validRecords);
  }

  /**
   * Validates and processes a TectonRecord.
   *
   * @param record The original SinkRecord.
   * @param tectonRecord The converted TectonRecord.
   * @param validRecords The list of valid records to add to if valid.
   */
  private void validateAndAddRecord(final SinkRecord record, final TectonRecord tectonRecord,
      final List<SinkRecord> validRecords) {
    if (!tectonRecord.isValid()) {
      LOG.warn("Invalid or empty record skipped from topic: {}", record.topic());
      reportErrantRecord(record, new DataException("Invalid TectonRecord."));
    } else {
      validRecords.add(record);
    }
  }

  /**
   * Logs the processed record data if loggingEventDataEnabled is enabled. Intended for debugging
   * purposes and could expose sensitive information to the logs.
   *
   * @param tectonRecord The TectonRecord to log.
   */
  private void logProcessedRecord(final TectonRecord tectonRecord) {
    if (config.loggingEventDataEnabled) {
      LOG.debug("Processed Tecton Record: {}", tectonRecord);
    }
  }

  /**
   * Derives the PushSourceName from the source topic if one is not already configured. Note the
   * PushSource(s) must be pre-configured in Tecton.
   *
   * @param record The SinkRecord to derive the push source name from.
   * @return The push source name.
   */
  private String resolvePushSourceName(final SinkRecord record) {
    return Optional.ofNullable(config.pushSourceName).filter(name -> !name.trim().isEmpty())
        .orElseGet(() -> {
          LOG.debug("pushSourceName is not set in config, using the topic name: {}",
              record.topic());
          return record.topic();
        });
  }

  /**
   * Processes the batch requests synchronously or asynchronously depending on configuration.
   *
   * @param batchRequests List of TectonApiRequests to process.
   * @throws ConnectException if a non-retriable error occurs.
   * @throws RetriableException if a retriable error occurs.
   */
  private void processBatchRequests(final List<TectonApiRequest> batchRequests)
      throws ConnectException, RetriableException {
    if (config.httpAsyncEnabled) {
      LOG.debug("Sending {} batches", batchRequests.size());
      List<CompletableFuture<TectonApiResponse>> futures = httpClient.sendAsyncBatch(batchRequests);
      futures.forEach(this::handleFuture);
    } else {
      for (TectonApiRequest request : batchRequests) {
        httpClient.sendSync(request);
      }
    }
  }

  /**
   * Handles asynchronous response exceptions.
   *
   * @param future The future representing the asynchronous operation.
   */
  private void handleFuture(final CompletableFuture<TectonApiResponse> future) {
    future.exceptionally(ex -> {
      LOG.error("Error sending batch to Tecton: {}", ex.toString());
      LOG.debug("Error details: ", ex);

      if (ex instanceof ConnectException) {
        throw (ConnectException) ex;
      } else if (ex instanceof RetriableException) {
        throw (RetriableException) ex;
      } else {
        throw new ConnectException("Unexpected error processing batch request", ex);
      }
    });
  }

  /**
   * Reports failed records to the Kafka Connect ErrantRecordReporter if configured.
   *
   * @param record The record that failed processing.
   * @param e The exception that occurred.
   */
  private void reportErrantRecord(final SinkRecord record, final Exception e) {
    if (reporter.isPresent()) {
      try {
        reporter.get().report(record, e);
      } catch (Exception reportEx) {
        LOG.error("Failed to report errant record: {}", reportEx.toString());
        throw new ConnectException("Error reporting errant record", e);
      }
    } else {
      throw new ConnectException("Error processing record", e);
    }
  }

  /**
   * Logs detailed Sink Record information. Beware, logs record value which may contain sensitive
   * information.
   *
   * @param record The record to log.
   */
  private void logSinkRecord(final SinkRecord record) {
    if (config.loggingEventDataEnabled) {
      LOG.debug(
          "Detailed record info: Topic: {}, Partition: {}, Offset: {}, Key: {}, Value: {}, Timestamp: {}, Headers: {}",
          record.topic(), record.kafkaPartition(), record.kafkaOffset(), record.key(),
          record.value(), record.timestamp() != null ? new Date(record.timestamp()) : "null",
          record.headers());
    }
  }
}
