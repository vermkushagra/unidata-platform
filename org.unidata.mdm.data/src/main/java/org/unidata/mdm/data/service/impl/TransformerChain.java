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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.data.Attribute;
import org.unidata.mdm.core.type.data.Attribute.AttributeType;
import org.unidata.mdm.core.type.data.CodeAttribute;
import org.unidata.mdm.core.type.data.CodeAttribute.CodeDataType;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.SimpleAttribute;
import org.unidata.mdm.core.type.data.SimpleAttribute.DataType;
import org.unidata.mdm.core.type.data.impl.AbstractCodeAttribute;
import org.unidata.mdm.core.type.data.impl.AbstractSimpleAttribute;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.model.AttributeModelElement.AttributeValueType;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.transform.DataVersionTransformer;
import org.unidata.mdm.data.util.TransformUtils;
import org.unidata.mdm.meta.service.MetaModelService;

/**
 * TODO: Pretty old stuff. No one runs 4.4 anymore. Think about to remove this.
 * @author Mikhail Mikhailov
 *         The transormer's chain.
 */
@Component("recordTransformerChain")
public class TransformerChain implements InitializingBean {
    /**
     * MMSE.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Chain start.
     */
    private DataVersionTransformer chain;

    /**
     * Constructor.
     */
    private TransformerChain() {
        super();
    }
    /**
     * Gets the chain.
     *
     * @return chain
     */
    public DataVersionTransformer getTransformerChain() {
        return chain;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        // First and the only transformer so far
        DataVersionTransformer transformer44 = new DataVersion44Transformer();
        chain = transformer44;
    }

    /**
     * @author Mikhail Mikhailov
     *         Version 4.4. transformer.
     */
    private class DataVersion44Transformer extends DataVersionTransformer {
        /**
         * Constructor.
         */
        public DataVersion44Transformer() {
            super(4, 4);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void apply(OriginRecord record) {

            String entityName = record.getInfoSection().getOriginKey().getEntityName();
            Map<String, AttributeModelElement> attrs = metaModelService.getAttributesInfoMap(entityName);

            // 1. Fix code attributes for lookups,
            // which are simple attributes in pre-4.4 versions
            boolean isLookup = metaModelService.isLookupEntity(entityName);
            if (isLookup) {

                List<AttributeModelElement> codeAttrs = attrs.entrySet().stream()
                        .filter(entry -> entry.getValue().isCode())
                        .map(Entry::getValue)
                        .collect(Collectors.toList());

                // First level only
                for (AttributeModelElement attr : codeAttrs) {

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
            List<AttributeModelElement> codeAttrs = attrs.entrySet().stream()
                    .filter(entry -> entry.getValue().isLookupLink())
                    .map(Entry::getValue)
                    .collect(Collectors.toList());

            for (AttributeModelElement attrHolder : codeAttrs) {

                Collection<Attribute> oldAttrs = record.getAttributeRecursive(attrHolder.getPath());
                for (Attribute oldAttr : oldAttrs) {

                    if (oldAttr.getAttributeType() == AttributeType.SIMPLE) {

                        SimpleAttribute<?> oldSimpleAttr = oldAttr.narrow();
                        DataType currentType = oldSimpleAttr.getDataType();

                        // Links can only be strings or integers in 4.4
                        if (currentType == DataType.STRING
                         && attrHolder.isLookupLink()
                         && attrHolder.getValueType() == AttributeValueType.INTEGER) {

                            SimpleAttribute<?> newAttr
                                    = AbstractSimpleAttribute.of(
                                    DataType.INTEGER, attrHolder.getPath(), TransformUtils.toLong(oldSimpleAttr.getValue()));

                            DataRecord thisRecord = oldAttr.getRecord();
                            thisRecord.removeAttribute(oldAttr.getName());
                            thisRecord.addAttribute(newAttr);
                        }
                    }
                }
            }
        }
    }
}
