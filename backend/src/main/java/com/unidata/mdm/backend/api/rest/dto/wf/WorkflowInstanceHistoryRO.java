package com.unidata.mdm.backend.api.rest.dto.wf;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Denis Kostovarov
 */
public class WorkflowInstanceHistoryRO {
    private final WorkflowHistoryItemType itemType;

    private String id;

    private String name;

    private String filename;

    private String description;

    private String assignee;

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime startTime;

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime endTime;

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime claimTime;

    public enum WorkflowHistoryItemType {
        WORKFLOW, COMMENT, ATTACH, UNKNOWN;

        public static WorkflowHistoryItemType fromString(final String str) {
            for (final WorkflowHistoryItemType v : values()) {
                if (v.name().equalsIgnoreCase(str)) {
                    return v;
                }
            }
            return UNKNOWN;
        }

        public static Set<String> toSet() {
            final Set<String> res = new HashSet<>(values().length);
            for (final WorkflowHistoryItemType v : values()) {
                res.add(v.name());
            }
            return res;
        }
    }

    public WorkflowHistoryItemType getItemType() {
        return itemType;
    }

    public WorkflowInstanceHistoryRO (final WorkflowHistoryItemType type) {
        this.itemType = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public ZonedDateTime getClaimTime() {
        return claimTime;
    }

    public void setClaimTime(ZonedDateTime claimTime) {
        this.claimTime = claimTime;
    }
}
