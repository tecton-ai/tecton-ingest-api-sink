package com.tecton.ingestclient.converter;

import org.apache.kafka.connect.sink.SinkRecord;

public interface IRecordConverter {
  TectonRecord convert(SinkRecord record);
  void close();
}
