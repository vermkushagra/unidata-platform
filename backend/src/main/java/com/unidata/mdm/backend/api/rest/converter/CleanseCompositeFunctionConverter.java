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
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFLink;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFNode;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFNodeType;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CompositeCFLogic;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionPortApplicationModeRO;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionType;
import com.unidata.mdm.backend.api.rest.dto.meta.DQRRuleExecutionContext;
import com.unidata.mdm.backend.api.rest.dto.meta.PortDefinition;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.Node;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.NodeLink;
import com.unidata.mdm.meta.Port;

// TODO: Auto-generated Javadoc
/**
 * The Class CleanseCompositeFunctionConverter.
 *
 * @author Michael Yashin. Created on 21.05.2015.
 */
@ConverterQualifier
@Component
public class CleanseCompositeFunctionConverter implements
        Converter<CompositeCleanseFunctionDef, CleanseFunctionDefinition> {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.core.convert.converter.Converter#convert(java.lang
     * .Object)
     */
    @Override
    public CleanseFunctionDefinition convert(CompositeCleanseFunctionDef source) {

        if (source == null) {
            return null;
        }
        CleanseFunctionDefinition target = new CleanseFunctionDefinition();

        target.setName(source.getFunctionName());
        target.setDescription(source.getDescription());
        target.setJavaClass(source.getJavaClass());
        target.setType(CleanseFunctionType.COMPOSITE_FUNCTION);
        target.getInputPorts().addAll(convertPorts(source.getInputPorts()));
        target.setLogic(convertLogic(source.getLogic()));
        target.getOutputPorts().addAll(convertPorts(source.getOutputPorts()));
        target.setSupportedExecutionContexts(source.getSupportedExecutionContexts() == null
                ? null
                : source.getSupportedExecutionContexts().stream()
                    .map(Enum::name)
                    .filter(Objects::nonNull)
                    .map(DQRRuleExecutionContext::valueOf)
                    .collect(Collectors.toList()));

        return target;
    }

    /**
     * Convert logic.
     *
     * @param source
     *            the source
     * @return the composite function logic
     */
    private CompositeCFLogic convertLogic(CompositeCleanseFunctionLogic source) {
        if (source == null) {
            return null;
        }
        CompositeCFLogic target = new CompositeCFLogic();
        for (NodeLink nodeLink : source.getLinks()) {
            target.addLink(convertNodeLink(nodeLink));
        }
        for (Node node : source.getNodes()) {
            target.addNode(convertNode(node));
        }
        return target;
    }

    /**
     * Convert node.
     *
     * @param source
     *            the source
     * @return the CF node
     */
    private CFNode convertNode(Node source) {
        if (source == null) {
            return null;
        }
        CFNode target = new CFNode();
        target.setFunctionName(source.getFunctionName());
        target.setValue(DQRuleDefToDQRuleDefinitionConverter.convertConstant(source.getValue(), ""));
        target.setNodeId(source.getNodeId() == null ? null : source.getNodeId().toString());
        target.setNodeType(CFNodeType.valueOf(source.getNodeType().name()));
        target.setUiRelativePosition(source.getConstant().getStringValue());
        return target;
    }

    /**
     * Convert node link.
     *
     * @param source
     *            the source
     * @return the link
     */
    private CFLink convertNodeLink(NodeLink source) {
        if (source == null) {
            return null;
        }
        CFLink target = new CFLink();
        target.setFromNodeId(source.getFromNodeId() == null ? null : source.getFromNodeId().toString());
        target.setFromPort(source.getFromPort());
        target.setToNodeId(source.getToNodeId() == null ? null : source.getToNodeId().toString());
        target.setToPort(source.getToPort());
        target.setToPortType(source.getToPortType()==null?null:CFNodeType.valueOf(source.getToPortType().name()));
        target.setFromPortType(source.getFromPortType()==null?null:CFNodeType.valueOf(source.getFromPortType().name()));
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
        target.setDataType(SimpleDataType.fromValue(source.getDataType().name()));
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        target.setRequired(source.isRequired());
        target.setPortApplicationMode(CleanseFunctionPortApplicationModeRO.valueOf(source.getApplicationMode().name()));
        return target;
    }

}
