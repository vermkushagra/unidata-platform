package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.meta.CleanseFunctionExtendedDef;
import com.unidata.mdm.meta.Port;
import com.unidata.mdm.meta.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionType;
import com.unidata.mdm.backend.api.rest.dto.meta.PortDefinition;

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
        return target;
    }

}
