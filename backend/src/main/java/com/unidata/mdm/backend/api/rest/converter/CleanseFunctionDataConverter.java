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

package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.api.rest.dto.data.ArrayAttributeRO;
import com.unidata.mdm.backend.api.rest.dto.data.SimpleAttributeRO;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext.CleanseFunctionContextBuilder;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.SimpleAttribute;

/**
 * The Class CleanseFunctionDataConverter.
 */
public class CleanseFunctionDataConverter {

    /**
     * Instantiates a new cleanse function data converter.
     */
    private CleanseFunctionDataConverter() {
    }

    /**
     * Convert rest simple to meta simple attr.
     *
     * @param source
     *            the source
     * @param functionName the name of the function to execute
     * @return context
     */
    public static final CleanseFunctionContext from(List<SimpleAttributeRO> source, String functionName) {

        CleanseFunctionContextBuilder cfcb = CleanseFunctionContext.builder()
                .cleanseFunctionName(functionName);

        for (SimpleAttributeRO simpleAttribute : source) {
            SimpleAttribute<?> attr = SimpleAttributeConverter.from(simpleAttribute);
            cfcb.input(CleanseFunctionInputParam.of(attr.getName(), Collections.singletonList(attr)));
        }

        return cfcb.build();
    }

    /**
     * Convert meta simple attr to rest simple attr.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static final Pair<List<SimpleAttributeRO>, List<ArrayAttributeRO>> to(CleanseFunctionContext source) {

        List<SimpleAttributeRO> simple = new ArrayList<>();
        List<ArrayAttributeRO> array = new ArrayList<>();
        for (CleanseFunctionOutputParam param : source.output()) {

            if (param.isSingleton()) {
                Attribute attr = param.getSingleton();
                if(attr==null) {
                	continue;
                }
                if (attr.getAttributeType() == AttributeType.SIMPLE) {
                    SimpleAttribute<?> sa = attr.narrow();
                    SimpleAttributeRO saro = SimpleAttributeConverter.to(sa);
                    if (Objects.nonNull(saro)) {
                        saro.setName(param.getPortName());
                        simple.add(saro);
                    }
                } else if (attr.getAttributeType() == AttributeType.ARRAY) {
                    ArrayAttribute<?> aa = attr.narrow();
                    ArrayAttributeRO aaro = ArrayAttributeConverter.to(aa);
                    if (Objects.nonNull(aaro)) {
                        aaro.setName(param.getPortName());
                        array.add(aaro);
                    }
                }
            }
        }

        return new ImmutablePair<>(simple, array);
    }
}
