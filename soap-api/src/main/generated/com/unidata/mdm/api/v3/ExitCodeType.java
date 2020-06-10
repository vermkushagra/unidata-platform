
package com.unidata.mdm.api.v3;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExitCodeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ExitCodeType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Success"/&gt;
 *     &lt;enumeration value="Error"/&gt;
 *     &lt;enumeration value="Warning"/&gt;
 *     &lt;enumeration value="AuthenticationError"/&gt;
 *     &lt;enumeration value="AuthorizationError"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ExitCodeType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
public enum ExitCodeType {

    @XmlEnumValue("Success")
    SUCCESS("Success"),
    @XmlEnumValue("Error")
    ERROR("Error"),
    @XmlEnumValue("Warning")
    WARNING("Warning"),
    @XmlEnumValue("AuthenticationError")
    AUTHENTICATION_ERROR("AuthenticationError"),
    @XmlEnumValue("AuthorizationError")
    AUTHORIZATION_ERROR("AuthorizationError");
    private final String value;

    ExitCodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ExitCodeType fromValue(String v) {
        for (ExitCodeType c: ExitCodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
