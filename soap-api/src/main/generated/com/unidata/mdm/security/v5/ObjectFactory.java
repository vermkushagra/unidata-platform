
package com.unidata.mdm.security.v5;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import com.unidata.mdm.api.v5.CredentialsDef;
import com.unidata.mdm.api.v5.SessionTokenDef;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.unidata.mdm.security.v5 package. 
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

    private final static QName _SecurityHeader_QNAME = new QName("http://security.mdm.unidata.com/v5/", "securityHeader");
    private final static QName _LoginRequest_QNAME = new QName("http://security.mdm.unidata.com/v5/", "loginRequest");
    private final static QName _LoginResponse_QNAME = new QName("http://security.mdm.unidata.com/v5/", "loginResponse");
    private final static QName _LogoutRequest_QNAME = new QName("http://security.mdm.unidata.com/v5/", "logoutRequest");
    private final static QName _LogoutResponse_QNAME = new QName("http://security.mdm.unidata.com/v5/", "logoutResponse");
    private final static QName _Info_QNAME = new QName("http://security.mdm.unidata.com/v5/", "info");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.unidata.mdm.security.v5
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link InfoType }
     * 
     */
    public InfoType createInfoType() {
        return new InfoType();
    }

    /**
     * Create an instance of {@link LoginRequestWrapper }
     * 
     */
    public LoginRequestWrapper createLoginRequestWrapper() {
        return new LoginRequestWrapper();
    }

    /**
     * Create an instance of {@link LoginResponseWrapper }
     * 
     */
    public LoginResponseWrapper createLoginResponseWrapper() {
        return new LoginResponseWrapper();
    }

    /**
     * Create an instance of {@link LogoutRequestWrapper }
     * 
     */
    public LogoutRequestWrapper createLogoutRequestWrapper() {
        return new LogoutRequestWrapper();
    }

    /**
     * Create an instance of {@link LogoutResponseWrapper }
     * 
     */
    public LogoutResponseWrapper createLogoutResponseWrapper() {
        return new LogoutResponseWrapper();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SessionTokenDef }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://security.mdm.unidata.com/v5/", name = "securityHeader")
    public JAXBElement<SessionTokenDef> createSecurityHeader(SessionTokenDef value) {
        return new JAXBElement<SessionTokenDef>(_SecurityHeader_QNAME, SessionTokenDef.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CredentialsDef }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://security.mdm.unidata.com/v5/", name = "loginRequest")
    public JAXBElement<CredentialsDef> createLoginRequest(CredentialsDef value) {
        return new JAXBElement<CredentialsDef>(_LoginRequest_QNAME, CredentialsDef.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SessionTokenDef }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://security.mdm.unidata.com/v5/", name = "loginResponse")
    public JAXBElement<SessionTokenDef> createLoginResponse(SessionTokenDef value) {
        return new JAXBElement<SessionTokenDef>(_LoginResponse_QNAME, SessionTokenDef.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SessionTokenDef }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://security.mdm.unidata.com/v5/", name = "logoutRequest")
    public JAXBElement<SessionTokenDef> createLogoutRequest(SessionTokenDef value) {
        return new JAXBElement<SessionTokenDef>(_LogoutRequest_QNAME, SessionTokenDef.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SessionTokenDef }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://security.mdm.unidata.com/v5/", name = "logoutResponse")
    public JAXBElement<SessionTokenDef> createLogoutResponse(SessionTokenDef value) {
        return new JAXBElement<SessionTokenDef>(_LogoutResponse_QNAME, SessionTokenDef.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://security.mdm.unidata.com/v5/", name = "info")
    public JAXBElement<InfoType> createInfo(InfoType value) {
        return new JAXBElement<InfoType>(_Info_QNAME, InfoType.class, null, value);
    }

}
