package com.unidata.mdm.backend.common.dto.job;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * @author Denis Kostovarov
 */
public class JobParameterDTO extends JobTemplateParameterDTO {
    private Object value;

    public JobParameterDTO(final String name, final String value) {
        this.setName(name);
        this.value = value;
        setType(JobParameterType.STRING);
    }

    public JobParameterDTO(final String name, final ZonedDateTime value) {
        this.setName(name);
        this.value = value;
        setType(JobParameterType.DATE);
    }

    public JobParameterDTO(final String name, final Long value) {
        this.setName(name);
        this.value = value;
        setType(JobParameterType.LONG);
    }

    public JobParameterDTO(final String name, final Double value) {
        this.setName(name);
        this.value = value;
        setType(JobParameterType.DOUBLE);
    }

    public JobParameterDTO(final String name, final Boolean value) {
        this.setName(name);
        this.value = value;
        setType(JobParameterType.BOOLEAN);
    }

    public JobParameterDTO(final Long id, final String name, final String value) {
        this.setId(id);
        this.setName(name);
        this.value = value;
        setType(JobParameterType.STRING);
    }

    public JobParameterDTO(final Long id, final String name, final ZonedDateTime value) {
        this.setId(id);
        this.setName(name);
        this.value = value;
        setType(JobParameterType.DATE);
    }

    public JobParameterDTO(final Long id, final String name, final Long value) {
        this.setId(id);
        this.setName(name);
        this.value = value;
        setType(JobParameterType.LONG);
    }

    public JobParameterDTO(final Long id, final String name, final Double value) {
        this.setId(id);
        this.setName(name);
        this.value = value;
        setType(JobParameterType.DOUBLE);
    }

    public JobParameterDTO(final Long id, final String name, final Boolean value) {
        this.setId(id);
        this.setName(name);
        this.value = value;
        setType(JobParameterType.BOOLEAN);
    }

    public String getStringValue() {
        if (getType() == JobParameterType.STRING) {
            return (String) value;
        }
        return null;
    }

    public ZonedDateTime getDateValue() {
        if (getType() == JobParameterType.DATE) {
            return (ZonedDateTime) value;
        }
        return null;
    }

    public Long getLongValue() {
        if (getType() == JobParameterType.LONG) {
            return (Long) value;
        }
        return null;
    }

    public Double getDoubleValue() {
        if (getType() == JobParameterType.DOUBLE) {
            return (Double) value;
        }
        return null;
    }

    public Boolean getBooleanValue() {
        if (getType() == JobParameterType.BOOLEAN) {
            return (Boolean) value;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobParameterDTO that = (JobParameterDTO) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
