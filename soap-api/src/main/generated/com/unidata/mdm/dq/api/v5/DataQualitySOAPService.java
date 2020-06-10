package com.unidata.mdm.dq.api.v5;

import java.net.URL;
import javax.annotation.Generated;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 3.1.0
 * 2020-06-05T12:30:16.710+03:00
 * Generated source version: 3.1.0
 * 
 */
@WebServiceClient(name = "DataQualitySOAPService", 
                  wsdlLocation = "classpath:api/v5/dq-unidata-api.wsdl",
                  targetNamespace = "http://api.dq.mdm.unidata.com/v5/") 
@Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2020-06-05T12:30:16.710+03:00", comments = "Apache CXF 3.1.0")
public class DataQualitySOAPService extends Service {

    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2020-06-05T12:30:16.710+03:00")
    public final static URL WSDL_LOCATION;

    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2020-06-05T12:30:16.710+03:00")
    public final static QName SERVICE = new QName("http://api.dq.mdm.unidata.com/v5/", "DataQualitySOAPService");
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2020-06-05T12:30:16.710+03:00")
    public final static QName ApplyDQ = new QName("http://api.dq.mdm.unidata.com/v5/", "applyDQ");
    static {
        URL url = DataQualitySOAPService.class.getClassLoader().getResource("api/v5/dq-unidata-api.wsdl");
        if (url == null) {
            java.util.logging.Logger.getLogger(DataQualitySOAPService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "classpath:api/v5/dq-unidata-api.wsdl");
        }       
        WSDL_LOCATION = url;   
    }

    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2020-06-05T12:30:16.710+03:00")
    public DataQualitySOAPService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2020-06-05T12:30:16.710+03:00")
    public DataQualitySOAPService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2020-06-05T12:30:16.710+03:00")
    public DataQualitySOAPService() {
        super(WSDL_LOCATION, SERVICE);
    }
    




    /**
     *
     * @return
     *     returns DataQualityPortType
     */
    @WebEndpoint(name = "applyDQ")
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2020-06-05T12:30:16.710+03:00")
    public DataQualityPortType getApplyDQ() {
        return super.getPort(ApplyDQ, DataQualityPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns DataQualityPortType
     */
    @WebEndpoint(name = "applyDQ")
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2020-06-05T12:30:16.710+03:00")
    public DataQualityPortType getApplyDQ(WebServiceFeature... features) {
        return super.getPort(ApplyDQ, DataQualityPortType.class, features);
    }

}
