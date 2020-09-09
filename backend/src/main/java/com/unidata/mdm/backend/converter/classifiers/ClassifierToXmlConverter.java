/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
