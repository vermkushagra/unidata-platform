
package com.unidata.mdm.login.api.v5;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.annotation.Generated;
import javax.xml.namespace.QName;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 3.1.0
 * 2020-06-05T12:30:20.207+03:00
 * Generated source version: 3.1.0
 * 
 */
@Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2020-06-05T12:30:20.207+03:00", comments = "Apache CXF 3.1.0")
public final class LoginPortType_LoginPort_Client {

    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2020-06-05T12:30:20.207+03:00")
    private static final QName SERVICE_NAME = new QName("http://api.login.mdm.unidata.com/v5/", "LoginSOAPService");

    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2020-06-05T12:30:20.207+03:00")
    private LoginPortType_LoginPort_Client() {
    }

    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2020-06-05T12:30:20.207+03:00")
    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = LoginSOAPService.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        LoginSOAPService ss = new LoginSOAPService(wsdlURL, SERVICE_NAME);
        LoginPortType port = ss.getLoginPort();  
        
        {
        System.out.println("Invoking login...");
        com.unidata.mdm.api.v5.CredentialsDef _login_loginRequest = null;
        com.unidata.mdm.security.v5.InfoType _login_infoVal = null;
        javax.xml.ws.Holder<com.unidata.mdm.security.v5.InfoType> _login_info = new javax.xml.ws.Holder<com.unidata.mdm.security.v5.InfoType>(_login_infoVal);
        try {
            com.unidata.mdm.api.v5.SessionTokenDef _login__return = port.login(_login_loginRequest, _login_info);
            System.out.println("login.result=" + _login__return);

            System.out.println("login._login_info=" + _login_info.value);
        } catch (ApiFault e) { 
            System.out.println("Expected exception: ApiFault has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking logout...");
        com.unidata.mdm.api.v5.SessionTokenDef _logout_logoutRequest = null;
        com.unidata.mdm.security.v5.InfoType _logout_infoVal = null;
        javax.xml.ws.Holder<com.unidata.mdm.security.v5.InfoType> _logout_info = new javax.xml.ws.Holder<com.unidata.mdm.security.v5.InfoType>(_logout_infoVal);
        try {
            com.unidata.mdm.api.v5.SessionTokenDef _logout__return = port.logout(_logout_logoutRequest, _logout_info);
            System.out.println("logout.result=" + _logout__return);

            System.out.println("logout._logout_info=" + _logout_info.value);
        } catch (ApiFault e) { 
            System.out.println("Expected exception: ApiFault has occurred.");
            System.out.println(e.toString());
        }
            }

        System.exit(0);
    }

}