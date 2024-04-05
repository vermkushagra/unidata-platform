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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import com.sun.xml.bind.XmlAccessorFactory;


/**
 * 
 * Базовая структура, описывающая кодовый атрибут справочника. Состоит из обязательного имени, кодового значения и массива значений в зависимости от типа данных
 *             
 * 
 * <p>Java class for AbstractCodeAttribute complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractCodeAttribute"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://data.mdm.unidata.com/}AbstractAttribute"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="intValue" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *           &lt;element name="stringValue" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;/choice&gt;
 *         &lt;choice&gt;
 *           &lt;element name="supplementaryIntValues" type="{http://www.w3.org/2001/XMLSchema}long" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="supplementaryStringValues" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "AbstractCodeAttribute", propOrder = {
    "intValue",
    "stringValue",
    "supplementaryIntValues",
    "supplementaryStringValues"
})
@XmlSeeAlso({
    CodeAttribute.class
})
@XmlAccessorFactory(CodeAttributeAccessorFactory.class)
public class AbstractCodeAttribute
    extends AbstractAttribute
    implements Serializable
{

    private final static long serialVersionUID = 12345L;
    protected Long intValue;
    protected String stringValue;
    @XmlElement(type = Long.class)
    protected List<Long> supplementaryIntValues;
    protected List<String> supplementaryStringValues;

    /**
     * Gets the value of the intValue property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIntValue() {
        return intValue;
    }

    /**
     * Sets the value of the intValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIntValue(Long value) {
        this.intValue = value;
    }

    /**
     * Gets the value of the stringValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStringValue() {
        return stringValue;
    }

    /**
     * Sets the value of the stringValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStringValue(String value) {
        this.stringValue = value;
    }

    /**
     * Gets the value of the supplementaryIntValues property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supplementaryIntValues property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupplementaryIntValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getSupplementaryIntValues() {
        if (supplementaryIntValues == null) {
            supplementaryIntValues = new ArrayList<Long>();
        }
        return this.supplementaryIntValues;
    }

    /**
     * Gets the value of the supplementaryStringValues property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supplementaryStringValues property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupplementaryStringValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSupplementaryStringValues() {
        if (supplementaryStringValues == null) {
            supplementaryStringValues = new ArrayList<String>();
        }
        return this.supplementaryStringValues;
    }

    public AbstractCodeAttribute withIntValue(Long value) {
        setIntValue(value);
        return this;
    }

    public AbstractCodeAttribute withStringValue(String value) {
        setStringValue(value);
        return this;
    }

    public AbstractCodeAttribute withSupplementaryIntValues(Long... values) {
        if (values!= null) {
            for (Long value: values) {
                getSupplementaryIntValues().add(value);
            }
        }
        return this;
    }

    public AbstractCodeAttribute withSupplementaryIntValues(Collection<Long> values) {
        if (values!= null) {
            getSupplementaryIntValues().addAll(values);
        }
        return this;
    }

    public AbstractCodeAttribute withSupplementaryStringValues(String... values) {
        if (values!= null) {
            for (String value: values) {
                getSupplementaryStringValues().add(value);
            }
        }
        return this;
    }

    public AbstractCodeAttribute withSupplementaryStringValues(Collection<String> values) {
        if (values!= null) {
            getSupplementaryStringValues().addAll(values);
        }
        return this;
    }

    @Override
    public AbstractCodeAttribute withName(String value) {
        setName(value);
        return this;
    }

}