/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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

    private String completedBy;

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime startTime;

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime endTime;

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime claimTime;

    public String getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

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
