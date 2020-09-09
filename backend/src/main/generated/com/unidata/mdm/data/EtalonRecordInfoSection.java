//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.09.09 at 03:48:37 PM MSK 
//


package com.unidata.mdm.data;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for EtalonRecordInfoSection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EtalonRecordInfoSection"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="entityName" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="createdBy" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="updatedBy" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="status" type="{http://data.mdm.unidata.com/}RecordStatus" /&gt;
 *       &lt;attribute name="approval" type="{http://data.mdm.unidata.com/}ApprovalState" /&gt;
 *       &lt;attribute name="rangeFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="rangeTo" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="createDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="updateDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EtalonRecordInfoSection")
public class EtalonRecordInfoSection implements Serializable
{

    private final static long serialVersionUID = 12345L;
    @XmlAttribute(name = "entityName")
    protected String entityName;
    @XmlAttribute(name = "createdBy")
    protected String createdBy;
    @XmlAttribute(name = "updatedBy")
    protected String updatedBy;
    @XmlAttribute(name = "status")
    protected RecordStatus status;
    @XmlAttribute(name = "approval")
    protected ApprovalState approval;
    @XmlAttribute(name = "rangeFrom")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar rangeFrom;
    @XmlAttribute(name = "rangeTo")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar rangeTo;
    @XmlAttribute(name = "createDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar createDate;
    @XmlAttribute(name = "updateDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar updateDate;

    /**
     * Gets the value of the entityName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Sets the value of the entityName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntityName(String value) {
        this.entityName = value;
    }

    /**
     * Gets the value of the createdBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the value of the createdBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedBy(String value) {
        this.createdBy = value;
    }

    /**
     * Gets the value of the updatedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Sets the value of the updatedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdatedBy(String value) {
        this.updatedBy = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link RecordStatus }
     *     
     */
    public RecordStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link RecordStatus }
     *     
     */
    public void setStatus(RecordStatus value) {
        this.status = value;
    }

    /**
     * Gets the value of the approval property.
     * 
     * @return
     *     possible object is
     *     {@link ApprovalState }
     *     
     */
    public ApprovalState getApproval() {
        return approval;
    }

    /**
     * Sets the value of the approval property.
     * 
     * @param value
     *     allowed object is
     *     {@link ApprovalState }
     *     
     */
    public void setApproval(ApprovalState value) {
        this.approval = value;
    }

    /**
     * Gets the value of the rangeFrom property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRangeFrom() {
        return rangeFrom;
    }

    /**
     * Sets the value of the rangeFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRangeFrom(XMLGregorianCalendar value) {
        this.rangeFrom = value;
    }

    /**
     * Gets the value of the rangeTo property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRangeTo() {
        return rangeTo;
    }

    /**
     * Sets the value of the rangeTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRangeTo(XMLGregorianCalendar value) {
        this.rangeTo = value;
    }

    /**
     * Gets the value of the createDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreateDate() {
        return createDate;
    }

    /**
     * Sets the value of the createDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreateDate(XMLGregorianCalendar value) {
        this.createDate = value;
    }

    /**
     * Gets the value of the updateDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getUpdateDate() {
        return updateDate;
    }

    /**
     * Sets the value of the updateDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setUpdateDate(XMLGregorianCalendar value) {
        this.updateDate = value;
    }

    public EtalonRecordInfoSection withEntityName(String value) {
        setEntityName(value);
        return this;
    }

    public EtalonRecordInfoSection withCreatedBy(String value) {
        setCreatedBy(value);
        return this;
    }

    public EtalonRecordInfoSection withUpdatedBy(String value) {
        setUpdatedBy(value);
        return this;
    }

    public EtalonRecordInfoSection withStatus(RecordStatus value) {
        setStatus(value);
        return this;
    }

    public EtalonRecordInfoSection withApproval(ApprovalState value) {
        setApproval(value);
        return this;
    }

    public EtalonRecordInfoSection withRangeFrom(XMLGregorianCalendar value) {
        setRangeFrom(value);
        return this;
    }

    public EtalonRecordInfoSection withRangeTo(XMLGregorianCalendar value) {
        setRangeTo(value);
        return this;
    }

    public EtalonRecordInfoSection withCreateDate(XMLGregorianCalendar value) {
        setCreateDate(value);
        return this;
    }

    public EtalonRecordInfoSection withUpdateDate(XMLGregorianCalendar value) {
        setUpdateDate(value);
        return this;
    }

}
