package com.tecton.connector.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.kafka.connect.sink.ErrantRecordReporter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tecton.connector.client.HttpClient;
import com.tecton.connector.converter.RecordConverter;
import com.tecton.connector.error.ConnectorException;
import com.tecton.connector.error.InvalidRecordException;
import com.tecton.connector.error.SerializationException;
import com.tecton.kafka.connect.TectonHttpSinkConnectorConfig;

/**
 * Processes records in batches and sends them to Tecton's Ingest API.
 */
public class BatchRecordProcessor implements RecordProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(BatchRecordProcessor.class);

    private final TectonHttpSinkConnectorConfig config;
    private final RecordConverter converter;
    private final HttpClient httpClient;
    private final ErrorHandler errorHandler;
    private final ErrantRecordReporter errantRecordReporter;
    private final BatchProcessingStrategy processingStrategy;

    /**
     * Constructs a BatchRecordProcessor.
     *
     * @param config               The connector configuration.
     * @param converter            The record converter.
     * @param httpClient           The HTTP client.
     * @param errorHandler         The error handler.
     * @param errantRecordReporter The errant record reporter.
     */
    public BatchRecordProcessor(TectonHttpSinkConnectorConfig config,
                                RecordConverter converter,
                                HttpClient httpClient,
                                ErrorHandler errorHandler,
                                ErrantRecordReporter errantRecordReporter) {
        this.config = config;
        this.converter = converter;
        this.httpClient = httpClient;
        this.errorHandler = errorHandler;
        this.errantRecordReporter = errantRecordReporter;
        this.processingStrategy = createProcessingStrategy();
    }

    @Override
    public void processRecords(Collection<SinkRecord> records) throws ConnectorException {
        if (records.isEmpty()) {
            LOG.debug("No records to process");
            return;
        }

        LOG.info("Processing {} records", records.size());
        List<SinkRecord> validRecords = new ArrayList<>();
        for (SinkRecord record : records) {
            try {
                converter.convert(record); // Validate conversion
                validRecords.add(record);
            } catch (InvalidRecordException | SerializationException e) {
                handleErrantRecord(record, e);
            }
        }

        if (validRecords.isEmpty()) {
            LOG.warn("No valid records to process after validation");
            return;
        }

        List<List<SinkRecord>> batches = partitionRecords(validRecords, config.batchMaxSize);
        for (List<SinkRecord> batch : batches) {
            processingStrategy.process(batch);
        }
    }

    @Override
    public void close() {
        // Close resources if necessary
    }

    private void handleErrantRecord(SinkRecord record, Exception e) {
        if (errantRecordReporter != null) {
            errantRecordReporter.report(record, e);
        } else {
            LOG.error("Error processing record from topic {}: {}", record.topic(), e.getMessage(), e);
            // Depending on error policy, you might want to throw an exception here
        }
    }

    private List<List<SinkRecord>> partitionRecords(List<SinkRecord> records, int batchSize) {
        List<List<SinkRecord>> batches = new ArrayList<>();
        for (int i = 0; i < records.size(); i += batchSize) {
            batches.add(records.subList(i, Math.min(i + batchSize, records.size())));
        }
        return batches;
    }

    private BatchProcessingStrategy createProcessingStrategy() {
        if (config.httpAsyncEnabled) {
            return new AsynchronousBatchProcessor(config, converter, httpClient, errorHandler);
        } else {
            return new SynchronousBatchProcessor(config, converter, httpClient, errorHandler);
        }
    }
}
