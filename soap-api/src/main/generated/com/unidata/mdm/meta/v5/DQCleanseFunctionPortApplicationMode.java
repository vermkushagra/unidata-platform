
package com.unidata.mdm.meta.v5;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DQCleanseFunctionPortApplicationMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DQCleanseFunctionPortApplicationMode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="MODE_ALL"/&gt;
 *     &lt;enumeration value="MODE_ALL_WITH_INCOMPLETE"/&gt;
 *     &lt;enumeration value="MODE_ONCE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DQCleanseFunctionPortApplicationMode")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public enum DQCleanseFunctionPortApplicationMode {

    MODE_ALL,
    MODE_ALL_WITH_INCOMPLETE,
    MODE_ONCE;

    public String value() {
        return name();
    }

    public static DQCleanseFunctionPortApplicationMode fromValue(String v) {
        return valueOf(v);
    }

}
