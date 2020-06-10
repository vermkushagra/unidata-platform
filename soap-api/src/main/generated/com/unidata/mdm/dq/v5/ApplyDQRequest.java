
package com.unidata.mdm.dq.v5;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="info" type="{http://dq.mdm.unidata.com/v5/}DQApplyInfoType"/&gt;
 *         &lt;element name="payload"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence maxOccurs="500"&gt;
 *                   &lt;element name="record" type="{http://dq.mdm.unidata.com/v5/}DQRecordType"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "info",
    "payload"
})
@XmlRootElement(name = "applyDQRequest")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
public class ApplyDQRequest
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    protected DQApplyInfoType info;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    protected ApplyDQRequest.Payload payload;

    /**
     * Gets the value of the info property.
     * 
     * @return
     *     possible object is
     *     {@link DQApplyInfoType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public DQApplyInfoType getInfo() {
        return info;
    }

    /**
     * Sets the value of the info property.
     * 
     * @param value
     *     allowed object is
     *     {@link DQApplyInfoType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public void setInfo(DQApplyInfoType value) {
        this.info = value;
    }

    /**
     * Gets the value of the payload property.
     * 
     * @return
     *     possible object is
     *     {@link ApplyDQRequest.Payload }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ApplyDQRequest.Payload getPayload() {
        return payload;
    }

    /**
     * Sets the value of the payload property.
     * 
     * @param value
     *     allowed object is
     *     {@link ApplyDQRequest.Payload }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public void setPayload(ApplyDQRequest.Payload value) {
        this.payload = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ApplyDQRequest withInfo(DQApplyInfoType value) {
        setInfo(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ApplyDQRequest withPayload(ApplyDQRequest.Payload value) {
        setPayload(value);
        return this;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public String toString() {
        return ToStringBuilder.reflectionToString(this, JAXBToStringStyle.DEFAULT_STYLE);
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence maxOccurs="500"&gt;
     *         &lt;element name="record" type="{http://dq.mdm.unidata.com/v5/}DQRecordType"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "record"
    })
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public static class Payload
        implements Serializable
    {

        @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
        private final static long serialVersionUID = 12345L;
        @XmlElement(required = true)
        @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
        protected List<DQRecordType> record;

        /**
         * Gets the value of the record property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the record property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRecord().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link DQRecordType }
         * 
         * 
         */
        @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
        public List<DQRecordType> getRecord() {
            if (record == null) {
                record = new ArrayList<DQRecordType>();
            }
            return this.record;
        }

        @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
        public ApplyDQRequest.Payload withRecord(DQRecordType... values) {
            if (values!= null) {
                for (DQRecordType value: values) {
                    getRecord().add(value);
                }
            }
            return this;
        }

        @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
        public ApplyDQRequest.Payload withRecord(Collection<DQRecordType> values) {
            if (values!= null) {
                getRecord().addAll(values);
            }
            return this;
        }

        /**
         * Generates a String representation of the contents of this type.
         * This is an extension method, produced by the 'ts' xjc plugin
         * 
         */
        @Override
        @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
        public String toString() {
            return ToStringBuilder.reflectionToString(this, JAXBToStringStyle.DEFAULT_STYLE);
        }

    }

}
