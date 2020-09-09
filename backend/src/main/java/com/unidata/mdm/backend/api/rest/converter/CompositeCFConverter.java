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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.cleanse.CFLink;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFNode;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFNodeType;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CompositeCFLogic;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.PortDefinition;
import com.unidata.mdm.backend.service.cleanse.composite.CompositeFunctionMDAGRep;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.Node;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.NodeLink;
import com.unidata.mdm.meta.CompositeCleanseFunctionNodeType;
import com.unidata.mdm.meta.DQCleanseFunctionPortApplicationMode;
import com.unidata.mdm.meta.DQRuleExecutionContext;
import com.unidata.mdm.meta.Port;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * The Class CompositeCFConverter.
 */
public class CompositeCFConverter {

    /**
     * Convert.
     *
     * @param fromList1
     *            the from list1
     * @param mdagRep
     *            the mdag rep
     * @return the list
     */
    public static List<List<CFLink>> convert(List<List<Node>> fromList1, CompositeFunctionMDAGRep mdagRep) {
        List<List<CFLink>> toList1 = new ArrayList<List<CFLink>>();
        for (List<Node> fromList2 : fromList1) {
            List<CFLink> toList2 = new ArrayList<CFLink>();
            for (int i = 0; i < fromList2.size(); i++) {
                if (i != fromList2.size() - 1) {
                    Set<NodeLink> nodeLinks1 = mdagRep.getAllEdges(fromList2.get(i), fromList2.get(i + 1));
                    Set<NodeLink> nodeLinks2 = mdagRep.getAllEdges(fromList2.get(i + 1), fromList2.get(i));
                    toList2.addAll(convertLinks(nodeLinks1));
                    toList2.addAll(convertLinks(nodeLinks2));
                }
            }
            toList1.add(toList2);
        }
        return toList1;
    }

    /**
     * Convert links.
     *
     * @param links
     *            the links
     * @return the list
     */
    private static List<CFLink> convertLinks(Set<NodeLink> links) {
        List<CFLink> cfLinks = new ArrayList<CFLink>();
        for (NodeLink nodeLink : links) {
            CFLink cfLink = new CFLink();
            cfLink.setFromNodeId(nodeLink.getFromNodeId().toString());
            cfLink.setToNodeId(nodeLink.getToNodeId().toString());
            cfLink.setFromPort(nodeLink.getFromPort());
            cfLink.setToPortType(nodeLink.getToPortType()==null?null:CFNodeType.valueOf(nodeLink.getToPortType().name()));
            cfLink.setFromPortType(nodeLink.getFromPortType()==null?null:CFNodeType.valueOf(nodeLink.getFromPortType().name()));

            cfLink.setToPort(nodeLink.getToPort());
            cfLinks.add(cfLink);
        }
        return cfLinks;
    }

    /**
     * Converts {@link CleanseFunctionDefinition} to
     * {@link CompositeCleanseFunctionDef}.
     *
     * @param source
     *            Cleanse function definition as it is represented on REST level
     *            {@link CompositeCleanseFunctionDef}
     * @return the composite cleanse function def
     *         {@link CompositeCleanseFunctionDef}
     */
    public static CompositeCleanseFunctionDef convert(CleanseFunctionDefinition source) {
        if (source == null) {
            return null;
        }
        CompositeCleanseFunctionDef target = JaxbUtils.getMetaObjectFactory().createCompositeCleanseFunctionDef();
        target.setDescription(source.getDescription());
        target.setFunctionName(source.getName());
        target.setInputPorts(convertPorts(source.getInputPorts()));
        target.setLogic(convertLogic(source.getLogic()));
        target.setOutputPorts(convertPorts(source.getOutputPorts()));
        target.getSupportedExecutionContexts().addAll(CollectionUtils.isEmpty(source.getSupportedExecutionContexts())
                ? Collections.emptyList()
                : source.getSupportedExecutionContexts().stream()
                    .map(Enum::name)
                    .filter(Objects::nonNull)
                    .map(DQRuleExecutionContext::valueOf)
                    .collect(Collectors.toList()));

        return target;
    }

