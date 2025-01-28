package com.tecton.kafka.connect;

import com.tecton.connector.client.HttpClient;
import com.tecton.connector.client.RetryInterceptor;
import com.tecton.connector.client.TectonHttpClient;
import com.tecton.connector.client.TimingEventListener;
import com.tecton.connector.converter.JsonRecordConverter;
import com.tecton.connector.error.ConnectorException;
import com.tecton.connector.processor.BatchRecordProcessor;
import com.tecton.connector.processor.DefaultErrorHandler;
import com.tecton.connector.processor.RecordProcessor;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.RetriableException;
import org.apache.kafka.connect.sink.ErrantRecordReporter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of a SinkTask that forwards records to Tecton via HTTP.
 */
public class TectonHttpSinkTask extends SinkTask {

    private static final Logger LOG = LoggerFactory.getLogger(TectonHttpSinkTask.class);

    private TectonHttpSinkConnectorConfig config;
    private RecordProcessor recordProcessor;
    private HttpClient httpClient;

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }

    @Override
    public void start(final Map<String, String> props) {
        LOG.info("Starting TectonHttpSinkTask");

        // Load configuration
        this.config = new TectonHttpSinkConnectorConfig(props);

        // Build OkHttpClient with timeouts, pool, etc.
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(config.httpConnectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(config.httpReadTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(config.httpWriteTimeout, TimeUnit.MILLISECONDS)
                .callTimeout(config.httpCallTimeout, TimeUnit.MILLISECONDS)
                .connectionPool(new ConnectionPool(
                        config.connectionPoolSize,
                        config.keepAliveDuration,
                        TimeUnit.MILLISECONDS))
                .eventListener(new TimingEventListener())
                // IMPORTANT: pass the httpRetryBackoff as is, since it's already in ms
                .addInterceptor(new RetryInterceptor(config.httpMaxRetries, config.httpRetryBackoff))
                .build();

        // Wrap OkHttpClient in a custom TectonHttpClient
        httpClient = new TectonHttpClient.Builder()
                .client(okHttpClient)
                .clusterEndpoint(config.httpClusterEndpoint)
                .authToken(config.httpAuthToken)
                .loggingEventDataEnabled(config.loggingEventDataEnabled)
                .build();

        // Initialize the record processor
        recordProcessor = new BatchRecordProcessor(
                config,
                new JsonRecordConverter(),
                httpClient,
                new DefaultErrorHandler(),
                initialiseErrantRecordReporter()
        );

        LOG.info("TectonHttpSinkTask initialized successfully");
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
        } catch (ConnectorException e) {
            LOG.error("Connector exception occurred", e);
            throw new RetriableException(e);
        }
    }

    @Override
    public void flush(final Map<TopicPartition, OffsetAndMetadata> offsets) {
        LOG.debug("Flushing data (no-op)");
    }

    @Override
    public void stop() {
        LOG.info("Stopping TectonHttpSinkTask");
        try {
            if (recordProcessor != null) {
                recordProcessor.close();
            }
            if (httpClient != null) {
                httpClient.close();
                LOG.info("Tecton HTTP client shut down successfully");
            }
        } catch (Exception e) {
            LOG.error("Error during task shutdown", e);
        }
    }

    /**
     * Initialize an errant record reporter if the runtime supports it.
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
