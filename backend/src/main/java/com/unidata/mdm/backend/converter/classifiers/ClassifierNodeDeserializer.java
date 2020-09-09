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
