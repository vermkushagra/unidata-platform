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
