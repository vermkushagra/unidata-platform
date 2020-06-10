
package com.unidata.mdm.error_handling.v5;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.unidata.mdm.error_handling.v5 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ApiFault_QNAME = new QName("http://error-handling.mdm.unidata.com/v5/", "ApiFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.unidata.mdm.error_handling.v5
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ApiFaultType }
     * 
     */
    public ApiFaultType createApiFaultType() {
        return new ApiFaultType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ApiFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://error-handling.mdm.unidata.com/v5/", name = "ApiFault")
    public JAXBElement<ApiFaultType> createApiFault(ApiFaultType value) {
        return new JAXBElement<ApiFaultType>(_ApiFault_QNAME, ApiFaultType.class, null, value);
    }

}
