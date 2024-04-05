//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.09.09 at 03:48:37 PM MSK 
//


package com.unidata.mdm.api;

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