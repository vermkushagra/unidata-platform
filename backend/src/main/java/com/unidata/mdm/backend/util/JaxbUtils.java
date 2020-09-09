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

package com.unidata.mdm.backend.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.bind.marshaller.CharacterEscapeHandler;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.MetadataException;
import com.unidata.mdm.classifier.FullClassifierDef;
import com.unidata.mdm.conf.Configuration;
import com.unidata.mdm.data.EtalonRecord;
import com.unidata.mdm.data.OriginClassifierRecord;
import com.unidata.mdm.data.OriginRecord;
import com.unidata.mdm.data.RelationTo;
import com.unidata.mdm.match.MatchingSettingsDef;
import com.unidata.mdm.meta.CleanseFunctionDef;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;
import com.unidata.mdm.meta.EntitiesGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.EnumerationDataType;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.MeasurementValues;
import com.unidata.mdm.meta.Model;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SourceSystemDef;
import com.unidata.mdm.security.Security;

/**
 * @author Mikhail Mikhailov
 *
 */
public class JaxbUtils {

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
     * Meta root package.
     */
    private static final String META_ROOT_PACKAGE = "com.unidata.mdm.meta";
    /**
     * API root package.
     */
    private static final String API_ROOT_PACKAGE = "com.unidata.mdm.api";
    /**
     * Configuration root package.
     */
    private static final String CONF_ROOT_PACKAGE = "com.unidata.mdm.conf";
    /**
     * Classifiers root package
     */
    private static final String CLASSIFIER_ROOT_PACKAGE = "com.unidata.mdm.classifier";
    /**
     * Match root package
     */
    private static final String MATCH_ROOT_PACKAGE = "com.unidata.mdm.match";
    /**
     * Match root package
     */
    private static final String SECURITY_ROOT_PACKAGE = "com.unidata.mdm.security";

    /**
     * XSD dateTime date format.
     */
    public static final String XSD_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";
    /*
     * private static final SchemaFactory SCHEMA_FACTORY =
     * SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
     */
    // private static Schema META_SCHEMA;

    /**
     * Data object factory.
     */
    private static final com.unidata.mdm.data.ObjectFactory DATA_OBJECT_FACTORY
        = new com.unidata.mdm.data.ObjectFactory();

    /**
     * Data unmarshaller.
     */
    private static JAXBContext DATA_CONTEXT;

    /**
     * Meta object factory.
     */
    private static final com.unidata.mdm.backend.meta.impl.ObjectFactoryEx META_OBJECT_FACTORY
        = new com.unidata.mdm.backend.meta.impl.ObjectFactoryEx();

    /**
     * Meta unmarshaller.
     */
    private static JAXBContext META_CONTEXT;

    /**
     * API object factory.
     */
    private static final com.unidata.mdm.api.ObjectFactory API_OBJECT_FACTORY = new com.unidata.mdm.api.ObjectFactory();

    /**
     * Meta unmarshaller.
     */
    private static JAXBContext API_CONTEXT;

    /**
     * Conf object factory.
     */
    private static final com.unidata.mdm.backend.conf.impl.ObjectFactoryEx CONF_OBJECT_FACTORY
        = new com.unidata.mdm.backend.conf.impl.ObjectFactoryEx();

    /**
     * Configuration context.
     */
    private static JAXBContext CONF_CONTEXT;

    /**
     * Classifier unmarshaller.
     */
    private static JAXBContext CLASSIFIER_CONTEXT;

    /**
     * Classifier factory.
     */
    private static final com.unidata.mdm.classifier.ObjectFactory CLASSIFIER_FACTORY
        = new com.unidata.mdm.classifier.ObjectFactory();

    /**
     * Matching context
     */
    private static JAXBContext MATCH_CONTEXT;

    /**
     * Security context
     */
    private static JAXBContext SECURITY_CONTEXT;

    /**
     * Security factory.
     */
    private static final com.unidata.mdm.security.ObjectFactory SECURITY_FACTORY =
            new com.unidata.mdm.security.ObjectFactory();

    /**
     * Meta model namespace URI.
     */
    public static final String META_URI = "http://meta.mdm.unidata.com/";

