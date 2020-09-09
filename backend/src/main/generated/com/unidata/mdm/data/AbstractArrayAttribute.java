//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.09.09 at 03:48:37 PM MSK 
//


package com.unidata.mdm.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import com.sun.xml.bind.XmlAccessorFactory;


/**
 * 
 * Базовая структура, описывающая простой атрибут сущности. Состоит из обязательного имени и массива значений в зависимости от типа данных
 *             
 * 
 * <p>Java class for AbstractArrayAttribute complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractArrayAttribute"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://data.mdm.unidata.com/}AbstractAttribute"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="intValue" type="{http://www.w3.org/2001/XMLSchema}long" maxOccurs="unbounded"/&gt;
 *           &lt;element name="dateValue" type="{http://www.w3.org/2001/XMLSchema}date" maxOccurs="unbounded"/&gt;
 *           &lt;element name="timeValue" type="{http://www.w3.org/2001/XMLSchema}time" maxOccurs="unbounded"/&gt;
 *           &lt;element name="timestampValue" type="{http://www.w3.org/2001/XMLSchema}dateTime" maxOccurs="unbounded"/&gt;
 *           &lt;element name="stringValue" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/&gt;
 *           &lt;element name="numberValue" type="{http://www.w3.org/2001/XMLSchema}double" maxOccurs="unbounded"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractArrayAttribute", propOrder = {
    "intValue",
    "dateValue",
    "timeValue",
    "timestampValue",
    "stringValue",
    "numberValue"
})
@XmlSeeAlso({
    ArrayAttribute.class
})
@XmlAccessorFactory(ArrayAttributeAccessorFactory.class)
public class AbstractArrayAttribute
    extends AbstractAttribute
    implements Serializable
{

    private final static long serialVersionUID = 12345L;
    @XmlElement(nillable = true)
    protected List<Long> intValue;
    @XmlElement(nillable = true)
    @XmlSchemaType(name = "date")
    protected List<XMLGregorianCalendar> dateValue;
    @XmlElement(nillable = true)
    @XmlSchemaType(name = "time")
    protected List<XMLGregorianCalendar> timeValue;
    @XmlElement(nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected List<XMLGregorianCalendar> timestampValue;
    @XmlElement(nillable = true)
    protected List<String> stringValue;
    @XmlElement(nillable = true)
    protected List<Double> numberValue;

    /**
     * Gets the value of the intValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the intValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIntValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getIntValue() {
        if (intValue == null) {
            intValue = new ArrayList<Long>();
        }
        return this.intValue;
    }

    /**
     * Gets the value of the dateValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dateValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDateValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XMLGregorianCalendar }
     * 
     * 
     */
    public List<XMLGregorianCalendar> getDateValue() {
        if (dateValue == null) {
            dateValue = new ArrayList<XMLGregorianCalendar>();
        }
        return this.dateValue;
    }

    /**
     * Gets the value of the timeValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the timeValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTimeValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XMLGregorianCalendar }
     * 
     * 
     */
    public List<XMLGregorianCalendar> getTimeValue() {
        if (timeValue == null) {
            timeValue = new ArrayList<XMLGregorianCalendar>();
        }
        return this.timeValue;
    }

    /**
     * Gets the value of the timestampValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the timestampValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTimestampValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XMLGregorianCalendar }
     * 
     * 
     */
    public List<XMLGregorianCalendar> getTimestampValue() {
        if (timestampValue == null) {
            timestampValue = new ArrayList<XMLGregorianCalendar>();
        }
        return this.timestampValue;
    }

    /**
     * Gets the value of the stringValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stringValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStringValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getStringValue() {
        if (stringValue == null) {
            stringValue = new ArrayList<String>();
        }
        return this.stringValue;
    }

    /**
     * Gets the value of the numberValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the numberValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNumberValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     * 
     * 
     */
    public List<Double> getNumberValue() {
        if (numberValue == null) {
            numberValue = new ArrayList<Double>();
        }
        return this.numberValue;
    }

    public AbstractArrayAttribute withIntValue(Long... values) {
        if (values!= null) {
            for (Long value: values) {
                getIntValue().add(value);
            }
        }
        return this;
    }

    public AbstractArrayAttribute withIntValue(Collection<Long> values) {
        if (values!= null) {
            getIntValue().addAll(values);
        }
        return this;
    }

    public AbstractArrayAttribute withDateValue(XMLGregorianCalendar... values) {
        if (values!= null) {
            for (XMLGregorianCalendar value: values) {
                getDateValue().add(value);
            }
        }
        return this;
    }

    public AbstractArrayAttribute withDateValue(Collection<XMLGregorianCalendar> values) {
        if (values!= null) {
            getDateValue().addAll(values);
        }
        return this;
    }

    public AbstractArrayAttribute withTimeValue(XMLGregorianCalendar... values) {
        if (values!= null) {
            for (XMLGregorianCalendar value: values) {
                getTimeValue().add(value);
            }
        }
        return this;
    }

    public AbstractArrayAttribute withTimeValue(Collection<XMLGregorianCalendar> values) {
        if (values!= null) {
            getTimeValue().addAll(values);
        }
        return this;
    }

    public AbstractArrayAttribute withTimestampValue(XMLGregorianCalendar... values) {
        if (values!= null) {
            for (XMLGregorianCalendar value: values) {
                getTimestampValue().add(value);
            }
        }
        return this;
    }

    public AbstractArrayAttribute withTimestampValue(Collection<XMLGregorianCalendar> values) {
        if (values!= null) {
            getTimestampValue().addAll(values);
        }
        return this;
    }

    public AbstractArrayAttribute withStringValue(String... values) {
        if (values!= null) {
            for (String value: values) {
                getStringValue().add(value);
            }
        }
        return this;
    }

    public AbstractArrayAttribute withStringValue(Collection<String> values) {
        if (values!= null) {
            getStringValue().addAll(values);
        }
        return this;
    }

    public AbstractArrayAttribute withNumberValue(Double... values) {
        if (values!= null) {
            for (Double value: values) {
                getNumberValue().add(value);
            }
        }
        return this;
    }

    public AbstractArrayAttribute withNumberValue(Collection<Double> values) {
        if (values!= null) {
            getNumberValue().addAll(values);
        }
        return this;
    }

    @Override
    public AbstractArrayAttribute withName(String value) {
        setName(value);
        return this;
    }

}
