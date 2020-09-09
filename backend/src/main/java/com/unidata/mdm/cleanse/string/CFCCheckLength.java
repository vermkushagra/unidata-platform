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

package com.unidata.mdm.cleanse.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.SingleValueAttribute;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.cleanse.common.CleanseConstants;

/**
 * Checks string length.
 * @author ilya.bykov
 */
public class CFCCheckLength extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF check value.
	 */
	public CFCCheckLength() {
		super(CFCCheckLength.class);
	}

	/**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void execute(CleanseFunctionContext ctx) {

        CleanseFunctionInputParam param1 = ctx.getInputParamByPortName(CleanseConstants.INPUT1);
        CleanseFunctionInputParam param2 = ctx.getInputParamByPortName(CleanseConstants.INPUT2);
        CleanseFunctionInputParam param3 = ctx.getInputParamByPortName(CleanseConstants.INPUT3);

        Long minValue = param2 != null ? param2.toSingletonValue() : null;
        Long maxValue = param3 != null ? param3.toSingletonValue() : null;

        minValue = minValue == null || minValue == 0L ? Long.MIN_VALUE : minValue;
        maxValue = maxValue == null || maxValue == 0L ? Long.MAX_VALUE : maxValue;

        if (param1 == null || param1.isEmpty()) {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(CleanseConstants.OUTPUT1, Boolean.FALSE));
        } else {

            boolean[] result = new boolean[param1.getAttributes().size()];
            List<Pair<String, Attribute>> failed = new ArrayList<>(param1.getAttributes().size());
            for (int i = 0; i < param1.getAttributes().size(); i++) {

                Attribute attribute = param1.getAttributes().get(i);
                List<String> valuesToCheck = attribute.isSingleValue()
                        ? Collections.singletonList(((SingleValueAttribute<String>) attribute).getValue())
                        : ((ArrayAttribute<String>) attribute).toList();

                for (String valueToCheck : valuesToCheck) {

                    result[i] = Objects.isNull(valueToCheck)
                            ? true
                            : (valueToCheck.length() >= minValue) && (valueToCheck.length() <= maxValue);

                    if (!result[i]) {
                        break;
                    }
                }

                if (!result[i]) {
                    failed.add(ImmutablePair.of(attribute.toLocalPath(), attribute));
                }
            }

            ctx.putOutputParam(CleanseFunctionOutputParam.of(CleanseConstants.OUTPUT1, BooleanUtils.and(result)));
            ctx.failedValidations().addAll(failed);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isContextAware() {
        return true;
    }
}
