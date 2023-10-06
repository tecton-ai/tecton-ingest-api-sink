package com.tecton.ingestclient.converter;

import java.util.List;
import java.util.Map;
import org.apache.kafka.connect.errors.DataException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents a Tecton Record with associated Kafka metadata.
 */
public class TectonRecord {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @JsonProperty("record")
    private final Map<String, Object> recordData;

    /**
     * Constructs a new Tecton Record.
     *
     * @param recordData The main record data.
     */
    public TectonRecord(final Map<String, Object> recordData) {
        this.recordData = recordData;
    }

    /**
     * @return The main record data.
     */
    public Map<String, Object> getRecordData() {
      return recordData;
    }

    /**
     * Determines if the record data is valid. 
     * A valid record data is one where all values are either of primitive type, List, or Map.
     *
     * @return True if the record data is valid, false otherwise.
     */
    @JsonIgnore
    public boolean isValid() {
        return recordData.values().stream().allMatch(TectonRecord::isValidTectonValue);
    }

    /**
     * Helper method to determine if a given value is a valid Tecton value.
     *
     * @param value The object to check.
     * @return True if the value is valid, false otherwise.
     */
    private static boolean isValidTectonValue(final Object value) {
        if (value == null) return true;
        if (value instanceof String ||  value instanceof Number || value instanceof Boolean) {
            return true;
        }
        if (value instanceof List) {
            return ((List<?>) value).stream().allMatch(TectonRecord::isValidTectonValue);
        }
        if (value instanceof Map) {
            return ((Map<?, ?>) value).keySet().stream().allMatch(key -> key instanceof String)
                    && ((Map<?, ?>) value).values().stream().allMatch(TectonRecord::isValidTectonValue);
        }
        return false;
    }

    /**
     * Converts the Tecton Record to a JSON string representation.
     *
     * @return The JSON string representation of the Tecton Record.
     * @throws DataException if there's an error during conversion.
     */
    public String toJson() throws DataException {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new DataException("Failed to convert TectonRecord to JSON string.", e);
        }
    }
    
    /**
     * Converts the Tecton Record to a string representation.
     *
     * @return The JSON string representation of the Tecton Record.
     */
    @Override
    public String toString() {
        try {
            return this.toJson();
        } catch (DataException e) {
            return "TectonRecord [conversion to JSON failed]";
        }
    }
 }
