
package com.unidata.mdm.meta.v5;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * <p>Java class for DQRMappingDef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DQRMappingDef"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dqrMapping" type="{http://meta.mdm.unidata.com/v5/}DQRMappingDef" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="attributeConstantValue" type="{http://meta.mdm.unidata.com/v5/}ConstantValueDef" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="attributeName" use="required" type="{http://meta.mdm.unidata.com/v5/}KeyAttribute" /&gt;
 *       &lt;attribute name="filterValue" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="inputPort" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="outputPort" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="applicationMode" type="{http://meta.mdm.unidata.com/v5/}DQCleanseFunctionPortApplicationMode" default="MODE_ALL" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DQRMappingDef", propOrder = {
    "dqrMapping",
    "attributeConstantValue"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public class DQRMappingDef
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected List<DQRMappingDef> dqrMapping;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected ConstantValueDef attributeConstantValue;
    @XmlAttribute(name = "attributeName", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected String attributeName;
    @XmlAttribute(name = "filterValue")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected String filterValue;
    @XmlAttribute(name = "inputPort")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected String inputPort;
    @XmlAttribute(name = "outputPort")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected String outputPort;
    @XmlAttribute(name = "applicationMode")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected DQCleanseFunctionPortApplicationMode applicationMode;

    /**
     * Gets the value of the dqrMapping property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dqrMapping property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDqrMapping().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DQRMappingDef }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public List<DQRMappingDef> getDqrMapping() {
        if (dqrMapping == null) {
            dqrMapping = new ArrayList<DQRMappingDef>();
        }
        return this.dqrMapping;
    }

    /**
     * Gets the value of the attributeConstantValue property.
     * 
     * @return
     *     possible object is
     *     {@link ConstantValueDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public ConstantValueDef getAttributeConstantValue() {
        return attributeConstantValue;
    }

    /**
     * Sets the value of the attributeConstantValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConstantValueDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setAttributeConstantValue(ConstantValueDef value) {
        this.attributeConstantValue = value;
    }

    /**
     * Gets the value of the attributeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Sets the value of the attributeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setAttributeName(String value) {
        this.attributeName = value;
    }

    /**
     * Gets the value of the filterValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public String getFilterValue() {
        return filterValue;
    }

    /**
     * Sets the value of the filterValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setFilterValue(String value) {
        this.filterValue = value;
    }

    /**
     * Gets the value of the inputPort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public String getInputPort() {
        return inputPort;
    }

    /**
     * Sets the value of the inputPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setInputPort(String value) {
        this.inputPort = value;
    }

    /**
     * Gets the value of the outputPort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public String getOutputPort() {
        return outputPort;
    }

    /**
     * Sets the value of the outputPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setOutputPort(String value) {
        this.outputPort = value;
    }

    /**
     * Gets the value of the applicationMode property.
     * 
     * @return
     *     possible object is
     *     {@link DQCleanseFunctionPortApplicationMode }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public DQCleanseFunctionPortApplicationMode getApplicationMode() {
        if (applicationMode == null) {
            return DQCleanseFunctionPortApplicationMode.MODE_ALL;
        } else {
            return applicationMode;
        }
    }

    /**
     * Sets the value of the applicationMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link DQCleanseFunctionPortApplicationMode }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setApplicationMode(DQCleanseFunctionPortApplicationMode value) {
        this.applicationMode = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public DQRMappingDef withDqrMapping(DQRMappingDef... values) {
        if (values!= null) {
            for (DQRMappingDef value: values) {
                getDqrMapping().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public DQRMappingDef withDqrMapping(Collection<DQRMappingDef> values) {
        if (values!= null) {
            getDqrMapping().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public DQRMappingDef withAttributeConstantValue(ConstantValueDef value) {
        setAttributeConstantValue(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public DQRMappingDef withAttributeName(String value) {
        setAttributeName(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public DQRMappingDef withFilterValue(String value) {
        setFilterValue(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public DQRMappingDef withInputPort(String value) {
        setInputPort(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public DQRMappingDef withOutputPort(String value) {
        setOutputPort(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public DQRMappingDef withApplicationMode(DQCleanseFunctionPortApplicationMode value) {
        setApplicationMode(value);
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
