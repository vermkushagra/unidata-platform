
package com.unidata.mdm.api.v4;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.unidata.mdm.data.v4.EtalonKey;
import com.unidata.mdm.data.v4.OriginKey;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


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
 *         &lt;element name="fromOriginKey" type="{http://data.mdm.unidata.com/v4/}OriginKey" minOccurs="0"/&gt;
 *         &lt;element name="fromEtalonKey" type="{http://data.mdm.unidata.com/v4/}EtalonKey"/&gt;
 *         &lt;element name="toOriginKey" type="{http://data.mdm.unidata.com/v4/}OriginKey" minOccurs="0"/&gt;
 *         &lt;element name="toEtalonKey" type="{http://data.mdm.unidata.com/v4/}EtalonKey"/&gt;
 *         &lt;element name="relationType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="SoftDeleteActionType" use="required" type="{http://api.mdm.unidata.com/v4/}SoftDeleteActionType" /&gt;
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
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
public class SoftDeleteRelationEventDetailsDef
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected OriginKey fromOriginKey;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected EtalonKey fromEtalonKey;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected OriginKey toOriginKey;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected EtalonKey toEtalonKey;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected String relationType;
    @XmlAttribute(name = "SoftDeleteActionType", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected SoftDeleteActionType softDeleteActionType;
    @XmlAttribute(name = "operationId")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected String operationId;

    /**
     * Gets the value of the fromOriginKey property.
     * 
     * @return
     *     possible object is
     *     {@link OriginKey }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setOperationId(String value) {
        this.operationId = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public SoftDeleteRelationEventDetailsDef withFromOriginKey(OriginKey value) {
        setFromOriginKey(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public SoftDeleteRelationEventDetailsDef withFromEtalonKey(EtalonKey value) {
        setFromEtalonKey(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public SoftDeleteRelationEventDetailsDef withToOriginKey(OriginKey value) {
        setToOriginKey(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public SoftDeleteRelationEventDetailsDef withToEtalonKey(EtalonKey value) {
        setToEtalonKey(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public SoftDeleteRelationEventDetailsDef withRelationType(String value) {
        setRelationType(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public SoftDeleteRelationEventDetailsDef withSoftDeleteActionType(SoftDeleteActionType value) {
        setSoftDeleteActionType(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public SoftDeleteRelationEventDetailsDef withOperationId(String value) {
        setOperationId(value);
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
