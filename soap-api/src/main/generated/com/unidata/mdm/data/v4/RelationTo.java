
package com.unidata.mdm.data.v4;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 * Структура, описывающая связь между сущностями. Используется для представления связи в композитных сущностях. Состоит из обязательного имени связи, а также ключа для идентификации либо основной, либо исходной записи сущности.
 * Помимо этого, сама связь может содержать, как простые так и сложные атрибуты.
 * Примером такой связи между сущностями может служить связь, сущностями 'сотрудник' и 'отдел', каждая из которых имеет свой жизненный цикл.
 *             
 * 
 * <p>Java class for RelationTo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RelationTo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://data.mdm.unidata.com/v4/}RelationBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="toEtalonKey" type="{http://data.mdm.unidata.com/v4/}EtalonKey"/&gt;
 *           &lt;element name="toOriginKey" type="{http://data.mdm.unidata.com/v4/}OriginKey"/&gt;
 *         &lt;/choice&gt;
 *         &lt;sequence&gt;
 *           &lt;element name="simpleAttributes" type="{http://data.mdm.unidata.com/v4/}SimpleAttribute" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="complexAttributes" type="{http://data.mdm.unidata.com/v4/}ComplexAttribute" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="infoSection" type="{http://data.mdm.unidata.com/v4/}RelationToInfoSection" minOccurs="0"/&gt;
 *         &lt;/sequence&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RelationTo", propOrder = {
    "toEtalonKey",
    "toOriginKey",
    "simpleAttributes",
    "complexAttributes",
    "infoSection"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
public class RelationTo
    extends RelationBase
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected EtalonKey toEtalonKey;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected OriginKey toOriginKey;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected List<SimpleAttribute> simpleAttributes;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected List<ComplexAttribute> complexAttributes;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RelationToInfoSection infoSection;

    /**
     * Gets the value of the toEtalonKey property.
     * 
     * @return
     *     possible object is
     *     {@link EtalonKey }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public EtalonKey getToEtalonKey() {
        return toEtalonKey;
    }

    /**
     * Sets the value of the toEtalonKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link EtalonKey }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setToEtalonKey(EtalonKey value) {
        this.toEtalonKey = value;
    }

    /**
     * Gets the value of the toOriginKey property.
     * 
     * @return
     *     possible object is
     *     {@link OriginKey }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public OriginKey getToOriginKey() {
        return toOriginKey;
    }

    /**
     * Sets the value of the toOriginKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link OriginKey }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setToOriginKey(OriginKey value) {
        this.toOriginKey = value;
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

    /**
     * Gets the value of the infoSection property.
     * 
     * @return
     *     possible object is
     *     {@link RelationToInfoSection }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RelationToInfoSection getInfoSection() {
        return infoSection;
    }

    /**
     * Sets the value of the infoSection property.
     * 
     * @param value
     *     allowed object is
     *     {@link RelationToInfoSection }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setInfoSection(RelationToInfoSection value) {
        this.infoSection = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RelationTo withToEtalonKey(EtalonKey value) {
        setToEtalonKey(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RelationTo withToOriginKey(OriginKey value) {
        setToOriginKey(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RelationTo withSimpleAttributes(SimpleAttribute... values) {
        if (values!= null) {
            for (SimpleAttribute value: values) {
                getSimpleAttributes().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RelationTo withSimpleAttributes(Collection<SimpleAttribute> values) {
        if (values!= null) {
            getSimpleAttributes().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RelationTo withComplexAttributes(ComplexAttribute... values) {
        if (values!= null) {
            for (ComplexAttribute value: values) {
                getComplexAttributes().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RelationTo withComplexAttributes(Collection<ComplexAttribute> values) {
        if (values!= null) {
            getComplexAttributes().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RelationTo withInfoSection(RelationToInfoSection value) {
        setInfoSection(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RelationTo withRelName(String value) {
        setRelName(value);
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
