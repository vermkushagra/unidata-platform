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

package org.unidata.mdm.data.serialization.protostuff;

import java.io.IOException;
import java.util.Objects;

import org.unidata.mdm.core.type.data.ArrayAttribute;
import org.unidata.mdm.core.type.data.Attribute;
import org.unidata.mdm.core.type.data.CodeAttribute;
import org.unidata.mdm.core.type.data.ComplexAttribute;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.SimpleAttribute;
import org.unidata.mdm.core.type.data.impl.SerializableDataRecord;
import org.unidata.mdm.data.serialization.SerializedBlobSimpleAttributeImpl;
import org.unidata.mdm.data.serialization.SerializedBooleanSimpleAttributeImpl;
import org.unidata.mdm.data.serialization.SerializedClobSimpleAttributeImpl;
import org.unidata.mdm.data.serialization.SerializedComplexAttributeImpl;
import org.unidata.mdm.data.serialization.SerializedDateArrayAttributeImpl;
import org.unidata.mdm.data.serialization.SerializedDateSimpleAttributeImpl;
import org.unidata.mdm.data.serialization.SerializedIntegerArrayAttributeImpl;
import org.unidata.mdm.data.serialization.SerializedIntegerCodeAttributeImpl;
import org.unidata.mdm.data.serialization.SerializedIntegerSimpleAttributeImpl;
import org.unidata.mdm.data.serialization.SerializedMeasuredSimpleAttributeImpl;
import org.unidata.mdm.data.serialization.SerializedNumberArrayAttributeImpl;
import org.unidata.mdm.data.serialization.SerializedNumberSimpleAttributeImpl;
import org.unidata.mdm.data.serialization.SerializedStringArrayAttributeImpl;
import org.unidata.mdm.data.serialization.SerializedStringCodeAttributeImpl;
import org.unidata.mdm.data.serialization.SerializedStringSimpleAttributeImpl;
import org.unidata.mdm.data.serialization.SerializedTimeArrayAttributeImpl;
import org.unidata.mdm.data.serialization.SerializedTimeSimpleAttributeImpl;
import org.unidata.mdm.data.serialization.SerializedTimestampArrayAttributeImpl;
import org.unidata.mdm.data.serialization.SerializedTimestampSimpleAttributeImpl;

import io.protostuff.Input;
import io.protostuff.Output;
import io.protostuff.Schema;

/**
 * @author Mikhail Mikhailov
 * Date record schema.
 */
