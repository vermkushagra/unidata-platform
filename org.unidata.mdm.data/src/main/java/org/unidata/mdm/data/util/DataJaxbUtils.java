package org.unidata.mdm.data.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unidata.mdm.core.serialization.jaxb.CustomEscapeHandler;
import org.unidata.mdm.data.EtalonRecord;
import org.unidata.mdm.data.EtalonRecordFull;
import org.unidata.mdm.data.OriginRecord;
import org.unidata.mdm.data.RelationTo;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.util.AbstractJaxbUtils;

import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

/**
 * JAXb stuff, related to base data.
 * @author Mikhail Mikhailov on Oct 18, 2019
 */
public final class DataJaxbUtils extends AbstractJaxbUtils {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataJaxbUtils.class);
    /**
     * Data root package.
     */
    private static final String DATA_ROOT_PACKAGE = "org.unidata.mdm.data";
    /**
     * API root package.
     */
    private static final String API_ROOT_PACKAGE = "org.unidata.mdm.api";
    /**
     * XSD dateTime date format.
     */
    public static final String XSD_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";
    /**
     * Data unmarshaller.
     */
    private static JAXBContext DATA_CONTEXT;
    /**
     * Meta unmarshaller.
     */
    private static JAXBContext API_CONTEXT;
    /**
     * Data object factory.
     */
    private static final org.unidata.mdm.data.ObjectFactory DATA_OBJECT_FACTORY
            = new org.unidata.mdm.data.ObjectFactory();
    /**
     * API object factory.
     */
    private static final org.unidata.mdm.api.ObjectFactory API_OBJECT_FACTORY
            = new org.unidata.mdm.api.ObjectFactory();
    /**
     * Static initializer.
     */
    static {
        final Map<String, Object> defaultContextProperties = new HashMap<>();
        defaultContextProperties.put(JAXBRIContext.XMLACCESSORFACTORY_SUPPORT, Boolean.TRUE);
        defaultContextProperties.put(JAXBRIContext.DISABLE_XML_SECURITY, Boolean.TRUE);
        defaultContextProperties.put(JAXBRIContext.SUPRESS_ACCESSOR_WARNINGS, Boolean.TRUE);

        try {
            DATA_CONTEXT = JAXBContext.newInstance(DATA_ROOT_PACKAGE,
                    Thread.currentThread().getContextClassLoader(),
                    defaultContextProperties);
            API_CONTEXT = JAXBContext.newInstance(API_ROOT_PACKAGE);
        } catch (Exception exc) {
            final String message = "JAXB init failure. Exiting.";
            LOGGER.error(message, exc);
            throw new PlatformFailureException(message, exc, DataExceptionIds.EX_DATA_JAXB_CONTEXT_INIT_FAILURE);
        }
    }
    /**
     * Constructor.
     */
    private DataJaxbUtils() {
        super();
    }
    /**
     * Gets data context.
     *
     * @return the context or null.
     */
    public static JAXBContext getDataContext() {
        return DATA_CONTEXT;
    }
    /**
     * Gets API context.
     *
     * @return the context or null.
     */
    public static JAXBContext getAPIContext() {
        return API_CONTEXT;
    }
    /**
     * @return the dataObjectFactory
     */
    public static org.unidata.mdm.data.ObjectFactory getDataObjectFactory() {
        return DATA_OBJECT_FACTORY;
    }
    /**
     * @return the apiObjectFactory
     */
    public static org.unidata.mdm.api.ObjectFactory getApiObjectFactory() {
        return API_OBJECT_FACTORY;
    }
    /**
     * Unmarshals a {@link EtalonRecord}.
     *
     * @param s string
     * @return golden record or null
     */
    public static OriginRecord unmarshalOriginRecord(String s) {
        try {
            return DataJaxbUtils.getDataContext()
                    .createUnmarshaller()
                    .unmarshal(new StreamSource(new StringReader(s)), OriginRecord.class)
                    .getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall origin record from [{}]";
            LOGGER.warn(message, s, je);
            throw new PlatformFailureException(message, je, DataExceptionIds.EX_DATA_CANNOT_UNMARSHAL_ORIGIN, s);
        }
    }

    /**
     * Unmarshals a {@link RelationTo}.
     *
     * @param s string
     * @return relation element or null
     */
    public static RelationTo unmarshalRelationTo(String s) {
        try {
            return DataJaxbUtils.getDataContext()
                    .createUnmarshaller()
                    .unmarshal(new StreamSource(new StringReader(s)), RelationTo.class)
                    .getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall relation from [{}]";
            LOGGER.warn(message, s, je);
            throw new PlatformFailureException(message, je, DataExceptionIds.EX_DATA_CANNOT_UNMARSHAL_RELATION, s);
        }
    }
    /**
     * Marshal a {@link EtalonRecord}.
     *
     * @param record the record
     * @return string
     */
    public static String marshalEtalonRecord(EtalonRecord record) {
        try {
            JAXBElement<EtalonRecord> el = DATA_OBJECT_FACTORY.createEtalonRecord(record);
            StringWriter sw = new StringWriter();

            Marshaller marshaller = DATA_CONTEXT.createMarshaller();
            marshaller.setProperty(CharacterEscapeHandler.class.getName(), CustomEscapeHandler.INSTANCE);

            marshaller.marshal(el, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall etalon record from [{}]";
            LOGGER.warn(message, record, je);
            throw new PlatformFailureException(message, je, DataExceptionIds.EX_DATA_CANNOT_MARSHAL_ETALON, record);
        }
    }

    /**
     * Marshal a {@link EtalonRecordFull}.
     *
     * @param record the record
     * @return string
     */
    public static String marshalEtalonRecordFull(EtalonRecordFull record) {
        try {
            JAXBElement<EtalonRecordFull> el = DATA_OBJECT_FACTORY.createEtalonRecordFull(record);
            StringWriter sw = new StringWriter();

            Marshaller marshaller = DATA_CONTEXT.createMarshaller();
            marshaller.setProperty(CharacterEscapeHandler.class.getName(), CustomEscapeHandler.INSTANCE);

            marshaller.marshal(el, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall etalon record from [{}]";
            LOGGER.warn(message, record, je);
            throw new PlatformFailureException(message, je, DataExceptionIds.EX_DATA_CANNOT_MARSHAL_ETALON, record);
        }
    }

    /**
     * Marshal a {@link OriginRecord}.
     *
     * @param record the record
     * @return string
     */
    public static String marshalOriginRecord(OriginRecord record) {
        try {
            JAXBElement<OriginRecord> el = DATA_OBJECT_FACTORY.createOriginRecord(record);
            StringWriter sw = new StringWriter();
            Marshaller marshaller = DATA_CONTEXT.createMarshaller();
            marshaller.setProperty(CharacterEscapeHandler.class.getName(), CustomEscapeHandler.INSTANCE);
            marshaller.marshal(el, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall origin record from [{}]";
            LOGGER.warn(message, record, je);
            throw new PlatformFailureException(message, je, DataExceptionIds.EX_DATA_CANNOT_MARSHAL_ORIGIN, record);
        }
    }

    /**
     * Marshal a {@link RelationTo}.
     *
     * @param relationTo relation to record
     * @return string
     */
    public static String marshalRelationTo(RelationTo relationTo) {
        try {
            JAXBElement<RelationTo> el = DATA_OBJECT_FACTORY.createRelationTo(relationTo);
            StringWriter sw = new StringWriter();
            Marshaller marshaller = DATA_CONTEXT.createMarshaller();
            marshaller.setProperty(CharacterEscapeHandler.class.getName(), CustomEscapeHandler.INSTANCE);
            marshaller.marshal(el, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall relation from [{}]";
            LOGGER.warn(message, relationTo, je);
            throw new PlatformFailureException(message, je, DataExceptionIds.EX_DATA_CANNOT_MARSHAL_RELATION, relationTo);
        }
    }
}
