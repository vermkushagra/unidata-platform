
package com.unidata.mdm.meta.v5;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * <p>Java class for BVRMergeTypeDef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BVRMergeTypeDef"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://meta.mdm.unidata.com/v5/}AbstractMergeTypeDef"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sourceSystemsConfig" type="{http://meta.mdm.unidata.com/v5/}ListOfSourceSystems"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BVRMergeTypeDef", propOrder = {
    "sourceSystemsConfig"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public class BVRMergeTypeDef
    extends AbstractMergeTypeDef
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected ListOfSourceSystems sourceSystemsConfig;

    /**
     * Gets the value of the sourceSystemsConfig property.
     * 
     * @return
     *     possible object is
     *     {@link ListOfSourceSystems }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public ListOfSourceSystems getSourceSystemsConfig() {
        return sourceSystemsConfig;
    }

    /**
     * Sets the value of the sourceSystemsConfig property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListOfSourceSystems }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setSourceSystemsConfig(ListOfSourceSystems value) {
        this.sourceSystemsConfig = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public BVRMergeTypeDef withSourceSystemsConfig(ListOfSourceSystems value) {
        setSourceSystemsConfig(value);
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
