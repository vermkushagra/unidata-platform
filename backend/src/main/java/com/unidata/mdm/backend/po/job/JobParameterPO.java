package com.unidata.mdm.backend.po.job;

import com.unidata.mdm.backend.po.AbstractPO;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Job value PO.
 * @author Denis Kostovarov
 */
public class JobParameterPO extends AbstractPO {
    public static final String TABLE_NAME = "job_parameter";
    public static final String FIELD_ID = "id";
    public static final String FIELD_JOB_ID = "job_id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_VAL_STRING = "val_string";
    public static final String FIELD_VAL_DATE = "val_date";
    public static final String FIELD_VAL_LONG = "val_long";
    public static final String FIELD_VAL_DOUBLE = "val_double";
    public static final String FIELD_VAL_BOOLEAN = "val_boolean";
    public static final String FIELD_VAL_TEXT = "val_text";

    private Long id;
    private Long jobId;
    private String name;
    private Object value;
    private JobParameterType parameterType;

    /**
     * Construct a new JobParameterDTO as String.
     */
    public JobParameterPO(String name, String value) {
        this.name = name;
        this.value = value;
        parameterType = JobParameterType.STRING;
    }

    /**
     * Construct a new JobParameterDTO as Long.
     *
     * @param value    Long value
     */
    public JobParameterPO(String name, Long value) {
        this.name = name;
        this.value = value;
        parameterType = JobParameterType.LONG;
    }

    /**
     * Construct a new JobParameter as Date.
     *
     * @param value    Date value
     */
    public JobParameterPO(String name, ZonedDateTime value) {
        this.name = name;
        this.value = value;
        parameterType = JobParameterType.DATE;
    }

    /**
     * Construct a new JobParameter as Double.
     *
     * @param value    Double value
     */
    public JobParameterPO(String name, Double value) {
        this.name = name;
        this.value = value;
        parameterType = JobParameterType.DOUBLE;
    }

    /**
     * Construct a new JobParameter as Boolean.
     *
     * @param value    Boolean value
     */
    public JobParameterPO(String name, Boolean value) {
        this.name = name;
        this.value = value;
        parameterType = JobParameterType.BOOLEAN;
    }

    /**
     * @return the value contained within this JobParameter.
     */
    public Object getValueObject() {
        return value;
    }

    public String getStringValue() {
        if (parameterType == JobParameterType.STRING) {
            return (String) value;
        }
        return null;
    }

    public ZonedDateTime getDateValue() {
        if (parameterType == JobParameterType.DATE) {
            return (ZonedDateTime) value;
        }
        return null;
    }

    public Long getLongValue() {
        if (parameterType == JobParameterType.LONG) {
            return (Long) value;
        }
        return null;
    }

    public Double getDoubleValue() {
        if (parameterType == JobParameterType.DOUBLE) {
            return (Double) value;
        }
        return null;
    }

    public Boolean getBooleanValue() {
        if (parameterType == JobParameterType.BOOLEAN) {
            return (Boolean) value;
        }
        return null;
    }

    /**
     * @return a ParameterType representing the type of this value.
     */
    public JobParameterType getType() {
        return parameterType;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JobParameterPO)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        JobParameterPO rhs = (JobParameterPO) obj;
        return value == null ? rhs.value == null && parameterType == rhs.parameterType : value.equals(rhs.value);
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + parameterType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return value == null ? null : (parameterType == JobParameterType.DATE ? ""
                + ((ZonedDateTime) value).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) : value.toString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    /**
     * Enumeration representing the type of a JobParameterDTO.
     */
    public enum JobParameterType {

        STRING(FIELD_VAL_STRING),
        DATE(FIELD_VAL_DATE),
        LONG(FIELD_VAL_LONG),
        DOUBLE(FIELD_VAL_DOUBLE),
        BOOLEAN(FIELD_VAL_BOOLEAN),
        TEXT(FIELD_VAL_TEXT);

        private final String fieldName;

        JobParameterType(final String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return fieldName;
        }
    }
}
