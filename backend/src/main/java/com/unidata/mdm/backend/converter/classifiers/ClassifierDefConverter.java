package com.unidata.mdm.backend.converter.classifiers;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.classifier.ClassifierDef;


/**
 * The Class ClassifierDefConverter.
 */
@ConverterQualifier
@Component
public class ClassifierDefConverter implements Converter<ClassifierDef, ClsfDTO> {

    /* (non-Javadoc)
     * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
     */
    @Override
    public ClsfDTO convert(ClassifierDef source) {
    	ClsfDTO classifier = new ClsfDTO();
        classifier.setCodePattern(source.getCodePattern());
        classifier.setDisplayName(source.getDisplayName());
        classifier.setDescription(source.getDescription());
        classifier.setName(source.getName());
        return classifier;
    }
}
