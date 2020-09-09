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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.SingleValueAttribute;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Cleanse function check is provided string empty.
 * @author ilya.bykov
 */
public class CFIsEmpty extends BasicCleanseFunctionAbstract {

    /**
     * Instantiates a new CF is empty.
     */
    public CFIsEmpty() {
        super(CFIsEmpty.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CleanseFunctionContext ctx) {
        CleanseFunctionInputParam input = ctx.getInputParamByPortName(INPUT1);
        boolean holdsNoValue = Objects.isNull(input) || input.isEmpty();
        boolean holdsEmptyStrings = holdsNoValue ? true : holdsEmptyStrings(input);
        ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, holdsNoValue || holdsEmptyStrings));
    }
    /**
     * Tells whether input consists entirely of empty strings.
     * @param input the input to check
     * @return true for empty strings, false otherwise
     */
    @SuppressWarnings("unchecked")
    private boolean holdsEmptyStrings(CleanseFunctionInputParam input) {

        for (int i = 0; i < input.getAttributes().size(); i++) {

            Attribute attribute = input.getAttributes().get(i);
            List<String> valuesToCheck = attribute.isSingleValue()
                ? Collections.singletonList(((SingleValueAttribute<String>) attribute).getValue())
                : ((ArrayAttribute<String>) attribute).toList();

            for (String valueToCheck : valuesToCheck) {
                if (StringUtils.isNotEmpty(valueToCheck)) {
                    return false;
                }
            }
        }

        return true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isContextAware() {
        return true;
    }
}