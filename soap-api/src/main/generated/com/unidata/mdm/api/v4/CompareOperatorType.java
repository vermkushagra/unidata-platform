
package com.unidata.mdm.api.v4;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CompareOperatorType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CompareOperatorType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="EQUALS"/&gt;
 *     &lt;enumeration value="NOT_EQUALS"/&gt;
 *     &lt;enumeration value="GREATER"/&gt;
 *     &lt;enumeration value="GREATER_OR_EQUALS"/&gt;
 *     &lt;enumeration value="LESS"/&gt;
 *     &lt;enumeration value="LESS_OR_EQUALS"/&gt;
 *     &lt;enumeration value="LIKE"/&gt;
 *     &lt;enumeration value="FUZZY_EQUALS"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "CompareOperatorType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
public enum CompareOperatorType {

    EQUALS,
    NOT_EQUALS,
    GREATER,
    GREATER_OR_EQUALS,
    LESS,
    LESS_OR_EQUALS,
    LIKE,
    FUZZY_EQUALS;

    public String value() {
        return name();
    }

    public static CompareOperatorType fromValue(String v) {
        return valueOf(v);
    }

}
