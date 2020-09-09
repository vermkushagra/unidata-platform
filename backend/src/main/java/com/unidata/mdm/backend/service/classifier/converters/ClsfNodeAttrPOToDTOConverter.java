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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.api.rest.dto.CodeDataType;
import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeAttrRO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeArrayAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeSimpleAttrDTO;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeArrayAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeSimpleAttrPO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.util.JaxbUtils;


/**
 * The Class ClsfNodeAttrPOToDTOConverter.
 */
public class ClsfNodeAttrPOToDTOConverter {

    /** The Constant SDF. */
    private static final SimpleDateFormat SDF = new SimpleDateFormat(JaxbUtils.XSD_DATE_TIME_FORMAT);

    /**
     * Instantiates a new clsf node attr PO to DTO converter.
     */
    private ClsfNodeAttrPOToDTOConverter() {
        super();
    }

    public static <P extends ClsfNodeAttrPO> List<ClsfNodeAttrDTO> convertNodeAttrs(final List<P> attrs) {
        return attrs.stream().
                map(ClsfNodeAttrPOToDTOConverter::convertNodeAttr)
                .collect(Collectors.toList());
    }

    public static <P extends ClsfNodeAttrPO> ClsfNodeAttrDTO convertNodeAttr(P attr) {
        return attr instanceof ClsfNodeSimpleAttrPO ?
                convert((ClsfNodeSimpleAttrPO) attr, 0) :
                convert((ClsfNodeArrayAttrPO) attr, 0);
    }

    private static <T extends ClsfNodeAttrDTO> T convertNodeAttr(ClsfNodeAttrPO source, int nodeId, Supplier<T> objectSupplier) {
        final T target = objectSupplier.get();
        target.setAttrName(source.getAttrName());
        target.setCreatedAt(source.getCreatedAt());
        target.setCreatedBy(source.getCreatedBy());
        if (source.getDataType() != null) {
            target.setDataType(DataType.valueOf(source.getDataType()));
        }
        target.setLookupEntityType(source.getLookupEntityType());
        if (StringUtils.isNoneBlank(source.getLookupEntityCodeAttributeType())) {
            target.setLookupEntityCodeAttributeType(CodeAttribute.CodeDataType.valueOf(source.getLookupEntityCodeAttributeType()));
        }
        target.setDescription(source.getDescription());
        target.setDisplayName(source.getDisplayName());
        target.setHidden(source.isHidden());
        target.setNullable(source.isNullable());
        target.setReadOnly(source.isReadOnly());
        target.setSearchable(source.isSearchable());
        target.setUnique(source.isUnique());
        target.setUpdatedAt(source.getUpdatedAt());
        target.setUpdatedBy(source.getUpdatedBy());
        target.setInherited(nodeId!=source.getNodeId());
        target.setOrder(source.getOrder());
        target.setCustomProperties(ClsfCustomPropertyToPOConverter.convert(source.getCustomProperties()));
        return target;
    }

    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the clsf node attr DTO
     */
    public static ClsfNodeSimpleAttrDTO convert(ClsfNodeSimpleAttrPO source, int nodeId) {
        if (source == null) {
            return null;
        }

        final ClsfNodeSimpleAttrDTO target = convertNodeAttr(source, nodeId, ClsfNodeSimpleAttrDTO::new);

        target.setEnumDataType(source.getEnumDataType());
        target.setDefaultValue(value(source, target, source.getDefaultValue()));

        return target;
    }

    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the clsf node attr DTO
     */
    public static ClsfNodeArrayAttrDTO convert(ClsfNodeArrayAttrPO source, int nodeId) {
        if (source == null) {
            return null;
        }
        final ClsfNodeArrayAttrDTO target = convertNodeAttr(source, nodeId, ClsfNodeArrayAttrDTO::new);

        target.setValues(
                source.getValues().stream()
                        .map(v -> value(source, target, v))
                        .collect(Collectors.toList())
        );

        return target;
    }

    private static Object value(final ClsfNodeAttrPO source, final ClsfNodeAttrDTO target, final String value) {
        if (StringUtils.isBlank(source.getLookupEntityType())) {
            return fromStringValue(target.getDataType(), value);
        }
        else {
            return fromStringValue(target.getLookupEntityCodeAttributeType(), value);
        }
    }

    /**
     * Convert.
     *
     * @param source
     *            the source
     * @param nodeId
     * @return the list
     */
    public static List<ClsfNodeSimpleAttrDTO> convertSimpleAttrs(List<ClsfNodeSimpleAttrPO> source, int nodeId) {
        if (source == null) {
            return null;
        }
        List<ClsfNodeSimpleAttrDTO> target = new ArrayList<>();
        for (ClsfNodeSimpleAttrPO element : source) {
            target.add(convert(element, nodeId));
        }
        return target;
    }

    public static List<ClsfNodeArrayAttrDTO> convertArrayAttrs(final List<ClsfNodeArrayAttrPO> source, final int nodeId) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }
        return source.stream()
                .map(attr -> convert(attr, nodeId))
                .collect(Collectors.toList());
    }

    /**
     * From string value.
     *
     * @param dataType the data type
     * @param value the value
     * @return the object
     */
    private static Object fromStringValue(DataType dataType, String value) {
        if (value == null || dataType == null) {
            return null;
        }
        Object target = null;
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


    private static Object fromStringValue(CodeAttribute.CodeDataType codeDataType, String value) {
        if (value == null || codeDataType == null) {
            return null;
        }
        switch (codeDataType) {
            case STRING:
                return value;
            case INTEGER:
                if (!StringUtils.isEmpty(value)) {
                    return Long.parseLong(value);
                }
        }
        return null;
    }

    public static List<ClsfNodeAttrDTO> convertNodeAttrs(final Collection<ClsfNodeAttrPO> attrs, int nodeId) {
        return attrs.stream()
                .map(attr -> {
                    if (attr instanceof ClsfNodeSimpleAttrPO) {
                        return ClsfNodeAttrPOToDTOConverter.convert((ClsfNodeSimpleAttrPO) attr, nodeId);
                    } else if (attr instanceof ClsfNodeArrayAttrPO) {
                        return ClsfNodeAttrPOToDTOConverter.convert((ClsfNodeArrayAttrPO) attr, nodeId);
                    }
                    throw new RuntimeException("Unknown attr class" + attr.getClass().getName());
                })
                .collect(Collectors.toList());
    }
}
