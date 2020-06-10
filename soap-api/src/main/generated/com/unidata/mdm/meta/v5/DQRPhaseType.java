
package com.unidata.mdm.meta.v5;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DQRPhaseType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DQRPhaseType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="BEFORE_UPSERT"/&gt;
 *     &lt;enumeration value="AFTER_UPSERT"/&gt;
 *     &lt;enumeration value="AFTER_MERGE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DQRPhaseType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public enum DQRPhaseType {

    BEFORE_UPSERT,
    AFTER_UPSERT,
    AFTER_MERGE;

    public String value() {
        return name();
    }

    public static DQRPhaseType fromValue(String v) {
        return valueOf(v);
    }

}
