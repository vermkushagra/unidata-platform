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

package com.unidata.mdm.api.wsdl.v3;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.CodeAttribute.CodeDataType;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.common.types.impl.AbstractCodeAttribute;
import com.unidata.mdm.backend.common.types.impl.AbstractSimpleAttribute;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * @author Mikhail Mikhailov
 * SOAP transformer counterpart.
 */
public class DataVersion44Transformer {
    /**
     * MMS.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Constructor.
     */
    public DataVersion44Transformer() {
        super();
    }

    void apply(DataRecord record, String entityName) {

        Map<String, AttributeInfoHolder> attrs = metaModelService.getAttributesInfoMap(entityName);

        // 1. Fix code attributes for lookups,
        // which are simple attributes in pre-4.4 versions
        boolean isLookup = metaModelService.isLookupEntity(entityName);
        if (isLookup) {

            List<AttributeInfoHolder> codeAttrs = attrs.entrySet().stream()
                    .filter(entry -> entry.getValue().isCode())
                    .map(Entry::getValue)
                    .collect(Collectors.toList());

            // First level only
            for (AttributeInfoHolder attr : codeAttrs) {

                Attribute old = record.getAttribute(attr.getPath());
                if (Objects.nonNull(old) && old.getAttributeType() == AttributeType.SIMPLE) {

                    SimpleAttribute<?> oldAttr = old.narrow();
                    CodeAttribute<?> newAttr
                        = AbstractCodeAttribute.of(
                            CodeDataType.valueOf(oldAttr.getDataType().name()), attr.getPath(), oldAttr.getValue());

                    record.removeAttribute(attr.getPath());
                    record.addAttribute(newAttr);
                }
            }
        }

        // 2. Fix links to lookups for enitites
        // which have always type string regardless of the type
        // of the target code attribute in pre-4.4 versions
//        if (!isLookup) {
        //UPD: UN-5462

            List<AttributeInfoHolder> codeAttrs = attrs.entrySet().stream()
                    .filter(entry -> entry.getValue().isLookupLink())
                    .map(Entry::getValue)
                    .collect(Collectors.toList());

            for (AttributeInfoHolder attrHolder : codeAttrs) {

                Collection<Attribute> oldAttrs = record.getAttributeRecursive(attrHolder.getPath());
                for (Attribute oldAttr : oldAttrs) {

                    if (oldAttr.getAttributeType() == AttributeType.SIMPLE) {

                        SimpleAttribute<?> oldSimpleAttr = oldAttr.narrow();
                        SimpleAttributeDef currentAttrDef = attrHolder.narrow();
                        DataType currentType = oldSimpleAttr.getDataType();

                        // Links can only be strings or integers in 4.4
                        if (currentType == DataType.STRING
                         && currentAttrDef.getLookupEntityCodeAttributeType() == SimpleDataType.INTEGER) {

                            SimpleAttribute<?> newAttr
                                = AbstractSimpleAttribute.of(
                                    DataType.INTEGER, attrHolder.getPath(), toLong(oldSimpleAttr.getValue()));

                            DataRecord thisRecord = oldAttr.getRecord();
                            thisRecord.removeAttribute(oldAttr.getName());
                            thisRecord.addAttribute(newAttr);
                        }
//                    }
                }
            }
        }
    }

    /**
     * Object to long, if required.
     *
     * @param o
     *            object
     * @return the long
     */
    private Long toLong(Object o) {
        return o == null
                ? null
                : Number.class.isAssignableFrom(o.getClass())
                    ? ((Number) o).longValue()
                    : o instanceof Boolean
                        ? ((Boolean) o).booleanValue() ? 1L : 0L
                        : Long.valueOf(o.toString());
    }
}
