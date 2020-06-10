
package com.unidata.mdm.security.v5;

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
 * <p>Java class for LoginResponseWrapper complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LoginResponseWrapper"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://security.mdm.unidata.com/v5/}securityHeader"/&gt;
 *         &lt;element ref="{http://security.mdm.unidata.com/v5/}loginResponse"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LoginResponseWrapper", propOrder = {
    "securityHeader",
    "loginResponse"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public class LoginResponseWrapper
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected SessionTokenDef securityHeader;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    protected SessionTokenDef loginResponse;

    /**
     * Gets the value of the securityHeader property.
     * 
     * @return
     *     possible object is
     *     {@link SessionTokenDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setSecurityHeader(SessionTokenDef value) {
        this.securityHeader = value;
    }

    /**
     * Gets the value of the loginResponse property.
     * 
     * @return
     *     possible object is
     *     {@link SessionTokenDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public SessionTokenDef getLoginResponse() {
        return loginResponse;
    }

    /**
     * Sets the value of the loginResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link SessionTokenDef }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public void setLoginResponse(SessionTokenDef value) {
        this.loginResponse = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public LoginResponseWrapper withSecurityHeader(SessionTokenDef value) {
        setSecurityHeader(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public LoginResponseWrapper withLoginResponse(SessionTokenDef value) {
        setLoginResponse(value);
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
