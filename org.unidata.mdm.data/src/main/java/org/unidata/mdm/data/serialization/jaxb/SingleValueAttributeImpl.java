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

package org.unidata.mdm.data.serialization.jaxb;

import org.unidata.mdm.data.AbstractSingleValueAttribute;
import org.unidata.mdm.data.BlobValue;
import org.unidata.mdm.data.ClobValue;
import org.unidata.mdm.data.MeasuredValue;
import org.unidata.mdm.data.ObjectFactory;
import org.unidata.mdm.data.ValueDataType;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author Mikhail Mikhailov
 *         Simple attribute value custom implementation.
 */
@SuppressWarnings("serial")
public class SingleValueAttributeImpl extends AbstractSingleValueAttribute {

    /**
     * Value data type.
     */
    @XmlTransient
    private ValueDataType type;

    /**
     * Constructor.
     */
    public SingleValueAttributeImpl() {
        super();
    }

    /**
     * @return the type
     */
    @XmlTransient
    public ValueDataType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    protected void setType(ValueDataType type) {
        this.type = type;
    }

    /**
     * @see AbstractSingleValueAttribute#setBlobValue(BlobValue)
     */
    public void setBlobValue(ObjectFactory factory, BlobValue value) {
        JAXBElement<BlobValue> elt = factory.createAbstractSingleValueAttributeBlobValue(value);
        elt.setNil(value == null);
        super.setBlobValue(elt);
        this.type = ValueDataType.BLOB;
    }

    /**
     * @see AbstractSingleValueAttribute#setClobValue(JAXBElement)( ClobValue)
     */
    public void setClobValue(ObjectFactory factory, ClobValue value) {
        JAXBElement<ClobValue> elt = factory.createAbstractSingleValueAttributeClobValue(value);
        elt.setNil(value == null);
        super.setClobValue(elt);
        this.type = ValueDataType.CLOB;
    }

    /**
     * @see AbstractSingleValueAttribute#setMeasuredValue(JAXBElement) (org.unidata.mdm.data.MeasuredValue)
     */
    public void setMeasuredValue(ObjectFactory factory, MeasuredValue value) {
        JAXBElement<MeasuredValue> elt = factory.createAbstractSingleValueAttributeMeasuredValue(value);
        elt.setNil(value == null);
        super.setMeasuredValue(elt);
        this.type = ValueDataType.MEASURED;
    }

    /**
     * @see AbstractSingleValueAttribute#setIntValue(Long)
     */
    public void setIntValue(ObjectFactory factory, Long value) {
        JAXBElement<Long> elt = factory.createAbstractSingleValueAttributeIntValue(value);
        elt.setNil(value == null);
        super.setIntValue(elt);
        this.type = ValueDataType.INTEGER;
    }

    /**
     * @see AbstractSingleValueAttribute#setDateValue(XMLGregorianCalendar)
     */
    public void setDateValue(ObjectFactory factory, XMLGregorianCalendar value) {
        JAXBElement<XMLGregorianCalendar> elt = factory.createAbstractSingleValueAttributeDateValue(value);
        elt.setNil(value == null);
        super.setDateValue(elt);
        this.type = ValueDataType.DATE;
    }

    /**
     * @see AbstractSingleValueAttribute#setTimeValue(XMLGregorianCalendar)
     */
    public void setTimeValue(ObjectFactory factory, XMLGregorianCalendar value) {
        JAXBElement<XMLGregorianCalendar> elt = factory.createAbstractSingleValueAttributeTimeValue(value);
        elt.setNil(value == null);
        super.setTimeValue(elt);
        this.type = ValueDataType.TIME;
    }

    /**
     * @see AbstractSingleValueAttribute#setTimestampValue(XMLGregorianCalendar)
     */
    public void setTimestampValue(ObjectFactory factory, XMLGregorianCalendar value) {
        JAXBElement<XMLGregorianCalendar> elt = factory.createAbstractSingleValueAttributeTimestampValue(value);
        elt.setNil(value == null);
        super.setTimestampValue(elt);
        this.type = ValueDataType.TIMESTAMP;
    }

    /**
     * @see AbstractSingleValueAttribute#setStringValue(String)
     */
    public void setStringValue(ObjectFactory factory, String value) {
        JAXBElement<String> elt = factory.createAbstractSingleValueAttributeStringValue(value);
        elt.setNil(value == null);
        super.setStringValue(elt);
        this.type = ValueDataType.STRING;
    }

    /**
     * @see AbstractSingleValueAttribute#setNumberValue(Double)
     */
    public void setNumberValue(ObjectFactory factory, Double value) {
        JAXBElement<Double> elt = factory.createAbstractSingleValueAttributeNumberValue(value);
        elt.setNil(value == null);
        super.setNumberValue(elt);
        this.type = ValueDataType.NUMBER;
    }

