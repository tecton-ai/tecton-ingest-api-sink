package com.tecton.ingestclient.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecton.ingestclient.converter.TectonRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the request payload for the Tecton API.
 */
public class TectonApiRequest {

  @JsonProperty("workspace_name")
  private final String workspaceName;

  @JsonProperty("dry_run")
  private final boolean dryRun;

  @JsonProperty("records")
  private final Map<String, List<TectonRecord>> records;

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /**
   * Private constructor to create a new API request.
   *
   * @param workspaceName the workspace name. Must not be null.
   * @param dryRun indicates if it's a dry run.
   * @param records the records associated with the request. Must not be null.
   */
  private TectonApiRequest(String workspaceName, boolean dryRun,
      Map<String, List<TectonRecord>> records) {
    this.workspaceName = Objects.requireNonNull(workspaceName, "Workspace name cannot be null.");
    this.dryRun = dryRun;
    this.records = Collections.unmodifiableMap(records);
  }

  /**
   * @return the workspace name associated with the request.
   */
  public String getWorkspaceName() {
    return workspaceName;
  }

  /**
   * @return whether the request is a dry run.
   */
  public boolean isDryRun() {
    return dryRun;
  }

  /**
   * @return the map of records associated with the request.
   */
  public Map<String, List<TectonRecord>> getRecords() {
    return records;
  }

  /**
   * Calculates and returns the total count of records.
   *
   * @return the total number of records associated with the request.
   */
  public int getRecordCount() {
    return records.values().stream().mapToInt(List::size).sum();
  }

  @Override
  public String toString() {
    return toJson(this);
  }

  private static String toJson(Object obj) {
    try {
      return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      return "Error converting to JSON: " + e.getMessage();
    }
  }

  /**
   * Represents a wrapper around the record data.
   */
  public static class RecordWrapper {
    private final String pushSource;
    private final TectonRecord record;

    /**
     * Constructs a new record wrapper.
     *
     * @param pushSource the source of the push. Must not be null.
     * @param record the record data. Must not be null.
     */
    public RecordWrapper(String pushSource, TectonRecord record) {
      this.pushSource = Objects.requireNonNull(pushSource, "Push source cannot be null.");
      this.record = Objects.requireNonNull(record, "Record cannot be null.");
    }

    /**
     * Returns the push source.
     *
     * @return the push source.
     */
    public String getPushSource() {
      return pushSource;
    }

    /**
     * Returns the record data.
     *
     * @return a map containing the record data.
     */
    public TectonRecord getRecordData() {
      return record;
    }

    @Override
    public String toString() {
      return toJson(this);
    }
  }

  /**
   * Builder class for {@link TectonApiRequest}.
   */
  public static class Builder {
    private String workspaceName;
    private boolean dryRun;
    private final Map<String, List<TectonRecord>> records = new HashMap<>();

    /**
     * Sets the workspace name.
     *
     * @param workspaceName the name of the workspace.
     * @return this builder.
     */
    public Builder workspaceName(String workspaceName) {
      if (workspaceName == null) {
        throw new NullPointerException("Workspace name cannot be null.");
      }
      this.workspaceName = workspaceName;
      return this;
    }

    /**
     * Sets the dry run flag.
     *
     * @param dryRun indicates if it's a dry run.
     * @return this builder.
     */
    public Builder dryRun(boolean dryRun) {
      this.dryRun = dryRun;
      return this;
    }

    /**
     * Adds a record to the request.
     *
     * @param recordWrapper the record to be added.
     * @return this builder.
     */
    public Builder addRecord(RecordWrapper recordWrapper) {
      records.computeIfAbsent(recordWrapper.getPushSource(), k -> new ArrayList<>())
          .add(recordWrapper.getRecordData());
      return this;
    }

    /**
     * Builds a new {@link TectonApiRequest} instance.
     *
     * @return a new instance of {@link TectonApiRequest}.
     */
    public TectonApiRequest build() {
      return new TectonApiRequest(workspaceName, dryRun, records);
    }

    /**
     * Indicates whether there are any records added to this builder.
     *
     * @return true if there are records, otherwise false.
     */
    public boolean hasRecords() {
      return !this.records.isEmpty();
    }

    /**
     * Computes and returns the total count of records added to this builder.
     *
     * @return the total number of records.
     */
    public int getRecordCount() {
      return records.values().stream().mapToInt(List::size).sum();
    }

    /**
     * Clears all the records from this builder.
     *
     * @return this builder.
     */
    public Builder clearRecords() {
      this.records.clear();
      return this;
    }
  }
}
