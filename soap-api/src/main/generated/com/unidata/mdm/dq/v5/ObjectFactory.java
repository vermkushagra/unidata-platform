
package com.unidata.mdm.dq.v5;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import com.unidata.mdm.api.v5.CredentialsDef;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.unidata.mdm.dq.v5 package. 
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

    private final static QName _InfoHeader_QNAME = new QName("http://dq.mdm.unidata.com/v5/", "infoHeader");
    private final static QName _LoginRequest_QNAME = new QName("http://dq.mdm.unidata.com/v5/", "loginRequest");
    private final static QName _ApplyDQRequestWrapper_QNAME = new QName("http://dq.mdm.unidata.com/v5/", "applyDQRequestWrapper");
    private final static QName _GetResultsRequest_QNAME = new QName("http://dq.mdm.unidata.com/v5/", "getResultsRequest");
    private final static QName _GetResultsResponse_QNAME = new QName("http://dq.mdm.unidata.com/v5/", "getResultsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.unidata.mdm.dq.v5
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ApplyDQRequest }
     * 
     */
    public ApplyDQRequest createApplyDQRequest() {
        return new ApplyDQRequest();
    }

    /**
     * Create an instance of {@link ApplyDQResponse }
     * 
     */
    public ApplyDQResponse createApplyDQResponse() {
        return new ApplyDQResponse();
    }

    /**
     * Create an instance of {@link InfoType }
     * 
     */
    public InfoType createInfoType() {
        return new InfoType();
    }

    /**
     * Create an instance of {@link DQApplyInfoType }
     * 
     */
    public DQApplyInfoType createDQApplyInfoType() {
        return new DQApplyInfoType();
    }

    /**
     * Create an instance of {@link ApplyDQRequest.Payload }
     * 
     */
    public ApplyDQRequest.Payload createApplyDQRequestPayload() {
        return new ApplyDQRequest.Payload();
    }

    /**
     * Create an instance of {@link ApplyDQRequestWrapper }
     * 
     */
    public ApplyDQRequestWrapper createApplyDQRequestWrapper() {
        return new ApplyDQRequestWrapper();
    }

    /**
     * Create an instance of {@link ApplyDQResponse.Payload }
     * 
     */
    public ApplyDQResponse.Payload createApplyDQResponsePayload() {
        return new ApplyDQResponse.Payload();
    }

    /**
     * Create an instance of {@link ResultsRequestType }
     * 
     */
    public ResultsRequestType createResultsRequestType() {
        return new ResultsRequestType();
    }

    /**
     * Create an instance of {@link ResultsResponseType }
     * 
     */
    public ResultsResponseType createResultsResponseType() {
        return new ResultsResponseType();
    }

    /**
     * Create an instance of {@link DQRecordType }
     * 
     */
    public DQRecordType createDQRecordType() {
        return new DQRecordType();
    }

    /**
     * Create an instance of {@link ApplyDQResponseWrapper }
     * 
     */
    public ApplyDQResponseWrapper createApplyDQResponseWrapper() {
        return new ApplyDQResponseWrapper();
    }

    /**
     * Create an instance of {@link DataQualityResultType }
     * 
     */
    public DataQualityResultType createDataQualityResultType() {
        return new DataQualityResultType();
    }

    /**
     * Create an instance of {@link ClassifierDQErrors }
     * 
     */
    public ClassifierDQErrors createClassifierDQErrors() {
        return new ClassifierDQErrors();
    }

    /**
     * Create an instance of {@link ResultsRequestWrapper }
     * 
     */
    public ResultsRequestWrapper createResultsRequestWrapper() {
        return new ResultsRequestWrapper();
    }

    /**
     * Create an instance of {@link ResultsResponseWrapper }
     * 
     */
    public ResultsResponseWrapper createResultsResponseWrapper() {
        return new ResultsResponseWrapper();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dq.mdm.unidata.com/v5/", name = "infoHeader")
    public JAXBElement<InfoType> createInfoHeader(InfoType value) {
        return new JAXBElement<InfoType>(_InfoHeader_QNAME, InfoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CredentialsDef }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dq.mdm.unidata.com/v5/", name = "loginRequest")
    public JAXBElement<CredentialsDef> createLoginRequest(CredentialsDef value) {
        return new JAXBElement<CredentialsDef>(_LoginRequest_QNAME, CredentialsDef.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ApplyDQRequestWrapper }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dq.mdm.unidata.com/v5/", name = "applyDQRequestWrapper")
    public JAXBElement<ApplyDQRequestWrapper> createApplyDQRequestWrapper(ApplyDQRequestWrapper value) {
        return new JAXBElement<ApplyDQRequestWrapper>(_ApplyDQRequestWrapper_QNAME, ApplyDQRequestWrapper.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResultsRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dq.mdm.unidata.com/v5/", name = "getResultsRequest")
    public JAXBElement<ResultsRequestType> createGetResultsRequest(ResultsRequestType value) {
        return new JAXBElement<ResultsRequestType>(_GetResultsRequest_QNAME, ResultsRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResultsResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dq.mdm.unidata.com/v5/", name = "getResultsResponse")
    public JAXBElement<ResultsResponseType> createGetResultsResponse(ResultsResponseType value) {
        return new JAXBElement<ResultsResponseType>(_GetResultsResponse_QNAME, ResultsResponseType.class, null, value);
    }

}
