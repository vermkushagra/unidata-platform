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

package com.unidata.mdm.backend.api.rest.converter;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.api.rest.dto.meta.AbstractAttributeDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.SimpleAttributeDefinition;
import com.unidata.mdm.meta.AbstractAttributeDef;
import com.unidata.mdm.meta.AttributeMeasurementSettingsDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleDataType;

public class SimpleAttributeDefConverter {

    /**
     * Constructor.
     */
    private SimpleAttributeDefConverter() {
        super();
    }

    /**
     * Copies simple attributes from list to list.
     *
     * @param source
     *            the source
     * @param target
     *            the target
     */
    public static void copySimpleAttributeDataList(List<SimpleAttributeDefinition> source,
                                                   List<SimpleAttributeDef> target) {
        if (source == null) {
            return;
        }

        for (SimpleAttributeDefinition sourceAttr : source) {
            SimpleAttributeDef targetAttr = new SimpleAttributeDef();
            copySimpleAttributeData(sourceAttr, targetAttr);
            target.add(targetAttr);
        }
    }

    /**
     * Copy simple attributes data from REST to internal.
     *
     * @param source
     *            REST source
     * @param target
     *            internal
     */
    private static void copySimpleAttributeData(SimpleAttributeDefinition source, SimpleAttributeDef target) {
        copyAbstractAttributeData(source, target);
        target.setMask(source.getMask());
        target.setEnumDataType(source.getEnumDataType());
        target.setLookupEntityType(source.getLookupEntityType());

        if (StringUtils.isNotEmpty(source.getLookupEntityType())) {
            if (CollectionUtils.isNotEmpty(source.getLookupEntityDisplayAttributes())) {
                target.withLookupEntityDisplayAttributes(source.getLookupEntityDisplayAttributes());
            }
            if (CollectionUtils.isNotEmpty(source.getLookupEntitySearchAttributes())) {
                target.withLookupEntitySearchAttributes(source.getLookupEntitySearchAttributes());
            }
        }

        target.setUseAttributeNameForDisplay(source.isUseAttributeNameForDisplay());
        target.setNullable(source.isNullable());
        target.setSimpleDataType(source.getSimpleDataType() == null ? null : SimpleDataType.fromValue(source
                .getSimpleDataType().value()));
        target.setUnique(source.isUnique());
        target.setOrder(BigInteger.valueOf(source.getOrder()));
        target.setSearchable(source.isSearchable());
        target.setSearchMorphologically(source.isSearchMorphologically());
        target.setDisplayable(source.isDisplayable());
        target.setMainDisplayable(source.isMainDisplayable());
        target.setLinkDataType(source.getLinkDataType());
        String valueId = source.getValueId();
        String unitId = source.getDefaultUnitId();
        if (target.getSimpleDataType() == SimpleDataType.NUMBER && !isBlank(valueId) && !isBlank(unitId)) {
            AttributeMeasurementSettingsDef measurement = new AttributeMeasurementSettingsDef();
            measurement.withValueId(valueId).withDefaultUnitId(unitId);
            target.withMeasureSettings(measurement);
            target.setSimpleDataType(SimpleDataType.MEASURED);
        }
    }

    /**
     * Copy abstract attribute data from REST to internal.
     *
     * @param source
     *            REST source
     * @param target
     *            internal
     */
    public static void copyAbstractAttributeData(AbstractAttributeDefinition source, AbstractAttributeDef target) {
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setDescription(source.getDescription());
        target.setHidden(source.isHidden());
        target.setReadOnly(source.isReadOnly());
        target.withCustomProperties(ToCustomPropertyDefConverter.convert(source.getCustomProperties()));
    }
}
