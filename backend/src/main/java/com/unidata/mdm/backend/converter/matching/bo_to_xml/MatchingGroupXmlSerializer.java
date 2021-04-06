package com.unidata.mdm.backend.converter.matching.bo_to_xml;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.AbstractXmlSerializer;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.match.MatchingGroupDef;

@ConverterQualifier
@Component
public class MatchingGroupXmlSerializer extends AbstractXmlSerializer<MatchingGroupDef> {

    /**
     * Matching group {@link QName}.
     */
    private static final QName MATCH_GROUP_QNAME = new QName("http://match.mdm.unidata.com/", "MatchingGroupDef", "matchingGroup");

    @Override
    protected QName getQName() {
        return MATCH_GROUP_QNAME;
    }

    @Override
    protected JAXBContext getContext() {
        return JaxbUtils.getMatchContext();
    }

    @Override
    protected Class<MatchingGroupDef> getConvertClass() {
        return MatchingGroupDef.class;
    }
}
