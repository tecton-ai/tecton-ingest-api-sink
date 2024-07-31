package com.tecton.ingestclient.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.tecton.ingestclient.util.JsonUtil;

class TectonApiErrorTest {

  private TectonApiError.RequestError requestError;
  private TectonApiError.RecordError recordError;

  @BeforeEach
  void setUp() {
    requestError = new TectonApiError.RequestError("Sample Error", "Type A");
    recordError = new TectonApiError.RecordError("View A", "Source X", "Type B", "Sample Error");
  }

  @Test
  void testRequestErrorFields() {
    assertEquals("Sample Error", requestError.getErrorMessage());
    assertEquals("Type A", requestError.getErrorType());
  }

  @Test
  void testRecordErrorFields() {
    assertEquals("Sample Error", recordError.getErrorMessage());
    assertEquals("Type B", recordError.getErrorType());
    assertEquals("View A", recordError.getFeatureViewName());
    assertEquals("Source X", recordError.getPushSourceName());
  }

  @Test
  void testTectonApiErrorFields() {
    TectonApiError tectonApiError =
        new TectonApiError(requestError, "Workspace 1", Collections.singletonList(recordError));

    assertEquals(requestError, tectonApiError.getRequestError());
    assertEquals("Workspace 1", tectonApiError.getWorkspaceName());
    assertEquals(1, tectonApiError.getRecordErrors().size());
    assertEquals(recordError, tectonApiError.getRecordErrors().get(0));
  }

  @Test
  void testRequestErrorToString() {
    String json = requestError.toString();
    assertTrue(json.contains("Sample Error"));
    assertTrue(json.contains("Type A"));

    // Verify JSON conversion using JsonUtil
    String expectedJson = JsonUtil.toJson(requestError);
    assertEquals(expectedJson, json);
  }

  @Test
  void testRecordErrorToString() {
    String json = recordError.toString();
    assertTrue(json.contains("Sample Error"));
    assertTrue(json.contains("Type B"));
    assertTrue(json.contains("View A"));
    assertTrue(json.contains("Source X"));

    // Verify JSON conversion using JsonUtil
    String expectedJson = JsonUtil.toJson(recordError);
    assertEquals(expectedJson, json);
  }

  @Test
  void testTectonApiErrorToString() {
    TectonApiError tectonApiError =
        new TectonApiError(requestError, "Workspace 1", Collections.singletonList(recordError));

    String json = tectonApiError.toString();
    assertTrue(json.contains("Workspace 1"));
    assertTrue(json.contains("Sample Error"));
    assertTrue(json.contains("Type A"));
    assertTrue(json.contains("Type B"));
    assertTrue(json.contains("View A"));
    assertTrue(json.contains("Source X"));

    // Verify JSON conversion using JsonUtil
    String expectedJson = JsonUtil.toJson(tectonApiError);
    assertEquals(expectedJson, json);
  }
}
