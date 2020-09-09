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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeArrayAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeSimpleAttrDTO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeArrayAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeSimpleAttrPO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.unidata.mdm.backend.util.JaxbUtils;

/**
 * The Class ClsfNodeAttrDTOToPOConverter.
 */
public final class ClsfNodeAttrDTOToPOConverter {
    private ClsfNodeAttrDTOToPOConverter() {
    }

    /** The sdf. */
    private static SimpleDateFormat SDF = new SimpleDateFormat(JaxbUtils.XSD_DATE_TIME_FORMAT);

    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the clsf node attr PO
     */
    private static <T extends ClsfNodeAttrPO> T convert(ClsfNodeAttrDTO source, Supplier<T> objectSupplier) {
        T target = objectSupplier.get();
        target.setAttrName(source.getAttrName());
        target.setCreatedAt(source.getCreatedAt());
        target.setCreatedBy(source.getCreatedBy());
        if (source.getDataType() != null) {
            target.setDataType(source.getDataType().name());
        }

        target.setLookupEntityType(source.getLookupEntityType());
        if (source.getLookupEntityCodeAttributeType() != null) {
            target.setLookupEntityCodeAttributeType(source.getLookupEntityCodeAttributeType().name());
        }
        target.setDescription(source.getDescription());
        target.setDisplayName(source.getDisplayName());
        target.setHidden(source.isHidden());
        target.setReadOnly(source.isReadOnly());
        target.setSearchable(source.isSearchable());
        target.setUnique(source.isUnique());
        target.setNullable(source.isNullable());
        target.setUpdatedAt(source.getUpdatedAt());
        target.setUpdatedBy(source.getUpdatedBy());
        target.setOrder(source.getOrder());
        target.setCustomProperties(ClsfCustomPropertyToPOConverter.convert(source.getCustomProperties()));
        return target;
    }

    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the clsf node attr PO
     */
    public static ClsfNodeSimpleAttrPO convert(ClsfNodeSimpleAttrDTO source) {
        if (source == null) {
            return null;
        }
        ClsfNodeSimpleAttrPO target = convert(source, ClsfNodeSimpleAttrPO::new);
        target.setEnumDataType(source.getEnumDataType());
        target.setDefaultValue(toStringValue(source, source.getDefaultValue()));
        return target;
    }

    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<ClsfNodeSimpleAttrPO> convertSimpleAttrs(List<ClsfNodeSimpleAttrDTO> source) {
        if (source == null) {
            return null;
        }
        List<ClsfNodeSimpleAttrPO> target = new ArrayList<>();
        for (ClsfNodeSimpleAttrDTO element : source) {
            target.add(convert(element));
        }
        return target;
    }

    /**
     * To string value.
     *
     * @param source the source
     * @return the string
     */
    private static String toStringValue(ClsfNodeAttrDTO source, Object value) {
        if (source == null || value == null || (source.getDataType() == null && source.getLookupEntityCodeAttributeType() == null)) {
            return null;
        }
        if (source.getLookupEntityCodeAttributeType() != null) {
            return String.valueOf(value);
        }
        switch (source.getDataType()) {
            case BLOB:
            case CLOB:
                return null;
            case BOOLEAN:
                return BooleanUtils.toString((Boolean) value, "true", "false", null);
            case DATE:
            case TIME:
            case TIMESTAMP:
                return SDF.format((Date) value);
            case INTEGER:
                return Long.toString((Long) value);
            case NUMBER:
                return Double.toString((Double) value);
            case STRING:
                return (String) value;
            default:
                break;
        }
        return null;
    }

    public static List<ClsfNodeArrayAttrPO> convertArrayAttrs(List<ClsfNodeArrayAttrDTO> nodeArrayAttrs) {
        if (CollectionUtils.isEmpty(nodeArrayAttrs)) {
            return Collections.emptyList();
        }
        return nodeArrayAttrs.stream()
                .map(ClsfNodeAttrDTOToPOConverter::convert)
                .collect(Collectors.toList());
    }

    private static ClsfNodeArrayAttrPO convert(final ClsfNodeArrayAttrDTO source) {
        if (source == null) {
            return null;
        }
        ClsfNodeArrayAttrPO target = convert(source, ClsfNodeArrayAttrPO::new);
        if (CollectionUtils.isNotEmpty(source.getValues())) {
            target.setValues(
                    source.getValues().stream()
                            .map(v -> toStringValue(source, v))
                            .collect(Collectors.toList())
            );
        }
        return target;
    }
}
