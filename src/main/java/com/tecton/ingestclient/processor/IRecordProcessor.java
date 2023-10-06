package com.tecton.ingestclient.processor;

import java.util.Collection;
import org.apache.kafka.connect.sink.SinkRecord;

public interface IRecordProcessor {
    public void processRecords (Collection<SinkRecord> records);
    public void close();
}
