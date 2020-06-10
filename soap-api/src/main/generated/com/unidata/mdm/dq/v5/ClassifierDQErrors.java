
package com.unidata.mdm.dq.v5;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import com.unidata.mdm.data.v5.DataQualityError;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * <p>Java class for ClassifierDQErrors complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClassifierDQErrors"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="errors" type="{http://data.mdm.unidata.com/v5/}DataQualityError" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="classifierName" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="classifierNodeId" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="classifierNodePath" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClassifierDQErrors", propOrder = {
    "errors"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
public class ClassifierDQErrors
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    protected List<DataQualityError> errors;
    @XmlAttribute(name = "classifierName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    protected String classifierName;
    @XmlAttribute(name = "classifierNodeId")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    protected String classifierNodeId;
    @XmlAttribute(name = "classifierNodePath")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    protected String classifierNodePath;

    /**
     * Gets the value of the errors property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the errors property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getErrors().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataQualityError }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public List<DataQualityError> getErrors() {
        if (errors == null) {
            errors = new ArrayList<DataQualityError>();
        }
        return this.errors;
    }

    /**
     * Gets the value of the classifierName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public String getClassifierName() {
        return classifierName;
    }

    /**
     * Sets the value of the classifierName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public void setClassifierName(String value) {
        this.classifierName = value;
    }

    /**
     * Gets the value of the classifierNodeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public String getClassifierNodeId() {
        return classifierNodeId;
    }

    /**
     * Sets the value of the classifierNodeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public void setClassifierNodeId(String value) {
        this.classifierNodeId = value;
    }

    /**
     * Gets the value of the classifierNodePath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public String getClassifierNodePath() {
        return classifierNodePath;
    }

    /**
     * Sets the value of the classifierNodePath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public void setClassifierNodePath(String value) {
        this.classifierNodePath = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ClassifierDQErrors withErrors(DataQualityError... values) {
        if (values!= null) {
            for (DataQualityError value: values) {
                getErrors().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ClassifierDQErrors withErrors(Collection<DataQualityError> values) {
        if (values!= null) {
            getErrors().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ClassifierDQErrors withClassifierName(String value) {
        setClassifierName(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ClassifierDQErrors withClassifierNodeId(String value) {
        setClassifierNodeId(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ClassifierDQErrors withClassifierNodePath(String value) {
        setClassifierNodePath(value);
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
