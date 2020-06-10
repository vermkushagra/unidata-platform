package com.unidata.mdm.login.api.v5;

import javax.annotation.Generated;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 3.1.0
 * 2020-06-05T12:30:20.251+03:00
 * Generated source version: 3.1.0
 * 
 */
@WebService(targetNamespace = "http://api.login.mdm.unidata.com/v5/", name = "LoginPortType")
@XmlSeeAlso({com.unidata.mdm.error_handling.v5.ObjectFactory.class, com.unidata.mdm.security.v5.ObjectFactory.class, com.unidata.mdm.api.v5.ObjectFactory.class, com.unidata.mdm.data.v5.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2020-06-05T12:30:20.251+03:00", comments = "Apache CXF 3.1.0")
public interface LoginPortType {

    @WebMethod(action = "login")
    @WebResult(name = "loginResponse", targetNamespace = "http://security.mdm.unidata.com/v5/", partName = "loginResponse")
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2020-06-05T12:30:20.251+03:00")
    public com.unidata.mdm.api.v5.SessionTokenDef login(
        @WebParam(partName = "loginRequest", name = "loginRequest", targetNamespace = "http://security.mdm.unidata.com/v5/")
        com.unidata.mdm.api.v5.CredentialsDef loginRequest,
        @WebParam(partName = "info", mode = WebParam.Mode.INOUT, name = "info", targetNamespace = "http://security.mdm.unidata.com/v5/", header = true)
        javax.xml.ws.Holder<com.unidata.mdm.security.v5.InfoType> info
    ) throws ApiFault;

    @WebMethod(action = "logout")
    @WebResult(name = "logoutResponse", targetNamespace = "http://security.mdm.unidata.com/v5/", partName = "logoutResponse")
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2020-06-05T12:30:20.251+03:00")
    public com.unidata.mdm.api.v5.SessionTokenDef logout(
        @WebParam(partName = "logoutRequest", name = "logoutRequest", targetNamespace = "http://security.mdm.unidata.com/v5/")
        com.unidata.mdm.api.v5.SessionTokenDef logoutRequest,
        @WebParam(partName = "info", mode = WebParam.Mode.INOUT, name = "info", targetNamespace = "http://security.mdm.unidata.com/v5/", header = true)
        javax.xml.ws.Holder<com.unidata.mdm.security.v5.InfoType> info
    ) throws ApiFault;
}
