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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
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
    public FormFieldsGroup getFormFieldGroup(Map<Integer, Pair<AttributeInfoHolder, SimpleAttribute<?>>> attributeMap) {

        Pair<AttributeInfoHolder, SimpleAttribute<?>> value = attributeMap.get(ALGORITHM_ID);
        //algorithm return all records as a result.
        if (Objects.isNull(value.getValue()) || value.getValue().isEmpty()) {
            return null;
        }

        return FormFieldsGroup.createOrGroup()
                .addFormField(empty(value.getKey().getPath()))
                .addFormField(strict(value.getValue()));
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
