package com.unidata.mdm.backend.converter.classifiers;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.classifier.ClassifierDef;

@ConverterQualifier
@Component
public class ClassifierToDefConverter implements Converter<ClsfDTO, ClassifierDef> {

    @Override
    public ClassifierDef convert(ClsfDTO source) {
        ClassifierDef classifierDef = new ClassifierDef();
        classifierDef.setCodePattern(source.getCodePattern());
        classifierDef.setName(source.getName());
        classifierDef.setDisplayName(source.getDisplayName());
        classifierDef.setDescription(source.getDescription());
        classifierDef.setValidateCodeByLevel(source.isValidateCodeByLevel());
        return classifierDef;
    }
}
