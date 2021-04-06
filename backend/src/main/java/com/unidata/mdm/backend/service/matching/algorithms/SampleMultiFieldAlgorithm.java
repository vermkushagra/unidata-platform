package com.unidata.mdm.backend.service.matching.algorithms;

import static com.unidata.mdm.backend.common.search.FormField.strict;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute;

public class SampleMultiFieldAlgorithm extends AbstractAlgorithm {

    @Override
    public boolean isExact() {
        return true;
    }

    @Nullable
    @Override
    public FormFieldsGroup getFormFieldGroup(Map<Integer, SimpleAttribute<?>> attributeMap) {
        Map<Integer, String> fields = getMatchingFieldMap();
        Collection<FormField> formFields = new ArrayList<>(fields.size());
        for (Integer fieldId : fields.keySet()) {
            SimpleAttribute<?> simpleAttribute = attributeMap.get(fieldId);
            formFields.add(strict(simpleAttribute));
        }
        return FormFieldsGroup.createAndGroup(formFields);
    }

    @Override
    public Collection<Integer> getFieldsForIdentify() {
        return getMatchingFieldMap().keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object construct(Attribute attr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgorithmType getType() {
        return AlgorithmType.EXACT_STRICT_MATCH;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getAlgorithmId() {
        return 0;
    }
}
