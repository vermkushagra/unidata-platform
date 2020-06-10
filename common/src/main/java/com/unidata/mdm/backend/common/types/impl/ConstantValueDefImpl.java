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

package com.unidata.mdm.backend.common.types.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.unidata.mdm.meta.ConstantValueDef;
import com.unidata.mdm.meta.ConstantValueType;

/**
 * @author Mikhail Mikhailov
 * Type setting extension.
 */
public class ConstantValueDefImpl extends ConstantValueDef {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 8073725919726730239L;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIntValue(Long value) {
        super.setType(ConstantValueType.INTEGER);
        super.setIntValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDateValue(LocalDate value) {
        super.setType(ConstantValueType.DATE);
        super.setDateValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTimeValue(LocalTime value) {
        super.setType(ConstantValueType.TIME);
        super.setTimeValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTimestampValue(LocalDateTime value) {
        super.setType(ConstantValueType.TIMESTAMP);
        super.setTimestampValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStringValue(String value) {
        super.setType(ConstantValueType.STRING);
        super.setStringValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNumberValue(Double value) {
        super.setType(ConstantValueType.NUMBER);
        super.setNumberValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBoolValue(Boolean value) {
        super.setType(ConstantValueType.BOOLEAN);
        super.setBoolValue(value);
    }

}
