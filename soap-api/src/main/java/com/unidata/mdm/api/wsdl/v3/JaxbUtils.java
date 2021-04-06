package com.unidata.mdm.api.wsdl.v3;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.bind.api.JAXBRIContext;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.data.v3.EtalonRecord;
import com.unidata.mdm.data.v3.OriginClassifierRecord;
import com.unidata.mdm.data.v3.OriginRecord;
import com.unidata.mdm.data.v3.RelationTo;

public class JaxbUtils {
   /**
    * Europe/Moscow timezone.
    */

   private static final String EUROPE_MOSCOW = "Europe/Moscow";

   /**
    * Object factory param.
    */
   private static final String OBJECT_FACTORY_PARAM = "com.sun.xml.bind.ObjectFactory";
   /**
    * Logger.
    */
   private static final Logger LOGGER = LoggerFactory.getLogger(JaxbUtils.class);
   /**
    * Data root package.
    */
   private static final String DATA_ROOT_PACKAGE = "com.unidata.mdm.data";  
   /**
    * API root package.
    */
   private static final String API_ROOT_PACKAGE = "com.unidata.mdm.api";
   /**
    * XSD dateTime date format.
    */
   public static final String XSD_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";

   /**
    * Data object factory.
    */
   private static final com.unidata.mdm.data.v3.ObjectFactory DATA_OBJECT_FACTORY
       = new com.unidata.mdm.data.v3.ObjectFactory();

   /**
    * Data unmarshaller.
    */
   private static JAXBContext DATA_CONTEXT;



   /**
    * API object factory.
    */
   private static final com.unidata.mdm.api.v3.ObjectFactory API_OBJECT_FACTORY = new com.unidata.mdm.api.v3.ObjectFactory();

   /**
    * Meta unmarshaller.
    */
   private static JAXBContext API_CONTEXT;


   /**
    * Datatype factory, which is not guaranteed to be thread safe
    * (implementation specific).
    */
   private static ThreadLocal<DatatypeFactory> DATATYPE_FACTORY = new ThreadLocal<DatatypeFactory>() {
       /**
        * @see java.lang.ThreadLocal#initialValue()
        */
       @Override
       protected DatatypeFactory initialValue() {
           try {
               return DatatypeFactory.newInstance();
           } catch (DatatypeConfigurationException e) {
               final String message = "DatatypeFactory initialization failure. [{}]";
               LOGGER.error(message, e);
               throw new DataProcessingException(message, ExceptionId.EX_SYSTEM_JAXB_TYPE_FACTORY_INIT_FAILURE, e);
           }
       }
   };

   static {
       try {

           final Map<String, Object> defaultContextProperties = new HashMap<>();
           defaultContextProperties.put(JAXBRIContext.XMLACCESSORFACTORY_SUPPORT, Boolean.TRUE);
           defaultContextProperties.put(JAXBRIContext.DISABLE_XML_SECURITY, Boolean.TRUE);
           defaultContextProperties.put(JAXBRIContext.SUPRESS_ACCESSOR_WARNINGS, Boolean.TRUE);

           DATA_CONTEXT = JAXBContext.newInstance(DATA_ROOT_PACKAGE,
                   Thread.currentThread().getContextClassLoader(),
                   defaultContextProperties);
         

           API_CONTEXT = JAXBContext.newInstance(API_ROOT_PACKAGE);
        
       } catch (Exception e) {
           final String message = "JAXB failure. Exiting. [{}]";
           LOGGER.error(message, e);
           throw new DataProcessingException(message, e, ExceptionId.EX_SYSTEM_JAXB_CONTEXT_INIT_FAILURE);
       }
   }

