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

package org.unidata.mdm.meta.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unidata.mdm.core.util.FileUtils;
import org.unidata.mdm.meta.EntitiesGroupDef;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.EnumerationDataType;
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.MeasurementValues;
import org.unidata.mdm.meta.Model;
import org.unidata.mdm.meta.NestedEntityDef;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.SourceSystemDef;
import org.unidata.mdm.meta.exception.MetaExceptionIds;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.util.AbstractJaxbUtils;

import com.sun.xml.bind.api.JAXBRIContext;

/**
 * @author Mikhail Mikhailov on Oct 4, 2019
 * JAXB stuff, related to meta model.
 * TODO: Get rid of JAXB.
 */
public final class MetaJaxbUtils extends AbstractJaxbUtils {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaJaxbUtils.class);
    /**
     * Object factory param.
     */
    private static final String OBJECT_FACTORY_PARAM = "com.sun.xml.bind.ObjectFactory";
    /**
     * Meta root package.
     */
    private static final String META_ROOT_PACKAGE = "org.unidata.mdm.meta";
    /**
     * Meta model namespace URI.
     */
    public static final String META_URI = "http://meta.mdm.unidata.org/";
    /**
     * XSD dateTime date format.
     */
    public static final String XSD_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";
    /**
     * Meta object factory.
     */
    private static final org.unidata.mdm.meta.type.serialization.impl.ObjectFactoryEx META_OBJECT_FACTORY
            = new org.unidata.mdm.meta.type.serialization.impl.ObjectFactoryEx();
    /**
     * Meta unmarshaller.
     */
    private static JAXBContext META_CONTEXT;
    /**
     * Static initializer.
     */
    static {
        try {

            final Map<String, Object> defaultContextProperties = new HashMap<>();
            defaultContextProperties.put(JAXBRIContext.XMLACCESSORFACTORY_SUPPORT, Boolean.TRUE);
            defaultContextProperties.put(JAXBRIContext.DISABLE_XML_SECURITY, Boolean.TRUE);
            defaultContextProperties.put(JAXBRIContext.SUPRESS_ACCESSOR_WARNINGS, Boolean.TRUE);

            META_CONTEXT = JAXBContext.newInstance(META_ROOT_PACKAGE,
                    Thread.currentThread().getContextClassLoader(),
                    defaultContextProperties);

        } catch (Exception e) {
            final String message = "Meta JAXB context init failure. Exiting. [{}]";
            LOGGER.error(message, e);
            throw new PlatformFailureException(message, e, MetaExceptionIds.EX_META_JAXB_CONTEXT_INIT_FAILURE);
        }
    }
    /**
     * Constructor.
     */
    private MetaJaxbUtils() {
        super();
    }
    /**
     * Gets meta context.
     *
     * @return the context or null.
     */
    public static JAXBContext getMetaContext() {
        return META_CONTEXT;
    }
    /**
     * @return the metaObjectFactory
     */
    public static org.unidata.mdm.meta.ObjectFactory getMetaObjectFactory() {
        return META_OBJECT_FACTORY;
    }
    /**
     * Unmarshal model.
     *
     * @param s source
     * @return model
     */
    public static Model unmarshalMetaModel(String s) {
        try {
            return (Model) META_CONTEXT.createUnmarshaller().unmarshal(new StringReader(s));
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall model from [{}]";
            LOGGER.error(message, s, je);
            throw new PlatformFailureException(message, je, MetaExceptionIds.EX_META_CANNOT_UNMARSHAL_MODEL);
        }
    }

    /**
     * Marshal the model.
     *
     * @param m the model
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
            throw new PlatformFailureException(message, je, MetaExceptionIds.EX_META_CANNOT_MARSHAL_MODEL);
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
     * @param record the record
     * @return string
     */
  //TODO: Commented out in scope of UN-11834. Move to DQ.
    /*
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
    */
    /**
     * Marshal a {@link SourceSystemDef}.
     *
     * @param record the record
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
            throw new PlatformFailureException(message, je, MetaExceptionIds.EX_META_CANNOT_MARSHAL_SOURCE_SYSTEM, record);
        }
    }

    /**
     * Marshal a {@link EnumerationDataType}.
     *
     * @param record the record
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
            throw new PlatformFailureException(message, je, MetaExceptionIds.EX_META_CANNOT_MARSHAL_ENUMERATION, record);
        }
    }

    /**
     * Marshal a {@link LookupEntityDef}.
     *
     * @param record the record
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
            throw new PlatformFailureException(message, je, MetaExceptionIds.EX_META_CANNOT_MARSHAL_LOOKUP_ENTITY, record);
        }
    }

    /**
     * Marshal a {@link NestedEntityDef}.
     *
     * @param record the record
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
            throw new PlatformFailureException(message, je, MetaExceptionIds.EX_META_CANNOT_MARSHAL_NESTED_ENTITY, record);
        }
    }

    /**
     * Marshal a {@link EntityDef}.
     *
     * @param record the record
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
            throw new PlatformFailureException(message, je, MetaExceptionIds.EX_META_CANNOT_MARSHAL_ENTITY, record);
        }
    }

    /**
     * Marshal a {@link EntityDef}.
     *
     * @param record the record
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
            throw new PlatformFailureException(message, je, MetaExceptionIds.EX_META_CANNOT_MARSHAL_ENTITIES_GROUP, record);
        }
    }


    /**
     * Marshal a {@link RelationDef}.
     *
     * @param record the record
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
            throw new PlatformFailureException(message, je, MetaExceptionIds.EX_META_CANNOT_MARSHAL_RELATION, record);
        }
    }


    /**
     * Unmarshals a {@link CleanseFunctionDef}.
     *
     * @param s string
     * @return cleanse function record or null
     */
  //TODO: Commented out in scope of UN-11834. Move to DQ.
    /*
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
    */
    /**
     * Unmarshals a {@link CompositeCleanseFunctionDef}.
     *
     * @param s string
     * @return record or null
     */
  //TODO: Commented out in scope of UN-11834. Move to DQ.
    /*
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
    */
    /**
     * Unmarshals a {@link CleanseFunctionGroupDef}.
     *
     * @param s string
     * @return record or null
     */
  //TODO: Commented out in scope of UN-11834. Move to DQ.
    /*
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
    */
    /**
     * Unmarshals a {@link SourceSystemDef}.
     *
     * @param s string
     * @return record or null
     */
    public static SourceSystemDef unmarshalSourceSystem(String s) {
        try {
            Unmarshaller u = META_CONTEXT.createUnmarshaller();
            u.setProperty(OBJECT_FACTORY_PARAM, META_OBJECT_FACTORY);
            return u.unmarshal(new StreamSource(new StringReader(s)), SourceSystemDef.class).getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall source system from [{}]";
            LOGGER.warn(message, s, je);
            throw new PlatformFailureException(message, je, MetaExceptionIds.EX_META_CANNOT_UNMARSHAL_SOURCE_SYSTEM, s);
        }
    }

    /**
     * Unmarshals a {@link EnumerationDataType}.
     *
     * @param s string
     * @return record or null
     */
    public static EnumerationDataType unmarshalEnumeration(String s) {
        try {
            Unmarshaller u = META_CONTEXT.createUnmarshaller();
            u.setProperty(OBJECT_FACTORY_PARAM, META_OBJECT_FACTORY);
            return u.unmarshal(new StreamSource(new StringReader(s)), EnumerationDataType.class).getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall enumeration from [{}]";
            LOGGER.warn(message, s, je);
            throw new PlatformFailureException(message, je, MetaExceptionIds.EX_META_CANNOT_UNMARSHAL_ENUMERATION, s);
        }
    }

    /**
     * Unmarshals a {@link LookupEntityDef}.
     *
     * @param s string
     * @return record or null
     */
    public static LookupEntityDef unmarshalLookupEntity(String s) {
        try {
            Unmarshaller u = META_CONTEXT.createUnmarshaller();
            u.setProperty(OBJECT_FACTORY_PARAM, META_OBJECT_FACTORY);
            return u.unmarshal(new StreamSource(new StringReader(s)), LookupEntityDef.class).getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall lookup entity from [{}]";
            LOGGER.warn(message, s, je);
            throw new PlatformFailureException(message, je, MetaExceptionIds.EX_META_CANNOT_UNMARSHAL_LOOKUP_ENTITY, s);
        }
    }

    /**
     * Unmarshals a {@link NestedEntityDef}.
     *
     * @param s string
     * @return record or null
     */
    public static NestedEntityDef unmarshalNestedEntity(String s) {
        try {
            Unmarshaller u = META_CONTEXT.createUnmarshaller();
            u.setProperty(OBJECT_FACTORY_PARAM, META_OBJECT_FACTORY);
            return u.unmarshal(new StreamSource(new StringReader(s)), NestedEntityDef.class).getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall nested entity from [{}]";
            LOGGER.warn(message, s, je);
            throw new PlatformFailureException(message, je, MetaExceptionIds.EX_META_CANNOT_UNMARSHAL_NESTED_ENTITY, s);
        }
    }

    /**
     * Unmarshals a {@link EntityDef}.
     *
     * @param s string
     * @return record or null
     */
    public static EntityDef unmarshalEntity(String s) {
        try {
            Unmarshaller u = META_CONTEXT.createUnmarshaller();
            u.setProperty(OBJECT_FACTORY_PARAM, META_OBJECT_FACTORY);
            return u.unmarshal(new StreamSource(new StringReader(s)), EntityDef.class).getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall entity from [{}]";
            LOGGER.warn(message, s, je);
            throw new PlatformFailureException(message, je, MetaExceptionIds.EX_META_CANNOT_UNMARSHAL_ENTITY, s);
        }
    }

    /**
     * Unmarshals a {@link RelationDef}.
     *
     * @param s string
     * @return record or null
     */
    public static RelationDef unmarshalRelation(String s) {
        try {
            Unmarshaller u = META_CONTEXT.createUnmarshaller();
            u.setProperty(OBJECT_FACTORY_PARAM, META_OBJECT_FACTORY);
            return u.unmarshal(new StreamSource(new StringReader(s)), RelationDef.class).getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall relation from [{}]";
            LOGGER.warn(message, s, je);
            throw new PlatformFailureException(message, je, MetaExceptionIds.EX_META_CANNOT_UNMARSHAL_RELATION, s);
        }
    }

    /**
     * Unmarshals a {@link EntitiesGroupDef}.
     *
     * @param s string
     * @return record or null
     */
    public static EntitiesGroupDef unmarshalGroup(String s) {
        try {
            Unmarshaller u = META_CONTEXT.createUnmarshaller();
            u.setProperty(OBJECT_FACTORY_PARAM, META_OBJECT_FACTORY);
            return u.unmarshal(new StreamSource(new StringReader(s)), EntitiesGroupDef.class).getValue();
        } catch (JAXBException je) {
            final String message = "Cannot unmarshall group from [{}]";
            LOGGER.warn(message, s, je);
            throw new PlatformFailureException(message, je, MetaExceptionIds.EX_META_CANNOT_UNMARSHAL_GROUP, s);
        }
    }
    /**
     * Creates model from uploaded document.
     *
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

            byte[] buf = new byte[8192];
            int count = -1;
            while ((count = is.read(buf, 0, buf.length)) != -1) {
                os.write(buf, 0, count);
            }

            model = unmarshalMetaModel(os.toString(StandardCharsets.UTF_8.name()));
        } catch (Exception exc) {
            throw exc;
        } finally {
            is.close();
        }

        return model;
    }

    public static MeasurementValues createMeasurementValuesFromInputStream(InputStream is) throws IOException, JAXBException {

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

            JAXBContext context = getMetaContext();
            measurementValues = context.createUnmarshaller().unmarshal(
                    new StreamSource(new StringReader(os.toString(StandardCharsets.UTF_8.name()))),
                    MeasurementValues.class)
                    .getValue();
        }

        return measurementValues;
    }
}
