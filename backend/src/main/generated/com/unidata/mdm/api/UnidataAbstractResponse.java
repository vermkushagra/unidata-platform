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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UnidataAbstractResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UnidataAbstractResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnidataAbstractResponse")
@XmlSeeAlso({
    ResponseAuthenticate.class,
    ResponseGetLookupValues.class,
    ResponseCleanse.class,
    ResponseUpsert.class,
    ResponseBulkUpsert.class,
    ResponseRelationsUpsert.class,
    ResponseUpsertList.class,
    ResponseMerge.class,
    ResponseJoin.class,
    ResponseSoftDelete.class,
    ResponseRelationsSoftDelete.class,
    ResponseGet.class,
    ResponseRelationsGet.class,
    ResponseInfoGet.class,
    ResponseSearch.class,
    ResponseGetDataQualityErrors.class
})
public class UnidataAbstractResponse implements Serializable
{

    private final static long serialVersionUID = 12345L;

}
