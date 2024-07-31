package com.tecton.ingestclient.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for JSON processing using Jackson. Provides methods to convert objects to JSON
 * strings and parse JSON strings to objects. This class is configured to ignore unknown properties
 * in JSON responses to ensure flexibility with API changes.
 */
public class JsonUtil {

  private static final Logger LOG = LoggerFactory.getLogger(JsonUtil.class);
  private static final ObjectMapper OBJECT_MAPPER;

  static {
    OBJECT_MAPPER = new ObjectMapper();
    OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  private JsonUtil() {
    // Utility class
  }

  /**
   * Converts an object to a JSON string with pretty printing.
   *
   * @param obj the object to convert to JSON.
   * @return the JSON string representation of the object.
   */
  public static String toJson(Object obj) {
    try {
      return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      LOG.warn("Error converting to JSON", e);
      return "Error converting to JSON";
    }
  }

  /**
   * Parses a JSON string to an object of the specified type.
   *
   * @param json the JSON string to parse.
   * @param valueType the class of the type to parse the JSON string to.
   * @param <T> the type of the object to return.
   * @return the object parsed from the JSON string.
   * @throws RuntimeException if there is an error parsing the JSON string.
   */
  public static <T> T fromJson(String json, Class<T> valueType) {
    try {
      return OBJECT_MAPPER.readValue(json, valueType);
    } catch (JsonProcessingException e) {
      LOG.error("Error parsing JSON", e);
      throw new RuntimeException("Error parsing JSON", e);
    }
  }

  /**
   * Gets the shared ObjectMapper instance.
   *
   * @return the shared ObjectMapper instance.
   */
  public static ObjectMapper getMapper() {
    return OBJECT_MAPPER;
  }
}
