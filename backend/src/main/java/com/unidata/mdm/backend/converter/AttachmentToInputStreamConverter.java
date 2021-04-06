package com.unidata.mdm.backend.converter;

import java.io.InputStream;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@ConverterQualifier
@Component
public class AttachmentToInputStreamConverter implements Converter<Attachment, InputStream> {

    @Override
    public InputStream convert(Attachment source) {
            InputStream stream = source.getObject(InputStream.class);        
            return stream;
    }
}
