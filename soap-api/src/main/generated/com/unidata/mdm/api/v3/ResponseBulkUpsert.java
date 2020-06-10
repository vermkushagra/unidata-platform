
package com.unidata.mdm.api.v3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 *                 Ответ на запрос на множественную вставку или модификацию основной или исходной записи сущности ('BulkRequestUpsert').
 *                 Документацию об ответе при единичной вставке в ('ResponseUpsert')
 *             
 * 
 * <p>Java class for ResponseBulkUpsert complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseBulkUpsert"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://api.mdm.unidata.com/v3/}UnidataAbstractResponse"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="upsertRecordResponses" type="{http://api.mdm.unidata.com/v3/}ResponseUpsert" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseBulkUpsert", propOrder = {
    "upsertRecordResponses"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
public class ResponseBulkUpsert
    extends UnidataAbstractResponse
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected List<ResponseUpsert> upsertRecordResponses;

    /**
     * Gets the value of the upsertRecordResponses property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the upsertRecordResponses property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUpsertRecordResponses().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResponseUpsert }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public List<ResponseUpsert> getUpsertRecordResponses() {
        if (upsertRecordResponses == null) {
            upsertRecordResponses = new ArrayList<ResponseUpsert>();
        }
        return this.upsertRecordResponses;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseBulkUpsert withUpsertRecordResponses(ResponseUpsert... values) {
        if (values!= null) {
            for (ResponseUpsert value: values) {
                getUpsertRecordResponses().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseBulkUpsert withUpsertRecordResponses(Collection<ResponseUpsert> values) {
        if (values!= null) {
            getUpsertRecordResponses().addAll(values);
        }
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
