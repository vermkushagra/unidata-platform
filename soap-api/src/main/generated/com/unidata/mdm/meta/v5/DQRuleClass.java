
package com.unidata.mdm.meta.v5;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DQRuleClass.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DQRuleClass"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="SYSTEM"/&gt;
 *     &lt;enumeration value="USER_DEFINED"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DQRuleClass")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public enum DQRuleClass {

    SYSTEM,
    USER_DEFINED;

    public String value() {
        return name();
    }

    public static DQRuleClass fromValue(String v) {
        return valueOf(v);
    }

}
