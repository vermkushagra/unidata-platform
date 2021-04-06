package com.unidata.mdm.backend.converter.matching.bo_to_xml;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.AbstractXmlSerializer;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.match.MatchingSettingsDef;

@ConverterQualifier
@Component
public class MatchingSettingsXmlSerializer extends AbstractXmlSerializer<MatchingSettingsDef> {

    /**
     * Matching rule {@link QName}.
     */
    private static final QName MATCH_SETTINGS_QNAME = new QName("http://match.mdm.unidata.com/", "MatchSettingsDef", "matchingSettings");

    @Override
    protected QName getQName() {
        return MATCH_SETTINGS_QNAME;
    }

    @Override
    protected JAXBContext getContext() {
        return JaxbUtils.getMatchContext();
    }

    @Override
    protected Class<MatchingSettingsDef> getConvertClass() {
        return MatchingSettingsDef.class;
    }
}
