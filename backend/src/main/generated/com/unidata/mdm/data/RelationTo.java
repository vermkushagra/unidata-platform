//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.09.09 at 03:48:37 PM MSK 
//


package com.unidata.mdm.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


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
 *     &lt;extension base="{http://data.mdm.unidata.com/}RelationBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="toEtalonKey" type="{http://data.mdm.unidata.com/}EtalonKey"/&gt;
 *           &lt;element name="toOriginKey" type="{http://data.mdm.unidata.com/}OriginKey"/&gt;
 *         &lt;/choice&gt;
 *         &lt;sequence&gt;
 *           &lt;element name="simpleAttributes" type="{http://data.mdm.unidata.com/}SimpleAttribute" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="complexAttributes" type="{http://data.mdm.unidata.com/}ComplexAttribute" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="infoSection" type="{http://data.mdm.unidata.com/}RelationToInfoSection" minOccurs="0"/&gt;
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
public class RelationTo
    extends RelationBase
    implements Serializable
{

    private final static long serialVersionUID = 12345L;
    protected EtalonKey toEtalonKey;
    protected OriginKey toOriginKey;
    protected List<SimpleAttribute> simpleAttributes;
    protected List<ComplexAttribute> complexAttributes;
    protected RelationToInfoSection infoSection;

    /**
     * Gets the value of the toEtalonKey property.
     * 
     * @return
     *     possible object is
     *     {@link EtalonKey }
     *     
     */
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
    public void setInfoSection(RelationToInfoSection value) {
        this.infoSection = value;
    }

    public RelationTo withToEtalonKey(EtalonKey value) {
        setToEtalonKey(value);
        return this;
    }

    public RelationTo withToOriginKey(OriginKey value) {
        setToOriginKey(value);
        return this;
    }

    public RelationTo withSimpleAttributes(SimpleAttribute... values) {
        if (values!= null) {
            for (SimpleAttribute value: values) {
                getSimpleAttributes().add(value);
            }
        }
        return this;
    }

    public RelationTo withSimpleAttributes(Collection<SimpleAttribute> values) {
        if (values!= null) {
            getSimpleAttributes().addAll(values);
        }
        return this;
    }

    public RelationTo withComplexAttributes(ComplexAttribute... values) {
        if (values!= null) {
            for (ComplexAttribute value: values) {
                getComplexAttributes().add(value);
            }
        }
        return this;
    }

    public RelationTo withComplexAttributes(Collection<ComplexAttribute> values) {
        if (values!= null) {
            getComplexAttributes().addAll(values);
        }
        return this;
    }

    public RelationTo withInfoSection(RelationToInfoSection value) {
        setInfoSection(value);
        return this;
    }

    @Override
    public RelationTo withRelName(String value) {
        setRelName(value);
        return this;
    }

}
