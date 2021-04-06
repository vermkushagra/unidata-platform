package com.unidata.mdm.backend.api.rest.converter;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.api.rest.dto.meta.ArrayAttributeDefinitionRO;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.ArrayValueType;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * @author Mikhail Mikhailov
 *
 */
public class ArrayAttributeDefConverter {

    /**
     * Constructor.
     */
    private ArrayAttributeDefConverter() {
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
    public static void copySimpleAttributeDataList(List<ArrayAttributeDefinitionRO> source,
            List<ArrayAttributeDef> target) {

        if (source == null) {
            return;
        }

        for (ArrayAttributeDefinitionRO sourceAttr : source) {
            ArrayAttributeDef targetAttr = JaxbUtils.getMetaObjectFactory().createArrayAttributeDef();
            copyArrayAttributeData(sourceAttr, targetAttr);
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
    private static void copyArrayAttributeData(ArrayAttributeDefinitionRO source, ArrayAttributeDef target) {

        SimpleAttributeDefConverter.copyAbstractAttributeData(source, target);

        target.setMask(source.getMask());

        target.setNullable(source.isNullable());
        target.setArrayValueType(
                source.getArrayDataType() == null
                ? null
                : ArrayValueType.fromValue(SimpleDataType.fromValue(source.getArrayDataType().value())));

        target.setLookupEntityType(source.getLookupEntityType());

        if(StringUtils.isNotEmpty(source.getLookupEntityType())
        && CollectionUtils.isNotEmpty(source.getLookupEntityDisplayAttributes())){
            target.withLookupEntityDisplayAttributes(source.getLookupEntityDisplayAttributes());
        }

        target.setOrder(BigInteger.valueOf(source.getOrder()));
        target.setSearchable(source.isSearchable());
        target.setSearchMorphologically(source.isSearchMorphologically());
        target.setExchangeSeparator(source.getExchangeSeparator());
        target.setUseAttributeNameForDisplay(source.isUseAttributeNameForDisplay());
    }
}
