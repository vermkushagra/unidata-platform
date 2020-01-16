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

package org.unidata.mdm.soap.core.util;

import com.sun.xml.bind.api.JAXBRIContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unidata.mdm.soap.core.exception.CoreSoapExceptionIds;
import org.unidata.mdm.system.exception.PlatformBusinessException;

import javax.xml.bind.JAXBContext;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.HashMap;
import java.util.Map;

public final class SecurityJaxbUtils {
    private SecurityJaxbUtils() {}

   private static final String EUROPE_MOSCOW = "Europe/Moscow";

   /**
    * Object factory param.
    */
   private static final String OBJECT_FACTORY_PARAM = "com.sun.xml.bind.ObjectFactory";
   /**
    * Logger.
    */
   private static final Logger LOGGER = LoggerFactory.getLogger(SecurityJaxbUtils.class);
   /**
    * Data root package.
    */
   private static final String SECURITY_ROOT_PACKAGE = "org.unidata.mdm.security";

   /**
    * Security object factory.
    */
   private static final org.unidata.mdm.security.v1.ObjectFactory SECURITY_OBJECT_FACTORY
       = new org.unidata.mdm.security.v1.ObjectFactory();

   /**
    * Security unmarshaller.
    */
   private static JAXBContext SECURITY_CONTEXT;


   /**
    * Datatype factory, which is not guaranteed to be thread safe
    * (implementation specific).
    */
   private static ThreadLocal<DatatypeFactory> DATATYPE_FACTORY = new ThreadLocal<DatatypeFactory>() {
       /**
        * @see ThreadLocal#initialValue()
        */
       @Override
       protected DatatypeFactory initialValue() {
           try {
               return DatatypeFactory.newInstance();
           } catch (DatatypeConfigurationException e) {
               final String message = "DatatypeFactory initialization failure. [{}]";
               LOGGER.error(message, e);
               throw new PlatformBusinessException(
                       message,
                       CoreSoapExceptionIds.EX_SYSTEM_JAXB_TYPE_FACTORY_INIT_FAILURE,
                       e
               );
           }
       }
   };

   static {
       try {

           final Map<String, Object> defaultContextProperties = new HashMap<>();
           defaultContextProperties.put(JAXBRIContext.XMLACCESSORFACTORY_SUPPORT, Boolean.TRUE);
           defaultContextProperties.put(JAXBRIContext.DISABLE_XML_SECURITY, Boolean.TRUE);
           defaultContextProperties.put(JAXBRIContext.SUPRESS_ACCESSOR_WARNINGS, Boolean.TRUE);

           SECURITY_CONTEXT = JAXBContext.newInstance(SECURITY_ROOT_PACKAGE,
                   Thread.currentThread().getContextClassLoader(),
                   defaultContextProperties);

       } catch (Exception e) {
           final String message = "JAXB failure. Exiting. [{}]";
           LOGGER.error(message, e);
           throw new PlatformBusinessException(message, e, CoreSoapExceptionIds.EX_SYSTEM_JAXB_CONTEXT_INIT_FAILURE);
       }
   }

   /**
    * Gets data context.
    *
    * @return the context or null.
    */
   public static JAXBContext getSecurityContext() {
       return SECURITY_CONTEXT;
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
    * @return the dataObjectFactory
    */
   public static org.unidata.mdm.security.v1.ObjectFactory getSecurityObjectFactory() {
       return SECURITY_OBJECT_FACTORY;
   }
}
