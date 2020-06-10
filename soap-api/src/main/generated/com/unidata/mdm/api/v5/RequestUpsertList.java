
package com.unidata.mdm.api.v5;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import com.unidata.mdm.data.v5.EtalonRecord;
import com.unidata.mdm.data.v5.OriginRecord;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 * Запрос на вставку или модификацию основной или исходной записи сущности.
 * Общая часть запроса должна содержать данные для аутентификации пользователя (смотри элемент 'common' из структуры 'UnidataRequestBody').
 * 
 * 'upsert' означает комбинацию слов 'update' и 'insert'.
 * 
 * Все операции по модификации данных производятся над исходными записями сущностей из конкретной системы источника, включая служебную систему источник.
 * При этом основная запись сущности вычисляется заново как результат консолидации всех исходных записей.
 * 
 * Имеется возможность передать либо основную запись сущности (элемент 'etalonRecord'), либо исходную запись (элемент 'originRecord'). 
 * При передаче основной записи, платформа подберёт соответствующую исходную запись из служебной системы источника и
 * обеспечит выигрыш соответствующих значений атрибутов при консолидации. 
 * Любая из переданных записей содержит ключ, на основе значения которого принимается решение будет ли это операция вставки или изменения.
 * 
 * По умолчанию, к любой основной записи применяются все имеющиеся правила контроля качества данных. Вызывающая сторона может принудительно отказаться от применения правил, передав skipCleanse='true'
 * В случае операции изменения записи, есть возможность передать выборочный набор атрибутов. Все непереданные атрибуты остаются без изменений.
 * 
 * Поле range указывает границы действия версии данных. Отсутствующие значения означают бесконечность в прошлом или будущем или их комбинацию. Отсутствующий элемент означает бесконечность с обоих концов.
 *             
 * 
 * <p>Java class for RequestUpsertList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestUpsertList"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://api.mdm.unidata.com/v5/}UnidataAbstractRequest"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="etalonRecords" type="{http://data.mdm.unidata.com/v5/}EtalonRecord" maxOccurs="unbounded"/&gt;
 *           &lt;element name="originRecords" type="{http://data.mdm.unidata.com/v5/}OriginRecord" maxOccurs="unbounded"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="range" type="{http://api.mdm.unidata.com/v5/}TimeIntervalDef" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="lastUpdateDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="skipCleanse" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="bypassExtensionPoints" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestUpsertList", propOrder = {
    "etalonRecords",
    "originRecords",
    "range"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public class RequestUpsertList
    extends UnidataAbstractRequest
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected List<EtalonRecord> etalonRecords;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected List<OriginRecord> originRecords;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected TimeIntervalDef range;
    @XmlAttribute(name = "lastUpdateDate")
    @XmlSchemaType(name = "dateTime")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected XMLGregorianCalendar lastUpdateDate;
    @XmlAttribute(name = "skipCleanse")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected Boolean skipCleanse;
    @XmlAttribute(name = "bypassExtensionPoints")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected Boolean bypassExtensionPoints;

    /**
     * Gets the value of the etalonRecords property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the etalonRecords property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEtalonRecords().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EtalonRecord }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public List<EtalonRecord> getEtalonRecords() {
        if (etalonRecords == null) {
            etalonRecords = new ArrayList<EtalonRecord>();
        }
        return this.etalonRecords;
    }

    /**
     * Gets the value of the originRecords property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the originRecords property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOriginRecords().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OriginRecord }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public List<OriginRecord> getOriginRecords() {
        if (originRecords == null) {
            originRecords = new ArrayList<OriginRecord>();
        }
        return this.originRecords;
    }

    /**
     * Gets the value of the range property.
     * 
     * @return
     *     possible object is
     *     {@link TimeIntervalDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public TimeIntervalDef getRange() {
        return range;
    }

    /**
     * Sets the value of the range property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeIntervalDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setRange(TimeIntervalDef value) {
        this.range = value;
    }

    /**
     * Gets the value of the lastUpdateDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public XMLGregorianCalendar getLastUpdateDate() {
        return lastUpdateDate;
    }

    /**
     * Sets the value of the lastUpdateDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setLastUpdateDate(XMLGregorianCalendar value) {
        this.lastUpdateDate = value;
    }

    /**
     * Gets the value of the skipCleanse property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public boolean isSkipCleanse() {
        if (skipCleanse == null) {
            return false;
        } else {
            return skipCleanse;
        }
    }

    /**
     * Sets the value of the skipCleanse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setSkipCleanse(Boolean value) {
        this.skipCleanse = value;
    }

    /**
     * Gets the value of the bypassExtensionPoints property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public boolean isBypassExtensionPoints() {
        if (bypassExtensionPoints == null) {
            return false;
        } else {
            return bypassExtensionPoints;
        }
    }

    /**
     * Sets the value of the bypassExtensionPoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setBypassExtensionPoints(Boolean value) {
        this.bypassExtensionPoints = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsertList withEtalonRecords(EtalonRecord... values) {
        if (values!= null) {
            for (EtalonRecord value: values) {
                getEtalonRecords().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsertList withEtalonRecords(Collection<EtalonRecord> values) {
        if (values!= null) {
            getEtalonRecords().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsertList withOriginRecords(OriginRecord... values) {
        if (values!= null) {
            for (OriginRecord value: values) {
                getOriginRecords().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsertList withOriginRecords(Collection<OriginRecord> values) {
        if (values!= null) {
            getOriginRecords().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsertList withRange(TimeIntervalDef value) {
        setRange(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsertList withLastUpdateDate(XMLGregorianCalendar value) {
        setLastUpdateDate(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsertList withSkipCleanse(Boolean value) {
        setSkipCleanse(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsertList withBypassExtensionPoints(Boolean value) {
        setBypassExtensionPoints(value);
        return this;
    }

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
