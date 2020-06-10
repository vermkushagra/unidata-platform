
package com.unidata.mdm.api.v4;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import com.unidata.mdm.data.v4.IntegralRecord;
import com.unidata.mdm.data.v4.RelationBase;
import com.unidata.mdm.data.v4.RelationTo;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 * Контейнер для одиночных связей.
 *             
 * 
 * <p>Java class for UpsertRelationRecordDef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpsertRelationRecordDef"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="integralEntity" type="{http://data.mdm.unidata.com/v4/}IntegralRecord"/&gt;
 *           &lt;element name="relationTo" type="{http://data.mdm.unidata.com/v4/}RelationTo"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="range" type="{http://api.mdm.unidata.com/v4/}TimeIntervalDef"/&gt;
 *         &lt;element name="referenceAliasKey" type="{http://api.mdm.unidata.com/v4/}ReferenceAliasKey"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpsertRelationRecordDef", propOrder = {
    "record",
    "range",
    "referenceAliasKey"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
public class UpsertRelationRecordDef
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlElements({
        @XmlElement(name = "integralEntity", type = IntegralRecord.class),
        @XmlElement(name = "relationTo", type = RelationTo.class)
    })
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RelationBase record;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected TimeIntervalDef range;
    @XmlElement(required = true, nillable = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected ReferenceAliasKey referenceAliasKey;

    /**
     * Gets the value of the record property.
     * 
     * @return
     *     possible object is
     *     {@link IntegralRecord }
     *     {@link RelationTo }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RelationBase getRecord() {
        return record;
    }

    /**
     * Sets the value of the record property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegralRecord }
     *     {@link RelationTo }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRecord(RelationBase value) {
        this.record = value;
    }

    /**
     * Gets the value of the range property.
     * 
     * @return
     *     possible object is
     *     {@link TimeIntervalDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRange(TimeIntervalDef value) {
        this.range = value;
    }

    /**
     * Gets the value of the referenceAliasKey property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceAliasKey }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public ReferenceAliasKey getReferenceAliasKey() {
        return referenceAliasKey;
    }

    /**
     * Sets the value of the referenceAliasKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceAliasKey }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setReferenceAliasKey(ReferenceAliasKey value) {
        this.referenceAliasKey = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UpsertRelationRecordDef withRecord(RelationBase value) {
        setRecord(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UpsertRelationRecordDef withRange(TimeIntervalDef value) {
        setRange(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UpsertRelationRecordDef withReferenceAliasKey(ReferenceAliasKey value) {
        setReferenceAliasKey(value);
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
