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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.CodeAttribute.CodeDataType;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.common.types.SingleValueAttribute;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.cleanse.common.CleanseConstants;

/**
 * Checks that input string matches provided regular expression.
 *
 * @author ilya.bykov
 *
 */
public class CFCheckValue extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF check value.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public CFCheckValue() {
		super(CFCheckValue.class);
	}
	/**
     * {@inheritDoc}
     */
    @Override
    public void execute(CleanseFunctionContext ctx) {

        CleanseFunctionInputParam param1 = ctx.getInputParamByPortName(CleanseConstants.INPUT1);
        CleanseFunctionInputParam param2 = ctx.getInputParamByPortName(CleanseConstants.INPUT2);
        CleanseFunctionInputParam param3 = ctx.getInputParamByPortName(CleanseConstants.INPUT3);

        String regex = param1 != null && !param1.isEmpty() ? param1.toSingletonValue() : null;
        if (((param2 == null || param2.isEmpty()) && (param3 == null || param3.isEmpty())) || StringUtils.isBlank(regex)) {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(CleanseConstants.OUTPUT1, Boolean.FALSE));
        } else {

            List<Attribute> selected;
            if (Objects.isNull(param2) || param2.isEmpty()) {
                if (Objects.isNull(param3) || param3.isEmpty()) {
                    selected = Collections.emptyList();
                } else {
                    selected = param3.getAttributes();
                }
            } else {
                selected = param2.getAttributes();
            }

            boolean[] result = new boolean[selected.size()];
            List<Pair<String, Attribute>> paths = new ArrayList<>(selected.size());
            for (int i = 0; i < selected.size(); i++) {

                Attribute attribute = selected.get(i);
                List<String> valuesToCheck = collectValuesToCheck(attribute);

                for (String valueToCheck : valuesToCheck) {

                    result[i] = valueToCheck.matches(regex);
                    if (!result[i]) {
                        break;
                    }
                }

                if (!result[i]) {
                    paths.add(ImmutablePair.of(attribute.toLocalPath(), attribute));
                }
            }

            ctx.putOutputParam(CleanseFunctionOutputParam.of(CleanseConstants.OUTPUT1, BooleanUtils.and(result)));
            ctx.failedValidations().addAll(paths);
        }
    }
    @SuppressWarnings("unchecked")
    private List<String> collectValuesToCheck(Attribute attribute) {

        if (attribute.isSingleValue()) {
            if ((attribute.getAttributeType() == AttributeType.SIMPLE && ((SimpleAttribute<?>) attribute).getDataType() == DataType.STRING)
             || (attribute.getAttributeType() == AttributeType.CODE && ((CodeAttribute<?>) attribute).getDataType() == CodeDataType.STRING)) {
                return Collections.singletonList(((SingleValueAttribute<String>) attribute).getValue());
            } else if ((attribute.getAttributeType() == AttributeType.SIMPLE && ((SimpleAttribute<?>) attribute).getDataType() == DataType.INTEGER)
                    || (attribute.getAttributeType() == AttributeType.CODE && ((CodeAttribute<?>) attribute).getDataType() == CodeDataType.INTEGER)) {
                return Collections.singletonList(((SingleValueAttribute<Long>) attribute).getValue().toString());
            }
        } else {
            if (attribute.getAttributeType() == AttributeType.ARRAY && ((SimpleAttribute<?>) attribute).getDataType() == DataType.STRING) {
               return ((ArrayAttribute<String>) attribute).toList();
           } else if (attribute.getAttributeType() == AttributeType.ARRAY && ((SimpleAttribute<?>) attribute).getDataType() == DataType.INTEGER) {
               List<Long> values = ((ArrayAttribute<Long>) attribute).toList();
               return values.stream().filter(Objects::nonNull).map(Object::toString).collect(Collectors.toList());
           }
        }

        return Collections.emptyList();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isContextAware() {
        return true;
    }
}
