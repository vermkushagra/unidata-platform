
package com.unidata.mdm.api.v5;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.unidata.mdm.api.v5 package. 
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

    private final static QName _ApiRequestBody_QNAME = new QName("http://api.mdm.unidata.com/v5/", "apiRequestBody");
    private final static QName _ApiResponseBody_QNAME = new QName("http://api.mdm.unidata.com/v5/", "apiResponseBody");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.unidata.mdm.api.v5
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SessionTokenDef }
     * 
     */
    public SessionTokenDef createSessionTokenDef() {
        return new SessionTokenDef();
    }

    /**
     * Create an instance of {@link CredentialsDef }
     * 
     */
    public CredentialsDef createCredentialsDef() {
        return new CredentialsDef();
    }

    /**
     * Create an instance of {@link UnidataRequestBody }
     * 
     */
    public UnidataRequestBody createUnidataRequestBody() {
        return new UnidataRequestBody();
    }

    /**
     * Create an instance of {@link UnidataResponseBody }
     * 
     */
    public UnidataResponseBody createUnidataResponseBody() {
        return new UnidataResponseBody();
    }

    /**
     * Create an instance of {@link Samples }
     * 
     */
    public Samples createSamples() {
        return new Samples();
    }

    /**
     * Create an instance of {@link SampleDef }
     * 
     */
    public SampleDef createSampleDef() {
        return new SampleDef();
    }

    /**
     * Create an instance of {@link UnidataMessageDef }
     * 
     */
    public UnidataMessageDef createUnidataMessageDef() {
        return new UnidataMessageDef();
    }

    /**
     * Create an instance of {@link UpsertEventDetailsDef }
     * 
     */
    public UpsertEventDetailsDef createUpsertEventDetailsDef() {
        return new UpsertEventDetailsDef();
    }

    /**
     * Create an instance of {@link UpsertEventClassifierDetailsDef }
     * 
     */
    public UpsertEventClassifierDetailsDef createUpsertEventClassifierDetailsDef() {
        return new UpsertEventClassifierDetailsDef();
    }

    /**
     * Create an instance of {@link UpsertEventRelationDetailsDef }
     * 
     */
    public UpsertEventRelationDetailsDef createUpsertEventRelationDetailsDef() {
        return new UpsertEventRelationDetailsDef();
    }

    /**
     * Create an instance of {@link SoftDeleteEventDetailsDef }
     * 
     */
    public SoftDeleteEventDetailsDef createSoftDeleteEventDetailsDef() {
        return new SoftDeleteEventDetailsDef();
    }

    /**
     * Create an instance of {@link WipeDeleteEventDetailsDef }
     * 
     */
    public WipeDeleteEventDetailsDef createWipeDeleteEventDetailsDef() {
        return new WipeDeleteEventDetailsDef();
    }

    /**
     * Create an instance of {@link SoftDeleteClassifierEventDetailsDef }
     * 
     */
    public SoftDeleteClassifierEventDetailsDef createSoftDeleteClassifierEventDetailsDef() {
        return new SoftDeleteClassifierEventDetailsDef();
    }

    /**
     * Create an instance of {@link SoftDeleteRelationEventDetailsDef }
     * 
     */
    public SoftDeleteRelationEventDetailsDef createSoftDeleteRelationEventDetailsDef() {
        return new SoftDeleteRelationEventDetailsDef();
    }

    /**
     * Create an instance of {@link RestoreEventDetailsDef }
     * 
     */
    public RestoreEventDetailsDef createRestoreEventDetailsDef() {
        return new RestoreEventDetailsDef();
    }

    /**
     * Create an instance of {@link MergeEventDetailsDef }
     * 
     */
    public MergeEventDetailsDef createMergeEventDetailsDef() {
        return new MergeEventDetailsDef();
    }

    /**
     * Create an instance of {@link TimeIntervalDef }
     * 
     */
    public TimeIntervalDef createTimeIntervalDef() {
        return new TimeIntervalDef();
    }

    /**
     * Create an instance of {@link AsyncSectionDef }
     * 
     */
    public AsyncSectionDef createAsyncSectionDef() {
        return new AsyncSectionDef();
    }

    /**
     * Create an instance of {@link SecuritySectionDef }
     * 
     */
    public SecuritySectionDef createSecuritySectionDef() {
        return new SecuritySectionDef();
    }

    /**
     * Create an instance of {@link CommonSectionDef }
     * 
     */
    public CommonSectionDef createCommonSectionDef() {
        return new CommonSectionDef();
    }

    /**
     * Create an instance of {@link CommonResponseDef }
     * 
     */
    public CommonResponseDef createCommonResponseDef() {
        return new CommonResponseDef();
    }

    /**
     * Create an instance of {@link ExecutionErrorDef }
     * 
     */
    public ExecutionErrorDef createExecutionErrorDef() {
        return new ExecutionErrorDef();
    }

    /**
     * Create an instance of {@link ExecutionMessageDef }
     * 
     */
    public ExecutionMessageDef createExecutionMessageDef() {
        return new ExecutionMessageDef();
    }

    /**
     * Create an instance of {@link RoleRefDef }
     * 
     */
    public RoleRefDef createRoleRefDef() {
        return new RoleRefDef();
    }

    /**
     * Create an instance of {@link SearchBaseDef }
     * 
     */
    public SearchBaseDef createSearchBaseDef() {
        return new SearchBaseDef();
    }

    /**
     * Create an instance of {@link SearchAndDef }
     * 
     */
    public SearchAndDef createSearchAndDef() {
        return new SearchAndDef();
    }

    /**
     * Create an instance of {@link SearchOrDef }
     * 
     */
    public SearchOrDef createSearchOrDef() {
        return new SearchOrDef();
    }

    /**
     * Create an instance of {@link SearchAtomDef }
     * 
     */
    public SearchAtomDef createSearchAtomDef() {
        return new SearchAtomDef();
    }

    /**
     * Create an instance of {@link SearchConditionDef }
     * 
     */
    public SearchConditionDef createSearchConditionDef() {
        return new SearchConditionDef();
    }

    /**
     * Create an instance of {@link UpsertRelationOverrideDef }
     * 
     */
    public UpsertRelationOverrideDef createUpsertRelationOverrideDef() {
        return new UpsertRelationOverrideDef();
    }

    /**
     * Create an instance of {@link UpsertRelationsDef }
     * 
     */
    public UpsertRelationsDef createUpsertRelationsDef() {
        return new UpsertRelationsDef();
    }

    /**
     * Create an instance of {@link UpsertRelationRecordDef }
     * 
     */
    public UpsertRelationRecordDef createUpsertRelationRecordDef() {
        return new UpsertRelationRecordDef();
    }

    /**
     * Create an instance of {@link ReferenceAliasKey }
     * 
     */
    public ReferenceAliasKey createReferenceAliasKey() {
        return new ReferenceAliasKey();
    }

    /**
     * Create an instance of {@link UpsertRelationDef }
     * 
     */
    public UpsertRelationDef createUpsertRelationDef() {
        return new UpsertRelationDef();
    }

    /**
     * Create an instance of {@link DeleteRelationsDef }
     * 
     */
    public DeleteRelationsDef createDeleteRelationsDef() {
        return new DeleteRelationsDef();
    }

    /**
     * Create an instance of {@link DeleteRelationDef }
     * 
     */
    public DeleteRelationDef createDeleteRelationDef() {
        return new DeleteRelationDef();
    }

    /**
     * Create an instance of {@link DeleteRelationRecordDef }
     * 
     */
    public DeleteRelationRecordDef createDeleteRelationRecordDef() {
        return new DeleteRelationRecordDef();
    }

    /**
     * Create an instance of {@link LookupEntityRefDef }
     * 
     */
    public LookupEntityRefDef createLookupEntityRefDef() {
        return new LookupEntityRefDef();
    }

    /**
     * Create an instance of {@link UnidataAbstractRequest }
     * 
     */
    public UnidataAbstractRequest createUnidataAbstractRequest() {
        return new UnidataAbstractRequest();
    }

    /**
     * Create an instance of {@link UnidataAbstractResponse }
     * 
     */
    public UnidataAbstractResponse createUnidataAbstractResponse() {
        return new UnidataAbstractResponse();
    }

    /**
     * Create an instance of {@link RequestAuthenticate }
     * 
     */
    public RequestAuthenticate createRequestAuthenticate() {
        return new RequestAuthenticate();
    }

    /**
     * Create an instance of {@link ResponseAuthenticate }
     * 
     */
    public ResponseAuthenticate createResponseAuthenticate() {
        return new ResponseAuthenticate();
    }

    /**
     * Create an instance of {@link RequestGetLookupValues }
     * 
     */
    public RequestGetLookupValues createRequestGetLookupValues() {
        return new RequestGetLookupValues();
    }

    /**
     * Create an instance of {@link ResponseGetLookupValues }
     * 
     */
    public ResponseGetLookupValues createResponseGetLookupValues() {
        return new ResponseGetLookupValues();
    }

    /**
     * Create an instance of {@link RequestCleanse }
     * 
     */
    public RequestCleanse createRequestCleanse() {
        return new RequestCleanse();
    }

    /**
     * Create an instance of {@link ResponseCleanse }
     * 
     */
    public ResponseCleanse createResponseCleanse() {
        return new ResponseCleanse();
    }

    /**
     * Create an instance of {@link RequestUpsert }
     * 
     */
    public RequestUpsert createRequestUpsert() {
        return new RequestUpsert();
    }

    /**
     * Create an instance of {@link ResponseUpsert }
     * 
     */
    public ResponseUpsert createResponseUpsert() {
        return new ResponseUpsert();
    }

    /**
     * Create an instance of {@link RequestBulkUpsert }
     * 
     */
    public RequestBulkUpsert createRequestBulkUpsert() {
        return new RequestBulkUpsert();
    }

    /**
     * Create an instance of {@link ResponseBulkUpsert }
     * 
     */
    public ResponseBulkUpsert createResponseBulkUpsert() {
        return new ResponseBulkUpsert();
    }

    /**
     * Create an instance of {@link RequestRelationsUpsert }
     * 
     */
    public RequestRelationsUpsert createRequestRelationsUpsert() {
        return new RequestRelationsUpsert();
    }

    /**
     * Create an instance of {@link ResponseRelationsUpsert }
     * 
     */
    public ResponseRelationsUpsert createResponseRelationsUpsert() {
        return new ResponseRelationsUpsert();
    }

    /**
     * Create an instance of {@link RequestUpsertList }
     * 
     */
    public RequestUpsertList createRequestUpsertList() {
        return new RequestUpsertList();
    }

    /**
     * Create an instance of {@link ResponseUpsertList }
     * 
     */
    public ResponseUpsertList createResponseUpsertList() {
        return new ResponseUpsertList();
    }

    /**
     * Create an instance of {@link RequestMerge }
     * 
     */
    public RequestMerge createRequestMerge() {
        return new RequestMerge();
    }

    /**
     * Create an instance of {@link ResponseMerge }
     * 
     */
    public ResponseMerge createResponseMerge() {
        return new ResponseMerge();
    }

    /**
     * Create an instance of {@link RequestJoin }
     * 
     */
    public RequestJoin createRequestJoin() {
        return new RequestJoin();
    }

    /**
     * Create an instance of {@link ResponseJoin }
     * 
     */
    public ResponseJoin createResponseJoin() {
        return new ResponseJoin();
    }

    /**
     * Create an instance of {@link RequestSplit }
     * 
     */
    public RequestSplit createRequestSplit() {
        return new RequestSplit();
    }

    /**
     * Create an instance of {@link ResponseSplit }
     * 
     */
    public ResponseSplit createResponseSplit() {
        return new ResponseSplit();
    }

    /**
     * Create an instance of {@link RequestSoftDelete }
     * 
     */
    public RequestSoftDelete createRequestSoftDelete() {
        return new RequestSoftDelete();
    }

    /**
     * Create an instance of {@link ResponseSoftDelete }
     * 
     */
    public ResponseSoftDelete createResponseSoftDelete() {
        return new ResponseSoftDelete();
    }

    /**
     * Create an instance of {@link RequestRelationsSoftDelete }
     * 
     */
    public RequestRelationsSoftDelete createRequestRelationsSoftDelete() {
        return new RequestRelationsSoftDelete();
    }

    /**
     * Create an instance of {@link ResponseRelationsSoftDelete }
     * 
     */
    public ResponseRelationsSoftDelete createResponseRelationsSoftDelete() {
        return new ResponseRelationsSoftDelete();
    }

    /**
     * Create an instance of {@link RequestGet }
     * 
     */
    public RequestGet createRequestGet() {
        return new RequestGet();
    }

    /**
     * Create an instance of {@link ResponseGet }
     * 
     */
    public ResponseGet createResponseGet() {
        return new ResponseGet();
    }

    /**
     * Create an instance of {@link RequestGetAllPeriods }
     * 
     */
    public RequestGetAllPeriods createRequestGetAllPeriods() {
        return new RequestGetAllPeriods();
    }

    /**
     * Create an instance of {@link ResponseGetAllPeriods }
     * 
     */
    public ResponseGetAllPeriods createResponseGetAllPeriods() {
        return new ResponseGetAllPeriods();
    }

    /**
     * Create an instance of {@link RequestRelationsGet }
     * 
     */
    public RequestRelationsGet createRequestRelationsGet() {
        return new RequestRelationsGet();
    }

    /**
     * Create an instance of {@link ResponseRelationsGet }
     * 
     */
    public ResponseRelationsGet createResponseRelationsGet() {
        return new ResponseRelationsGet();
    }

    /**
     * Create an instance of {@link RequestInfoGet }
     * 
     */
    public RequestInfoGet createRequestInfoGet() {
        return new RequestInfoGet();
    }

    /**
     * Create an instance of {@link ResponseInfoGet }
     * 
     */
    public ResponseInfoGet createResponseInfoGet() {
        return new ResponseInfoGet();
    }

    /**
     * Create an instance of {@link RequestDataProfile }
     * 
     */
    public RequestDataProfile createRequestDataProfile() {
        return new RequestDataProfile();
    }

    /**
     * Create an instance of {@link ResponseDataProfile }
     * 
     */
    public ResponseDataProfile createResponseDataProfile() {
        return new ResponseDataProfile();
    }

    /**
     * Create an instance of {@link EntityDataProfile }
     * 
     */
    public EntityDataProfile createEntityDataProfile() {
        return new EntityDataProfile();
    }

    /**
     * Create an instance of {@link AttributeDataProfile }
     * 
     */
    public AttributeDataProfile createAttributeDataProfile() {
        return new AttributeDataProfile();
    }

    /**
     * Create an instance of {@link AttributeTopValues }
     * 
     */
    public AttributeTopValues createAttributeTopValues() {
        return new AttributeTopValues();
    }

    /**
     * Create an instance of {@link RequestSearch }
     * 
     */
    public RequestSearch createRequestSearch() {
        return new RequestSearch();
    }

    /**
     * Create an instance of {@link ResponseSearch }
     * 
     */
    public ResponseSearch createResponseSearch() {
        return new ResponseSearch();
    }

    /**
     * Create an instance of {@link RequestCheckDuplicates }
     * 
     */
    public RequestCheckDuplicates createRequestCheckDuplicates() {
        return new RequestCheckDuplicates();
    }

    /**
     * Create an instance of {@link ResponseCheckDuplicates }
     * 
     */
    public ResponseCheckDuplicates createResponseCheckDuplicates() {
        return new ResponseCheckDuplicates();
    }

    /**
     * Create an instance of {@link RequestGetDataQualityErrors }
     * 
     */
    public RequestGetDataQualityErrors createRequestGetDataQualityErrors() {
        return new RequestGetDataQualityErrors();
    }

    /**
     * Create an instance of {@link ResponseGetDataQualityErrors }
     * 
     */
    public ResponseGetDataQualityErrors createResponseGetDataQualityErrors() {
        return new ResponseGetDataQualityErrors();
    }

    /**
     * Create an instance of {@link AliasCodeAttributePointerDef }
     * 
     */
    public AliasCodeAttributePointerDef createAliasCodeAttributePointerDef() {
        return new AliasCodeAttributePointerDef();
    }

    /**
     * Create an instance of {@link ClassifierPointerDef }
     * 
     */
    public ClassifierPointerDef createClassifierPointerDef() {
        return new ClassifierPointerDef();
    }

    /**
     * Create an instance of {@link Statistic }
     * 
     */
    public Statistic createStatistic() {
        return new Statistic();
    }

    /**
     * Create an instance of {@link TimeSerie }
     * 
     */
    public TimeSerie createTimeSerie() {
        return new TimeSerie();
    }

    /**
     * Create an instance of {@link RequestFindAllJobs }
     * 
     */
    public RequestFindAllJobs createRequestFindAllJobs() {
        return new RequestFindAllJobs();
    }

    /**
     * Create an instance of {@link RequestSaveJob }
     * 
     */
    public RequestSaveJob createRequestSaveJob() {
        return new RequestSaveJob();
    }

    /**
     * Create an instance of {@link RequestRemoveJob }
     * 
     */
    public RequestRemoveJob createRequestRemoveJob() {
        return new RequestRemoveJob();
    }

    /**
     * Create an instance of {@link RequestFindJob }
     * 
     */
    public RequestFindJob createRequestFindJob() {
        return new RequestFindJob();
    }

    /**
     * Create an instance of {@link RequestRunJob }
     * 
     */
    public RequestRunJob createRequestRunJob() {
        return new RequestRunJob();
    }

    /**
     * Create an instance of {@link RequestJobStatus }
     * 
     */
    public RequestJobStatus createRequestJobStatus() {
        return new RequestJobStatus();
    }

    /**
     * Create an instance of {@link RequestStopJob }
     * 
     */
    public RequestStopJob createRequestStopJob() {
        return new RequestStopJob();
    }

    /**
     * Create an instance of {@link Job }
     * 
     */
    public Job createJob() {
        return new Job();
    }

    /**
     * Create an instance of {@link JobParameter }
     * 
     */
    public JobParameter createJobParameter() {
        return new JobParameter();
    }

    /**
     * Create an instance of {@link JobParameterValue }
     * 
     */
    public JobParameterValue createJobParameterValue() {
        return new JobParameterValue();
    }

    /**
     * Create an instance of {@link ResponseFindAllJobs }
     * 
     */
    public ResponseFindAllJobs createResponseFindAllJobs() {
        return new ResponseFindAllJobs();
    }

    /**
     * Create an instance of {@link ResponseSaveJob }
     * 
     */
    public ResponseSaveJob createResponseSaveJob() {
        return new ResponseSaveJob();
    }

    /**
     * Create an instance of {@link ResponseRemoveJob }
     * 
     */
    public ResponseRemoveJob createResponseRemoveJob() {
        return new ResponseRemoveJob();
    }

    /**
     * Create an instance of {@link ResponseFindJob }
     * 
     */
    public ResponseFindJob createResponseFindJob() {
        return new ResponseFindJob();
    }

    /**
     * Create an instance of {@link ResponseRunJob }
     * 
     */
    public ResponseRunJob createResponseRunJob() {
        return new ResponseRunJob();
    }

    /**
     * Create an instance of {@link ResponseJobStatus }
     * 
     */
    public ResponseJobStatus createResponseJobStatus() {
        return new ResponseJobStatus();
    }

    /**
     * Create an instance of {@link ResponseStopJob }
     * 
     */
    public ResponseStopJob createResponseStopJob() {
        return new ResponseStopJob();
    }

    /**
     * Create an instance of {@link JobExecution }
     * 
     */
    public JobExecution createJobExecution() {
        return new JobExecution();
    }

    /**
     * Create an instance of {@link JobExecutionExitStatus }
     * 
     */
    public JobExecutionExitStatus createJobExecutionExitStatus() {
        return new JobExecutionExitStatus();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnidataRequestBody }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.mdm.unidata.com/v5/", name = "apiRequestBody")
    public JAXBElement<UnidataRequestBody> createApiRequestBody(UnidataRequestBody value) {
        return new JAXBElement<UnidataRequestBody>(_ApiRequestBody_QNAME, UnidataRequestBody.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnidataResponseBody }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.mdm.unidata.com/v5/", name = "apiResponseBody")
    public JAXBElement<UnidataResponseBody> createApiResponseBody(UnidataResponseBody value) {
        return new JAXBElement<UnidataResponseBody>(_ApiResponseBody_QNAME, UnidataResponseBody.class, null, value);
    }

}
