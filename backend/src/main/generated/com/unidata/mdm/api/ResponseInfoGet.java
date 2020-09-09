//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.09.09 at 03:48:37 PM MSK 
//


package com.unidata.mdm.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Ответ на Запрос на получение общей информации для
 * 				конкретного типа
 * 				сущностей ('RequestInfoGet').
 * 				Общая часть ответа
 * 				содержит код возврата, идентификатор логической
 * 				операции и сообщения
 * 				об ошибках (смотри элемент 'common' из
 * 				структуры
 * 				'UnidataResponseBody').
 * 			
 * 
 * <p>Java class for ResponseInfoGet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseInfoGet"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://api.mdm.unidata.com/}UnidataAbstractResponse"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="statistic" type="{http://api.mdm.unidata.com/}Statistic" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseInfoGet", propOrder = {
    "statistic"
})
public class ResponseInfoGet
    extends UnidataAbstractResponse
    implements Serializable
{

    private final static long serialVersionUID = 12345L;
    @XmlElement(required = true)
    protected List<Statistic> statistic;

    /**
     * Gets the value of the statistic property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the statistic property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStatistic().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Statistic }
     * 
     * 
     */
    public List<Statistic> getStatistic() {
        if (statistic == null) {
            statistic = new ArrayList<Statistic>();
        }
        return this.statistic;
    }

    public ResponseInfoGet withStatistic(Statistic... values) {
        if (values!= null) {
            for (Statistic value: values) {
                getStatistic().add(value);
            }
        }
        return this;
    }

    public ResponseInfoGet withStatistic(Collection<Statistic> values) {
        if (values!= null) {
            getStatistic().addAll(values);
        }
        return this;
    }

}
