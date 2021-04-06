package com.unidata.mdm.api.wsdl.v3;

import javax.xml.bind.JAXBException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.sun.xml.bind.InternalAccessorFactory;
import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.data.v3.SimpleAttribute;
import com.unidata.mdm.data.v3.ValueDataType;

/**
 * @author Mikhail Mikhailov
 * Accessor factory for single attribute generated type.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SimpleAttributeAccessorFactory implements InternalAccessorFactory {

    /**
     * Constructor.
     */
    public SimpleAttributeAccessorFactory() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Accessor createFieldAccessor(Class valueType, Field f, boolean readOnly, boolean supressWarnings)
            throws JAXBException {
        return new SimpleAttributeFieldAccessor(valueType, f, readOnly, supressWarnings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Accessor createPropertyAccessor(Class valueType, Method getter, Method setter)
            throws JAXBException {
        return new SimpleAttributePropertyAccessor(valueType, getter, setter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Accessor createFieldAccessor(Class bean, Field f, boolean readOnly) throws JAXBException {
        return new SimpleAttributeFieldAccessor(bean, f, readOnly, true);
    };

    /**
     * Simple attribute properties accessor.
     * @author Mikhail Mikhailov
     */
    public static final class SimpleAttributePropertyAccessor extends Accessor<SimpleAttribute, Object> {
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
         * &lt;xs:element name="blobValue" type="data:BlobValue" nillable="false" /&gt;
         * &lt;xs:element name="clobValue" type="data:ClobValue" nillable="false" /&gt;
         * &lt;xs:element name="intValue" type="xs:long" nillable="false" /&gt;
         * &lt;xs:element name="dateValue" type="xs:date" nillable="false" /&gt;
         * &lt;xs:element name="timeValue" type="xs:time" nillable="false" /&gt;
         * &lt;xs:element name="timestampValue" type="xs:dateTime" nillable="false" /&gt;
         * &lt;xs:element name="stringValue" type="xs:string" nillable="false" /&gt;
         * &lt;xs:element name="numberValue" type="xs:double" nillable="false" /&gt;
         * &lt;xs:element name="boolValue" type="xs:boolean" nillable="false" /&gt;
         */
        @SuppressWarnings("serial")
        private static final Map<String, ValueDataType> PROPERTIES_TO_TYPES
            = new HashMap<String, ValueDataType>() {
            {
                put("setBlobValue", ValueDataType.BLOB);
                put("setClobValue", ValueDataType.CLOB);
                put("setIntValue", ValueDataType.INTEGER);
                put("setDateValue", ValueDataType.DATE);
                put("setTimeValue", ValueDataType.TIME);
                put("setTimestampValue", ValueDataType.TIMESTAMP);
                put("setStringValue", ValueDataType.STRING);
                put("setNumberValue", ValueDataType.NUMBER);
                put("setBoolValue", ValueDataType.BOOLEAN);
                put("setMeasuredValue",ValueDataType.MEASURED);
            }
        };

        /**
         * Constructor.
         * @param type the value type
         * @param getter the getter
         * @param setter the setter
         */
        protected SimpleAttributePropertyAccessor(Class type, Method getter, Method setter) {
            super(type);
            this.getter = getter;
            this.setter = setter;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object get(SimpleAttribute bean) throws AccessorException {
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
        public void set(SimpleAttribute bean, Object value) throws AccessorException {
            try {
                setter.invoke(bean, value);
                ValueDataType type = PROPERTIES_TO_TYPES.get(getter.getName());

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
    public static final class SimpleAttributeFieldAccessor extends Accessor<SimpleAttribute, Object> {
            /**
             * Maps values to types.
             * &lt;xs:element name="blobValue" type="data:BlobValue" nillable="false" /&gt;
             * &lt;xs:element name="clobValue" type="data:ClobValue" nillable="false" /&gt;
             * &lt;xs:element name="intValue" type="xs:long" nillable="false" /&gt;
             * &lt;xs:element name="dateValue" type="xs:date" nillable="false" /&gt;
             * &lt;xs:element name="timeValue" type="xs:time" nillable="false" /&gt;
             * &lt;xs:element name="timestampValue" type="xs:dateTime" nillable="false" /&gt;
             * &lt;xs:element name="stringValue" type="xs:string" nillable="false" /&gt;
             * &lt;xs:element name="numberValue" type="xs:double" nillable="false" /&gt;
             * &lt;xs:element name="boolValue" type="xs:boolean" nillable="false" /&gt;
             */
            @SuppressWarnings("serial")
            private static final Map<String, ValueDataType> FIELDS_TO_TYPES
                = new HashMap<String, ValueDataType>() {
                {
                    put("blobValue", ValueDataType.BLOB);
                    put("clobValue", ValueDataType.CLOB);
                    put("intValue", ValueDataType.INTEGER);
                    put("dateValue", ValueDataType.DATE);
                    put("timeValue", ValueDataType.TIME);
                    put("timestampValue", ValueDataType.TIMESTAMP);
                    put("stringValue", ValueDataType.STRING);
                    put("numberValue", ValueDataType.NUMBER);
                    put("boolValue", ValueDataType.BOOLEAN);
                    put("measuredValue", ValueDataType.MEASURED);
                }
            };
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
            public SimpleAttributeFieldAccessor(Class type, Field field, boolean isReadOnly, boolean supressWarnings) {
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
            public Object get(SimpleAttribute bean) throws AccessorException {
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
            public void set(SimpleAttribute bean, Object value) throws AccessorException {
                if (!isReadOnly) {
                    try {
                        field.set(bean, value);
                        ValueDataType type = FIELDS_TO_TYPES.get(field.getName());

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
