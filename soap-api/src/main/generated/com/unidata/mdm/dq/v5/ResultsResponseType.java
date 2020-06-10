
package com.unidata.mdm.dq.v5;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.unidata.mdm.data.v5.DataQualityError;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * <p>Java class for ResultsResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResultsResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="recordErrors" type="{http://data.mdm.unidata.com/v5/}DataQualityError" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="classifierErrors" type="{http://dq.mdm.unidata.com/v5/}ClassifierDQErrors" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResultsResponseType", propOrder = {
    "recordErrors",
    "classifierErrors"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
public class ResultsResponseType
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    protected List<DataQualityError> recordErrors;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    protected List<ClassifierDQErrors> classifierErrors;

    /**
     * Gets the value of the recordErrors property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the recordErrors property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRecordErrors().add(newItem);
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
    public List<DataQualityError> getRecordErrors() {
        if (recordErrors == null) {
            recordErrors = new ArrayList<DataQualityError>();
        }
        return this.recordErrors;
    }

    /**
     * Gets the value of the classifierErrors property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classifierErrors property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClassifierErrors().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClassifierDQErrors }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public List<ClassifierDQErrors> getClassifierErrors() {
        if (classifierErrors == null) {
            classifierErrors = new ArrayList<ClassifierDQErrors>();
        }
        return this.classifierErrors;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ResultsResponseType withRecordErrors(DataQualityError... values) {
        if (values!= null) {
            for (DataQualityError value: values) {
                getRecordErrors().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ResultsResponseType withRecordErrors(Collection<DataQualityError> values) {
        if (values!= null) {
            getRecordErrors().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ResultsResponseType withClassifierErrors(ClassifierDQErrors... values) {
        if (values!= null) {
            for (ClassifierDQErrors value: values) {
                getClassifierErrors().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ResultsResponseType withClassifierErrors(Collection<ClassifierDQErrors> values) {
        if (values!= null) {
            getClassifierErrors().addAll(values);
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
