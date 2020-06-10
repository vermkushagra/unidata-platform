package com.unidata.mdm.api.wsdl.v5;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.XMLGregorianCalendar;

import com.unidata.mdm.data.v5.AbstractArrayAttribute;
import com.unidata.mdm.data.v5.ArrayDataType;

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
