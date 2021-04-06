package com.unidata.mdm.backend.service.dump.protostuff;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.impl.AbstractAttribute;
import com.unidata.mdm.backend.service.dump.types.VerifyableArrayAttribute;
import com.unidata.mdm.backend.util.ByteUtils;

import io.protostuff.Input;
import io.protostuff.Output;
import io.protostuff.Schema;

/**
 * @author Mikhail Mikhailov
 * Array attributes.
 */
public class ArrayAttributeSchema implements Schema<ArrayAttribute<?>> {
    /**
     * {@inheritDoc}
     */
    @Override
    public String getFieldName(int number) {
        return SchemaFields.intToString(number);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int getFieldNumber(String name) {
        return SchemaFields.stringToInt(name);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInitialized(ArrayAttribute<?> message) {

        if (message instanceof VerifyableArrayAttribute<?>) {
            return ((VerifyableArrayAttribute<?>) message).isValid();
        }

        return true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayAttribute<?> newMessage() {
        // Should not be called.
        return null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String messageName() {
        return ArrayAttribute.class.getSimpleName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String messageFullName() {
        return ArrayAttribute.class.getName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? super ArrayAttribute<?>> typeClass() {
        return ArrayAttribute.class;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void mergeFrom(Input input, ArrayAttribute<?> message) throws IOException {

        List<Object> objects = new ArrayList<>();
        while (true) {
            int field = input.readFieldNumber(this);
            switch (field) {
            case 0:
                message.castValue(objects);
                return;
            case SchemaFields.FIELD_NAME_VAL:
                ((AbstractAttribute) message).setName(input.readString());
                break;
            case SchemaFields.STRING_ARRAY_VALUE_VAL:
                objects.add(input.readString());
                break;
            case SchemaFields.INTEGER_ARRAY_VALUE_VAL:
                objects.add(input.readInt64());
                break;
            case SchemaFields.NUMBER_ARRAY_VALUE_VAL:
                objects.add(input.readDouble());
                break;
            case SchemaFields.DATE_ARRAY_VALUE_VAL:
                objects.add(ByteUtils.unpackLocalDate(input.readByteArray()));
                break;
            case SchemaFields.TIME_ARRAY_VALUE_VAL:
                objects.add(ByteUtils.unpackLocalTime(input.readByteArray()));
                break;
            case SchemaFields.TIMESTAMP_ARRAY_VALUE_VAL:
                objects.add(ByteUtils.unpackLocalDateTime(input.readByteArray()));
                break;
            default:
                break;
            }
        }

    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void writeTo(Output output, ArrayAttribute<?> message) throws IOException {

        output.writeString(SchemaFields.FIELD_NAME.getValue(), message.getName(), false);
        if (message.isEmpty()) {
            return;
        }

        switch (message.getDataType()) {
        case STRING:
            Object[] stringVal = ((ArrayAttribute<String>) message).toArray();
            for (int i = 0; i < stringVal.length; i++) {
                output.writeString(SchemaFields.STRING_ARRAY_VALUE.getValue(), stringVal[i].toString(), false);
            }
            break;
        case INTEGER:
            Object[] integerVal = ((ArrayAttribute<Long>) message).toArray();
            for (int i = 0; i < integerVal.length; i++) {
                output.writeInt64(SchemaFields.INTEGER_ARRAY_VALUE.getValue(), (Long) integerVal[i], false);
            }
            break;
        case NUMBER:
            Object[] numberVal = ((ArrayAttribute<Double>) message).toArray();
            for (int i = 0; i < numberVal.length; i++) {
                output.writeDouble(SchemaFields.NUMBER_ARRAY_VALUE.getValue(), (Double) numberVal[i], false);
            }
            break;
        case DATE:
            Object[] dateVal = ((ArrayAttribute<LocalDate>) message).toArray();
            for (int i = 0; i < dateVal.length; i++) {
                output.writeByteArray(SchemaFields.DATE_ARRAY_VALUE.getValue(), ByteUtils.packLocalDate((LocalDate) dateVal[i]), false);
            }
            break;
        case TIME:
            Object[] timeVal = ((ArrayAttribute<LocalTime>) message).toArray();
            for (int i = 0; i < timeVal.length; i++) {
                output.writeByteArray(SchemaFields.TIME_ARRAY_VALUE.getValue(), ByteUtils.packLocalTime((LocalTime) timeVal[i]), false);
            }
            break;
        case TIMESTAMP:
            Object[] timestampVal = ((ArrayAttribute<LocalDateTime>) message).toArray();
            for (int i = 0; i < timestampVal.length; i++) {
                output.writeByteArray(SchemaFields.TIMESTAMP_ARRAY_VALUE.getValue(), ByteUtils.packLocalDateTime((LocalDateTime) timestampVal[i]), false);
            }
            break;
        default:
            break;
        }
    }
}
