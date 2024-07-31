package com.tecton.ingestclient.processor;

import java.util.Collection;
import org.apache.kafka.connect.sink.SinkRecord;

/**
 * Defines the contract for record processors which handle the processing of SinkRecord collections.
 */
public interface IRecordProcessor {

  /**
   * Processes a collection of SinkRecords.
   *
   * @param records A collection of SinkRecords to be processed.
   */
  void processRecords(Collection<SinkRecord> records);

  /**
   * Performs any necessary cleanup actions before the processor is closed or disposed of.
   */
  void close();
}
