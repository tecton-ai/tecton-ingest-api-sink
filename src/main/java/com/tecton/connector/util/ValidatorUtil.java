package com.tecton.connector.util;

/**
 * Utility class for validation methods.
 */
public final class ValidatorUtil {

    private ValidatorUtil() {
        // Prevent instantiation
    }

    /**
     * Validates that a string is not null or empty.
     *
     * @param value   The string to validate.
     * @param message The exception message if validation fails.
     * @throws IllegalArgumentException If the string is null or empty.
     */
    public static void validateNotEmpty(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates that an object is not null.
     *
     * @param object  The object to validate.
     * @param message The exception message if validation fails.
     * @throws IllegalArgumentException If the object is null.
     */
    public static void validateNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    // Add additional validation methods as needed
}
