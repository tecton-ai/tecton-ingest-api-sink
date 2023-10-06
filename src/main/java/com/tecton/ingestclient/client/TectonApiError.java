package com.tecton.ingestclient.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Represents an error response from the Tecton API. Used for deserialising JSON error responses
 * from the API.
 */
public class TectonApiError {

  @JsonProperty("requestError")
  private RequestError requestError;

  @JsonProperty("workspaceName")
  private String workspaceName;

  @JsonProperty("recordErrors")
  private List<RecordError> recordErrors;

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /**
   * Default constructor. Intended for Jackson deserialisation.
   */
  public TectonApiError() {
    // Left blank for Jackson deserialisation
  }

  /**
   * Retrieves the request error details.
   * 
   * @return the request error details.
   */
  public RequestError getRequestError() {
    return requestError;
  }

  /**
   * Sets the request error details.
   *
   * @param requestError the details to set.
   */
  public void setRequestError(RequestError requestError) {
    this.requestError = requestError;
  }

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
   * @param workspaceName the name to set.
   */
  public void setWorkspaceName(String workspaceName) {
    this.workspaceName = workspaceName;
  }

  /**
   * Retrieves the list of record errors.
   * 
   * @return the list of record errors.
   */
  public List<RecordError> getRecordErrors() {
    return recordErrors;
  }

  /**
   * Sets the list of record errors.
   *
   * @param recordErrors the list to set.
   */
  public void setRecordErrors(List<RecordError> recordErrors) {
    this.recordErrors = recordErrors;
  }

  /**
   * Converts this instance into its JSON representation.
   *
   * @return the JSON representation or a descriptive error string if conversion fails.
   */
  @Override
  public String toString() {
    return toJson(this);
  }

  /**
   * Converts an object into its JSON representation.
   *
   * @param obj the object to convert.
   * @return the JSON representation or a descriptive error string if conversion fails.
   */
  private static String toJson(Object obj) {
    try {
      return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      return "Error converting to JSON";
    }
  }

  /**
   * Represents the request error details in a Tecton API error response.
   */
  public static class RequestError {

    @JsonProperty("errorMessage")
    private String errorMessage;

    @JsonProperty("errorType")
    private String errorType;

    /**
     * Default constructor. Intended for Jackson deserialisation.
     */
    public RequestError() {
      // Left blank for Jackson deserialisation
    }

    /**
     * Retrieves the error message.
     *
     * @return the error message.
     */
    public String getErrorMessage() {
      return errorMessage;
    }

    /**
     * Sets the error message.
     *
     * @param errorMessage the message to set.
     */
    public void setErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
    }

    /**
     * Retrieves the error type.
     * 
     * @return the error type.
     */
    public String getErrorType() {
      return errorType;
    }

    /**
     * Sets the error type.
     *
     * @param errorType the type to set.
     */
    public void setErrorType(String errorType) {
      this.errorType = errorType;
    }

    /**
     * Converts this instance into its JSON representation.
     *
     * @return the JSON representation or a descriptive error string if conversion fails.
     */
    @Override
    public String toString() {
      return toJson(this);
    }
  }

  /**
   * Represents individual record error details in a Tecton API error response.
   */
  public static class RecordError {

    @JsonProperty("featureViewName")
    private String featureViewName;

    @JsonProperty("pushSourceName")
    private String pushSourceName;

    @JsonProperty("errorType")
    private String errorType;

    @JsonProperty("errorMessage")
    private String errorMessage;

    /**
     * Default constructor. Intended for Jackson deserialisation.
     */
    public RecordError() {
      // Left blank for Jackson deserialisation
    }

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
     * @param featureViewName the name to set.
     */
    public void setFeatureViewName(String featureViewName) {
      this.featureViewName = featureViewName;
    }

    /**
     * Retrieves the push source name.
     * 
     * @return the push source name.
     */
    public String getPushSourceName() {
      return pushSourceName;
    }

    /**
     * Sets the push source name.
     *
     * @param pushSourceName the name to set.
     */
    public void setPushSourceName(String pushSourceName) {
      this.pushSourceName = pushSourceName;
    }

    /**
     * Retrieves the error type.
     * 
     * @return the error type.
     */
    public String getErrorType() {
      return errorType;
    }

    /**
     * Sets the error type.
     *
     * @param errorType the type to set.
     */
    public void setErrorType(String errorType) {
      this.errorType = errorType;
    }

    /**
     * Retrieves the error message.
     * 
     * @return the error message.
     */
    public String getErrorMessage() {
      return errorMessage;
    }

    /**
     * Sets the error message.
     *
     * @param errorMessage the message to set.
     */
    public void setErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
    }

    /**
     * Converts this instance into its JSON representation.
     *
     * @return the JSON representation or a descriptive error string if conversion fails.
     */
    @Override
    public String toString() {
      return toJson(this);
    }
  }
}
