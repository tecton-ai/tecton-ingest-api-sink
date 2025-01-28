package com.tecton.kafka.connect;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Range;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Validator;
import org.apache.kafka.common.config.ConfigDef.Width;
import org.apache.kafka.common.config.ConfigException;

/**
 * Configuration class for TectonHttpSinkConnector. 
 * Holds the parameters required to sink data to the Tecton Ingest API.
 */
public class TectonHttpSinkConnectorConfig extends AbstractConfig {

    // Configuration Group Names
    private static final String CONNECTOR_GROUP = "Connector";
    private static final String HTTP_GROUP = "HTTP";
    private static final String KAFKA_GROUP = "Kafka";
    private static final String LOGGING_GROUP = "Logging";

    // Connector configurations
    public static final String WORKSPACE_NAME_CONFIG = "tecton.workspace.name";
    private static final String WORKSPACE_NAME_DOC = "The Tecton workspace name where the data will be ingested.";
    private static final String WORKSPACE_NAME_DISPLAY = "Tecton Workspace Name";

    public static final String PUSH_SOURCE_NAME_CONFIG = "tecton.push.source.name";
    private static final String PUSH_SOURCE_NAME_DOC = "The Tecton Push Source name to write the records to.";
    private static final String PUSH_SOURCE_NAME_DISPLAY = "Tecton Push Source Name";

    public static final String DRY_RUN_ENABLED_CONFIG = "tecton.dry.run.enabled";
    private static final String DRY_RUN_ENABLED_DOC = "Whether to enable dry run mode. If true, data will not be ingested.";
    private static final String DRY_RUN_ENABLED_DISPLAY = "Dry Run Enabled";
    private static final boolean DRY_RUN_ENABLED_DEFAULT = false;

    public static final String BATCH_MAX_SIZE_CONFIG = "tecton.batch.max.size";
    private static final String BATCH_MAX_SIZE_DOC = "The maximum number of records to include in a single batch when sending to Tecton.";
    private static final String BATCH_MAX_SIZE_DISPLAY = "Batch Max Size";
    private static final int BATCH_MAX_SIZE_DEFAULT = 500;

    // HTTP configurations
    public static final String HTTP_CLUSTER_ENDPOINT_CONFIG = "tecton.http.cluster.endpoint";
    private static final String HTTP_CLUSTER_ENDPOINT_DOC = "The Tecton HTTP cluster endpoint URL (e.g., https://<your_cluster>.tecton.ai).";
    private static final String HTTP_CLUSTER_ENDPOINT_DISPLAY = "Tecton Cluster Endpoint";

    public static final String HTTP_AUTH_TOKEN_CONFIG = "tecton.http.auth.token";
    private static final String HTTP_AUTH_TOKEN_DOC = "The Tecton authentication token.";
    private static final String HTTP_AUTH_TOKEN_DISPLAY = "Tecton Auth Token";

    public static final String HTTP_CONNECT_TIMEOUT_CONFIG = "tecton.http.connect.timeout.ms";
    private static final String HTTP_CONNECT_TIMEOUT_DOC = "HTTP connection timeout in milliseconds.";
    private static final String HTTP_CONNECT_TIMEOUT_DISPLAY = "HTTP Connect Timeout (ms)";
    private static final int HTTP_CONNECT_TIMEOUT_DEFAULT = (int) TimeUnit.SECONDS.toMillis(10);

    public static final String HTTP_READ_TIMEOUT_CONFIG = "tecton.http.read.timeout.ms";
    private static final String HTTP_READ_TIMEOUT_DOC = "HTTP read timeout in milliseconds.";
    private static final String HTTP_READ_TIMEOUT_DISPLAY = "HTTP Read Timeout (ms)";
    private static final int HTTP_READ_TIMEOUT_DEFAULT = (int) TimeUnit.SECONDS.toMillis(30);

    public static final String HTTP_WRITE_TIMEOUT_CONFIG = "tecton.http.write.timeout.ms";
    private static final String HTTP_WRITE_TIMEOUT_DOC = "HTTP write timeout in milliseconds.";
    private static final String HTTP_WRITE_TIMEOUT_DISPLAY = "HTTP Write Timeout (ms)";
    private static final int HTTP_WRITE_TIMEOUT_DEFAULT = (int) TimeUnit.SECONDS.toMillis(30);

