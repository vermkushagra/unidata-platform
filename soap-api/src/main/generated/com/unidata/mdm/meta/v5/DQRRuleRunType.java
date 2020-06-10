
package com.unidata.mdm.meta.v5;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DQRRuleRunType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DQRRuleRunType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="RUN_ALWAYS"/&gt;
 *     &lt;enumeration value="RUN_NEVER"/&gt;
 *     &lt;enumeration value="RUN_ON_REQUIRED_PRESENT"/&gt;
 *     &lt;enumeration value="RUN_ON_ALL_PRESENT"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DQRRuleRunType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public enum DQRRuleRunType {

    RUN_ALWAYS,
    RUN_NEVER,
    RUN_ON_REQUIRED_PRESENT,
    RUN_ON_ALL_PRESENT;

    public String value() {
        return name();
    }

    public static DQRRuleRunType fromValue(String v) {
        return valueOf(v);
    }

}
