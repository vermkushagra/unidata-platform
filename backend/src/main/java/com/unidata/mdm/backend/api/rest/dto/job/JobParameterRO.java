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

package com.unidata.mdm.backend.api.rest.dto.job;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.unidata.mdm.backend.common.job.JobParameterType;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
@JsonSerialize(using = JobParameterROSerializer.class)
@JsonDeserialize(using = JobParameterRODeserializer.class)
public class JobParameterRO {
    private boolean multiSelect;
    private Long id;
    private String name;
    private Object[] value;
    private JobParameterType type;

    public JobParameterRO(final String name, final String... value) {
        this.name = name;
        this.value = value;
        this.type = JobParameterType.STRING;
    }

    public JobParameterRO(final String name, final ZonedDateTime... value) {
        this.name = name;
        this.value = value;
        this.type = JobParameterType.DATE;
    }

    public JobParameterRO(final String name, final Long... value) {
        this.name = name;
        this.value = value;
        this.type = JobParameterType.LONG;
    }

    public JobParameterRO(final String name, final Double... value) {
        this.name = name;
        this.value = value;
        this.type = JobParameterType.DOUBLE;
    }

    public JobParameterRO(final String name, final Boolean... value) {
        this.name = name;
        this.value = value;
        this.type = JobParameterType.BOOLEAN;
    }

    public JobParameterRO(final String name, final JobParameterType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public JobParameterType getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStringValue() {
        if (JobParameterType.STRING == type && value != null && value.length > 0) {
            return (String) value[0];
        }
        return null;
    }

    public String[] getStringArrayValue() {
        if (JobParameterType.STRING == type && value != null && value.length > 0) {
            return (String[])value;
        }
        return null;
    }

    public ZonedDateTime getDateValue() {
        if (JobParameterType.DATE == type && value != null && value.length > 0) {
            return (ZonedDateTime) value[0];
        }
        return null;
    }

    public ZonedDateTime[] getDateArrayValue() {
        if (JobParameterType.DATE == type && value != null && value.length > 0) {
            return (ZonedDateTime[])value;
        }
        return null;
    }

    public Long getLongValue() {
        if (JobParameterType.LONG == type && value != null && value.length > 0) {
            return (Long) value[0];
        }
        return null;
    }

    public Long[] getLongArrayValue() {
        if (JobParameterType.LONG == type && value != null && value.length > 0) {
            return (Long[])value;
        }
        return null;
    }

    public Double getDoubleValue() {
        if (JobParameterType.DOUBLE == type && value != null && value.length > 0) {
            return (Double) value[0];
        }
        return null;
    }

    public Double[] getDoubleArrayValue() {
        if (JobParameterType.DOUBLE == type && value != null && value.length > 0) {
            return (Double[])value;
        }
        return null;
    }

    public Boolean getBooleanValue() {
        if (JobParameterType.BOOLEAN == type && value != null && value.length > 0) {
            return (Boolean) value[0];
        }
        return null;
    }

    public Boolean[] getBooleanArrayValue() {
        if (JobParameterType.BOOLEAN == type && value != null && value.length > 0) {
            return (Boolean[])value;
        }
        return null;
    }

    public int getValueSize() {
        if (value != null) {
            return value.length;
        }

        return 0;
    }

    public Object[] getArrayValue() {
        return value;
    }

    /**
     * @return the multiSelect
     */
    public boolean isMultiSelect() {
        return multiSelect;
    }

    /**
     * @param multiSelect the multiSelect to set
     */
    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }
}
