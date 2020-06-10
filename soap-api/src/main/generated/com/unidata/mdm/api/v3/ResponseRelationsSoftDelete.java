
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
import com.unidata.mdm.data.v3.EtalonKey;
import com.unidata.mdm.data.v3.OriginKey;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 * Ответ на запрос на логическое удаление основной или исходной записи сущности ('RequestSoftDelete').
 * Общая часть ответа содержит код возврата, идентификатор логической операции и сообщения об ошибках (смотри элемент 'common' из структуры 'UnidataResponseBody').
 * 
 * В случае успешного исполнения содержит список ключей логически удалённых записей, а также соответствующих исходных записей.
 *             
 * 
 * <p>Java class for ResponseRelationsSoftDelete complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseRelationsSoftDelete"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://api.mdm.unidata.com/v3/}UnidataAbstractResponse"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="etalonKeys" type="{http://data.mdm.unidata.com/v3/}EtalonKey" maxOccurs="unbounded"/&gt;
 *         &lt;element name="originKeys" type="{http://data.mdm.unidata.com/v3/}OriginKey" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseRelationsSoftDelete", propOrder = {
    "etalonKeys",
    "originKeys"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
public class ResponseRelationsSoftDelete
    extends UnidataAbstractResponse
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected List<EtalonKey> etalonKeys;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected List<OriginKey> originKeys;

    /**
     * Gets the value of the etalonKeys property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the etalonKeys property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEtalonKeys().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EtalonKey }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public List<EtalonKey> getEtalonKeys() {
        if (etalonKeys == null) {
            etalonKeys = new ArrayList<EtalonKey>();
        }
        return this.etalonKeys;
    }

    /**
     * Gets the value of the originKeys property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the originKeys property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOriginKeys().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OriginKey }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public List<OriginKey> getOriginKeys() {
        if (originKeys == null) {
            originKeys = new ArrayList<OriginKey>();
        }
        return this.originKeys;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseRelationsSoftDelete withEtalonKeys(EtalonKey... values) {
        if (values!= null) {
            for (EtalonKey value: values) {
                getEtalonKeys().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseRelationsSoftDelete withEtalonKeys(Collection<EtalonKey> values) {
        if (values!= null) {
            getEtalonKeys().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseRelationsSoftDelete withOriginKeys(OriginKey... values) {
        if (values!= null) {
            for (OriginKey value: values) {
                getOriginKeys().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseRelationsSoftDelete withOriginKeys(Collection<OriginKey> values) {
        if (values!= null) {
            getOriginKeys().addAll(values);
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
