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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.cleanse.common.CleanseConstants;
import com.unidata.mdm.cleanse.misc.CFSystemCleanseFunction;

/**
 * Execute regular expression on the input string.
 *
 * @author ilya.bykov
 */
public class CFRegExp extends BasicCleanseFunctionAbstract implements CFSystemCleanseFunction {

	/**
	 * Instantiates a new CF reg exp.
	 *
	 */
	public CFRegExp() {
		super(CFRegExp.class);
	}
	/**
     * {@inheritDoc}
     */
    @Override
    public void execute(CleanseFunctionContext ctx) {

        CleanseFunctionInputParam param1 = ctx.getInputParamByPortName(CleanseConstants.INPUT1);
        CleanseFunctionInputParam param2 = ctx.getInputParamByPortName(CleanseConstants.INPUT2);
        CleanseFunctionInputParam param3 = ctx.getInputParamByPortName(CleanseConstants.INPUT3);

        // Multiple values were filtered
        if (!ensureAllSingletons(ctx, param1)) {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(CleanseConstants.OUTPUT1, (String) null));
            return;
        }

        String input = param1 == null || param1.isEmpty() ? StringUtils.EMPTY : param1.toSingletonValue();
        String pattern = param2.toSingletonValue();
        Long group = param3.toSingletonValue();

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(input);

        ctx.putOutputParam(CleanseFunctionOutputParam.of(CleanseConstants.OUTPUT1, matcher.find() ? matcher.group(group.intValue()) : null));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isContextAware() {
        return true;
    }
}
