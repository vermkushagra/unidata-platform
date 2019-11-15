package org.unidata.mdm.data.serialization.protostuff;

import java.io.IOException;
import java.util.Objects;

import org.unidata.mdm.core.type.data.ComplexAttribute;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.impl.AbstractAttribute;
import org.unidata.mdm.data.serialization.VerifyableComplexAttribute;

import io.protostuff.Input;
import io.protostuff.Output;
import io.protostuff.Schema;

/**
 * @author Mikhail Mikhailov
 * Complex attributes.
 */
public class ComplexAttributeSchema implements Schema<ComplexAttribute> {
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
    public boolean isInitialized(ComplexAttribute message) {

        if (message instanceof VerifyableComplexAttribute) {
            return ((VerifyableComplexAttribute) message).isValid();
        }

        return true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ComplexAttribute newMessage() {
        // Should not be called.
        return null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String messageName() {
        return ComplexAttribute.class.getSimpleName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String messageFullName() {
        return ComplexAttribute.class.getName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? super ComplexAttribute> typeClass() {
        return ComplexAttribute.class;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void mergeFrom(Input input, ComplexAttribute message) throws IOException {

        while (true) {
            int field = input.readFieldNumber(this);
            switch (field) {
            case 0:
                return;
            case SchemaFields.FIELD_NAME_VAL:
                ((AbstractAttribute) message).setName(input.readString());
                break;
            case SchemaFields.DATA_RECORD_VAL:
                message.add(input.mergeObject(null, Schemas.DATA_RECORD_SCHEMA));
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
    public void writeTo(Output output, ComplexAttribute message) throws IOException {

        output.writeString(SchemaFields.FIELD_NAME.getValue(), message.getName(), false);
        if (Objects.isNull(message.isEmpty())) {
            return;
        }

        for (DataRecord child : message) {
            output.writeObject(SchemaFields.DATA_RECORD.getValue(), child, Schemas.DATA_RECORD_SCHEMA, true);
        }
    }
}
