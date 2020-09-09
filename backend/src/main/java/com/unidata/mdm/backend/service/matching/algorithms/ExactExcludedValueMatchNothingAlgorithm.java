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

package com.unidata.mdm.backend.service.matching.algorithms;

import static com.unidata.mdm.backend.common.search.FormField.empty;
import static com.unidata.mdm.backend.common.search.FormField.strict;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.ArrayValue;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute;

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
    public FormFieldsGroup getFormFieldGroup(Map<Integer, Pair<AttributeInfoHolder, SimpleAttribute<?>>> attributeMap) {
        return getFormFieldGroup(attributeMap, null);
    }

    @Nullable
    @Override
    public FormFieldsGroup getFormFieldGroup(
            Map<Integer, Pair<AttributeInfoHolder, SimpleAttribute<?>>> attributeMap,
            Map<Integer, Object> additionalMap) {

        Pair<AttributeInfoHolder, SimpleAttribute<?>> value = attributeMap.get(ALGORITHM_ID);
        if(MapUtils.isNotEmpty(additionalMap) && additionalMap.containsKey(ALGORITHM_ID)) {

            Object additional = additionalMap.get(ALGORITHM_ID);
            final List<String> excludedValues = additional != null
                    ? Arrays.asList(additional.toString().split(","))
                    : Collections.singletonList(null);

            if (Objects.nonNull(value.getValue())
             && !value.getValue().isEmpty()
             && excludedValues.contains(value.getValue().getValue())) {
                return FormFieldsGroup.createOrGroup()
                        .addFormField(FormField.noneMatch());
            }
        }

        //algorithm return all records as a result.
        if (Objects.isNull(value.getValue())) {
            return null;
        }

        String attrPath = value.getKey().getPath();
        return FormFieldsGroup.createOrGroup()
                .addFormField(value.getValue().isEmpty()
                    ? empty(attrPath)
                    : strict(value.getValue()));
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
