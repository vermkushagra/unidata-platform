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

package org.unidata.mdm.data.service.impl;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.unidata.mdm.core.context.FetchLargeObjectRequestContext;
import org.unidata.mdm.core.service.LargeObjectsServiceComponent;
import org.unidata.mdm.core.type.data.ArrayAttribute;
import org.unidata.mdm.core.type.data.Attribute;
import org.unidata.mdm.core.type.data.CodeAttribute;
import org.unidata.mdm.core.type.data.ComplexAttribute;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.SimpleAttribute;
import org.unidata.mdm.core.type.data.impl.AbstractLargeValue;
import org.unidata.mdm.core.type.data.impl.MeasuredSimpleAttributeImpl;
import org.unidata.mdm.core.type.measurement.MeasurementValue;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.model.AttributeModelElement.AttributeValueType;
import org.unidata.mdm.core.type.model.AttributedModelElement;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.exception.DataProcessingException;
import org.unidata.mdm.meta.EnumerationDataType;
import org.unidata.mdm.meta.EnumerationValue;
import org.unidata.mdm.meta.service.MetaMeasurementService;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.util.ModelUtils;
import org.unidata.mdm.search.service.SearchService;
import org.unidata.mdm.system.exception.PlatformBusinessException;

/**
 * @author Mikhail Mikhailov on Nov 5, 2019
 */
