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

import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunction;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionGroup;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionType;
import com.unidata.mdm.backend.api.rest.dto.meta.DQRRuleExecutionContext;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.meta.CleanseFunctionDef;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;

/**
 * @author Michael Yashin. Created on 21.05.2015.
 */
@ConverterQualifier
@Component
public class CleanseFunctionGroupConverter implements Converter<CleanseFunctionGroupDef, CleanseFunctionGroup> {
    @Override
    public CleanseFunctionGroup convert(CleanseFunctionGroupDef source) {
        CleanseFunctionGroup result = new CleanseFunctionGroup();
        result.setName(source.getGroupName());
        result.setDescription(source.getDescription());
        for (Object element : source.getGroupOrCleanseFunctionOrCompositeCleanseFunction()) {
            if (element instanceof CleanseFunctionGroupDef) {
                CleanseFunctionGroup group = convert((CleanseFunctionGroupDef)element);
                result.getGroups().add(group);
            }
            if (element instanceof CleanseFunctionDef) {
                CleanseFunction function = convertFunction((CleanseFunctionDef) element);
                result.getFunctions().add(function);
            }
        }
        return result;
    }

    protected CleanseFunction convertFunction(CleanseFunctionDef functionDef) {
        CleanseFunction function = new CleanseFunction();
        function.setName(functionDef.getFunctionName());
        function.setDescription(functionDef.getDescription());
        function.setJavaClass(functionDef.getJavaClass());
        if (functionDef instanceof CompositeCleanseFunctionDef) {
            function.setType(CleanseFunctionType.COMPOSITE_FUNCTION);
        } else {
            function.setType(CleanseFunctionType.BASIC_FUNCTION);
        }

        function.setSupportedExecutionContexts(functionDef.getSupportedExecutionContexts() == null
                ? null
                : functionDef.getSupportedExecutionContexts().stream()
                    .map(ctx -> DQRRuleExecutionContext.valueOf(ctx.name()))
                    .collect(Collectors.toList()));

        return function;
    }

}
