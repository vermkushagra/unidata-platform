
package com.unidata.mdm.api.v4;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClassifierPointerType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ClassifierPointerType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="NODE_NAME"/&gt;
 *     &lt;enumeration value="NODE_CODE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ClassifierPointerType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
public enum ClassifierPointerType {

    NODE_NAME,
    NODE_CODE;

    public String value() {
        return name();
    }

    public static ClassifierPointerType fromValue(String v) {
        return valueOf(v);
    }

}
