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

package com.unidata.mdm.api.wsdl.v5;

import java.util.Objects;

import com.unidata.mdm.data.v5.AbstractCodeAttribute;
import com.unidata.mdm.data.v5.CodeDataType;

/**
 * @author Mikhail Mikhailov
 *         Simple attribute value custom implementation.
 */
@SuppressWarnings("serial")
public class CodeAttributeImpl extends AbstractCodeAttribute {

    /**
     * Value data type.
     */
    private CodeDataType type;
    /**
     * Constructor.
     */
    public CodeAttributeImpl() {
        super();
    }

    /**
     * @return the type
     */
    public CodeDataType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    protected void setType(CodeDataType type) {
        this.type = type;
    }

    /**
     * Gets value of the attribute.
     *
     * @return the value
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue() {

        if (type != null) {
            switch (type) {
                case STRING:
                    return (T) stringValue;
                case INTEGER:
                    return (T) intValue;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIntValue(Long value) {
        super.setIntValue(value);
        this.type = CodeDataType.INTEGER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStringValue(String value) {
        super.setStringValue(value);
        this.type = CodeDataType.STRING;
    }

    /**
     * @see java.lang.Object#hashCode()
     * TODO re-write this crap asap. Introduce solid value identity system instead.
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, getValue());
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!getClass().isInstance(obj)) {
            return false;
        }

        CodeAttributeImpl other = (CodeAttributeImpl) obj;
        if (type != other.type) {
            return false;
        }

        return Objects.equals(getValue(), other.getValue());
    }
}
