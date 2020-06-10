
package com.unidata.mdm.meta.v5;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ConstantValueType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ConstantValueType"&gt;
 *   &lt;restriction base="{http://meta.mdm.unidata.com/v5/}SimpleDataType"&gt;
 *     &lt;enumeration value="Date"/&gt;
 *     &lt;enumeration value="Time"/&gt;
 *     &lt;enumeration value="Timestamp"/&gt;
 *     &lt;enumeration value="String"/&gt;
 *     &lt;enumeration value="Integer"/&gt;
 *     &lt;enumeration value="Number"/&gt;
 *     &lt;enumeration value="Boolean"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ConstantValueType")
@XmlEnum(SimpleDataType.class)
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public enum ConstantValueType {

    @XmlEnumValue("Date")
    DATE(SimpleDataType.DATE),
    @XmlEnumValue("Time")
    TIME(SimpleDataType.TIME),
    @XmlEnumValue("Timestamp")
    TIMESTAMP(SimpleDataType.TIMESTAMP),
    @XmlEnumValue("String")
    STRING(SimpleDataType.STRING),
    @XmlEnumValue("Integer")
    INTEGER(SimpleDataType.INTEGER),
    @XmlEnumValue("Number")
    NUMBER(SimpleDataType.NUMBER),
    @XmlEnumValue("Boolean")
    BOOLEAN(SimpleDataType.BOOLEAN);
    private final SimpleDataType value;

    ConstantValueType(SimpleDataType v) {
        value = v;
    }

    public SimpleDataType value() {
        return value;
    }

    public static ConstantValueType fromValue(SimpleDataType v) {
        for (ConstantValueType c: ConstantValueType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

}
