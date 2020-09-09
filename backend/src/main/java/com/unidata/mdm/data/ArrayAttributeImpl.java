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

package com.unidata.mdm.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author Mikhail Mikhailov
 *
 */
@SuppressWarnings("serial")
public class ArrayAttributeImpl extends AbstractArrayAttribute {

    @XmlTransient
    private ArrayDataType type;

    /**
     * Constructor.
     */
    public ArrayAttributeImpl() {
        super();
    }

    /**
     * @return the type
     */
    @XmlTransient
    public ArrayDataType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ArrayDataType type) {
        this.type = type;
    }


    @Override
    public List<Long> getIntValue() {
        if (intValue == null) {
            intValue = new ArrayList<>();
            type = ArrayDataType.INTEGER;
        }
        return this.intValue;
    }

    @Override
    public List<XMLGregorianCalendar> getDateValue() {
        if (dateValue == null) {
            dateValue = new ArrayList<>();
            type = ArrayDataType.DATE;
        }
        return this.dateValue;
    }

    @Override
    public List<XMLGregorianCalendar> getTimeValue() {
        if (timeValue == null) {
            timeValue = new ArrayList<>();
            type = ArrayDataType.TIME;
        }
        return this.timeValue;
    }

    @Override
    public List<XMLGregorianCalendar> getTimestampValue() {
        if (timestampValue == null) {
            timestampValue = new ArrayList<>();
            type = ArrayDataType.TIMESTAMP;
        }
        return this.timestampValue;
    }

    @Override
    public List<String> getStringValue() {
        if (stringValue == null) {
            stringValue = new ArrayList<>();
            type = ArrayDataType.STRING;
        }
        return this.stringValue;
    }

    @Override
    public List<Double> getNumberValue() {
        if (numberValue == null) {
            numberValue = new ArrayList<>();
            type = ArrayDataType.NUMBER;
        }
        return this.numberValue;
    }
}
