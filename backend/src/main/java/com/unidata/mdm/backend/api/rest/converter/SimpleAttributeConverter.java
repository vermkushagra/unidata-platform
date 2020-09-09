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

/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.data.LargeObjectRO;
import com.unidata.mdm.backend.api.rest.dto.data.SimpleAttributeRO;
import com.unidata.mdm.backend.api.rest.dto.data.extended.ExtendedSimpleAttributeRO;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.BinaryLargeValue;
import com.unidata.mdm.backend.common.types.CharacterLargeValue;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.extended.WinnerInformationSimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.BinaryLargeValueImpl;
import com.unidata.mdm.backend.common.types.impl.BlobSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.CharacterLargeValueImpl;
import com.unidata.mdm.backend.common.types.impl.ClobSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.DateSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.EnumSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.IntegerSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.MeasuredSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.NumberSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.TimeSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.TimestampSimpleAttributeImpl;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.meta.SimpleAttributeDef;

/**
 * @author Mikhail Mikhailov
 */
public class SimpleAttributeConverter {


    /**
     * Constructor.
     */
    private SimpleAttributeConverter() {
        super();
    }

    /**
     * Convert Simple data attribute to REST simple data attribute.
     *
     * @param source the source
     */
    public static SimpleAttribute<?> from(SimpleAttributeRO source) {

        if (source == null) {
            return null;
        }

        SimpleAttribute<?> target = null;
        if (source.getType() != null) {

            switch (source.getType()) {
                case BOOLEAN:
                    target = new BooleanSimpleAttributeImpl(source.getName(), (Boolean) source.getValue());
                    break;
                case DATE:
                    target = new DateSimpleAttributeImpl(source.getName(),
                            Objects.isNull(source.getValue())
                                    ? null
                                    : (LocalDate) source.getValue());
                    break;
                case TIME:
                    target = new TimeSimpleAttributeImpl(source.getName(),
                            Objects.isNull(source.getValue())
                                    ? null
                                    : (LocalTime) source.getValue());
                    break;
                case TIMESTAMP:
                    target = new TimestampSimpleAttributeImpl(source.getName(),
                            Objects.isNull(source.getValue())
                                    ? null
                                    : (LocalDateTime) source.getValue());
                    break;
                case INTEGER:
                    target = new IntegerSimpleAttributeImpl(source.getName(), (Long) source.getValue());
                    break;
                case NUMBER:
                    if (StringUtils.isBlank(source.getUnitId()) && StringUtils.isBlank(source.getValueId())) {
                        target = new NumberSimpleAttributeImpl(source.getName(), (Double) source.getValue());
                    } else {
                        target = new MeasuredSimpleAttributeImpl(source.getName(), (Double) source.getValue())
                                .withInitialUnitId(source.getUnitId())
                                .withValueId(source.getValueId());
                    }
                    break;
                case STRING:
                    target = new StringSimpleAttributeImpl(source.getName(), (String) source.getValue());
                    break;
                case BLOB:
                    // Bytes aren't transfered via normal save/get calls
                    LargeObjectRO blob = (LargeObjectRO) source.getValue();
                    target = new BlobSimpleAttributeImpl(source.getName(),
                            blob == null ? null : new BinaryLargeValueImpl()
                                    .withFileName(blob.getFileName())
                                    .withId(blob.getId())
                                    .withMimeType(blob.getMimeType())
                                    .withSize(blob.getSize()));
                    break;
                case CLOB:
                    // Content is not transfered via normal save/get calls
                    LargeObjectRO clob = (LargeObjectRO) source.getValue();
                    target = new ClobSimpleAttributeImpl(source.getName(),
                            clob == null ? null : new CharacterLargeValueImpl()
                                    .withFileName(clob.getFileName())
                                    .withId(clob.getId())
                                    .withMimeType(clob.getMimeType())
                                    .withSize(clob.getSize()));
                    break;
                default:
                    break;
            }
        }

        return target;
    }

    /**
     * Copy list of simple attributes.
     *
     * @param source source list
     * @return collection
     */
    public static Collection<Attribute> from(List<SimpleAttributeRO> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<Attribute> destination = new ArrayList<>(source.size());
        for (SimpleAttributeRO a : source) {
            destination.add(from(a));
        }

        return destination;
    }

    /**
     * Copy list of simple attributes.
     *
     * @param source source list
     * @param target target
     */
    public static void to(Collection<SimpleAttribute<?>> source, Collection<SimpleAttributeRO> target) {
        if (source == null || source.isEmpty()) {
            return;
        }

        for (SimpleAttribute<?> a : source) {
            SimpleAttributeRO attributeRO = to(a);
            if (attributeRO != null) {
                target.add(attributeRO);
            }
        }
    }

    /**
     * Copy list of simple attributes with additional information.
     *
     * @param source source list
     * @param target target list
     * @param originKey origin key
     * @param etalonRecord etalon record for source
     */
    public static void to(Collection<SimpleAttribute<?>> source, Collection<SimpleAttributeRO> target, EtalonRecord etalonRecord, OriginKey originKey) {
        if (CollectionUtils.isEmpty(source)) {
            return;
        }

        for (SimpleAttribute<?> sourceAttribute : source) {
            if (sourceAttribute != null) {
                ExtendedSimpleAttributeRO targetAttribute = new ExtendedSimpleAttributeRO();
                populate(sourceAttribute, targetAttribute);
                SimpleAttribute winnerAttribute = etalonRecord.getSimpleAttribute(sourceAttribute.getName());

                targetAttribute.setWinner(winnerAttribute instanceof WinnerInformationSimpleAttribute
                        && originKey.getExternalId().equals(((WinnerInformationSimpleAttribute) winnerAttribute).getWinnerExternalId())
                        && originKey.getSourceSystem().equals(((WinnerInformationSimpleAttribute) winnerAttribute).getWinnerSourceSystem()));

                target.add(targetAttribute);
            }

        }
    }

