package com.unidata.mdm.backend.converter.classifiers;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.api.rest.dto.XmlClassifierWrapper;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.classifier.FullClassifierDef;


/**
 * The Class ClassifierToXmlConverter.
 */
@ConverterQualifier
@Component
public class ClassifierToXmlConverter implements Converter<XmlClassifierWrapper, StreamingOutput> {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierToXmlConverter.class);

    /**
     * Classfier node {@link QName}.
     */
    private static final QName CLASSIFIER_FULL_DEF_QNAME = new QName("http://classifier.mdm.unidata.com/", "FullClassifierDef", "fullClassifierDef");

    /** The classifier def converter. */
    @Autowired
    private Converter<ClsfDTO, FullClassifierDef> classifierDefConverter;

    /* (non-Javadoc)
     * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
     */
    @Override
    public StreamingOutput convert(XmlClassifierWrapper source) {

        final FullClassifierDef fullClassifierDef = classifierDefConverter.convert(source.getClassifierPresentation());
        return output -> {
            try {
                JAXBElement<FullClassifierDef> jaxb = new JAXBElement<>(CLASSIFIER_FULL_DEF_QNAME, FullClassifierDef.class, null, fullClassifierDef);
                StringWriter sw = new StringWriter();
                Marshaller marshaller = JaxbUtils.getClassifierContext().createMarshaller();
                marshaller.marshal(jaxb, sw);
                output.write(sw.toString().getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                final String message = "Cannot marshall classifier from [{}]";
                LOGGER.error(message, fullClassifierDef, e);
                throw new DataProcessingException(message, e, ExceptionId.EX_CONVERSION_CLASSIFIER_TO_XML_FAILED);
            }
        };
    }

}