public abstract class AbstractValidationServiceImpl {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordValidationServiceImpl.class);
    /**
     * MetaModel service.
     */
    @Autowired
    protected MetaModelService metaModelService;
    /**
     * Search service.
     */
    @Autowired
    protected SearchService searchService;
    /**
     * Measured service
     */
    @Autowired
    protected MetaMeasurementService measurementService;
    /**
     * LOB component.
     */
    @Autowired
    protected LargeObjectsServiceComponent lobComponent;

    /**
     * Constructor.
     */
    protected AbstractValidationServiceImpl() {
        super();
    }

    protected void checkDataRecord(DataRecord record, String id) {
        if (id == null) {
            final String message = "Invalid upsert request context. No entity name was supplied. Upsert rejected.";
            LOGGER.warn(message);
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_NO_ID);
        }

        AttributedModelElement attributesWrapper = metaModelService.getEntityModelElementById(id);
        if (attributesWrapper == null) {
            final String message = "Invalid upsert request context. Entity was not found by name. Upsert rejected.";
            LOGGER.warn(message, id);
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_ENTITY_NOT_FOUND_BY_NAME, id);
        }

        checkAttributes(record, attributesWrapper, StringUtils.EMPTY, 0);
    }

    /**
     * Checks attributes for validity
     *
     * @param record the record
     * @param attributesWrapper attributes wrapper
     * @param prefix the prefix
     * @param level current level
     * @throws DataProcessingException
     */
    protected void checkAttributes(final DataRecord record, final AttributedModelElement attributesWrapper, final String prefix, final int level) {
        final Map<String, AttributeModelElement> attrs = attributesWrapper.getAttributes();

        Collection<String> requiredInLevelAttrs = attrs.entrySet().stream()
                .filter(attr -> attr.getValue().getLevel() == level)
                .filter(attr -> attr.getValue().isOfPath(prefix))
                .filter(attr -> isRequiredAttr(attr.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (record != null) {
            for (Attribute attr : record.getAllAttributes()) {
                if (attr.getAttributeType() == Attribute.AttributeType.COMPLEX) {
                    continue;
                }

                String attrPath = ModelUtils.getAttributePath(level, prefix, attr.getName());
                AttributeModelElement infoHolder = attrs.get(attrPath);
                checkFlatAttribute(attr, infoHolder, attrPath);

                if (valueExists(attr)) {
                    requiredInLevelAttrs.remove(attrPath);
                }
            }
        }

        if (!requiredInLevelAttrs.isEmpty()) {
            final String message = "Some required attributes are not present. {}";
            LOGGER.warn(message, requiredInLevelAttrs);
            throw new PlatformBusinessException(message, DataExceptionIds.EX_DATA_UPSERT_REQUIRED_ATTRS_NOT_PRESENT,
                    requiredInLevelAttrs.stream()
                            .map(name -> attrs.get(name).getDisplayName())
                            .collect(Collectors.toList())
            );
        }

        Map<String, Integer> count = new HashMap<>();

        if (record != null) {
            for (ComplexAttribute attr : record.getComplexAttributes()) {
                String attrPath = ModelUtils.getAttributePath(level, prefix, attr.getName());
                for (DataRecord nested : attr) {
                    checkAttributes(
                            nested,
                            attributesWrapper,
                            attrPath,
                            level + 1
                    );
                }
                count.putIfAbsent(attrPath, 0);
                count.put(attrPath, count.get(attrPath) + attr.size());
            }
        }

        attrs.entrySet().stream()
                .filter(entity -> entity.getValue().getLevel() == level)
                .filter(entity -> entity.getValue().isOfPath(prefix))
                .filter(entity -> entity.getValue().isComplex())
                .forEach(entity -> checkCountOfComplexAttrs(count.get(entity.getKey()), entity.getValue()));
    }

    protected boolean valueExists(Attribute attr) {

        if (attr.getAttributeType() == Attribute.AttributeType.ARRAY) {
            return !((ArrayAttribute<?>) attr).isEmpty();
        } else if (attr.getAttributeType() == Attribute.AttributeType.CODE) {
            return ((CodeAttribute<?>) attr).getValue() != null;
        } else if (attr.getAttributeType() == Attribute.AttributeType.SIMPLE) {
            return ((SimpleAttribute<?>) attr).getValue() != null;
        }

        return false;
    }

    protected boolean isRequiredAttr(AttributeModelElement holder) {

        if (holder.isSimple() || holder.isCode() || holder.isArray()) {
            return !holder.isNullable();
        }

        return false;
    }

    /**
     * Check number of complex attributes.
     *
     * @param realCount real number of complex attributes in entity
     * @param complexAttribute - definition of complex attributes
     */
    protected void checkCountOfComplexAttrs(@Nullable Integer realCount, @Nonnull AttributeModelElement complexAttribute) {
        int count = realCount == null ? 0 : realCount;
        int minCount = complexAttribute.getComplex().getMinCount();
        int maxCount = complexAttribute.getComplex().getMaxCount();
        if ((count - minCount < 0) || (count - maxCount > 0)) {
            final String message = "Quantity of complex attributes '{}' should be in range {} - {} but current value is {}. Upsert rejected.";
            LOGGER.warn(message, complexAttribute.getName(), minCount, maxCount, count);
            throw new PlatformBusinessException(message, DataExceptionIds.EX_DATA_UPSERT_INCORRECT_QUANTITY_OF_COMPLEX_ATTRIBUTES_IN_RANGE,
                  complexAttribute.getDisplayName(), minCount, maxCount, count);
        }
    }

    /**
     * Check simple attribute.
     *
     * @param attr - value
     * @param attrDef -  attribute definition
     * @param attr path
     */
    @SuppressWarnings("unchecked")
    protected void checkFlatAttribute(Attribute attr, AttributeModelElement attrDef, String attrPath) {

        if (attrDef == null) {
            final String message = "Attribute '{}' supplied for upsert is missing in the model. Upsert rejected.";
            LOGGER.warn(message, attrPath);
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_MISSING_ATTRIBUTE, attrPath);
        }

        boolean wrongType =
                (attrDef.isSimple() && attr.getAttributeType() != Attribute.AttributeType.SIMPLE)
             || (attrDef.isCode() && attr.getAttributeType() != Attribute.AttributeType.CODE)
             || (attrDef.isArray() && attr.getAttributeType() != Attribute.AttributeType.ARRAY);

        if (wrongType) {
            final String message = "Attribute {} supplied for upsert is of the wrong type {} compared to the model (type {} is expected). Upsert rejected.";
            LOGGER.warn(message, attrPath, attr.getAttributeType().name(),
                    attrDef.isSimple()
                            ? Attribute.AttributeType.SIMPLE.name()
                            : attrDef.isCode() ? Attribute.AttributeType.CODE.name() : Attribute.AttributeType.ARRAY.name());

            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_WRONG_ATTRIBUTE_TYPE,
                    attrPath,
                    attr.getAttributeType().name(),
                    attrDef.isSimple()
                            ? Attribute.AttributeType.SIMPLE.name()
                            : attrDef.isCode() ? Attribute.AttributeType.CODE.name() : Attribute.AttributeType.ARRAY.name());
        }

        if (attrDef.isSimple()) {

            if (attrDef.isLinkTemplate()) {
                checkStringValueAndType((SimpleAttribute<?>) attr, attrPath);
            } else if (attrDef.isEnumValue()) {
                checkStringValueAndType((SimpleAttribute<?>) attr, attrPath);
                checkEnumAttr((SimpleAttribute<?>) attr, attrDef);
            } else if (attrDef.isLookupLink()) {
                checkValueForLookupTarget((SimpleAttribute<?>) attr, attrDef);
            } else {

                checkSimpleAttributeValue((SimpleAttribute<?>) attr, attrDef.getValueType(), attrPath);

                if (attrDef.isMeasured()) {
                    checkMeasuredAttr((MeasuredSimpleAttributeImpl) attr, attrDef);
                }

                if (attrDef.isBlob() || attrDef.isClob()) {
                    checkLargeObjectAttr((SimpleAttribute<AbstractLargeValue>) attr, attrDef);
                }
            }
        } else if (attrDef.isCode()) {
            checkCodeAttributeValue((CodeAttribute<?>) attr, attrDef.getValueType(), attrPath);
        } else if (attrDef.isArray()) {
            checkArrayAttributeValue((ArrayAttribute<?>) attr, attrDef, attrPath);
        }
    }

    /**
     * TODO remove this as soon specific types exist.
     *
     * @param attr
     * @param sDef
     */
    protected void checkValueForLookupTarget(SimpleAttribute<?> attr, AttributeModelElement sDef) {

        String targetName = sDef.getLookupLinkName();
        if (isBlank(targetName)) {
            return;
        }

        if ((sDef.getValueType() == AttributeValueType.INTEGER && attr.getDataType() != SimpleAttribute.DataType.INTEGER)
         || (sDef.getValueType() == AttributeValueType.STRING && attr.getDataType() != SimpleAttribute.DataType.STRING)) {
            final String message = "Wrong code attribute link value type {}, referencing {}. Upsert rejected.";
            LOGGER.warn(message, attr.getValue(), targetName);
            throw new DataProcessingException(message,
                    DataExceptionIds.EX_DATA_UPSERT_WRONG_SIMPLE_CODE_ATTRIBUTE_REFERENCE_VALUE,
                    attr.getDataType().name(), targetName);
        }
    }

    protected void checkStringValueAndType(SimpleAttribute<?> attr, String attrPath) {

        boolean notAStringAttrType = attr.getDataType() != null && attr.getDataType() != SimpleAttribute.DataType.STRING;
        if (notAStringAttrType) {
            final String message =
                    "Attribute '{}' supplied for upsert is either Enumeration or calculated Link in the model, "
                            + "and has to have type 'String' "
                            + "while type attribute '{}' is supplied for upsert. Upsert rejected.";
            LOGGER.warn(message, attrPath, attr.getDataType().name());
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_WRONG_SPECIAL_ATTRIBUTE_TYPE, attrPath,
                    attr.getDataType().name());
        }
    }

    protected void checkEnumAttr(SimpleAttribute<?> attr, AttributeModelElement sDef) {

        String value = attr.castValue();
        String enumId = sDef.getEnumName();
        if (value == null || StringUtils.isBlank(enumId)) {
            return;
        }

        EnumerationDataType enumeration = metaModelService.getEnumerationById(enumId);
        Collection<EnumerationValue> enumValues = enumeration == null ? emptyList() : enumeration.getEnumVal();
        boolean isEnumPresent =  enumValues.stream().anyMatch(val -> val.getName().equals(value));
        if (!isEnumPresent) {
            throw new PlatformBusinessException("Enum value doesn't present", DataExceptionIds.EX_DATA_UPSERT_ENUM_ATTRIBUTE_INCORRECT,
                    value, enumId);
        }
    }

    protected void checkMeasuredAttr(MeasuredSimpleAttributeImpl measAttr, AttributeModelElement el) {

        String valueAttrId = measAttr.getValueId();
        String unitId = measAttr.getInitialUnitId();

        if (valueAttrId == null && unitId == null) {
            return;
        }

        String valueId = el.getMeasured().getValueId();
        MeasurementValue measurementValue = measurementService.getValueById(valueId);
        if (measurementValue == null) {
            return;
        }

        if (valueAttrId != null && !valueAttrId.equals(valueId)) {
            throw new DataProcessingException("measured value is not present",
                    DataExceptionIds.EX_DATA_ATTRIBUTE_MEASURED_VALUE_NOT_PRESENT, measAttr.getName());
        }
        if (unitId != null && !measurementValue.present(unitId)) {
            throw new DataProcessingException("measured unit is not present",
                    DataExceptionIds.EX_DATA_ATTRIBUTE_MEASURED_UNIT_NOT_PRESENT, measAttr.getName());
        }
    }

    /**
     * @param attr - value
     * @param type - attr type
     * @param attrPath - path to value
     */
    protected void checkCodeAttributeValue(@Nonnull CodeAttribute<?> attr, @Nonnull AttributeValueType type, @Nonnull String attrPath) {

        CodeAttribute.CodeDataType expectedType = CodeAttribute.CodeDataType.valueOf(type.name());
        if (attr.getDataType() != expectedType) {
            final String message = "Code attribute {} supplied for upsert has type {} "
                    + "while type attribute {} is expected from the model. Upsert rejected.";
            LOGGER.warn(message, attrPath, attr.getDataType().name(), expectedType.name());
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_WRONG_CODE_ATTRIBUTE_VALUE_TYPE,
                    attrPath,
                    attr.getDataType().name(),
                    expectedType.name());
        }
    }

    /**
     * @param attr - value
     * @param type - attr type
     * @param attrPath - path to value
     */
    protected void checkSimpleAttributeValue(@Nonnull SimpleAttribute<?> attr, @Nonnull AttributeValueType type, @Nonnull String attrPath) {

        SimpleAttribute.DataType expectedType = SimpleAttribute.DataType.valueOf(type.name());
        if (attr.getDataType() != expectedType) {
            final String message = "Attribute {} supplied for upsert has type {} "
                    + "while type attribute {} is expected from the model. Upsert rejected.";
            LOGGER.warn(message, attrPath, attr.getDataType().name(), expectedType.name());
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_WRONG_SIMPLE_ATTRIBUTE_VALUE_TYPE,
                    attrPath,
                    attr.getDataType().name(),
                    expectedType.name());
        }
    }

    /**
     * Check expected value and type attribute.
     *
     * @param attr the attribute
     * @param def attribute definition
     * @param attrPath attribute path
     */
    protected void checkArrayAttributeValue(ArrayAttribute<?> attr, AttributeModelElement def, String attrPath) {

        ArrayAttribute.ArrayDataType expectedType = ArrayAttribute.ArrayDataType.valueOf(def.getValueType().name());


        if (attr.getDataType() != expectedType) {

            if (def.isLookupLink()) {
                final String message = "Wrong array code attribute link value type {}, referencing {}. Upsert rejected.";
                LOGGER.warn(message, attr.getValue(), def.getLookupLinkName());
                throw new DataProcessingException(message,
                        DataExceptionIds.EX_DATA_UPSERT_WRONG_ARRAY_CODE_ATTRIBUTE_REFERENCE_VALUE,
                        attr.getDataType().name(), def.getLookupLinkName());

            } else {
                final String message = "Array attribute {} supplied for upsert has type {} "
                        + "while type attribute {} is expected from the model. Upsert rejected.";
                LOGGER.warn(message, attrPath, attr.getDataType().name(), expectedType.name());
                throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_WRONG_ARRAY_ATTRIBUTE_VALUE_TYPE,
                        attrPath,
                        attr.getDataType().name(),
                        expectedType.name());
            }
        }
    }

    protected void checkLargeObjectAttr(SimpleAttribute<AbstractLargeValue> attr, AttributeModelElement sDef) {

        if (attr.getValue() == null) {
            return;
        }

        boolean isExist = lobComponent.checkExistLargeObject(new FetchLargeObjectRequestContext.FetchLargeObjectRequestContextBuilder()
                .recordKey(attr.getValue().getId())
                .binary(sDef.getValueType() == AttributeValueType.BLOB)
                .build());

        if (!isExist) {
            throw new DataProcessingException("Can't find large object for attribute",
                    DataExceptionIds.EX_DATA_UPSERT_LARGE_OBJECT_VALUE_UNAVAILABLE, attr.getName());
        }
    }

}
