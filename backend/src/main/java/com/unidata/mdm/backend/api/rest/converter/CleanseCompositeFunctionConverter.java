package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic;
import com.unidata.mdm.meta.Port;
import com.unidata.mdm.meta.SimpleDataType;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.Node;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.NodeLink;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFLink;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFNode;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFNodeType;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CompositeCFLogic;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionType;
import com.unidata.mdm.backend.api.rest.dto.meta.PortDefinition;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

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
        }
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        target.setRequired(source.isRequired());
        return target;
    }

}
