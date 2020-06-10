
package com.unidata.mdm.api.v5;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.unidata.mdm.data.v5.Cluster;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 *                 Ответ на запрос проверки записи на потенциальыне дубликаты
 *             
 * 
 * <p>Java class for ResponseCheckDuplicates complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseCheckDuplicates"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://api.mdm.unidata.com/v5/}UnidataAbstractResponse"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="clusters" type="{http://data.mdm.unidata.com/v5/}Cluster" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseCheckDuplicates", propOrder = {
    "clusters"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public class ResponseCheckDuplicates
    extends UnidataAbstractResponse
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected List<Cluster> clusters;

    /**
     * Gets the value of the clusters property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the clusters property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClusters().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Cluster }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public List<Cluster> getClusters() {
        if (clusters == null) {
            clusters = new ArrayList<Cluster>();
        }
        return this.clusters;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public ResponseCheckDuplicates withClusters(Cluster... values) {
        if (values!= null) {
            for (Cluster value: values) {
                getClusters().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public ResponseCheckDuplicates withClusters(Collection<Cluster> values) {
        if (values!= null) {
            getClusters().addAll(values);
        }
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