    /**
     * @see AbstractSingleValueAttribute#setBoolValue(Boolean)
     */
    public void setBoolValue(ObjectFactory factory, Boolean value) {
        JAXBElement<Boolean> elt = factory.createAbstractSingleValueAttributeBoolValue(value);
        elt.setNil(value == null);
        super.setBoolValue(elt);
        this.type = ValueDataType.BOOLEAN;
    }

    /* (non-Javadoc)
     * @see org.unidata.mdm.data.AbstractSingleValueAttribute#setBlobValue(javax.xml.bind.JAXBElement)
     */
    @Override
    public void setBlobValue(JAXBElement<BlobValue> value) {
        super.setBlobValue(value);
        this.type = ValueDataType.BLOB;
    }

    /* (non-Javadoc)
     * @see org.unidata.mdm.data.AbstractSingleValueAttribute#setClobValue(javax.xml.bind.JAXBElement)
     */
    @Override
    public void setClobValue(JAXBElement<ClobValue> value) {
        super.setClobValue(value);
        this.type = ValueDataType.CLOB;
    }

    /* (non-Javadoc)
     * @see org.unidata.mdm.data.AbstractSingleValueAttribute#setIntValue(javax.xml.bind.JAXBElement)
     */
    @Override
    public void setIntValue(JAXBElement<Long> value) {
        super.setIntValue(value);
        this.type = ValueDataType.INTEGER;
    }

    /* (non-Javadoc)
     * @see org.unidata.mdm.data.AbstractSingleValueAttribute#setDateValue(javax.xml.bind.JAXBElement)
     */
    @Override
    public void setDateValue(JAXBElement<XMLGregorianCalendar> value) {
        super.setDateValue(value);
        this.type = ValueDataType.DATE;
    }

    /* (non-Javadoc)
     * @see org.unidata.mdm.data.AbstractSingleValueAttribute#setTimeValue(javax.xml.bind.JAXBElement)
     */
    @Override
    public void setTimeValue(JAXBElement<XMLGregorianCalendar> value) {
        super.setTimeValue(value);
        this.type = ValueDataType.TIME;
    }

    /* (non-Javadoc)
     * @see org.unidata.mdm.data.AbstractSingleValueAttribute#setTimestampValue(javax.xml.bind.JAXBElement)
     */
    @Override
    public void setTimestampValue(JAXBElement<XMLGregorianCalendar> value) {
        super.setTimestampValue(value);
        this.type = ValueDataType.TIMESTAMP;
    }

    /* (non-Javadoc)
     * @see org.unidata.mdm.data.AbstractSingleValueAttribute#setStringValue(javax.xml.bind.JAXBElement)
     */
    @Override
    public void setStringValue(JAXBElement<String> value) {
        super.setStringValue(value);
        this.type = ValueDataType.STRING;
    }

    /* (non-Javadoc)
     * @see org.unidata.mdm.data.AbstractSingleValueAttribute#setNumberValue(javax.xml.bind.JAXBElement)
     */
    @Override
    public void setNumberValue(JAXBElement<Double> value) {
        super.setNumberValue(value);
        this.type = ValueDataType.NUMBER;
    }

    /* (non-Javadoc)
     * @see org.unidata.mdm.data.AbstractSingleValueAttribute#setBoolValue(javax.xml.bind.JAXBElement)
     */
    @Override
    public void setBoolValue(JAXBElement<Boolean> value) {
        super.setBoolValue(value);
        this.type = ValueDataType.BOOLEAN;
    }

    /* (non-Javadoc)
    * @see org.unidata.mdm.data.AbstractSingleValueAttribute#setMeasuredValue(javax.xml.bind.JAXBElement)
    */
    @Override
    public void setMeasuredValue(JAXBElement<MeasuredValue> value) {
        super.setMeasuredValue(value);
        this.type = ValueDataType.MEASURED;
    }

    /**
     * Supports MF fluent interface for {@link BlobValue} manually.
     * @param factory object factory
     * @param value the value
     *
     * @return self
     */
    public SingleValueAttributeImpl withBlobValue(ObjectFactory factory, BlobValue value) {
        setBlobValue(factory, value);
        return this;
    }

    /**
     * Supports MF fluent interface for {@link MeasuredValue} manually.
     * @param factory object factory
     * @param value the value
     *
     * @return self
     */
    public SingleValueAttributeImpl withMeasuredValue(ObjectFactory factory, MeasuredValue value) {
        setMeasuredValue(factory, value);
        return this;
    }

    /**
     * Supports MF fluent interface for {@link ClobValue} manually.
     * @param factory object factory
     * @param value the value
     *
     * @return self
     */
    public SingleValueAttributeImpl withClobValue(ObjectFactory factory, ClobValue value) {
        setClobValue(factory, value);
        return this;
    }

