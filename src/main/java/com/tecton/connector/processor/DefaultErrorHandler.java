package com.tecton.connector.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of ErrorHandler that logs errors.
 */
public class DefaultErrorHandler implements ErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultErrorHandler.class);

    @Override
    public void handle(Throwable throwable) {
        LOG.error("Error during record processing: {}", throwable.getMessage(), throwable);
        // Additional error handling logic can be added here
    }
}
