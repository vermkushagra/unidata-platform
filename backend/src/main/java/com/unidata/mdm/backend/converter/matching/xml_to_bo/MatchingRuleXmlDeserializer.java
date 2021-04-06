package com.unidata.mdm.backend.converter.matching.xml_to_bo;

import javax.xml.bind.JAXBContext;

import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.AbstractXmlDeserializer;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.match.MatchingRuleDef;

@ConverterQualifier
@Component
public class MatchingRuleXmlDeserializer extends AbstractXmlDeserializer<MatchingRuleDef> {

    @Override
    protected JAXBContext getContext() {
        return JaxbUtils.getMatchContext();
    }

    @Override
    protected Class<MatchingRuleDef> getConvertClass() {
        return MatchingRuleDef.class;
    }

}
