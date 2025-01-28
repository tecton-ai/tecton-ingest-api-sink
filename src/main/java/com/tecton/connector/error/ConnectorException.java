package com.tecton.connector.error;

/**
 * Base exception class for the connector, providing a common superclass for all connector exceptions.
 */
public class ConnectorException extends Exception {

    /**
     * Constructs a new ConnectorException with the specified detail message.
     *
     * @param message The detail message.
     */
    public ConnectorException(String message) {
        super(message);
    }

    /**
     * Constructs a new ConnectorException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of the exception.
     */
    public ConnectorException(String message, Throwable cause) {
        super(message, cause);
    }
}
