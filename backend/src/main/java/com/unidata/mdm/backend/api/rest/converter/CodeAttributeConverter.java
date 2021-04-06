package com.unidata.mdm.backend.api.rest.converter;

import com.unidata.mdm.backend.api.rest.dto.CodeDataType;
import com.unidata.mdm.backend.api.rest.dto.data.CodeAttributeRO;
import com.unidata.mdm.backend.api.rest.dto.data.extended.ExtendedCodeAttributeRO;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.extended.WinnerInformationCodeAttribute;
import com.unidata.mdm.backend.common.types.impl.IntegerCodeAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringCodeAttributeImpl;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Mikhail Mikhailov
 *
 */
public class CodeAttributeConverter {

    /**
     * Constructor.
     */
    private CodeAttributeConverter() {
        super();
    }

    public static CodeAttribute<?> from(CodeAttributeRO source) {

        if (Objects.isNull(source)) {
            return null;
        }

        CodeAttribute<?> result;
        com.unidata.mdm.backend.common.types.CodeAttribute.CodeDataType type
            = com.unidata.mdm.backend.common.types.CodeAttribute.CodeDataType.valueOf(source.getType().name());

        switch (type) {
        case INTEGER:
            result = new IntegerCodeAttributeImpl(source.getName(), source.getValue() == null ? null : (Long) source.getValue());
            break;
        case STRING:
            result = new StringCodeAttributeImpl(source.getName(), source.getValue() == null ? null : (String) source.getValue());
            break;
        default:
            return null;
        }

        return result;
    }

    public static List<CodeAttribute<?>> from(Collection<CodeAttributeRO> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<CodeAttribute<?>> result = new ArrayList<>(source.size());
        for (CodeAttributeRO val : source) {
            CodeAttribute<?> converted = from(val);
            if (Objects.isNull(converted)) {
                continue;
            }

            result.add(converted);
        }

        return result;
    }

    public static CodeAttributeRO to(CodeAttribute<?> source) {

        if (Objects.isNull(source)) {
            return null;
        }

        CodeAttributeRO result = new CodeAttributeRO();
        List<Object> supplementary = null;

        if (source.hasSupplementary()) {
            supplementary = new ArrayList<>(source.getSupplementary().size());
            supplementary.addAll(source.getSupplementary());
        }

        result.setName(source.getName());
        result.setType(CodeDataType.valueOf(source.getDataType().name()));
        result.setValue(source.getValue());
        result.setSupplementary(supplementary);

        return result;
    }

    public static List<CodeAttributeRO> to(Collection<CodeAttribute<?>> source) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<CodeAttributeRO> result = new ArrayList<>(source.size());
        for (CodeAttribute<?> sourceAttribute : source) {
            if(sourceAttribute != null){
                CodeAttributeRO targetAttribute = new CodeAttributeRO();
                populate(sourceAttribute, targetAttribute);
                result.add(targetAttribute);
            }
        }
        return result;
    }

    public static List<CodeAttributeRO> to(Collection<CodeAttribute<?>> source, EtalonRecord etalonRecord, OriginKey originKey) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<CodeAttributeRO> result = new ArrayList<>(source.size());
        for (CodeAttribute<?> sourceAttribute : source) {
            if(sourceAttribute != null){
                ExtendedCodeAttributeRO targetAttribute = new ExtendedCodeAttributeRO();
                populate(sourceAttribute, targetAttribute);

                CodeAttribute winnerAttribute = etalonRecord.getCodeAttribute(sourceAttribute.getName());
                targetAttribute.setWinner(winnerAttribute instanceof WinnerInformationCodeAttribute
                                && originKey.getExternalId().equals(((WinnerInformationCodeAttribute) winnerAttribute).getWinnerExternalId())
                                && originKey.getSourceSystem().equals(((WinnerInformationCodeAttribute) winnerAttribute).getWinnerSourceSystem()));


                result.add(targetAttribute);
            }
        }
        return result;
    }

    private static void populate(CodeAttribute<?> source, CodeAttributeRO target) {
        List<Object> supplementary = null;

        if (source.hasSupplementary()) {
            supplementary = new ArrayList<>(source.getSupplementary().size());
            supplementary.addAll(source.getSupplementary());
        }

        target.setName(source.getName());
        target.setType(CodeDataType.valueOf(source.getDataType().name()));
        target.setValue(source.getValue());
        target.setSupplementary(supplementary);
    }
}
