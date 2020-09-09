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

import static com.unidata.mdm.backend.common.search.FormField.strict;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
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
    public FormFieldsGroup getFormFieldGroup(Map<Integer, Pair<AttributeInfoHolder, SimpleAttribute<?>>> attributeMap) {
        Map<Integer, String> fields = getMatchingFieldMap();
        Collection<FormField> formFields = new ArrayList<>(fields.size());
        for (Integer fieldId : fields.keySet()) {
            Pair<AttributeInfoHolder, SimpleAttribute<?>> input = attributeMap.get(fieldId);
            formFields.add(strict(input.getValue()));
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
