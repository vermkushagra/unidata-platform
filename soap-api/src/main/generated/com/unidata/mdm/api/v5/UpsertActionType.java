
package com.unidata.mdm.api.v5;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpsertActionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="UpsertActionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Insert"/&gt;
 *     &lt;enumeration value="Update"/&gt;
 *     &lt;enumeration value="UpsertOrigin"/&gt;
 *     &lt;enumeration value="NoAction"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "UpsertActionType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public enum UpsertActionType {

    @XmlEnumValue("Insert")
    INSERT("Insert"),
    @XmlEnumValue("Update")
    UPDATE("Update"),
    @XmlEnumValue("UpsertOrigin")
    UPSERT_ORIGIN("UpsertOrigin"),
    @XmlEnumValue("NoAction")
    NO_ACTION("NoAction");
    private final String value;

    UpsertActionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UpsertActionType fromValue(String v) {
        for (UpsertActionType c: UpsertActionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
