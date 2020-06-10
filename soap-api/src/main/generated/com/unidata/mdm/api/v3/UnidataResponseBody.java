
package com.unidata.mdm.api.v3;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 * Структура любого API ответа. Состоит из
 * - обязательной секции 'common', содержащий стандартный набор общих данных, таких как код ошибки, итд
 * - одного из конкретных ответов
 *             
 * 
 * <p>Java class for UnidataResponseBody complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UnidataResponseBody"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="common" type="{http://api.mdm.unidata.com/v3/}CommonResponseDef"/&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="responseAuthenticate" type="{http://api.mdm.unidata.com/v3/}ResponseAuthenticate"/&gt;
 *           &lt;element name="responseGetLookupValues" type="{http://api.mdm.unidata.com/v3/}ResponseGetLookupValues"/&gt;
 *           &lt;element name="responseCleanse" type="{http://api.mdm.unidata.com/v3/}ResponseCleanse"/&gt;
 *           &lt;element name="responseUpsert" type="{http://api.mdm.unidata.com/v3/}ResponseUpsert"/&gt;
 *           &lt;element name="responseRelationsUpsert" type="{http://api.mdm.unidata.com/v3/}ResponseRelationsUpsert"/&gt;
 *           &lt;element name="responseMerge" type="{http://api.mdm.unidata.com/v3/}ResponseMerge"/&gt;
 *           &lt;element name="responseJoin" type="{http://api.mdm.unidata.com/v3/}ResponseJoin"/&gt;
 *           &lt;element name="responseSoftDelete" type="{http://api.mdm.unidata.com/v3/}ResponseSoftDelete"/&gt;
 *           &lt;element name="responseRelationsSoftDelete" type="{http://api.mdm.unidata.com/v3/}ResponseRelationsSoftDelete"/&gt;
 *           &lt;element name="responseGet" type="{http://api.mdm.unidata.com/v3/}ResponseGet"/&gt;
 *           &lt;element name="responseRelationsGet" type="{http://api.mdm.unidata.com/v3/}ResponseRelationsGet"/&gt;
 *           &lt;element name="responseSearch" type="{http://api.mdm.unidata.com/v3/}ResponseSearch"/&gt;
 *           &lt;element name="responseGetDataQualityErrors" type="{http://api.mdm.unidata.com/v3/}ResponseGetDataQualityErrors"/&gt;
 *           &lt;element name="responseInfoGet" type="{http://api.mdm.unidata.com/v3/}ResponseInfoGet"/&gt;
 *           &lt;element name="responseBulkUpsert" type="{http://api.mdm.unidata.com/v3/}ResponseBulkUpsert"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnidataResponseBody", propOrder = {
    "common",
    "responseAuthenticate",
    "responseGetLookupValues",
    "responseCleanse",
    "responseUpsert",
    "responseRelationsUpsert",
    "responseMerge",
    "responseJoin",
    "responseSoftDelete",
    "responseRelationsSoftDelete",
    "responseGet",
    "responseRelationsGet",
    "responseSearch",
    "responseGetDataQualityErrors",
    "responseInfoGet",
    "responseBulkUpsert"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
public class UnidataResponseBody
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected CommonResponseDef common;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected ResponseAuthenticate responseAuthenticate;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected ResponseGetLookupValues responseGetLookupValues;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected ResponseCleanse responseCleanse;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected ResponseUpsert responseUpsert;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected ResponseRelationsUpsert responseRelationsUpsert;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected ResponseMerge responseMerge;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected ResponseJoin responseJoin;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected ResponseSoftDelete responseSoftDelete;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected ResponseRelationsSoftDelete responseRelationsSoftDelete;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected ResponseGet responseGet;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected ResponseRelationsGet responseRelationsGet;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected ResponseSearch responseSearch;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected ResponseGetDataQualityErrors responseGetDataQualityErrors;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected ResponseInfoGet responseInfoGet;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected ResponseBulkUpsert responseBulkUpsert;

    /**
     * Gets the value of the common property.
     * 
     * @return
     *     possible object is
     *     {@link CommonResponseDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public CommonResponseDef getCommon() {
        return common;
    }

    /**
     * Sets the value of the common property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommonResponseDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setCommon(CommonResponseDef value) {
        this.common = value;
    }

    /**
     * Gets the value of the responseAuthenticate property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseAuthenticate }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseAuthenticate getResponseAuthenticate() {
        return responseAuthenticate;
    }

    /**
     * Sets the value of the responseAuthenticate property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseAuthenticate }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setResponseAuthenticate(ResponseAuthenticate value) {
        this.responseAuthenticate = value;
    }

    /**
     * Gets the value of the responseGetLookupValues property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseGetLookupValues }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseGetLookupValues getResponseGetLookupValues() {
        return responseGetLookupValues;
    }

    /**
     * Sets the value of the responseGetLookupValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseGetLookupValues }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setResponseGetLookupValues(ResponseGetLookupValues value) {
        this.responseGetLookupValues = value;
    }

    /**
     * Gets the value of the responseCleanse property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseCleanse }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseCleanse getResponseCleanse() {
        return responseCleanse;
    }

    /**
     * Sets the value of the responseCleanse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseCleanse }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setResponseCleanse(ResponseCleanse value) {
        this.responseCleanse = value;
    }

    /**
     * Gets the value of the responseUpsert property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseUpsert }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseUpsert getResponseUpsert() {
        return responseUpsert;
    }

    /**
     * Sets the value of the responseUpsert property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseUpsert }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setResponseUpsert(ResponseUpsert value) {
        this.responseUpsert = value;
    }

    /**
     * Gets the value of the responseRelationsUpsert property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseRelationsUpsert }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseRelationsUpsert getResponseRelationsUpsert() {
        return responseRelationsUpsert;
    }

    /**
     * Sets the value of the responseRelationsUpsert property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseRelationsUpsert }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setResponseRelationsUpsert(ResponseRelationsUpsert value) {
        this.responseRelationsUpsert = value;
    }

    /**
     * Gets the value of the responseMerge property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseMerge }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseMerge getResponseMerge() {
        return responseMerge;
    }

    /**
     * Sets the value of the responseMerge property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseMerge }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setResponseMerge(ResponseMerge value) {
        this.responseMerge = value;
    }

    /**
     * Gets the value of the responseJoin property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseJoin }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseJoin getResponseJoin() {
        return responseJoin;
    }

    /**
     * Sets the value of the responseJoin property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseJoin }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setResponseJoin(ResponseJoin value) {
        this.responseJoin = value;
    }

    /**
     * Gets the value of the responseSoftDelete property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseSoftDelete }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseSoftDelete getResponseSoftDelete() {
        return responseSoftDelete;
    }

    /**
     * Sets the value of the responseSoftDelete property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseSoftDelete }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setResponseSoftDelete(ResponseSoftDelete value) {
        this.responseSoftDelete = value;
    }

    /**
     * Gets the value of the responseRelationsSoftDelete property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseRelationsSoftDelete }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseRelationsSoftDelete getResponseRelationsSoftDelete() {
        return responseRelationsSoftDelete;
    }

    /**
     * Sets the value of the responseRelationsSoftDelete property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseRelationsSoftDelete }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setResponseRelationsSoftDelete(ResponseRelationsSoftDelete value) {
        this.responseRelationsSoftDelete = value;
    }

    /**
     * Gets the value of the responseGet property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseGet }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseGet getResponseGet() {
        return responseGet;
    }

    /**
     * Sets the value of the responseGet property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseGet }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setResponseGet(ResponseGet value) {
        this.responseGet = value;
    }

    /**
     * Gets the value of the responseRelationsGet property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseRelationsGet }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseRelationsGet getResponseRelationsGet() {
        return responseRelationsGet;
    }

    /**
     * Sets the value of the responseRelationsGet property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseRelationsGet }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setResponseRelationsGet(ResponseRelationsGet value) {
        this.responseRelationsGet = value;
    }

    /**
     * Gets the value of the responseSearch property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseSearch }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseSearch getResponseSearch() {
        return responseSearch;
    }

    /**
     * Sets the value of the responseSearch property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseSearch }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setResponseSearch(ResponseSearch value) {
        this.responseSearch = value;
    }

    /**
     * Gets the value of the responseGetDataQualityErrors property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseGetDataQualityErrors }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseGetDataQualityErrors getResponseGetDataQualityErrors() {
        return responseGetDataQualityErrors;
    }

    /**
     * Sets the value of the responseGetDataQualityErrors property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseGetDataQualityErrors }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setResponseGetDataQualityErrors(ResponseGetDataQualityErrors value) {
        this.responseGetDataQualityErrors = value;
    }

    /**
     * Gets the value of the responseInfoGet property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseInfoGet }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseInfoGet getResponseInfoGet() {
        return responseInfoGet;
    }

    /**
     * Sets the value of the responseInfoGet property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseInfoGet }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setResponseInfoGet(ResponseInfoGet value) {
        this.responseInfoGet = value;
    }

    /**
     * Gets the value of the responseBulkUpsert property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseBulkUpsert }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseBulkUpsert getResponseBulkUpsert() {
        return responseBulkUpsert;
    }

    /**
     * Sets the value of the responseBulkUpsert property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseBulkUpsert }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setResponseBulkUpsert(ResponseBulkUpsert value) {
        this.responseBulkUpsert = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public UnidataResponseBody withCommon(CommonResponseDef value) {
        setCommon(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public UnidataResponseBody withResponseAuthenticate(ResponseAuthenticate value) {
        setResponseAuthenticate(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public UnidataResponseBody withResponseGetLookupValues(ResponseGetLookupValues value) {
        setResponseGetLookupValues(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public UnidataResponseBody withResponseCleanse(ResponseCleanse value) {
        setResponseCleanse(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public UnidataResponseBody withResponseUpsert(ResponseUpsert value) {
        setResponseUpsert(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public UnidataResponseBody withResponseRelationsUpsert(ResponseRelationsUpsert value) {
        setResponseRelationsUpsert(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public UnidataResponseBody withResponseMerge(ResponseMerge value) {
        setResponseMerge(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public UnidataResponseBody withResponseJoin(ResponseJoin value) {
        setResponseJoin(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public UnidataResponseBody withResponseSoftDelete(ResponseSoftDelete value) {
        setResponseSoftDelete(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public UnidataResponseBody withResponseRelationsSoftDelete(ResponseRelationsSoftDelete value) {
        setResponseRelationsSoftDelete(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public UnidataResponseBody withResponseGet(ResponseGet value) {
        setResponseGet(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public UnidataResponseBody withResponseRelationsGet(ResponseRelationsGet value) {
        setResponseRelationsGet(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public UnidataResponseBody withResponseSearch(ResponseSearch value) {
        setResponseSearch(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public UnidataResponseBody withResponseGetDataQualityErrors(ResponseGetDataQualityErrors value) {
        setResponseGetDataQualityErrors(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public UnidataResponseBody withResponseInfoGet(ResponseInfoGet value) {
        setResponseInfoGet(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public UnidataResponseBody withResponseBulkUpsert(ResponseBulkUpsert value) {
        setResponseBulkUpsert(value);
        return this;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public String toString() {
        return ToStringBuilder.reflectionToString(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
