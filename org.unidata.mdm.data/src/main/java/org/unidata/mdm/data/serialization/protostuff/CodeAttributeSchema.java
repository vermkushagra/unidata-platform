package org.unidata.mdm.data.serialization.protostuff;

import java.io.IOException;

import org.unidata.mdm.core.type.data.CodeAttribute;
import org.unidata.mdm.core.type.data.impl.AbstractAttribute;
import org.unidata.mdm.data.serialization.VerifyableCodeAttribute;

import io.protostuff.Input;
import io.protostuff.Output;
import io.protostuff.Schema;

/**
 * @author Mikhail Mikhailov
 * Code attributes.
 */
public class CodeAttributeSchema implements Schema<CodeAttribute<?>> {
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
    public boolean isInitialized(CodeAttribute<?> message) {

        if (message instanceof VerifyableCodeAttribute<?>) {
            return ((VerifyableCodeAttribute<?>) message).isValid();
        }

        return true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CodeAttribute<?> newMessage() {
        // Should not be called.
        return null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String messageName() {
        return CodeAttribute.class.getSimpleName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String messageFullName() {
        return CodeAttribute.class.getName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? super CodeAttribute<?>> typeClass() {
        return CodeAttribute.class;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void mergeFrom(Input input, CodeAttribute<?> message) throws IOException {
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
            default:
                break;
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void writeTo(Output output, CodeAttribute<?> message) throws IOException {

        output.writeString(SchemaFields.FIELD_NAME.getValue(), message.getName(), false);

        switch (message.getDataType()) {
        case STRING:
            String stringVal = message.castValue();
            output.writeString(SchemaFields.STRING_VALUE.getValue(), stringVal, false);
            break;
        case INTEGER:
            Long integerVal = message.castValue();
            output.writeInt64(SchemaFields.INTEGER_VALUE.getValue(), integerVal, false);
            break;
        default:
            break;
        }

    }
}
