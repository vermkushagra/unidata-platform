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
import java.util.Objects;

import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.impl.AbstractAttribute;
import com.unidata.mdm.backend.service.dump.types.VerifyableComplexAttribute;

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
