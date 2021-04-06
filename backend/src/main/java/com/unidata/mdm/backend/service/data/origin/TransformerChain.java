package com.unidata.mdm.backend.service.data.origin;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.CodeAttribute.CodeDataType;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.common.types.impl.AbstractCodeAttribute;
import com.unidata.mdm.backend.common.types.impl.AbstractSimpleAttribute;
import com.unidata.mdm.backend.exchange.util.TransformUtils;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * @author Mikhail Mikhailov
 *         The transormer's chain.
 */
@Component("recordsTransformerChain")
public class TransformerChain implements InitializingBean {
    /**
     * MMSE.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
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
        void apply(OriginRecord record) {

            String entityName = record.getInfoSection().getOriginKey().getEntityName();
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
