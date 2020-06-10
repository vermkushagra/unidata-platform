
package com.unidata.mdm.api.v4;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 * Ответ на запрос на вставку или модификацию основной или исходной записи сущности ('RequestRelationsUpsert').
 * Общая часть ответа содержит код возврата, идентификатор логической операции и сообщения об ошибках (смотри элемент 'common' из структуры 'UnidataResponseBody').
 * В случае успешного исполнения всегда содержит два ключа, один идентифицирующий исходную запись, фактически изменённую, второй идентифицирующий основную запись сущности.
 * Помимо этого возращается тип действия, фактически выполненного платформой - 'action'.
 *             
 * 
 * <p>Java class for ResponseRelationsUpsert complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseRelationsUpsert"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://api.mdm.unidata.com/v4/}UnidataAbstractResponse"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="upsertCount" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="originAction" use="required" type="{http://api.mdm.unidata.com/v4/}UpsertActionType" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseRelationsUpsert", propOrder = {
    "upsertCount"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
public class ResponseRelationsUpsert
    extends UnidataAbstractResponse
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected int upsertCount;
    @XmlAttribute(name = "originAction", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected UpsertActionType originAction;

    /**
     * Gets the value of the upsertCount property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public int getUpsertCount() {
        return upsertCount;
    }

    /**
     * Sets the value of the upsertCount property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setUpsertCount(int value) {
        this.upsertCount = value;
    }

    /**
     * Gets the value of the originAction property.
     * 
     * @return
     *     possible object is
     *     {@link UpsertActionType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public UpsertActionType getOriginAction() {
        return originAction;
    }

    /**
     * Sets the value of the originAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpsertActionType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public void setOriginAction(UpsertActionType value) {
        this.originAction = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public ResponseRelationsUpsert withUpsertCount(int value) {
        setUpsertCount(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public ResponseRelationsUpsert withOriginAction(UpsertActionType value) {
        setOriginAction(value);
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
