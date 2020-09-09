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

package com.unidata.mdm.cleanse.misc;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.ArrayAttribute.ArrayDataType;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.CodeAttribute.CodeDataType;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.common.types.SingleValueAttribute;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Check that all selected values exist.
 * @author ilya.bykov
 */
public class CFIsExists extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF is exists.
	 */
	public CFIsExists() {
		super(CFIsExists.class);
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public void execute(CleanseFunctionContext ctx) {

        CleanseFunctionInputParam input = ctx.getInputParamByPortName(INPUT1);

        boolean holdsNoEmptyValues = true;
        boolean hasInput = Objects.nonNull(input);
        if (hasInput) {

            List<Pair<String, Attribute>> failedPaths = checkEmptyValues(input);
            if (CollectionUtils.isNotEmpty(failedPaths)) {

                holdsNoEmptyValues = false;
                ctx.failedValidations().addAll(failedPaths);
            }
        }

        if (hasInput && CollectionUtils.isNotEmpty(input.getIncomplete())) {

            holdsNoEmptyValues = false;
            ctx.failedValidations().addAll(
                input.getIncomplete().stream()
                    .map(element -> new ImmutablePair<String, Attribute>(element.toLocalPath(), null))
                    .collect(Collectors.toList()));
        }

        ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, hasInput && holdsNoEmptyValues && !input.isEmpty()));
    }
    /**
     * Tells whether input consists entirely of empty strings.
     * @param input the input to check
     * @return true for empty strings, false otherwise
     */
    @SuppressWarnings("unchecked")
    private List<Pair<String, Attribute>> checkEmptyValues(CleanseFunctionInputParam input) {

        List<Pair<String, Attribute>> failed = new ArrayList<>();
        for (int i = 0; i < input.getAttributes().size(); i++) {

            Attribute attribute = input.getAttributes().get(i);
            if (attribute.isEmpty()) {
                failed.add(ImmutablePair.of(attribute.toLocalPath(), attribute));
                continue;
            }

            if (attribute.getAttributeType() == AttributeType.ARRAY) {

                boolean hasEmpty = ((ArrayAttribute<String>) attribute).toList().stream()
                        .anyMatch(obj -> ((ArrayAttribute<?>) attribute).getDataType() == ArrayDataType.STRING
                            ? StringUtils.isBlank(obj.toString())
                            : Objects.isNull(obj));

                if (hasEmpty) {
                    failed.add(ImmutablePair.of(attribute.toLocalPath(), attribute));
                }
            } else if (attribute.getAttributeType() == AttributeType.COMPLEX) {

                boolean noEmptyRecords = ((ComplexAttribute) attribute).stream().noneMatch(record -> record.getSize() == 0);
                if (!noEmptyRecords) {
                    failed.add(ImmutablePair.of(attribute.toLocalPath(), attribute));
                }
            } else {

                SingleValueAttribute<?> sa = attribute.narrow();

                if (sa.getAttributeType() == AttributeType.CODE
                && ((CodeAttribute<?>) sa).getDataType() == CodeDataType.STRING
                && StringUtils.isBlank(((CodeAttribute<String>) sa).getValue())) {
                    failed.add(ImmutablePair.of(attribute.toLocalPath(), attribute));
                }

                if (sa.getAttributeType() == AttributeType.SIMPLE
                && ((SimpleAttribute<?>) sa).getDataType() == DataType.STRING
                && StringUtils.isBlank(((SimpleAttribute<String>) sa).getValue())) {
                    failed.add(ImmutablePair.of(attribute.toLocalPath(), attribute));
                }
            }
        }

        return failed;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isContextAware() {
        return true;
    }
}
