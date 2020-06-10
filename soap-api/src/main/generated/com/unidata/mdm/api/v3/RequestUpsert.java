
package com.unidata.mdm.api.v3;

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
import com.unidata.mdm.data.v3.EtalonRecord;
import com.unidata.mdm.data.v3.OriginRecord;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 *                 Запрос на вставку или модификацию основной или исходной записи сущности.
 *                 Общая часть запроса должна содержать данные для аутентификации пользователя (смотри элемент 'common' из
 *                 структуры 'UnidataRequestBody').
 * 
 *                 'upsert' означает комбинацию слов 'update' и 'insert'.
 * 
 *                 Все операции по модификации данных производятся над исходными записями сущностей из конкретной системы
 *                 источника, включая служебную систему источник.
 *                 При этом основная запись сущности вычисляется заново как результат консолидации всех исходных записей.
 * 
 *                 Имеется возможность передать либо основную запись сущности (элемент 'etalonRecord'), либо исходную
 *                 запись (элемент 'originRecord').
 *                 При передаче основной записи, платформа подберёт соответствующую исходную запись из служебной системы
 *                 источника и
 *                 обеспечит выигрыш соответствующих значений атрибутов при консолидации.
 *                 Любая из переданных записей содержит ключ, на основе значения которого принимается решение будет ли это
 *                 операция вставки или изменения.
 * 
 *                 По умолчанию, к любой основной записи применяются все имеющиеся правила контроля качества данных.
 *                 Вызывающая сторона может принудительно отказаться от применения правил, передав skipCleanse='true'
 *                 В случае операции изменения записи, есть возможность передать выборочный набор атрибутов. Все
 *                 непереданные атрибуты остаются без изменений.
 * 
 *                 Поле range указывает границы действия версии данных. Отсутствующие значения означают бесконечность в
 *                 прошлом или будущем или их комбинацию. Отсутствующий элемент означает бесконечность с обоих концов.
 *             
 * 
 * <p>Java class for RequestUpsert complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestUpsert"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://api.mdm.unidata.com/v3/}UnidataAbstractRequest"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="etalonRecord" type="{http://data.mdm.unidata.com/v3/}EtalonRecord"/&gt;
 *           &lt;element name="originRecord" type="{http://data.mdm.unidata.com/v3/}OriginRecord"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="relations" type="{http://api.mdm.unidata.com/v3/}UpsertRelationsDef" minOccurs="0"/&gt;
 *         &lt;element name="range" type="{http://api.mdm.unidata.com/v3/}TimeIntervalDef" minOccurs="0"/&gt;
 *         &lt;element name="aliasCodeAttributePointers" type="{http://api.mdm.unidata.com/v3/}AliasCodeAttributePointerDef" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="classifierPointers" type="{http://api.mdm.unidata.com/v3/}ClassifierPointerDef" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="entityName" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="lastUpdateDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="skipCleanse" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="mergeWithPreviousVersion" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="bypassExtensionPoints" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestUpsert", propOrder = {
    "etalonRecord",
    "originRecord",
    "relations",
    "range",
    "aliasCodeAttributePointers",
    "classifierPointers"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
public class RequestUpsert
    extends UnidataAbstractRequest
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected EtalonRecord etalonRecord;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected OriginRecord originRecord;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected UpsertRelationsDef relations;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected TimeIntervalDef range;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected List<AliasCodeAttributePointerDef> aliasCodeAttributePointers;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected List<ClassifierPointerDef> classifierPointers;
    @XmlAttribute(name = "entityName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected String entityName;
    @XmlAttribute(name = "lastUpdateDate")
    @XmlSchemaType(name = "dateTime")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected XMLGregorianCalendar lastUpdateDate;
    @XmlAttribute(name = "skipCleanse")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected Boolean skipCleanse;
    @XmlAttribute(name = "mergeWithPreviousVersion")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected Boolean mergeWithPreviousVersion;
    @XmlAttribute(name = "bypassExtensionPoints")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected Boolean bypassExtensionPoints;

    /**
     * Gets the value of the etalonRecord property.
     * 
     * @return
     *     possible object is
     *     {@link EtalonRecord }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public EtalonRecord getEtalonRecord() {
        return etalonRecord;
    }

    /**
     * Sets the value of the etalonRecord property.
     * 
     * @param value
     *     allowed object is
     *     {@link EtalonRecord }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setEtalonRecord(EtalonRecord value) {
        this.etalonRecord = value;
    }

    /**
     * Gets the value of the originRecord property.
     * 
     * @return
     *     possible object is
     *     {@link OriginRecord }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public OriginRecord getOriginRecord() {
        return originRecord;
    }

    /**
     * Sets the value of the originRecord property.
     * 
     * @param value
     *     allowed object is
     *     {@link OriginRecord }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setOriginRecord(OriginRecord value) {
        this.originRecord = value;
    }

    /**
     * Gets the value of the relations property.
     * 
     * @return
     *     possible object is
     *     {@link UpsertRelationsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public UpsertRelationsDef getRelations() {
        return relations;
    }

    /**
     * Sets the value of the relations property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpsertRelationsDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setRelations(UpsertRelationsDef value) {
        this.relations = value;
    }

    /**
     * Gets the value of the range property.
     * 
     * @return
     *     possible object is
     *     {@link TimeIntervalDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setRange(TimeIntervalDef value) {
        this.range = value;
    }

    /**
     * Gets the value of the aliasCodeAttributePointers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the aliasCodeAttributePointers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAliasCodeAttributePointers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AliasCodeAttributePointerDef }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public List<AliasCodeAttributePointerDef> getAliasCodeAttributePointers() {
        if (aliasCodeAttributePointers == null) {
            aliasCodeAttributePointers = new ArrayList<AliasCodeAttributePointerDef>();
        }
        return this.aliasCodeAttributePointers;
    }

    /**
     * Gets the value of the classifierPointers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classifierPointers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClassifierPointers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClassifierPointerDef }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public List<ClassifierPointerDef> getClassifierPointers() {
        if (classifierPointers == null) {
            classifierPointers = new ArrayList<ClassifierPointerDef>();
        }
        return this.classifierPointers;
    }

    /**
     * Gets the value of the entityName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public String getEntityName() {
        return entityName;
    }

    /**
     * Sets the value of the entityName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setEntityName(String value) {
        this.entityName = value;
    }

    /**
     * Gets the value of the lastUpdateDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setSkipCleanse(Boolean value) {
        this.skipCleanse = value;
    }

    /**
     * Gets the value of the mergeWithPreviousVersion property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public boolean isMergeWithPreviousVersion() {
        if (mergeWithPreviousVersion == null) {
            return false;
        } else {
            return mergeWithPreviousVersion;
        }
    }

    /**
     * Sets the value of the mergeWithPreviousVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setMergeWithPreviousVersion(Boolean value) {
        this.mergeWithPreviousVersion = value;
    }

    /**
     * Gets the value of the bypassExtensionPoints property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setBypassExtensionPoints(Boolean value) {
        this.bypassExtensionPoints = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsert withEtalonRecord(EtalonRecord value) {
        setEtalonRecord(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsert withOriginRecord(OriginRecord value) {
        setOriginRecord(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsert withRelations(UpsertRelationsDef value) {
        setRelations(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsert withRange(TimeIntervalDef value) {
        setRange(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsert withAliasCodeAttributePointers(AliasCodeAttributePointerDef... values) {
        if (values!= null) {
            for (AliasCodeAttributePointerDef value: values) {
                getAliasCodeAttributePointers().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsert withAliasCodeAttributePointers(Collection<AliasCodeAttributePointerDef> values) {
        if (values!= null) {
            getAliasCodeAttributePointers().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsert withClassifierPointers(ClassifierPointerDef... values) {
        if (values!= null) {
            for (ClassifierPointerDef value: values) {
                getClassifierPointers().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsert withClassifierPointers(Collection<ClassifierPointerDef> values) {
        if (values!= null) {
            getClassifierPointers().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsert withEntityName(String value) {
        setEntityName(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsert withLastUpdateDate(XMLGregorianCalendar value) {
        setLastUpdateDate(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsert withSkipCleanse(Boolean value) {
        setSkipCleanse(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsert withMergeWithPreviousVersion(Boolean value) {
        setMergeWithPreviousVersion(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestUpsert withBypassExtensionPoints(Boolean value) {
        setBypassExtensionPoints(value);
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
