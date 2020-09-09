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

package com.unidata.mdm.backend.converter;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;

public abstract class AbstractXmlDeserializer<T> implements Converter<String, T> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractXmlDeserializer.class);

    @Override
    public T convert(String source) {
        try {
            JAXBContext context = getContext();
            boolean isURI = isURI(source);
            StreamSource streamSource = isURI ? new StreamSource(source) : new StreamSource(new ByteArrayInputStream(source.getBytes("utf-8")));
            return context.createUnmarshaller().unmarshal(streamSource, getConvertClass()).getValue();
        } catch (Exception e) {
            final String message = "Cannot unmarshall " + getConvertClass() + " from [{}]";
            LOGGER.error(message, source, e);
            throw new DataProcessingException(message, e, ExceptionId.EX_UNMARSHALLING);
        }
    }

    private boolean isURI(String source) {
        try {
            URI uri = new URI(source);
            return uri.getPath() != null;
        } catch (Exception e) {
            return false;
        }
    }

    protected abstract JAXBContext getContext();

    protected abstract Class<T> getConvertClass();

}
