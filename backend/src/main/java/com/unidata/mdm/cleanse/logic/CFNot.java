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

package com.unidata.mdm.cleanse.logic;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Objects;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.cleanse.misc.CFSystemCleanseFunction;

/**
 * Cleanse function performs NOT.
 */
public class CFNot extends BasicCleanseFunctionAbstract implements CFSystemCleanseFunction {

    /**
     * Instantiates a new CF not.
     */
    public CFNot() {
        super(CFNot.class);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void execute(CleanseFunctionContext ctx) {

        CleanseFunctionInputParam input = ctx.getInputParamByPortName(INPUT1);
        if (Objects.isNull(input) || input.isEmpty()) {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, (Boolean) null));
            return;
        }

        // Multiple values were filtered
        if (!ensureAllSingletons(ctx, input)) {
            return;
        }

        Boolean value = ((SimpleAttribute<Boolean>) input.getSingleton()).getValue();
        ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, !value));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isContextAware() {
        return true;
    }
}
