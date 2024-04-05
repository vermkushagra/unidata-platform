
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
 * <p>Java class for ResultsRequestWrapper complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResultsRequestWrapper"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://security.mdm.unidata.com/v5/}securityHeader"/&gt;
 *         &lt;element ref="{http://dq.mdm.unidata.com/v5/}infoHeader"/&gt;
 *         &lt;element ref="{http://dq.mdm.unidata.com/v5/}getResultsRequest"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResultsRequestWrapper", propOrder = {
    "securityHeader",
    "infoHeader",
    "getResultsRequest"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
public class ResultsRequestWrapper
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
    protected ResultsRequestType getResultsRequest;

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
     * Gets the value of the getResultsRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ResultsRequestType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ResultsRequestType getGetResultsRequest() {
        return getResultsRequest;
    }

    /**
     * Sets the value of the getResultsRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultsRequestType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public void setGetResultsRequest(ResultsRequestType value) {
        this.getResultsRequest = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ResultsRequestWrapper withSecurityHeader(SessionTokenDef value) {
        setSecurityHeader(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ResultsRequestWrapper withInfoHeader(InfoType value) {
        setInfoHeader(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:15+03:00", comments = "JAXB RI v2.2.11")
    public ResultsRequestWrapper withGetResultsRequest(ResultsRequestType value) {
        setGetResultsRequest(value);
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