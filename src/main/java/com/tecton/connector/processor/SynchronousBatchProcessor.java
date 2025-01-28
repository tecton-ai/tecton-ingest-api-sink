package com.tecton.connector.processor;

import java.util.List;
import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tecton.connector.client.HttpClient;
import com.tecton.connector.converter.RecordConverter;
import com.tecton.connector.error.ConnectorException;
import com.tecton.connector.model.TectonApiRequest;
import com.tecton.connector.model.TectonRecord;
import com.tecton.kafka.connect.TectonHttpSinkConnectorConfig;

/**
 * Processes batches synchronously.
 */
public class SynchronousBatchProcessor implements BatchProcessingStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(SynchronousBatchProcessor.class);

    private final TectonHttpSinkConnectorConfig config;
    private final RecordConverter converter;
    private final HttpClient httpClient;
    private final ErrorHandler errorHandler;

    public SynchronousBatchProcessor(TectonHttpSinkConnectorConfig config,
                                     RecordConverter converter,
                                     HttpClient httpClient,
                                     ErrorHandler errorHandler) {
        this.config = config;
        this.converter = converter;
        this.httpClient = httpClient;
        this.errorHandler = errorHandler;
    }

    @Override
    public void process(List<SinkRecord> records) {
        try {
            TectonApiRequest request = buildRequest(records);
            httpClient.sendSync(request);
            LOG.info("Successfully sent batch of {} records", records.size());
        } catch (ConnectorException e) {
            errorHandler.handle(e);
        }
    }

    private TectonApiRequest buildRequest(List<SinkRecord> records) throws ConnectorException {
        TectonApiRequest.Builder builder = new TectonApiRequest.Builder()
                .workspaceName(config.workspaceName)
                .dryRun(config.dryRunEnabled);

        for (SinkRecord record : records) {
            TectonRecord tectonRecord = converter.convert(record);
            String pushSource = config.pushSourceName != null ? config.pushSourceName : record.topic();
            builder.addRecord(pushSource, tectonRecord);
        }

        return builder.build();
    }
}
