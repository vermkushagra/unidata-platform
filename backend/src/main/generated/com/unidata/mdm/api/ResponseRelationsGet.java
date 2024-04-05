//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.09.09 at 03:48:37 PM MSK 
//


package com.unidata.mdm.api;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.unidata.mdm.data.EntityRelations;


/**
 * 
 * Ответ на запрос на получение основных и исходных записей связей для конкретной сущности.
 *             
 * 
 * <p>Java class for ResponseRelationsGet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseRelationsGet"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://api.mdm.unidata.com/}UnidataAbstractResponse"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="relations" type="{http://data.mdm.unidata.com/}EntityRelations"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseRelationsGet", propOrder = {
    "relations"
})
public class ResponseRelationsGet
    extends UnidataAbstractResponse
    implements Serializable
{

    private final static long serialVersionUID = 12345L;
    @XmlElement(required = true)
    protected EntityRelations relations;

    /**
     * Gets the value of the relations property.
     * 
     * @return
     *     possible object is
     *     {@link EntityRelations }
     *     
     */
    public EntityRelations getRelations() {
        return relations;
    }

    /**
     * Sets the value of the relations property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityRelations }
     *     
     */
    public void setRelations(EntityRelations value) {
        this.relations = value;
    }

    public ResponseRelationsGet withRelations(EntityRelations value) {
        setRelations(value);
        return this;
    }

}