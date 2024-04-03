package com.tecton.kafka.connect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.jcustenborder.kafka.connect.utils.VersionUtil;
import com.github.jcustenborder.kafka.connect.utils.config.Description;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationImportant;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationNote;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationTip;
import com.github.jcustenborder.kafka.connect.utils.config.Title;

/**
 * An HTTP Sink Connector for Kafka, which sends records to Tecton's Ingest API.
 *
 */
@Title("Tecton HTTP Sink Connector")
@Description("This connector is responsible for sending Kafka events to Tecton via the Ingest API.")
@DocumentationImportant("Ensure Tecton Push Sources have been applied, and Ingest API is reachable.")
@DocumentationTip("Monitor the connector's log for troubleshooting purposes.")
@DocumentationNote("Refer to the official Tecton documentation for more details.")
public class TectonHttpSinkConnector extends SinkConnector {

  private static final Logger log = LoggerFactory.getLogger(TectonHttpSinkConnector.class);

  private Map<String, String> settings;

  /**
   * Returns a set of configurations for the tasks based on the current configuration.
   *
   * @param maxTasks the maximum number of configurations to generate
   * @return a list of configuration maps, one map for each task
   */
  @Override
  public List<Map<String, String>> taskConfigs(int maxTasks) {
    log.info("Configuring {} tasks for the connector", maxTasks);

    List<Map<String, String>> configs = new ArrayList<>();
    for (int i = 0; i < maxTasks; i++) {
      configs.add(this.settings);
    }
    return configs;
  }

  /**
   * Starts the connector by initializing the necessary resources.
   *
   * @param settings the configuration settings
   */
  @Override
  public void start(Map<String, String> settings) {
    log.info("Starting the TectonHttpSinkConnector");

    this.settings = settings;
    new TectonHttpSinkConnectorConfig(settings);

    // Further setup for the connector if needed
  }

  /**
   * Stops the connector and releases the allocated resources.
   */
  @Override
  public void stop() {
    log.info("Stopping the TectonHttpSinkConnector");
    // Clean-up activities when stopping the connector
  }

  /**
   * Defines the configuration for the connector.
   *
   * @return the configuration definition for the connector
   */
  @Override
  public ConfigDef config() {
    return TectonHttpSinkConnectorConfig.config();
  }

  /**
   * Returns the Task implementation for the connector.
   *
   * @return the class of the Task implementation
   */
  @Override
  public Class<? extends Task> taskClass() {
    return TectonHttpSinkTask.class;
  }

  /**
   * Returns the version of the connector.
   *
   * @return the version string
   */
  @Override
  public String version() {
    // It's important to handle versioning carefully for future compatibility
    return VersionUtil.version(this.getClass());
  }
}
