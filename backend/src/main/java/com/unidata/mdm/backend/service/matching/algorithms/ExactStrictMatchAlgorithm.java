package com.unidata.mdm.backend.service.matching.algorithms;

import static com.unidata.mdm.backend.common.search.FormField.strict;
import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createAndGroup;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.types.SimpleAttribute;

/**
 * Records will be matched only when Attribute values is equal.
 */
public class ExactStrictMatchAlgorithm extends AbstractStrictAlgorithm {
    /**
     * This algorithm/field id.
     */
    public static final Integer ALGORITHM_ID = AlgorithmType.EXACT_STRICT_MATCH.getId();
    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getAlgorithmId() {
        return ALGORITHM_ID;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public AlgorithmType getType() {
        return AlgorithmType.EXACT_STRICT_MATCH;
    }

    @Nullable
    @Override
    public FormFieldsGroup getFormFieldGroup(Map<Integer, SimpleAttribute<?>> attributeMap) {
        SimpleAttribute<?> simpleAttribute = attributeMap.get(ALGORITHM_ID);
        return createAndGroup(strict(simpleAttribute));
    }

    @Override
    public Collection<Integer> getFieldsForIdentify() {
        return Collections.singleton(ALGORITHM_ID);
    }
}
