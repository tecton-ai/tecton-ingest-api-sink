package com.tecton.ingestclient.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.tecton.ingestclient.converter.TectonRecord;

class TectonApiRequestTest {

  private TectonApiRequest.Builder builder;

  @BeforeEach
  void setUp() {
    builder = new TectonApiRequest.Builder();
  }

  @Test
  void testNullWorkspaceNameThrows() {
    assertThrows(NullPointerException.class, () -> builder.workspaceName(null));
  }

  @Test
  void testDryRun() {
    builder.workspaceName("defaultWorkspace") // Set a default workspace name
        .dryRun(true);
    assertTrue(builder.build().isDryRun());

    builder.workspaceName("defaultWorkspace") // Set it again for the next test condition
        .dryRun(false);
    assertFalse(builder.build().isDryRun());
  }

  private TectonRecord createMockTectonRecord() {
    Map<String, Object> mockData = new HashMap<>();
    mockData.put("key1", "value1");
    mockData.put("key2", 42);
    return new TectonRecord(mockData);
  }

  @Test
  void testAddRecord() {
    TectonRecord record = createMockTectonRecord();
    builder.addRecord(new TectonApiRequest.RecordWrapper("pushSource1", record));
    assertEquals(1, builder.getRecordCount());
    assertTrue(builder.hasRecords());

    builder.addRecord(new TectonApiRequest.RecordWrapper("pushSource2", record));
    assertEquals(2, builder.getRecordCount());
  }

  @Test
  void testClearRecords() {
    TectonRecord record = createMockTectonRecord();
    builder.addRecord(new TectonApiRequest.RecordWrapper("pushSource1", record))
        .addRecord(new TectonApiRequest.RecordWrapper("pushSource2", record));

    builder.clearRecords();
    assertEquals(0, builder.getRecordCount());
    assertFalse(builder.hasRecords());
  }

  @Test
  void testBuild() {
    builder.workspaceName("sampleWorkspace").dryRun(true);
    TectonApiRequest request = builder.build();

    assertEquals("sampleWorkspace", request.getWorkspaceName());
    assertTrue(request.isDryRun());
  }

  @Test
  void testToStringContainsWorkspaceName() {
    builder.workspaceName("sampleWorkspace");
    TectonApiRequest request = builder.build();

    assertTrue(request.toString().contains("sampleWorkspace"));
  }

  @Test
  void testRecordWrapper() {
    TectonRecord record = createMockTectonRecord(); // Use our helper method
    TectonApiRequest.RecordWrapper wrapper =
        new TectonApiRequest.RecordWrapper("pushSource", record);

    assertEquals("pushSource", wrapper.getPushSource());
    assertEquals(record, wrapper.getRecordData());
  }

  @Test
  void testNullPushSourceThrows() {
    TectonRecord record = createMockTectonRecord(); // Use our helper method
    assertThrows(NullPointerException.class,
        () -> new TectonApiRequest.RecordWrapper(null, record));
  }

  @Test
  void testNullRecordThrows() {
    assertThrows(NullPointerException.class,
        () -> new TectonApiRequest.RecordWrapper("pushSource", null));
  }

  @Test
  void testRecordWrapperToStringContainsPushSource() {
    TectonRecord record = createMockTectonRecord(); // Use our helper method
    TectonApiRequest.RecordWrapper wrapper =
        new TectonApiRequest.RecordWrapper("pushSource", record);

    assertTrue(wrapper.toString().contains("pushSource"));
  }
}