    /**
     * Convert simple attributes from one search hit to collection RO attributes
     *
     * @param hit source hit
     * @param prefix prefix for extract data from hit
     * @param attributesDef attributes definition
     * @return collection RO attributes
     */
    public static List<SimpleAttributeRO> to(SearchResultHitDTO hit, String prefix, List<SimpleAttributeDef> attributesDef) {
        List<SimpleAttributeRO> result = null;
        if (CollectionUtils.isNotEmpty(attributesDef)) {
            result = new ArrayList<>();
            for (SimpleAttributeDef attributeDef : attributesDef) {
                SearchResultHitFieldDTO hf = hit.getFieldValue(prefix + attributeDef.getName());
                if (hf != null && hf.isNonNullField()) {
                    SimpleAttributeRO simpleAttributeRO = new SimpleAttributeRO();
                    simpleAttributeRO.setName(attributeDef.getName());
                    simpleAttributeRO.setType(SimpleDataType.fromValue(ModelUtils.defineDataType(attributeDef).name()));
                    Object value = hf.getFirstValue();
                    if (value != null) {
                        switch (simpleAttributeRO.getType()) {
                            case DATE:
                                value = LocalDate.parse(value.toString());
                                break;
                            case TIMESTAMP:
                                value = LocalDateTime.parse(value.toString());
                                break;
                            case TIME:
                                value = LocalTime.parse(value.toString());
                                break;
                        }

                        simpleAttributeRO.setDisplayValue(hf.getFirstDisplayValue());
                        simpleAttributeRO.setTargetEtalonId(hf.getSystemId());
                    }
                    simpleAttributeRO.setValue(value);
                    result.add(simpleAttributeRO);
                }
            }
        }
        return result;
    }

    /**
     * Convert Simple data attribute to REST simple data attribute.
     *
     * @param source the source
     */
    public static SimpleAttributeRO to(SimpleAttribute<?> source) {
        if (source == null) {
            return null;
        }

        SimpleAttributeRO target = new SimpleAttributeRO();
        populate(source, target);
        return target;
    }

    protected static void populate(SimpleAttribute<?> source, SimpleAttributeRO target) {

        target.setName(source.getName());
        if (source.getDataType() != null) {
            switch (source.getDataType()) {
                case BOOLEAN:
                    target.setValue(source.getValue());
                    break;
                case DATE:
                    target.setValue(source.getValue() == null
                            ? null
                            : (LocalDate) source.castValue());
                    break;
                case TIME:
                    target.setValue(source.getValue() == null
                            ? null
                            : (LocalTime) source.castValue());
                    break;
                case TIMESTAMP:
                    target.setValue(source.getValue() == null
                            ? null
                            : (LocalDateTime) source.castValue());
                    break;
                case INTEGER:
                    target.setValue(source.getValue());
                    target.setDisplayValue(((IntegerSimpleAttributeImpl) source).getDisplayValue());
                    target.setTargetEtalonId(((IntegerSimpleAttributeImpl) source).getLinkEtalonId());
                    break;
                case NUMBER:
                    target.setValue(source.getValue());
                    break;
                case MEASURED:
                    MeasuredSimpleAttributeImpl measuredSimpleAttribute = (MeasuredSimpleAttributeImpl) source;
                    //FE(sencha) can't work with null ids
                    if (measuredSimpleAttribute.getValueId() == null) {
                        return;
                    }
                    target.setValue(measuredSimpleAttribute.getInitialValue());
                    target.setUnitId(measuredSimpleAttribute.getInitialUnitId());
                    target.setValueId(measuredSimpleAttribute.getValueId());
                    break;
                case STRING:
                    target.setValue(source.getValue());
                    target.setDisplayValue(((StringSimpleAttributeImpl) source).getDisplayValue());
                    target.setTargetEtalonId(((StringSimpleAttributeImpl) source).getLinkEtalonId());
                    break;
                case ENUM:
                    target.setValue(((EnumSimpleAttributeImpl) source).getValue());
                    target.setDisplayValue(((EnumSimpleAttributeImpl) source).getDisplayValue());
                    break;
                case BLOB:
                    BinaryLargeValue sourceBlob = source.castValue();
                    if (sourceBlob != null) {

                        LargeObjectRO targetBlob = new LargeObjectRO();
                        targetBlob.setId(sourceBlob.getId());
                        targetBlob.setFileName(sourceBlob.getFileName());
                        targetBlob.setMimeType(sourceBlob.getMimeType());
                        targetBlob.setSize(sourceBlob.getSize());

                        target.setValue(targetBlob);
                    }
                    break;
                case CLOB:
                    CharacterLargeValue sourceClob = source.castValue();
                    if (sourceClob != null) {

                        LargeObjectRO targetClob = new LargeObjectRO();
                        targetClob.setId(sourceClob.getId());
                        targetClob.setFileName(sourceClob.getFileName());
                        targetClob.setMimeType(sourceClob.getMimeType());
                        targetClob.setSize(sourceClob.getSize());

                        target.setValue(targetClob);
                    }
                    break;
                default:
                    break;
            }
            if (source.getDataType() == SimpleAttribute.DataType.MEASURED) {
                target.setType(SimpleDataType.NUMBER);
            } else if (source.getDataType() == SimpleAttribute.DataType.ENUM) {
                target.setType(SimpleDataType.STRING);
            } else {
                target.setType(SimpleDataType.fromValue(source.getDataType().name()));
            }
        }
    }
}
