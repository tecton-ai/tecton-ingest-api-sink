package com.tecton.connector.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecton.connector.error.InvalidRecordException;
import com.tecton.connector.error.SerializationException;
import com.tecton.connector.model.TectonRecord;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Implementation of RecordConverter that converts SinkRecords into TectonRecords using JSON serialization.
 */
public class JsonRecordConverter implements RecordConverter {

    private static final Logger LOG = LoggerFactory.getLogger(JsonRecordConverter.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Thread-local instance of JsonConverter for thread safety
    private static final ThreadLocal<JsonConverter> JSON_CONVERTER = ThreadLocal.withInitial(() -> {
        JsonConverter converter = new JsonConverter();
        converter.configure(Map.of("schemas.enable", "false"), false);
        return converter;
    });

    @Override
    public TectonRecord convert(SinkRecord record) throws InvalidRecordException, SerializationException {
        Object value = record.value();
        try {
            Map<String, Object> recordData;
            if (value instanceof String) {
                recordData = parseJsonString((String) value);
            } else if (value instanceof Struct) {
                recordData = structToMap(record.topic(), record.valueSchema(), value);
            } else if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> mapValue = (Map<String, Object>) value;
                recordData = mapValue;
            } else {
                throw new InvalidRecordException("Unsupported record value type: " + value.getClass().getName());
            }
            return new TectonRecord(recordData);
        } catch (JsonProcessingException | DataException e) {
            throw new SerializationException("Failed to convert record from topic " + record.topic(), e);
        }
    }

    private Map<String, Object> parseJsonString(String json) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, Map.class);
    }

    private Map<String, Object> structToMap(String topic, Schema schema, Object struct) throws DataException, JsonProcessingException {
        JsonConverter jsonConverter = JSON_CONVERTER.get();
        byte[] jsonBytes = jsonConverter.fromConnectData(topic, schema, struct);
        String jsonString = new String(jsonBytes, StandardCharsets.UTF_8);
        return OBJECT_MAPPER.readValue(jsonString, Map.class);
    }
}
