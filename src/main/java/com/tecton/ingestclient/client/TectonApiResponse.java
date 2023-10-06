package com.tecton.ingestclient.client;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents a response from the Tecton API containing workspace information and ingestion metrics.
 */
public class TectonApiResponse {

  @JsonProperty("workspaceName")
  private String workspaceName;

  @JsonProperty("ingestMetrics")
  private IngestMetrics ingestMetrics;

  /**
   * Default constructor used by Jackson for deserialisation.
   */
  public TectonApiResponse() {}

  /**
   * Retrieves the workspace name.
   * 
   * @return the workspace name.
   */
  public String getWorkspaceName() {
    return workspaceName;
  }

  /**
   * Sets the workspace name.
   * 
   * @param workspaceName the name of the workspace to set.
   */
  public void setWorkspaceName(String workspaceName) {
    this.workspaceName = workspaceName;
  }

  /**
   * Retrieves the ingestion metrics.
   * 
   * @return the ingestion metrics.
   */
  public IngestMetrics getIngestMetrics() {
    return ingestMetrics;
  }

  /**
   * Sets the ingestion metrics.
   * 
   * @param ingestMetrics the metrics related to ingestion to set.
   */
  public void setIngestMetrics(IngestMetrics ingestMetrics) {
    this.ingestMetrics = ingestMetrics;
  }

  @Override
  public String toString() {
    return toJson(this);
  }

  /**
   * Converts the given object to its JSON representation.
   *
   * @param obj The object to convert to JSON.
   * @return A string representing the object in JSON format or an error message.
   */
  private static String toJson(Object obj) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      return "Error converting to JSON";
    }
  }

  /**
   * Represents the ingestion metrics details in Tecton API response.
   */
  public static class IngestMetrics {

    @JsonProperty("featureViewIngestMetrics")
    private List<FeatureViewMetric> featureViewIngestMetrics;

    /**
     * Default constructor used by Jackson for deserialisation.
     */
    public IngestMetrics() {}

    /**
     * Retrieves the feature view ingestion metrics.
     * 
     * @return the feature view ingestion metrics.
     */
    public List<FeatureViewMetric> getFeatureViewIngestMetrics() {
      return featureViewIngestMetrics;
    }

    /**
     * Sets the feature view ingestion metrics.
     * 
     * @param featureViewIngestMetrics the metrics related to feature view ingestion to set.
     */
    public void setFeatureViewIngestMetrics(List<FeatureViewMetric> featureViewIngestMetrics) {
      this.featureViewIngestMetrics = featureViewIngestMetrics;
    }

    @Override
    public String toString() {
      return toJson(this);
    }

    /**
     * Represents the feature view metric details in Tecton API response.
     */
    public static class FeatureViewMetric {

      @JsonProperty("featureViewName")
      private String featureViewName;

      @JsonProperty("onlineRecordIngestCount")
      private String onlineRecordIngestCount;

      @JsonProperty("offlineRecordIngestCount")
      private String offlineRecordIngestCount;

      /**
       * Default constructor used by Jackson for deserialisation.
       */
      public FeatureViewMetric() {}

      /**
       * Retrieves the feature view name.
       * 
       * @return the feature view name.
       */
      public String getFeatureViewName() {
        return featureViewName;
      }

      /**
       * Sets the feature view name.
       * 
       * @param featureViewName the name of the feature view to set.
       */
      public void setFeatureViewName(String featureViewName) {
        this.featureViewName = featureViewName;
      }

      /**
       * Retrieves the count of records ingested online.
       * 
       * @return the count of online ingested records.
       */
      public String getOnlineRecordIngestCount() {
        return onlineRecordIngestCount;
      }

      /**
       * Sets the count of records ingested online.
       * 
       * @param onlineRecordIngestCount the count of online ingested records to set.
       */
      public void setOnlineRecordIngestCount(String onlineRecordIngestCount) {
        this.onlineRecordIngestCount = onlineRecordIngestCount;
      }

      /**
       * Retrieves the count of records ingested offline.
       * 
       * @return the count of offline ingested records.
       */
      public String getOfflineRecordIngestCount() {
        return offlineRecordIngestCount;
      }

      /**
       * Sets the count of records ingested offline.
       * 
       * @param offlineRecordIngestCount the count of offline ingested records to set.
       */
      public void setOfflineRecordIngestCount(String offlineRecordIngestCount) {
        this.offlineRecordIngestCount = offlineRecordIngestCount;
      }

      @Override
      public String toString() {
        return toJson(this);
      }
    }
  }
}
