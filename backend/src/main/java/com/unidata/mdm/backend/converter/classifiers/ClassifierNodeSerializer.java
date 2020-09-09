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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.StringWriter;

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


/**
 * The Class ClassifierNodeSerializer.
 */
@ConverterQualifier
@Component
public class ClassifierNodeSerializer implements Converter<ClsfNodeDTO, String> {


    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierNodeSerializer.class);

    /**
     * Classfier node {@link QName}.
     */
    private static final QName CLASSIFIER_NODE_QNAME = new QName("http://classifier.mdm.unidata.com/", "ClassifierNodeDef", "classifier");

    /** The node def converter. */
    @Autowired
    private Converter<ClsfNodeDTO, ClassifierNodeDef> nodeDefConverter;

    /* (non-Javadoc)
     * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
     */
    @Override
    public String convert(ClsfNodeDTO source) {
        ClassifierNodeDef classifierNodeDef = nodeDefConverter.convert(source);
        try {
            JAXBElement<ClassifierNodeDef> jaxb = new JAXBElement<>(CLASSIFIER_NODE_QNAME, ClassifierNodeDef.class, null, classifierNodeDef);
            StringWriter sw = new StringWriter();
            Marshaller marshaller = JaxbUtils.getClassifierContext().createMarshaller();
            marshaller.marshal(jaxb, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall node from [{}]";
            LOGGER.error(message, classifierNodeDef, je);
            throw new DataProcessingException(message, je, ExceptionId.EX_META_CANNOT_MARSHAL_CLASSIFIER_NODE);
        }
    }
}
