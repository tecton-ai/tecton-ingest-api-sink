package com.tecton.connector.converter;

import com.tecton.connector.error.InvalidRecordException;
import com.tecton.connector.error.SerializationException;
import com.tecton.connector.model.TectonRecord;
import org.apache.kafka.connect.sink.SinkRecord;

/**
 * Defines a mechanism to convert Kafka SinkRecords into TectonRecords for ingestion.
 */
public interface RecordConverter {

    /**
     * Converts a Kafka SinkRecord into a TectonRecord suitable for ingestion into Tecton.
     *
     * @param record The Kafka SinkRecord to convert.
     * @return The converted TectonRecord.
     * @throws InvalidRecordException If the record is invalid or cannot be converted.
     * @throws SerializationException If there is an error during serialization.
     */
    TectonRecord convert(SinkRecord record) throws InvalidRecordException, SerializationException;
}
