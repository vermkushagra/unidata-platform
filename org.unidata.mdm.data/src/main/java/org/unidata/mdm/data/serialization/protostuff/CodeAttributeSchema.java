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
