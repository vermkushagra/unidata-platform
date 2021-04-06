package com.unidata.mdm.backend.converter.matching.bo_to_xml;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.AbstractXmlSerializer;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.match.MatchingAlgorithmDef;

@ConverterQualifier
@Component
public class MatchingAlgorithmXmlSerializer extends AbstractXmlSerializer<MatchingAlgorithmDef> {

    /**
     * Matching algo {@link QName}.
     */
    private static final QName MATCH_ALGO_QNAME = new QName("http://match.mdm.unidata.com/", "MatchingAlgorithmDef", "matchingAlgorithm");

    @Override
    protected QName getQName() {
        return MATCH_ALGO_QNAME;
    }

    @Override
    protected JAXBContext getContext() {
        return JaxbUtils.getMatchContext();
    }

    @Override
    protected Class<MatchingAlgorithmDef> getConvertClass() {
        return MatchingAlgorithmDef.class;
    }
}
