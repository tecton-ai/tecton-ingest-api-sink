package com.tecton.ingestclient.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tecton.kafka.connect.TectonHttpSinkConnectorConfig;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.header.ConnectHeaders;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TectonRecordConverterTest {

  private TectonRecordConverter converter;
  private TectonHttpSinkConnectorConfig config;

  @BeforeEach
  void setUp() {
      config = mock(TectonHttpSinkConnectorConfig.class);
      converter = new TectonRecordConverter(config);
  }

  @Test
  void testConvertWithJsonString() {
      when(config.kafkaKeyEnabled).thenReturn(true);
      when(config.kafkaTimestampEnabled).thenReturn(true);
      when(config.kafkaHeadersEnabled).thenReturn(true);
      
      SinkRecord record = new SinkRecord("testTopic", 0, Schema.STRING_SCHEMA, "key", 
          Schema.STRING_SCHEMA, "{\"field\": \"value\"}", 12345);

      TectonRecord tectonRecord = converter.convert(record);

      Map<String, Object> expectedData = new HashMap<>();
      expectedData.put("field", "value");
      expectedData.put("kafka_key", "key");
      expectedData.put("kafka_timestamp", "1970-01-01T00:00:12.345Z");

      assertEquals(expectedData, tectonRecord.getRecordData());
      assertTrue(tectonRecord.isValid());
  }

  @Test
  void testConvertWithStruct() {
      Schema schema = SchemaBuilder.struct().field("field", Schema.STRING_SCHEMA).build();
      Struct struct = new Struct(schema).put("field", "value");

      SinkRecord record = new SinkRecord("testTopic", 0, Schema.STRING_SCHEMA, "key", 
          schema, struct, 12345);

      TectonRecord tectonRecord = converter.convert(record);

      Map<String, Object> expectedData = new HashMap<>();
      expectedData.put("field", "value");

      assertEquals(expectedData, tectonRecord.getRecordData());
      assertTrue(tectonRecord.isValid());
  }

  @Test
  void testConvertWithHeaders() {
      when(config.kafkaHeadersEnabled).thenReturn(true);

      ConnectHeaders headers = new ConnectHeaders();
      headers.addString("header1", "value1");
      headers.addString("header2", "value2");

      SinkRecord record = new SinkRecord("testTopic", 0, Schema.STRING_SCHEMA, "key", 
          Schema.STRING_SCHEMA, "{\"field\": \"value\"}", 12345, null, null, headers);

      TectonRecord tectonRecord = converter.convert(record);

      Map<String, Object> expectedData = new HashMap<>();
      expectedData.put("field", "value");
      expectedData.put("header1", "value1");
      expectedData.put("header2", "value2");

      assertEquals(expectedData, tectonRecord.getRecordData());
      assertTrue(tectonRecord.isValid());
  }

  @Test
  void testConvertWithInvalidData() {
      SinkRecord record = new SinkRecord("testTopic", 0, Schema.STRING_SCHEMA, "key", 
          Schema.INT32_SCHEMA, 12345, 12345);

      assertThrows(DataException.class, () -> converter.convert(record));
  }

  @Test
  void testRecordToJsonConversion() {
      when(config.kafkaKeyEnabled).thenReturn(true);
      SinkRecord record = new SinkRecord("testTopic", 0, Schema.STRING_SCHEMA, "key", 
          Schema.STRING_SCHEMA, "{\"field\": \"value\"}", 12345);

      TectonRecord tectonRecord = converter.convert(record);

      String expectedJson = "{\"record\":{\"field\":\"value\",\"kafka_key\":\"key\"}}";
      assertEquals(expectedJson, tectonRecord.toJson());
  }
}
