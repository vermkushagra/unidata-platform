/**
 * Date: 10.03.2016
 */

package com.unidata.mdm.backend.api.rest.dto.job;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.unidata.mdm.backend.common.dto.job.JobParameterType;

import java.time.ZonedDateTime;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
@JsonSerialize(using = JobParameterROSerializer.class)
@JsonDeserialize(using = JobParameterRODeserializer.class)
public class JobParameterRO {
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

    public ZonedDateTime getDateValue() {
        if (JobParameterType.DATE == type && value != null && value.length > 0) {
            return (ZonedDateTime) value[0];
        }
        return null;
    }

    public Long getLongValue() {
        if (JobParameterType.LONG == type && value != null && value.length > 0) {
            return (Long) value[0];
        }
        return null;
    }

    public Double getDoubleValue() {
        if (JobParameterType.DOUBLE == type && value != null && value.length > 0) {
            return (Double) value[0];
        }
        return null;
    }

    public Boolean getBooleanValue() {
        if (JobParameterType.BOOLEAN == type && value != null && value.length > 0) {
            return (Boolean) value[0];
        }
        return null;
    }

    public int getValueSze() {
        if (value != null) {
            return value.length;
        }

        return 0;
    }

    public Object[] getArrayValue() {
        return value;
    }
}
