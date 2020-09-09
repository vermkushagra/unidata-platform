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
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT2;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Cleanse function performs XOR.
 * @author ilya.bykov
 */
public class CFXor extends BasicCleanseFunctionAbstract {

    /**
     * Instantiates a new CF xor.
     *
     */
    public CFXor() {
        super(CFXor.class);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void execute(CleanseFunctionContext ctx) {

        CleanseFunctionInputParam param1 = ctx.getInputParamByPortName(INPUT1);
        CleanseFunctionInputParam param2 = ctx.getInputParamByPortName(INPUT2);

        Boolean[] result = Stream.concat(
                Objects.isNull(param1) || param1.isEmpty() ? Stream.empty() : param1.getAttributes().stream(),
                Objects.isNull(param2) || param2.isEmpty() ? Stream.empty() : param2.getAttributes().stream())
            .map(attr -> ((SimpleAttribute<Boolean>) attr).getValue())
            .filter(Objects::nonNull)
            .toArray(size -> new Boolean[size]);

        if (result.length == 0) {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, (Boolean) null));
        } else {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, BooleanUtils.xor(result)));
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