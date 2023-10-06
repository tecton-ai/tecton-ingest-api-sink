package com.tecton.ingestclient.converter;

import org.junit.jupiter.api.Test;
import org.apache.kafka.connect.errors.DataException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TectonRecordTest {

    @Test
    void testRecordIsValid() {
        Map<String, Object> validData = new HashMap<>();
        validData.put("key1", "value1");
        validData.put("key2", 42);
        validData.put("key3", true);

        TectonRecord validRecord = new TectonRecord(validData);
        assertTrue(validRecord.isValid());
    }

    @Test
    void testRecordIsInvalid() {
        Map<String, Object> invalidData = new HashMap<>();
        invalidData.put("key1", new Object());

        TectonRecord invalidRecord = new TectonRecord(invalidData);
        assertFalse(invalidRecord.isValid());
    }

    @Test
    void testToJson() {
        Map<String, Object> validData = new HashMap<>();
        validData.put("key1", "value1");
        TectonRecord record = new TectonRecord(validData);
        assertNotNull(record.toJson());
    }
}
