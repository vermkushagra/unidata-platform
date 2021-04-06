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
