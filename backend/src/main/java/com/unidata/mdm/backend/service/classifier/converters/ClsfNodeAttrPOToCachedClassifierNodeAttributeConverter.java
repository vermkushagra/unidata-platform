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

package com.unidata.mdm.backend.service.classifier.converters;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.types.ArrayAttribute.ArrayDataType;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.service.classifier.cache.CachedClassifierNodeArrayAttribute;
import com.unidata.mdm.backend.service.classifier.cache.CachedClassifierNodeAttribute;
import com.unidata.mdm.backend.service.classifier.cache.CachedClassifierNodeSimpleAttribute;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeArrayAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeSimpleAttrPO;
import com.unidata.mdm.backend.util.JaxbUtils;

/**
 * The Class ClsfNodeAttrPOToDTOConverter.
 */
public class ClsfNodeAttrPOToCachedClassifierNodeAttributeConverter {

    /** The Constant SDF. */
    private static final SimpleDateFormat SDF = new SimpleDateFormat(JaxbUtils.XSD_DATE_TIME_FORMAT);

    /**
     * Instantiates a new clsf node attr PO to DTO converter.
     */
    private ClsfNodeAttrPOToCachedClassifierNodeAttributeConverter() {
        super();
    }
    /**
     * Does sets common part for all types of attributes.
     * @param target the target
     * @param source the source
     * @param nodeId the node id
     */
    private static void convert(CachedClassifierNodeAttribute target, ClsfNodeAttrPO source, int nodeId) {

        target.setName(source.getAttrName());
        target.setDisplayName(source.getDisplayName());
        target.setDescription(source.getDescription());
        target.setReadOnly(source.isReadOnly());
        target.setHidden(source.isHidden());
        target.setNullable(source.isNullable());
        target.setInherited(nodeId != source.getNodeId());
        target.setUnique(source.isUnique());
        target.setSearchable(source.isSearchable());
        target.setOrder(source.getOrder());
        target.setCustomProperties(ClsfCustomPropertyPOToCachedClassifierPropertyConverter.convert(source.getCustomProperties()));
    }
    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the clsf node attr DTO
     */
    public static CachedClassifierNodeAttribute convert(ClsfNodeArrayAttrPO source, int nodeId) {

        if (source == null) {
            return null;
        }

        final CachedClassifierNodeArrayAttribute target = new CachedClassifierNodeArrayAttribute();

        // Common
        convert(target, source, nodeId);

        // Array stuff
        if (StringUtils.isNotBlank(source.getLookupEntityType())
         && StringUtils.isNotBlank(source.getLookupEntityCodeAttributeType())) {
            target.setDataType(ArrayDataType.valueOf(source.getLookupEntityCodeAttributeType()));
            target.setLookupLink(true);
            target.setLookupName(source.getLookupEntityType());
        } else {
            target.setDataType(ArrayDataType.valueOf(source.getDataType()));
        }

        target.setValues(source.getValues().stream()
                .map(v -> value(target.getDataType(), v))
                .filter(Objects::nonNull)
                .toArray(size -> new Serializable[size]));

        return target;
    }

    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the clsf node attr DTO
     */
    public static CachedClassifierNodeAttribute convert(ClsfNodeSimpleAttrPO source, int nodeId) {

        if (source == null) {
            return null;
        }

        final CachedClassifierNodeSimpleAttribute target = new CachedClassifierNodeSimpleAttribute();

        // Common
        convert(target, source, nodeId);

        // Simple stuff
        if (StringUtils.isNotBlank(source.getLookupEntityType())
         && StringUtils.isNotBlank(source.getLookupEntityCodeAttributeType())) {
            target.setDataType(DataType.valueOf(source.getLookupEntityCodeAttributeType()));
            target.setLookupLink(true);
            target.setLookupName(source.getLookupEntityType());
        } else if (StringUtils.isNotBlank(source.getEnumDataType())) {
            target.setDataType(DataType.STRING);
            target.setEnumLink(true);
            target.setEnumName(source.getEnumDataType());
        } else {
            target.setDataType(DataType.valueOf(source.getDataType()));
        }

        target.setValue(value(target.getDataType(), source.getDefaultValue()));
        return target;
    }

    /**
     * Convert.
     *
     * @param source
     *            the source
     * @param nodeId
     * @return the list
     */
    public static Map<AttributeType, List<CachedClassifierNodeAttribute>> convert(List<ClsfNodeAttrPO> source, int nodeId) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyMap();
        }

        Map<AttributeType, List<CachedClassifierNodeAttribute>> target = new EnumMap<>(AttributeType.class);
        for (ClsfNodeAttrPO element : source) {

            if (Objects.isNull(element)) {
                continue;
            }

            if (element instanceof ClsfNodeSimpleAttrPO) {
                target.computeIfAbsent(AttributeType.SIMPLE, key -> new ArrayList<>(4))
                    .add(convert((ClsfNodeSimpleAttrPO) element, nodeId));
            } else if (element instanceof ClsfNodeArrayAttrPO) {
                target.computeIfAbsent(AttributeType.ARRAY, key -> new ArrayList<>(4))
                    .add(convert((ClsfNodeArrayAttrPO) element, nodeId));
            }
        }

        return target;
    }

    /**
     * From string value.
     *
     * @param type the data type
     * @param value the value
     * @return the object
     */
    private static Serializable value(ArrayDataType type, String value) {

        if (value == null || type == null) {
            return null;
        }

        Serializable target = null;
        switch (type) {
        case DATE:
        case TIME:
        case TIMESTAMP:
            // TODO refactor to use LDT.
            if (!StringUtils.isEmpty(value)) {
                try {
                    target = SDF.parse(value);
                } catch (ParseException e) {
                    throw new SystemRuntimeException(
                            "Incorrect date format. Supported date format is " + JaxbUtils.XSD_DATE_TIME_FORMAT,
                            ExceptionId.EX_DATA_CANNOT_PARSE_DATE);
                }
            }
            break;
        case INTEGER:
            if (!StringUtils.isEmpty(value)) {
                target = Long.parseLong(value);
            }
            break;
        case NUMBER:
            if (!StringUtils.isEmpty(value)) {
                target = Double.parseDouble(value);
            }
            break;
        case STRING:
            target = value;
            break;
        default:
            break;
        }

        return target;
    }
    /**
     * From string value.
     *
     * @param dataType the data type
     * @param value the value
     * @return the object
     */
    private static Serializable value(DataType dataType, String value) {

        if (value == null || dataType == null) {
            return null;
        }

        Serializable target = null;
        switch (dataType) {
        case BLOB:
        case CLOB:
            break;
        case BOOLEAN:
            target = BooleanUtils.toBooleanObject(value);
            break;
        case DATE:
        case TIME:
        case TIMESTAMP:
            // TODO refactor to use LDT.
            if (!StringUtils.isEmpty(value)) {
                try {
                    target = SDF.parse(value);
                } catch (ParseException e) {
                    throw new SystemRuntimeException(
                            "Incorrect date format. Supported date format is " + JaxbUtils.XSD_DATE_TIME_FORMAT,
                            ExceptionId.EX_DATA_CANNOT_PARSE_DATE);
                }
            }
            break;
        case INTEGER:
            if (!StringUtils.isEmpty(value)) {
                target = Long.parseLong(value);
            }
            break;
        case NUMBER:
            if (!StringUtils.isEmpty(value)) {
                target = Double.parseDouble(value);
            }
            break;
        case STRING:
            target = value;
            break;
        default:
            break;
        }

        return target;
    }
}
