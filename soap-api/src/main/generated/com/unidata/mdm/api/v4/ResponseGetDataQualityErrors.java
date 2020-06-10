
package com.unidata.mdm.api.v4;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.unidata.mdm.data.v4.DataQualityError;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 * Ответ на запрос на получение списка ошибок основной сущности, созданных в результате применения правил контроля качества данных ('RequestGetDataQualityErrors').
 * Общая часть ответа содержит код возврата, идентификатор логической операции и сообщения об ошибках (смотри элемент 'common' из структуры 'UnidataResponseBody').
 * Содержит список ошибок качества данных
 *             
 * 
 * <p>Java class for ResponseGetDataQualityErrors complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseGetDataQualityErrors"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://api.mdm.unidata.com/v4/}UnidataAbstractResponse"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dqError" type="{http://data.mdm.unidata.com/v4/}DataQualityError" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseGetDataQualityErrors", propOrder = {
    "dqError"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
public class ResponseGetDataQualityErrors
    extends UnidataAbstractResponse
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected List<DataQualityError> dqError;

    /**
     * Gets the value of the dqError property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dqError property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDqError().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataQualityError }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public List<DataQualityError> getDqError() {
        if (dqError == null) {
            dqError = new ArrayList<DataQualityError>();
        }
        return this.dqError;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public ResponseGetDataQualityErrors withDqError(DataQualityError... values) {
        if (values!= null) {
            for (DataQualityError value: values) {
                getDqError().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public ResponseGetDataQualityErrors withDqError(Collection<DataQualityError> values) {
        if (values!= null) {
            getDqError().addAll(values);
        }
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
