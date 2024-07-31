package com.tecton.ingestclient.converter;

import org.apache.kafka.connect.sink.SinkRecord;

/**
 * Defines a mechanism to convert Kafka's {@code SinkRecord} to {@code TectonRecord} for ingestion.
 *
 * <p>
 * Implementations of this interface should provide mechanisms to handle the transformation of Kafka
 * records to the format required by the Tecton system. This conversion can involve data mapping,
 * transformations, and other necessary processing steps to facilitate the ingestion of data into
 * Tecton.
 * </p>
 */
public interface IRecordConverter {

  /**
   * Converts a {@code SinkRecord} from Kafka into a {@code TectonRecord}.
   *
   * @param record The Kafka sink record to be converted.
   * @return A {@code TectonRecord} representing the converted data ready for ingestion to Tecton.
   */
  TectonRecord convert(SinkRecord record);

  /**
   * Closes the converter, releasing any resources that were acquired.
   *
   * <p>
   * This method should be called when the converter is no longer needed, allowing it to perform
   * cleanup operations such as closing connections, releasing file handles, etc.
   * </p>
   */
  void close();
}
