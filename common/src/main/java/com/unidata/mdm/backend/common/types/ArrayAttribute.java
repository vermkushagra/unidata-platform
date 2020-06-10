package com.unidata.mdm.backend.common.types;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.types.impl.DateArrayValue;
import com.unidata.mdm.backend.common.types.impl.IntegerArrayValue;
import com.unidata.mdm.backend.common.types.impl.NumberArrayValue;
import com.unidata.mdm.backend.common.types.impl.StringArrayValue;
import com.unidata.mdm.backend.common.types.impl.TimeArrayValue;
import com.unidata.mdm.backend.common.types.impl.TimestampArrayValue;

/**
 * @author Mikhail Mikhailov
 * Array attribute.
 */
public interface ArrayAttribute<T> extends Attribute, Iterable<ArrayValue<T>> {
    /**
     * @author Mikhail Mikhailov
     * Denotes type of the contained data.
     */
    public enum ArrayDataType {
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
        TIMESTAMP
    }
    /**
     * {@inheritDoc}
     */
    @Override
    default AttributeType getAttributeType() {
        return AttributeType.ARRAY;
    }
    /**
     * Gets type of contained data.
     * @return type
     */
    ArrayDataType getDataType();
    /**
     * Gets contained value.
     * @return value.
     */
    List<ArrayValue<T>> getValue();
    /**
     * Sets the value.
     * @param value to set
     */
    void setValue(List<ArrayValue<T>> value);
    /**
     * Casts and sets the value.
     * @param value to set
     */
    @SuppressWarnings("unchecked")
    default void castValue(Object[] value) {
        if (Objects.isNull(value) || value.length == 0) {
            return;
        }

        final List<ArrayValue<T>> result = new ArrayList<>(value.length);
        for (int i = 0; i < value.length; i++) {

            Object v = value[i];
            switch (getDataType()) {
            case DATE:
                result.add((ArrayValue<T>) new DateArrayValue((LocalDate) v));
                break;
            case INTEGER:
                result.add((ArrayValue<T>) new IntegerArrayValue((Long) v));
                break;
            case NUMBER:
                result.add((ArrayValue<T>) new NumberArrayValue((Double) v));
                break;
            case STRING:
                result.add((ArrayValue<T>) new StringArrayValue((String) v));
                break;
            case TIME:
                result.add((ArrayValue<T>) new TimeArrayValue((LocalTime) v));
                break;
            case TIMESTAMP:
                result.add((ArrayValue<T>) new TimestampArrayValue((LocalDateTime) v));
                break;
            }
        }

        setValue(result);
    }

    /**
     * Casts and sets the value.
     * @param value to set
     */
    @SuppressWarnings("unchecked")
    default void castValue(List<?>  value) {
        if (Objects.isNull(value) || value.isEmpty()) {
            return;
        }

        final List<ArrayValue<T>> result = new ArrayList<>(value.size());
        for (int i = 0; i < value.size(); i++) {

            Object v = value.get(i);
            switch (getDataType()) {
            case DATE:
                result.add((ArrayValue<T>) new DateArrayValue((LocalDate) v));
                break;
            case INTEGER:
                result.add((ArrayValue<T>) new IntegerArrayValue((Long) v));
                break;
            case NUMBER:
                result.add((ArrayValue<T>) new NumberArrayValue((Double) v));
                break;
            case STRING:
                result.add((ArrayValue<T>) new StringArrayValue((String) v));
                break;
            case TIME:
                result.add((ArrayValue<T>) new TimeArrayValue((LocalTime) v));
                break;
            case TIMESTAMP:
                result.add((ArrayValue<T>) new TimestampArrayValue((LocalDateTime) v));
                break;
            }
        }

        setValue(result);
    }

    /**
     * Empty mark.
     * @return true, if empty, false otherwise
     */
    @Override
    default boolean isEmpty() {
        return getValue() == null || getValue().isEmpty();
    }

    /**
     * Returns the underlaying objects as a collection.
     * @return collection
     */
    @SuppressWarnings("unchecked")
    default<V> List<V> toList() {
        return getValue().stream()
                .map(ArrayValue::getValue)
                .map(v -> (V) v)
                .collect(Collectors.toList());
    }

    /**
     * Collects values to array.
     * @return array of values
     */
    default Object[] toArray() {

        if (isEmpty()) {
            return null;
        }

        return toList().toArray();
    }

    /**
     * Collects values to array.
     * @param cl array class for result
     * @return array of values
     */
    default T[] toArray(Class<T[]> cl) {
        Object[] array = toArray();
        if(array != null){
            return Arrays.copyOf(array, array.length, cl);
        } else {
            return null;
        }
    }
}
