
package com.unidata.mdm.api.v3;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import com.unidata.mdm.data.v3.EtalonKey;
import com.unidata.mdm.data.v3.OriginKey;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 *                 Запрос на логическое удаление основной записи сущности, исходной записи сущности или ее отдельного
 *                 периода.
 *                 Общая часть запроса должна содержать данные для аутентификации пользователя (смотри элемент 'common' из
 *                 структуры 'UnidataRequestBody').
 *                 Логическое удаление позволяет пометить либо исходную запись из конкретной системы источника, либо
 *                 основную запись, как логически удалённую, при этом такие записи выпадают из обычных операции, таких как
 *                 поиск.
 *                 При логическом удалении одной исходной записи из конкретной системы источника, основная запись может
 *                 продолжать оставаться активной, если в её составе есть другие активные исходные записи.
 * 
 *                 Поле range указывает границы действия НЕАКТИВНОЙ (удаленной) версии данных для указанного источника,
 *                 которая будет создана в результате запроса, если речь идет о деактивации периода.
 *                 Отсутствующие значения означают бесконечность в прошлом или будущем или их комбинацию.
 *                 Тип удаления указывается в поле actionType.
 *                 При наличии прав запрос может содержать значение true в поле wipe. В этом случае запись будет ФИЗИЧЕСКИ
 *                 и БЕЗВОЗВРАТНО удалена из хранилищи (hard delete).
 *             
 * 
 * <p>Java class for RequestSoftDelete complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestSoftDelete"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://api.mdm.unidata.com/v3/}UnidataAbstractRequest"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="etalonKey" type="{http://data.mdm.unidata.com/v3/}EtalonKey"/&gt;
 *           &lt;element name="originKey" type="{http://data.mdm.unidata.com/v3/}OriginKey"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="range" type="{http://api.mdm.unidata.com/v3/}TimeIntervalDef" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="actionType" use="required" type="{http://api.mdm.unidata.com/v3/}SoftDeleteActionType" /&gt;
 *       &lt;attribute name="wipe" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestSoftDelete", propOrder = {
    "etalonKey",
    "originKey",
    "range"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
public class RequestSoftDelete
    extends UnidataAbstractRequest
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected EtalonKey etalonKey;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected OriginKey originKey;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected TimeIntervalDef range;
    @XmlAttribute(name = "actionType", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected SoftDeleteActionType actionType;
    @XmlAttribute(name = "wipe")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected Boolean wipe;

    /**
     * Gets the value of the etalonKey property.
     * 
     * @return
     *     possible object is
     *     {@link EtalonKey }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public EtalonKey getEtalonKey() {
        return etalonKey;
    }

    /**
     * Sets the value of the etalonKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link EtalonKey }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setEtalonKey(EtalonKey value) {
        this.etalonKey = value;
    }

    /**
     * Gets the value of the originKey property.
     * 
     * @return
     *     possible object is
     *     {@link OriginKey }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public OriginKey getOriginKey() {
        return originKey;
    }

    /**
     * Sets the value of the originKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link OriginKey }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setOriginKey(OriginKey value) {
        this.originKey = value;
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
     * Gets the value of the actionType property.
     * 
     * @return
     *     possible object is
     *     {@link SoftDeleteActionType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public SoftDeleteActionType getActionType() {
        return actionType;
    }

    /**
     * Sets the value of the actionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link SoftDeleteActionType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setActionType(SoftDeleteActionType value) {
        this.actionType = value;
    }

    /**
     * Gets the value of the wipe property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public Boolean isWipe() {
        return wipe;
    }

    /**
     * Sets the value of the wipe property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setWipe(Boolean value) {
        this.wipe = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestSoftDelete withEtalonKey(EtalonKey value) {
        setEtalonKey(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestSoftDelete withOriginKey(OriginKey value) {
        setOriginKey(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestSoftDelete withRange(TimeIntervalDef value) {
        setRange(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestSoftDelete withActionType(SoftDeleteActionType value) {
        setActionType(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public RequestSoftDelete withWipe(Boolean value) {
        setWipe(value);
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
