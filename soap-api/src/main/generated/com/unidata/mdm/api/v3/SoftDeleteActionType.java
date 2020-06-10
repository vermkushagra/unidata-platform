
package com.unidata.mdm.api.v3;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SoftDeleteActionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SoftDeleteActionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="SoftDeleteEtalon"/&gt;
 *     &lt;enumeration value="SoftDeleteOrigin"/&gt;
 *     &lt;enumeration value="SoftDeleteEtalonPeriod"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "SoftDeleteActionType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
public enum SoftDeleteActionType {

    @XmlEnumValue("SoftDeleteEtalon")
    SOFT_DELETE_ETALON("SoftDeleteEtalon"),
    @XmlEnumValue("SoftDeleteOrigin")
    SOFT_DELETE_ORIGIN("SoftDeleteOrigin"),
    @XmlEnumValue("SoftDeleteEtalonPeriod")
    SOFT_DELETE_ETALON_PERIOD("SoftDeleteEtalonPeriod");
    private final String value;

    SoftDeleteActionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SoftDeleteActionType fromValue(String v) {
        for (SoftDeleteActionType c: SoftDeleteActionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
