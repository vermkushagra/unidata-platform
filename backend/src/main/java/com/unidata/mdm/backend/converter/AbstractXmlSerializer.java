package com.unidata.mdm.backend.converter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;

public abstract class AbstractXmlSerializer<T> implements Converter<T, String> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractXmlSerializer.class);

    @Override
    public String convert(T source) {
        try {
            JAXBElement<T> jaxb = new JAXBElement<>(getQName(), getConvertClass(), null, source);
            StringWriter sw = new StringWriter();
            Marshaller marshaller = getContext().createMarshaller();
            marshaller.marshal(jaxb, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall " + getConvertClass() + " from [{}]";
            LOGGER.error(message, source, je);
            throw new DataProcessingException(message, je, ExceptionId.EX_MARSHALLING);
        }
    }

    protected abstract QName getQName();

    protected abstract JAXBContext getContext();

    protected abstract Class<T> getConvertClass();

}
