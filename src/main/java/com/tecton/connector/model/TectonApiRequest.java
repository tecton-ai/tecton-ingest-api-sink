package com.tecton.connector.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tecton.connector.util.JsonUtil;

/**
 * Represents the request payload for the Tecton API, containing the workspace name, dry run flag,
 * and a map of records organized by their push sources.
 */
public class TectonApiRequest {

    private static final Logger LOG = LoggerFactory.getLogger(TectonApiRequest.class);

    @JsonProperty("workspace_name")
    private final String workspaceName;
    @JsonProperty("dry_run")
    private final boolean dryRun;
    @JsonProperty("records")
    private final Map<String, List<TectonRecord>> records;

    /**
     * Constructs a new TectonApiRequest.
     *
     * @param workspaceName the workspace name. Must not be null.
     * @param dryRun        indicates if it's a dry run.
     * @param records       the records associated with the request. Must not be null.
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
    @JsonIgnore
    public int getRecordCount() {
        return records.values().stream().mapToInt(List::size).sum();
    }

    @Override
    public String toString() {
        try {
            return JsonUtil.toJson(this);
        } catch (JsonProcessingException e) {
            LOG.error("Error serializing TectonApiRequest to JSON. workspaceName: {}, dryRun: {}, recordCount: {}",
                    workspaceName, dryRun, getRecordCount(), e);
            return String.format(
                    "{\"workspaceName\":\"%s\",\"dryRun\":%b,\"recordCount\":%d,\"error\":\"Error converting to JSON\"}",
                    workspaceName, dryRun, getRecordCount());
        }
    }

    /**
     * Builder class for {@link TectonApiRequest} that facilitates the construction of a request.
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
         * @param pushSource the pushSource for the record.
         * @param record     the record data.
         * @return this builder.
         */
        public Builder addRecord(String pushSource, TectonRecord record) {
            Objects.requireNonNull(pushSource, "Push source cannot be null.");
            Objects.requireNonNull(record, "Record content cannot be null.");

            records.computeIfAbsent(pushSource, k -> new java.util.ArrayList<>()).add(record);
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
