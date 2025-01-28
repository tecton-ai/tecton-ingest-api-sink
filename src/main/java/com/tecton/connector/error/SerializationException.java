package com.tecton.connector.error;

/**
 * Exception thrown when serialization or deserialization of data fails.
 */
public class SerializationException extends ConnectorException {

    /**
     * Constructs a new SerializationException with the specified detail message.
     *
     * @param message The detail message.
     */
    public SerializationException(String message) {
        super(message);
    }

    /**
     * Constructs a new SerializationException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of the exception.
     */
    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