    /**
     * Datatype factory, which is not guaranteed to be thread safe
     * (implementation specific).
     */
    private static ThreadLocal<DatatypeFactory> DATATYPE_FACTORY = ThreadLocal.withInitial(() -> {
            try {
                return DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException e) {
                final String message = "DatatypeFactory initialization failure. [{}]";
                LOGGER.error(message, e);
                throw new DataProcessingException(message, ExceptionId.EX_SYSTEM_JAXB_TYPE_FACTORY_INIT_FAILURE, e);
            }
    });

    static {
        try {

            final Map<String, Object> defaultContextProperties = new HashMap<>();
            defaultContextProperties.put(JAXBRIContext.XMLACCESSORFACTORY_SUPPORT, Boolean.TRUE);
            defaultContextProperties.put(JAXBRIContext.DISABLE_XML_SECURITY, Boolean.TRUE);
            defaultContextProperties.put(JAXBRIContext.SUPRESS_ACCESSOR_WARNINGS, Boolean.TRUE);

            DATA_CONTEXT = JAXBContext.newInstance(DATA_ROOT_PACKAGE,
                    Thread.currentThread().getContextClassLoader(),
                    defaultContextProperties);

            META_CONTEXT = JAXBContext.newInstance(META_ROOT_PACKAGE,
                    Thread.currentThread().getContextClassLoader(),
                    defaultContextProperties);

            CLASSIFIER_CONTEXT = JAXBContext.newInstance(CLASSIFIER_ROOT_PACKAGE,
                    Thread.currentThread().getContextClassLoader(),
                    defaultContextProperties);

            MATCH_CONTEXT = JAXBContext.newInstance(MATCH_ROOT_PACKAGE,
                    Thread.currentThread().getContextClassLoader(),
                    defaultContextProperties);

            SECURITY_CONTEXT = JAXBContext.newInstance(SECURITY_ROOT_PACKAGE,
                    Thread.currentThread().getContextClassLoader(),
                    defaultContextProperties);

            API_CONTEXT = JAXBContext.newInstance(API_ROOT_PACKAGE);
            CONF_CONTEXT = JAXBContext.newInstance(CONF_ROOT_PACKAGE);

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
     * Gets configuration context.
     *
     * @return the context or null.
     */
    public static JAXBContext getConfContext() {
        return CONF_CONTEXT;
    }

    public static JAXBContext getClassifierContext(){
        return CLASSIFIER_CONTEXT;
    }

    public static JAXBContext getMatchContext(){
        return MATCH_CONTEXT;
    }

    public static JAXBContext getSecurityContext() {
        return SECURITY_CONTEXT;
    }

    /**
     * Unmarshals {@link Configuration}.
     *
     * @param is
     *            input stream
     * @return configuration
     */
    public static Configuration unmarshalConfiguration(InputStream is) {
        try {
            Unmarshaller u = JaxbUtils.getConfContext().createUnmarshaller();
            u.setProperty(OBJECT_FACTORY_PARAM, CONF_OBJECT_FACTORY);
            return (Configuration) u.unmarshal(is);
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall configuration from [{}]";
            LOGGER.warn(message, is, je);
            throw new DataProcessingException(message, je, ExceptionId.EX_SYSTEM_JAXB_CANNOT_UNMARSHAL_CONFIGURATION,
                    is);
        }
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

            Marshaller marshaller = DATA_CONTEXT.createMarshaller();
            marshaller.setProperty(CharacterEscapeHandler.class.getName(), CustomEscapeHandler.theInstance);

            marshaller.marshal(el, sw);
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
            Marshaller marshaller = DATA_CONTEXT.createMarshaller();
            marshaller.setProperty(CharacterEscapeHandler.class.getName(), CustomEscapeHandler.theInstance);
            marshaller.marshal(el, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall origin record from [{}]";
            LOGGER.warn(message, record, je);
            throw new DataProcessingException(message, je, ExceptionId.EX_DATA_CANNOT_MARSHAL_ORIGIN, record);
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
            marshaller.setProperty(CharacterEscapeHandler.class.getName(), CustomEscapeHandler.theInstance);
            marshaller.marshal(el, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall relation from [{}]";
            LOGGER.warn(message, relationTo, je);
            throw new DataProcessingException(message, je, ExceptionId.EX_DATA_CANNOT_MARSHAL_RELATION, relationTo);
        }
    }

    /**
     * Marshal a {@link OriginClassifierRecord}.
     *
     * @param record
     *            the record
     * @return string
     */
    public static String marshalOriginClassifier(OriginClassifierRecord record) {
        try {
            JAXBElement<OriginClassifierRecord> el = DATA_OBJECT_FACTORY.createOriginClassifierRecord(record);
            StringWriter sw = new StringWriter();
            Marshaller marshaller = DATA_CONTEXT.createMarshaller();
            marshaller.setProperty(CharacterEscapeHandler.class.getName(), CustomEscapeHandler.theInstance);
            marshaller.marshal(el, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall origin classifier from [{}]";
            LOGGER.warn(message, record, je);
            throw new DataProcessingException(message, je, ExceptionId.EX_DATA_CANNOT_MARSHAL_CLASSIFIER, record);
        }
    }
    // -------------------------------------------------------------- Meta model
    // --------------------------------------------------------------------
    /**
     * Unmarshal model.
     *
     * @param s
     *            source
     * @return model
     */
    public static Model unmarshalMetaModel(String s) {
        try {
            return (Model) META_CONTEXT.createUnmarshaller().unmarshal(new StringReader(s));
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall model from [{}]";
            LOGGER.error(message, s, je);
            throw new DataProcessingException(message, je, ExceptionId.EX_META_CANNOT_UNMARSHAL_MODEL);
        }
    }

    /**
     * Marshal the model.
     *
     * @param m
     *            the model
     * @return string
     */
    public static String marshalMetaModel(Model m) {
        try {
            StringWriter sw = new StringWriter();
            META_CONTEXT.createMarshaller().marshal(m, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall model from [{}]";
            LOGGER.error(message, m, je);
            throw new DataProcessingException(message, je, ExceptionId.EX_META_CANNOT_MARSHAL_MODEL);
        }
    }

    /**
     * Marshal a {@link CleanseFunctionDef}.
     *
     * @param record
     *            the record
     * @return string
     */
    /*
     * public static String marshalCleanseFunction(CleanseFunctionDef record) {
     * try { JAXBElement<CleanseFunctionDef> el =
     * META_OBJECT_FACTORY.createCleanseFunctionDef(record); StringWriter sw =
     * new StringWriter(); META_CONTEXT.createMarshaller().marshal(el, sw);
     * return sw.toString(); } catch (JAXBException je) { final String message =
     * "Cannot marshall cleanse function record from [{}]"; LOGGER.warn(message,
     * record, je); throw new MetadataException(message, je,
     * ExceptionId.EX_META_CANNOT_MARSHAL_CLEANSE_FUNCTION, record); } }
     */
    /**
     * Marshal a {@link CompositeCleanseFunctionDef}.
     *
     * @param record
     *            the record
     * @return string
     */

    /*
     * public static String
     * marshalCompositeCleanseFunction(CompositeCleanseFunctionDef record) { try
     * { JAXBElement<CompositeCleanseFunctionDef> el =
     * META_OBJECT_FACTORY.createCompositeCleanseFunctionDef(record);
     * StringWriter sw = new StringWriter();
     * META_CONTEXT.createMarshaller().marshal(el, sw); return sw.toString(); }
     * catch (JAXBException je) { final String message =
     * "Cannot marshall composite cleanse function record from [{}]";
     * LOGGER.warn(message, record, je); throw new MetadataException(message,
     * je, ExceptionId.EX_META_CANNOT_MARSHAL_COMPOSITE_CLEANSE_FUNCTION,
     * record); } }
     */
    /**
     * Marshal a {@link CleanseFunctionGroupDef}.
     *
     * @param record
     *            the record
     * @return string
     */
    public static String marshalCleanseFunctionGroup(CleanseFunctionGroupDef record) {
        try {
            JAXBElement<CleanseFunctionGroupDef> el = META_OBJECT_FACTORY.createCleanseFunctionGroupDef(record);
            StringWriter sw = new StringWriter();
            META_CONTEXT.createMarshaller().marshal(el, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall cleanse function group record from [{}]";
            LOGGER.warn(message, record, je);
            throw new MetadataException(message, je, ExceptionId.EX_META_CANNOT_MARSHAL_CLEANSE_FUNCTION_GROUP, record);
        }
    }

    /**
     * Marshal a {@link SourceSystemDef}.
     *
     * @param record
     *            the record
     * @return string
     */
    public static String marshalSourceSystem(SourceSystemDef record) {
        try {
            JAXBElement<SourceSystemDef> el = META_OBJECT_FACTORY.createSourceSystemDef(record);
            StringWriter sw = new StringWriter();
            Marshaller m = META_CONTEXT.createMarshaller();
            // m.setSchema(META_SCHEMA);
            m.marshal(el, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall source system record from [{}]";
            LOGGER.warn(message, record, je);
            throw new MetadataException(message, je, ExceptionId.EX_META_CANNOT_MARSHAL_SOURCE_SYSTEM, record);
        }
    }

    /**
     * Marshal a {@link EnumerationDataType}.
     *
     * @param record
     *            the record
     * @return string
     */
    public static String marshalEnumeration(EnumerationDataType record) {
        try {
            JAXBElement<EnumerationDataType> el = META_OBJECT_FACTORY.createEnumerationDataType(record);
            StringWriter sw = new StringWriter();
            Marshaller m = META_CONTEXT.createMarshaller();
            // m.setSchema(META_SCHEMA);
            m.marshal(el, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall enumeration record from [{}]";
            LOGGER.warn(message, record, je);
            throw new MetadataException(message, je, ExceptionId.EX_META_CANNOT_MARSHAL_ENUMERATION, record);
        }
    }

    /**
     * Marshal a {@link LookupEntityDef}.
     *
     * @param record
     *            the record
     * @return string
     */
    public static String marshalLookupEntity(LookupEntityDef record) {
        try {
            JAXBElement<LookupEntityDef> el = META_OBJECT_FACTORY.createLookupEntityDef(record);
            StringWriter sw = new StringWriter();
            Marshaller m = META_CONTEXT.createMarshaller();
            // m.setSchema(META_SCHEMA);
            m.marshal(el, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall lookup entity record from [{}]";
            LOGGER.warn(message, record, je);
            throw new MetadataException(message, je, ExceptionId.EX_META_CANNOT_MARSHAL_LOOKUP_ENTITY, record);
        }
    }

    /**
     * Marshal a {@link NestedEntityDef}.
     *
     * @param record
     *            the record
     * @return string
     */
    public static String marshalNestedEntity(NestedEntityDef record) {
        try {
            JAXBElement<NestedEntityDef> el = META_OBJECT_FACTORY.createNestedEntityDef(record);
            StringWriter sw = new StringWriter();
            Marshaller m = META_CONTEXT.createMarshaller();
            // m.setSchema(META_SCHEMA);
            m.marshal(el, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall nested entity record from [{}]";
            LOGGER.warn(message, record, je);
            throw new MetadataException(message, je, ExceptionId.EX_META_CANNOT_MARSHAL_NESTED_ENTITY, record);
        }
    }

    /**
     * Marshal a {@link EntityDef}.
     *
     * @param record
     *            the record
     * @return string
     */
    public static String marshalEntity(EntityDef record) {
        try {
            JAXBElement<EntityDef> el = META_OBJECT_FACTORY.createEntityDef(record);
            StringWriter sw = new StringWriter();
            Marshaller m = META_CONTEXT.createMarshaller();
            // m.setSchema(META_SCHEMA);
            m.marshal(el, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall entity record from [{}]";
            LOGGER.warn(message, record, je);
            throw new MetadataException(message, je, ExceptionId.EX_META_CANNOT_MARSHAL_ENTITY, record);
        }
    }

    /**
     * Marshal a {@link EntityDef}.
     *
     * @param record
     *            the record
     * @return string
     */
    public static String marshalEntitiesGroup(EntitiesGroupDef record) {
        try {
            JAXBElement<EntitiesGroupDef> el = META_OBJECT_FACTORY.createEntitiesGroupDef(record);
            StringWriter sw = new StringWriter();
            Marshaller m = META_CONTEXT.createMarshaller();
            // m.setSchema(META_SCHEMA);
            m.marshal(el, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall entities group record from [{}]";
            LOGGER.warn(message, record, je);
            throw new MetadataException(message, je, ExceptionId.EX_META_CANNOT_MARSHAL_ENTITY, record);
        }
    }


    /**
     * Marshal a {@link RelationDef}.
     *
     * @param record
     *            the record
     * @return string
     */
    public static String marshalRelation(RelationDef record) {
        try {
            JAXBElement<RelationDef> el = META_OBJECT_FACTORY.createRelationDef(record);
            StringWriter sw = new StringWriter();
            Marshaller m = META_CONTEXT.createMarshaller();
            // m.setSchema(META_SCHEMA);
            m.marshal(el, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall entity record from [{}]";
            LOGGER.warn(message, record, je);
            throw new MetadataException(message, je, ExceptionId.EX_META_CANNOT_MARSHAL_RELATION, record);
        }
    }


    /**
     * Unmarshals a {@link CleanseFunctionDef}.
     *
     * @param s
     *            string
     * @return cleanse function record or null
     */
    public static CleanseFunctionDef unmarshalCleanseFunction(String s) {
        try {
            Unmarshaller u = JaxbUtils.getMetaContext().createUnmarshaller();
            u.setProperty(OBJECT_FACTORY_PARAM, META_OBJECT_FACTORY);
            return u.unmarshal(new StreamSource(new StringReader(s)), CleanseFunctionDef.class).getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall cleanse function from [{}]";
            LOGGER.warn(message, s, je);
            throw new MetadataException(message, je, ExceptionId.EX_META_CANNOT_UNMARSHAL_CLEANSE_FUNCTION, s);
        }
    }

    /**
     * Unmarshals a {@link CompositeCleanseFunctionDef}.
     *
     * @param s
     *            string
     * @return record or null
     */
    public static CompositeCleanseFunctionDef unmarshalCompositeCleanseFunction(String s) {
        try {
            Unmarshaller u = JaxbUtils.getMetaContext().createUnmarshaller();
            u.setProperty(OBJECT_FACTORY_PARAM, META_OBJECT_FACTORY);
            return u.unmarshal(new StreamSource(new StringReader(s)), CompositeCleanseFunctionDef.class).getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall composite cleanse function from [{}]";
            LOGGER.warn(message, s, je);
            throw new MetadataException(message, je, ExceptionId.EX_META_CANNOT_UNMARSHAL_COMPOSITE_CLEANSE_FUNCTION,
                    s);
        }
    }

    /**
     * Unmarshals a {@link CleanseFunctionGroupDef}.
     *
     * @param s
     *            string
     * @return record or null
     */
    public static CleanseFunctionGroupDef unmarshalCleanseFunctionGroup(String s) {
        try {
            Unmarshaller u = JaxbUtils.getMetaContext().createUnmarshaller();
            u.setProperty(OBJECT_FACTORY_PARAM, META_OBJECT_FACTORY);
            return u.unmarshal(new StreamSource(new StringReader(s)), CleanseFunctionGroupDef.class).getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall cleanse function group from [{}]";
            LOGGER.warn(message, s, je);
            throw new MetadataException(message, je, ExceptionId.EX_META_CANNOT_UNMARSHAL_CLEANSE_FUNCTION_GROUP, s);
        }
    }

    /**
     * Unmarshals a {@link SourceSystemDef}.
     *
     * @param s
     *            string
     * @return record or null
     */
    public static SourceSystemDef unmarshalSourceSystem(String s) {
        try {
            Unmarshaller u = JaxbUtils.getMetaContext().createUnmarshaller();
            u.setProperty(OBJECT_FACTORY_PARAM, META_OBJECT_FACTORY);
            return u.unmarshal(new StreamSource(new StringReader(s)), SourceSystemDef.class).getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall source system from [{}]";
            LOGGER.warn(message, s, je);
            throw new MetadataException(message, je, ExceptionId.EX_META_CANNOT_UNMARSHAL_SOURCE_SYSTEM, s);
        }
    }

    /**
     * Unmarshals a {@link EnumerationDataType}.
     *
     * @param s
     *            string
     * @return record or null
     */
    public static EnumerationDataType unmarshalEnumeration(String s) {
        try {
            Unmarshaller u = JaxbUtils.getMetaContext().createUnmarshaller();
            u.setProperty(OBJECT_FACTORY_PARAM, META_OBJECT_FACTORY);
            return u.unmarshal(new StreamSource(new StringReader(s)), EnumerationDataType.class).getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall enumeration from [{}]";
            LOGGER.warn(message, s, je);
            throw new MetadataException(message, je, ExceptionId.EX_META_CANNOT_UNMARSHAL_ENUMERATION, s);
        }
    }

    /**
     * Unmarshals a {@link LookupEntityDef}.
     *
     * @param s
     *            string
     * @return record or null
     */
    public static LookupEntityDef unmarshalLookupEntity(String s) {
        try {
            Unmarshaller u = JaxbUtils.getMetaContext().createUnmarshaller();
            u.setProperty(OBJECT_FACTORY_PARAM, META_OBJECT_FACTORY);
            return u.unmarshal(new StreamSource(new StringReader(s)), LookupEntityDef.class).getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall lookup entity from [{}]";
            LOGGER.warn(message, s, je);
            throw new MetadataException(message, je, ExceptionId.EX_META_CANNOT_UNMARSHAL_LOOKUP_ENTITY, s);
        }
    }

    /**
     * Unmarshals a {@link NestedEntityDef}.
     *
     * @param s
     *            string
     * @return record or null
     */
    public static NestedEntityDef unmarshalNestedEntity(String s) {
        try {
            Unmarshaller u = JaxbUtils.getMetaContext().createUnmarshaller();
            u.setProperty(OBJECT_FACTORY_PARAM, META_OBJECT_FACTORY);
            return u.unmarshal(new StreamSource(new StringReader(s)), NestedEntityDef.class).getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall nested entity from [{}]";
            LOGGER.warn(message, s, je);
            throw new MetadataException(message, je, ExceptionId.EX_META_CANNOT_UNMARSHAL_NESTED_ENTITY, s);
        }
    }

    /**
     * Unmarshals a {@link EntityDef}.
     *
     * @param s
     *            string
     * @return record or null
     */
    public static EntityDef unmarshalEntity(String s) {
        try {
            Unmarshaller u = JaxbUtils.getMetaContext().createUnmarshaller();
            u.setProperty(OBJECT_FACTORY_PARAM, META_OBJECT_FACTORY);
            return u.unmarshal(new StreamSource(new StringReader(s)), EntityDef.class).getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall entity from [{}]";
            LOGGER.warn(message, s, je);
            throw new MetadataException(message, je, ExceptionId.EX_META_CANNOT_UNMARSHAL_ENTITY, s);
        }
    }

    /**
     * Unmarshals a {@link RelationDef}.
     *
     * @param s
     *            string
     * @return record or null
     */
    public static RelationDef unmarshalRelation(String s) {
        try {
            Unmarshaller u = JaxbUtils.getMetaContext().createUnmarshaller();
            u.setProperty(OBJECT_FACTORY_PARAM, META_OBJECT_FACTORY);
            return u.unmarshal(new StreamSource(new StringReader(s)), RelationDef.class).getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall relation from [{}]";
            LOGGER.warn(message, s, je);
            throw new MetadataException(message, je, ExceptionId.EX_META_CANNOT_UNMARSHAL_RELATION, s);
        }
    }

    /**
     * Unmarshals a {@link EntitiesGroupDef}.
     * @param s string
     * @return record or null
     */
    public static EntitiesGroupDef unmarshalGroup(String s){
        try {
            Unmarshaller u = JaxbUtils.getMetaContext().createUnmarshaller();
            u.setProperty(OBJECT_FACTORY_PARAM, META_OBJECT_FACTORY);
            return u.unmarshal(new StreamSource(new StringReader(s)), EntitiesGroupDef.class).getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall group from [{}]";
            LOGGER.warn(message, s, je);
            throw new MetadataException(message, je, ExceptionId.EX_META_CANNOT_UNMARSHAL_GROUP, s);
        }
    }

    // -------------------------------------------------------------- End of
    // Meta model
    // --------------------------------------------------------------------
    /**
     * Gets meta context.
     *
     * @return the context or null.
     */
    public static JAXBContext getMetaContext() {
        return META_CONTEXT;
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
            //FIXME: Set server default timezone
            Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
            calendar.setTime((Date) value);
            //calendar.setTimeZone();
            result = calendarToXMGregorianCalendar(calendar);
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
     * Gets {@link Calendar} from {@link XMLGregorianCalendar}.
     *
     * @param xmlCalendar
     *            XML calendar
     * @return util calendar
     */
    public static Calendar xmlGregorianCalendarToCalendar(XMLGregorianCalendar xmlCalendar) {
        Calendar result = null;
        if (xmlCalendar != null) {
            result = xmlCalendar.toGregorianCalendar(TimeZone.getDefault(), null, null);
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
    public static com.unidata.mdm.data.ObjectFactory getDataObjectFactory() {
        return DATA_OBJECT_FACTORY;
    }

    /**
     * @return the metaObjectFactory
     */
    public static com.unidata.mdm.meta.ObjectFactory getMetaObjectFactory() {
        return META_OBJECT_FACTORY;
    }

    /**
     * @return the apiObjectFactory
     */
    public static com.unidata.mdm.api.ObjectFactory getApiObjectFactory() {
        return API_OBJECT_FACTORY;
    }

    /**
     * @return classifier factory
     */
    public static com.unidata.mdm.classifier.ObjectFactory getClassifierObjectFactory() {
        return CLASSIFIER_FACTORY;
    }

    /**
     * @return security factory
     */
    public static com.unidata.mdm.security.ObjectFactory getSecurityFactory() {
        return SECURITY_FACTORY;
    }

    /**
     * Creates model from uploaded document.
     * @param is the input stream
     * @return model
     * @throws Exception
     */
    public static Model createModelFromInputStream(InputStream is)
            throws IOException {

        Model model = null;
        // Reset stream, if it was already closed by CXF (string recognized)
        int available = is.available();
        if (available == 0) {
            is.reset();
            available = is.available();
        }

        try (ByteArrayOutputStream os = new ByteArrayOutputStream(available)) {

            byte[] buf = new byte[FileUtils.DEFAULT_BUFFER_SIZE];
            int count = -1;
            while ((count = is.read(buf, 0, buf.length)) != -1) {
                os.write(buf, 0, count);
            }

            model = unmarshalMetaModel(os.toString(StandardCharsets.UTF_8.name()));
        } catch (Exception exc) {
            throw exc;
        }finally {
			is.close();
		}

        return model;
    }

    public static FullClassifierDef createClassifierFromInputStream(InputStream is)
            throws IOException, JAXBException {

        FullClassifierDef classifier = null;
        // Reset stream, if it was already closed by CXF (string recognized)
        int available = is.available();
        if (available == 0) {
            is.reset();
            available = is.available();
        }

        try (ByteArrayOutputStream os = new ByteArrayOutputStream(available)) {

            byte[] buf = new byte[FileUtils.DEFAULT_BUFFER_SIZE];
            int count = -1;
            while ((count = is.read(buf, 0, buf.length)) != -1) {
                os.write(buf, 0, count);
            }

            JAXBContext context = JaxbUtils.getClassifierContext();
            classifier = context.createUnmarshaller().unmarshal(
                    new StreamSource(new StringReader(os.toString(StandardCharsets.UTF_8.name()))),
                    FullClassifierDef.class)
                        .getValue();
        }

        return classifier;
    }

    public static MeasurementValues createMeasurementValuesFromInputStream(InputStream is) throws IOException, JAXBException  {

        MeasurementValues measurementValues = null;
        // Reset stream, if it was already closed by CXF (string recognized)
        int available = is.available();
        if (available == 0) {
            is.reset();
            available = is.available();
        }

        try (ByteArrayOutputStream os = new ByteArrayOutputStream(available)) {
            byte[] buf = new byte[FileUtils.DEFAULT_BUFFER_SIZE];
            int count = -1;
            while ((count = is.read(buf, 0, buf.length)) != -1) {
                os.write(buf, 0, count);
            }

            JAXBContext context = JaxbUtils.getMetaContext();
            measurementValues = context.createUnmarshaller().unmarshal(
                    new StreamSource(new StringReader(os.toString(StandardCharsets.UTF_8.name()))),
                    MeasurementValues.class)
                        .getValue();
        }

        return measurementValues;
    }

    public static MatchingSettingsDef createMatchingUserSettingsFromInputStream(InputStream is)
            throws IOException, JAXBException  {

        MatchingSettingsDef measurementValues = null;
        // Reset stream, if it was already closed by CXF (string recognized)
        int available = is.available();
        if (available == 0) {
            is.reset();
            available = is.available();
        }

        try (ByteArrayOutputStream os = new ByteArrayOutputStream(available)) {
            byte[] buf = new byte[FileUtils.DEFAULT_BUFFER_SIZE];
            int count = -1;
            while ((count = is.read(buf, 0, buf.length)) != -1) {
                os.write(buf, 0, count);
            }

            JAXBContext context = JaxbUtils.getMatchContext();
            measurementValues = context.createUnmarshaller().unmarshal(
                    new StreamSource(new StringReader(os.toString(StandardCharsets.UTF_8.name()))),
                    MatchingSettingsDef.class)
                        .getValue();
        }

        return measurementValues;
    }

    public static String marshalMatchingUserSettings(MatchingSettingsDef source) {
    	if(source==null) {
    		return null;
    	}
        try {
            StringWriter sw = new StringWriter();
            MATCH_CONTEXT.createMarshaller().marshal(source, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall matching user settings from [{}]";
            LOGGER.warn(message, source, je);
            throw new DataProcessingException(message, je, ExceptionId.EX_MATCHING_CLUSTER_NOT_FOUND, source);
        }
    }

    public static String marshalMeasurementValues(MeasurementValues source)  {
    	if(source==null) {
    		return null;
    	}
        try {
            StringWriter sw = new StringWriter();
            JaxbUtils.getMetaContext().createMarshaller().marshal(source, sw);
            return sw.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall measurement values from [{}]";
            LOGGER.warn(message, source, je);
            throw new DataProcessingException(message, je, ExceptionId.EX_MEASUREMENT_MARSHAL_FAILED, source);
        }
    }

    public static String marshalSecurity(final Security security) {
        if (security == null) {
            return null;
        }
        try {
            final StringWriter stringWriter = new StringWriter();
            getSecurityContext().createMarshaller().marshal(security, stringWriter);
            return stringWriter.toString();
        } catch (JAXBException je) {
            final String message = "Cannot marshall security value from [{}]";
            LOGGER.warn(message, security, je);
            throw new DataProcessingException(message, je, ExceptionId.EX_MEASUREMENT_MARSHAL_FAILED, security);
        }
    }

    public static Security createSecurityFromFromInputStream(InputStream is) throws IOException, JAXBException {
        Security security = null;
        // Reset stream, if it was already closed by CXF (string recognized)
        int available = is.available();
        if (available == 0) {
            is.reset();
            available = is.available();
        }

        try (ByteArrayOutputStream os = new ByteArrayOutputStream(available)) {
            byte[] buf = new byte[FileUtils.DEFAULT_BUFFER_SIZE];
            int count = -1;
            while ((count = is.read(buf, 0, buf.length)) != -1) {
                os.write(buf, 0, count);
            }

            JAXBContext context = JaxbUtils.getSecurityContext();
            security = context.createUnmarshaller().unmarshal(
                    new StreamSource(new StringReader(os.toString(StandardCharsets.UTF_8.name()))),
                    Security.class
            ).getValue();
        }

        return security;
    }
}
