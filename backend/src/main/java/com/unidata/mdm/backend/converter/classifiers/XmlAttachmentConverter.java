package com.unidata.mdm.backend.converter.classifiers;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.api.rest.dto.XmlAttachmentWrapper;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.classifier.FullClassifierDef;


/**
 * The Class XmlAttachmentConverter.
 */
@ConverterQualifier
@Component
public class XmlAttachmentConverter implements Converter<XmlAttachmentWrapper, FullClassifierDef> {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlAttachmentConverter.class);

    /* (non-Javadoc)
     * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
     */
    @Override
    public FullClassifierDef convert(XmlAttachmentWrapper source) {
    	File toImport = source.getAttachment().toFile();
        try {
            JAXBContext context = JaxbUtils.getClassifierContext();
            return context.createUnmarshaller()
                    .unmarshal(new StreamSource(toImport), FullClassifierDef.class).getValue();
        } catch (Exception e) {
            final String message = "Cannot unmarshall full classifier from [{}]";
            LOGGER.error(message, source, e);
            throw new DataProcessingException(message, e, ExceptionId.EX_META_CANNOT_UNMARSHAL_FULL_CLASSIFIER);
        }finally {
			toImport.delete();
		}
    }
}
