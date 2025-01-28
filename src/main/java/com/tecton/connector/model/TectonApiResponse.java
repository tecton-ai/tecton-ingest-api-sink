package com.tecton.connector.model;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tecton.connector.util.JsonUtil;

/**
 * Represents a response from the Tecton API containing workspace information and ingestion metrics.
 * This class is configured to ignore unknown properties in JSON responses to ensure flexibility
 * with API changes.
 */
public class TectonApiResponse {

  private static final Logger LOG = LoggerFactory.getLogger(TectonApiResponse.class);

  @JsonProperty("workspaceName")
  private final String workspaceName;

  @JsonProperty("ingestMetrics")
  private final IngestMetrics ingestMetrics;

  /**
   * Constructor for setting fields directly.
   *
   * @param workspaceName the name of the workspace
   * @param ingestMetrics the ingestion metrics
   */
  @JsonCreator
  public TectonApiResponse(@JsonProperty("workspaceName") String workspaceName,
      @JsonProperty("ingestMetrics") IngestMetrics ingestMetrics) {
    this.workspaceName = workspaceName;
    this.ingestMetrics = ingestMetrics;
  }

  /**
   * Gets the workspace name associated with the response.
   *
   * @return the workspace name.
   */
  public String getWorkspaceName() {
    return workspaceName;
  }

  /**
   * Gets the ingestion metrics associated with the response.
   *
   * @return the ingestion metrics.
   */
  public IngestMetrics getIngestMetrics() {
    return ingestMetrics;
  }

  @Override
  public String toString() {
    try {
      return JsonUtil.toJson(this);
    } catch (JsonProcessingException e) {
      LOG.error("Error serializing TectonApiResponse to JSON. workspaceName: {}", workspaceName, e);
      return String.format(
          "{\"workspaceName\":\"%s\",\"ingestMetrics\":\"%s\",\"error\":\"Error converting to JSON\"}",
          workspaceName, ingestMetrics);
    }
  }

  /**
   * Represents the ingestion metrics details in Tecton API response.
   */
  public static class IngestMetrics {

    private static final Logger LOG = LoggerFactory.getLogger(IngestMetrics.class);

    @JsonProperty("featureViewIngestMetrics")
    private final List<FeatureViewMetric> featureViewIngestMetrics;

    @JsonProperty("dataSourceIngestMetrics")
    private final List<DataSourceMetric> dataSourceIngestMetrics;

    /**
     * Constructor for setting fields directly.
     *
     * @param featureViewIngestMetrics the list of feature view metrics
     * @param dataSourceIngestMetrics the list of data source metrics
     */
    @JsonCreator
    public IngestMetrics(
        @JsonProperty("featureViewIngestMetrics") List<FeatureViewMetric> featureViewIngestMetrics,
        @JsonProperty("dataSourceIngestMetrics") List<DataSourceMetric> dataSourceIngestMetrics) {
      this.featureViewIngestMetrics = featureViewIngestMetrics;
      this.dataSourceIngestMetrics = dataSourceIngestMetrics;
    }

    /**
     * Gets the feature view ingestion metrics.
     *
     * @return the feature view ingestion metrics.
     */
    public List<FeatureViewMetric> getFeatureViewIngestMetrics() {
      return featureViewIngestMetrics;
    }

    /**
     * Gets the data source ingestion metrics.
     *
     * @return the data source ingestion metrics.
     */
    public List<DataSourceMetric> getDataSourceIngestMetrics() {
      return dataSourceIngestMetrics;
    }

    @Override
    public String toString() {
      try {
        return JsonUtil.toJson(this);
      } catch (JsonProcessingException e) {
        LOG.error("Error serializing IngestMetrics to JSON.", e);
        return String.format(
            "{\"featureViewIngestMetrics\":\"%s\",\"dataSourceIngestMetrics\":\"%s\",\"error\":\"Error converting to JSON\"}",
            featureViewIngestMetrics, dataSourceIngestMetrics);
      }
    }

    /**
     * Represents the individual ingestion metric details for data sources in Tecton API response.
     */
    public static class DataSourceMetric {

      private static final Logger LOG = LoggerFactory.getLogger(DataSourceMetric.class);

      @JsonProperty("dataSourceName")
      private final String dataSourceName;

      @JsonProperty("offlineRecordIngestCount")
      private final String offlineRecordIngestCount;

      @JsonProperty("dataSourceId")
      private final String dataSourceId;

