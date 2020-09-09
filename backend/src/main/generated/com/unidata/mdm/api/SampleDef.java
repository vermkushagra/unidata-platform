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


/**
 * 
 * Дополнительный тип данных предназначенный для внутреннего тестирования SoapAPI.
 * Позволяет сформировать произвольный запрос ответ в любом редакторе, поддерживающим проверку XML файла на основе XML Schema
 *             
 * 
 * <p>Java class for SampleDef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SampleDef"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="requestBody" type="{http://api.mdm.unidata.com/}UnidataRequestBody"/&gt;
 *         &lt;element name="responseBody" type="{http://api.mdm.unidata.com/}UnidataResponseBody"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SampleDef", propOrder = {
    "requestBody",
    "responseBody"
})
public class SampleDef implements Serializable
{

    private final static long serialVersionUID = 12345L;
    @XmlElement(required = true)
    protected UnidataRequestBody requestBody;
    @XmlElement(required = true)
    protected UnidataResponseBody responseBody;

    /**
     * Gets the value of the requestBody property.
     * 
     * @return
     *     possible object is
     *     {@link UnidataRequestBody }
     *     
     */
    public UnidataRequestBody getRequestBody() {
        return requestBody;
    }

    /**
     * Sets the value of the requestBody property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnidataRequestBody }
     *     
     */
    public void setRequestBody(UnidataRequestBody value) {
        this.requestBody = value;
    }

    /**
     * Gets the value of the responseBody property.
     * 
     * @return
     *     possible object is
     *     {@link UnidataResponseBody }
     *     
     */
    public UnidataResponseBody getResponseBody() {
        return responseBody;
    }

    /**
     * Sets the value of the responseBody property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnidataResponseBody }
     *     
     */
    public void setResponseBody(UnidataResponseBody value) {
        this.responseBody = value;
    }

    public SampleDef withRequestBody(UnidataRequestBody value) {
        setRequestBody(value);
        return this;
    }

    public SampleDef withResponseBody(UnidataResponseBody value) {
        setResponseBody(value);
        return this;
    }

}
