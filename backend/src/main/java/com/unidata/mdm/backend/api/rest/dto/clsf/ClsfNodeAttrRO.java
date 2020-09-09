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

package com.unidata.mdm.backend.api.rest.dto.clsf;

import com.unidata.mdm.backend.api.rest.dto.CodeDataType;
import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.meta.AbstractAttributeDefinition;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

public abstract class ClsfNodeAttrRO extends AbstractAttributeDefinition {

    /**
     * Date format without milliseconds. Frontend specific.
     */
    private static final FastDateFormat DEFAULT_TIMESTAMP_NO_MS = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");

    /** Can be null. */
    private boolean nullable = true;

    /** Should be unique. */
    private boolean unique = false;

    /**
     * Attribute is generally searchable.
     */
    private boolean searchable = false;

    /**
     * Lookup entity type
     */
    private String lookupEntityType;

    /**
     * Lookup entity data type
     */
    private CodeDataType lookupEntityCodeAttributeType;

    private int order;

    /**
     * Parses string representation of date according to date format from
     * {@see DEFAULT_TIMESTAMP_NO_MS}.
     *
     * @param dateAsString
     *            string representation of date.
     * @return parsed date.
     * @throws ParseException
     *             the parse exception
     */
    private static Date parse(String dateAsString) throws ParseException {
        return dateAsString != null ? DEFAULT_TIMESTAMP_NO_MS.parse(dateAsString) : null;
    }

    /**
     * Checks if is nullable.
     *
     * @return true, if is nullable
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * Sets the nullable.
     *
     * @param nullable
     *            the new nullable
     */
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    /**
     * Checks if is unique.
     *
     * @return true, if is unique
     */
    public boolean isUnique() {
        return unique;
    }

    /**
     * Sets the unique.
     *
     * @param unique
     *            the new unique
     */
    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    /**
     * Checks if is searchable.
     *
     * @return true, if is searchable
     */
    public boolean isSearchable() {
        return searchable;
    }

    /**
     * Sets the searchable.
     *
     * @param searchable
     *            the new searchable
     */
    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    public String getLookupEntityType() {
        return lookupEntityType;
    }

    public void setLookupEntityType(String lookupEntityType) {
        this.lookupEntityType = lookupEntityType;
    }

    public CodeDataType getLookupEntityCodeAttributeType() {
        return lookupEntityCodeAttributeType;
    }

    public void setLookupEntityCodeAttributeType(final CodeDataType lookupEntityCodeAttributeType) {
        this.lookupEntityCodeAttributeType = lookupEntityCodeAttributeType;
    }

    protected final Function<String, Object> booleanConverter = (value) -> {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception ignored) {
            throw new BusinessException("Wrong value!", ExceptionId.EX_CLASSIFIER_INVALID_ATTRIBUTE_VALUE, value,
                    name, SimpleDataType.BOOLEAN);
        }
    };

    protected Function<String, Object> dateConverter(final Object type) {
        return (value) -> {
            try {
                return parse(value);
            } catch (ParseException ignored) {
                throw new BusinessException("Wrong value!", ExceptionId.EX_CLASSIFIER_INVALID_ATTRIBUTE_VALUE, value,
                        name, type);
            }
        };
    }

    protected final Function<String, Object> numberConverter = (value) -> {
        try {
            return Double.parseDouble(value);
        } catch (Exception ignored) {
            throw new BusinessException("Wrong value!", ExceptionId.EX_CLASSIFIER_INVALID_ATTRIBUTE_VALUE, value,
                    name, SimpleDataType.NUMBER);
        }
    };

    protected final Function<String, Object> integerConverter = (value) -> {
        try {
            return Long.parseLong(value);
        } catch (Exception ignored) {
            throw new BusinessException("Wrong value!", ExceptionId.EX_CLASSIFIER_INVALID_ATTRIBUTE_VALUE, value,
                    name, SimpleDataType.INTEGER);
        }
    };

    protected Function<String, Object> stringConverter = (value) -> value;


    private Function<String, Object> typeConverter() {
        if (StringUtils.isNoneBlank(getLookupEntityType()) && getLookupEntityCodeAttributeType() != null) {
            switch (getLookupEntityCodeAttributeType()) {
                case STRING:
                    return stringConverter;
                case INTEGER:
                    return integerConverter;
                default:
                    throw new RuntimeException("Unsupported data type: " + getLookupEntityCodeAttributeType().name());
            }
        }
        return dataTypeConverter();
    }

    protected abstract Function<String,Object> dataTypeConverter();

    protected Function<String, Object> stringToObjectConverter() {
        return (value) -> Optional.ofNullable(value)
                .filter(StringUtils::isNotBlank)
                .map(typeConverter())
                .orElse(null);
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

}
