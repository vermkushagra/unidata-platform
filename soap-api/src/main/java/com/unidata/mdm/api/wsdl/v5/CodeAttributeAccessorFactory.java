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

package com.unidata.mdm.api.wsdl.v5;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.sun.xml.bind.InternalAccessorFactory;
import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.data.v5.CodeAttribute;
import com.unidata.mdm.data.v5.CodeDataType;

/**
 * @author Mikhail Mikhailov
 * Accessor factory for single attribute generated type.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CodeAttributeAccessorFactory implements InternalAccessorFactory {

    /**
     * Constructor.
     */
    public CodeAttributeAccessorFactory() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Accessor createFieldAccessor(Class valueType, Field f, boolean readOnly, boolean supressWarnings)
            throws JAXBException {
        return new CodeAttributeFieldAccessor(valueType, f, readOnly, supressWarnings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Accessor createPropertyAccessor(Class valueType, Method getter, Method setter)
            throws JAXBException {
        return new CodeAttributePropertyAccessor(valueType, getter, setter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Accessor createFieldAccessor(Class bean, Field f, boolean readOnly) throws JAXBException {
        return new CodeAttributeFieldAccessor(bean, f, readOnly, true);
    };

    /**
     * Simple attribute properties accessor.
     * @author Mikhail Mikhailov
     */
    public static final class CodeAttributePropertyAccessor extends Accessor<CodeAttribute, Object> {
        /**
         * Getter method
         */
        private final Method getter;
        /**
         * Setter method.
         */
        private final Method setter;
        /**
         * Maps values to types.
         * &lt;xs:element name="intValue" type="xs:long" nillable="false" /&gt;
         * &lt;xs:element name="stringValue" type="xs:string" nillable="false" /&gt;
         */
        private static final Map<String, CodeDataType> PROPERTIES_TO_TYPES = new HashMap<>();

        static {
            PROPERTIES_TO_TYPES.put("setIntValue", CodeDataType.INTEGER);
            PROPERTIES_TO_TYPES.put("setStringValue", CodeDataType.STRING);
        }

        /**
         * Constructor.
         * @param type the value type
         * @param getter the getter
         * @param setter the setter
         */
        protected CodeAttributePropertyAccessor(Class type, Method getter, Method setter) {
            super(type);
            this.getter = getter;
            this.setter = setter;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object get(CodeAttribute bean) throws AccessorException {
            try {
                return getter.invoke(bean);
            } catch (Exception e) {
                throw new AccessorException("Failed to access propery: " + getter.toGenericString(), e);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void set(CodeAttribute bean, Object value) throws AccessorException {
            try {
                setter.invoke(bean, value);
                CodeDataType type = PROPERTIES_TO_TYPES.get(getter.getName());

                // Known field, set type for this
                if (type != null) {
                    bean.setType(type);
                }
            } catch (Exception e) {
                throw new AccessorException("Failed to mutate property: " + setter.toGenericString(), e);
            }
        }

    };


    /**
     * Simple attribute fields accessor.
     * @author Mikhail Mikhailov
     */
    public static final class CodeAttributeFieldAccessor extends Accessor<CodeAttribute, Object> {
            /**
             * Maps values to types.
             * &lt;xs:element name="intValue" type="xs:long" nillable="false" /&gt;
             * &lt;xs:element name="stringValue" type="xs:string" nillable="false" /&gt;
             */
            private static final Map<String, CodeDataType> FIELDS_TO_TYPES = new HashMap<>();

            static {
                FIELDS_TO_TYPES.put("intValue", CodeDataType.INTEGER);
                FIELDS_TO_TYPES.put("stringValue", CodeDataType.STRING);
            }
            /**
             * The field to serve.
             */
            private final Field field;
            /**
             * RO flag.
             */
            private final boolean isReadOnly;
            /**
             * Constructor.
             * @param type the type
             * @param field the field
             * @param isReadOnly read only or not
             * @param supressWarnings supress warnings
             */
            public CodeAttributeFieldAccessor(Class type, Field field, boolean isReadOnly, boolean supressWarnings) {
                super(type);
                this.field = field;
                this.isReadOnly = isReadOnly;

                int mod = field.getModifiers();
                if (supressWarnings) {
                    if (!Modifier.isPublic(mod) || Modifier.isFinal(mod) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
                        try {
                            // attempt to make it accessible, but do so in the security context of the calling application.
                            // don't do this in the doPrivilege block, as that would create a security hole for anyone
                            // to make any field accessible.
                            field.setAccessible(true);
                        } catch (SecurityException e) {
                            // Fail
                            final String message = "Cannot set field [{}] accessible for marshaling/unmarshaling!";
                            throw new DataProcessingException(message, ExceptionId.EX_SYSTEM_JAXB_CANNOT_SET_FIELD_PERMISSION, field.getName());
                        }
                    }
                }
            }

            /**
             * Field get.
             */
            @Override
            public Object get(CodeAttribute bean) throws AccessorException {
                try {
                    return field.get(bean);
                } catch (Exception e) {
                    throw new AccessorException("Failed to access field: " + field.toGenericString(), e);
                }
            }

            /**
             * Field set
             */
            @Override
            public void set(CodeAttribute bean, Object value) throws AccessorException {
                if (!isReadOnly) {
                    try {
                        field.set(bean, value);
                        CodeDataType type = FIELDS_TO_TYPES.get(field.getName());

                        // Known field, set type for this
                        if (type != null) {
                            bean.setType(type);
                        }
                    } catch (Exception e) {
                        throw new AccessorException("Failed to mutate field: " + field.toGenericString(), e);
                    }
                }
            }
    }
}
