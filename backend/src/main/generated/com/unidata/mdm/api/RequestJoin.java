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
import com.unidata.mdm.data.EtalonKey;
import com.unidata.mdm.data.OriginKey;


/**
 * 
 * Запрос на добавление внешнего ID к cуществующему etalonId.
 * Общая часть запроса должна содержать данные для аутентификации пользователя (смотри элемент 'common' из структуры 'UnidataRequestBody').
 *             
 * 
 * <p>Java class for RequestJoin complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestJoin"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://api.mdm.unidata.com/}UnidataAbstractRequest"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="etalonKey" type="{http://data.mdm.unidata.com/}EtalonKey"/&gt;
 *         &lt;element name="originKey" type="{http://data.mdm.unidata.com/}OriginKey"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestJoin", propOrder = {
    "etalonKey",
    "originKey"
})
public class RequestJoin
    extends UnidataAbstractRequest
    implements Serializable
{

    private final static long serialVersionUID = 12345L;
    @XmlElement(required = true)
    protected EtalonKey etalonKey;
    @XmlElement(required = true)
    protected OriginKey originKey;

    /**
     * Gets the value of the etalonKey property.
     * 
     * @return
     *     possible object is
     *     {@link EtalonKey }
     *     
     */
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
    public void setOriginKey(OriginKey value) {
        this.originKey = value;
    }

    public RequestJoin withEtalonKey(EtalonKey value) {
        setEtalonKey(value);
        return this;
    }

    public RequestJoin withOriginKey(OriginKey value) {
        setOriginKey(value);
        return this;
    }

}
