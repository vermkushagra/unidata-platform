package com.unidata.mdm.backend.service.matching.algorithms;

import static com.unidata.mdm.backend.common.search.FormField.fuzzy;
import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createAndGroup;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute;

/**
 * Records will be matched only when Attribute values is fuzzy equals.
 */
public class InexactFixedLengthAlgorithm extends AbstractAlgorithm {
    /**
     * The field id.
     */
    public static final Integer ALGORITHM_ID = AlgorithmType.INEXACT_NORMALIZED_LENGTH.getId();
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
        return AlgorithmType.INEXACT_NORMALIZED_LENGTH;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExact() {
        return false;
    }

    @Nullable
    @Override
    public FormFieldsGroup getFormFieldGroup(Map<Integer, SimpleAttribute<?>> attributeMap) {
        SimpleAttribute<?> simpleAttribute = attributeMap.get(ALGORITHM_ID);
        return createAndGroup(fuzzy(simpleAttribute));
    }

    @Override
    public Collection<Integer> getFieldsForIdentify() {
        return Collections.singleton(ALGORITHM_ID);
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
