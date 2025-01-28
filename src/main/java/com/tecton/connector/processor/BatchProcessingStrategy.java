package com.tecton.connector.processor;

import org.apache.kafka.connect.sink.SinkRecord;

import java.util.List;

/**
 * Defines the strategy for processing batches of records.
 */
public interface BatchProcessingStrategy {

    /**
     * Processes a batch of SinkRecords.
     *
     * @param records The list of SinkRecords to process.
     */
    void process(List<SinkRecord> records);
}
