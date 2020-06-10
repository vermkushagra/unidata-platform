
package com.unidata.mdm.data.v4;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import com.unidata.mdm.dq.v4.DQRecordType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 * Рекурсивная структура, описывающая запись произвольной вложенной сущности. Состоит из произвольного количества простых атрибутов, а также сложных атрибутов, обеспечивающих рекурсивного погружение.
 *             
 * 
 * <p>Java class for NestedRecord complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NestedRecord"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="simpleAttribute" type="{http://data.mdm.unidata.com/v4/}SimpleAttribute" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="codeAttribute" type="{http://data.mdm.unidata.com/v4/}CodeAttribute" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="arrayAttribute" type="{http://data.mdm.unidata.com/v4/}ArrayAttribute" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="complexAttribute" type="{http://data.mdm.unidata.com/v4/}ComplexAttribute" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NestedRecord", propOrder = {
    "id",
    "simpleAttributes",
    "codeAttributes",
    "arrayAttributes",
    "complexAttributes"
})
@XmlSeeAlso({
    DQRecordType.class,
    EtalonRecord.class,
    OriginRecord.class,
    ClassifierRecord.class
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
public class NestedRecord
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected String id;
    @XmlElement(name = "simpleAttribute")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected List<SimpleAttribute> simpleAttributes;
    @XmlElement(name = "codeAttribute")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected List<CodeAttribute> codeAttributes;
    @XmlElement(name = "arrayAttribute")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected List<ArrayAttribute> arrayAttributes;
    @XmlElement(name = "complexAttribute")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected List<ComplexAttribute> complexAttributes;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the simpleAttributes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the simpleAttributes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSimpleAttributes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SimpleAttribute }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public List<SimpleAttribute> getSimpleAttributes() {
        if (simpleAttributes == null) {
            simpleAttributes = new ArrayList<SimpleAttribute>();
        }
        return this.simpleAttributes;
    }

    /**
     * Gets the value of the codeAttributes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the codeAttributes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCodeAttributes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CodeAttribute }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public List<CodeAttribute> getCodeAttributes() {
        if (codeAttributes == null) {
            codeAttributes = new ArrayList<CodeAttribute>();
        }
        return this.codeAttributes;
    }

    /**
     * Gets the value of the arrayAttributes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the arrayAttributes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArrayAttributes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArrayAttribute }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public List<ArrayAttribute> getArrayAttributes() {
        if (arrayAttributes == null) {
            arrayAttributes = new ArrayList<ArrayAttribute>();
        }
        return this.arrayAttributes;
    }

    /**
     * Gets the value of the complexAttributes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the complexAttributes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComplexAttributes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ComplexAttribute }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public List<ComplexAttribute> getComplexAttributes() {
        if (complexAttributes == null) {
            complexAttributes = new ArrayList<ComplexAttribute>();
        }
        return this.complexAttributes;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public NestedRecord withId(String value) {
        setId(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public NestedRecord withSimpleAttributes(SimpleAttribute... values) {
        if (values!= null) {
            for (SimpleAttribute value: values) {
                getSimpleAttributes().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public NestedRecord withSimpleAttributes(Collection<SimpleAttribute> values) {
        if (values!= null) {
            getSimpleAttributes().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public NestedRecord withCodeAttributes(CodeAttribute... values) {
        if (values!= null) {
            for (CodeAttribute value: values) {
                getCodeAttributes().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public NestedRecord withCodeAttributes(Collection<CodeAttribute> values) {
        if (values!= null) {
            getCodeAttributes().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public NestedRecord withArrayAttributes(ArrayAttribute... values) {
        if (values!= null) {
            for (ArrayAttribute value: values) {
                getArrayAttributes().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public NestedRecord withArrayAttributes(Collection<ArrayAttribute> values) {
        if (values!= null) {
            getArrayAttributes().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public NestedRecord withComplexAttributes(ComplexAttribute... values) {
        if (values!= null) {
            for (ComplexAttribute value: values) {
                getComplexAttributes().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public NestedRecord withComplexAttributes(Collection<ComplexAttribute> values) {
        if (values!= null) {
            getComplexAttributes().addAll(values);
        }
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
