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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;

@ConverterQualifier
@Component
public class AttachmentToFileConverter implements Converter<Attachment, File> {

    private static final String PREFIX = "stream2file";
    private static final String SUFFIX = ".tmp";

    @Autowired
    private Converter<Attachment, InputStream> converter;

    @Override
    public File convert(Attachment source) {
        try {
            InputStream inputStream = converter.convert(source);
            final File tempFile = File.createTempFile(PREFIX, SUFFIX);
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                IOUtils.copy(inputStream, out);
            }
            return tempFile;
        } catch (IOException e) {
            throw new DataProcessingException("Attachment cannot be convert to file", e, ExceptionId.EX_CONVERSION_ATTACHMENT_TO_FILE_FAILED);
        }
    }

}
