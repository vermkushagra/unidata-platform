
package com.unidata.mdm.api.v5;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 * Структура, описывающая произвольное событие генерируемое платформой на изменение данных.
 *             
 * 
 * <p>Java class for UnidataMessageDef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UnidataMessageDef"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="upsertEventDetails" type="{http://api.mdm.unidata.com/v5/}UpsertEventDetailsDef"/&gt;
 *         &lt;element name="upsertEventClassifierDetails" type="{http://api.mdm.unidata.com/v5/}UpsertEventClassifierDetailsDef"/&gt;
 *         &lt;element name="UpsertEventRelationDetailsDef" type="{http://api.mdm.unidata.com/v5/}UpsertEventRelationDetailsDef"/&gt;
 *         &lt;element name="mergeEventDetails" type="{http://api.mdm.unidata.com/v5/}MergeEventDetailsDef"/&gt;
 *         &lt;element name="softDeleteEventDetails" type="{http://api.mdm.unidata.com/v5/}SoftDeleteEventDetailsDef"/&gt;
 *         &lt;element name="softDeleteClassifierEventDetails" type="{http://api.mdm.unidata.com/v5/}SoftDeleteClassifierEventDetailsDef"/&gt;
 *         &lt;element name="softDeleteRelationEventDetails" type="{http://api.mdm.unidata.com/v5/}SoftDeleteRelationEventDetailsDef"/&gt;
 *         &lt;element name="restoreEventDetails" type="{http://api.mdm.unidata.com/v5/}RestoreEventDetailsDef"/&gt;
 *         &lt;element name="wipeDeleteEventDetails" type="{http://api.mdm.unidata.com/v5/}WipeDeleteEventDetailsDef"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="eventType" use="required" type="{http://api.mdm.unidata.com/v5/}UnidataEventType" /&gt;
 *       &lt;attribute name="eventDate" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="publishDate" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="operationId" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnidataMessageDef", propOrder = {
    "upsertEventDetails",
    "upsertEventClassifierDetails",
    "upsertEventRelationDetailsDef",
    "mergeEventDetails",
    "softDeleteEventDetails",
    "softDeleteClassifierEventDetails",
    "softDeleteRelationEventDetails",
    "restoreEventDetails",
    "wipeDeleteEventDetails"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public class UnidataMessageDef implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected UpsertEventDetailsDef upsertEventDetails;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected UpsertEventClassifierDetailsDef upsertEventClassifierDetails;
    @XmlElement(name = "UpsertEventRelationDetailsDef")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected UpsertEventRelationDetailsDef upsertEventRelationDetailsDef;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected MergeEventDetailsDef mergeEventDetails;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected SoftDeleteEventDetailsDef softDeleteEventDetails;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected SoftDeleteClassifierEventDetailsDef softDeleteClassifierEventDetails;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected SoftDeleteRelationEventDetailsDef softDeleteRelationEventDetails;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected RestoreEventDetailsDef restoreEventDetails;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected WipeDeleteEventDetailsDef wipeDeleteEventDetails;
    @XmlAttribute(name = "eventType", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected UnidataEventType eventType;
    @XmlAttribute(name = "eventDate", required = true)
    @XmlSchemaType(name = "dateTime")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected XMLGregorianCalendar eventDate;
    @XmlAttribute(name = "publishDate", required = true)
    @XmlSchemaType(name = "dateTime")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected XMLGregorianCalendar publishDate;
    @XmlAttribute(name = "operationId")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected String operationId;

    /**
     * Gets the value of the upsertEventDetails property.
     * 
     * @return
     *     possible object is
     *     {@link UpsertEventDetailsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UpsertEventDetailsDef getUpsertEventDetails() {
        return upsertEventDetails;
    }

    /**
     * Sets the value of the upsertEventDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpsertEventDetailsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setUpsertEventDetails(UpsertEventDetailsDef value) {
        this.upsertEventDetails = value;
    }

    /**
     * Gets the value of the upsertEventClassifierDetails property.
     * 
     * @return
     *     possible object is
     *     {@link UpsertEventClassifierDetailsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UpsertEventClassifierDetailsDef getUpsertEventClassifierDetails() {
        return upsertEventClassifierDetails;
    }

    /**
     * Sets the value of the upsertEventClassifierDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpsertEventClassifierDetailsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setUpsertEventClassifierDetails(UpsertEventClassifierDetailsDef value) {
        this.upsertEventClassifierDetails = value;
    }

    /**
     * Gets the value of the upsertEventRelationDetailsDef property.
     * 
     * @return
     *     possible object is
     *     {@link UpsertEventRelationDetailsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UpsertEventRelationDetailsDef getUpsertEventRelationDetailsDef() {
        return upsertEventRelationDetailsDef;
    }

    /**
     * Sets the value of the upsertEventRelationDetailsDef property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpsertEventRelationDetailsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setUpsertEventRelationDetailsDef(UpsertEventRelationDetailsDef value) {
        this.upsertEventRelationDetailsDef = value;
    }

    /**
     * Gets the value of the mergeEventDetails property.
     * 
     * @return
     *     possible object is
     *     {@link MergeEventDetailsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public MergeEventDetailsDef getMergeEventDetails() {
        return mergeEventDetails;
    }

    /**
     * Sets the value of the mergeEventDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link MergeEventDetailsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setMergeEventDetails(MergeEventDetailsDef value) {
        this.mergeEventDetails = value;
    }

    /**
     * Gets the value of the softDeleteEventDetails property.
     * 
     * @return
     *     possible object is
     *     {@link SoftDeleteEventDetailsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SoftDeleteEventDetailsDef getSoftDeleteEventDetails() {
        return softDeleteEventDetails;
    }

    /**
     * Sets the value of the softDeleteEventDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link SoftDeleteEventDetailsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setSoftDeleteEventDetails(SoftDeleteEventDetailsDef value) {
        this.softDeleteEventDetails = value;
    }

    /**
     * Gets the value of the softDeleteClassifierEventDetails property.
     * 
     * @return
     *     possible object is
     *     {@link SoftDeleteClassifierEventDetailsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SoftDeleteClassifierEventDetailsDef getSoftDeleteClassifierEventDetails() {
        return softDeleteClassifierEventDetails;
    }

    /**
     * Sets the value of the softDeleteClassifierEventDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link SoftDeleteClassifierEventDetailsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setSoftDeleteClassifierEventDetails(SoftDeleteClassifierEventDetailsDef value) {
        this.softDeleteClassifierEventDetails = value;
    }

    /**
     * Gets the value of the softDeleteRelationEventDetails property.
     * 
     * @return
     *     possible object is
     *     {@link SoftDeleteRelationEventDetailsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SoftDeleteRelationEventDetailsDef getSoftDeleteRelationEventDetails() {
        return softDeleteRelationEventDetails;
    }

    /**
     * Sets the value of the softDeleteRelationEventDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link SoftDeleteRelationEventDetailsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setSoftDeleteRelationEventDetails(SoftDeleteRelationEventDetailsDef value) {
        this.softDeleteRelationEventDetails = value;
    }

    /**
     * Gets the value of the restoreEventDetails property.
     * 
     * @return
     *     possible object is
     *     {@link RestoreEventDetailsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public RestoreEventDetailsDef getRestoreEventDetails() {
        return restoreEventDetails;
    }

    /**
     * Sets the value of the restoreEventDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link RestoreEventDetailsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setRestoreEventDetails(RestoreEventDetailsDef value) {
        this.restoreEventDetails = value;
    }

    /**
     * Gets the value of the wipeDeleteEventDetails property.
     * 
     * @return
     *     possible object is
     *     {@link WipeDeleteEventDetailsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public WipeDeleteEventDetailsDef getWipeDeleteEventDetails() {
        return wipeDeleteEventDetails;
    }

    /**
     * Sets the value of the wipeDeleteEventDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link WipeDeleteEventDetailsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setWipeDeleteEventDetails(WipeDeleteEventDetailsDef value) {
        this.wipeDeleteEventDetails = value;
    }

    /**
     * Gets the value of the eventType property.
     * 
     * @return
     *     possible object is
     *     {@link UnidataEventType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UnidataEventType getEventType() {
        return eventType;
    }

    /**
     * Sets the value of the eventType property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnidataEventType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setEventType(UnidataEventType value) {
        this.eventType = value;
    }

    /**
     * Gets the value of the eventDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public XMLGregorianCalendar getEventDate() {
        return eventDate;
    }

    /**
     * Sets the value of the eventDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setEventDate(XMLGregorianCalendar value) {
        this.eventDate = value;
    }

    /**
     * Gets the value of the publishDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public XMLGregorianCalendar getPublishDate() {
        return publishDate;
    }

    /**
     * Sets the value of the publishDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setPublishDate(XMLGregorianCalendar value) {
        this.publishDate = value;
    }

    /**
     * Gets the value of the operationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setOperationId(String value) {
        this.operationId = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UnidataMessageDef withUpsertEventDetails(UpsertEventDetailsDef value) {
        setUpsertEventDetails(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UnidataMessageDef withUpsertEventClassifierDetails(UpsertEventClassifierDetailsDef value) {
        setUpsertEventClassifierDetails(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UnidataMessageDef withUpsertEventRelationDetailsDef(UpsertEventRelationDetailsDef value) {
        setUpsertEventRelationDetailsDef(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UnidataMessageDef withMergeEventDetails(MergeEventDetailsDef value) {
        setMergeEventDetails(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UnidataMessageDef withSoftDeleteEventDetails(SoftDeleteEventDetailsDef value) {
        setSoftDeleteEventDetails(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UnidataMessageDef withSoftDeleteClassifierEventDetails(SoftDeleteClassifierEventDetailsDef value) {
        setSoftDeleteClassifierEventDetails(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UnidataMessageDef withSoftDeleteRelationEventDetails(SoftDeleteRelationEventDetailsDef value) {
        setSoftDeleteRelationEventDetails(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UnidataMessageDef withRestoreEventDetails(RestoreEventDetailsDef value) {
        setRestoreEventDetails(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UnidataMessageDef withWipeDeleteEventDetails(WipeDeleteEventDetailsDef value) {
        setWipeDeleteEventDetails(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UnidataMessageDef withEventType(UnidataEventType value) {
        setEventType(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UnidataMessageDef withEventDate(XMLGregorianCalendar value) {
        setEventDate(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UnidataMessageDef withPublishDate(XMLGregorianCalendar value) {
        setPublishDate(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UnidataMessageDef withOperationId(String value) {
        setOperationId(value);
        return this;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public String toString() {
        return ToStringBuilder.reflectionToString(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
