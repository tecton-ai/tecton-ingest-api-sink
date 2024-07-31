package com.tecton.kafka.connect;

import java.util.Collection;
import java.util.Map;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.ErrantRecordReporter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.jcustenborder.kafka.connect.utils.VersionUtil;
import com.tecton.ingestclient.client.TectonHttpClient;
import com.tecton.ingestclient.processor.BatchRecordProcessor;
import com.tecton.ingestclient.processor.IRecordProcessor;

/**
 * Implementation of a SinkTask that forwards records to Tecton via HTTP.
 */
public class TectonHttpSinkTask extends SinkTask {

  private static final Logger LOG = LoggerFactory.getLogger(TectonHttpSinkTask.class);

  /** Configuration for the Tecton HTTP Sink Connector. */
  private TectonHttpSinkConnectorConfig config;

  /** Processor for Kafka records. */
  private IRecordProcessor recordProcessor;

  @Override
  public void start(final Map<String, String> settings) {
    LOG.info("Starting TectonHttpSinkTask");

    config = new TectonHttpSinkConnectorConfig(settings);
    TectonHttpClient httpClient = TectonHttpClient.create(config);
    recordProcessor =
        new BatchRecordProcessor(httpClient, initialiseErrantRecordReporter(), config);

    LOG.info("TectonHttpSinkTask initialised successfully");
  }

  @Override
  public void put(final Collection<SinkRecord> records) {
    if (records.isEmpty()) {
      LOG.debug("No records to process");
      return;
    }

    LOG.info("Processing {} records", records.size());
    try {
      recordProcessor.processRecords(records);
    } catch (ConnectException e) {
      LOG.error("Error processing records", e);
      throw e;
    }
  }

  @Override
  public void flush(final Map<TopicPartition, OffsetAndMetadata> offsets) {
    LOG.debug("Flushing data");
  }

  @Override
  public void stop() {
    LOG.info("Stopping TectonHttpSinkTask");
    recordProcessor.close();
  }

  @Override
  public String version() {
    return VersionUtil.version(this.getClass());
  }

  /**
   * Initialises the errant record reporter to report problematic records and the corresponding
   * problems.
   *
   * @return ErrantRecordReporter instance or null if not supported.
   */
  private ErrantRecordReporter initialiseErrantRecordReporter() {
    try {
      return context.errantRecordReporter();
    } catch (NoClassDefFoundError | NoSuchMethodError e) {
      LOG.warn("Connect runtimes prior to Kafka 2.6 do not support the errant record reporter.", e);
      return null;
    }
  }
}
