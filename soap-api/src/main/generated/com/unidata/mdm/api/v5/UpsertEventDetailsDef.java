
package com.unidata.mdm.api.v5;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.unidata.mdm.data.v5.EtalonRecord;
import com.unidata.mdm.data.v5.OriginKey;
import com.unidata.mdm.data.v5.OriginRecord;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 * Структура, описывающая детали события по изменению записи сущности. Всегда содержит исходную запись, которую затронуло событие, а также пересчитанную основную запись
 *             
 * 
 * <p>Java class for UpsertEventDetailsDef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpsertEventDetailsDef"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="originRecord" type="{http://data.mdm.unidata.com/v5/}OriginRecord"/&gt;
 *         &lt;element name="etalonRecord" type="{http://data.mdm.unidata.com/v5/}EtalonRecord"/&gt;
 *         &lt;element name="originKey" type="{http://data.mdm.unidata.com/v5/}OriginKey"/&gt;
 *         &lt;element name="supplementaryKeys" type="{http://data.mdm.unidata.com/v5/}OriginKey" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="upsertActionType" use="required" type="{http://api.mdm.unidata.com/v5/}UpsertActionType" /&gt;
 *       &lt;attribute name="operationId" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpsertEventDetailsDef", propOrder = {
    "originRecord",
    "etalonRecord",
    "originKey",
    "supplementaryKeys"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public class UpsertEventDetailsDef
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected OriginRecord originRecord;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected EtalonRecord etalonRecord;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected OriginKey originKey;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected List<OriginKey> supplementaryKeys;
    @XmlAttribute(name = "upsertActionType", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected UpsertActionType upsertActionType;
    @XmlAttribute(name = "operationId")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected String operationId;

    /**
     * Gets the value of the originRecord property.
     * 
     * @return
     *     possible object is
     *     {@link OriginRecord }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public OriginRecord getOriginRecord() {
        return originRecord;
    }

    /**
     * Sets the value of the originRecord property.
     * 
     * @param value
     *     allowed object is
     *     {@link OriginRecord }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setOriginRecord(OriginRecord value) {
        this.originRecord = value;
    }

    /**
     * Gets the value of the etalonRecord property.
     * 
     * @return
     *     possible object is
     *     {@link EtalonRecord }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public EtalonRecord getEtalonRecord() {
        return etalonRecord;
    }

    /**
     * Sets the value of the etalonRecord property.
     * 
     * @param value
     *     allowed object is
     *     {@link EtalonRecord }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setEtalonRecord(EtalonRecord value) {
        this.etalonRecord = value;
    }

    /**
     * Gets the value of the originKey property.
     * 
     * @return
     *     possible object is
     *     {@link OriginKey }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setOriginKey(OriginKey value) {
        this.originKey = value;
    }

    /**
     * Gets the value of the supplementaryKeys property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supplementaryKeys property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupplementaryKeys().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OriginKey }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public List<OriginKey> getSupplementaryKeys() {
        if (supplementaryKeys == null) {
            supplementaryKeys = new ArrayList<OriginKey>();
        }
        return this.supplementaryKeys;
    }

    /**
     * Gets the value of the upsertActionType property.
     * 
     * @return
     *     possible object is
     *     {@link UpsertActionType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UpsertActionType getUpsertActionType() {
        return upsertActionType;
    }

    /**
     * Sets the value of the upsertActionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpsertActionType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setUpsertActionType(UpsertActionType value) {
        this.upsertActionType = value;
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
    public UpsertEventDetailsDef withOriginRecord(OriginRecord value) {
        setOriginRecord(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UpsertEventDetailsDef withEtalonRecord(EtalonRecord value) {
        setEtalonRecord(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UpsertEventDetailsDef withOriginKey(OriginKey value) {
        setOriginKey(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UpsertEventDetailsDef withSupplementaryKeys(OriginKey... values) {
        if (values!= null) {
            for (OriginKey value: values) {
                getSupplementaryKeys().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UpsertEventDetailsDef withSupplementaryKeys(Collection<OriginKey> values) {
        if (values!= null) {
            getSupplementaryKeys().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UpsertEventDetailsDef withUpsertActionType(UpsertActionType value) {
        setUpsertActionType(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public UpsertEventDetailsDef withOperationId(String value) {
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
