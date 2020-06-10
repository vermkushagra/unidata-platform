
package com.unidata.mdm.api.v3;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StatisticEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="StatisticEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="NEW"/&gt;
 *     &lt;enumeration value="ERRORS"/&gt;
 *     &lt;enumeration value="UPDATED"/&gt;
 *     &lt;enumeration value="TOTAL"/&gt;
 *     &lt;enumeration value="MERGED"/&gt;
 *     &lt;enumeration value="DUPLICATES"/&gt;
 *     &lt;enumeration value="CLUSTERS"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "StatisticEnum")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:00+03:00", comments = "JAXB RI v2.2.11")
public enum StatisticEnum {

    NEW,
    ERRORS,
    UPDATED,
    TOTAL,
    MERGED,
    DUPLICATES,
    CLUSTERS;

    public String value() {
        return name();
    }

    public static StatisticEnum fromValue(String v) {
        return valueOf(v);
    }

}
