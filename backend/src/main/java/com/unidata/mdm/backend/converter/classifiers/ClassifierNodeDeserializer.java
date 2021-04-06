package com.unidata.mdm.backend.converter.classifiers;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.classifier.ClassifierNodeDef;

@ConverterQualifier
@Component
public class ClassifierNodeDeserializer implements Converter<String, ClsfNodeDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierNodeDeserializer.class);

    @Autowired
    private Converter<ClassifierNodeDef, ClsfNodeDTO> nodeConverter;

    @Override
    public ClsfNodeDTO convert(String source) {
        JAXBContext context = JaxbUtils.getClassifierContext();

        try {
            ClassifierNodeDef classifierNodeDef = context.createUnmarshaller()
                    .unmarshal(new StreamSource(new StringReader(source)), ClassifierNodeDef.class)
                    .getValue();
            return nodeConverter.convert(classifierNodeDef);
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall node from [{}]";
            LOGGER.error(message, source, je);
            throw new DataProcessingException(message, je, ExceptionId.EX_META_CANNOT_UNMARSHAL_CLASSIFIER_NODE);
        }
    }
}
