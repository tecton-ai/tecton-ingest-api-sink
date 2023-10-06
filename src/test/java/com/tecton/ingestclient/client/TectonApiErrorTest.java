package com.tecton.ingestclient.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TectonApiErrorTest {

  private TectonApiError tectonApiError;
  private TectonApiError.RequestError requestError;
  private TectonApiError.RecordError recordError;

  @BeforeEach
  void setUp() {
    tectonApiError = new TectonApiError();
    requestError = new TectonApiError.RequestError();
    recordError = new TectonApiError.RecordError();
  }

  @Test
  void testRequestErrorFields() {
    requestError.setErrorMessage("Sample Error");
    requestError.setErrorType("Type A");

    assertEquals("Sample Error", requestError.getErrorMessage());
    assertEquals("Type A", requestError.getErrorType());
  }

  @Test
  void testRecordErrorFields() {
    recordError.setErrorMessage("Sample Error");
    recordError.setErrorType("Type B");
    recordError.setFeatureViewName("View A");
    recordError.setPushSourceName("Source X");

    assertEquals("Sample Error", recordError.getErrorMessage());
    assertEquals("Type B", recordError.getErrorType());
    assertEquals("View A", recordError.getFeatureViewName());
    assertEquals("Source X", recordError.getPushSourceName());
  }

  @Test
  void testTectonApiErrorFields() {
    tectonApiError.setRequestError(requestError);
    tectonApiError.setWorkspaceName("Workspace 1");
    tectonApiError.setRecordErrors(Collections.singletonList(recordError));

    assertEquals(requestError, tectonApiError.getRequestError());
    assertEquals("Workspace 1", tectonApiError.getWorkspaceName());
    assertEquals(1, tectonApiError.getRecordErrors().size());
    assertEquals(recordError, tectonApiError.getRecordErrors().get(0));
  }

  @Test
  void testRequestErrorToString() {
    requestError.setErrorMessage("Sample Error");
    requestError.setErrorType("Type A");

    String json = requestError.toString();
    assertTrue(json.contains("Sample Error"));
    assertTrue(json.contains("Type A"));
  }

  @Test
  void testRecordErrorToString() {
    recordError.setErrorMessage("Sample Error");
    recordError.setErrorType("Type B");
    recordError.setFeatureViewName("View A");
    recordError.setPushSourceName("Source X");

    String json = recordError.toString();
    assertTrue(json.contains("Sample Error"));
    assertTrue(json.contains("Type B"));
    assertTrue(json.contains("View A"));
    assertTrue(json.contains("Source X"));
  }

  @Test
  void testTectonApiErrorToString() {
    // Populating the requestError and recordError objects before they are added to tectonApiError
    requestError.setErrorMessage("Sample Error");
    requestError.setErrorType("Type A");
    recordError.setErrorMessage("Sample Error");
    recordError.setErrorType("Type B");
    recordError.setFeatureViewName("View A");
    recordError.setPushSourceName("Source X");

    tectonApiError.setRequestError(requestError);
    tectonApiError.setWorkspaceName("Workspace 1");
    tectonApiError.setRecordErrors(Collections.singletonList(recordError));

    String json = tectonApiError.toString();
    assertTrue(json.contains("Workspace 1"));
    assertTrue(json.contains("Sample Error"));
    assertTrue(json.contains("Type A"));
    assertTrue(json.contains("Type B"));
    assertTrue(json.contains("View A"));
    assertTrue(json.contains("Source X"));
  }
}
