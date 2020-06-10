
package com.unidata.mdm.meta.v5;

import java.io.Serializable;
import java.util.Collection;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * <p>Java class for NestedEntityDef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NestedEntityDef"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://meta.mdm.unidata.com/v5/}ComplexAttributesHolderEntityDef"&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NestedEntityDef")
@XmlSeeAlso({
    EntityDef.class
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public class NestedEntityDef
    extends ComplexAttributesHolderEntityDef
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withComplexAttribute(ComplexAttributeDef... values) {
        if (values!= null) {
            for (ComplexAttributeDef value: values) {
                getComplexAttribute().add(value);
            }
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withComplexAttribute(Collection<ComplexAttributeDef> values) {
        if (values!= null) {
            getComplexAttribute().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withSimpleAttribute(SimpleAttributeDef... values) {
        if (values!= null) {
            for (SimpleAttributeDef value: values) {
                getSimpleAttribute().add(value);
            }
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withSimpleAttribute(Collection<SimpleAttributeDef> values) {
        if (values!= null) {
            getSimpleAttribute().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withArrayAttribute(ArrayAttributeDef... values) {
        if (values!= null) {
            for (ArrayAttributeDef value: values) {
                getArrayAttribute().add(value);
            }
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withArrayAttribute(Collection<ArrayAttributeDef> values) {
        if (values!= null) {
            getArrayAttribute().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withMergeSettings(MergeSettingsDef value) {
        setMergeSettings(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withClassifiers(String... values) {
        if (values!= null) {
            for (String value: values) {
                getClassifiers().add(value);
            }
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withClassifiers(Collection<String> values) {
        if (values!= null) {
            getClassifiers().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withClassifierProperties(ClassifierPropertyDef... values) {
        if (values!= null) {
            for (ClassifierPropertyDef value: values) {
                getClassifierProperties().add(value);
            }
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withClassifierProperties(Collection<ClassifierPropertyDef> values) {
        if (values!= null) {
            getClassifierProperties().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withValidityPeriod(PeriodBoundaryDef value) {
        setValidityPeriod(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withAttributeGroups(AttributeGroupDef... values) {
        if (values!= null) {
            for (AttributeGroupDef value: values) {
                getAttributeGroups().add(value);
            }
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withAttributeGroups(Collection<AttributeGroupDef> values) {
        if (values!= null) {
            getAttributeGroups().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withRelationGroups(RelationGroupDef... values) {
        if (values!= null) {
            for (RelationGroupDef value: values) {
                getRelationGroups().add(value);
            }
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withRelationGroups(Collection<RelationGroupDef> values) {
        if (values!= null) {
            getRelationGroups().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withClassifierGroups(ClassifierGroupDef... values) {
        if (values!= null) {
            for (ClassifierGroupDef value: values) {
                getClassifierGroups().add(value);
            }
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withClassifierGroups(Collection<ClassifierGroupDef> values) {
        if (values!= null) {
            getClassifierGroups().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withDataQuality(EntityDataQualityDef value) {
        setDataQuality(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withCustomProperties(CustomPropertyDef... values) {
        if (values!= null) {
            for (CustomPropertyDef value: values) {
                getCustomProperties().add(value);
            }
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withCustomProperties(Collection<CustomPropertyDef> values) {
        if (values!= null) {
            getCustomProperties().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withName(String value) {
        setName(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withDisplayName(String value) {
        setDisplayName(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withDescription(String value) {
        setDescription(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withVersion(Long value) {
        setVersion(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withUpdatedAt(XMLGregorianCalendar value) {
        setUpdatedAt(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public NestedEntityDef withCreateAt(XMLGregorianCalendar value) {
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
