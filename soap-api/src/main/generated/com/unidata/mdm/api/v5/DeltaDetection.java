
package com.unidata.mdm.api.v5;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DeltaDetection.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DeltaDetection"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="None"/&gt;
 *     &lt;enumeration value="AttributesOnly"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DeltaDetection")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public enum DeltaDetection {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("AttributesOnly")
    ATTRIBUTES_ONLY("AttributesOnly");
    private final String value;

    DeltaDetection(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DeltaDetection fromValue(String v) {
        for (DeltaDetection c: DeltaDetection.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
