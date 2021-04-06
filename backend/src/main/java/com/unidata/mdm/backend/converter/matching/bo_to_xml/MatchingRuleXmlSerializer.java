package com.unidata.mdm.backend.converter.matching.bo_to_xml;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.AbstractXmlSerializer;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.match.MatchingRuleDef;

@ConverterQualifier
@Component
public class MatchingRuleXmlSerializer extends AbstractXmlSerializer<MatchingRuleDef> {

    /**
     * Matching rule {@link QName}.
     */
    private static final QName MATCH_RULE_QNAME = new QName("http://match.mdm.unidata.com/", "MatchingRuleDef", "matchingRule");


    @Override
    protected QName getQName() {
        return MATCH_RULE_QNAME;
    }

    @Override
    protected JAXBContext getContext() {
        return JaxbUtils.getMatchContext();
    }

    @Override
    protected Class<MatchingRuleDef> getConvertClass() {
        return MatchingRuleDef.class;
    }
}
