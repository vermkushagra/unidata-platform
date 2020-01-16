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

package org.unidata.mdm.data.service.segments;

import static org.apache.commons.lang3.tuple.Pair.of;
import static org.unidata.mdm.meta.util.ModelUtils.getAttributePath;

import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.unidata.mdm.core.type.data.Attribute;
import org.unidata.mdm.core.type.data.ComplexAttribute;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.SimpleAttribute;
import org.unidata.mdm.core.type.data.impl.MeasuredSimpleAttributeImpl;
import org.unidata.mdm.core.type.measurement.MeasurementValue;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.model.AttributeModelElement.AttributeValueType;
import org.unidata.mdm.core.type.model.MeasuredModelElement;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.meta.service.MetaMeasurementService;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.exception.PlatformBusinessException;

/**
 * Measured attributes normalizer
 */
public interface MeasurementMetaSettingSupport {

    /**
     * Measurement service
     */
    MetaMeasurementService measurementService();
    /**
     * Meta model service
     */
    MetaModelService modelService();

    default void processDataRecord(DataRecord record, String entityName, String parentName) {
        processSimpleAttrs(record.getAllAttributes(), entityName, parentName);
        record.getAllAttributes()
              .stream()
              .filter(attr -> attr.getAttributeType() == Attribute.AttributeType.COMPLEX)
              .map(attr -> (ComplexAttribute) attr)
              .map(attr -> extractDataRecords(attr, parentName))
              .flatMap(Collection::stream)
              .forEach(pair -> processDataRecord(pair.getValue(), entityName, pair.getKey()));
    }

    default Collection<Pair<String, DataRecord>> extractDataRecords(ComplexAttribute attribute, String parentName) {
        String attrName = attribute.getName();
        String fullName = getAttributePath(parentName, attrName);
        return attribute.stream().map(rec -> of(fullName, rec)).collect(Collectors.toList());
    }

    default void processSimpleAttrs(Collection<Attribute> attributes, String entityName, String parentName) {
        attributes.stream()
                  .filter(attr -> attr.getAttributeType() == Attribute.AttributeType.SIMPLE)
                  .filter(attr -> isNumber(attr.narrow()))
                  .map(attr -> Pair.of((SimpleAttribute<?>) attr, modelService().<AttributeModelElement>getAttributeInfoByPath(entityName, getAttributePath(parentName, attr.getName()))))
                  .filter(pair -> pair.getValue() != null)
                  .filter(pair -> pair.getValue().getValueType() == AttributeValueType.MEASURED)
                  .forEach(pair -> normalize(pair.getKey(), pair.getValue()));
    }

    default boolean isNumber(SimpleAttribute<?> attr) {
        SimpleAttribute.DataType attrType = attr.getDataType();
        return attrType == SimpleAttribute.DataType.MEASURED || attrType == SimpleAttribute.DataType.NUMBER;
    }

    default void normalize(SimpleAttribute<?> attr, AttributeModelElement modelAttribute) {

        DataRecord record = attr.getRecord();
        MeasuredModelElement measureSettings = modelAttribute.getMeasured();
        if (measureSettings == null) {
            throw new PlatformBusinessException("Attribute settings not found!",
                    DataExceptionIds.EX_DATA_UPSERT_MEASUREMENT_VALUE_UNAVAILABLE, attr.getName(),
                    modelAttribute.getDisplayName());
        }

        MeasuredSimpleAttributeImpl measuredAttribute = getMeasuredAttrs(attr);
        MeasurementValue value = measurementService().getValueById(measureSettings.getValueId());
        if (value == null) {
            throw new PlatformBusinessException("Measurement value doesn't exist!",
                    DataExceptionIds.EX_MEASUREMENT_VALUE_DOESNT_EXIST,
                    measureSettings.getValueId());
        }

        if (measuredAttribute.isMeasurementMetaDataDefine()) {

            //check value and unit
            boolean isValueSame = measuredAttribute.getValueId().equals(value.getId());
            if (!isValueSame) {
                throw new PlatformBusinessException("Attribute value is not the same.", // What is this?
                        DataExceptionIds.EX_DATA_UPSERT_WRONG_MEASUREMENT_VALUES, measuredAttribute.getValueId());
            }

            boolean isUnitCorrect = value.present(measuredAttribute.getInitialUnitId());
            if (!isUnitCorrect) {
                throw new PlatformBusinessException("Measurement value metadata doesn't contain unit definition.",
                        DataExceptionIds.EX_DATA_UPSERT_MEASUREMENT_UNIT_UNAVAILABLE, attr.getName(),
                        measuredAttribute.getInitialUnitId());
            }
        } else {
            //set default
            measuredAttribute.withInitialUnitId(value.getBaseUnitId());
            measuredAttribute.withValueId(value.getId());
            if (measuredAttribute.getInitialValue() == null && measuredAttribute.getValue() != null) {
                try {
                    //Ability work with types like a long , int, float, or even correct string.
                    Object untypedValue  =  measuredAttribute.getValue();
                    String stringValue = untypedValue.toString();
                    Double doubleValue = Double.parseDouble(stringValue);
                    measuredAttribute.withInitialValue(doubleValue);
                    measuredAttribute.withValue(doubleValue);
                } catch (Exception e) {
                    throw new PlatformBusinessException("Incorrect measurement value type.",
                            DataExceptionIds.EX_DATA_UPSERT_ENRICH_MEASUREMENT_VALUE_IS_INCORRECT, attr.getName(),
                            measuredAttribute.getValue());
                }
            }
        }

        record.addAttribute(measuredAttribute);
    }

    default MeasuredSimpleAttributeImpl getMeasuredAttrs(SimpleAttribute<?> attr) {
        SimpleAttribute.DataType attrType = attr.getDataType();
        if (attrType == SimpleAttribute.DataType.NUMBER) {
            //from dq
            return new MeasuredSimpleAttributeImpl(attr.<SimpleAttribute<Double>>narrow());
        } else {
            return (MeasuredSimpleAttributeImpl) attr;
        }
    }
}
