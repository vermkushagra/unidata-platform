package com.unidata.mdm.backend.api.rest.converter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.unidata.mdm.meta.CleanseFunctionConstant;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.Node;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.NodeLink;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFLink;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFNode;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CompositeCFLogic;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.PortDefinition;
import com.unidata.mdm.backend.service.cleanse.composite.CompositeFunctionMDAGRep;
import com.unidata.mdm.meta.CompositeCleanseFunctionNodeType;
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
        CompositeCleanseFunctionDef target = new CompositeCleanseFunctionDef();
        target.setDescription(source.getDescription());
        target.setFunctionName(source.getName());
        target.setInputPorts(convertPorts(source.getInputPorts()));
        target.setLogic(convertLogic(source.getLogic()));
        target.setOutputPorts(convertPorts(source.getOutputPorts()));

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
        CompositeCleanseFunctionLogic target = new CompositeCleanseFunctionLogic();
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
        Node target = new Node();
        target.setConstant(new CleanseFunctionConstant().withStringValue(source.getUiRelativePosition()));
        target.setFunctionName(source.getFunctionName());
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
        NodeLink target = new NodeLink();
        target.setFromNodeId(new BigInteger(source.getFromNodeId()));
        target.setFromPort(source.getFromPort());
        target.setToNodeId(new BigInteger(source.getToNodeId()));
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

        List<Port> target = new ArrayList<Port>();
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
        Port target = new Port();
        target.setDataType(SimpleDataType.valueOf(source.getDataType().name()));
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        target.setRequired(source.isRequired());
        return target;
    }

}
