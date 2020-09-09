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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.unidata.mdm.data.EtalonKey;
import com.unidata.mdm.data.OriginKey;


/**
 * 
 *                 Структура, описывающая детали события по удалению связи.
 *             
 * 
 * <p>Java class for SoftDeleteRelationEventDetailsDef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SoftDeleteRelationEventDetailsDef"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="fromOriginKey" type="{http://data.mdm.unidata.com/}OriginKey" minOccurs="0"/&gt;
 *         &lt;element name="fromEtalonKey" type="{http://data.mdm.unidata.com/}EtalonKey"/&gt;
 *         &lt;element name="toOriginKey" type="{http://data.mdm.unidata.com/}OriginKey" minOccurs="0"/&gt;
 *         &lt;element name="toEtalonKey" type="{http://data.mdm.unidata.com/}EtalonKey"/&gt;
 *         &lt;element name="relationType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="SoftDeleteActionType" use="required" type="{http://api.mdm.unidata.com/}SoftDeleteActionType" /&gt;
 *       &lt;attribute name="operationId" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SoftDeleteRelationEventDetailsDef", propOrder = {
    "fromOriginKey",
    "fromEtalonKey",
    "toOriginKey",
    "toEtalonKey",
    "relationType"
})
public class SoftDeleteRelationEventDetailsDef implements Serializable
{

    private final static long serialVersionUID = 12345L;
    protected OriginKey fromOriginKey;
    @XmlElement(required = true)
    protected EtalonKey fromEtalonKey;
    protected OriginKey toOriginKey;
    @XmlElement(required = true)
    protected EtalonKey toEtalonKey;
    @XmlElement(required = true)
    protected String relationType;
    @XmlAttribute(name = "SoftDeleteActionType", required = true)
    protected SoftDeleteActionType softDeleteActionType;
    @XmlAttribute(name = "operationId")
    protected String operationId;

    /**
     * Gets the value of the fromOriginKey property.
     * 
     * @return
     *     possible object is
     *     {@link OriginKey }
     *     
     */
    public OriginKey getFromOriginKey() {
        return fromOriginKey;
    }

    /**
     * Sets the value of the fromOriginKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link OriginKey }
     *     
     */
    public void setFromOriginKey(OriginKey value) {
        this.fromOriginKey = value;
    }

    /**
     * Gets the value of the fromEtalonKey property.
     * 
     * @return
     *     possible object is
     *     {@link EtalonKey }
     *     
     */
    public EtalonKey getFromEtalonKey() {
        return fromEtalonKey;
    }

    /**
     * Sets the value of the fromEtalonKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link EtalonKey }
     *     
     */
    public void setFromEtalonKey(EtalonKey value) {
        this.fromEtalonKey = value;
    }

    /**
     * Gets the value of the toOriginKey property.
     * 
     * @return
     *     possible object is
     *     {@link OriginKey }
     *     
     */
    public OriginKey getToOriginKey() {
        return toOriginKey;
    }

    /**
     * Sets the value of the toOriginKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link OriginKey }
     *     
     */
    public void setToOriginKey(OriginKey value) {
        this.toOriginKey = value;
    }

    /**
     * Gets the value of the toEtalonKey property.
     * 
     * @return
     *     possible object is
     *     {@link EtalonKey }
     *     
     */
    public EtalonKey getToEtalonKey() {
        return toEtalonKey;
    }

    /**
     * Sets the value of the toEtalonKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link EtalonKey }
     *     
     */
    public void setToEtalonKey(EtalonKey value) {
        this.toEtalonKey = value;
    }

    /**
     * Gets the value of the relationType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelationType() {
        return relationType;
    }

    /**
     * Sets the value of the relationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelationType(String value) {
        this.relationType = value;
    }

    /**
     * Gets the value of the softDeleteActionType property.
     * 
     * @return
     *     possible object is
     *     {@link SoftDeleteActionType }
     *     
     */
    public SoftDeleteActionType getSoftDeleteActionType() {
        return softDeleteActionType;
    }

    /**
     * Sets the value of the softDeleteActionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link SoftDeleteActionType }
     *     
     */
    public void setSoftDeleteActionType(SoftDeleteActionType value) {
        this.softDeleteActionType = value;
    }

    /**
     * Gets the value of the operationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationId() {
        return operationId;
    }

    /**
     * Sets the value of the operationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationId(String value) {
        this.operationId = value;
    }

    public SoftDeleteRelationEventDetailsDef withFromOriginKey(OriginKey value) {
        setFromOriginKey(value);
        return this;
    }

    public SoftDeleteRelationEventDetailsDef withFromEtalonKey(EtalonKey value) {
        setFromEtalonKey(value);
        return this;
    }

    public SoftDeleteRelationEventDetailsDef withToOriginKey(OriginKey value) {
        setToOriginKey(value);
        return this;
    }

    public SoftDeleteRelationEventDetailsDef withToEtalonKey(EtalonKey value) {
        setToEtalonKey(value);
        return this;
    }

    public SoftDeleteRelationEventDetailsDef withRelationType(String value) {
        setRelationType(value);
        return this;
    }

    public SoftDeleteRelationEventDetailsDef withSoftDeleteActionType(SoftDeleteActionType value) {
        setSoftDeleteActionType(value);
        return this;
    }

    public SoftDeleteRelationEventDetailsDef withOperationId(String value) {
        setOperationId(value);
        return this;
    }

}
