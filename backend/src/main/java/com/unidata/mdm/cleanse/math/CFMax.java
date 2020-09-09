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
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.math.NumberUtils;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.types.SingleValueAttribute;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Cleanse function determine maximum.
 */
public class CFMax extends BasicCleanseFunctionAbstract {

    /**
     * Instantiates a new CF max.
     */
    public CFMax(){
        super(CFMax.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CleanseFunctionContext ctx) {

        CleanseFunctionInputParam param1 = ctx.getInputParamByPortName(INPUT1);
        CleanseFunctionInputParam param2 = ctx.getInputParamByPortName(INPUT2);

        double[] result = Stream.concat(
                Objects.isNull(param1) || param1.isEmpty() ? Stream.empty() : param1.getAttributes().stream(),
                Objects.isNull(param2) || param2.isEmpty() ? Stream.empty() : param2.getAttributes().stream())
            .map(attr -> ((Number) ((SingleValueAttribute<?>) attr).castValue()))
            .filter(Objects::nonNull)
            .mapToDouble(Number::doubleValue)
            .toArray();

        if (result.length == 0) {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, (Double) null));
        } else {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, NumberUtils.max(result)));
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
