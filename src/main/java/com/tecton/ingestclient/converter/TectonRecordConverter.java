package com.tecton.ingestclient.converter;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecton.kafka.connect.TectonHttpSinkConnectorConfig;

/**
 * Converts SinkRecords into TectonRecords with optional extraction of metadata.
 */
public class TectonRecordConverter implements IRecordConverter {

  private static final Logger LOG = LoggerFactory.getLogger(TectonRecordConverter.class);
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final String SCHEMAS_ENABLE = "schemas.enable";
  private static final String SCHEMAS_ENABLE_VALUE = "false";

  private final JsonConverter jsonConverter = new JsonConverter();
  private final TectonHttpSinkConnectorConfig config;

  /**
   * Creates a new TectonRecordConverter with the given configuration.
   *
   * @param config The configuration for the converter.
   */
  public TectonRecordConverter(TectonHttpSinkConnectorConfig config) {
    this.config = config;
    jsonConverter.configure(Collections.singletonMap(SCHEMAS_ENABLE, SCHEMAS_ENABLE_VALUE), false);
    LOG.debug("TectonRecordConverter initialised with configuration: {}", config);
  }

  /**
   * Converts a {@code SinkRecord} from Kafka into a {@code TectonRecord}.
   *
   * @param record The Kafka sink record to be converted.
   * @return A {@code TectonRecord} representing the converted data ready for ingestion to Tecton.
   */
  @Override
  public TectonRecord convert(SinkRecord record) {
    LOG.debug("Converting SinkRecord from topic {}", record.topic());
    Map<String, Object> recordData = extractRecordData(record);
    if (config.kafkaSanitiseKeysEnabled) {
      recordData = sanitiseKeys(recordData);
    }
    extractAndAddMetadata(record, recordData);
    return new TectonRecord(recordData);
  }

  /**
   * Closes the converter, releasing any resources that were acquired.
   */
  @Override
  public void close() {
    jsonConverter.close();
  }

  /**
   * Extracts the main record data from the given SinkRecord.
   *
   * @param record The SinkRecord to extract data from.
   * @return A map representing the extracted record data.
   * @throws DataException If the data is not of a supported type.
   */
  @SuppressWarnings("unchecked")
  private Map<String, Object> extractRecordData(SinkRecord record) {
    Object value = record.value();
    if (value instanceof String) {
      try {
        return OBJECT_MAPPER.readValue((String) value, new TypeReference<Map<String, Object>>() {});
      } catch (Exception e) {
        LOG.error("Error processing record from topic {}: {}", record.topic(), e.getMessage());
        throw new DataException(
            String.format("Failed to process record from topic %s", record.topic()), e);
      }
    } else if (value instanceof Struct) {
      return structToJson(record.topic(), record.valueSchema(), value);
    } else if (value instanceof Map) {
      return (Map<String, Object>) value;
    } else {
      LOG.error("Record from topic {} is not a supported Tecton type.", record.topic());
      throw new DataException(
          String.format("Record from topic %s is not a supported Tecton type.", record.topic()));
    }
  }

  /**
   * Sanitises the keys in a map by replacing all non-alphanumeric characters with underscores and
   * ensuring keys do not start with digits, which are prefixed with an underscore to ensure
   * compatibility with JSON key naming conventions required by Tecton.
   *
   * @param original The original map containing keys that may need sanitisation.
   * @return A new map with sanitised keys.
   */
  @SuppressWarnings("unchecked")
  private Map<String, Object> sanitiseKeys(Map<String, Object> original) {
    if (original == null) {
      LOG.debug("No data to sanitize, received a null map.");
      return Collections.emptyMap();
    }

    Map<String, Object> sanitised = new HashMap<>();
    original.forEach((key, value) -> {
      String cleanKey = key.replaceAll("[^a-zA-Z0-9_]", "_").replaceAll("^\\d+", "_$0");
      if (!key.equals(cleanKey)) {
        LOG.debug("Sanitized key '{}' to '{}'.", key, cleanKey);
      }

      if (value instanceof Map) {
        value = sanitiseKeys((Map<String, Object>) value);
      }

      sanitised.put(cleanKey, value);
    });

    return sanitised;
  }

  /**
   * Extracts and adds metadata (key, timestamp, headers) to the record data if enabled in the
   * configuration. Sets the keys to null if the metadata is not present.
   *
   * @param record The SinkRecord from which to extract metadata.
   * @param recordData The map to which the extracted metadata will be added.
   */
  private void extractAndAddMetadata(SinkRecord record, Map<String, Object> recordData) {
    LOG.debug("Extracting metadata for record from topic {}", record.topic());

    if (config.kafkaKeyEnabled) {
      recordData.put("kafka_key", record.key() != null ? extractKeyData(record) : null);
    }
    if (config.kafkaTimestampEnabled) {
      recordData.put("kafka_timestamp", record.timestamp() != null ? formatTimestamp(record.timestamp()) : null);
    }
    if (config.kafkaHeadersEnabled) {
      Map<String, String> headers = extractHeaders(record);
      recordData.put("kafka_headers", !headers.isEmpty() ? headers : null);
    }
  }

  /**
   * Formats the timestamp as an ISO 8601 string with UTC zone offset.
   *
   * @param timestamp The timestamp in milliseconds since epoch.
   * @return The formatted timestamp.
   */
  private String formatTimestamp(long timestamp) {
    return DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC)
        .format(Instant.ofEpochMilli(timestamp));
  }

  /**
   * Extracts headers from the given SinkRecord.
   *
   * @param record The SinkRecord from which to extract headers.
   * @return A map representing the extracted headers.
   */
  private Map<String, String> extractHeaders(SinkRecord record) {
    Map<String, String> headersMap = new HashMap<>();
    record.headers().forEach(header -> headersMap.put(header.key(),
        header.value() != null ? header.value().toString() : null));
    return headersMap;
  }

  /**
   * Extracts the key data from the given SinkRecord.
   *
   * @param record The SinkRecord from which to extract key data.
   * @return The extracted key data.
   */
  private Object extractKeyData(SinkRecord record) {
    if (record.key() instanceof Struct) {
      return structToJson(record.topic(), record.keySchema(), record.key());
    }
    return record.key().toString();
  }

  /**
   * Converts a Struct value to a JSON-based map representation.
   *
   * @param topic The topic associated with the struct.
   * @param schema The schema associated with the struct.
   * @param struct The struct to be converted.
   * @return A map representation of the struct.
   * @throws DataException If conversion fails.
   */
  private Map<String, Object> structToJson(String topic, Schema schema, Object struct) {
    byte[] jsonBytes = jsonConverter.fromConnectData(topic, schema, struct);
    try {
      return OBJECT_MAPPER.readValue(new String(jsonBytes, StandardCharsets.UTF_8),
          new TypeReference<Map<String, Object>>() {});
    } catch (JsonProcessingException e) {
      LOG.error("Failed to convert Struct to JSON for topic {}: {}", topic, e.getMessage());
      throw new DataException(String.format("Failed to convert Struct to JSON for topic %s", topic),
          e);
    }
  }
}
