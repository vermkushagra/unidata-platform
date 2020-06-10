
package com.unidata.mdm.data.v4;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 * Вспомогательная структура содержащая информацию о связях данной сущности. Состоит из набора обычных связей к другим сущностям ('relationTo'), а также набора неотделимых сущностей ('integralEntity')
 *             
 * 
 * <p>Java class for EntityRelations complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EntityRelations"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="integralEntities" type="{http://data.mdm.unidata.com/v4/}IntegralRecord" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="relationsTo" type="{http://data.mdm.unidata.com/v4/}RelationTo" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EntityRelations", propOrder = {
    "integralEntities",
    "relationsTo"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
public class EntityRelations
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected List<IntegralRecord> integralEntities;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected List<RelationTo> relationsTo;

    /**
     * Gets the value of the integralEntities property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the integralEntities property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIntegralEntities().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IntegralRecord }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public List<IntegralRecord> getIntegralEntities() {
        if (integralEntities == null) {
            integralEntities = new ArrayList<IntegralRecord>();
        }
        return this.integralEntities;
    }

    /**
     * Gets the value of the relationsTo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relationsTo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelationsTo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RelationTo }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public List<RelationTo> getRelationsTo() {
        if (relationsTo == null) {
            relationsTo = new ArrayList<RelationTo>();
        }
        return this.relationsTo;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public EntityRelations withIntegralEntities(IntegralRecord... values) {
        if (values!= null) {
            for (IntegralRecord value: values) {
                getIntegralEntities().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public EntityRelations withIntegralEntities(Collection<IntegralRecord> values) {
        if (values!= null) {
            getIntegralEntities().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public EntityRelations withRelationsTo(RelationTo... values) {
        if (values!= null) {
            for (RelationTo value: values) {
                getRelationsTo().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public EntityRelations withRelationsTo(Collection<RelationTo> values) {
        if (values!= null) {
            getRelationsTo().addAll(values);
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
