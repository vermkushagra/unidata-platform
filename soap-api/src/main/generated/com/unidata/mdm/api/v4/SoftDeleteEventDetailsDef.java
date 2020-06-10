
package com.unidata.mdm.api.v4;

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
import com.unidata.mdm.data.v4.EtalonKey;
import com.unidata.mdm.data.v4.EtalonRecord;
import com.unidata.mdm.data.v4.OriginKey;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 *                 Структура, описывающая детали события по удалению записи сущности. Всегда содержит ключи удаленных записей.
 *             
 * 
 * <p>Java class for SoftDeleteEventDetailsDef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SoftDeleteEventDetailsDef"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="etalonKey" type="{http://data.mdm.unidata.com/v4/}EtalonKey"/&gt;
 *         &lt;element name="originKey" type="{http://data.mdm.unidata.com/v4/}OriginKey" minOccurs="0"/&gt;
 *         &lt;element name="etalonRecord" type="{http://data.mdm.unidata.com/v4/}EtalonRecord" minOccurs="0"/&gt;
 *         &lt;element name="supplementaryKeys" type="{http://data.mdm.unidata.com/v4/}OriginKey" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "SoftDeleteEventDetailsDef", propOrder = {
    "etalonKey",
    "originKey",
    "etalonRecord",
    "supplementaryKeys"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
public class SoftDeleteEventDetailsDef
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected EtalonKey etalonKey;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected OriginKey originKey;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected EtalonRecord etalonRecord;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected List<OriginKey> supplementaryKeys;
    @XmlAttribute(name = "SoftDeleteActionType", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected SoftDeleteActionType softDeleteActionType;
    @XmlAttribute(name = "operationId")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected String operationId;

    /**
     * Gets the value of the etalonKey property.
     * 
     * @return
     *     possible object is
     *     {@link EtalonKey }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setEtalonKey(EtalonKey value) {
        this.etalonKey = value;
    }

    /**
     * Gets the value of the originKey property.
     * 
     * @return
     *     possible object is
     *     {@link OriginKey }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setOriginKey(OriginKey value) {
        this.originKey = value;
    }

    /**
     * Gets the value of the etalonRecord property.
     * 
     * @return
     *     possible object is
     *     {@link EtalonRecord }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setEtalonRecord(EtalonRecord value) {
        this.etalonRecord = value;
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public List<OriginKey> getSupplementaryKeys() {
        if (supplementaryKeys == null) {
            supplementaryKeys = new ArrayList<OriginKey>();
        }
        return this.supplementaryKeys;
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
    public SoftDeleteEventDetailsDef withEtalonKey(EtalonKey value) {
        setEtalonKey(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public SoftDeleteEventDetailsDef withOriginKey(OriginKey value) {
        setOriginKey(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public SoftDeleteEventDetailsDef withEtalonRecord(EtalonRecord value) {
        setEtalonRecord(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public SoftDeleteEventDetailsDef withSupplementaryKeys(OriginKey... values) {
        if (values!= null) {
            for (OriginKey value: values) {
                getSupplementaryKeys().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public SoftDeleteEventDetailsDef withSupplementaryKeys(Collection<OriginKey> values) {
        if (values!= null) {
            getSupplementaryKeys().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public SoftDeleteEventDetailsDef withSoftDeleteActionType(SoftDeleteActionType value) {
        setSoftDeleteActionType(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public SoftDeleteEventDetailsDef withOperationId(String value) {
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
