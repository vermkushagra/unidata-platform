
package com.unidata.mdm.api.v4;

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
 * Структура любого API запроса. Состоит из
 * - обязательной секции 'common', содержащий стандартный набор общих параметров, таких как идентификатор хранилища, безопасность, итд
 * - одного из конкретных запросов
 *             
 * 
 * <p>Java class for UnidataRequestBody complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UnidataRequestBody"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="common" type="{http://api.mdm.unidata.com/v4/}CommonSectionDef"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="requestAuthenticate" type="{http://api.mdm.unidata.com/v4/}RequestAuthenticate"/&gt;
 *           &lt;element name="requestGetLookupValues" type="{http://api.mdm.unidata.com/v4/}RequestGetLookupValues"/&gt;
 *           &lt;element name="requestCleanse" type="{http://api.mdm.unidata.com/v4/}RequestCleanse"/&gt;
 *           &lt;element name="requestUpsert" type="{http://api.mdm.unidata.com/v4/}RequestUpsert"/&gt;
 *           &lt;element name="requestRelationsUpsert" type="{http://api.mdm.unidata.com/v4/}RequestRelationsUpsert"/&gt;
 *           &lt;element name="requestMerge" type="{http://api.mdm.unidata.com/v4/}RequestMerge"/&gt;
 *           &lt;element name="requestJoin" type="{http://api.mdm.unidata.com/v4/}RequestJoin"/&gt;
 *           &lt;element name="requestSoftDelete" type="{http://api.mdm.unidata.com/v4/}RequestSoftDelete"/&gt;
 *           &lt;element name="requestRelationsSoftDelete" type="{http://api.mdm.unidata.com/v4/}RequestRelationsSoftDelete"/&gt;
 *           &lt;element name="requestGet" type="{http://api.mdm.unidata.com/v4/}RequestGet"/&gt;
 *           &lt;element name="requestGetAllPeriods" type="{http://api.mdm.unidata.com/v4/}RequestGetAllPeriods"/&gt;
 *           &lt;element name="requestRelationsGet" type="{http://api.mdm.unidata.com/v4/}RequestRelationsGet"/&gt;
 *           &lt;element name="requestSearch" type="{http://api.mdm.unidata.com/v4/}RequestSearch"/&gt;
 *           &lt;element name="requestGetDataQualityErrors" type="{http://api.mdm.unidata.com/v4/}RequestGetDataQualityErrors"/&gt;
 *           &lt;element name="requestInfoGet" type="{http://api.mdm.unidata.com/v4/}RequestInfoGet"/&gt;
 *           &lt;element name="requestBulkUpsert" type="{http://api.mdm.unidata.com/v4/}RequestBulkUpsert"/&gt;
 *           &lt;element name="requestFindAllJobs" type="{http://api.mdm.unidata.com/v4/}RequestFindAllJobs"/&gt;
 *           &lt;element name="requestSaveJob" type="{http://api.mdm.unidata.com/v4/}RequestSaveJob"/&gt;
 *           &lt;element name="requestRemoveJob" type="{http://api.mdm.unidata.com/v4/}RequestRemoveJob"/&gt;
 *           &lt;element name="requestFindJob" type="{http://api.mdm.unidata.com/v4/}RequestFindJob"/&gt;
 *           &lt;element name="requestRunJob" type="{http://api.mdm.unidata.com/v4/}RequestRunJob"/&gt;
 *           &lt;element name="requestJobStatus" type="{http://api.mdm.unidata.com/v4/}RequestJobStatus"/&gt;
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
@XmlType(name = "UnidataRequestBody", propOrder = {
    "common",
    "requestAuthenticate",
    "requestGetLookupValues",
    "requestCleanse",
    "requestUpsert",
    "requestRelationsUpsert",
    "requestMerge",
    "requestJoin",
    "requestSoftDelete",
    "requestRelationsSoftDelete",
    "requestGet",
    "requestGetAllPeriods",
    "requestRelationsGet",
    "requestSearch",
    "requestGetDataQualityErrors",
    "requestInfoGet",
    "requestBulkUpsert",
    "requestFindAllJobs",
    "requestSaveJob",
    "requestRemoveJob",
    "requestFindJob",
    "requestRunJob",
    "requestJobStatus"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
public class UnidataRequestBody
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected CommonSectionDef common;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestAuthenticate requestAuthenticate;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestGetLookupValues requestGetLookupValues;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestCleanse requestCleanse;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestUpsert requestUpsert;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestRelationsUpsert requestRelationsUpsert;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestMerge requestMerge;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestJoin requestJoin;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestSoftDelete requestSoftDelete;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestRelationsSoftDelete requestRelationsSoftDelete;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestGet requestGet;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestGetAllPeriods requestGetAllPeriods;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestRelationsGet requestRelationsGet;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestSearch requestSearch;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestGetDataQualityErrors requestGetDataQualityErrors;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestInfoGet requestInfoGet;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestBulkUpsert requestBulkUpsert;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestFindAllJobs requestFindAllJobs;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestSaveJob requestSaveJob;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestRemoveJob requestRemoveJob;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestFindJob requestFindJob;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestRunJob requestRunJob;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected RequestJobStatus requestJobStatus;

    /**
     * Gets the value of the common property.
     * 
     * @return
     *     possible object is
     *     {@link CommonSectionDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public CommonSectionDef getCommon() {
        return common;
    }

    /**
     * Sets the value of the common property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommonSectionDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setCommon(CommonSectionDef value) {
        this.common = value;
    }

    /**
     * Gets the value of the requestAuthenticate property.
     * 
     * @return
     *     possible object is
     *     {@link RequestAuthenticate }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestAuthenticate getRequestAuthenticate() {
        return requestAuthenticate;
    }

    /**
     * Sets the value of the requestAuthenticate property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestAuthenticate }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestAuthenticate(RequestAuthenticate value) {
        this.requestAuthenticate = value;
    }

    /**
     * Gets the value of the requestGetLookupValues property.
     * 
     * @return
     *     possible object is
     *     {@link RequestGetLookupValues }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestGetLookupValues getRequestGetLookupValues() {
        return requestGetLookupValues;
    }

    /**
     * Sets the value of the requestGetLookupValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestGetLookupValues }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestGetLookupValues(RequestGetLookupValues value) {
        this.requestGetLookupValues = value;
    }

    /**
     * Gets the value of the requestCleanse property.
     * 
     * @return
     *     possible object is
     *     {@link RequestCleanse }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestCleanse getRequestCleanse() {
        return requestCleanse;
    }

    /**
     * Sets the value of the requestCleanse property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestCleanse }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestCleanse(RequestCleanse value) {
        this.requestCleanse = value;
    }

    /**
     * Gets the value of the requestUpsert property.
     * 
     * @return
     *     possible object is
     *     {@link RequestUpsert }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsert getRequestUpsert() {
        return requestUpsert;
    }

    /**
     * Sets the value of the requestUpsert property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestUpsert }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestUpsert(RequestUpsert value) {
        this.requestUpsert = value;
    }

    /**
     * Gets the value of the requestRelationsUpsert property.
     * 
     * @return
     *     possible object is
     *     {@link RequestRelationsUpsert }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestRelationsUpsert getRequestRelationsUpsert() {
        return requestRelationsUpsert;
    }

    /**
     * Sets the value of the requestRelationsUpsert property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestRelationsUpsert }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestRelationsUpsert(RequestRelationsUpsert value) {
        this.requestRelationsUpsert = value;
    }

    /**
     * Gets the value of the requestMerge property.
     * 
     * @return
     *     possible object is
     *     {@link RequestMerge }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestMerge getRequestMerge() {
        return requestMerge;
    }

    /**
     * Sets the value of the requestMerge property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestMerge }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestMerge(RequestMerge value) {
        this.requestMerge = value;
    }

    /**
     * Gets the value of the requestJoin property.
     * 
     * @return
     *     possible object is
     *     {@link RequestJoin }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestJoin getRequestJoin() {
        return requestJoin;
    }

    /**
     * Sets the value of the requestJoin property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestJoin }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestJoin(RequestJoin value) {
        this.requestJoin = value;
    }

    /**
     * Gets the value of the requestSoftDelete property.
     * 
     * @return
     *     possible object is
     *     {@link RequestSoftDelete }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestSoftDelete getRequestSoftDelete() {
        return requestSoftDelete;
    }

    /**
     * Sets the value of the requestSoftDelete property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestSoftDelete }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestSoftDelete(RequestSoftDelete value) {
        this.requestSoftDelete = value;
    }

    /**
     * Gets the value of the requestRelationsSoftDelete property.
     * 
     * @return
     *     possible object is
     *     {@link RequestRelationsSoftDelete }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestRelationsSoftDelete getRequestRelationsSoftDelete() {
        return requestRelationsSoftDelete;
    }

    /**
     * Sets the value of the requestRelationsSoftDelete property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestRelationsSoftDelete }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestRelationsSoftDelete(RequestRelationsSoftDelete value) {
        this.requestRelationsSoftDelete = value;
    }

    /**
     * Gets the value of the requestGet property.
     * 
     * @return
     *     possible object is
     *     {@link RequestGet }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestGet getRequestGet() {
        return requestGet;
    }

    /**
     * Sets the value of the requestGet property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestGet }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestGet(RequestGet value) {
        this.requestGet = value;
    }

    /**
     * Gets the value of the requestGetAllPeriods property.
     * 
     * @return
     *     possible object is
     *     {@link RequestGetAllPeriods }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestGetAllPeriods getRequestGetAllPeriods() {
        return requestGetAllPeriods;
    }

    /**
     * Sets the value of the requestGetAllPeriods property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestGetAllPeriods }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestGetAllPeriods(RequestGetAllPeriods value) {
        this.requestGetAllPeriods = value;
    }

    /**
     * Gets the value of the requestRelationsGet property.
     * 
     * @return
     *     possible object is
     *     {@link RequestRelationsGet }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestRelationsGet getRequestRelationsGet() {
        return requestRelationsGet;
    }

    /**
     * Sets the value of the requestRelationsGet property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestRelationsGet }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestRelationsGet(RequestRelationsGet value) {
        this.requestRelationsGet = value;
    }

    /**
     * Gets the value of the requestSearch property.
     * 
     * @return
     *     possible object is
     *     {@link RequestSearch }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestSearch getRequestSearch() {
        return requestSearch;
    }

    /**
     * Sets the value of the requestSearch property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestSearch }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestSearch(RequestSearch value) {
        this.requestSearch = value;
    }

    /**
     * Gets the value of the requestGetDataQualityErrors property.
     * 
     * @return
     *     possible object is
     *     {@link RequestGetDataQualityErrors }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestGetDataQualityErrors getRequestGetDataQualityErrors() {
        return requestGetDataQualityErrors;
    }

    /**
     * Sets the value of the requestGetDataQualityErrors property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestGetDataQualityErrors }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestGetDataQualityErrors(RequestGetDataQualityErrors value) {
        this.requestGetDataQualityErrors = value;
    }

    /**
     * Gets the value of the requestInfoGet property.
     * 
     * @return
     *     possible object is
     *     {@link RequestInfoGet }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestInfoGet getRequestInfoGet() {
        return requestInfoGet;
    }

    /**
     * Sets the value of the requestInfoGet property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestInfoGet }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestInfoGet(RequestInfoGet value) {
        this.requestInfoGet = value;
    }

    /**
     * Gets the value of the requestBulkUpsert property.
     * 
     * @return
     *     possible object is
     *     {@link RequestBulkUpsert }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestBulkUpsert getRequestBulkUpsert() {
        return requestBulkUpsert;
    }

    /**
     * Sets the value of the requestBulkUpsert property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestBulkUpsert }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestBulkUpsert(RequestBulkUpsert value) {
        this.requestBulkUpsert = value;
    }

    /**
     * Gets the value of the requestFindAllJobs property.
     * 
     * @return
     *     possible object is
     *     {@link RequestFindAllJobs }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestFindAllJobs getRequestFindAllJobs() {
        return requestFindAllJobs;
    }

    /**
     * Sets the value of the requestFindAllJobs property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestFindAllJobs }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestFindAllJobs(RequestFindAllJobs value) {
        this.requestFindAllJobs = value;
    }

    /**
     * Gets the value of the requestSaveJob property.
     * 
     * @return
     *     possible object is
     *     {@link RequestSaveJob }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestSaveJob getRequestSaveJob() {
        return requestSaveJob;
    }

    /**
     * Sets the value of the requestSaveJob property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestSaveJob }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestSaveJob(RequestSaveJob value) {
        this.requestSaveJob = value;
    }

    /**
     * Gets the value of the requestRemoveJob property.
     * 
     * @return
     *     possible object is
     *     {@link RequestRemoveJob }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestRemoveJob getRequestRemoveJob() {
        return requestRemoveJob;
    }

    /**
     * Sets the value of the requestRemoveJob property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestRemoveJob }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestRemoveJob(RequestRemoveJob value) {
        this.requestRemoveJob = value;
    }

    /**
     * Gets the value of the requestFindJob property.
     * 
     * @return
     *     possible object is
     *     {@link RequestFindJob }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestFindJob getRequestFindJob() {
        return requestFindJob;
    }

    /**
     * Sets the value of the requestFindJob property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestFindJob }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestFindJob(RequestFindJob value) {
        this.requestFindJob = value;
    }

    /**
     * Gets the value of the requestRunJob property.
     * 
     * @return
     *     possible object is
     *     {@link RequestRunJob }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestRunJob getRequestRunJob() {
        return requestRunJob;
    }

    /**
     * Sets the value of the requestRunJob property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestRunJob }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestRunJob(RequestRunJob value) {
        this.requestRunJob = value;
    }

    /**
     * Gets the value of the requestJobStatus property.
     * 
     * @return
     *     possible object is
     *     {@link RequestJobStatus }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public RequestJobStatus getRequestJobStatus() {
        return requestJobStatus;
    }

    /**
     * Sets the value of the requestJobStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestJobStatus }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setRequestJobStatus(RequestJobStatus value) {
        this.requestJobStatus = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withCommon(CommonSectionDef value) {
        setCommon(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestAuthenticate(RequestAuthenticate value) {
        setRequestAuthenticate(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestGetLookupValues(RequestGetLookupValues value) {
        setRequestGetLookupValues(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestCleanse(RequestCleanse value) {
        setRequestCleanse(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestUpsert(RequestUpsert value) {
        setRequestUpsert(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestRelationsUpsert(RequestRelationsUpsert value) {
        setRequestRelationsUpsert(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestMerge(RequestMerge value) {
        setRequestMerge(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestJoin(RequestJoin value) {
        setRequestJoin(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestSoftDelete(RequestSoftDelete value) {
        setRequestSoftDelete(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestRelationsSoftDelete(RequestRelationsSoftDelete value) {
        setRequestRelationsSoftDelete(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestGet(RequestGet value) {
        setRequestGet(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestGetAllPeriods(RequestGetAllPeriods value) {
        setRequestGetAllPeriods(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestRelationsGet(RequestRelationsGet value) {
        setRequestRelationsGet(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestSearch(RequestSearch value) {
        setRequestSearch(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestGetDataQualityErrors(RequestGetDataQualityErrors value) {
        setRequestGetDataQualityErrors(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestInfoGet(RequestInfoGet value) {
        setRequestInfoGet(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestBulkUpsert(RequestBulkUpsert value) {
        setRequestBulkUpsert(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestFindAllJobs(RequestFindAllJobs value) {
        setRequestFindAllJobs(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestSaveJob(RequestSaveJob value) {
        setRequestSaveJob(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestRemoveJob(RequestRemoveJob value) {
        setRequestRemoveJob(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestFindJob(RequestFindJob value) {
        setRequestFindJob(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestRunJob(RequestRunJob value) {
        setRequestRunJob(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UnidataRequestBody withRequestJobStatus(RequestJobStatus value) {
        setRequestJobStatus(value);
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
