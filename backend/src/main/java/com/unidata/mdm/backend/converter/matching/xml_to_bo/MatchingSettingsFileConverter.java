package com.unidata.mdm.backend.converter.matching.xml_to_bo;

import java.io.File;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.service.matching.data.MatchingUserSettings;
import com.unidata.mdm.match.MatchingSettingsDef;

@ConverterQualifier
@Component
public class MatchingSettingsFileConverter implements Converter<Path, MatchingUserSettings> {

    @Autowired
    private Converter<String, MatchingSettingsDef> deserializer;

    @Autowired
    private Converter<MatchingSettingsDef, MatchingUserSettings> matchingSettingsConverter;

    @Override
    public MatchingUserSettings convert(Path source) {
        File file = source.toFile();
        MatchingSettingsDef def = deserializer.convert(file.toURI().toASCIIString());
        return matchingSettingsConverter.convert(def);
    }
}
