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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.util.FileUtils;

@ConverterQualifier
@Component
public class InputStreamToStringConverter implements Converter<InputStream, String> {

    @Override
    public String convert(InputStream source) {
        try {
            try (ByteArrayOutputStream os = new ByteArrayOutputStream(source.available())) {

                byte[] buf = new byte[FileUtils.DEFAULT_BUFFER_SIZE];
                int count = -1;
                while ((count = source.read(buf, 0, buf.length)) != -1) {
                    os.write(buf, 0, count);
                }
                return os.toString(StandardCharsets.UTF_8.name());
            }
        } catch (IOException e) {
            throw new DataProcessingException("Input stream can not be converted to string",e, ExceptionId.EX_CONVERSION_STEAM_TO_STRING_FAILED);
        }
    }
}
