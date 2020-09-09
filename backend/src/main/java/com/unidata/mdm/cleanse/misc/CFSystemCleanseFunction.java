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

package com.unidata.mdm.cleanse.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.ArrayAttribute.ArrayDataType;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.CodeAttribute.CodeDataType;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.DataQualityStatus;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.common.types.SingleValueAttribute;
import com.unidata.mdm.backend.service.cleanse.DQUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.meta.SeverityType;

/**
 * @author Mikhail Mikhailov
 * System CFs support.
 */
public interface CFSystemCleanseFunction {
    /**
     * Extracts and maps values to holder attributes.
     * @param input the input param
     * @return map
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    default Map<Object, List<Attribute>> extractAndMapAttributes(CleanseFunctionInputParam input) {

        if (Objects.isNull(input) || input.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Object, List<Attribute>> filtered = new HashMap<>(input.getAttributes().size());
        for (Attribute attribute : input.getAttributes()) {
            switch (attribute.getAttributeType()) {
            case ARRAY:
                ArrayAttribute<?> array = attribute.narrow();
                array.forEach(av -> {

                    if (Objects.isNull(av.getValue())
                     || (array.getDataType() == ArrayDataType.STRING && StringUtils.isBlank(av.castValue()))) {
                        return;
                    }

                    filtered.computeIfAbsent(av.getValue(), key -> new ArrayList<>()).add(attribute);
                });
                break;
            case CODE:
            case SIMPLE:

                SingleValueAttribute<?> single = attribute.narrow();
                if (Objects.isNull(single.getValue())) {
                    continue;
                }

                if (single.getAttributeType() == AttributeType.CODE
                && ((CodeAttribute<?>) single).getDataType() == CodeDataType.STRING
                && StringUtils.isBlank(((CodeAttribute<String>) single).getValue())) {
                    continue;
                }

                if (single.getAttributeType() == AttributeType.SIMPLE
                && ((SimpleAttribute<?>) single).getDataType() == DataType.STRING
                && StringUtils.isBlank(((SimpleAttribute<String>) single).getValue())) {
                    continue;
                }

                filtered.computeIfAbsent(single.getValue(), key -> new ArrayList<>()).add(attribute);
                break;
            default:
                break;
            }
        }

        return filtered;
    }
    /**
     * Checks, that all supplied parameters are singletons, adding an error to the context, if this is not the case.
     * @param params the parameters to check
     * @param ctx the context to add errors to
     * @return true, if all singletons, false otherwise
     */
    default boolean ensureAllSingletons(CleanseFunctionContext ctx, CleanseFunctionInputParam... params) {

        boolean allSingletons = true;
        for (int i = 0; params != null && i < params.length;  i++) {

            CleanseFunctionInputParam param = params[i];
            if (Objects.isNull(param)) {
                continue;
            }

            if (!param.isEmpty() && !param.isSingleton()) {

                String message = MessageUtils.getMessage("app.dq.multiple.attributes.filtered", ctx.getRuleName(), ctx.getCleanseFunctionName());
                ctx.errors().add(DataQualityError.builder()
                        .category(DQUtils.CATEGORY_SYSTEM)
                        .executionMode(ctx.getExecutionMode())
                        .status(DataQualityStatus.NEW)
                        .message(message)
                        .values(param.getAttributes().stream()
                                .map(attr -> new ImmutablePair<>(attr.toLocalPath(), attr))
                                .collect(Collectors.toList()))
                        .severity(SeverityType.NORMAL.name())
                        .ruleName(ctx.getRuleName())
                        .build());

                allSingletons = false;
            }
        }

        return allSingletons;
    }
}
