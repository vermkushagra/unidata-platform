
package com.unidata.mdm.meta.v5;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * <p>Java class for SimpleAttributesHolderEntityDef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SimpleAttributesHolderEntityDef"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://meta.mdm.unidata.com/v5/}AbstractEntityDef"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="simpleAttribute" type="{http://meta.mdm.unidata.com/v5/}SimpleAttributeDef" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="arrayAttribute" type="{http://meta.mdm.unidata.com/v5/}ArrayAttributeDef" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimpleAttributesHolderEntityDef", propOrder = {
    "simpleAttribute",
    "arrayAttribute"
})
@XmlSeeAlso({
    LookupEntityDef.class,
    ComplexAttributesHolderEntityDef.class
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public class SimpleAttributesHolderEntityDef
    extends AbstractEntityDef
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlElement(nillable = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected List<SimpleAttributeDef> simpleAttribute;
    @XmlElement(nillable = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected List<ArrayAttributeDef> arrayAttribute;

    /**
     * Gets the value of the simpleAttribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the simpleAttribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSimpleAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SimpleAttributeDef }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public List<SimpleAttributeDef> getSimpleAttribute() {
        if (simpleAttribute == null) {
            simpleAttribute = new ArrayList<SimpleAttributeDef>();
        }
        return this.simpleAttribute;
    }

    /**
     * Gets the value of the arrayAttribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the arrayAttribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArrayAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArrayAttributeDef }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public List<ArrayAttributeDef> getArrayAttribute() {
        if (arrayAttribute == null) {
            arrayAttribute = new ArrayList<ArrayAttributeDef>();
        }
        return this.arrayAttribute;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withSimpleAttribute(SimpleAttributeDef... values) {
        if (values!= null) {
            for (SimpleAttributeDef value: values) {
                getSimpleAttribute().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withSimpleAttribute(Collection<SimpleAttributeDef> values) {
        if (values!= null) {
            getSimpleAttribute().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withArrayAttribute(ArrayAttributeDef... values) {
        if (values!= null) {
            for (ArrayAttributeDef value: values) {
                getArrayAttribute().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withArrayAttribute(Collection<ArrayAttributeDef> values) {
        if (values!= null) {
            getArrayAttribute().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withMergeSettings(MergeSettingsDef value) {
        setMergeSettings(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withClassifiers(String... values) {
        if (values!= null) {
            for (String value: values) {
                getClassifiers().add(value);
            }
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withClassifiers(Collection<String> values) {
        if (values!= null) {
            getClassifiers().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withClassifierProperties(ClassifierPropertyDef... values) {
        if (values!= null) {
            for (ClassifierPropertyDef value: values) {
                getClassifierProperties().add(value);
            }
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withClassifierProperties(Collection<ClassifierPropertyDef> values) {
        if (values!= null) {
            getClassifierProperties().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withValidityPeriod(PeriodBoundaryDef value) {
        setValidityPeriod(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withAttributeGroups(AttributeGroupDef... values) {
        if (values!= null) {
            for (AttributeGroupDef value: values) {
                getAttributeGroups().add(value);
            }
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withAttributeGroups(Collection<AttributeGroupDef> values) {
        if (values!= null) {
            getAttributeGroups().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withRelationGroups(RelationGroupDef... values) {
        if (values!= null) {
            for (RelationGroupDef value: values) {
                getRelationGroups().add(value);
            }
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withRelationGroups(Collection<RelationGroupDef> values) {
        if (values!= null) {
            getRelationGroups().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withClassifierGroups(ClassifierGroupDef... values) {
        if (values!= null) {
            for (ClassifierGroupDef value: values) {
                getClassifierGroups().add(value);
            }
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withClassifierGroups(Collection<ClassifierGroupDef> values) {
        if (values!= null) {
            getClassifierGroups().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withDataQuality(EntityDataQualityDef value) {
        setDataQuality(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withCustomProperties(CustomPropertyDef... values) {
        if (values!= null) {
            for (CustomPropertyDef value: values) {
                getCustomProperties().add(value);
            }
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withCustomProperties(Collection<CustomPropertyDef> values) {
        if (values!= null) {
            getCustomProperties().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withName(String value) {
        setName(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withDisplayName(String value) {
        setDisplayName(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withDescription(String value) {
        setDescription(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withVersion(Long value) {
        setVersion(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withUpdatedAt(XMLGregorianCalendar value) {
        setUpdatedAt(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SimpleAttributesHolderEntityDef withCreateAt(XMLGregorianCalendar value) {
        setCreateAt(value);
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