public class DataRecordSchema implements Schema<DataRecord> {
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
    public boolean isInitialized(DataRecord message) {
        return message != null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public DataRecord newMessage() {
        return new SerializableDataRecord();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String messageName() {
        return DataRecord.class.getSimpleName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String messageFullName() {
        return DataRecord.class.getName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<DataRecord> typeClass() {
        return DataRecord.class;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void mergeFrom(Input input, DataRecord message) throws IOException {

        while (true) {
            int field = input.readFieldNumber(this);
            switch (field) {
            case 0:
                return;
            case SchemaFields.STRING_SIMPLE_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedStringSimpleAttributeImpl(), Schemas.SIMPLE_ATTRIBUTE_SCHEMA));
                break;
            case SchemaFields.INTEGER_SIMPLE_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedIntegerSimpleAttributeImpl(), Schemas.SIMPLE_ATTRIBUTE_SCHEMA));
                break;
            case SchemaFields.NUMBER_SIMPLE_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedNumberSimpleAttributeImpl(), Schemas.SIMPLE_ATTRIBUTE_SCHEMA));
                break;
            case SchemaFields.BOOLEAN_SIMPLE_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedBooleanSimpleAttributeImpl(), Schemas.SIMPLE_ATTRIBUTE_SCHEMA));
                break;
            case SchemaFields.BLOB_SIMPLE_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedBlobSimpleAttributeImpl(), Schemas.SIMPLE_ATTRIBUTE_SCHEMA));
                break;
            case SchemaFields.CLOB_SIMPLE_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedClobSimpleAttributeImpl(), Schemas.SIMPLE_ATTRIBUTE_SCHEMA));
                break;
            case SchemaFields.DATE_SIMPLE_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedDateSimpleAttributeImpl(), Schemas.SIMPLE_ATTRIBUTE_SCHEMA));
                break;
            case SchemaFields.TIME_SIMPLE_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedTimeSimpleAttributeImpl(), Schemas.SIMPLE_ATTRIBUTE_SCHEMA));
                break;
            case SchemaFields.TIMESTAMP_SIMPLE_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedTimestampSimpleAttributeImpl(), Schemas.SIMPLE_ATTRIBUTE_SCHEMA));
                break;
            case SchemaFields.MEASURED_SIMPLE_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedMeasuredSimpleAttributeImpl(), Schemas.SIMPLE_ATTRIBUTE_SCHEMA));
                break;
            case SchemaFields.STRING_CODE_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedStringCodeAttributeImpl(), Schemas.CODE_ATTRIBUTE_SCHEMA));
                break;
            case SchemaFields.INTEGER_CODE_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedIntegerCodeAttributeImpl(), Schemas.CODE_ATTRIBUTE_SCHEMA));
                break;
            case SchemaFields.STRING_ARRAY_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedStringArrayAttributeImpl(), Schemas.ARRAY_ATTRIBUTE_SCHEMA));
                break;
            case SchemaFields.INTEGER_ARRAY_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedIntegerArrayAttributeImpl(), Schemas.ARRAY_ATTRIBUTE_SCHEMA));
                break;
            case SchemaFields.NUMBER_ARRAY_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedNumberArrayAttributeImpl(), Schemas.ARRAY_ATTRIBUTE_SCHEMA));
                break;
            case SchemaFields.DATE_ARRAY_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedDateArrayAttributeImpl(), Schemas.ARRAY_ATTRIBUTE_SCHEMA));
                break;
            case SchemaFields.TIME_ARRAY_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedTimeArrayAttributeImpl(), Schemas.ARRAY_ATTRIBUTE_SCHEMA));
                break;
            case SchemaFields.TIMESTAMP_ARRAY_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedTimestampArrayAttributeImpl(), Schemas.ARRAY_ATTRIBUTE_SCHEMA));
                break;
            case SchemaFields.COMPLEX_ATTRIBUTE_VAL:
                message.addAttribute(input.mergeObject(new SerializedComplexAttributeImpl(), Schemas.COMPLEX_ATTRIBUTE_SCHEMA));
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
    public void writeTo(Output output, DataRecord message) throws IOException {

        for (Attribute attribute : message.getAllAttributes()) {

            switch (attribute.getAttributeType()) {
            case SIMPLE:
                writeSimpleAttributeTo(output, (SimpleAttribute<?>) attribute);
                break;
            case CODE:
                writeCodeAttributeTo(output, (CodeAttribute<?>) attribute);
                break;
            case ARRAY:
                writeArrayAttributeTo(output, (ArrayAttribute<?>) attribute);
                break;
            case COMPLEX:
                writeComplexAttributeTo(output, (ComplexAttribute) attribute);
                break;
            }
        }

    }
    /**
     * Writes simple attribute.
     * @param output the output
     * @param attribute the attribute
     * @throws IOException
     */
    private void writeSimpleAttributeTo(Output output, SimpleAttribute<?> attribute) throws IOException {

        switch (attribute.getDataType()) {
        case BLOB:
            output.writeObject(SchemaFields.BLOB_SIMPLE_ATTRIBUTE.getValue(), attribute, Schemas.SIMPLE_ATTRIBUTE_SCHEMA, false);
            break;
        case CLOB:
            output.writeObject(SchemaFields.CLOB_SIMPLE_ATTRIBUTE.getValue(), attribute, Schemas.SIMPLE_ATTRIBUTE_SCHEMA, false);
            break;
        case BOOLEAN:
            output.writeObject(SchemaFields.BOOLEAN_SIMPLE_ATTRIBUTE.getValue(), attribute, Schemas.SIMPLE_ATTRIBUTE_SCHEMA, false);
            break;
        case INTEGER:
            output.writeObject(SchemaFields.INTEGER_SIMPLE_ATTRIBUTE.getValue(), attribute, Schemas.SIMPLE_ATTRIBUTE_SCHEMA, false);
            break;
        case MEASURED:
            output.writeObject(SchemaFields.MEASURED_SIMPLE_ATTRIBUTE.getValue(), attribute, Schemas.SIMPLE_ATTRIBUTE_SCHEMA, false);
            break;
        case NUMBER:
            output.writeObject(SchemaFields.NUMBER_SIMPLE_ATTRIBUTE.getValue(), attribute, Schemas.SIMPLE_ATTRIBUTE_SCHEMA, false);
            break;
        case STRING:
            output.writeObject(SchemaFields.STRING_SIMPLE_ATTRIBUTE.getValue(), attribute, Schemas.SIMPLE_ATTRIBUTE_SCHEMA, false);
            break;
        case DATE:
            output.writeObject(SchemaFields.DATE_SIMPLE_ATTRIBUTE.getValue(), attribute, Schemas.SIMPLE_ATTRIBUTE_SCHEMA, false);
            break;
        case TIME:
            output.writeObject(SchemaFields.TIME_SIMPLE_ATTRIBUTE.getValue(), attribute, Schemas.SIMPLE_ATTRIBUTE_SCHEMA, false);
            break;
        case TIMESTAMP:
            output.writeObject(SchemaFields.TIMESTAMP_SIMPLE_ATTRIBUTE.getValue(), attribute, Schemas.SIMPLE_ATTRIBUTE_SCHEMA, false);
            break;
        default:
            break;
        }
    }
    /**
     * Writes code attribute.
     * @param output the output
     * @param attribute the attribute
     * @throws IOException
     */
    private void writeCodeAttributeTo(Output output, CodeAttribute<?> attribute) throws IOException {

        if (Objects.isNull(attribute.getValue())) {
            return;
        }

        switch (attribute.getDataType()) {
        case INTEGER:
            output.writeObject(SchemaFields.INTEGER_CODE_ATTRIBUTE.getValue(), attribute, Schemas.CODE_ATTRIBUTE_SCHEMA, false);
            break;
        case STRING:
            output.writeObject(SchemaFields.STRING_CODE_ATTRIBUTE.getValue(), attribute, Schemas.CODE_ATTRIBUTE_SCHEMA, false);
            break;
        default:
            break;
        }
    }
    /**
     * Writes array attribute.
     * @param output the output
     * @param attribute the attribute
     * @throws IOException
     */
    private void writeArrayAttributeTo(Output output, ArrayAttribute<?> attribute) throws IOException {

        switch (attribute.getDataType()) {
        case DATE:
            output.writeObject(SchemaFields.DATE_ARRAY_ATTRIBUTE.getValue(), attribute, Schemas.ARRAY_ATTRIBUTE_SCHEMA, false);
            break;
        case INTEGER:
            output.writeObject(SchemaFields.INTEGER_ARRAY_ATTRIBUTE.getValue(), attribute, Schemas.ARRAY_ATTRIBUTE_SCHEMA, false);
            break;
        case NUMBER:
            output.writeObject(SchemaFields.NUMBER_ARRAY_ATTRIBUTE.getValue(), attribute, Schemas.ARRAY_ATTRIBUTE_SCHEMA, false);
            break;
        case STRING:
            output.writeObject(SchemaFields.STRING_ARRAY_ATTRIBUTE.getValue(), attribute, Schemas.ARRAY_ATTRIBUTE_SCHEMA, false);
            break;
        case TIME:
            output.writeObject(SchemaFields.TIME_ARRAY_ATTRIBUTE.getValue(), attribute, Schemas.ARRAY_ATTRIBUTE_SCHEMA, false);
            break;
        case TIMESTAMP:
            output.writeObject(SchemaFields.TIMESTAMP_ARRAY_ATTRIBUTE.getValue(), attribute, Schemas.ARRAY_ATTRIBUTE_SCHEMA, false);
            break;
        default:
            break;
        }
    }
    /**
     * Writes complex attribute.
     * @param output the output
     * @param attribute the attribute
     * @throws IOException
     */
    private void writeComplexAttributeTo(Output output, ComplexAttribute attribute) throws IOException {
        output.writeObject(SchemaFields.COMPLEX_ATTRIBUTE.getValue(), attribute, Schemas.COMPLEX_ATTRIBUTE_SCHEMA, false);
    }
}
