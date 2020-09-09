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
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionPortApplicationModeRO;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionType;
import com.unidata.mdm.backend.api.rest.dto.meta.DQRRuleExecutionContext;
import com.unidata.mdm.backend.api.rest.dto.meta.PortDefinition;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.meta.CleanseFunctionExtendedDef;
import com.unidata.mdm.meta.Port;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * The Class CleanseFunctionConverter.
 *
 * @author Michael Yashin. Created on 21.05.2015.
 */
@ConverterQualifier
@Component
public class CleanseFunctionConverter implements Converter<CleanseFunctionExtendedDef, CleanseFunctionDefinition> {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.core.convert.converter.Converter#convert(java.lang
     * .Object)
     */
    @Override
    public CleanseFunctionDefinition convert(CleanseFunctionExtendedDef source) {
        CleanseFunctionDefinition target = new CleanseFunctionDefinition();

        target.setName(source.getFunctionName());
        target.setDescription(source.getDescription());
        target.setJavaClass(source.getJavaClass());
        target.setType(CleanseFunctionType.BASIC_FUNCTION);
        target.getInputPorts().addAll(convertPorts(source.getInputPorts()));
        target.getOutputPorts().addAll(convertPorts(source.getOutputPorts()));
        target.setSupportedExecutionContexts(source.getSupportedExecutionContexts() == null
                ? null
                : source.getSupportedExecutionContexts().stream()
                    .map(ctx -> DQRRuleExecutionContext.valueOf(ctx.name()))
                    .collect(Collectors.toList()));

        return target;
    }

    /**
     * Convert ports.
     *
     * @param source
     *            the source
     * @return the list
     */
    private List<PortDefinition> convertPorts(List<Port> source) {
        if (source == null) {
            return new ArrayList<PortDefinition>();
        }
        List<PortDefinition> target = new ArrayList<PortDefinition>();
        for (Port sourcePort : source) {
            target.add(convertPort(sourcePort));
        }
        return target;
    }

    /**
     * Convert port.
     *
     * @param source
     *            the source
     * @return the port definition
     */
    private PortDefinition convertPort(Port source) {
        if (source == null) {
            return null;
        }
        PortDefinition target = new PortDefinition();
        if (source.getDataType().equals(SimpleDataType.BOOLEAN)) {
            target.setDataType(com.unidata.mdm.backend.api.rest.dto.SimpleDataType.BOOLEAN);
        } else if (source.getDataType().equals(SimpleDataType.DATE)) {
            target.setDataType(com.unidata.mdm.backend.api.rest.dto.SimpleDataType.DATE);
        } else if (source.getDataType().equals(SimpleDataType.TIME)) {
            target.setDataType(com.unidata.mdm.backend.api.rest.dto.SimpleDataType.TIME);
        } else if (source.getDataType().equals(SimpleDataType.TIMESTAMP)) {
            target.setDataType(com.unidata.mdm.backend.api.rest.dto.SimpleDataType.TIMESTAMP);
        } else if (source.getDataType().equals(SimpleDataType.INTEGER)) {
            target.setDataType(com.unidata.mdm.backend.api.rest.dto.SimpleDataType.INTEGER);
        } else if (source.getDataType().equals(SimpleDataType.NUMBER)) {
            target.setDataType(com.unidata.mdm.backend.api.rest.dto.SimpleDataType.NUMBER);
        } else if (source.getDataType().equals(SimpleDataType.STRING)) {
            target.setDataType(com.unidata.mdm.backend.api.rest.dto.SimpleDataType.STRING);
        } else if (source.getDataType().equals(SimpleDataType.ANY)) {
            target.setDataType(com.unidata.mdm.backend.api.rest.dto.SimpleDataType.ANY);
        }
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        target.setRequired(source.isRequired());
        target.setPortApplicationMode(CleanseFunctionPortApplicationModeRO.valueOf(source.getApplicationMode().name()));
        return target;
    }

}
