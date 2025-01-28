package com.tecton.connector.error;

/**
 * Exception thrown when an error occurs during HTTP communication with the Tecton API.
 */
public class HttpClientException extends ConnectorException {

    private final int statusCode;

    /**
     * Constructs a new HttpClientException with the specified detail message.
     *
     * @param message The detail message.
     */
    public HttpClientException(String message) {
        super(message);
        this.statusCode = -1;
    }

    /**
     * Constructs a new HttpClientException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of the exception.
     */
    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
    }

    /**
     * Constructs a new HttpClientException with the specified detail message and status code.
     *
     * @param message    The detail message.
     * @param statusCode The HTTP status code associated with the error.
     */
    public HttpClientException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Constructs a new HttpClientException with the specified detail message, cause, and status code.
     *
     * @param message    The detail message.
     * @param cause      The cause of the exception.
     * @param statusCode The HTTP status code associated with the error.
     */
    public HttpClientException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * Returns the HTTP status code associated with the exception.
     *
     * @return The HTTP status code, or -1 if not applicable.
     */
    public int getStatusCode() {
        return statusCode;
    }
}
