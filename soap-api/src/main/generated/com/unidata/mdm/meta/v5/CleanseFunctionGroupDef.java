
package com.unidata.mdm.meta.v5;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * <p>Java class for CleanseFunctionGroupDef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CleanseFunctionGroupDef"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://meta.mdm.unidata.com/v5/}VersionedObjectDef"&gt;
 *       &lt;choice maxOccurs="unbounded"&gt;
 *         &lt;element name="group" type="{http://meta.mdm.unidata.com/v5/}CleanseFunctionGroupDef"/&gt;
 *         &lt;element name="cleanseFunction" type="{http://meta.mdm.unidata.com/v5/}CleanseFunctionDef"/&gt;
 *         &lt;element name="compositeCleanseFunction" type="{http://meta.mdm.unidata.com/v5/}CompositeCleanseFunctionDef"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="groupName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CleanseFunctionGroupDef", propOrder = {
    "groupOrCleanseFunctionOrCompositeCleanseFunction"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public class CleanseFunctionGroupDef
    extends VersionedObjectDef
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlElements({
        @XmlElement(name = "group", type = CleanseFunctionGroupDef.class),
        @XmlElement(name = "cleanseFunction", type = CleanseFunctionDef.class),
        @XmlElement(name = "compositeCleanseFunction", type = CompositeCleanseFunctionDef.class)
    })
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected List<Serializable> groupOrCleanseFunctionOrCompositeCleanseFunction;
    @XmlAttribute(name = "groupName", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected String groupName;
    @XmlAttribute(name = "description")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected String description;

    /**
     * Gets the value of the groupOrCleanseFunctionOrCompositeCleanseFunction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groupOrCleanseFunctionOrCompositeCleanseFunction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroupOrCleanseFunctionOrCompositeCleanseFunction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CleanseFunctionGroupDef }
     * {@link CleanseFunctionDef }
     * {@link CompositeCleanseFunctionDef }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public List<Serializable> getGroupOrCleanseFunctionOrCompositeCleanseFunction() {
        if (groupOrCleanseFunctionOrCompositeCleanseFunction == null) {
            groupOrCleanseFunctionOrCompositeCleanseFunction = new ArrayList<Serializable>();
        }
        return this.groupOrCleanseFunctionOrCompositeCleanseFunction;
    }

    /**
     * Gets the value of the groupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets the value of the groupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setGroupName(String value) {
        this.groupName = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setDescription(String value) {
        this.description = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public CleanseFunctionGroupDef withGroupOrCleanseFunctionOrCompositeCleanseFunction(Serializable... values) {
        if (values!= null) {
            for (Serializable value: values) {
                getGroupOrCleanseFunctionOrCompositeCleanseFunction().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public CleanseFunctionGroupDef withGroupOrCleanseFunctionOrCompositeCleanseFunction(Collection<Serializable> values) {
        if (values!= null) {
            getGroupOrCleanseFunctionOrCompositeCleanseFunction().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public CleanseFunctionGroupDef withGroupName(String value) {
        setGroupName(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public CleanseFunctionGroupDef withDescription(String value) {
        setDescription(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public CleanseFunctionGroupDef withVersion(Long value) {
        setVersion(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public CleanseFunctionGroupDef withUpdatedAt(XMLGregorianCalendar value) {
        setUpdatedAt(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public CleanseFunctionGroupDef withCreateAt(XMLGregorianCalendar value) {
        setCreateAt(value);
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