   /**
    * Constructor.
    */
   private JaxbUtils() {
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
    * Unmarshals a {@link EtalonRecord}.
    *
    * @param s
    *            string
    * @return golden record or null
    */
   public static OriginRecord unmarshalOriginRecord(String s) {
       try {
           return JaxbUtils.getDataContext()
                   .createUnmarshaller()
                   .unmarshal(new StreamSource(new StringReader(s)), OriginRecord.class)
                   .getValue();
       } catch (JAXBException je) {
           final String message = "Cannot unmarshall origin record from [{}]";
           LOGGER.warn(message, s, je);
           throw new DataProcessingException(message, je, ExceptionId.EX_DATA_CANNOT_UNMARSHAL_ORIGIN, s);
       }
   }

   /**
    * Unmarshals a {@link RelationTo}.
    *
    * @param s
    *            string
    * @return relation element or null
    */
   public static RelationTo unmarshalRelationTo(String s) {
       try {
           return JaxbUtils.getDataContext()
                   .createUnmarshaller()
                   .unmarshal(new StreamSource(new StringReader(s)), RelationTo.class)
                   .getValue();
       } catch (JAXBException je) {
           final String message = "Cannot unmarshall relation from [{}]";
           LOGGER.warn(message, s, je);
           throw new DataProcessingException(message, je, ExceptionId.EX_DATA_CANNOT_UNMARSHAL_RELATION, s);
       }
   }

   /**
    * Unmarshals a {@link RelationTo}.
    *
    * @param s
    *            string
    * @return relation element or null
    */
   public static OriginClassifierRecord unmarshalOriginClassifier(String s) {
       try {
           return JaxbUtils.getDataContext()
                   .createUnmarshaller()
                   .unmarshal(new StreamSource(new StringReader(s)), OriginClassifierRecord.class)
                   .getValue();
       } catch (JAXBException je) {
           final String message = "Cannot unmarshall origin classifier from [{}]";
           LOGGER.warn(message, s, je);
           throw new DataProcessingException(message, je, ExceptionId.EX_DATA_CANNOT_UNMARSHAL_CLASSIFIER, s);
       }
   }
   /**
    * Marshal a {@link EtalonRecord}.
    *
    * @param record
    *            the record
    * @return string
    */
   public static String marshalEtalonRecord(EtalonRecord record) {
       try {
           JAXBElement<EtalonRecord> el = DATA_OBJECT_FACTORY.createEtalonRecord(record);
           StringWriter sw = new StringWriter();
           DATA_CONTEXT.createMarshaller().marshal(el, sw);
           return sw.toString();
       } catch (JAXBException je) {
           final String message = "Cannot marshall etalon record from [{}]";
           LOGGER.warn(message, record, je);
           throw new DataProcessingException(message, je, ExceptionId.EX_DATA_CANNOT_MARSHAL_ETALON, record);
       }
   }

   /**
    * Marshal a {@link OriginRecord}.
    *
    * @param record
    *            the record
    * @return string
    */
   public static String marshalOriginRecord(OriginRecord record) {
       try {
           JAXBElement<OriginRecord> el = DATA_OBJECT_FACTORY.createOriginRecord(record);
           StringWriter sw = new StringWriter();
           DATA_CONTEXT.createMarshaller().marshal(el, sw);
           return sw.toString();
       } catch (JAXBException je) {
           final String message = "Cannot marshall origin record from [{}]";
           LOGGER.warn(message, record, je);
           throw new DataProcessingException(message, je, ExceptionId.EX_DATA_CANNOT_MARSHAL_ORIGIN, record);
       }
   }

   /**
    * Marshal a {@link Relat}.
    *
    * @param record
    *            the record
    * @return string
    */
   public static String marshalRelationTo(RelationTo relationTo) {
       try {
           JAXBElement<RelationTo> el = DATA_OBJECT_FACTORY.createRelationTo(relationTo);
           StringWriter sw = new StringWriter();
           DATA_CONTEXT.createMarshaller().marshal(el, sw);
           return sw.toString();
       } catch (JAXBException je) {
           final String message = "Cannot marshall relation from [{}]";
           LOGGER.warn(message, relationTo, je);
           throw new DataProcessingException(message, je, ExceptionId.EX_DATA_CANNOT_MARSHAL_RELATION, relationTo);
       }
   }

   /**
    * Marshal a {@link Relat}.
    *
    * @param record
    *            the record
    * @return string
    */
   public static String marshalOriginClassifier(OriginClassifierRecord record) {
       try {
           JAXBElement<OriginClassifierRecord> el = DATA_OBJECT_FACTORY.createOriginClassifierRecord(record);
           StringWriter sw = new StringWriter();
           DATA_CONTEXT.createMarshaller().marshal(el, sw);
           return sw.toString();
       } catch (JAXBException je) {
           final String message = "Cannot marshall origin classifier from [{}]";
           LOGGER.warn(message, record, je);
           throw new DataProcessingException(message, je, ExceptionId.EX_DATA_CANNOT_MARSHAL_CLASSIFIER, record);
       }
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
    * Gets a data type factory.
    *
    * @return data type factory
    */
   public static DatatypeFactory getDatatypeFactory() {
       return DATATYPE_FACTORY.get();
   }

   /**
    * Creates an {@link XMLGregorianCalendar} instance from string value. The
    * string must be in internal format already, which is ISO 8601 (transformer
    * applied).
    *
    * @param value
    *            the value
    * @return calendar
    */
   public static XMLGregorianCalendar ISO8601StringToXMGregorianCalendar(String value) {
       return getDatatypeFactory().newXMLGregorianCalendar(value);
   }

   /**
    * Creates an {@link XMLGregorianCalendar} instance from a {@link Date}
    * value.
    *
    * @param value
    *            the value
    * @return calendar
    */
   public static XMLGregorianCalendar dateToXMGregorianCalendar(Object value) {
       XMLGregorianCalendar result = null;
       if (value != null && value instanceof Date) {
           Calendar calendar = new GregorianCalendar();
           calendar.setTime((Date) value);
           //FIXME: Next code line temporary resurrected. Without it frontend have troubles with dates(e.g. 01.03.2010 shown as 28.03.2010).
           calendar.setTimeZone(TimeZone.getTimeZone(EUROPE_MOSCOW));
           result = calendarToXMGregorianCalendar(calendar);
       }

       return result;
   }

   /**
    * Creates an {@link XMLGregorianCalendar} instance from a {@link Date}
    * value.
    *
    * @param value
    *            the value
    * @return calendar
    */
   public static XMLGregorianCalendar localDateValueToXMGregorianCalendar(Object value) {
       XMLGregorianCalendar result = null;
       if (value != null && value instanceof Date) {
           Calendar calendar = new GregorianCalendar();
           calendar.setTime((Date) value);
           result = getDatatypeFactory()
                   .newXMLGregorianCalendarDate(
                           calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                           DatatypeConstants.FIELD_UNDEFINED);
       }

       return result;
   }
   /**
    * Creates an {@link XMLGregorianCalendar} instance from a {@link Date}
    * value.
    *
    * @param value
    *            the value
    * @return calendar
    */
   public static XMLGregorianCalendar localTimeValueToXMGregorianCalendar(Object value) {
       XMLGregorianCalendar result = null;
       if (value != null && value instanceof Date) {
           Calendar calendar = new GregorianCalendar();
           calendar.setTime((Date) value);
           result = getDatatypeFactory()
                   .newXMLGregorianCalendarTime(
                           calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND),
                           DatatypeConstants.FIELD_UNDEFINED);
       }

       return result;
   }
   /**
    * Creates an {@link XMLGregorianCalendar} instance from a {@link Date}
    * value.
    *
    * @param value
    *            the value
    * @return calendar
    */
   public static XMLGregorianCalendar localTimestampValueToXMGregorianCalendar(Object value) {
       XMLGregorianCalendar result = null;
       if (value != null && value instanceof Date) {
           Calendar calendar = new GregorianCalendar();
           calendar.setTime((Date) value);
           result = getDatatypeFactory()
                   .newXMLGregorianCalendar(
                           calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                           calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND),
                           DatatypeConstants.FIELD_UNDEFINED);
       }

       return result;
   }
   /**
    * Creates an {@link XMLGregorianCalendar} instance from a {@link Calendar}
    * value.
    *
    * @param value
    *            the value
    * @return calendar
    */
   public static XMLGregorianCalendar calendarToXMGregorianCalendar(Object value) {
       XMLGregorianCalendar result = null;
       if (value != null && value instanceof GregorianCalendar) {
           result = getDatatypeFactory().newXMLGregorianCalendar((GregorianCalendar) value);
       }
       return result;
   }

   /**
    * Gets {@link Calendar} from {@link XMLGregorianCalendar}.
    *
    * @param xmlCalendar
    *            XML calendar
    * @return util calendar
    */
   public static Calendar xmlGregorianCalendarToCalendar(XMLGregorianCalendar xmlCalendar) {
       Calendar result = null;
       if (xmlCalendar != null) {
           result = xmlCalendar.toGregorianCalendar();
       }
       return result;
   }

   /**
    * Gets {@link Date} from {@link XMLGregorianCalendar}.
    *
    * @param xmlCalendar
    *            XML calendar
    * @return util date
    */
   public static Date xmlGregorianCalendarToDate(XMLGregorianCalendar xmlCalendar) {
       Calendar calendar = xmlGregorianCalendarToCalendar(xmlCalendar);
       return calendar != null ? calendar.getTime() : null;
   }

   /**
    * @return the dataObjectFactory
    */
   public static com.unidata.mdm.data.v3.ObjectFactory getDataObjectFactory() {
       return DATA_OBJECT_FACTORY;
   }

   /**
    * @return the apiObjectFactory
    */
   public static com.unidata.mdm.api.v3.ObjectFactory getApiObjectFactory() {
       return API_OBJECT_FACTORY;
   }


}
