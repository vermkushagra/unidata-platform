
package com.unidata.mdm.api.v4;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UnidataEventType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="UnidataEventType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Upsert"/&gt;
 *     &lt;enumeration value="Merge"/&gt;
 *     &lt;enumeration value="SoftDelete"/&gt;
 *     &lt;enumeration value="WipeDelete"/&gt;
 *     &lt;enumeration value="Restore"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "UnidataEventType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
public enum UnidataEventType {

    @XmlEnumValue("Upsert")
    UPSERT("Upsert"),
    @XmlEnumValue("Merge")
    MERGE("Merge"),
    @XmlEnumValue("SoftDelete")
    SOFT_DELETE("SoftDelete"),
    @XmlEnumValue("WipeDelete")
    WIPE_DELETE("WipeDelete"),
    @XmlEnumValue("Restore")
    RESTORE("Restore");
    private final String value;

    UnidataEventType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UnidataEventType fromValue(String v) {
        for (UnidataEventType c: UnidataEventType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
