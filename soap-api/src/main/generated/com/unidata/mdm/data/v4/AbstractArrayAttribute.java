
package com.unidata.mdm.data.v4;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import com.sun.xml.bind.XmlAccessorFactory;
import com.unidata.mdm.api.wsdl.v4.ArrayAttributeAccessorFactory;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


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
 *     &lt;extension base="{http://data.mdm.unidata.com/v4/}AbstractAttribute"&gt;
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
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
public class AbstractArrayAttribute
    extends AbstractAttribute
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlElement(type = Long.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected List<Long> intValue;
    @XmlSchemaType(name = "date")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected List<XMLGregorianCalendar> dateValue;
    @XmlSchemaType(name = "time")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected List<XMLGregorianCalendar> timeValue;
    @XmlSchemaType(name = "dateTime")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected List<XMLGregorianCalendar> timestampValue;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected List<String> stringValue;
    @XmlElement(type = Double.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public List<Double> getNumberValue() {
        if (numberValue == null) {
            numberValue = new ArrayList<Double>();
        }
        return this.numberValue;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public AbstractArrayAttribute withIntValue(Long... values) {
        if (values!= null) {
            for (Long value: values) {
                getIntValue().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public AbstractArrayAttribute withIntValue(Collection<Long> values) {
        if (values!= null) {
            getIntValue().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public AbstractArrayAttribute withDateValue(XMLGregorianCalendar... values) {
        if (values!= null) {
            for (XMLGregorianCalendar value: values) {
                getDateValue().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public AbstractArrayAttribute withDateValue(Collection<XMLGregorianCalendar> values) {
        if (values!= null) {
            getDateValue().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public AbstractArrayAttribute withTimeValue(XMLGregorianCalendar... values) {
        if (values!= null) {
            for (XMLGregorianCalendar value: values) {
                getTimeValue().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public AbstractArrayAttribute withTimeValue(Collection<XMLGregorianCalendar> values) {
        if (values!= null) {
            getTimeValue().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public AbstractArrayAttribute withTimestampValue(XMLGregorianCalendar... values) {
        if (values!= null) {
            for (XMLGregorianCalendar value: values) {
                getTimestampValue().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public AbstractArrayAttribute withTimestampValue(Collection<XMLGregorianCalendar> values) {
        if (values!= null) {
            getTimestampValue().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public AbstractArrayAttribute withStringValue(String... values) {
        if (values!= null) {
            for (String value: values) {
                getStringValue().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public AbstractArrayAttribute withStringValue(Collection<String> values) {
        if (values!= null) {
            getStringValue().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public AbstractArrayAttribute withNumberValue(Double... values) {
        if (values!= null) {
            for (Double value: values) {
                getNumberValue().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public AbstractArrayAttribute withNumberValue(Collection<Double> values) {
        if (values!= null) {
            getNumberValue().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public AbstractArrayAttribute withName(String value) {
        setName(value);
        return this;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public String toString() {
        return ToStringBuilder.reflectionToString(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
