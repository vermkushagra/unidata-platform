
package com.unidata.mdm.api.v5;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


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
    ResponseSplit.class,
    ResponseSoftDelete.class,
    ResponseRelationsSoftDelete.class,
    ResponseGet.class,
    ResponseGetAllPeriods.class,
    ResponseRelationsGet.class,
    ResponseInfoGet.class,
    ResponseDataProfile.class,
    ResponseSearch.class,
    ResponseCheckDuplicates.class,
    ResponseGetDataQualityErrors.class,
    ResponseFindAllJobs.class,
    ResponseSaveJob.class,
    ResponseRemoveJob.class,
    ResponseFindJob.class,
    ResponseRunJob.class,
    ResponseJobStatus.class,
    ResponseStopJob.class
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public class UnidataAbstractResponse
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;

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