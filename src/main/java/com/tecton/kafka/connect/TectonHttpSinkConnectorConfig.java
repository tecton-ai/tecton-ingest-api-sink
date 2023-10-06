package com.tecton.kafka.connect;

import static org.apache.kafka.common.config.ConfigDef.Importance.*;
import static org.apache.kafka.common.config.ConfigDef.Type.*;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import com.github.jcustenborder.kafka.connect.utils.config.ConfigKeyBuilder;

import java.time.Duration;
import java.util.Map;

/**
 * Configuration class for TectonHttpSinkConnector. Holds the configuration parameters required to
 * sink data to the Tecton Ingest API.
 */
public class TectonHttpSinkConnectorConfig extends AbstractConfig {

  // Default values
  private static final int DEFAULT_TIMEOUT_SECONDS = 30;
  private static final int DEFAULT_BATCH_MAX_SIZE = 10;

  // HTTP-related configurations
  private static final String HTTP_CLUSTER_ENDPOINT_CONFIG = "tecton.http.cluster.endpoint";
  private static final String HTTP_AUTH_TOKEN_CONFIG = "tecton.http.auth.token";
  private static final String HTTP_CONNECT_TIMEOUT_CONFIG = "tecton.http.connect.timeout";
  private static final String HTTP_REQUEST_TIMEOUT_CONFIG = "tecton.http.request.timeout";
  private static final String HTTP_ASYNC_ENABLED_CONFIG = "tecton.http.async.enabled";
  private static final String HTTP_CONCURRENCY_LIMIT_CONFIG = "tecton.http.concurrency.limit";

  // Tecton payload related configurations
  private static final String WORKSPACE_NAME_CONFIG = "tecton.workspace.name";
  private static final String PUSH_SOURCE_NAME_CONFIG = "tecton.push.source.name";
  private static final String DRY_RUN_ENABLED_CONFIG = "tecton.dry.run.enabled";
  private static final String BATCH_MAX_SIZE_CONFIG = "tecton.batch.max.size";

  // Kafka-related configurations
  private static final String KAFKA_TIMESTAMP_ENABLED_CONFIG = "tecton.kafka.timestamp.enabled";
  private static final String KAFKA_KEY_ENABLED_CONFIG = "tecton.kafka.key.enabled";
  private static final String KAFKA_HEADERS_ENABLED_CONFIG = "tecton.kafka.headers.enabled";

  // Logging configuration
  private static final String LOGGING_EVENT_DATA_ENABLED_CONFIG =
      "tecton.logging.event.data.enabled";

//HTTP-related Documentation
 static final String HTTP_CLUSTER_ENDPOINT_DOC = 
     "The endpoint of your Tecton cluster, formatted as: https://<your_cluster>.tecton.ai.";
 static final String HTTP_AUTH_TOKEN_DOC = 
     "The authorisation token used to authenticate requests to the Stream Ingest API.";
 static final String HTTP_CONNECT_TIMEOUT_DOC = 
     "The HTTP connect timeout for the Tecton API, in seconds.";
 static final String HTTP_REQUEST_TIMEOUT_DOC = 
     "The timeout for the HTTP request to Tecton's Ingest API, in seconds.";
 static final String HTTP_ASYNC_ENABLED_DOC = 
     "Enables HTTP asynchronous sending for concurrent invocations. The default is true.";
 static final String HTTP_CONCURRENCY_LIMIT_DOC = 
     "Limits the number of concurrent HTTP requests when async is enabled. The default is 50.";

 // Tecton Payload-related Documentation
 static final String WORKSPACE_NAME_DOC = 
     "The name of the workspace in Tecton.";
 static final String PUSH_SOURCE_NAME_DOC = 
     "The name of the Tecton Push Source.";
 static final String DRY_RUN_ENABLED_DOC = 
     "If set to true, the connector will not actually send data.";
 static final String BATCH_MAX_SIZE_DOC = 
     "The maximum size of the batch of events sent to Tecton. The default is 10.";

 // Kafka-related Documentation
 static final String KAFKA_TIMESTAMP_ENABLED_DOC = 
     "Indicates whether to include the Kafka timestamp in the Tecton record.";
 static final String KAFKA_KEY_ENABLED_DOC = 
     "Indicates whether to include the Kafka key in the Tecton record.";
 static final String KAFKA_HEADERS_ENABLED_DOC = 
     "Indicates whether to include the Kafka headers in the Tecton record.";

 // Logging Documentation
 static final String LOGGING_EVENT_DATA_ENABLED_DOC = 
     "Determines whether the event data should be logged for debugging purposes. The default is false.";

  // Configuration parameters
  public final String httpAuthToken;
  public final String httpClusterEndpoint;
  public final Duration httpConnectTimeout;
  public final Duration httpRequestTimeout;
  public final boolean httpAsyncEnabled;
  public final int httpConcurrencyLimit;
  public final String workspaceName;
  public final String pushSourceName;
  public final boolean dryRunEnabled;
  public final int batchMaxSize;
  public final boolean kafkaTimestampEnabled;
  public final boolean kafkaKeyEnabled;
  public final boolean kafkaHeadersEnabled;
  public final boolean loggingEventDataEnabled;

