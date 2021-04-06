package com.unidata.mdm.backend.converter.matching.xml_to_bo;

import javax.xml.bind.JAXBContext;

import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.AbstractXmlDeserializer;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.match.MatchingGroupDef;

@ConverterQualifier
@Component
public class MatchingGroupXmlDeserializer extends AbstractXmlDeserializer<MatchingGroupDef> {

    @Override
    protected JAXBContext getContext() {
        return JaxbUtils.getMatchContext();
    }

    @Override
    protected Class<MatchingGroupDef> getConvertClass() {
        return MatchingGroupDef.class;
    }
}