    /**
     * Supports MF fluent interface for {@link Long} manually.
     * @param factory object factory
     * @param value the value
     *
     * @return self
     */
    public SingleValueAttributeImpl withIntValue(ObjectFactory factory, Long value) {
        setIntValue(factory, value);
        return this;
    }

    /**
     * Supports MF fluent interface for date values manually.
     * @param factory object factory
     * @param value the value
     *
     * @return self
     */
    public SingleValueAttributeImpl withDateValue(ObjectFactory factory, XMLGregorianCalendar value) {
        setDateValue(factory, value);
        return this;
    }

    /**
     * Supports MF fluent interface for time values manually.
     * @param factory object factory
     * @param value the value
     *
     * @return self
     */
    public SingleValueAttributeImpl withTimeValue(ObjectFactory factory, XMLGregorianCalendar value) {
        setTimeValue(factory, value);
        return this;
    }

    /**
     * Supports MF fluent interface for timestamp values manually.
     * @param factory object factory
     * @param value the value
     *
     * @return self
     */
    public SingleValueAttributeImpl withTimestampValue(ObjectFactory factory, XMLGregorianCalendar value) {
        setTimestampValue(factory, value);
        return this;
    }

    /**
     * Supports MF fluent interface for string values manually.
     * @param factory object factory
     * @param value the value
     *
     * @return self
     */
    public SingleValueAttributeImpl withStringValue(ObjectFactory factory, String value) {
        setStringValue(factory, value);
        return this;
    }

    /**
     * Supports MF fluent interface for {@link Double} values manually.
     * @param factory object factory
     * @param value the value
     *
     * @return self
     */
    public SingleValueAttributeImpl withNumberValue(ObjectFactory factory, Double value) {
        setNumberValue(factory, value);
        return this;
    }

    /**
     * Supports MF fluent interface for {@link Boolean} values manually.
     * @param factory object factory
     * @param value the value
     *
     * @return self
     */
    public SingleValueAttributeImpl withBoolValue(ObjectFactory factory, Boolean value) {
        setBoolValue(factory, value);
        return this;
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
                    return stringValue != null ? (T) stringValue.getValue() : null;
                case INTEGER:
                    return intValue != null ? (T) intValue.getValue() : null;
                case NUMBER:
                    return numberValue != null ? (T) numberValue.getValue() : null;
                case BOOLEAN:
                    return boolValue != null ? (T) boolValue.getValue() : null;
                case DATE:
                    return dateValue != null ? (T) dateValue.getValue() : null;
                case TIME:
                    return timeValue != null ? (T) timeValue.getValue() : null;
                case TIMESTAMP:
                    return timestampValue != null ? (T) timestampValue.getValue() : null;
                case BLOB:
                    return blobValue != null ? (T) blobValue.getValue() : null;
                case CLOB:
                    return clobValue != null ? (T) clobValue.getValue() : null;
                case MEASURED:
                    return measuredValue != null ? (T) measuredValue.getValue() : null;
            }
        }

        return null;
    }

    /**
     * @see Object#hashCode()
     * TODO re-write this crap asap. Introduce solid value identity system instead.
     */
    @Override
    public int hashCode() {

        if (type == ValueDataType.BLOB) {
            BlobValue bv = getValue();
            return Objects.hash(type,
                    bv != null ? bv.getFileName() : null,
                    bv != null ? bv.getSize() : null,
                    bv != null ? bv.getMimeType() : null,
                    bv != null ? bv.getId() : null);
        }

        if (type == ValueDataType.CLOB) {
            ClobValue cv = getValue();
            return Objects.hash(type,
                    cv != null ? cv.getFileName() : null,
                    cv != null ? cv.getSize() : null,
                    cv != null ? cv.getMimeType() : null,
                    cv != null ? cv.getId() : null);
        }

        if (type == ValueDataType.MEASURED) {
            MeasuredValue mv = getValue();
            return Objects.hash(type,
                    mv == null ? null : mv.getValue(),
                    mv == null ? null : mv.getMeasurementValueId(),
                    mv == null ? null : mv.getMeasurementUnitId());
        }

        if (type == ValueDataType.DATE || type == ValueDataType.TIME || type == ValueDataType.TIMESTAMP) {
            XMLGregorianCalendar xgc = getValue();
            return Objects.hash(type, xgc != null
                    ? LocalDateTime.ofInstant(xgc.toGregorianCalendar().toInstant(), java.time.ZoneId.systemDefault())
                    : null);
        }

        return Objects.hash(type, getValue());
    }

    /**
     * @see Object#equals(Object)
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

        SingleValueAttributeImpl other = (SingleValueAttributeImpl) obj;
        if (type != other.type) {
            return false;
        }

        return Objects.equals(getValue(), other.getValue());
    }
}
