
package com.unidata.mdm.dq.v4;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DQApplyStatusType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DQApplyStatusType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ENRICHED"/&gt;
 *     &lt;enumeration value="CONTAINS_ERRORS"/&gt;
 *     &lt;enumeration value="VALID"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DQApplyStatusType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
public enum DQApplyStatusType {

    ENRICHED,
    CONTAINS_ERRORS,
    VALID;

    public String value() {
        return name();
    }

    public static DQApplyStatusType fromValue(String v) {
        return valueOf(v);
    }

}
