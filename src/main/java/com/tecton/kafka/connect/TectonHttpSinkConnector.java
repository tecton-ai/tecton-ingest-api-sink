package com.tecton.kafka.connect;

import org.apache.kafka.common.config.Config;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SinkConnector implementation for sending data to Tecton via HTTP.
 */
public class TectonHttpSinkConnector extends SinkConnector {

    private Map<String, String> configProperties;

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {
        // Save the properties for later use in taskConfigs(...)
        this.configProperties = props;
        // No need to perform validation here; it's done in the validate(...) method
    }

    @Override
    public Class<? extends Task> taskClass() {
        return TectonHttpSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        // Create one task configuration per task
        List<Map<String, String>> taskConfigs = new ArrayList<>(maxTasks);
        for (int i = 0; i < maxTasks; i++) {
            taskConfigs.add(configProperties);
        }
        return taskConfigs;
    }

    @Override
    public void stop() {
        // No-op for the connector itself; tasks will handle their own stop
    }

    @Override
    public ConfigDef config() {
        // Return the static ConfigDef from our config class
        return TectonHttpSinkConnectorConfig.configDef();
    }

    @Override
    public Config validate(Map<String, String> connectorConfigs) {
        // Use the custom Validator to validate and return the results
        Validator validator = new Validator(connectorConfigs);
        return validator.validate();
    }
}
