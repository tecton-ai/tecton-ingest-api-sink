package com.tecton.ingestclient.client;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents an error response from the Tecton API.
 * This class is used for deserialising JSON error responses from the API.
 */
public class TectonApiError {

    @JsonProperty("requestError")
    private RequestError requestError;

    @JsonProperty("workspaceName")
    private String workspaceName;

    @JsonProperty("recordErrors")
    private List<RecordError> recordErrors;

    /**
     * Default constructor used by Jackson for deserialization.
     */
    public TectonApiError() {}

    /**
     * Gets the request error.
     * 
     * @return the {@link RequestError}.
     */
    public RequestError getRequestError() {
        return requestError;
    }

    /**
     * Sets the request error.
     * 
     * @param requestError the {@link RequestError} to set.
     */
    public void setRequestError(RequestError requestError) {
        this.requestError = requestError;
    }

    /**
     * Gets the workspace name.
     * 
     * @return the workspace name.
     */
    public String getWorkspaceName() {
        return workspaceName;
    }

    /**
     * Sets the workspace name.
     * 
     * @param workspaceName the workspace name to set.
     */
    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    /**
     * Gets the list of record errors.
     * 
     * @return the list of {@link RecordError}.
     */
    public List<RecordError> getRecordErrors() {
        return recordErrors;
    }

    /**
     * Sets the list of record errors.
     * 
     * @param recordErrors the list of {@link RecordError} to set.
     */
    public void setRecordErrors(List<RecordError> recordErrors) {
        this.recordErrors = recordErrors;
    }

    /**
     * Represents the current object as a string.
     * 
     * @return the string representation of the object.
     */
    @Override
    public String toString() {
        return toJson(this);
    }

    /**
     * Converts the given object to its JSON representation.
     *
     * @param obj the object to convert to JSON.
     * @return the JSON string representation of the object.
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
     * Represents the request error details in a Tecton API error response.
     */
    public static class RequestError {

        @JsonProperty("errorMessage")
        private String errorMessage;

        @JsonProperty("errorType")
        private String errorType;

        /**
         * Default constructor used by Jackson for deserialization.
         */
        public RequestError() {}

        /**
         * Gets the error message.
         * 
         * @return the error message.
         */
        public String getErrorMessage() {
            return errorMessage;
        }

        /**
         * Sets the error message.
         * 
         * @param errorMessage the error message to set.
         */
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
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
         * Sets the error type.
         * 
         * @param errorType the error type to set.
         */
        public void setErrorType(String errorType) {
            this.errorType = errorType;
        }

        /**
         * Represents the current object as a string.
         * 
         * @return the string representation of the object.
         */
        @Override
        public String toString() {
            return toJson(this);
        }
    }

    /**
     * Represents the record error details in a Tecton API error response.
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
         * Default constructor used by Jackson for deserialization.
         */
        public RecordError() {}

        /**
         * Gets the feature view name.
         * 
         * @return the feature view name.
         */
        public String getFeatureViewName() {
            return featureViewName;
        }

        /**
         * Sets the feature view name.
         * 
         * @param featureViewName the feature view name to set.
         */
        public void setFeatureViewName(String featureViewName) {
            this.featureViewName = featureViewName;
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
         * Sets the push source name.
         * 
         * @param pushSourceName the push source name to set.
         */
        public void setPushSourceName(String pushSourceName) {
            this.pushSourceName = pushSourceName;
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
         * Sets the error type.
         * 
         * @param errorType the error type to set.
         */
        public void setErrorType(String errorType) {
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
         * Sets the error message.
         * 
         * @param errorMessage the error message to set.
         */
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        /**
         * Represents the current object as a string.
         * 
         * @return the string representation of the object.
         */
        @Override
        public String toString() {
            return toJson(this);
        }
    }
}
