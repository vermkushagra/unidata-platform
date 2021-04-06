package com.unidata.mdm.backend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author Michael Yashin. Created on 24.04.2015.
 */
public class LoggingMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    private static final Logger log = LoggerFactory.getLogger(LoggingMappingJackson2HttpMessageConverter.class);

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        Object obj = super.read(type, contextClass, inputMessage);
        if (log.isDebugEnabled()) {
            log.debug(type.getTypeName() + "=" + (obj != null ? getObjectMapper().writeValueAsString(obj) : "null"));
        }
        return obj;
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        Object obj = super.readInternal(clazz, inputMessage);
        if (log.isDebugEnabled()) {
            log.debug(clazz.getTypeName() + "=" + (obj != null ? getObjectMapper().writeValueAsString(obj) : "null"));
        }
        return obj;
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        super.writeInternal(object, outputMessage);
        if (log.isDebugEnabled() && object != null) {
            log.debug(object.getClass().getTypeName() + "=" + getObjectMapper().writeValueAsString(object));
        }
    }
}
