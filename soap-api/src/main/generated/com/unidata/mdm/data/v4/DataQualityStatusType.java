
package com.unidata.mdm.data.v4;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DataQualityStatusType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DataQualityStatusType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="new"/&gt;
 *     &lt;enumeration value="resolved"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DataQualityStatusType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:07+03:00", comments = "JAXB RI v2.2.11")
public enum DataQualityStatusType {

    @XmlEnumValue("new")
    NEW("new"),
    @XmlEnumValue("resolved")
    RESOLVED("resolved");
    private final String value;

    DataQualityStatusType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DataQualityStatusType fromValue(String v) {
        for (DataQualityStatusType c: DataQualityStatusType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
