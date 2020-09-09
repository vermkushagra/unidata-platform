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

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.SingleValueAttribute;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.cleanse.common.CleanseConstants;

/**
 * Concatenate provided strings.
 *
 * @author ilya.bykov
 */
public class CFConcatenate extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF concatenate.
	 */
	public CFConcatenate() {
		super(CFConcatenate.class);
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
        CleanseFunctionInputParam param4 = ctx.getInputParamByPortName(CleanseConstants.INPUT4);

        String joined = Stream.of(param1, param2, param3, param4)
            .sequential()
            .filter(param -> param != null && !param.isEmpty())
            .map(CleanseFunctionInputParam::getAttributes)
            .flatMap(Collection::stream)
            .map(attr -> attr.isSingleValue() ? Collections.singletonList(((SingleValueAttribute<String>) attr).getValue()) : ((ArrayAttribute<String>) attr).<String>toList())
            .flatMap(Collection::stream)
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.joining());

        ctx.putOutputParam(CleanseFunctionOutputParam.of(CleanseConstants.OUTPUT1, joined));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isContextAware() {
        return true;
    }
}
