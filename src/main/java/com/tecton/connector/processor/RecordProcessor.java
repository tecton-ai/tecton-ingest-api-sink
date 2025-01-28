package com.tecton.connector.processor;

import com.tecton.connector.error.ConnectorException;
import org.apache.kafka.connect.sink.SinkRecord;

import java.util.Collection;

/**
 * Defines the contract for processing collections of SinkRecords.
 */
public interface RecordProcessor {

    /**
     * Processes a collection of SinkRecords.
     *
     * @param records The collection of SinkRecords to process.
     * @throws ConnectorException If an error occurs during processing.
     */
    void processRecords(Collection<SinkRecord> records) throws ConnectorException;

    /**
     * Closes the processor and releases any resources held.
     */
    void close();
}
