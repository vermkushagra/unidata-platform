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

package com.unidata.mdm.backend.po.job;

import com.unidata.mdm.backend.po.AbstractPO;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

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

    public static final String FIELD_VAL_ARR_STRING = "val_arr_string";
    public static final String FIELD_VAL_ARR_DATE = "val_arr_date";
    public static final String FIELD_VAL_ARR_LONG = "val_arr_long";
    public static final String FIELD_VAL_ARR_DOUBLE = "val_arr_double";
    public static final String FIELD_VAL_ARR_BOOLEAN = "val_arr_boolean";

    private Long id;
    private Long jobId;
    private String name;
    private Object[] value;
    private JobParameterType parameterType;

    /**
     * Construct a new JobParameterDTO as String.
     */
    public JobParameterPO(String name, String value) {
        this.name = name;
        this.value = new String[]{value};
        parameterType = JobParameterType.STRING;
    }

    public JobParameterPO(String name, String[] value) {
        this.name = name;
        this.value = value;
        parameterType = JobParameterType.STRING_ARR;
    }

    /**
     * Construct a new JobParameterDTO as Long.
     *
     * @param value    Long value
     */
    public JobParameterPO(String name, Long value) {
        this.name = name;
        this.value = new Long[]{value};
        parameterType = JobParameterType.LONG;
    }

    public JobParameterPO(String name, Long[] value) {
        this.name = name;
        this.value = value;
        parameterType = JobParameterType.LONG_ARR;
    }

    /**
     * Construct a new JobParameter as Date.
     *
     * @param value    Date value
     */
    public JobParameterPO(String name, ZonedDateTime value) {
        this.name = name;
        this.value = new ZonedDateTime[]{value};
        parameterType = JobParameterType.DATE;
    }

    public JobParameterPO(String name, ZonedDateTime[] value) {
        this.name = name;
        this.value = value;
        parameterType = JobParameterType.DATE_ARR;
    }

    /**
     * Construct a new JobParameter as Double.
     *
     * @param value    Double value
     */
    public JobParameterPO(String name, Double value) {
        this.name = name;
        this.value = new Double[]{value};
        parameterType = JobParameterType.DOUBLE;
    }

    public JobParameterPO(String name, Double[] value) {
        this.name = name;
        this.value = value;
        parameterType = JobParameterType.DOUBLE_ARR;
    }

    /**
     * Construct a new JobParameter as Boolean.
     *
     * @param value    Boolean value
     */
    public JobParameterPO(String name, Boolean value) {
        this.name = name;
        this.value = new Boolean[]{value};
        parameterType = JobParameterType.BOOLEAN;
    }

    public JobParameterPO(String name, Boolean[] value) {
        this.name = name;
        this.value = value;
        parameterType = JobParameterType.BOOLEAN_ARR;
    }

    /**
     * @return the value contained within this JobParameter.
     */
    public Object getValueObject() {
        if (value != null && value.length == 1) {
            return value[0];
        }
        return value;
    }

    public String getStringValue() {
        if (parameterType == JobParameterType.STRING && value != null && value.length == 1) {
            return (String) value[0];
        }
        return null;
    }

    public String[] getStringArrayValue() {
        if (parameterType == JobParameterType.STRING_ARR) {
            return (String[]) value;
        }
        return null;
    }

    public ZonedDateTime getDateValue() {
        if (parameterType == JobParameterType.DATE && value != null && value.length == 1) {
            return (ZonedDateTime) value[0];
        }
        return null;
    }

    public ZonedDateTime[] getDateArrayValue() {
        if (parameterType == JobParameterType.DATE_ARR) {
            return (ZonedDateTime[]) value;
        }
        return null;
    }

    public Long getLongValue() {
        if (parameterType == JobParameterType.LONG && value != null && value.length == 1) {
            return (Long) value[0];
        }
        return null;
    }

    public Long[] getLongArrayValue() {
        if (parameterType == JobParameterType.LONG_ARR) {
            return (Long[]) value;
        }
        return null;
    }

    public Double getDoubleValue() {
        if (parameterType == JobParameterType.DOUBLE && value != null && value.length == 1) {
            return (Double) value[0];
        }
        return null;
    }

    public Double[] getDoubleArrayValue() {
        if (parameterType == JobParameterType.DOUBLE_ARR) {
            return (Double[]) value;
        }
        return null;
    }

    public Boolean getBooleanValue() {
        if (parameterType == JobParameterType.BOOLEAN && value != null && value.length == 1) {
            return (Boolean) value[0];
        }
        return null;
    }

    public Boolean[] getBooleanArrayValue() {
        if (parameterType == JobParameterType.BOOLEAN_ARR) {
            return (Boolean[]) value;
        }
        return null;
    }

    /**
     * @return a ParameterType representing the type of this value.
     */
    public JobParameterType getType() {
        return parameterType;
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JobParameterPO)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        JobParameterPO rhs = (JobParameterPO) obj;
        return value == null ? rhs.value == null && parameterType == rhs.parameterType :
                Arrays.equals(value, rhs.value);
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + parameterType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return value == null ? null :
                (parameterType == JobParameterType.DATE || parameterType == JobParameterType.DATE_ARR ?
                        Arrays.stream(value)
                                .map(date -> ("" + ((ZonedDateTime) date).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)))
                                .collect(Collectors.toList()).toString() :
                        Arrays.asList(value).toString());
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
        // Arrays.
        STRING_ARR(FIELD_VAL_ARR_STRING),
        DATE_ARR(FIELD_VAL_ARR_DATE),
        LONG_ARR(FIELD_VAL_ARR_LONG),
        DOUBLE_ARR(FIELD_VAL_ARR_DOUBLE),
        BOOLEAN_ARR(FIELD_VAL_ARR_BOOLEAN),

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