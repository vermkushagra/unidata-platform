
package com.unidata.mdm.meta.v5;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SimpleDataType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SimpleDataType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Date"/&gt;
 *     &lt;enumeration value="Time"/&gt;
 *     &lt;enumeration value="Timestamp"/&gt;
 *     &lt;enumeration value="String"/&gt;
 *     &lt;enumeration value="Integer"/&gt;
 *     &lt;enumeration value="Number"/&gt;
 *     &lt;enumeration value="Boolean"/&gt;
 *     &lt;enumeration value="Blob"/&gt;
 *     &lt;enumeration value="Clob"/&gt;
 *     &lt;enumeration value="Measured"/&gt;
 *     &lt;enumeration value="Any"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "SimpleDataType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public enum SimpleDataType {

    @XmlEnumValue("Date")
    DATE("Date"),
    @XmlEnumValue("Time")
    TIME("Time"),
    @XmlEnumValue("Timestamp")
    TIMESTAMP("Timestamp"),
    @XmlEnumValue("String")
    STRING("String"),
    @XmlEnumValue("Integer")
    INTEGER("Integer"),
    @XmlEnumValue("Number")
    NUMBER("Number"),
    @XmlEnumValue("Boolean")
    BOOLEAN("Boolean"),
    @XmlEnumValue("Blob")
    BLOB("Blob"),
    @XmlEnumValue("Clob")
    CLOB("Clob"),
    @XmlEnumValue("Measured")
    MEASURED("Measured"),
    @XmlEnumValue("Any")
    ANY("Any");
    private final String value;

    SimpleDataType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SimpleDataType fromValue(String v) {
        for (SimpleDataType c: SimpleDataType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}