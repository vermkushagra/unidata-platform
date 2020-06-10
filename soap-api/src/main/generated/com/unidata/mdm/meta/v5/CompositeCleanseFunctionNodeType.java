
package com.unidata.mdm.meta.v5;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CompositeCleanseFunctionNodeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CompositeCleanseFunctionNodeType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Constant"/&gt;
 *     &lt;enumeration value="InputPorts"/&gt;
 *     &lt;enumeration value="OutputPorts"/&gt;
 *     &lt;enumeration value="Function"/&gt;
 *     &lt;enumeration value="Switch"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "CompositeCleanseFunctionNodeType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public enum CompositeCleanseFunctionNodeType {

    @XmlEnumValue("Constant")
    CONSTANT("Constant"),
    @XmlEnumValue("InputPorts")
    INPUT_PORTS("InputPorts"),
    @XmlEnumValue("OutputPorts")
    OUTPUT_PORTS("OutputPorts"),
    @XmlEnumValue("Function")
    FUNCTION("Function"),
    @XmlEnumValue("Switch")
    SWITCH("Switch");
    private final String value;

    CompositeCleanseFunctionNodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CompositeCleanseFunctionNodeType fromValue(String v) {
        for (CompositeCleanseFunctionNodeType c: CompositeCleanseFunctionNodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
