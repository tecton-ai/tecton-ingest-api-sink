package com.tecton.connector.error;

/**
 * Exception thrown when a record is invalid or cannot be processed.
 */
public class InvalidRecordException extends ConnectorException {

    /**
     * Constructs a new InvalidRecordException with the specified detail message.
     *
     * @param message The detail message.
     */
    public InvalidRecordException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidRecordException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of the exception.
     */
    public InvalidRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}