    public static final String HTTP_CALL_TIMEOUT_CONFIG = "tecton.http.call.timeout.ms";
    private static final String HTTP_CALL_TIMEOUT_DOC = "HTTP call timeout in milliseconds.";
    private static final String HTTP_CALL_TIMEOUT_DISPLAY = "HTTP Call Timeout (ms)";
    private static final int HTTP_CALL_TIMEOUT_DEFAULT = (int) TimeUnit.SECONDS.toMillis(60);

    public static final String HTTP_ASYNC_ENABLED_CONFIG = "tecton.http.async.enabled";
    private static final String HTTP_ASYNC_ENABLED_DOC = "Whether to enable asynchronous HTTP requests. Event order cannot be guaranteed when enabled.";
    private static final String HTTP_ASYNC_ENABLED_DISPLAY = "HTTP Async Enabled";
    private static final boolean HTTP_ASYNC_ENABLED_DEFAULT = false;

    public static final String HTTP_MAX_RETRIES_CONFIG = "tecton.http.max.retries";
    private static final String HTTP_MAX_RETRIES_DOC = "Maximum number of retries for HTTP requests.";
    private static final String HTTP_MAX_RETRIES_DISPLAY = "HTTP Max Retries";
    private static final int HTTP_MAX_RETRIES_DEFAULT = 3;

    public static final String HTTP_RETRY_BACKOFF_CONFIG = "tecton.http.retry.backoff.ms";
    private static final String HTTP_RETRY_BACKOFF_DOC = "Backoff time in milliseconds between HTTP retries.";
    private static final String HTTP_RETRY_BACKOFF_DISPLAY = "HTTP Retry Backoff (ms)";
    private static final long HTTP_RETRY_BACKOFF_DEFAULT = 1000L;

    public static final String CONNECTION_POOL_SIZE_CONFIG = "tecton.connection.pool.size";
    private static final String CONNECTION_POOL_SIZE_DOC = "The maximum number of idle connections to keep in the connection pool.";
    private static final String CONNECTION_POOL_SIZE_DISPLAY = "Connection Pool Size";
    private static final int CONNECTION_POOL_SIZE_DEFAULT = 5;

    public static final String KEEP_ALIVE_DURATION_CONFIG = "tecton.keep.alive.duration.ms";
    private static final String KEEP_ALIVE_DURATION_DOC = "The duration in milliseconds to keep connections alive in the connection pool.";
    private static final String KEEP_ALIVE_DURATION_DISPLAY = "Keep Alive Duration (ms)";
    private static final long KEEP_ALIVE_DURATION_DEFAULT = TimeUnit.MINUTES.toMillis(5);

    // Kafka configurations
    public static final String KAFKA_TIMESTAMP_ENABLED_CONFIG = "kafka.timestamp.enabled";
    private static final String KAFKA_TIMESTAMP_ENABLED_DOC = "Whether to include the Kafka record timestamp in the Tecton record.";
    private static final String KAFKA_TIMESTAMP_ENABLED_DISPLAY = "Include Kafka Timestamp";
    private static final boolean KAFKA_TIMESTAMP_ENABLED_DEFAULT = false;

    public static final String KAFKA_KEY_ENABLED_CONFIG = "kafka.key.enabled";
    private static final String KAFKA_KEY_ENABLED_DOC = "Whether to include the Kafka record key in the Tecton record.";
    private static final String KAFKA_KEY_ENABLED_DISPLAY = "Include Kafka Key";
    private static final boolean KAFKA_KEY_ENABLED_DEFAULT = false;

    public static final String KAFKA_HEADERS_ENABLED_CONFIG = "kafka.headers.enabled";
    private static final String KAFKA_HEADERS_ENABLED_DOC = "Whether to include the Kafka record headers in the Tecton record.";
    private static final String KAFKA_HEADERS_ENABLED_DISPLAY = "Include Kafka Headers";
    private static final boolean KAFKA_HEADERS_ENABLED_DEFAULT = false;

    public static final String KAFKA_SANITISE_KEYS_ENABLED_CONFIG = "kafka.sanitise.keys.enabled";
    private static final String KAFKA_SANITISE_KEYS_ENABLED_DOC = "Whether to sanitise JSON keys in the Tecton record.";
    private static final String KAFKA_SANITISE_KEYS_ENABLED_DISPLAY = "Sanitise JSON Keys";
    private static final boolean KAFKA_SANITISE_KEYS_ENABLED_DEFAULT = false;

