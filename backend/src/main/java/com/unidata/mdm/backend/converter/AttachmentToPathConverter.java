package com.unidata.mdm.backend.converter;

import java.nio.file.Path;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.util.FileUtils;

@ConverterQualifier
@Component
public class AttachmentToPathConverter implements Converter<Attachment, Path> {

    @Override
    public Path convert(Attachment source) {
        return FileUtils.saveFileTempFolder(source);
    }

}
