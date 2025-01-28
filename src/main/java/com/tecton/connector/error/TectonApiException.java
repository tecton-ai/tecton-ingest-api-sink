package com.tecton.connector.error;

/**
 * Exception thrown when the Tecton API returns an error response.
 */
public class TectonApiException extends ConnectorException {

    private final int statusCode;
    private final String responseBody;

    /**
     * Constructs a new ApiException with the specified detail message and status code.
     *
     * @param message      The detail message.
     * @param statusCode   The HTTP status code returned by the Tecton API.
     * @param responseBody The response body containing error details.
     */
    public TectonApiException(String message, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * Constructs a new ApiException with the specified detail message, cause, status code, and response body.
     *
     * @param message      The detail message.
     * @param cause        The cause of the exception.
     * @param statusCode   The HTTP status code returned by the Tecton API.
     * @param responseBody The response body containing error details.
     */
    public TectonApiException(String message, Throwable cause, int statusCode, String responseBody) {
        super(message, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * Returns the HTTP status code returned by the Tecton API.
     *
     * @return The HTTP status code.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the response body containing error details from the Tecton API.
     *
     * @return The response body as a String.
     */
    public String getResponseBody() {
        return responseBody;
    }
}