  /**
   * Constructor to initialise configuration parameters.
   *
   * @param originals the configuration settings
   */
  public TectonHttpSinkConnectorConfig(Map<?, ?> originals) {
    super(config(), originals);

    this.httpAuthToken = this.getPassword(HTTP_AUTH_TOKEN_CONFIG).value();
    this.httpClusterEndpoint = this.getString(HTTP_CLUSTER_ENDPOINT_CONFIG);
    this.httpConnectTimeout = Duration.ofSeconds(this.getInt(HTTP_CONNECT_TIMEOUT_CONFIG));
    this.httpRequestTimeout = Duration.ofSeconds(this.getInt(HTTP_REQUEST_TIMEOUT_CONFIG));
    this.httpAsyncEnabled = this.getBoolean(HTTP_ASYNC_ENABLED_CONFIG);
    this.httpConcurrencyLimit = this.getInt(HTTP_CONCURRENCY_LIMIT_CONFIG);
    this.workspaceName = this.getString(WORKSPACE_NAME_CONFIG);
    this.pushSourceName = this.getString(PUSH_SOURCE_NAME_CONFIG);
    this.dryRunEnabled = this.getBoolean(DRY_RUN_ENABLED_CONFIG);
    this.batchMaxSize = this.getInt(BATCH_MAX_SIZE_CONFIG);
    this.kafkaTimestampEnabled = this.getBoolean(KAFKA_TIMESTAMP_ENABLED_CONFIG);
    this.kafkaKeyEnabled = this.getBoolean(KAFKA_KEY_ENABLED_CONFIG);
    this.kafkaHeadersEnabled = this.getBoolean(KAFKA_HEADERS_ENABLED_CONFIG);
    this.loggingEventDataEnabled = this.getBoolean(LOGGING_EVENT_DATA_ENABLED_CONFIG);
  }

  /**
   * Define the configuration with its keys, types, defaults, and documentation.
   *
   * @return the configuration definition
   */
  public static ConfigDef config() {
    return new ConfigDef()
        // HTTP-related configurations
        .define(ConfigKeyBuilder.of(HTTP_CLUSTER_ENDPOINT_CONFIG, STRING)
            .documentation(HTTP_CLUSTER_ENDPOINT_DOC)
            .importance(HIGH)
            .build())
        .define(ConfigKeyBuilder.of(HTTP_AUTH_TOKEN_CONFIG, PASSWORD)
            .documentation(HTTP_AUTH_TOKEN_DOC)
            .importance(HIGH)
            .build())
        .define(ConfigKeyBuilder.of(HTTP_CONNECT_TIMEOUT_CONFIG, INT)
            .documentation(HTTP_CONNECT_TIMEOUT_DOC)
            .importance(MEDIUM)
            .defaultValue(DEFAULT_TIMEOUT_SECONDS)
            .build())
        .define(ConfigKeyBuilder.of(HTTP_REQUEST_TIMEOUT_CONFIG, INT)
            .documentation(HTTP_REQUEST_TIMEOUT_DOC)
            .importance(MEDIUM)
            .defaultValue(DEFAULT_TIMEOUT_SECONDS)
            .build())
        .define(ConfigKeyBuilder.of(HTTP_ASYNC_ENABLED_CONFIG, BOOLEAN)
            .documentation(HTTP_ASYNC_ENABLED_DOC)
            .importance(MEDIUM)
            .defaultValue(true)
            .build())
        .define(ConfigKeyBuilder.of(HTTP_CONCURRENCY_LIMIT_CONFIG, INT)
            .documentation(HTTP_CONCURRENCY_LIMIT_DOC)
            .importance(MEDIUM)
            .defaultValue(50)
            .build())
        
        // Tecton payload related configurations
        .define(ConfigKeyBuilder.of(WORKSPACE_NAME_CONFIG, STRING)
            .documentation(WORKSPACE_NAME_DOC)
            .importance(HIGH)
            .build())
        .define(ConfigKeyBuilder.of(PUSH_SOURCE_NAME_CONFIG, STRING)
            .documentation(PUSH_SOURCE_NAME_DOC)
            .importance(MEDIUM)
            .build())
        .define(ConfigKeyBuilder.of(DRY_RUN_ENABLED_CONFIG, BOOLEAN)
            .documentation(DRY_RUN_ENABLED_DOC)
            .importance(MEDIUM)
            .defaultValue(false)
            .build())
        .define(ConfigKeyBuilder.of(BATCH_MAX_SIZE_CONFIG, INT)
            .documentation(BATCH_MAX_SIZE_DOC)
            .importance(MEDIUM)
            .defaultValue(DEFAULT_BATCH_MAX_SIZE)
            .build())

        // Kafka-related configuration
        .define(ConfigKeyBuilder.of(KAFKA_TIMESTAMP_ENABLED_CONFIG, BOOLEAN)
            .documentation(KAFKA_TIMESTAMP_ENABLED_DOC)
            .importance(MEDIUM)
            .defaultValue(false)
            .build())
        .define(ConfigKeyBuilder.of(KAFKA_KEY_ENABLED_CONFIG, BOOLEAN)
            .documentation(KAFKA_KEY_ENABLED_DOC)
            .importance(MEDIUM)
            .defaultValue(false)
            .build())
        .define(ConfigKeyBuilder.of(KAFKA_HEADERS_ENABLED_CONFIG, BOOLEAN)
            .documentation(KAFKA_HEADERS_ENABLED_DOC)
            .importance(MEDIUM)
            .defaultValue(false)
            .build())
        
        // Logging-related configurations
        .define(ConfigKeyBuilder.of(LOGGING_EVENT_DATA_ENABLED_CONFIG, BOOLEAN)
            .documentation(LOGGING_EVENT_DATA_ENABLED_DOC)
            .importance(MEDIUM)
            .defaultValue(false)
            .build());
  }
}
