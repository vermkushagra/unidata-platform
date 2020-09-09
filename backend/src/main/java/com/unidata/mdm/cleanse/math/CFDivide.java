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

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.cleanse.misc.CFSystemCleanseFunction;

/**
 * Cleanse function performs division operation.
 * @author ilya.bykov
 */
public class CFDivide extends BasicCleanseFunctionAbstract implements CFSystemCleanseFunction {

    /**
     * Instantiates a new CF divide.
     */
    public CFDivide() {
        super(CFDivide.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CleanseFunctionContext ctx) {

        CleanseFunctionInputParam param1 = ctx.getInputParamByPortName(INPUT1);
        CleanseFunctionInputParam param2 = ctx.getInputParamByPortName(INPUT2);

        if (param1 == null || param1.isEmpty() || param2 == null || param2.isEmpty()) {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, 0D));
        } else {

            // Multiple values were filtered
            if (!ensureAllSingletons(ctx, param1, param2)) {
                return;
            }

            Number value1 = param1.toSingletonValue();
            Number value2 = param2.toSingletonValue();
            if(value2.doubleValue() == 0) {
                throw new CleanseFunctionExecutionException(getDefinition().getFunctionName(), "Division by zero.");
            }

            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, value1.doubleValue() / value2.doubleValue()));
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
