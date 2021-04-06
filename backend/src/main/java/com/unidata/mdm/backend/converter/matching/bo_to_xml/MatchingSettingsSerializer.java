package com.unidata.mdm.backend.converter.matching.bo_to_xml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingUserSettings;
import com.unidata.mdm.match.MatchingSettingsDef;

@ConverterQualifier
@Component
public class MatchingSettingsSerializer implements Converter<MatchingUserSettings, String> {

    @Autowired
    private Converter<MatchingUserSettings, MatchingSettingsDef> converter;

    @Autowired
    private Converter<MatchingSettingsDef, String> serializer;

    @Override
    public String convert(MatchingUserSettings source) {
        MatchingSettingsDef def = converter.convert(source);
        return serializer.convert(def);
    }

}
