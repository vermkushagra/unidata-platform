package org.unidata.mdm.data.serialization.jaxb;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.sun.xml.bind.InternalAccessorFactory;
import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import org.unidata.mdm.data.ArrayAttribute;
import org.unidata.mdm.data.ArrayDataType;
import org.unidata.mdm.data.SimpleAttribute;
import org.unidata.mdm.data.ValueDataType;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.system.exception.PlatformFailureException;

/**
 * @author Mikhail Mikhailov
 * Accessor factory for single attribute generated type.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ArrayAttributeAccessorFactory implements InternalAccessorFactory {

    /**
     * Constructor.
     */
    public ArrayAttributeAccessorFactory() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Accessor createFieldAccessor(Class valueType, Field f, boolean readOnly, boolean supressWarnings)
            throws JAXBException {
        return new ArrayAttributeFieldAccessor(valueType, f, readOnly, supressWarnings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Accessor createPropertyAccessor(Class valueType, Method getter, Method setter)
            throws JAXBException {
        return new ArrayAttributePropertyAccessor(valueType, getter, setter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Accessor createFieldAccessor(Class bean, Field f, boolean readOnly) throws JAXBException {
        return new ArrayAttributeFieldAccessor(bean, f, readOnly, true);
    };

    /**
     * Simple attribute properties accessor.
     * @author Mikhail Mikhailov
     */
    public static final class ArrayAttributePropertyAccessor extends Accessor<SimpleAttribute, Object> {
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
        private static final Map<String, ValueDataType> PROPERTIES_TO_TYPES = new HashMap<>();

        static {
            PROPERTIES_TO_TYPES.put("setBlobValue", ValueDataType.BLOB);
            PROPERTIES_TO_TYPES.put("setClobValue", ValueDataType.CLOB);
            PROPERTIES_TO_TYPES.put("setIntValue", ValueDataType.INTEGER);
            PROPERTIES_TO_TYPES.put("setDateValue", ValueDataType.DATE);
            PROPERTIES_TO_TYPES.put("setTimeValue", ValueDataType.TIME);
            PROPERTIES_TO_TYPES.put("setTimestampValue", ValueDataType.TIMESTAMP);
            PROPERTIES_TO_TYPES.put("setStringValue", ValueDataType.STRING);
            PROPERTIES_TO_TYPES.put("setNumberValue", ValueDataType.NUMBER);
            PROPERTIES_TO_TYPES.put("setBoolValue", ValueDataType.BOOLEAN);
            PROPERTIES_TO_TYPES.put("setMeasuredValue",ValueDataType.MEASURED);
        }

        /**
         * Constructor.
         * @param type the value type
         * @param getter the getter
         * @param setter the setter
         */
        protected ArrayAttributePropertyAccessor(Class type, Method getter, Method setter) {
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
    public static final class ArrayAttributeFieldAccessor extends Accessor<ArrayAttribute, Object> {
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
            private static final Map<String, ArrayDataType> FIELDS_TO_TYPES = new HashMap<>();

            static {
                FIELDS_TO_TYPES.put("intValue", ArrayDataType.INTEGER);
                FIELDS_TO_TYPES.put("dateValue", ArrayDataType.DATE);
                FIELDS_TO_TYPES.put("timeValue", ArrayDataType.TIME);
                FIELDS_TO_TYPES.put("timestampValue", ArrayDataType.TIMESTAMP);
                FIELDS_TO_TYPES.put("stringValue", ArrayDataType.STRING);
                FIELDS_TO_TYPES.put("numberValue", ArrayDataType.NUMBER);
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
            public ArrayAttributeFieldAccessor(Class type, Field field, boolean isReadOnly, boolean supressWarnings) {
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
                            throw new PlatformFailureException(message, DataExceptionIds.EX_SYSTEM_JAXB_CANNOT_SET_FIELD_PERMISSION, field.getName());
                        }
                    }
                }
            }

            /**
             * Field get.
             */
            @Override
            public Object get(ArrayAttribute bean) throws AccessorException {
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
            public void set(ArrayAttribute bean, Object value) throws AccessorException {
                if (!isReadOnly) {
                    try {
                        field.set(bean, value);
                        ArrayDataType type = FIELDS_TO_TYPES.get(field.getName());

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
