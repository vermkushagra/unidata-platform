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

package com.unidata.mdm.cleanse.convert;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Cleanse function parse string to boolean.
 *
 * @author ilya.bykov
 */
public class CFParseBoolean extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF parse boolean.
	 */
	public CFParseBoolean() {
		super(CFParseBoolean.class);
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public void execute(CleanseFunctionContext ctx) {

        CleanseFunctionInputParam param = ctx.getInputParamByPortName(INPUT1);
        if (param.isEmpty()) {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, (Boolean) null));
        } else {

            Object[] values = param.toValuesObjects();
            Boolean[] collected = Arrays.stream(values)
                .map(Object::toString)
                .map(StringUtils::trimToNull)
                .map(BooleanUtils::toBooleanObject)
                .filter(Objects::nonNull)
                .toArray(size -> new Boolean[size]);

            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, collected.length == 0 ? null : BooleanUtils.and(collected)));
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