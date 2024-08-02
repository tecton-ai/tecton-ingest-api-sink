package com.tecton.ingestclient.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tecton.ingestclient.converter.TectonRecord;
import com.tecton.ingestclient.util.JsonUtil;

import java.util.*;

/**
 * Represents the request payload for the Tecton API.
 */
public class TectonApiRequest {

  @JsonProperty("workspace_name")
  private final String workspaceName;
  @JsonProperty("dry_run")
  private final boolean dryRun;
  private final Map<String, List<TectonRecord>> records;

  /**
   * Constructor to create a new API request.
   *
   * @param workspaceName the workspace name. Must not be null.
   * @param dryRun indicates if it's a dry run.
   * @param records the records associated with the request. Must not be null.
   */
  @JsonCreator
  public TectonApiRequest(@JsonProperty("workspace_name") String workspaceName,
                          @JsonProperty("dry_run") boolean dryRun,
                          @JsonProperty("records") Map<String, List<TectonRecord>> records) {
    this.workspaceName = Objects.requireNonNull(workspaceName, "Workspace name cannot be null.");
    this.dryRun = dryRun;
    this.records = Collections.unmodifiableMap(new HashMap<>(Objects.requireNonNull(records, "Records cannot be null.")));
  }

  /**
   * Gets the workspace name associated with the request.
   *
   * @return the workspace name.
   */
  public String getWorkspaceName() {
    return workspaceName;
  }

  /**
   * Returns whether the request is a dry run.
   *
   * @return true if the request is a dry run, otherwise false.
   */
  public boolean isDryRun() {
    return dryRun;
  }

  /**
   * Gets the map of records associated with the request.
   *
   * @return the map of records.
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
    return JsonUtil.toJson(this);
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
     * Gets the push source.
     *
     * @return the push source.
     */
    public String getPushSource() {
      return pushSource;
    }

    /**
     * Gets the record data.
     *
     * @return the record data.
     */
    public TectonRecord getRecord() {
      return record;
    }

    @Override
    public String toString() {
      return JsonUtil.toJson(this);
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
      this.workspaceName = Objects.requireNonNull(workspaceName, "Workspace name cannot be null.");
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
      Objects.requireNonNull(recordWrapper, "Record wrapper cannot be null.");
      records.computeIfAbsent(recordWrapper.getPushSource(), k -> new ArrayList<>())
          .add(Objects.requireNonNull(recordWrapper.getRecord(), "Record cannot be null."));
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
      return !records.isEmpty();
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
      records.clear();
      return this;
    }
  }
}
