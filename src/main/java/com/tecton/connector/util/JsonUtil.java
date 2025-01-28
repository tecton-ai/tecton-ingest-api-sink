package com.tecton.connector.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for JSON serialization and deserialization using Jackson.
 * Provides methods to convert objects to JSON strings and parse JSON strings to objects.
 * Configured to ignore unknown properties in JSON responses for flexibility with API changes.
 */
public final class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    private JsonUtil() {
        // Prevent instantiation
    }

    /**
     * Creates and configures the ObjectMapper.
     *
     * @return The configured ObjectMapper.
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Add additional configurations if necessary
        return mapper;
    }

    /**
     * Converts an object to a JSON string with pretty printing.
     *
     * @param obj the object to convert to JSON.
     * @return the JSON string representation of the object.
     * @throws JsonProcessingException if there is an error converting the object to JSON.
     */
    public static String toJson(Object obj) throws JsonProcessingException {
        return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    /**
     * Parses a JSON string to an object of the specified type.
     *
     * @param json      the JSON string to parse.
     * @param valueType the class of the type to parse the JSON string to.
     * @param <T>       the type of the object to return.
     * @return the object parsed from the JSON string.
     * @throws JsonProcessingException if there is an error parsing the JSON string.
     */
    public static <T> T fromJson(String json, Class<T> valueType) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, valueType);
    }

    /**
     * Gets the shared ObjectMapper instance.
     *
     * @return the shared ObjectMapper instance.
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