    // Logging configurations
    public static final String LOGGING_EVENT_DATA_ENABLED_CONFIG = "tecton.logging.event.data.enabled";
    private static final String LOGGING_EVENT_DATA_ENABLED_DOC = "Whether to log event data for debugging purposes. Enabling this may log sensitive data.";
    private static final String LOGGING_EVENT_DATA_ENABLED_DISPLAY = "Log Event Data";
    private static final boolean LOGGING_EVENT_DATA_ENABLED_DEFAULT = false;

    // We build the ConfigDef once and reuse it
    private static final ConfigDef CONFIG_DEF = baseConfigDef();

    // Configuration parameters (fields)
    public final String workspaceName;
    public final String pushSourceName;
    public final boolean dryRunEnabled;
    public final int batchMaxSize;

    public final String httpClusterEndpoint;
    public final String httpAuthToken;
    public final int httpConnectTimeout;
    public final int httpReadTimeout;
    public final int httpWriteTimeout;
    public final int httpCallTimeout;
    public final boolean httpAsyncEnabled;
    public final int httpMaxRetries;
    public final long httpRetryBackoff;
    public final int connectionPoolSize;
    public final long keepAliveDuration;

    public final boolean kafkaTimestampEnabled;
    public final boolean kafkaKeyEnabled;
    public final boolean kafkaHeadersEnabled;
    public final boolean kafkaSanitiseKeysEnabled;

    public final boolean loggingEventDataEnabled;

    /**
     * Constructor to initialize configuration parameters from the given originals.
     */
    public TectonHttpSinkConnectorConfig(Map<String, String> originals) {
        super(CONFIG_DEF, originals);

        // Connector configurations
        this.workspaceName = getString(WORKSPACE_NAME_CONFIG);
        this.pushSourceName = getString(PUSH_SOURCE_NAME_CONFIG);
        this.dryRunEnabled = getBoolean(DRY_RUN_ENABLED_CONFIG);
        this.batchMaxSize = getInt(BATCH_MAX_SIZE_CONFIG);

        // HTTP configurations
        this.httpClusterEndpoint = getString(HTTP_CLUSTER_ENDPOINT_CONFIG);
        this.httpAuthToken = getPassword(HTTP_AUTH_TOKEN_CONFIG).value();
        this.httpConnectTimeout = getInt(HTTP_CONNECT_TIMEOUT_CONFIG);
        this.httpReadTimeout = getInt(HTTP_READ_TIMEOUT_CONFIG);
        this.httpWriteTimeout = getInt(HTTP_WRITE_TIMEOUT_CONFIG);
        this.httpCallTimeout = getInt(HTTP_CALL_TIMEOUT_CONFIG);
        this.httpAsyncEnabled = getBoolean(HTTP_ASYNC_ENABLED_CONFIG);
        this.httpMaxRetries = getInt(HTTP_MAX_RETRIES_CONFIG);
        this.httpRetryBackoff = getLong(HTTP_RETRY_BACKOFF_CONFIG);
        this.connectionPoolSize = getInt(CONNECTION_POOL_SIZE_CONFIG);
        this.keepAliveDuration = getLong(KEEP_ALIVE_DURATION_CONFIG);

        // Kafka configurations
        this.kafkaTimestampEnabled = getBoolean(KAFKA_TIMESTAMP_ENABLED_CONFIG);
        this.kafkaKeyEnabled = getBoolean(KAFKA_KEY_ENABLED_CONFIG);
        this.kafkaHeadersEnabled = getBoolean(KAFKA_HEADERS_ENABLED_CONFIG);
        this.kafkaSanitiseKeysEnabled = getBoolean(KAFKA_SANITISE_KEYS_ENABLED_CONFIG);

        // Logging configurations
        this.loggingEventDataEnabled = getBoolean(LOGGING_EVENT_DATA_ENABLED_CONFIG);
    }

    /**
     * Expose our static ConfigDef for others (e.g., the Connector) to use.
     */
    public static ConfigDef configDef() {
        return CONFIG_DEF;
    }

