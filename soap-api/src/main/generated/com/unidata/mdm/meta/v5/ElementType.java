
package com.unidata.mdm.meta.v5;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ElementType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ElementType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ENTITY"/&gt;
 *     &lt;enumeration value="NESTED_ENTITY"/&gt;
 *     &lt;enumeration value="LOOKUP"/&gt;
 *     &lt;enumeration value="RELATION"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ElementType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public enum ElementType {

    ENTITY,
    NESTED_ENTITY,
    LOOKUP,
    RELATION;

    public String value() {
        return name();
    }

    public static ElementType fromValue(String v) {
        return valueOf(v);
    }

}
