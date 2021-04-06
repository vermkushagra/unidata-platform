package com.unidata.mdm.backend.converter.matching.xml_to_bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingUserSettings;
import com.unidata.mdm.match.MatchingSettingsDef;

@ConverterQualifier
@Component
public class MatchingSettingsDeserializer implements Converter<String, MatchingUserSettings> {

    @Autowired
    private Converter<String, MatchingSettingsDef> deserializer;

    @Autowired
    private Converter<MatchingSettingsDef, MatchingUserSettings> converter;

    @Override
    public MatchingUserSettings convert(String source) {
        MatchingSettingsDef matchEngineSettings = deserializer.convert(source);
        return converter.convert(matchEngineSettings);
    }
}
