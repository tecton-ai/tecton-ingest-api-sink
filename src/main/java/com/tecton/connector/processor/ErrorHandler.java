package com.tecton.connector.processor;

/**
 * Defines a handler for processing errors that occur during record processing.
 */
public interface ErrorHandler {

    /**
     * Handles an exception that occurred during processing.
     *
     * @param throwable The exception to handle.
     */
    void handle(Throwable throwable);
}
