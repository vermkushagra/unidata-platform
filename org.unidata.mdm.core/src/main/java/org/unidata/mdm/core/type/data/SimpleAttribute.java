package org.unidata.mdm.core.type.data;

import org.unidata.mdm.search.type.FieldType;

/**
 * @author Mikhail Mikhailov
 * Simple attribute.
 */
public interface SimpleAttribute<T> extends SingleValueAttribute<T>, DisplayValue {
    /**
     * @author Mikhail Mikhailov
     * Denotes type of the contained data.
     */
    public enum DataType {
        /**
         * The string type.
         */
        STRING,
        /**
         * The integer type (long 8 bytes).
         */
        INTEGER,
        /**
         * The floating point type (double 8 bytes).
         */
        NUMBER,
        /**
         * The boolean type.
         */
        BOOLEAN,
        /**
         * Binary large object.
         */
        BLOB,
        /**
         * Character large object.
         */
        CLOB,
        /**
         * The date type.
         */
        DATE,
        /**
         * The time type.
         */
        TIME,
        /**
         * The timestamp type.
         */
        TIMESTAMP,
        /**
         * Link to a enum value.
         */
        ENUM,
        /**
         * Special href template, processed by get post-processor, type.
         */
        LINK,
        /**
         * Special type of number.
         */
        MEASURED;

        public FieldType toSearchType() {

            switch (this) {
            case BLOB: // filename is indexed
            case CLOB: // same as above
            case ENUM:
            case LINK:
            case STRING:
                return FieldType.STRING;
            case BOOLEAN:
                return FieldType.BOOLEAN;
            case DATE:
                return FieldType.DATE;
            case TIME:
                return FieldType.TIME;
            case TIMESTAMP:
                return FieldType.TIMESTAMP;
            case INTEGER:
                return FieldType.INTEGER;
            case MEASURED:
            case NUMBER:
                return FieldType.NUMBER;
            default:
                break;
            }

            return null;
        }
    }
    enum NarrowType {
        /**
         * Value for ES
         */
        ES,
        /**
         * return the same value as {#link getValue}
         */
        DEFAULT
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default AttributeType getAttributeType() {
        return AttributeType.SIMPLE;
    }
    /**
     * Gets type of contained data.
     * @return type
     */
    DataType getDataType();
    /**
     *
     * @return value for type
     */
    <V> V narrow(NarrowType type);
}
