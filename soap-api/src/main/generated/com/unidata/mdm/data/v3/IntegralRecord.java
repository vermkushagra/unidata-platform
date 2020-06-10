
package com.unidata.mdm.data.v3;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 * Структура, описывающая неотделимую сущность. Используется для представления записи в композитных объектах. Состоит из обязательного имени связи, а также либо основной, либо исходной записи сущности.
 * Примером такой связанной сущности может служить информация о банковском счёте, неотделимой от сущности 'сотрудник'
 *             
 * 
 * <p>Java class for IntegralRecord complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntegralRecord"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://data.mdm.unidata.com/v3/}RelationBase"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="etalonRecord" type="{http://data.mdm.unidata.com/v3/}EtalonRecord"/&gt;
 *         &lt;element name="originRecord" type="{http://data.mdm.unidata.com/v3/}OriginRecord"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntegralRecord", propOrder = {
    "etalonRecord",
    "originRecord"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
public class IntegralRecord
    extends RelationBase
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected EtalonRecord etalonRecord;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected OriginRecord originRecord;

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

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public IntegralRecord withEtalonRecord(EtalonRecord value) {
        setEtalonRecord(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public IntegralRecord withOriginRecord(OriginRecord value) {
        setOriginRecord(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public IntegralRecord withRelName(String value) {
        setRelName(value);
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
