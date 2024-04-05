//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.09.09 at 03:48:37 PM MSK 
//


package com.unidata.mdm.api;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.unidata.mdm.data.EtalonKey;
import com.unidata.mdm.data.OriginKey;


/**
 * 
 * Контейнер для одиночных связей.
 *             
 * 
 * <p>Java class for DeleteRelationRecordDef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DeleteRelationRecordDef"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="originKey" type="{http://data.mdm.unidata.com/}OriginKey"/&gt;
 *           &lt;element name="etalonKey" type="{http://data.mdm.unidata.com/}EtalonKey"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="range" type="{http://api.mdm.unidata.com/}TimeIntervalDef"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeleteRelationRecordDef", propOrder = {
    "originKey",
    "etalonKey",
    "range"
})
public class DeleteRelationRecordDef implements Serializable
{

    private final static long serialVersionUID = 12345L;
    protected OriginKey originKey;
    protected EtalonKey etalonKey;
    @XmlElement(required = true)
    protected TimeIntervalDef range;

    /**
     * Gets the value of the originKey property.
     * 
     * @return
     *     possible object is
     *     {@link OriginKey }
     *     
     */
    public OriginKey getOriginKey() {
        return originKey;
    }

    /**
     * Sets the value of the originKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link OriginKey }
     *     
     */
    public void setOriginKey(OriginKey value) {
        this.originKey = value;
    }

    /**
     * Gets the value of the etalonKey property.
     * 
     * @return
     *     possible object is
     *     {@link EtalonKey }
     *     
     */
    public EtalonKey getEtalonKey() {
        return etalonKey;
    }

    /**
     * Sets the value of the etalonKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link EtalonKey }
     *     
     */
    public void setEtalonKey(EtalonKey value) {
        this.etalonKey = value;
    }

    /**
     * Gets the value of the range property.
     * 
     * @return
     *     possible object is
     *     {@link TimeIntervalDef }
     *     
     */
    public TimeIntervalDef getRange() {
        return range;
    }

    /**
     * Sets the value of the range property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeIntervalDef }
     *     
     */
    public void setRange(TimeIntervalDef value) {
        this.range = value;
    }

    public DeleteRelationRecordDef withOriginKey(OriginKey value) {
        setOriginKey(value);
        return this;
    }

    public DeleteRelationRecordDef withEtalonKey(EtalonKey value) {
        setEtalonKey(value);
        return this;
    }

    public DeleteRelationRecordDef withRange(TimeIntervalDef value) {
        setRange(value);
        return this;
    }

}