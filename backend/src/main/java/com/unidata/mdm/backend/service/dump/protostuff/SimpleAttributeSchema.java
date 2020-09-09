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

package com.unidata.mdm.backend.service.dump.protostuff;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import com.unidata.mdm.backend.common.types.BinaryLargeValue;
import com.unidata.mdm.backend.common.types.CharacterLargeValue;
import com.unidata.mdm.backend.common.types.LargeValue;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.common.types.impl.AbstractAttribute;
import com.unidata.mdm.backend.common.types.impl.AbstractLargeValue;
import com.unidata.mdm.backend.common.types.impl.BinaryLargeValueImpl;
import com.unidata.mdm.backend.common.types.impl.CharacterLargeValueImpl;
import com.unidata.mdm.backend.common.types.impl.MeasuredSimpleAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.VerifyableSimpleAttribute;
import com.unidata.mdm.backend.util.ByteUtils;

import io.protostuff.Input;
import io.protostuff.Output;
import io.protostuff.Schema;

/**
 * @author Mikhail Mikhailov
 * Simple attributes.
 */
public class SimpleAttributeSchema implements Schema<SimpleAttribute<?>> {
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
    @SuppressWarnings("rawtypes")
    @Override
    public boolean isInitialized(SimpleAttribute<?> message) {

        if (message instanceof VerifyableSimpleAttribute) {
            return ((VerifyableSimpleAttribute) message).isValid();
        }

        return true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleAttribute<?> newMessage() {
        // Should never be called.
        return null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String messageName() {
        return SimpleAttribute.class.getSimpleName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String messageFullName() {
        return SimpleAttribute.class.getName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? super SimpleAttribute<?>> typeClass() {
        return SimpleAttribute.class;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void mergeFrom(Input input, SimpleAttribute<?> message) throws IOException {

        AbstractLargeValue lob = null;
        while (true) {
            int field = input.readFieldNumber(this);
            switch (field) {
            case 0:
                return;
            case SchemaFields.FIELD_NAME_VAL:
                ((AbstractAttribute) message).setName(input.readString());
                break;
            case SchemaFields.STRING_VALUE_VAL:
                message.castValue(input.readString());
                break;
            case SchemaFields.INTEGER_VALUE_VAL:
                message.castValue(input.readInt64());
                break;
            case SchemaFields.NUMBER_VALUE_VAL:
                Double doubleValue = input.readDouble();
                message.castValue(doubleValue);
                if (message.getDataType() == DataType.MEASURED) {
                    ((MeasuredSimpleAttributeImpl) message).withInitialValue(doubleValue);
                }
                break;
            case SchemaFields.BOOLEAN_VALUE_VAL:
                message.castValue(input.readBool());
                break;
            case SchemaFields.DATE_VALUE_VAL:
                message.castValue(ByteUtils.unpackLocalDate(input.readByteArray()));
                break;
            case SchemaFields.TIME_VALUE_VAL:
                message.castValue(ByteUtils.unpackLocalTime(input.readByteArray()));
                break;
            case SchemaFields.TIMESTAMP_VALUE_VAL:
                message.castValue(ByteUtils.unpackLocalDateTime(input.readByteArray()));
                break;
            // LOB
            case SchemaFields.LARGE_VALUE_ID_VAL:
                lob = ensureLargeValue(message);
                lob.setId(input.readString());
                break;
            case SchemaFields.LARGE_VALUE_FILENAME_VAL:
                lob = ensureLargeValue(message);
                lob.setFileName(input.readString());
                break;
            case SchemaFields.LARGE_VALUE_MIME_TYPE_VAL:
                lob = ensureLargeValue(message);
                lob.setMimeType(input.readString());
                break;
            case SchemaFields.LARGE_VALUE_SIZE_VAL:
                lob = ensureLargeValue(message);
                lob.setSize(input.readInt64());
                break;
            // Measured value
            case SchemaFields.MEASURED_UNIT_ID_VAL:
                ((MeasuredSimpleAttributeImpl) message).withInitialUnitId(input.readString());
                break;
            case SchemaFields.MEASURED_VALUE_ID_VAL:
                ((MeasuredSimpleAttributeImpl) message).withValueId(input.readString());
                break;
            default:
                break;
            }
        }

    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void writeTo(Output output, SimpleAttribute<?> message) throws IOException {

        output.writeString(SchemaFields.FIELD_NAME.getValue(), message.getName(), false);

        // for measured attribute need save additional information regardless of the attribute value ( UN-5498 )
        if(DataType.MEASURED.equals(message.getDataType())){
            MeasuredSimpleAttributeImpl measured =  (MeasuredSimpleAttributeImpl) message;
            writeMeasuredAttributeTo(output, measured);
        }

        if (Objects.isNull(message.getValue())) {
            return;
        }

        switch (message.getDataType()) {
        case STRING:
            String stringVal = message.castValue();
            output.writeString(SchemaFields.STRING_VALUE.getValue(), stringVal, false);
            break;
        case BOOLEAN:
            Boolean booleanVal = message.castValue();
            output.writeBool(SchemaFields.BOOLEAN_VALUE.getValue(), booleanVal, false);
            break;
        case INTEGER:
            Long integerVal = message.castValue();
            output.writeInt64(SchemaFields.INTEGER_VALUE.getValue(), integerVal, false);
            break;
        case NUMBER:
            Double numberVal = message.castValue();
            output.writeDouble(SchemaFields.NUMBER_VALUE.getValue(), numberVal, false);
            break;
        case DATE:
            LocalDate dateVal = message.castValue();
            output.writeByteArray(SchemaFields.DATE_VALUE.getValue(), ByteUtils.packLocalDate(dateVal), false);
            break;
        case TIME:
            LocalTime timeVal = message.castValue();
            output.writeByteArray(SchemaFields.TIME_VALUE.getValue(), ByteUtils.packLocalTime(timeVal), false);
            break;
        case TIMESTAMP:
            LocalDateTime timestampVal = message.castValue();
            output.writeByteArray(SchemaFields.TIMESTAMP_VALUE.getValue(), ByteUtils.packLocalDateTime(timestampVal), false);
            break;
        case MEASURED:
            break;
        case BLOB:
            BinaryLargeValue binaryVal = message.castValue();
            writeLargeValueTo(output, binaryVal);
            break;
        case CLOB:
            CharacterLargeValue characterVal = message.castValue();
            writeLargeValueTo(output, characterVal);
            break;
        default:
            break;
        }

    }

    /**
     * Ensures existence of the LOB object for an attribute.
     * @param attribute the attribute
     * @return the value
     */
    private AbstractLargeValue ensureLargeValue(SimpleAttribute<?> attribute) {

        AbstractLargeValue lob = attribute.castValue();
        if (lob == null) {
            lob = attribute.getDataType() == DataType.BLOB ? new BinaryLargeValueImpl() : new CharacterLargeValueImpl();
            attribute.castValue(lob);
        }

        return lob;
    }
    /**
     * Writes large value to stream.
     * @param output the output
     * @param largeValue large value
     */
    private void writeLargeValueTo(Output output, LargeValue largeValue) throws IOException {

        String bvId = largeValue.getId();
        String fileName = largeValue.getFileName();
        String mimeType = largeValue.getMimeType();
        long size = largeValue.getSize();

        if (Objects.nonNull(bvId)) {
            output.writeString(SchemaFields.LARGE_VALUE_ID.getValue(), bvId, false);
        }

        if (Objects.nonNull(fileName)) {
            output.writeString(SchemaFields.LARGE_VALUE_FILENAME.getValue(), fileName, false);
        }

        if (Objects.nonNull(mimeType)) {
            output.writeString(SchemaFields.LARGE_VALUE_MIME_TYPE.getValue(), mimeType, false);
        }

        output.writeInt64(SchemaFields.LARGE_VALUE_SIZE.getValue(), size, false);
    }
    /**
     * Writes measured attribute.
     * @param output the output
     * @param measured the attribute
     * @throws IOException
     */
    private void writeMeasuredAttributeTo(Output output, MeasuredSimpleAttributeImpl measured) throws IOException {

        String unitId = measured.getInitialUnitId();
        String valueId = measured.getValueId();
        Double initialValue = measured.getInitialValue();

        if (Objects.nonNull(unitId)) {
            output.writeString(SchemaFields.MEASURED_UNIT_ID.getValue(), unitId, false);
        }

        if (Objects.nonNull(valueId)) {
            output.writeString(SchemaFields.MEASURED_VALUE_ID.getValue(), valueId, false);
        }

        if(initialValue != null){
            output.writeDouble(SchemaFields.NUMBER_VALUE.getValue(), initialValue, false);
        }
    }
}
