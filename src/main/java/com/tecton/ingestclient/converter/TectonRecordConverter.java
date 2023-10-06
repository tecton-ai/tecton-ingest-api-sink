package com.tecton.ingestclient.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecton.kafka.connect.TectonHttpSinkConnectorConfig;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TectonRecordConverter implements IRecordConverter {

    private static final Logger LOG = LoggerFactory.getLogger(TectonRecordConverter.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String SCHEMAS_ENABLE = "schemas.enable";
    private static final String SCHEMAS_ENABLE_VALUE = "false";

    private final JsonConverter jsonConverter = new JsonConverter();
    private final TectonHttpSinkConnectorConfig config;

    public TectonRecordConverter(TectonHttpSinkConnectorConfig config) {
        this.config = config;
        jsonConverter.configure(Collections.singletonMap(SCHEMAS_ENABLE, SCHEMAS_ENABLE_VALUE), false);
    }

    @Override
    public TectonRecord convert(SinkRecord record) {
        Map<String, Object> recordData = extractRecordData(record);
        extractAndAddMetadata(record, recordData);
        return new TectonRecord(recordData);
    }

    @Override
    public void close() {
        jsonConverter.close();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractRecordData(SinkRecord record) {
        Object value = record.value();
        if (value instanceof String) {
            try {
                return OBJECT_MAPPER.readValue((String) value, new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                throw new DataException(String.format("Failed to process record from topic %s", record.topic()), e);
            }
        } else if (value instanceof Struct) {
            return structToJson(record.topic(), record.valueSchema(), value);
        } else if (value instanceof Map) {
            return (Map<String, Object>) value;
        } else {
            throw new DataException(String.format("Record from topic %s is not a supported Tecton type.", record.topic()));
        }
    }

    private void extractAndAddMetadata(SinkRecord record, Map<String, Object> recordData) {
        if (config.kafkaKeyEnabled && record.key() != null) {
            recordData.put("kafka_key", extractKeyData(record));
        }
        if (config.kafkaTimestampEnabled && record.timestamp() != null) {
            recordData.put("kafka_timestamp", formatTimestamp(record.timestamp()));
        }
        if (config.kafkaHeadersEnabled) {
            recordData.putAll(extractHeaders(record));
        }
    }

    private String formatTimestamp(long timestamp) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME
            .withZone(ZoneOffset.UTC)
            .format(Instant.ofEpochMilli(timestamp));
    }

    private Map<String, String> extractHeaders(SinkRecord record) {
        Map<String, String> headersMap = new HashMap<>();
        record.headers().forEach(header ->
            headersMap.put(header.key(), header.value() != null ? header.value().toString() : null)
        );
        return headersMap;
    }

    private Object extractKeyData(SinkRecord record) {
        if (record.key() instanceof Struct) {
            return structToJson(record.topic(), record.keySchema(), record.key());
        }
        return record.key().toString();
    }

    private Map<String, Object> structToJson(String topic, Schema schema, Object struct) {
        byte[] jsonBytes = jsonConverter.fromConnectData(topic, schema, struct);
        try {
            return OBJECT_MAPPER.readValue(new String(jsonBytes, StandardCharsets.UTF_8), new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new DataException(String.format("Failed to convert Struct to JSON for topic %s", topic), e);
        }
    }
}