    /**
     * Convert logic.
     *
     * @param source
     *            the source
     * @return the composite cleanse function logic
     */
    private static CompositeCleanseFunctionLogic convertLogic(CompositeCFLogic source) {
        if (source == null) {
            return null;
        }
        CompositeCleanseFunctionLogic target = JaxbUtils.getMetaObjectFactory().createCompositeCleanseFunctionLogic();
        target.setLinks(convertLinks(source.getLinks()));
        target.setNodes(convertNodes(source.getNodes()));
        return target;
    }

    /**
     * Convert nodes.
     *
     * @param source
     *            the source
     * @return the list
     */
    private static List<Node> convertNodes(List<CFNode> source) {
        if (source == null) {
            return null;
        }
        List<Node> target = new ArrayList<CompositeCleanseFunctionLogic.Node>();
        for (CFNode sourceNode : source) {
            target.add(convertNode(sourceNode));
        }
        return target;
    }

    /**
     * Convert node.
     *
     * @param source
     *            the source
     * @return the node
     */
    private static Node convertNode(CFNode source) {

        if (source == null) {
            return null;
        }

        Node target = JaxbUtils.getMetaObjectFactory().createCompositeCleanseFunctionLogicNode();
        target.setConstant(JaxbUtils.getMetaObjectFactory()
                .createCleanseFunctionConstant()
                .withStringValue(source.getUiRelativePosition()));
        target.setFunctionName(source.getFunctionName());
        target.setValue(DQRuleDefinitionToDQRuleDefConverter.convertConstant(source.getValue()));
        target.setNodeId(new BigInteger(source.getNodeId()));
        target.setNodeType(CompositeCleanseFunctionNodeType.valueOf(source.getNodeType().name()));
        return target;
    }

    /**
     * Convert links.
     *
     * @param source
     *            the source
     * @return the list
     */
    private static List<NodeLink> convertLinks(List<CFLink> source) {
        if (source == null) {
            return null;
        }

        List<NodeLink> target = new ArrayList<CompositeCleanseFunctionLogic.NodeLink>();
        for (CFLink sourceLink : source) {
            target.add(convertNodeLink(sourceLink));
        }
        return target;
    }

    /**
     * Convert node link.
     *
     * @param source
     *            the source
     * @return the node link
     */
    private static NodeLink convertNodeLink(CFLink source) {
        if (source == null) {
            return null;
        }
        NodeLink target = JaxbUtils.getMetaObjectFactory().createCompositeCleanseFunctionLogicNodeLink();
        target.setFromNodeId(new BigInteger(source.getFromNodeId()));
        target.setFromPort(source.getFromPort());
        target.setToNodeId(new BigInteger(source.getToNodeId()));
        target.setToPortType(source.getToPortType()==null?null:CompositeCleanseFunctionNodeType.valueOf(source.getToPortType().name()));
        target.setFromPortType(source.getFromPortType()==null?null:CompositeCleanseFunctionNodeType.valueOf(source.getFromPortType().name()));
        target.setToPort(source.getToPort());
        return target;
    }

    /**
     * Convert ports.
     *
     * @param source
     *            the source
     * @return the list
     */
    private static List<Port> convertPorts(List<PortDefinition> source) {
        if (source == null) {
            return null;
        }

        List<Port> target = new ArrayList<>();
        for (PortDefinition sourcePort : source) {
            target.add(convertPort(sourcePort));
        }
        return target;
    }

    /**
     * Convert port.
     *
     * @param source
     *            the source
     * @return the port
     */
    private static Port convertPort(PortDefinition source) {

        if (source == null) {
            return null;
        }

        Port target = JaxbUtils.getMetaObjectFactory().createPort();
        target.setDataType(SimpleDataType.valueOf(source.getDataType().name()));
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        target.setRequired(source.isRequired());
        target.setApplicationMode(source.getPortApplicationMode() == null
                ? null
                : DQCleanseFunctionPortApplicationMode.fromValue(source.getPortApplicationMode().name()));

        return target;
    }

}
