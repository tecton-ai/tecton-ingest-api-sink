package com.tecton.kafka.connect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TectonHttpSinkConnectorTest {

    private TectonHttpSinkConnector connector;
    private Map<String, String> settings;

    @BeforeEach
    public void setup() {
        connector = new TectonHttpSinkConnector();
        settings = new HashMap<>();
        
        // Mock settings - You can add more to this list to cover all settings if desired.
        settings.put(TectonHttpSinkConnectorConfig.HTTP_CLUSTER_ENDPOINT_CONFIG, "https://test.tecton.ai");
        settings.put(TectonHttpSinkConnectorConfig.HTTP_AUTH_TOKEN_CONFIG, "sample-auth-token");
        settings.put(TectonHttpSinkConnectorConfig.WORKSPACE_NAME_CONFIG, "test-workspace");
        settings.put(TectonHttpSinkConnectorConfig.PUSH_SOURCE_NAME_CONFIG, "test-push-source");
    }

    @Test
    public void testStart() {
        connector.start(settings);

        TectonHttpSinkConnectorConfig config = new TectonHttpSinkConnectorConfig(settings);
        assertNotNull(config);
    }

    @Test
    public void testConfig() {
        assertNotNull(connector.config());
        assertEquals(TectonHttpSinkConnectorConfig.config(), connector.config());
    }

    @Test
    public void testTaskClass() {
        assertEquals(TectonHttpSinkTask.class, connector.taskClass());
    }

    @Test
    public void testVersion() {
        assertNotNull(connector.version());
    }
}
