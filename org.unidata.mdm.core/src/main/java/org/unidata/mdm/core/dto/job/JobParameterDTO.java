package org.unidata.mdm.core.dto.job;

import java.time.ZonedDateTime;
import java.util.Objects;

import org.unidata.mdm.core.type.job.JobParameterType;

/**
 * @author Denis Kostovarov
 */
public class JobParameterDTO extends JobTemplateParameterDTO {
    private Object[] value;
    
    public JobParameterDTO(final String name) {
        this.setName(name);
        setType(JobParameterType.STRING);
    }
    public JobParameterDTO(final String name, final String... value) {
        this.setName(name);
        this.value = value;
        setType(JobParameterType.STRING);
    }

    public JobParameterDTO(final String name, final ZonedDateTime... value) {
        this.setName(name);
        this.value = value;
        setType(JobParameterType.DATE);
    }

    public JobParameterDTO(final String name, final Long... value) {
        this.setName(name);
        this.value = value;
        setType(JobParameterType.LONG);
    }

    public JobParameterDTO(final String name, final Double... value) {
        this.setName(name);
        this.value = value;
        setType(JobParameterType.DOUBLE);
    }

    public JobParameterDTO(final String name, final Boolean... value) {
        this.setName(name);
        this.value = value;
        setType(JobParameterType.BOOLEAN);
    }

    public JobParameterDTO(final Long id, final String name, final String... value) {
        this.setId(id);
        this.setName(name);
        this.value = value;
        setType(JobParameterType.STRING);
    }

    public JobParameterDTO(final Long id, final String name, final ZonedDateTime... value) {
        this.setId(id);
        this.setName(name);
        this.value = value;
        setType(JobParameterType.DATE);
    }

    public JobParameterDTO(final Long id, final String name, final Long... value) {
        this.setId(id);
        this.setName(name);
        this.value = value;
        setType(JobParameterType.LONG);
    }

    public JobParameterDTO(final Long id, final String name, final Double... value) {
        this.setId(id);
        this.setName(name);
        this.value = value;
        setType(JobParameterType.DOUBLE);
    }

    public JobParameterDTO(final Long id, final String name, final Boolean... value) {
        this.setId(id);
        this.setName(name);
        this.value = value;
        setType(JobParameterType.BOOLEAN);
    }

    public String getStringValue() {
        if (getType() == JobParameterType.STRING && value != null && value.length > 0) {
            return (String) value[0];
        }
        return null;
    }

    public String[] getStringArrayValue() {
        if (getType() == JobParameterType.STRING && value != null && value.length > 0) {
            return (String[])value;
        }
        return null;
    }

    public ZonedDateTime getDateValue() {
        if (getType() == JobParameterType.DATE && value != null && value.length > 0) {
            return (ZonedDateTime) value[0];
        }
        return null;
    }

    public ZonedDateTime[] getDateArrayValue() {
        if (getType() == JobParameterType.DATE && value != null && value.length > 0) {
            return (ZonedDateTime[])value;
        }
        return null;
    }

    public Long getLongValue() {
        if (getType() == JobParameterType.LONG && value != null && value.length > 0) {
            return (Long) value[0];
        }
        return null;
    }

    public Long[] getLongArrayValue() {
        if (getType() == JobParameterType.LONG && value != null && value.length > 0) {
            return (Long[])value;
        }
        return null;
    }

    public Double getDoubleValue() {
        if (getType() == JobParameterType.DOUBLE && value != null && value.length > 0) {
            return (Double) value[0];
        }
        return null;
    }

    public Double[] getDoubleArrayValue() {
        if (getType() == JobParameterType.DOUBLE && value != null && value.length > 0) {
            return (Double[]) value;
        }
        return null;
    }

    public Boolean getBooleanValue() {
        if (getType() == JobParameterType.BOOLEAN && value != null && value.length > 0) {
            return (Boolean) value[0];
        }
        return null;
    }

    public Boolean[] getBooleanArrayValue() {
        if (getType() == JobParameterType.BOOLEAN && value != null && value.length > 0) {
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

    public boolean isMultiValue() {
        return getValueSize() > 1;
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
