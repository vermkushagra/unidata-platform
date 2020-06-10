
package com.unidata.mdm.api.v4;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 * And element.
 *             
 * 
 * <p>Java class for SearchAndDef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SearchAndDef"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://api.mdm.unidata.com/v4/}SearchBaseDef"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice maxOccurs="unbounded"&gt;
 *           &lt;element name="and" type="{http://api.mdm.unidata.com/v4/}SearchAndDef"/&gt;
 *           &lt;element name="or" type="{http://api.mdm.unidata.com/v4/}SearchOrDef"/&gt;
 *           &lt;element name="atom" type="{http://api.mdm.unidata.com/v4/}SearchAtomDef"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchAndDef", propOrder = {
    "expressions"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
public class SearchAndDef
    extends SearchBaseDef
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlElements({
        @XmlElement(name = "and", type = SearchAndDef.class),
        @XmlElement(name = "or", type = SearchOrDef.class),
        @XmlElement(name = "atom", type = SearchAtomDef.class)
    })
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    protected List<SearchBaseDef> expressions;

    /**
     * Gets the value of the expressions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the expressions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExpressions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SearchAndDef }
     * {@link SearchOrDef }
     * {@link SearchAtomDef }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public List<SearchBaseDef> getExpressions() {
        if (expressions == null) {
            expressions = new ArrayList<SearchBaseDef>();
        }
        return this.expressions;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public SearchAndDef withExpressions(SearchBaseDef... values) {
        if (values!= null) {
            for (SearchBaseDef value: values) {
                getExpressions().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
    public SearchAndDef withExpressions(Collection<SearchBaseDef> values) {
        if (values!= null) {
            getExpressions().addAll(values);
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
