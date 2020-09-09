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

package com.unidata.mdm.backend.service.matching.data;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.hash.PrimitiveSink;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.ValidationResult;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.SingleValueAttribute;
import com.unidata.mdm.backend.common.types.impl.IntegerSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;

/**
 * Matching field.
 */
public class MatchingField implements Comparable<MatchingField> {
    /**
     * Field id.
     */
    private Integer id;
    /**
     * The attribute name.
     */
    private String attributeName;
    /**
     * Description.
     */
    private String description;

    private boolean constantField;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAttrName() {
        return attributeName;
    }

    public void setAttrName(String name) {
        this.attributeName = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Nullable
    public SimpleAttribute<?> extractAttribute(@Nonnull DataRecord etalonRecord) {
        SimpleAttribute<?> attr = etalonRecord.getSimpleAttribute(attributeName);
        return attr == null ? toSimpleAttribute(etalonRecord.getCodeAttribute(attributeName)) : attr;
    }

    private SimpleAttribute<?> toSimpleAttribute(CodeAttribute<?> codeAttribute) {
        if (codeAttribute == null || codeAttribute.getValue() == null) {
            return null;
        }
        switch (codeAttribute.getDataType()) {
            case STRING:
                return new StringSimpleAttributeImpl(codeAttribute.getName(), (String) codeAttribute.getValue());
            case INTEGER:
                return new IntegerSimpleAttributeImpl(codeAttribute.getName(), (Long) codeAttribute.getValue());
        }
        return null;
    }

    public void hashField(@Nonnull DataRecord etalonRecord, PrimitiveSink into) {

        SimpleAttribute<?> simpleAttr = extractAttribute(etalonRecord);
        if (simpleAttr == null || simpleAttr.getValue() == null || (simpleAttr.getDataType() == SimpleAttribute.DataType.STRING && simpleAttr.getValue().toString().isEmpty())) {
            return;
        }

        switch (simpleAttr.getDataType()) {
            case INTEGER:
                into.putLong(simpleAttr.castValue());
                break;
            case STRING:
                into.putUnencodedChars(simpleAttr.castValue());
                break;
            case BOOLEAN:
                into.putBoolean(simpleAttr.castValue());
                break;
            case NUMBER:
                into.putDouble(simpleAttr.castValue());
                break;
            default:
                into.putInt(simpleAttr.getValue().hashCode());
        }
    }

    /**
     * Checks validity of this field.
     * @return collection of validation results
     */
    public Collection<ValidationResult> checkCompleteness() {

        Collection<ValidationResult> validationErrors = new ArrayList<>(2);
        if (isNull(getId())) {
            ValidationResult validation = new ValidationResult("Field can not be define without an attribute id",
                    ExceptionId.EX_MATCHING_FIELD_INCORRECT_ID_ABSENT.getCode());
            validationErrors.add(validation);
        }
        if (isBlank(getAttrName())) {
            ValidationResult validation = new ValidationResult("Field can not be define without an attribute name",
                    ExceptionId.EX_MATCHING_FIELD_INCORRECT_ATTR_NAME_ABSENT.getCode());
            validationErrors.add(validation);
        }

        return validationErrors;
    }

    @Override
    public int compareTo(@Nonnull MatchingField o) {
        return Integer.compare(getId(), o.getId());
    }

    public boolean isConstantField() {
        return constantField;
    }

    public void setConstantField(boolean constantField) {
        this.constantField = constantField;
    }
}
