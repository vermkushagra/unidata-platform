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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.cleanse.common.CleanseConstants;
import com.unidata.mdm.cleanse.misc.CFSystemCleanseFunction;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Checks string value by mask.
 *
 * @author ilya.bykov
 */
public class CFCheckMask extends BasicCleanseFunctionAbstract implements CFSystemCleanseFunction {

    /** The regexp cache. */
    private LoadingCache<String, Pattern> regexpCache = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Pattern>() {

                @Override
                public Pattern load(String regexp) {
                    return Pattern.compile(regexp);

                }
            });

    /**
     * Instantiates a new CF check mask.
     */
    public CFCheckMask() {
        super(CFCheckMask.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CleanseFunctionContext ctx) {

        // Regex, mask, value, required
        CleanseFunctionInputParam param1 = ctx.getInputParamByPortName(CleanseConstants.INPUT1);
        CleanseFunctionInputParam param2 = ctx.getInputParamByPortName(CleanseConstants.INPUT2);
        CleanseFunctionInputParam param3 = ctx.getInputParamByPortName(CleanseConstants.INPUT3);
        CleanseFunctionInputParam param4 = ctx.getInputParamByPortName(CleanseConstants.INPUT4);

        String regexp = param1 != null && !param1.isEmpty() ? param1.toSingletonValue() : null;
        String mask =  param2 != null && !param2.isEmpty() ? param2.toSingletonValue() : null;
        boolean required = param4 != null && !param4.isEmpty() ? param4.toSingletonValue() : false;
        boolean noValidationParams = StringUtils.isAllBlank(mask, regexp);
        boolean noValidationValue = param3.isEmpty();

        if (noValidationParams || noValidationValue) {

            Boolean result;
            if (required) {
                result = noValidationParams ? Boolean.TRUE : Boolean.FALSE;
            } else {
                result = noValidationValue ? Boolean.TRUE : Boolean.FALSE;
            }

            ctx.putOutputParam(CleanseFunctionOutputParam.of(CleanseConstants.OUTPUT1, result));
        } else {

            regexp = StringUtils.isBlank(regexp) ? RegexpUtils.convertMaskToRegexString(mask) : regexp;

            boolean hasFailed = false;
            Map<Object, List<Attribute>> values = extractAndMapAttributes(param3);
            for (Iterator<Entry<Object, List<Attribute>>> ei = values.entrySet().iterator(); ei.hasNext(); ) {

                Entry<Object, List<Attribute>> entry = ei.next();
                if (StringUtils.isBlank(entry.getKey().toString()) && !required) {
                    ei.remove();
                    continue;
                }

                try {

                    if (!RegexpUtils.validate(regexpCache.get(regexp), entry.getKey().toString())) {
                        hasFailed = true;
                        ctx.failedValidations().addAll(entry.getValue().stream()
                                .map(attr -> new ImmutablePair<>(attr.toLocalPath(), attr))
                                .collect(Collectors.toList()));
                    } else {
                        ei.remove();
                    }

                } catch (ExecutionException e) {
                    hasFailed = true;
                    ctx.failedValidations().addAll(entry.getValue().stream()
                            .map(attr -> new ImmutablePair<>(attr.toLocalPath(), attr))
                            .collect(Collectors.toList()));
                    ei.remove();
                }
            }

            ctx.putOutputParam(CleanseFunctionOutputParam.of(CleanseConstants.OUTPUT1, !hasFailed));
            ctx.putOutputParam(CleanseFunctionOutputParam.of(CleanseConstants.OUTPUT2, !hasFailed
                    ? StringUtils.EMPTY
                    : MessageUtils.getMessage("app.cleanse.validation.mask",
                            values.keySet().toString(),
                            ctx.failedValidations().get(0).getLeft(),
                            mask)));
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
