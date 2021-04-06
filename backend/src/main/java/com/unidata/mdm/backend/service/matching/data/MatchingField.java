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
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.backend.util.MessageUtils;

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
        return attr == null ? new StringSimpleAttributeImpl(attributeName) : attr;
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
