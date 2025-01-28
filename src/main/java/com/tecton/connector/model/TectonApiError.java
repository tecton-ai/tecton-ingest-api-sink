package com.tecton.connector.model;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tecton.connector.util.JsonUtil;

/**
 * Represents an error response from the Tecton API, capturing request and record-specific errors.
 * This class encapsulates detailed error information including request-level errors and errors
 * specific to individual records processed by the API.
 */
public class TectonApiError {

    private static final Logger LOG = LoggerFactory.getLogger(TectonApiError.class);

    @JsonProperty("requestError")
    private final RequestError requestError;

    @JsonProperty("workspaceName")
    private final String workspaceName;

    @JsonProperty("recordErrors")
    private final List<RecordError> recordErrors;

    /**
     * Constructs a TectonApiError with the specified details.
     *
     * @param requestError   the request error details, which capture high-level issues with the API request.
     * @param workspaceName  the name of the workspace where the error occurred.
     * @param recordErrors   the list of record errors that capture issues with individual records processed by the API.
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
     * Returns the request-specific error details.
     *
     * @return the {@link RequestError} object containing error details about the overall API request.
     */
    public RequestError getRequestError() {
        return requestError;
    }

    /**
     * Returns the name of the workspace where the error occurred.
     *
     * @return the workspace name as a {@link String}.
     */
    public String getWorkspaceName() {
        return workspaceName;
    }

    /**
     * Returns the list of errors related to individual records.
     *
     * @return a {@link List} of {@link RecordError} objects capturing errors for specific records.
     */
    public List<RecordError> getRecordErrors() {
        return recordErrors;
    }

    /**
     * Converts the TectonApiError object to a JSON string representation.
     *
     * @return a JSON string representation of the TectonApiError object.
     */
    @Override
    public String toString() {
        try {
            return JsonUtil.toJson(this);
        } catch (JsonProcessingException e) {
            LOG.error("Error serializing TectonApiError to JSON. workspaceName: {}", workspaceName, e);
            return String.format(
                    "{\"workspaceName\":\"%s\",\"requestError\":\"%s\",\"recordErrorsCount\":%d}",
                    workspaceName, requestError, recordErrors != null ? recordErrors.size() : 0);
        }
    }

    /**
     * Represents request-specific error details in a Tecton API error response.
     * This class captures high-level issues associated with the overall API request.
     */
    public static class RequestError {

        private static final Logger LOG = LoggerFactory.getLogger(RequestError.class);

        @JsonProperty("errorMessage")
        private final String errorMessage;

        @JsonProperty("errorType")
        private final String errorType;

        /**
         * Constructs a RequestError with the specified message and type.
         *
         * @param errorMessage the error message providing details about what went wrong.
         * @param errorType    the type of error, describing the category of the issue encountered.
         */
        @JsonCreator
        public RequestError(@JsonProperty("errorMessage") String errorMessage,
                            @JsonProperty("errorType") String errorType) {
            this.errorMessage = errorMessage;
            this.errorType = errorType;
        }

        /**
         * Returns the error message describing the nature of the request error.
         *
         * @return the error message as a {@link String}.
         */
        public String getErrorMessage() {
            return errorMessage;
        }

        /**
         * Returns the type of error associated with the request.
         *
         * @return the error type as a {@link String}.
         */
        public String getErrorType() {
            return errorType;
        }

        /**
         * Converts the RequestError object to a JSON string representation.
         *
         * @return a JSON string representation of the RequestError object.
         */
        @Override
        public String toString() {
            try {
                return JsonUtil.toJson(this);
            } catch (JsonProcessingException e) {
                LOG.error("Error serializing RequestError to JSON. errorType: {}, errorMessage: {}", errorType, errorMessage, e);
                return String.format(
                        "{\"errorType\":\"%s\",\"errorMessage\":\"%s\"}", errorType, errorMessage);
            }
        }
    }

    /**
     * Represents record-specific error details in a Tecton API error response.
     * This class captures detailed information about errors related to individual records.
     */
    public static class RecordError {

        private static final Logger LOG = LoggerFactory.getLogger(RecordError.class);

        @JsonProperty("featureViewName")
        private final String featureViewName;

        @JsonProperty("pushSourceName")
        private final String pushSourceName;

        @JsonProperty("errorType")
        private final String errorType;

        @JsonProperty("errorMessage")
        private final String errorMessage;

        /**
         * Constructs a RecordError with details about the feature view, source, and error specifics.
         *
         * @param featureViewName the name of the feature view where the error occurred.
         * @param pushSourceName  the name of the push source associated with the error.
         * @param errorType       the type of error that occurred for the record.
         * @param errorMessage    the detailed error message describing what went wrong.
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
         * Returns the name of the feature view associated with the error.
         *
         * @return the feature view name as a {@link String}.
         */
        public String getFeatureViewName() {
            return featureViewName;
        }

        /**
         * Returns the name of the push source associated with the error.
         *
         * @return the push source name as a {@link String}.
         */
        public String getPushSourceName() {
            return pushSourceName;
        }

        /**
         * Returns the type of error encountered for the record.
         *
         * @return the error type as a {@link String}.
         */
        public String getErrorType() {
            return errorType;
        }

        /**
         * Returns the error message describing the specifics of the error.
         *
         * @return the error message as a {@link String}.
         */
        public String getErrorMessage() {
            return errorMessage;
        }

        /**
         * Converts the RecordError object to a JSON string representation.
         *
         * @return a JSON string representation of the RecordError object.
         */
        @Override
        public String toString() {
            try {
                return JsonUtil.toJson(this);
            } catch (JsonProcessingException e) {
                LOG.error("Error serializing RecordError to JSON. featureViewName: {}, pushSourceName: {}, errorType: {}", 
                          featureViewName, pushSourceName, errorType, e);
                return String.format(
                        "{\"featureViewName\":\"%s\",\"pushSourceName\":\"%s\",\"errorType\":\"%s\",\"errorMessage\":\"%s\"}", 
                        featureViewName, pushSourceName, errorType, errorMessage);
            }
        }
    }
}
