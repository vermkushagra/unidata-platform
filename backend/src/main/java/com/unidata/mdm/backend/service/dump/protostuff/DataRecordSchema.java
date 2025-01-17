package com.unidata.mdm.backend.service.dump.protostuff;

import java.io.IOException;

import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.service.dump.types.SerializedBlobSimpleAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.SerializedBooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.SerializedClobSimpleAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.SerializedComplexAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.SerializedDateArrayAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.SerializedDateSimpleAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.SerializedIntegerArrayAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.SerializedIntegerCodeAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.SerializedIntegerSimpleAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.SerializedMeasuredSimpleAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.SerializedNumberArrayAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.SerializedNumberSimpleAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.SerializedStringArrayAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.SerializedStringCodeAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.SerializedStringSimpleAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.SerializedTimeArrayAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.SerializedTimeSimpleAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.SerializedTimestampArrayAttributeImpl;
import com.unidata.mdm.backend.service.dump.types.SerializedTimestampSimpleAttributeImpl;

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
