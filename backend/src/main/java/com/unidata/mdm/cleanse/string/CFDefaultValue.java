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

import org.apache.commons.lang.StringUtils;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.cleanse.common.CleanseConstants;
import com.unidata.mdm.cleanse.misc.CFSystemCleanseFunction;

/**
 * In case if value not provided(null) returns default value.
 *
 * @author ilya.bykov
 */
public class CFDefaultValue extends BasicCleanseFunctionAbstract implements CFSystemCleanseFunction {

	/**
	 * Instantiates a new CF default value.
	 */
	public CFDefaultValue() {
		super(CFDefaultValue.class);
	}
	/**
     * {@inheritDoc}
     */
    @Override
    public void execute(CleanseFunctionContext ctx) {

        CleanseFunctionInputParam param1 = ctx.getInputParamByPortName(CleanseConstants.INPUT1);
        CleanseFunctionInputParam param2 = ctx.getInputParamByPortName(CleanseConstants.INPUT2);

        // Multiple values were filtered
        if (!ensureAllSingletons(ctx, param1)) {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(CleanseConstants.OUTPUT1, (String) null));
            return;
        }

        ctx.putOutputParam(CleanseFunctionOutputParam.of(CleanseConstants.OUTPUT1, param1 == null || StringUtils.isBlank(param1.toSingletonValue())
                ? param2.<String>toSingletonValue()
                : param1.<String>toSingletonValue()));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isContextAware() {
        return true;
    }
}
