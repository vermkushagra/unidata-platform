
package com.unidata.mdm.api.v5;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.xjc.runtime.JAXBToStringStyle;


/**
 * 
 *                 Ответ на запрос остановки операции.
 *             
 * 
 * <p>Java class for ResponseStopJob complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseStopJob"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://api.mdm.unidata.com/v5/}UnidataAbstractResponse"&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseStopJob")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
public class ResponseStopJob
    extends UnidataAbstractResponse
    implements Serializable
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    private final static long serialVersionUID = 12345L;

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2020-06-05T12:30:23+03:00", comments = "JAXB RI v2.2.11")
    public String toString() {
        return ToStringBuilder.reflectionToString(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
