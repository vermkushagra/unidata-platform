package com.unidata.mdm.backend.service.matching.algorithms;

import static com.unidata.mdm.backend.common.search.FormField.empty;
import static com.unidata.mdm.backend.common.search.FormField.strict;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute;

public class ExactNullMatchEverythingAlgorithm extends AbstractAlgorithm {
    /**
     * Field/algorithm ID.
     */
    public static final Integer ALGORITHM_ID = AlgorithmType.EXACT_NULL_MATCH_EVERYTHING.getId();
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExact() {
        return true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public AlgorithmType getType() {
        return AlgorithmType.EXACT_NULL_MATCH_EVERYTHING;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getAlgorithmId() {
        return ALGORITHM_ID;
    }
    @Nullable
    @Override
    public FormFieldsGroup getFormFieldGroup(Map<Integer, SimpleAttribute<?>> attributeMap) {
        SimpleAttribute<?> simpleAttribute = attributeMap.get(ALGORITHM_ID);
        //algorithm return all records as a result.
        if (simpleAttribute == null || simpleAttribute.getValue() == null) {
            return null;
        }
        String attrPath = simpleAttribute.getName();
        return FormFieldsGroup.createOrGroup().addFormField(empty(attrPath)).addFormField(strict(simpleAttribute));
    }

    @Override
    public Collection<Integer> getFieldsForIdentify() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object construct(Attribute attr) {
        // TODO Auto-generated method stub
        return null;
    }
}