    /**
     * Builds the ConfigDef with all connector parameters, documentation, etc.
     */
    private static ConfigDef baseConfigDef() {
        ConfigDef configDef = new ConfigDef();

        int orderInGroup = 0;

        // Connector configurations
        configDef.define(
                WORKSPACE_NAME_CONFIG,
                Type.STRING,
                Importance.HIGH,
                WORKSPACE_NAME_DOC,
                CONNECTOR_GROUP,
                ++orderInGroup,
                Width.MEDIUM,
                WORKSPACE_NAME_DISPLAY
        ).define(
                PUSH_SOURCE_NAME_CONFIG,
                Type.STRING,
                ConfigDef.NO_DEFAULT_VALUE,
                Importance.HIGH,
                PUSH_SOURCE_NAME_DOC,
                CONNECTOR_GROUP,
                ++orderInGroup,
                Width.MEDIUM,
                PUSH_SOURCE_NAME_DISPLAY
        ).define(
                DRY_RUN_ENABLED_CONFIG,
                Type.BOOLEAN,
                DRY_RUN_ENABLED_DEFAULT,
                Importance.LOW,
                DRY_RUN_ENABLED_DOC,
                CONNECTOR_GROUP,
                ++orderInGroup,
                Width.SHORT,
                DRY_RUN_ENABLED_DISPLAY
        ).define(
                BATCH_MAX_SIZE_CONFIG,
                Type.INT,
                BATCH_MAX_SIZE_DEFAULT,
                Range.between(1, 10000),
                Importance.MEDIUM,
                BATCH_MAX_SIZE_DOC,
                CONNECTOR_GROUP,
                ++orderInGroup,
                Width.SHORT,
                BATCH_MAX_SIZE_DISPLAY
        );

        // HTTP configurations
        orderInGroup = 0;
        configDef.define(
                HTTP_CLUSTER_ENDPOINT_CONFIG,
                Type.STRING,
                ConfigDef.NO_DEFAULT_VALUE,
                new UrlValidator(),
                Importance.HIGH,
                HTTP_CLUSTER_ENDPOINT_DOC,
                HTTP_GROUP,
                ++orderInGroup,
                Width.LONG,
                HTTP_CLUSTER_ENDPOINT_DISPLAY
        ).define(
                HTTP_AUTH_TOKEN_CONFIG,
                Type.PASSWORD,
                ConfigDef.NO_DEFAULT_VALUE,
                Importance.HIGH,
                HTTP_AUTH_TOKEN_DOC,
                HTTP_GROUP,
                ++orderInGroup,
                Width.LONG,
                HTTP_AUTH_TOKEN_DISPLAY
        ).define(
                HTTP_CONNECT_TIMEOUT_CONFIG,
                Type.INT,
                HTTP_CONNECT_TIMEOUT_DEFAULT,
                Range.atLeast(0),
                Importance.MEDIUM,
                HTTP_CONNECT_TIMEOUT_DOC,
                HTTP_GROUP,
                ++orderInGroup,
                Width.SHORT,
                HTTP_CONNECT_TIMEOUT_DISPLAY
        ).define(
                HTTP_READ_TIMEOUT_CONFIG,
                Type.INT,
                HTTP_READ_TIMEOUT_DEFAULT,
                Range.atLeast(0),
                Importance.MEDIUM,
                HTTP_READ_TIMEOUT_DOC,
                HTTP_GROUP,
                ++orderInGroup,
                Width.SHORT,
                HTTP_READ_TIMEOUT_DISPLAY
        ).define(
                HTTP_WRITE_TIMEOUT_CONFIG,
                Type.INT,
                HTTP_WRITE_TIMEOUT_DEFAULT,
                Range.atLeast(0),
                Importance.MEDIUM,
                HTTP_WRITE_TIMEOUT_DOC,
                HTTP_GROUP,
                ++orderInGroup,
                Width.SHORT,
                HTTP_WRITE_TIMEOUT_DISPLAY
        ).define(
                HTTP_CALL_TIMEOUT_CONFIG,
                Type.INT,
                HTTP_CALL_TIMEOUT_DEFAULT,
                Range.atLeast(0),
                Importance.MEDIUM,
                HTTP_CALL_TIMEOUT_DOC,
                HTTP_GROUP,
                ++orderInGroup,
                Width.SHORT,
                HTTP_CALL_TIMEOUT_DISPLAY
        ).define(
                HTTP_ASYNC_ENABLED_CONFIG,
                Type.BOOLEAN,
                HTTP_ASYNC_ENABLED_DEFAULT,
                Importance.LOW,
                HTTP_ASYNC_ENABLED_DOC,
                HTTP_GROUP,
                ++orderInGroup,
                Width.SHORT,
                HTTP_ASYNC_ENABLED_DISPLAY
        ).define(
                HTTP_MAX_RETRIES_CONFIG,
                Type.INT,
                HTTP_MAX_RETRIES_DEFAULT,
                Range.atLeast(0),
                Importance.LOW,
                HTTP_MAX_RETRIES_DOC,
                HTTP_GROUP,
                ++orderInGroup,
                Width.SHORT,
                HTTP_MAX_RETRIES_DISPLAY
        ).define(
                HTTP_RETRY_BACKOFF_CONFIG,
                Type.LONG,
                HTTP_RETRY_BACKOFF_DEFAULT,
                Range.atLeast(0),
                Importance.LOW,
                HTTP_RETRY_BACKOFF_DOC,
                HTTP_GROUP,
                ++orderInGroup,
                Width.SHORT,
                HTTP_RETRY_BACKOFF_DISPLAY
        ).define(
                CONNECTION_POOL_SIZE_CONFIG,
                Type.INT,
                CONNECTION_POOL_SIZE_DEFAULT,
                Range.between(1, 100),
                Importance.LOW,
                CONNECTION_POOL_SIZE_DOC,
                HTTP_GROUP,
                ++orderInGroup,
                Width.SHORT,
                CONNECTION_POOL_SIZE_DISPLAY
        ).define(
                KEEP_ALIVE_DURATION_CONFIG,
                Type.LONG,
                KEEP_ALIVE_DURATION_DEFAULT,
                Range.atLeast(0),
                Importance.LOW,
                KEEP_ALIVE_DURATION_DOC,
                HTTP_GROUP,
                ++orderInGroup,
                Width.SHORT,
                KEEP_ALIVE_DURATION_DISPLAY
        );

        // Kafka configurations
        orderInGroup = 0;
        configDef.define(
                KAFKA_TIMESTAMP_ENABLED_CONFIG,
                Type.BOOLEAN,
                KAFKA_TIMESTAMP_ENABLED_DEFAULT,
                Importance.LOW,
                KAFKA_TIMESTAMP_ENABLED_DOC,
                KAFKA_GROUP,
                ++orderInGroup,
                Width.SHORT,
                KAFKA_TIMESTAMP_ENABLED_DISPLAY
        ).define(
                KAFKA_KEY_ENABLED_CONFIG,
                Type.BOOLEAN,
                KAFKA_KEY_ENABLED_DEFAULT,
                Importance.LOW,
                KAFKA_KEY_ENABLED_DOC,
                KAFKA_GROUP,
                ++orderInGroup,
                Width.SHORT,
                KAFKA_KEY_ENABLED_DISPLAY
        ).define(
                KAFKA_HEADERS_ENABLED_CONFIG,
                Type.BOOLEAN,
                KAFKA_HEADERS_ENABLED_DEFAULT,
                Importance.LOW,
                KAFKA_HEADERS_ENABLED_DOC,
                KAFKA_GROUP,
                ++orderInGroup,
                Width.SHORT,
                KAFKA_HEADERS_ENABLED_DISPLAY
        ).define(
                KAFKA_SANITISE_KEYS_ENABLED_CONFIG,
                Type.BOOLEAN,
                KAFKA_SANITISE_KEYS_ENABLED_DEFAULT,
                Importance.LOW,
                KAFKA_SANITISE_KEYS_ENABLED_DOC,
                KAFKA_GROUP,
                ++orderInGroup,
                Width.SHORT,
                KAFKA_SANITISE_KEYS_ENABLED_DISPLAY
        );

        // Logging configurations
        orderInGroup = 0;
        configDef.define(
                LOGGING_EVENT_DATA_ENABLED_CONFIG,
                Type.BOOLEAN,
                LOGGING_EVENT_DATA_ENABLED_DEFAULT,
                Importance.LOW,
                LOGGING_EVENT_DATA_ENABLED_DOC,
                LOGGING_GROUP,
                ++orderInGroup,
                Width.SHORT,
                LOGGING_EVENT_DATA_ENABLED_DISPLAY
        );

        return configDef;
    }

    /**
     * Custom validator for a URL field.
     */
    private static class UrlValidator implements Validator {
        @Override
        public void ensureValid(String name, Object value) {
            String url = (String) value;
            try {
                new java.net.URI(url);
            } catch (Exception e) {
                throw new ConfigException(name, value, "Invalid URL.");
            }
        }

        @Override
        public String toString() {
            return "A valid URL";
        }
    }
}
