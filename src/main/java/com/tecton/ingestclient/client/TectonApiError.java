package com.tecton.ingestclient.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tecton.ingestclient.util.JsonUtil;

import java.util.List;

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
   * Constructor for setting fields directly.
   *
   * @param requestError the request error details
   * @param workspaceName the name of the workspace
   * @param recordErrors the list of record errors
   */
  @JsonCreator
  public TectonApiError(@JsonProperty("requestError") RequestError requestError,
      @JsonProperty("workspaceName") String workspaceName,
      @JsonProperty("recordErrors") List<RecordError> recordErrors) {
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
     * Constructor for setting fields directly.
     *
     * @param errorMessage the error message
     * @param errorType the type of error
     */
    @JsonCreator
    public RequestError(@JsonProperty("errorMessage") String errorMessage,
        @JsonProperty("errorType") String errorType) {
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
     * Constructor for setting fields directly.
     *
     * @param featureViewName the name of the feature view
     * @param pushSourceName the name of the push source
     * @param errorType the type of error
     * @param errorMessage the error message
     */
    @JsonCreator
    public RecordError(@JsonProperty("featureViewName") String featureViewName,
        @JsonProperty("pushSourceName") String pushSourceName,
        @JsonProperty("errorType") String errorType,
        @JsonProperty("errorMessage") String errorMessage) {
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
