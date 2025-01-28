package com.tecton.connector.util;

import org.slf4j.Logger;

/**
 * Utility class for enhanced logging methods.
 */
public final class LoggingUtil {

    private LoggingUtil() {
        // Prevent instantiation
    }

    /**
     * Logs a debug message if debug logging is enabled.
     *
     * @param logger  The logger instance.
     * @param message The message to log.
     */
    public static void debug(Logger logger, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    /**
     * Logs a formatted debug message if debug logging is enabled.
     *
     * @param logger The logger instance.
     * @param format The format string.
     * @param args   The arguments.
     */
    public static void debug(Logger logger, String format, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(format, args);
        }
    }

    // Add additional logging methods as needed
}
