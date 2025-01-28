package com.tecton.kafka.connect;

import org.apache.kafka.common.config.Config;
import org.apache.kafka.common.config.ConfigValue;
import org.apache.kafka.common.config.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Custom validator that extends the default validation logic with additional checks
 * and returns a Config object with any errors appended.
 */
public class Validator {

    private static final Logger LOG = LoggerFactory.getLogger(Validator.class);

    private final TectonHttpSinkConnectorConfig config;
    private final Map<String, String> props;
    private final List<ConfigValue> configValues;

    public Validator(Map<String, String> props) {
        this.props = props;
        // Construct config to catch any parse/validation errors early
        this.config = new TectonHttpSinkConnectorConfig(props);
        // Validate using the built-in checks in the ConfigDef
        this.configValues = TectonHttpSinkConnectorConfig.configDef().validate(props);
    }

    /**
     * Perform custom validations and return a Config object that includes errors (if any).
     */
    public Config validate() {
        validateRequiredConfigurations();
        validateEndpointAndAuthToken();
        // Add more custom validations if desired

        return new Config(configValues);
    }

    /**
     * Example: Checking for certain required fields that cannot be empty
     */
    private void validateRequiredConfigurations() {
        if (isNullOrEmpty(config.workspaceName)) {
            addErrorMessage(
                TectonHttpSinkConnectorConfig.WORKSPACE_NAME_CONFIG,
                "Workspace name must be specified and cannot be empty."
            );
        }
        if (isNullOrEmpty(config.pushSourceName)) {
            addErrorMessage(
                TectonHttpSinkConnectorConfig.PUSH_SOURCE_NAME_CONFIG,
                "Push source name must be specified and cannot be empty."
            );
        }
        if (isNullOrEmpty(config.httpClusterEndpoint)) {
            addErrorMessage(
                TectonHttpSinkConnectorConfig.HTTP_CLUSTER_ENDPOINT_CONFIG,
                "Tecton cluster endpoint must be specified and cannot be empty."
            );
        }
        if (isNullOrEmpty(config.httpAuthToken)) {
            addErrorMessage(
                TectonHttpSinkConnectorConfig.HTTP_AUTH_TOKEN_CONFIG,
                "Tecton auth token must be specified and cannot be empty."
            );
        }
    }

    /**
     * Validate the endpoint and auth token if you want to do a quick check without making a live network call.
     * For example, you might check if the endpoint starts with "http" or "https".
     */
    private void validateEndpointAndAuthToken() {
        if (!isNullOrEmpty(config.httpClusterEndpoint)
                && !(config.httpClusterEndpoint.startsWith("http://")
                     || config.httpClusterEndpoint.startsWith("https://"))) {
            addErrorMessage(
                TectonHttpSinkConnectorConfig.HTTP_CLUSTER_ENDPOINT_CONFIG,
                "Tecton cluster endpoint should start with http:// or https://"
            );
        }
    }

    /**
     * Utility method for marking a config property as having an error.
     */
    private void addErrorMessage(String property, String message) {
        for (ConfigValue configValue : configValues) {
            if (configValue.name().equals(property)) {
                configValue.addErrorMessage(message);
                break;
            }
        }
    }

    private boolean isNullOrEmpty(String s) {
        return (s == null || s.trim().isEmpty());
    }
}
