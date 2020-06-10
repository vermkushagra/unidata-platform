
package com.unidata.mdm.api.v5;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import com.unidata.mdm.data.v5.EtalonKey;
import com.unidata.mdm.data.v5.OriginKey;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 * Запрос на консолидацию нескольких основных записей сущности.
 * Общая часть запроса должна содержать данные для аутентификации пользователя (смотри элемент 'common' из структуры 'UnidataRequestBody').
 * 
 * Любая консолидация всегда производится над основными записями сущности, но при этом у пользователя есть возможность идентификации основной записи ключом от исходной записи из системы источника
 * Запрос всегда содержит один ключ, идентифицирующий выигрывающую запись и несколько ключей проигравших записей.
 * 
 * По умолчанию, к консолидированной записи применяются все имеющиеся правила контроля качества данных. Вызывающая сторона может принудительно отказаться от применения правил, передав skipCleanse='true'
 *             
 * 
 * <p>Java class for RequestMerge complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestMerge"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://api.mdm.unidata.com/v5/}UnidataAbstractRequest"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="masterEtalonKey" type="{http://data.mdm.unidata.com/v5/}EtalonKey"/&gt;
 *           &lt;element name="masterOriginKey" type="{http://data.mdm.unidata.com/v5/}OriginKey"/&gt;
 *         &lt;/choice&gt;
 *         &lt;choice maxOccurs="unbounded"&gt;
 *           &lt;element name="duplicateEtalonKey" type="{http://data.mdm.unidata.com/v5/}EtalonKey"/&gt;
 *           &lt;element name="duplicateOriginKey" type="{http://data.mdm.unidata.com/v5/}OriginKey"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="entityName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
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
@XmlType(name = "RequestMerge", propOrder = {
    "masterEtalonKey",
    "masterOriginKey",
    "duplicateEtalonKeyOrDuplicateOriginKey"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public class RequestMerge
    extends UnidataAbstractRequest
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected EtalonKey masterEtalonKey;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected OriginKey masterOriginKey;
    @XmlElements({
        @XmlElement(name = "duplicateEtalonKey", type = EtalonKey.class),
        @XmlElement(name = "duplicateOriginKey", type = OriginKey.class)
    })
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected List<Serializable> duplicateEtalonKeyOrDuplicateOriginKey;
    @XmlAttribute(name = "entityName", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected String entityName;
    @XmlAttribute(name = "skipCleanse")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected Boolean skipCleanse;
    @XmlAttribute(name = "bypassExtensionPoints")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected Boolean bypassExtensionPoints;

    /**
     * Gets the value of the masterEtalonKey property.
     * 
     * @return
     *     possible object is
     *     {@link EtalonKey }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public EtalonKey getMasterEtalonKey() {
        return masterEtalonKey;
    }

    /**
     * Sets the value of the masterEtalonKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link EtalonKey }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setMasterEtalonKey(EtalonKey value) {
        this.masterEtalonKey = value;
    }

    /**
     * Gets the value of the masterOriginKey property.
     * 
     * @return
     *     possible object is
     *     {@link OriginKey }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public OriginKey getMasterOriginKey() {
        return masterOriginKey;
    }

    /**
     * Sets the value of the masterOriginKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link OriginKey }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setMasterOriginKey(OriginKey value) {
        this.masterOriginKey = value;
    }

    /**
     * Gets the value of the duplicateEtalonKeyOrDuplicateOriginKey property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the duplicateEtalonKeyOrDuplicateOriginKey property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDuplicateEtalonKeyOrDuplicateOriginKey().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EtalonKey }
     * {@link OriginKey }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public List<Serializable> getDuplicateEtalonKeyOrDuplicateOriginKey() {
        if (duplicateEtalonKeyOrDuplicateOriginKey == null) {
            duplicateEtalonKeyOrDuplicateOriginKey = new ArrayList<Serializable>();
        }
        return this.duplicateEtalonKeyOrDuplicateOriginKey;
    }

    /**
     * Gets the value of the entityName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setEntityName(String value) {
        this.entityName = value;
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
    public RequestMerge withMasterEtalonKey(EtalonKey value) {
        setMasterEtalonKey(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public RequestMerge withMasterOriginKey(OriginKey value) {
        setMasterOriginKey(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public RequestMerge withDuplicateEtalonKeyOrDuplicateOriginKey(Serializable... values) {
        if (values!= null) {
            for (Serializable value: values) {
                getDuplicateEtalonKeyOrDuplicateOriginKey().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public RequestMerge withDuplicateEtalonKeyOrDuplicateOriginKey(Collection<Serializable> values) {
        if (values!= null) {
            getDuplicateEtalonKeyOrDuplicateOriginKey().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public RequestMerge withEntityName(String value) {
        setEntityName(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public RequestMerge withSkipCleanse(Boolean value) {
        setSkipCleanse(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public RequestMerge withBypassExtensionPoints(Boolean value) {
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
