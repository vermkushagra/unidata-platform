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

import java.util.Formatter;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.cleanse.common.CleanseConstants;
import com.unidata.mdm.cleanse.misc.CFSystemCleanseFunction;

/**
 * Format string according to provided pattern.
 *
 * @author ilya.bykov
 */
public class CFFormatString extends BasicCleanseFunctionAbstract implements CFSystemCleanseFunction {

	/**
	 * Instantiates a new CF format string.
	 */
	public CFFormatString(){
		super(CFFormatString.class);
	}
	/**
     * {@inheritDoc}
     */
    @Override
    public void execute(CleanseFunctionContext ctx) {

        CleanseFunctionInputParam param1 = ctx.getInputParamByPortName(CleanseConstants.INPUT1);
        CleanseFunctionInputParam param2 = ctx.getInputParamByPortName(CleanseConstants.INPUT2);

        // Multiple values were filtered
        if (param1 == null || param1.isEmpty() || param2 == null || param2.isEmpty() || !ensureAllSingletons(ctx, param1)) {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(CleanseConstants.OUTPUT1, (String) null));
            return;
        }

        StringBuilder sb = new StringBuilder();
        try (Formatter formatter = new Formatter(sb)) {
            formatter.format(param2.toSingletonValue(), new Object[] { param1.toSingletonValue() });
        }

        ctx.putOutputParam(CleanseFunctionOutputParam.of(CleanseConstants.OUTPUT1, sb.toString()));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isContextAware() {
        return true;
    }
}
