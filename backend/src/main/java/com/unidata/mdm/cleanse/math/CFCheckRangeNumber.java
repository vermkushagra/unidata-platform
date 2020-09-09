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

package com.unidata.mdm.cleanse.math;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT2;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT3;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

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
import com.unidata.mdm.cleanse.misc.CFSystemCleanseFunction;

/**
 * Cleanse function determine is given number in the provided range or not(for number values).
 * @author ilya.bykov
 */
public class CFCheckRangeNumber extends BasicCleanseFunctionAbstract implements CFSystemCleanseFunction {

    /**
     * Instantiates a new CF check value.
     *
     */
    public CFCheckRangeNumber() {
        super(CFCheckRangeNumber.class);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void execute(CleanseFunctionContext ctx) {

        CleanseFunctionInputParam param1 = ctx.getInputParamByPortName(INPUT1);
        CleanseFunctionInputParam param2 = ctx.getInputParamByPortName(INPUT2);
        CleanseFunctionInputParam param3 = ctx.getInputParamByPortName(INPUT3);

        if (param1 == null || param1.isEmpty()) {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, Boolean.FALSE));
        } else {

            Double minValue = param2 == null ? null : param2.toSingletonValue();
            Double maxValue = param3 == null ? null : param3.toSingletonValue();

            minValue = minValue == null ? Double.MIN_VALUE : minValue;
            maxValue = maxValue == null ? Double.MAX_VALUE : maxValue;

            // Multiple values were filtered
            boolean[] result = new boolean[param1.getAttributes().size()];
            List<Pair<String, Attribute>> failed = new ArrayList<>(param1.getAttributes().size());
            for (int i = 0; i < param1.getAttributes().size(); i++) {

                Attribute attribute = param1.getAttributes().get(i);
                List<Double> valuesToCheck = attribute.isSingleValue()
                        ? Collections.singletonList(((SingleValueAttribute<Double>) attribute).getValue())
                        : ((ArrayAttribute<Double>) attribute).toList();

                for (Double valueToCheck : valuesToCheck) {

                    result[i] = Objects.isNull(valueToCheck) ? true : (valueToCheck >= minValue) && (valueToCheck <= maxValue);
                    if (!result[i]) {
                        break;
                    }
                }

                if (!result[i]) {
                    failed.add(ImmutablePair.of(attribute.toLocalPath(), attribute));
                }
            }

            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, BooleanUtils.and(result)));
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
