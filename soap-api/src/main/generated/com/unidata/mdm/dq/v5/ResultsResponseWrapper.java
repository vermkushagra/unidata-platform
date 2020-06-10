
package com.unidata.mdm.dq.v5;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.unidata.mdm.api.v5.SessionTokenDef;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * <p>Java class for ResultsResponseWrapper complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResultsResponseWrapper"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://security.mdm.unidata.com/v5/}securityHeader"/&gt;
 *         &lt;element ref="{http://dq.mdm.unidata.com/v5/}infoHeader"/&gt;
 *         &lt;element ref="{http://dq.mdm.unidata.com/v5/}getResultsResponse"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResultsResponseWrapper", propOrder = {
    "securityHeader",
    "infoHeader",
    "getResultsResponse"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
public class ResultsResponseWrapper
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlElement(namespace = "http://security.mdm.unidata.com/v5/", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    protected SessionTokenDef securityHeader;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    protected InfoType infoHeader;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    protected ResultsResponseType getResultsResponse;

    /**
     * Gets the value of the securityHeader property.
     * 
     * @return
     *     possible object is
     *     {@link SessionTokenDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public SessionTokenDef getSecurityHeader() {
        return securityHeader;
    }

    /**
     * Sets the value of the securityHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link SessionTokenDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public void setSecurityHeader(SessionTokenDef value) {
        this.securityHeader = value;
    }

    /**
     * Gets the value of the infoHeader property.
     * 
     * @return
     *     possible object is
     *     {@link InfoType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public InfoType getInfoHeader() {
        return infoHeader;
    }

    /**
     * Sets the value of the infoHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link InfoType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public void setInfoHeader(InfoType value) {
        this.infoHeader = value;
    }

    /**
     * Gets the value of the getResultsResponse property.
     * 
     * @return
     *     possible object is
     *     {@link ResultsResponseType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ResultsResponseType getGetResultsResponse() {
        return getResultsResponse;
    }

    /**
     * Sets the value of the getResultsResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultsResponseType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public void setGetResultsResponse(ResultsResponseType value) {
        this.getResultsResponse = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ResultsResponseWrapper withSecurityHeader(SessionTokenDef value) {
        setSecurityHeader(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ResultsResponseWrapper withInfoHeader(InfoType value) {
        setInfoHeader(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ResultsResponseWrapper withGetResultsResponse(ResultsResponseType value) {
        setGetResultsResponse(value);
        return this;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public String toString() {
        return ToStringBuilder.reflectionToString(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
