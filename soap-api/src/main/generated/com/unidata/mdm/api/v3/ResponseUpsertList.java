
package com.unidata.mdm.api.v3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import com.unidata.mdm.data.v3.EtalonKey;
import com.unidata.mdm.data.v3.OriginKey;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 * Ответ на запрос на вставку или модификацию основной или исходной записи сущности ('RequestUpsert').
 * Общая часть ответа содержит код возврата, идентификатор логической операции и сообщения об ошибках (смотри элемент 'common' из структуры 'UnidataResponseBody').
 * В случае успешного исполнения всегда содержит два ключа, один идентифицирующий исходную запись, фактически изменённую, второй идентифицирующий основную запись сущности.
 * Помимо этого возращается тип действия, фактически выполненного платформой - 'action'.
 *             
 * 
 * <p>Java class for ResponseUpsertList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseUpsertList"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://api.mdm.unidata.com/v3/}UnidataAbstractResponse"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="originKeys" type="{http://data.mdm.unidata.com/v3/}OriginKey" maxOccurs="unbounded"/&gt;
 *           &lt;element name="etalonKeys" type="{http://data.mdm.unidata.com/v3/}EtalonKey" maxOccurs="unbounded"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="originActions" type="{http://api.mdm.unidata.com/v3/}UpsertActionType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseUpsertList", propOrder = {
    "originKeys",
    "etalonKeys",
    "originActions"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
public class ResponseUpsertList
    extends UnidataAbstractResponse
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected List<OriginKey> originKeys;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected List<EtalonKey> etalonKeys;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected List<UpsertActionType> originActions;

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
     * Gets the value of the originActions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the originActions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOriginActions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UpsertActionType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public List<UpsertActionType> getOriginActions() {
        if (originActions == null) {
            originActions = new ArrayList<UpsertActionType>();
        }
        return this.originActions;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseUpsertList withOriginKeys(OriginKey... values) {
        if (values!= null) {
            for (OriginKey value: values) {
                getOriginKeys().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseUpsertList withOriginKeys(Collection<OriginKey> values) {
        if (values!= null) {
            getOriginKeys().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseUpsertList withEtalonKeys(EtalonKey... values) {
        if (values!= null) {
            for (EtalonKey value: values) {
                getEtalonKeys().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseUpsertList withEtalonKeys(Collection<EtalonKey> values) {
        if (values!= null) {
            getEtalonKeys().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseUpsertList withOriginActions(UpsertActionType... values) {
        if (values!= null) {
            for (UpsertActionType value: values) {
                getOriginActions().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public ResponseUpsertList withOriginActions(Collection<UpsertActionType> values) {
        if (values!= null) {
            getOriginActions().addAll(values);
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
