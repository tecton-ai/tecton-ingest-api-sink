package com.tecton.ingestclient.client;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tecton.ingestclient.util.JsonUtil;

/**
 * Represents an error response from the Tecton API. Used for deserializing JSON error responses
 * from the API. This class is configured to be resilient to changes in the JSON structure, allowing
 * it to ignore unexpected fields.
 */
public class TectonApiError {

  @JsonProperty("requestError")
  private final RequestError requestError;

  @JsonProperty("workspaceName")
  private final String workspaceName;

  @JsonProperty("recordErrors")
  private final List<RecordError> recordErrors;

  /**
   * Default constructor for Jackson deserialization.
   */
  public TectonApiError() {
    this.requestError = null;
    this.workspaceName = null;
    this.recordErrors = null;
  }

  /**
   * Constructor for setting fields directly.
   */
  public TectonApiError(RequestError requestError, String workspaceName,
      List<RecordError> recordErrors) {
    this.requestError = requestError;
    this.workspaceName = workspaceName;
    this.recordErrors = recordErrors;
  }

  /**
   * Gets the request error details.
   *
   * @return the request error details.
   */
  public RequestError getRequestError() {
    return requestError;
  }

  /**
   * Gets the workspace name associated with the error.
   *
   * @return the workspace name.
   */
  public String getWorkspaceName() {
    return workspaceName;
  }

  /**
   * Gets the list of record errors.
   *
   * @return the list of record errors.
   */
  public List<RecordError> getRecordErrors() {
    return recordErrors;
  }

  @Override
  public String toString() {
    return JsonUtil.toJson(this);
  }

  /**
   * Represents the request error details in a Tecton API error response.
   */
  public static class RequestError {

    @JsonProperty("errorMessage")
    private final String errorMessage;

    @JsonProperty("errorType")
    private final String errorType;

    /**
     * Default constructor for Jackson deserialization.
     */
    public RequestError() {
      this.errorMessage = null;
      this.errorType = null;
    }

    /**
     * Constructor for setting fields directly.
     */
    public RequestError(String errorMessage, String errorType) {
      this.errorMessage = errorMessage;
      this.errorType = errorType;
    }

    /**
     * Gets the error message.
     *
     * @return the error message.
     */
    public String getErrorMessage() {
      return errorMessage;
    }

    /**
     * Gets the error type.
     *
     * @return the error type.
     */
    public String getErrorType() {
      return errorType;
    }

    @Override
    public String toString() {
      return JsonUtil.toJson(this);
    }
  }

  /**
   * Represents individual record error details in a Tecton API error response.
   */
  public static class RecordError {

    @JsonProperty("featureViewName")
    private final String featureViewName;

    @JsonProperty("pushSourceName")
    private final String pushSourceName;

    @JsonProperty("errorType")
    private final String errorType;

    @JsonProperty("errorMessage")
    private final String errorMessage;

    /**
     * Default constructor for Jackson deserialization.
     */
    public RecordError() {
      this.featureViewName = null;
      this.pushSourceName = null;
      this.errorType = null;
      this.errorMessage = null;
    }

    /**
     * Constructor for setting fields directly.
     */
    public RecordError(String featureViewName, String pushSourceName, String errorType,
        String errorMessage) {
      this.featureViewName = featureViewName;
      this.pushSourceName = pushSourceName;
      this.errorType = errorType;
      this.errorMessage = errorMessage;
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
     * Gets the push source name.
     *
     * @return the push source name.
     */
    public String getPushSourceName() {
      return pushSourceName;
    }

    /**
     * Gets the error type.
     *
     * @return the error type.
     */
    public String getErrorType() {
      return errorType;
    }

    /**
     * Gets the error message.
     *
     * @return the error message.
     */
    public String getErrorMessage() {
      return errorMessage;
    }

    @Override
    public String toString() {
      return JsonUtil.toJson(this);
    }
  }
}