      /**
       * Constructor for setting fields directly.
       *
       * @param dataSourceName the name of the data source
       * @param offlineRecordIngestCount the count of offline records ingested
       * @param dataSourceId the ID of the data source
       */
      @JsonCreator
      public DataSourceMetric(@JsonProperty("dataSourceName") String dataSourceName,
          @JsonProperty("offlineRecordIngestCount") String offlineRecordIngestCount,
          @JsonProperty("dataSourceId") String dataSourceId) {
        this.dataSourceName = dataSourceName;
        this.offlineRecordIngestCount = offlineRecordIngestCount;
        this.dataSourceId = dataSourceId;
      }

      /**
       * Gets the data source name.
       *
       * @return the data source name.
       */
      public String getDataSourceName() {
        return dataSourceName;
      }

      /**
       * Gets the offline record ingest count.
       *
       * @return the offline record ingest count.
       */
      public String getOfflineRecordIngestCount() {
        return offlineRecordIngestCount;
      }

      /**
       * Gets the data source ID.
       *
       * @return the data source ID.
       */
      public String getDataSourceId() {
        return dataSourceId;
      }

      @Override
      public String toString() {
        try {
          return JsonUtil.toJson(this);
        } catch (JsonProcessingException e) {
          LOG.error("Error serializing DataSourceMetric to JSON. dataSourceName: {}",
              dataSourceName, e);
          return String.format(
              "{\"dataSourceName\":\"%s\",\"offlineRecordIngestCount\":\"%s\",\"dataSourceId\":\"%s\",\"error\":\"Error converting to JSON\"}",
              dataSourceName, offlineRecordIngestCount, dataSourceId);
        }
      }
    }

    /**
     * Represents the individual feature view ingestion metrics in Tecton API response.
     */
    public static class FeatureViewMetric {

      private static final Logger LOG = LoggerFactory.getLogger(FeatureViewMetric.class);

      @JsonProperty("featureViewName")
      private final String featureViewName;

      @JsonProperty("onlineRecordIngestCount")
      private final String onlineRecordIngestCount;

      @JsonProperty("offlineRecordIngestCount")
      private final String offlineRecordIngestCount;

      @JsonProperty("featureViewId")
      private final String featureViewId;

      /**
       * Constructor for setting fields directly.
       *
       * @param featureViewName the name of the feature view
       * @param onlineRecordIngestCount the count of online records ingested
       * @param offlineRecordIngestCount the count of offline records ingested
       * @param featureViewId the ID of the feature view
       */
      @JsonCreator
      public FeatureViewMetric(@JsonProperty("featureViewName") String featureViewName,
          @JsonProperty("onlineRecordIngestCount") String onlineRecordIngestCount,
          @JsonProperty("offlineRecordIngestCount") String offlineRecordIngestCount,
          @JsonProperty("featureViewId") String featureViewId) {
        this.featureViewName = featureViewName;
        this.onlineRecordIngestCount = onlineRecordIngestCount;
        this.offlineRecordIngestCount = offlineRecordIngestCount;
        this.featureViewId = featureViewId;
      }

      /**
       * Gets the feature view name.
       *
       * @return the feature view name.
       */
      public String getFeatureViewName() {
        return featureViewName;
      }

      /**
       * Gets the online record ingest count.
       *
       * @return the online record ingest count.
       */
      public String getOnlineRecordIngestCount() {
        return onlineRecordIngestCount;
      }

      /**
       * Gets the offline record ingest count.
       *
       * @return the offline record ingest count.
       */
      public String getOfflineRecordIngestCount() {
        return offlineRecordIngestCount;
      }

      /**
       * Gets the feature view ID.
       *
       * @return the feature view ID.
       */
      public String getFeatureViewId() {
        return featureViewId;
      }

      @Override
      public String toString() {
        try {
          return JsonUtil.toJson(this);
        } catch (JsonProcessingException e) {
          LOG.error("Error serializing FeatureViewMetric to JSON. featureViewName: {}",
              featureViewName, e);
          return String.format(
              "{\"featureViewName\":\"%s\",\"onlineRecordIngestCount\":\"%s\",\"offlineRecordIngestCount\":\"%s\",\"featureViewId\":\"%s\",\"error\":\"Error converting to JSON\"}",
              featureViewName, onlineRecordIngestCount, offlineRecordIngestCount, featureViewId);
        }
      }
    }
  }
}
