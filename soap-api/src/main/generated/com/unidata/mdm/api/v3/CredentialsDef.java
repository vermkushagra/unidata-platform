
package com.unidata.mdm.api.v3;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 *                 Составная часть любого API запроса. Позволяет задать пользователя и пароль.
 *             
 * 
 * <p>Java class for CredentialsDef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CredentialsDef"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="username" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="password" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CredentialsDef")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
public class CredentialsDef
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;
    @XmlAttribute(name = "username", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected String username;
    @XmlAttribute(name = "password", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    protected String password;

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public void setPassword(String value) {
        this.password = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public CredentialsDef withUsername(String value) {
        setUsername(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public CredentialsDef withPassword(String value) {
        setPassword(value);
        return this;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
    public String toString() {
        return ToStringBuilder.reflectionToString(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}