package com.tecton.ingestclient.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.tecton.ingestclient.client.TectonApiResponse.IngestMetrics;
import com.tecton.ingestclient.client.TectonApiResponse.IngestMetrics.FeatureViewMetric;

class TectonApiResponseTest {

    private TectonApiResponse response;
    private IngestMetrics ingestMetrics;
    private FeatureViewMetric featureViewMetric;

    @BeforeEach
    void setUp() {
        response = new TectonApiResponse();
        ingestMetrics = new IngestMetrics();
        featureViewMetric = new FeatureViewMetric();

        // Setting default values
        response.setWorkspaceName("testWorkspace");
        response.setIngestMetrics(ingestMetrics);

        featureViewMetric.setFeatureViewName("featureTest");
        featureViewMetric.setOnlineRecordIngestCount("10");
        featureViewMetric.setOfflineRecordIngestCount("20");

        List<FeatureViewMetric> metricsList = new ArrayList<>();
        metricsList.add(featureViewMetric);
        ingestMetrics.setFeatureViewIngestMetrics(metricsList);
    }

    @Test
    void testGetWorkspaceName() {
        assertEquals("testWorkspace", response.getWorkspaceName());
    }

    @Test
    void testSetWorkspaceName() {
        response.setWorkspaceName("updatedWorkspace");
        assertEquals("updatedWorkspace", response.getWorkspaceName());
    }

    @Test
    void testGetIngestMetrics() {
        assertNotNull(response.getIngestMetrics());
    }

    @Test
    void testSetIngestMetrics() {
        IngestMetrics newMetrics = new IngestMetrics();
        response.setIngestMetrics(newMetrics);
        assertEquals(newMetrics, response.getIngestMetrics());
    }

    @Test
    void testIngestMetricsGetFeatureViewIngestMetrics() {
        assertNotNull(ingestMetrics.getFeatureViewIngestMetrics());
        assertEquals(1, ingestMetrics.getFeatureViewIngestMetrics().size());
    }

    @Test
    void testFeatureViewMetricGetFeatureViewName() {
        assertEquals("featureTest", featureViewMetric.getFeatureViewName());
    }

    @Test
    void testFeatureViewMetricGetOnlineRecordIngestCount() {
        assertEquals("10", featureViewMetric.getOnlineRecordIngestCount());
    }

    @Test
    void testFeatureViewMetricGetOfflineRecordIngestCount() {
        assertEquals("20", featureViewMetric.getOfflineRecordIngestCount());
    }

    @Test
    void testToStringContainsWorkspaceName() {
        String jsonString = response.toString();
        assertNotNull(jsonString);
        assertTrue(jsonString.contains("testWorkspace"));
    }
}
