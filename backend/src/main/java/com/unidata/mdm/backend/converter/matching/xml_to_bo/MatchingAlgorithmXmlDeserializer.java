package com.unidata.mdm.backend.converter.matching.xml_to_bo;

import javax.xml.bind.JAXBContext;

import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.AbstractXmlDeserializer;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.match.MatchingAlgorithmDef;

@ConverterQualifier
@Component
public class MatchingAlgorithmXmlDeserializer extends AbstractXmlDeserializer<MatchingAlgorithmDef> {

    @Override
    protected JAXBContext getContext() {
        return JaxbUtils.getMatchContext();
    }

    @Override
    protected Class<MatchingAlgorithmDef> getConvertClass() {
        return MatchingAlgorithmDef.class;
    }
}
