package com.unidata.mdm.backend.service.matching.algorithms;

import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.ArrayValue;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.unidata.mdm.backend.common.search.FormField.empty;
import static com.unidata.mdm.backend.common.search.FormField.strict;

public class ExactExcludedValueMatchNothingAlgorithm extends AbstractStrictAlgorithm {
    /**
     * Field/algorithm ID.
     */
    public static final Integer ALGORITHM_ID = AlgorithmType.EXACT_EXCLUDED_VALUE_MATCH_NOTHING.getId();

    public static final Integer EXCLUDED_VALUES = 42;

    /**
     * {@inheritDoc}
     */
    @Override
    public AlgorithmType getType() {
        return AlgorithmType.EXACT_EXCLUDED_VALUE_MATCH_NOTHING;
    }

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
    public Object construct(Attribute attr, Object additional) {

        if(additional != null){
            final List<String> excludedValues = Arrays.asList(additional.toString().split(","));
            if (CollectionUtils.isNotEmpty(excludedValues)) {
                if (attr instanceof SimpleAttribute) {
                    Object val = ((SimpleAttribute) attr).getValue();
                    if (val != null && excludedValues.contains(val.toString())) {
                        return null;
                    }
                } else if (attr instanceof ArrayAttribute) {
                    List<ArrayValue> values = ((ArrayAttribute) attr).getValue();
                    if (values.stream().allMatch(arrayValue -> arrayValue.getValue() != null
                            && excludedValues.contains(arrayValue.getValue().toString()))) {
                        return null;
                    }
                }
            }
        }

        return super.construct(attr);
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
        return Collections.singletonList(ALGORITHM_ID);
    }

    @Override
    public Collection<Integer> getSupplementaryFields(Integer id) {
        if (id.equals(ALGORITHM_ID)) {
            return Collections.singletonList(EXCLUDED_VALUES);
        } else {
            return Collections.emptyList();
        }
    }
}
