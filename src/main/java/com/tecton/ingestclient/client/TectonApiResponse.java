package com.tecton.ingestclient.client;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tecton.ingestclient.util.JsonUtil;

/**
 * Represents a response from the Tecton API containing workspace information and ingestion metrics.
 * This class is configured to ignore unknown properties in JSON responses to ensure flexibility
 * with API changes.
 */
public class TectonApiResponse {

  @JsonProperty("workspaceName")
  private final String workspaceName;

  @JsonProperty("ingestMetrics")
  private final IngestMetrics ingestMetrics;

  /**
   * Default constructor used by Jackson for deserialization.
   */
  public TectonApiResponse() {
    this.workspaceName = null;
    this.ingestMetrics = null;
  }

  /**
   * Constructor for setting fields directly.
   */
  public TectonApiResponse(String workspaceName, IngestMetrics ingestMetrics) {
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
    return JsonUtil.toJson(this);
  }

  /**
   * Represents the ingestion metrics details in Tecton API response.
   */
  public static class IngestMetrics {

    @JsonProperty("featureViewIngestMetrics")
    private final List<FeatureViewMetric> featureViewIngestMetrics;

    @JsonProperty("dataSourceIngestMetrics")
    private final List<DataSourceMetric> dataSourceIngestMetrics;

    /**
     * Default constructor for Jackson deserialization.
     */
    public IngestMetrics() {
      this.featureViewIngestMetrics = null;
      this.dataSourceIngestMetrics = null;
    }

    /**
     * Constructor for setting fields directly.
     */
    public IngestMetrics(List<FeatureViewMetric> featureViewIngestMetrics,
        List<DataSourceMetric> dataSourceIngestMetrics) {
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
      return JsonUtil.toJson(this);
    }

    /**
     * Represents the individual ingestion metric details for data sources in Tecton API response.
     */
    public static class DataSourceMetric {

      @JsonProperty("dataSourceName")
      private final String dataSourceName;

      @JsonProperty("offlineRecordIngestCount")
      private final String offlineRecordIngestCount;

      @JsonProperty("dataSourceId")
      private final String dataSourceId;

      /**
       * Default constructor for Jackson deserialization.
       */
      public DataSourceMetric() {
        this.dataSourceName = null;
        this.offlineRecordIngestCount = null;
        this.dataSourceId = null;
      }

      /**
       * Constructor for setting fields directly.
       */
      public DataSourceMetric(String dataSourceName, String offlineRecordIngestCount,
          String dataSourceId) {
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
        return JsonUtil.toJson(this);
      }
    }

    /**
     * Represents the individual feature view ingestion metrics in Tecton API response.
     */
    public static class FeatureViewMetric {

      @JsonProperty("featureViewName")
      private final String featureViewName;

      @JsonProperty("onlineRecordIngestCount")
      private final String onlineRecordIngestCount;

      @JsonProperty("offlineRecordIngestCount")
      private final String offlineRecordIngestCount;

      @JsonProperty("featureViewId")
      private final String featureViewId;

      /**
       * Default constructor for Jackson deserialization.
       */
      public FeatureViewMetric() {
        this.featureViewName = null;
        this.onlineRecordIngestCount = null;
        this.offlineRecordIngestCount = null;
        this.featureViewId = null;
      }

      /**
       * Constructor for setting fields directly.
       */
      public FeatureViewMetric(String featureViewName, String onlineRecordIngestCount,
          String offlineRecordIngestCount, String featureViewId) {
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
        return JsonUtil.toJson(this);
      }
    }
  }
}
