
package com.unidata.mdm.data.v3;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import com.sun.xml.bind.XmlAccessorFactory;
import com.unidata.mdm.api.wsdl.v3.SimpleAttributeAccessorFactory;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 * Базовая структура, описывающая значение атрибута сущности. Всегда содержит только одно значение в зависимости от типа данных
 *             
 * 
 * <p>Java class for AbstractSingleValueAttribute complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractSingleValueAttribute"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://data.mdm.unidata.com/v3/}AbstractAttribute"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="blobValue" type="{http://data.mdm.unidata.com/v3/}BlobValue"/&gt;
 *           &lt;element name="clobValue" type="{http://data.mdm.unidata.com/v3/}ClobValue"/&gt;
 *           &lt;element name="intValue" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *           &lt;element name="dateValue" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *           &lt;element name="timeValue" type="{http://www.w3.org/2001/XMLSchema}time"/&gt;
 *           &lt;element name="timestampValue" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *           &lt;element name="stringValue" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *           &lt;element name="numberValue" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *           &lt;element name="boolValue" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *           &lt;element name="measuredValue" type="{http://data.mdm.unidata.com/v3/}MeasuredValue"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="displayValue" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="linkEtalonId" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractSingleValueAttribute", propOrder = {
    "blobValue",
    "clobValue",
    "intValue",
    "dateValue",
    "timeValue",
    "timestampValue",
    "stringValue",
    "numberValue",
    "boolValue",
    "measuredValue"
})
@XmlSeeAlso({
    SimpleAttribute.class
})
@XmlAccessorFactory(SimpleAttributeAccessorFactory.class)
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
public class AbstractSingleValueAttribute
    extends AbstractAttribute
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlElementRef(name = "blobValue", namespace = "http://data.mdm.unidata.com/v3/", type = JAXBElement.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected JAXBElement<BlobValue> blobValue;
    @XmlElementRef(name = "clobValue", namespace = "http://data.mdm.unidata.com/v3/", type = JAXBElement.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected JAXBElement<ClobValue> clobValue;
    @XmlElementRef(name = "intValue", namespace = "http://data.mdm.unidata.com/v3/", type = JAXBElement.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected JAXBElement<Long> intValue;
    @XmlElementRef(name = "dateValue", namespace = "http://data.mdm.unidata.com/v3/", type = JAXBElement.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected JAXBElement<XMLGregorianCalendar> dateValue;
    @XmlElementRef(name = "timeValue", namespace = "http://data.mdm.unidata.com/v3/", type = JAXBElement.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected JAXBElement<XMLGregorianCalendar> timeValue;
    @XmlElementRef(name = "timestampValue", namespace = "http://data.mdm.unidata.com/v3/", type = JAXBElement.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected JAXBElement<XMLGregorianCalendar> timestampValue;
    @XmlElementRef(name = "stringValue", namespace = "http://data.mdm.unidata.com/v3/", type = JAXBElement.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected JAXBElement<String> stringValue;
    @XmlElementRef(name = "numberValue", namespace = "http://data.mdm.unidata.com/v3/", type = JAXBElement.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected JAXBElement<Double> numberValue;
    @XmlElementRef(name = "boolValue", namespace = "http://data.mdm.unidata.com/v3/", type = JAXBElement.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected JAXBElement<Boolean> boolValue;
    @XmlElementRef(name = "measuredValue", namespace = "http://data.mdm.unidata.com/v3/", type = JAXBElement.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected JAXBElement<MeasuredValue> measuredValue;
    @XmlAttribute(name = "displayValue")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected String displayValue;
    @XmlAttribute(name = "linkEtalonId")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected String linkEtalonId;

    /**
     * Gets the value of the blobValue property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BlobValue }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public JAXBElement<BlobValue> getBlobValue() {
        return blobValue;
    }

    /**
     * Sets the value of the blobValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BlobValue }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setBlobValue(JAXBElement<BlobValue> value) {
        this.blobValue = value;
    }

    /**
     * Gets the value of the clobValue property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ClobValue }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public JAXBElement<ClobValue> getClobValue() {
        return clobValue;
    }

    /**
     * Sets the value of the clobValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ClobValue }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setClobValue(JAXBElement<ClobValue> value) {
        this.clobValue = value;
    }

    /**
     * Gets the value of the intValue property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public JAXBElement<Long> getIntValue() {
        return intValue;
    }

    /**
     * Sets the value of the intValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setIntValue(JAXBElement<Long> value) {
        this.intValue = value;
    }

    /**
     * Gets the value of the dateValue property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public JAXBElement<XMLGregorianCalendar> getDateValue() {
        return dateValue;
    }

    /**
     * Sets the value of the dateValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setDateValue(JAXBElement<XMLGregorianCalendar> value) {
        this.dateValue = value;
    }

    /**
     * Gets the value of the timeValue property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public JAXBElement<XMLGregorianCalendar> getTimeValue() {
        return timeValue;
    }

    /**
     * Sets the value of the timeValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setTimeValue(JAXBElement<XMLGregorianCalendar> value) {
        this.timeValue = value;
    }

    /**
     * Gets the value of the timestampValue property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public JAXBElement<XMLGregorianCalendar> getTimestampValue() {
        return timestampValue;
    }

    /**
     * Sets the value of the timestampValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setTimestampValue(JAXBElement<XMLGregorianCalendar> value) {
        this.timestampValue = value;
    }

    /**
     * Gets the value of the stringValue property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public JAXBElement<String> getStringValue() {
        return stringValue;
    }

    /**
     * Sets the value of the stringValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setStringValue(JAXBElement<String> value) {
        this.stringValue = value;
    }

    /**
     * Gets the value of the numberValue property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public JAXBElement<Double> getNumberValue() {
        return numberValue;
    }

    /**
     * Sets the value of the numberValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setNumberValue(JAXBElement<Double> value) {
        this.numberValue = value;
    }

    /**
     * Gets the value of the boolValue property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public JAXBElement<Boolean> getBoolValue() {
        return boolValue;
    }

    /**
     * Sets the value of the boolValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setBoolValue(JAXBElement<Boolean> value) {
        this.boolValue = value;
    }

    /**
     * Gets the value of the measuredValue property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link MeasuredValue }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public JAXBElement<MeasuredValue> getMeasuredValue() {
        return measuredValue;
    }

    /**
     * Sets the value of the measuredValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link MeasuredValue }{@code >}
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setMeasuredValue(JAXBElement<MeasuredValue> value) {
        this.measuredValue = value;
    }

    /**
     * Gets the value of the displayValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public String getDisplayValue() {
        return displayValue;
    }

    /**
     * Sets the value of the displayValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setDisplayValue(String value) {
        this.displayValue = value;
    }

    /**
     * Gets the value of the linkEtalonId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public String getLinkEtalonId() {
        return linkEtalonId;
    }

    /**
     * Sets the value of the linkEtalonId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setLinkEtalonId(String value) {
        this.linkEtalonId = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public AbstractSingleValueAttribute withBlobValue(JAXBElement<BlobValue> value) {
        setBlobValue(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public AbstractSingleValueAttribute withClobValue(JAXBElement<ClobValue> value) {
        setClobValue(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public AbstractSingleValueAttribute withIntValue(JAXBElement<Long> value) {
        setIntValue(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public AbstractSingleValueAttribute withDateValue(JAXBElement<XMLGregorianCalendar> value) {
        setDateValue(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public AbstractSingleValueAttribute withTimeValue(JAXBElement<XMLGregorianCalendar> value) {
        setTimeValue(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public AbstractSingleValueAttribute withTimestampValue(JAXBElement<XMLGregorianCalendar> value) {
        setTimestampValue(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public AbstractSingleValueAttribute withStringValue(JAXBElement<String> value) {
        setStringValue(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public AbstractSingleValueAttribute withNumberValue(JAXBElement<Double> value) {
        setNumberValue(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public AbstractSingleValueAttribute withBoolValue(JAXBElement<Boolean> value) {
        setBoolValue(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public AbstractSingleValueAttribute withMeasuredValue(JAXBElement<MeasuredValue> value) {
        setMeasuredValue(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public AbstractSingleValueAttribute withDisplayValue(String value) {
        setDisplayValue(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public AbstractSingleValueAttribute withLinkEtalonId(String value) {
        setLinkEtalonId(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public AbstractSingleValueAttribute withName(String value) {
        setName(value);
        return this;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public String toString() {
        return ToStringBuilder.reflectionToString(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
