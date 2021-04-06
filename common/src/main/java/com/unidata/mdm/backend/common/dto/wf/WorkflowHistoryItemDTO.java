package com.unidata.mdm.backend.common.dto.wf;

import java.time.ZonedDateTime;

/**
 * @author Denis Kostovarov
 */
public class WorkflowHistoryItemDTO {
    public static final String ITEM_TYPE_WORKFLOW = "WORKFLOW";
    public static final String ITEM_TYPE_ATTACH = "ATTACH";
    public static final String ITEM_TYPE_COMMENT = "COMMENT";

    private final String itemType;
    private String id;
    private String name;
    private String filename;
    private String description;
    private String assignee;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private ZonedDateTime claimTime;

    public WorkflowHistoryItemDTO(final String type) {
        itemType = type;
    }

    public String getItemType() {
        return itemType;
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

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public ZonedDateTime getClaimTime() {
        return claimTime;
    }

    public void setClaimTime(ZonedDateTime claimTime) {
        this.claimTime = claimTime;
    }
}
