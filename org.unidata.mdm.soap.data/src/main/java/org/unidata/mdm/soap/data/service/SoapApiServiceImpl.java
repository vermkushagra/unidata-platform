package org.unidata.mdm.soap.data.service;

import static java.util.Objects.nonNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.unidata.mdm.api.v1.AsyncSectionDef;
import org.unidata.mdm.api.v1.CommonResponseDef;
import org.unidata.mdm.api.v1.CommonSectionDef;
import org.unidata.mdm.api.v1.DeleteRelationDef;
import org.unidata.mdm.api.v1.DeleteRelationRecordDef;
import org.unidata.mdm.api.v1.DeleteRelationsDef;
import org.unidata.mdm.api.v1.ExecutionErrorDef;
import org.unidata.mdm.api.v1.ExecutionMessageDef;
import org.unidata.mdm.api.v1.ExitCodeType;
import org.unidata.mdm.api.v1.ReferenceAliasKey;
import org.unidata.mdm.api.v1.RequestBulkUpsert;
import org.unidata.mdm.api.v1.RequestGet;
import org.unidata.mdm.api.v1.RequestGetAllPeriods;
import org.unidata.mdm.api.v1.RequestGetLookupValues;
import org.unidata.mdm.api.v1.RequestInfoGet;
import org.unidata.mdm.api.v1.RequestMerge;
import org.unidata.mdm.api.v1.RequestRelationsGet;
import org.unidata.mdm.api.v1.RequestRelationsSoftDelete;
import org.unidata.mdm.api.v1.RequestRelationsUpsert;
import org.unidata.mdm.api.v1.RequestSearch;
import org.unidata.mdm.api.v1.RequestSoftDelete;
import org.unidata.mdm.api.v1.RequestSplit;
import org.unidata.mdm.api.v1.RequestUpsert;
import org.unidata.mdm.api.v1.ResponseAuthenticate;
import org.unidata.mdm.api.v1.ResponseBulkUpsert;
import org.unidata.mdm.api.v1.ResponseGet;
import org.unidata.mdm.api.v1.ResponseGetAllPeriods;
import org.unidata.mdm.api.v1.ResponseInfoGet;
import org.unidata.mdm.api.v1.ResponseJoin;
import org.unidata.mdm.api.v1.ResponseMerge;
import org.unidata.mdm.api.v1.ResponseRelationsGet;
import org.unidata.mdm.api.v1.ResponseRelationsSoftDelete;
import org.unidata.mdm.api.v1.ResponseRelationsUpsert;
import org.unidata.mdm.api.v1.ResponseSearch;
import org.unidata.mdm.api.v1.ResponseSoftDelete;
import org.unidata.mdm.api.v1.ResponseSplit;
import org.unidata.mdm.api.v1.ResponseUpsert;
import org.unidata.mdm.api.v1.RoleRefDef;
import org.unidata.mdm.api.v1.SecuritySectionDef;
import org.unidata.mdm.api.v1.SoftDeleteActionType;
import org.unidata.mdm.api.v1.Statistic;
import org.unidata.mdm.api.v1.UnidataRequestBody;
import org.unidata.mdm.api.v1.UnidataResponseBody;
import org.unidata.mdm.api.v1.UpsertActionType;
import org.unidata.mdm.api.v1.UpsertRelationDef;
import org.unidata.mdm.api.v1.UpsertRelationRecordDef;
import org.unidata.mdm.api.wsdl.v1.UnidataServicePortImpl;
import org.unidata.mdm.core.exception.SystemRuntimeException;
import org.unidata.mdm.core.service.SecurityService;
import org.unidata.mdm.core.type.security.AuthenticationSystemParameter;
import org.unidata.mdm.core.type.security.EndpointType;
import org.unidata.mdm.core.type.security.Right;
import org.unidata.mdm.core.type.security.SecurityToken;
import org.unidata.mdm.core.type.security.User;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.DeleteRelationRequestContext;
import org.unidata.mdm.data.context.DeleteRelationsRequestContext;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.context.GetMultipleRequestContext;
import org.unidata.mdm.data.context.GetRecordTimelineRequestContext;
import org.unidata.mdm.data.context.GetRelationsRequestContext;
import org.unidata.mdm.data.context.GetRequestContext;
import org.unidata.mdm.data.context.JoinRequestContext;
import org.unidata.mdm.data.context.MergeRequestContext;
import org.unidata.mdm.data.context.RecordIdentityContext;
import org.unidata.mdm.data.context.SplitRecordRequestContext;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.context.UpsertRelationsRequestContext;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.dto.BulkUpsertResultDTO;
import org.unidata.mdm.data.dto.DeleteRecordDTO;
import org.unidata.mdm.data.dto.DeleteRelationDTO;
import org.unidata.mdm.data.dto.DeleteRelationsDTO;
import org.unidata.mdm.data.dto.GetRecordDTO;
import org.unidata.mdm.data.dto.GetRecordsDTO;
import org.unidata.mdm.data.dto.GetRelationDTO;
import org.unidata.mdm.data.dto.GetRelationsDTO;
import org.unidata.mdm.data.dto.KeysJoinDTO;
import org.unidata.mdm.data.dto.MergeRecordsDTO;
import org.unidata.mdm.data.dto.RelationStateDTO;
import org.unidata.mdm.data.dto.SplitRecordsDTO;
import org.unidata.mdm.data.dto.UpsertRecordDTO;
import org.unidata.mdm.data.dto.UpsertRelationDTO;
import org.unidata.mdm.data.dto.UpsertRelationsDTO;
import org.unidata.mdm.data.service.DataRecordsService;
import org.unidata.mdm.data.service.DataRelationsService;
import org.unidata.mdm.data.v1.ComplexAttribute;
import org.unidata.mdm.data.v1.EntityRelations;
import org.unidata.mdm.data.v1.EtalonKey;
import org.unidata.mdm.data.v1.EtalonRecord;
import org.unidata.mdm.data.v1.IntegralRecord;
import org.unidata.mdm.data.v1.NestedRecord;
import org.unidata.mdm.data.v1.OriginKey;
import org.unidata.mdm.data.v1.OriginRecord;
import org.unidata.mdm.data.v1.RelationBase;
import org.unidata.mdm.data.v1.RelationTo;
import org.unidata.mdm.data.v1.SimpleAttribute;
import org.unidata.mdm.data.v1.ValueDataType;
import org.unidata.mdm.meta.AbstractAttributeDef;
import org.unidata.mdm.meta.AbstractSimpleAttributeDef;
import org.unidata.mdm.meta.RelType;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.type.search.EntityIndexType;
import org.unidata.mdm.search.context.SearchRequestContext;
import org.unidata.mdm.search.dto.SearchResultDTO;
import org.unidata.mdm.search.dto.SearchResultHitDTO;
import org.unidata.mdm.search.service.SearchService;
import org.unidata.mdm.search.type.search.SearchRequestType;
import org.unidata.mdm.security.v1.CredentialsDef;
import org.unidata.mdm.security.v1.SessionTokenDef;
import org.unidata.mdm.soap.core.util.SecurityJaxbUtils;
import org.unidata.mdm.soap.data.configuration.ConfigurationConstants;
import org.unidata.mdm.soap.data.exception.DataSoapExceptionIds;
import org.unidata.mdm.soap.data.exception.ExitException;
import org.unidata.mdm.soap.data.exception.ExitState;
import org.unidata.mdm.soap.data.util.DumpUtils;
import org.unidata.mdm.soap.data.util.JaxbUtils;
import org.unidata.mdm.soap.data.util.SoapOperation;
import org.unidata.mdm.soap.data.util.SoapSearchUtils;
import org.unidata.mdm.system.exception.PlatformBusinessException;
import org.unidata.mdm.system.type.runtime.MeasurementContextName;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;
import org.unidata.mdm.system.util.ClientIpUtil;

/**
 * The Class SoapApiServiceImpl.
 */
public class SoapApiServiceImpl extends UnidataServicePortImpl {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UnidataServicePortImpl.class);

    private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";

    /**
     * Russian locale.
     */
    private static final Locale RU = new Locale("ru");

    /**
     * Api version.
     */
    @Value("${" + ConfigurationConstants.API_VERSION_PROPERTY + "}")
    private String apiVersion;
    /**
     * platform version.
     */
    @Value("${" + ConfigurationConstants.PLATFORM_VERSION_PROPERTY + "}")
    private String platformVersion;
    /**
     * Max attempt upsert count
     */
    @Value("${" + ConfigurationConstants.DATA_SOAP_UPSERT_MAX_ATTEMPT_COUNT + ":1}")
    private Integer maxAttemptCount;
    /**
     * Search service.
     */
    @Autowired
    private SearchService searchService;
    /**
     * Data records service.
     */
    @Autowired
    private DataRecordsService dataRecordsService;
    /**
     * Data relation service facade.
     */
    @Autowired
    private DataRelationsService dataRelationsService;
    /**
     * Model service.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Security service.
     */
    @Autowired
    private SecurityService securityService;
    /**
     * Statistic service.
     */
    // TODO @Modules
//    @Autowired
//    private StatService statService;
    /**
     * Message source.
     */
    @Autowired
    private MessageSource messageSource;

    /**
     * WS context.
     */
    @Resource
    private WebServiceContext jaxwsContext;

    /**
     * {@inheritDoc}
     */
    @Override
    public UnidataResponseBody apiCall(UnidataRequestBody request) {

        LOGGER.debug("Executing operation apiCall.");
        Authentication authentication = null;
        long start = System.currentTimeMillis();
        try {
            //why we didn't push storage id in inner code?
            if (request.getCommon() != null && StringUtils.isBlank(request.getCommon().getOperationId())) {
                request.getCommon().setOperationId(UUID.randomUUID().toString());
            }

            // Global response.
            UnidataResponseBody response = JaxbUtils.getApiObjectFactory().createUnidataResponseBody();

            // 1. Check authenticated.
            if (!isAuthenticated(request)) {
                authentication = handleAuthenticate(request, response);
                if (authentication == null) {
                    return response;
                }
            }
            // 3. Get
            if (request.getRequestGet() != null) {
                handleRequestGet(request, response);
            }
            // 5. Get lookup values
            if (request.getRequestGetLookupValues() != null) {
                handleRequestGetLookupValues(request, response);
            }
            // 6. Merge
            if (request.getRequestMerge() != null) {
                handleRequestMerge(request, response);
            }
            // 9. Search
            if (request.getRequestSearch() != null) {
                handleRequestSearch(request, response);
            }
            // 10. Soft delete
            if (request.getRequestSoftDelete() != null) {
                handleRequestSoftDelete(request, response);
            }
            // 11. Relations soft delete
            if (request.getRequestRelationsSoftDelete() != null) {
                handleRequestRelationsSoftDelete(request, response);
            }
            // 12. Upsert
            if (request.getRequestUpsert() != null) {
                handleRequestUpsert(request, response);
            }
            // 13. Upsert relations
            if (request.getRequestRelationsUpsert() != null) {
                handleRequestRelationsUpsert(request, response);
            }
            // 14
            if (request.getRequestRelationsGet() != null) {
                handleRequestRelationsGet(request, response);
            }
            // 15 Statistics
            if (request.getRequestInfoGet() != null) {
                handleRequestInfoGet(request, response);
            }
            // 16 bulk upsert
            if (nonNull(request.getRequestBulkUpsert())) {
                handleBulkUpsert(request, response);
            }
            // 17. Join
            if (request.getRequestJoin() != null) {
                handleRequestJoin(request, response);
            }

            if (request.getRequestSplit() != null) {
                handleRequestSplit(request, response);
            }


            // 24. Get All periods
            if (request.getRequestGetAllPeriods() != null) {
                handleRequestGetAllPeriods(request, response);
            }

            // Add call statistics
            CommonResponseDef common = null;
            if (response.getCommon() == null) {
                common = JaxbUtils.getApiObjectFactory().createCommonResponseDef();
                response.setCommon(common);
            } else {
                common = response.getCommon();
            }

            common.setProcessingTime((int) (System.currentTimeMillis() - start));
            common.setPlatform(platformVersion);
            common.setVersion(apiVersion);
            if (common.getExitCode() == null) {
                common.setExitCode(ExitCodeType.SUCCESS);
            }
            return response;
        } catch (Exception ex) {
            LOGGER.warn("Exception caught [{}].", ex);
        } finally {
            if (authentication != null && doLogout(request)) {
                handleLogout(request, authentication);
            }

            LOGGER.debug("Finished operation apiCall.");
        }

        return null;
    }

    /**
     * Handle upsert bulk operation.
     *
     * @param bulkRequest the request
     * @param response the response
     */
    private void handleBulkUpsert(UnidataRequestBody bulkRequest, UnidataResponseBody response) {

        if (CollectionUtils.isEmpty(bulkRequest.getRequestBulkUpsert().getUpsertRecordRequests())) {
            return;
        }

        MeasurementPoint.init(MeasurementContextName.MEASURE_SOAP_BULK_UPSERT);
        MeasurementPoint.start();


        List<UpsertRequestContext> upsertRequestContexts = Collections.emptyList();

        try {

            RequestBulkUpsert bulkRequestUpsert = bulkRequest.getRequestBulkUpsert();
            List<RequestUpsert> upsertRecordRequests = bulkRequestUpsert.getUpsertRecordRequests();
            upsertRequestContexts = upsertRecordRequests.stream()
                    .map(request -> convertToUpsertContext(request, bulkRequest.getCommon(), true))
                    .collect(Collectors.toList());

            BulkUpsertResultDTO result = dataRecordsService.bulkUpsertRecords(upsertRequestContexts, true);
            ResponseBulkUpsert upsert = JaxbUtils.getApiObjectFactory().createResponseBulkUpsert();

            Collection<ResponseUpsert> innerResponses = result.getRecords().stream()
                    .map(innerResult -> JaxbUtils.getApiObjectFactory().createResponseUpsert()
                            .withEtalonKey(DumpUtils.to(innerResult.getRecordKeys().getEtalonKey()))
                            .withOriginKey(DumpUtils.to(innerResult.getRecordKeys().getOriginKey()))
                            .withOriginAction(innerResult.getAction() == null
                                    ? UpsertActionType.NO_ACTION
                                    : UpsertActionType.valueOf(innerResult.getAction().name())))
                    .collect(Collectors.toList());

            upsert.withUpsertRecordResponses(innerResponses);
            response.setResponseBulkUpsert(upsert);
            createSuccess(response, SoapOperation.REQUEST_BULK_UPSERT,
                    bulkRequest.getCommon().getOperationId());
        } catch (ExitException eexc) {
            if (eexc.getExitState() == ExitState.ES_UPSERT_DENIED) {
                response.getResponseUpsert().withOriginAction(UpsertActionType.NO_ACTION);
                createSuccess(response, SoapOperation.REQUEST_BULK_UPSERT,
                        bulkRequest.getCommon().getOperationId());
            } else {
                createError(response, eexc, SoapOperation.REQUEST_BULK_UPSERT,
                        bulkRequest.getCommon().getOperationId());
            }
        } catch (Exception exc) {
            // TODO @Modules
//            Collection<DataQualityError> dqErrors = upsertRequestContexts.stream()
//                    .filter(Objects::nonNull)
//                    .map(UpsertRequestContext::getDqErrors)
//                    .filter(Objects::nonNull)
//                    .flatMap(Collection::stream)
//                    .map(DumpUtils::to)
//                    .collect(Collectors.toList());
            createError(response, exc, SoapOperation.REQUEST_BULK_UPSERT,
                    bulkRequest.getCommon().getOperationId());
        } finally {
            MeasurementPoint.stop();
        }
    }


    /**
     * Handle {@link RequestGet} type request.
     *
     * @param request the request
     * @param response the response
     */
    private void handleRequestGet(UnidataRequestBody request, UnidataResponseBody response) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_SOAP_GET);

        try {
            MeasurementPoint.start();

            GetRequestContext ctx = GetRequestContext.builder()
                    .etalonKey(DumpUtils.from(request.getRequestGet().getEtalonKey()))
                    .originKey(DumpUtils.from(request.getRequestGet().getOriginKey()))
                    .forDate(JaxbUtils.xmlGregorianCalendarToDate(request.getRequestGet().getAsOf()))
                    .fetchSoftDeleted(request.getRequestGet().isSoftDeleted())
                    .fetchOrigins(request.getRequestGet().isOriginsAsOf())
//                    .fetchRelations(true)
//                    .fetchClassifiers(true)
                    .fetchLargeObjects(request.getRequestGet().isFetchLargeData())
                    .build();

            ctx.setOperationId(request.getCommon().getOperationId());
            GetRecordDTO result = dataRecordsService.getRecord(ctx);
            List<OriginRecord> origins = !CollectionUtils.isEmpty(result.getOrigins())
                    ? new ArrayList<>(result.getOrigins().size())
                    : Collections.emptyList();

            for (int i = 0; !CollectionUtils.isEmpty(result.getOrigins()) && i < result.getOrigins().size(); i++) {
                origins.add(DumpUtils.to(result.getOrigins().get(i), result.getOrigins().get(i).getInfoSection(), OriginRecord.class));
            }

            EtalonRecord etalon = convertRecordToEtalon(result);

            ResponseGet get = JaxbUtils.getApiObjectFactory().createResponseGet();
            get.withEtalonRecord(etalon)
                    .withOriginRecords(origins)
                    .withRangeFromMin(JaxbUtils.dateToXMGregorianCalendar(result.getRangeFromMax()))
                    .withRangeToMax(JaxbUtils.dateToXMGregorianCalendar(result.getRangeToMin()));

            response.withResponseGet(get);
            if (Objects.nonNull(result.getEtalon()) || !CollectionUtils.isEmpty(result.getOrigins())) {
                createSuccess(response, SoapOperation.REQUEST_GET,
                        request.getCommon().getOperationId());
            } else {
                createWarning(response, SoapOperation.REQUEST_GET,
                        "No result. Record(s) not found.",
                        request.getCommon().getOperationId());
            }
        } catch (Exception exc) {
            createError(response, exc, SoapOperation.REQUEST_GET, request.getCommon().getOperationId());
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Handle {@link RequestGetAllPeriods} type request.
     *
     * @param request the request
     * @param response the response
     */
    private void handleRequestGetAllPeriods(UnidataRequestBody request, UnidataResponseBody response) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_SOAP_GET_ALL_PERIODS);

        try {
            MeasurementPoint.start();

            GetRequestContext ctx = GetRequestContext.builder()
                    .etalonKey(DumpUtils.from(request.getRequestGetAllPeriods().getEtalonKey()))
                    .originKey(DumpUtils.from(request.getRequestGetAllPeriods().getOriginKey()))
                    .entityName(request.getRequestGetAllPeriods().getEntityName())
                    .build();

            ctx.setOperationId(request.getCommon().getOperationId());

            Timeline<org.unidata.mdm.data.type.data.OriginRecord> timeline = dataRecordsService.loadTimeline(GetRecordTimelineRequestContext.builder()
                    .etalonKey(DumpUtils.from(request.getRequestGetAllPeriods().getEtalonKey()))
                    .originKey(DumpUtils.from(request.getRequestGetAllPeriods().getOriginKey()))
                    .entityName(request.getRequestGetAllPeriods().getEntityName())
                    .build());

            List<EtalonRecord> etalons = new ArrayList<>();

            for (TimeInterval<org.unidata.mdm.data.type.data.OriginRecord> timeInterval : timeline) {

                if (!timeInterval.isActive()) {
                    continue;
                }

                ctx = GetRequestContext.builder()
                        .etalonKey(DumpUtils.from(request.getRequestGetAllPeriods().getEtalonKey()))
                        .originKey(DumpUtils.from(request.getRequestGetAllPeriods().getOriginKey()))
                        .entityName(request.getRequestGetAllPeriods().getEntityName())
                        // Take left border from time interval to get record with certain attributes.
                        .forDate(timeInterval.getValidFrom())
//                            .fetchRelations(true)
//                            .fetchClassifiers(true)
                        .build();

                GetRecordDTO record = dataRecordsService.getRecord(ctx);

                EtalonRecord etalon = convertRecordToEtalon(record);

                if (etalon != null) {
                    etalons.add(etalon);
                }
            }

            ResponseGetAllPeriods get = JaxbUtils.getApiObjectFactory().createResponseGetAllPeriods();
            get.withEtalonRecord(etalons);

            response.withResponseGetAllPeriods(get);
            if (!CollectionUtils.isEmpty(etalons)) {
                createSuccess(response, SoapOperation.REQUEST_GET_ALL_PERIODS,
                        request.getCommon().getOperationId());
            } else {
                createWarning(response, SoapOperation.REQUEST_GET_ALL_PERIODS,
                        "No result. Record(s) not found.",
                        request.getCommon().getOperationId());
            }
        } catch (Exception exc) {
            createError(response, exc, SoapOperation.REQUEST_GET_ALL_PERIODS, request.getCommon().getOperationId());
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Prepare {@link EtalonRecord} object with relations and classifiers.
     *
     * @param record Record DTO.
     * @return Etalon object.
     */
    private EtalonRecord convertRecordToEtalon(GetRecordDTO record) {
        EtalonRecord etalon = DumpUtils.to(record.getEtalon(),
                record.getEtalon() != null ? record.getEtalon().getInfoSection() : null,
                EtalonRecord.class);

        if (etalon != null) {
            EntityRelations relations = JaxbUtils.getDataObjectFactory().createEntityRelations();
            // TODO @Modules
//            for (Iterator<Entry<RelationStateDTO, List<GetRelationDTO>>> it = MapUtils
//                    .isEmpty(record.getRelations()) ? null : record.getRelations().entrySet().iterator();
//                 it != null && it.hasNext(); ) {
//
//                Entry<RelationStateDTO, List<GetRelationDTO>> e = it.next();
//                for (GetRelationDTO dto : e.getValue()) {
//                    switch (dto.getRelationType()) {
//                        case CONTAINS:
//                            relations.getIntegralEntities().add(DumpUtils.to(dto.getEtalon(),
//                                    dto.getEtalon() != null ? dto.getEtalon().getInfoSection() : null,
//                                    IntegralRecord.class));
//                            break;
//                        case REFERENCES:
//                        case MANY_TO_MANY:
//                            relations.getRelationsTo().add(DumpUtils.to(dto.getEtalon(),
//                                    dto.getEtalon() != null ? dto.getEtalon().getInfoSection() : null,
//                                    RelationTo.class));
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            }

            etalon.withRelations(relations);
        }

        return etalon;
    }

    /**
     * Handle {@link RequestInfoGet} type request.
     *
     * @param request the request
     * @param response the response
     */
    private void handleRequestInfoGet(UnidataRequestBody request, UnidataResponseBody response) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_SOAP_GET_INFO);

        try {
            MeasurementPoint.start();

            RequestInfoGet requestInfoGet = request.getRequestInfoGet();
            String entityName = requestInfoGet.getEntityName();
            if (StringUtils.isBlank(entityName)) {
                throw new SystemRuntimeException("'RequestInfoGet' doesn't contain entity name",
                        DataSoapExceptionIds.EX_SOAP_INCORRECT_REQUEST_INFO_GET_EMPTY_ENTITY_NAME);
            }
            XMLGregorianCalendar from = requestInfoGet.getFrom();
            XMLGregorianCalendar to = requestInfoGet.getTo();
            if (Objects.isNull(from) || Objects.isNull(to)) {
                throw new SystemRuntimeException("'RequestInfoGet' doesn't contain from or to date",
                        DataSoapExceptionIds.EX_SOAP_INCORRECT_REQUEST_INFO_GET_EMPTY_FROM_OR_TO, from, to);
            }
            Date fromDate = from.toGregorianCalendar().getTime();
            Date toDate = to.toGregorianCalendar().getTime();
            if (fromDate.after(toDate) || fromDate.equals(toDate)) {
                throw new SystemRuntimeException("'RequestInfoGet' contain 'from' date which after 'to' date",
                        DataSoapExceptionIds.EX_SOAP_INCORRECT_REQUEST_INFO_GET_FROM_AFTER_TO, fromDate, toDate);
            }

            // TODO @Modules
//            StatisticRequestContext statisticRequest
//                    = StatisticRequestContext.builder()
//                    .entityName(entityName)
//                    .granularity(GranularityType.DAY)
//                    .startDate(fromDate)
//                    .endDate(toDate)
//                    .build();
//
//            StatisticResponseDTO result = statService.getStatistic(statisticRequest);
            List<Statistic> statistics = null;//convertStatistic(result);
            ResponseInfoGet responseInfoGet = new ResponseInfoGet();
            responseInfoGet.withStatistic(statistics);
            response.withResponseInfoGet(responseInfoGet);
            createSuccess(response, SoapOperation.REQUEST_INFO_GET,
                    request.getCommon().getOperationId());
        } catch (Exception exc) {
            createError(response, exc, SoapOperation.REQUEST_INFO_GET, request.getCommon().getOperationId());
        } finally {
            MeasurementPoint.stop();
        }

    }

    /**
     * Convert statistic response.
     *
     * @param source source.
     * @return converted object
     */
    // TODO @Modules
//    private List<Statistic> convertStatistic(StatisticResponseDTO source) {
//        if (source == null) {
//            return null;
//        }
//        List<Statistic> target = new ArrayList<>();
//        source.getStats().forEach(st -> target.add(convertStatistic(st)));
//        return target;
//
//    }

    /**
     * Convert statistic object.
     *
     * @param source source.
     * @return converted object.
     */
    // TODO @Modules
//    private Statistic convertStatistic(StatisticDTO source) {
//        if (source == null) {
//            return null;
//        }
//        Statistic target = new Statistic();
//        target.setType(StatisticEnum.fromValue(source.getType().name()));
//        source.getSeries().forEach(ts -> target.getSeries().add(convertTimeSerie(ts)));
//        return target;
//    }

    /**
     * Convert time serie object.
     *
     * @param source source.
     * @return converted object.
     */
    // TODO @Modules
//    private TimeSerie convertTimeSerie(TimeSerieDTO source) {
//        if (source == null) {
//            return null;
//        }
//        TimeSerie target = new TimeSerie();
//        target.setTime(JaxbUtils.dateToXMGregorianCalendar(source.getTime()));
//        target.setValue(source.getValue());
//        return target;
//    }

    // TODO @Modules
//    /**
//     * Handle {@link RequestGetDataQualityErrors} type request.
//     *
//     * @param request the request
//     * @param response the response
//     */
//    private void handleRequestGetDataQualityErrors(UnidataRequestBody request, UnidataResponseBody response) {
//        try {
//            RequestGetDataQualityErrors toProcess = request.getRequestGetDataQualityErrors();
//            if (toProcess.getEtalonKey() == null || StringUtils.isEmpty(toProcess.getEtalonKey().getId())
//                    || toProcess.getForDate() == null || StringUtils.isEmpty(toProcess.getEntityName())) {
//                throw new SystemRuntimeException("'RequestGetDataQualityErrors' doesn't contain 'etalonKey' or 'etalonId' or 'entityName' or 'forDate'",
//                        DataSoapExceptionIds.EX_SOAP_INCORRECT_REQUEST_INFO_GET_EMPTY_ENTITY_NAME);
//            }
//            List<org.unidata.mdm.backend.common.types.DataQualityError> errors = dataRecordsService.getDQErrors(
//                    toProcess.getEtalonKey().getId(), toProcess.getEntityName(),
//                    toProcess.getForDate().toGregorianCalendar().getTime());
//            ResponseGetDataQualityErrors result = new ResponseGetDataQualityErrors().withDqError(DumpUtils.to(errors));
//            response.setResponseGetDataQualityErrors(result);
//        } catch (Exception exc) {
//            createError(response, exc, SoapOperation.REQUEST_GET_DATA_QUALITY_ERRORS,
//                    request.getCommon().getOperationId(), null);
//        }
//    }

    /**
     * Handle {@link RequestGetLookupValues} type request.
     *
     * @param request the request
     * @param response the response
     */
    private void handleRequestGetLookupValues(UnidataRequestBody request, UnidataResponseBody response) {

    }

    /**
     * Handle {@link RequestMerge} type request.
     *
     * @param request the request
     * @param response the response
     */
    private void handleRequestMerge(UnidataRequestBody request, UnidataResponseBody response) {

        try {

            RequestMerge requestMerge = request.getRequestMerge();
            if (CollectionUtils.isEmpty(requestMerge.getDuplicateEtalonKeyOrDuplicateOriginKey())) {
                throw new SystemRuntimeException("'RequestMerge' doesn't contain duplicate data",
                        DataSoapExceptionIds.EX_SOAP_INCORRECT_REQUEST_MERGE_EMPTY_DUPLICATE_KEYS);
            }

            List<RecordIdentityContext> duplicates = new ArrayList<>();
            for (Object value : requestMerge.getDuplicateEtalonKeyOrDuplicateOriginKey()) {
                if (value instanceof EtalonKey) {
                    duplicates.add(new GetRequestContext.GetRequestContextBuilder()
                            .etalonKey(DumpUtils.from((EtalonKey) value))
                            .build());
                } else if (value instanceof OriginKey) {
                    duplicates.add(new GetRequestContext.GetRequestContextBuilder()
                            .originKey(DumpUtils.from((OriginKey) value))
                            .build());
                }
            }

            if (duplicates.stream().anyMatch(recordIdentityContext -> !recordIdentityContext.isValidRecordKey())) {
                throw new SystemRuntimeException("'RequestMerge' doesn't contain duplicate data",
                        DataSoapExceptionIds.EX_SOAP_INCORRECT_REQUEST_MERGE_EMPTY_DUPLICATE_KEYS);
            }

            MergeRequestContext ctx = new MergeRequestContext.MergeRequestContextBuilder()
                    .etalonKey(DumpUtils.from(requestMerge.getMasterEtalonKey()))
                    .originKey(DumpUtils.from(requestMerge.getMasterOriginKey()))
                    .duplicates(duplicates)
                    .build();

            if (!ctx.isValidRecordKey()) {
                throw new SystemRuntimeException("'RequestMerge' doesn't contain valid master keys",
                        DataSoapExceptionIds.EX_SOAP_INCORRECT_REQUEST_MERGE_EMPTY_MASTER_KEYS);
            }

            ctx.setOperationId(request.getCommon().getOperationId());

            MergeRecordsDTO result = dataRecordsService.merge(ctx);

            ResponseMerge merge = JaxbUtils.getApiObjectFactory().createResponseMerge();
            response.withResponseMerge(merge);

            if (result != null && result.getWinnerId() != null) {
                createSuccess(response, SoapOperation.REQUEST_MERGE,
                        request.getCommon().getOperationId());
            } else {
                createWarning(response, SoapOperation.REQUEST_MERGE,
                        "Winner ID was not returned by the service. Probably no action for merge was done.",
                        request.getCommon().getOperationId());
            }
        } catch (Exception exc) {
            createError(response, exc, SoapOperation.REQUEST_MERGE, request.getCommon().getOperationId());
        }
    }

    /**
     * Handle {@link RequestMerge} type request.
     *
     * @param request the request
     * @param response the response
     */
    private void handleRequestJoin(UnidataRequestBody request, UnidataResponseBody response) {

        try {

            final EtalonKey target = request.getRequestJoin().getEtalonKey();
            final OriginKey join = request.getRequestJoin().getOriginKey();

            JoinRequestContext ctx = JoinRequestContext.builder()
                    .etalonKey(DumpUtils.from(target))
                    .originKey(DumpUtils.from(join))
                    .build();

            ctx.setOperationId(request.getCommon().getOperationId());

            KeysJoinDTO result = dataRecordsService.join(ctx);

            ResponseJoin responseJoin = JaxbUtils.getApiObjectFactory().createResponseJoin();
            response.withResponseJoin(responseJoin);

            if (result != null && result.isAknowleged() && result.getKeys() != null) {
                createSuccess(response, SoapOperation.REQUEST_JOIN,
                        request.getCommon().getOperationId());
            } else {
                createWarning(response, SoapOperation.REQUEST_JOIN,
                        "External ID join operation was not successful.",
                        request.getCommon().getOperationId());
            }
        } catch (Exception exc) {
            createError(response, exc, SoapOperation.REQUEST_JOIN, request.getCommon().getOperationId());
        }
    }

    private void handleRequestSplit(UnidataRequestBody request, UnidataResponseBody response) {
        try {
            final RequestSplit requestSplit = request.getRequestSplit();
            final OriginKey originKey = requestSplit.getOriginKey();
            final SplitRecordsDTO result = dataRecordsService.detachOrigin(SplitRecordRequestContext.builder()
                    .externalId(originKey.getExternalId())
                    .sourceSystem(originKey.getSourceSystem())
                    .entityName(originKey.getEntityName())
                    .build());
            createSuccess(response, SoapOperation.REQUEST_SPLIT, request.getCommon().getOperationId());
            response.withResponseSplit(new ResponseSplit().withEtalonId(result.getEtalonId().get(DataRecordsService.ETALON_ID)));
        } catch (Exception e) {
            createError(response, e, SoapOperation.REQUEST_SPLIT, request.getCommon().getOperationId());
        }

    }

    /**
     * Handle {@link RequestSearch} type request.
     *
     * @param request the request
     * @param response the response
     */
    private void handleRequestSearch(UnidataRequestBody request, UnidataResponseBody response) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_SOAP_SEARCH);

        try {
            MeasurementPoint.start();

            boolean conditionGiven = request.getRequestSearch().getSearchCondition() != null;
            String queryString = conditionGiven
                    ? SoapSearchUtils.buildQStringFromConditions(request.getRequestSearch().getSearchCondition(),
                    metaModelService,
                    request.getRequestSearch().getEntityName())
                    : null;
            String entityName = request.getRequestSearch().getEntityName();
            SearchRequestContext ctx = SearchRequestContext.builder(EntityIndexType.RECORD, entityName, SecurityUtils.getCurrentUserStorageId())
                    .search(SearchRequestType.QSTRING)
                    .source(false)
                    .searchFields(Collections.<String>emptyList())
                    // No fields should be returned. Just the IDs
                    .text(queryString)
                    .count(request.getRequestSearch().getPageSize().intValue())
                    .page(request.getRequestSearch().getPageNumber().intValue())
                    .totalCount(request.getRequestSearch().isReturnCount())
                    //TODO: temporary
//                    .countOnly(request.getRequestSearch().isDoCountOnly())
                    .asOf(JaxbUtils.xmlGregorianCalendarToDate(request.getRequestSearch().getAsOf()))
                    .fetchAll(!conditionGiven)
                    .build();

            SearchResultDTO result = searchService.search(ctx);

            ResponseSearch search = JaxbUtils.getApiObjectFactory().createResponseSearch();

            search.withCount(BigInteger.valueOf(result.getTotalCount()))
                    .withPageNumber(BigInteger.valueOf(ctx.getPage()))
                    .withPageSize(BigInteger.valueOf(ctx.getCount()));

            response.withResponseSearch(search);
            //TODO: temporary
            if (request.getRequestSearch().isDoCountOnly()) {
                return;
            }

            if (!result.getHits().isEmpty()) {
                List<String> ids = new ArrayList<>(result.getHits().size());
                for (SearchResultHitDTO hit : result.getHits()) {
                    ids.add(hit.getId());
                }

                GetMultipleRequestContext dCtx = new GetMultipleRequestContext.GetMultipleRequestContextBuilder()
                        .entityName(ctx.getEntity())
                        .etalonKeys(ids).build();

                GetRecordsDTO records = dataRecordsService.getRecords(dCtx);
                if (records.getEtalons() == null || records.getEtalons().isEmpty()) {
                    createWarning(response, SoapOperation.REQUEST_SEARCH, "Data service didn't return any records.",
                            request.getCommon().getOperationId());
                } else {

                    List<EtalonRecord> etalons = new ArrayList<>(records.getEtalons().size());
                    for (int i = 0; i < records.getEtalons().size(); i++) {
                        etalons.add(DumpUtils.to(records.getEtalons().get(i), records.getEtalons().get(i).getInfoSection(), EtalonRecord.class));
                    }

                    search.withEtalonRecord(etalons);
                    createSuccess(response, SoapOperation.REQUEST_SEARCH,
                            request.getCommon().getOperationId());
                }
            } else {
                createSuccess(response, SoapOperation.REQUEST_SEARCH,
                        request.getCommon().getOperationId());
            }
        } catch (Exception exc) {
            createError(response, exc, SoapOperation.REQUEST_SEARCH, request.getCommon().getOperationId());
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Handle {@link RequestSoftDelete} type request.
     *
     * @param request the request
     * @param response the response
     */
    private void handleRequestSoftDelete(UnidataRequestBody request, UnidataResponseBody response) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_SOAP_DELETE);
        MeasurementPoint.start();

        try {

            // TODO Move allowed entity period check lower!
            Date validFrom = request.getRequestSoftDelete().getRange() != null
                    ? JaxbUtils.xmlGregorianCalendarToDate(request.getRequestSoftDelete().getRange().getRangeFrom())
                    : null;
            Date validTo = request.getRequestSoftDelete().getRange() != null
                    ? JaxbUtils.xmlGregorianCalendarToDate(request.getRequestSoftDelete().getRange().getRangeTo())
                    : null;

            OriginKey originKey = request.getRequestSoftDelete().getOriginKey();
            EtalonKey etalonKey = request.getRequestSoftDelete().getEtalonKey();
            SoftDeleteActionType actionType = request.getRequestSoftDelete().getActionType();

            DeleteRequestContext ctx = new DeleteRequestContext.DeleteRequestContextBuilder()
                    // Set etalon key
                    .etalonKey(etalonKey != null ? etalonKey.getId() : null)
                    // Set origin key
                    .originKey(originKey != null ? originKey.getId() : null)
                    // Set key attributes
                    .externalId(originKey != null ? originKey.getExternalId() : null)
                    .sourceSystem(originKey != null ? originKey.getSourceSystem() : null)
                    .entityName(originKey != null ? originKey.getEntityName() : null)
                    // Set range, if defined
                    .validFrom(validFrom)
                    .validTo(validTo)
                    // Action type
                    .inactivatePeriod(actionType == SoftDeleteActionType.SOFT_DELETE_ETALON_PERIOD)
                    .inactivateEtalon(actionType == SoftDeleteActionType.SOFT_DELETE_ETALON)
                    .inactivateOrigin(actionType == SoftDeleteActionType.SOFT_DELETE_ORIGIN)
                    .cascade(true)
                    .wipe(request.getRequestSoftDelete().isWipe() != null && request.getRequestSoftDelete().isWipe())
                    .build();

            ctx.setOperationId(request.getCommon().getOperationId());
            DeleteRecordDTO result = dataRecordsService.deleteRecord(ctx);

            ResponseSoftDelete softDelete = JaxbUtils.getApiObjectFactory().createResponseSoftDelete();
            response.withResponseSoftDelete(softDelete);

            if (result.wasSuccess()) {
                softDelete
                        .withEtalonKeys(DumpUtils.to(result.getEtalonKey()))
                        .withOriginKeys(DumpUtils.to(result.getOriginKey()));
                createSuccess(response, SoapOperation.REQUEST_SOFT_DELETE,
                        request.getCommon().getOperationId());
            } else {
                createWarning(response, SoapOperation.REQUEST_SEARCH,
                        "Service didn't return deleted key. No objects were deleted.",
                        request.getCommon().getOperationId());
            }
        } catch (Exception exc) {
            createError(response, exc, SoapOperation.REQUEST_SOFT_DELETE, request.getCommon().getOperationId());
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Handle {@link RequestSoftDelete} type request.
     *
     * @param request the request
     * @param response the response
     */
    private void handleRequestRelationsSoftDelete(UnidataRequestBody request, UnidataResponseBody response) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_SOAP_RELATIONS_DELETE);
        MeasurementPoint.start();

        try {

            RequestRelationsSoftDelete del = request.getRequestRelationsSoftDelete();
            SoftDeleteActionType actionType = del.getActionType();

            // TODO remove from xsd
            /*
            Date validFrom = ValidityPeriodUtils.ensureValidityPeriodStart(
                    del.getRange() != null
                    ? JaxbUtils.xmlGregorianCalendarToDate(del.getRange().getRangeFrom())
                    : null);
            Date validTo = ValidityPeriodUtils.ensureValidityPeriodEnd(
                    del.getRange() != null
                    ? JaxbUtils.xmlGregorianCalendarToDate(del.getRange().getRangeTo())
                    : null);
            */

            int totalSubmitted = 0;
            Map<String, List<DeleteRelationRequestContext>> relations =
                    convertRelationsForDelete(request.getCommon().getOperationId(), del.getRelations(), actionType, del.isWipe(), false);
            if (MapUtils.isNotEmpty(relations)) {
                totalSubmitted = relations.values().stream().mapToInt(List::size).sum();
            }

            DeleteRelationsRequestContext dCtx = new DeleteRelationsRequestContext.DeleteRelationsRequestContextBuilder()
                    .etalonKey(DumpUtils.from(del.getEtalonKey()))
                    .originKey(DumpUtils.from(del.getOriginKey()))
                    .relations(relations)
                    .build();

            dCtx.setOperationId(request.getCommon().getOperationId());

            DeleteRelationsDTO result = dataRelationsService.deleteRelations(dCtx);

            ResponseRelationsSoftDelete softDelete = JaxbUtils.getApiObjectFactory().createResponseRelationsSoftDelete();
            response.withResponseRelationsSoftDelete(softDelete);

            int totalDeleted = 0;
            for (Entry<RelationStateDTO, List<DeleteRelationDTO>> e : result.getRelations().entrySet()) {
                for (DeleteRelationDTO dto : e.getValue()) {
                    softDelete.getEtalonKeys()
                            .add(JaxbUtils.getDataObjectFactory().createEtalonKey().withId(dto.getRelationKeys().getEtalonKey().getId()));
                    softDelete.getOriginKeys()
                            .add(JaxbUtils.getDataObjectFactory().createOriginKey().withId(dto.getRelationKeys().getOriginKey().getId()));
                    ++totalDeleted;
                }
            }

            response.setResponseRelationsSoftDelete(softDelete);

            if ((totalSubmitted == totalDeleted) && totalDeleted != 0 && totalSubmitted != 0) {
                createSuccess(response, SoapOperation.REQUEST_SOFT_DELETE_REL,
                        request.getCommon().getOperationId());
            } else {
                createWarning(response, SoapOperation.REQUEST_SOFT_DELETE_REL,
                        "Service returned no or not all deleted key. No or only some objects were deleted.",
                        request.getCommon().getOperationId());
            }
        } catch (Exception exc) {
            createError(response, exc, SoapOperation.REQUEST_SOFT_DELETE_REL, request.getCommon().getOperationId());
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Handle {@link RequestRelationsUpsert} type request.
     *
     * @param request the request
     * @param response the response
     */
    private void handleRequestRelationsUpsert(UnidataRequestBody request, UnidataResponseBody response) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_SOAP_RELATIONS_UPSERT);
        MeasurementPoint.start();
        UpsertRelationsRequestContext rCtx = null;

        try {
            CommonSectionDef commonSection = request.getCommon();
            OriginKey fromOriginKey = request.getRequestRelationsUpsert().getOriginKey();
            EtalonKey fromEtalonKey = request.getRequestRelationsUpsert().getEtalonKey();

            if (fromEtalonKey == null && fromOriginKey == null) {
                throw new IllegalArgumentException("FROM keys not specified.");
            }

            Date validFrom =
                    request.getRequestRelationsUpsert().getRange() != null
                            ? JaxbUtils.xmlGregorianCalendarToDate(request.getRequestRelationsUpsert().getRange().getRangeFrom())
                            : null;
            Date validTo =
                    request.getRequestRelationsUpsert().getRange() != null
                            ? JaxbUtils.xmlGregorianCalendarToDate(request.getRequestRelationsUpsert().getRange().getRangeTo())
                            : null;

            int preparedRelationsCount = 0;
            int upsertedRelationsCount = 0;
            Map<String, List<UpsertRelationRequestContext>> relations = new HashMap<>();
            RequestRelationsUpsert relationsUpsert = request.getRequestRelationsUpsert();
            for (int i = 0; relationsUpsert != null
                    && i < relationsUpsert.getRelations().getRelation().size(); i++) {
                UpsertRelationDef relDef = relationsUpsert.getRelations().getRelation().get(i);
                List<UpsertRelationRequestContext> innerRelations = convertToRelations(relDef, commonSection, validFrom, validTo, false);
                if (!innerRelations.isEmpty()) {
                    for (UpsertRelationRequestContext requestContext : innerRelations) {
                        String relName = requestContext.getRelationName();
                        if (!relations.containsKey(relName)) {
                            relations.put(relName, new ArrayList<>());
                        }
                        relations.get(relName).add(requestContext);
                    }
                }
                preparedRelationsCount += innerRelations.size();
            }

            // Upsert relations
            if (!relations.isEmpty()) {
                rCtx = new UpsertRelationsRequestContext.UpsertRelationsRequestContextBuilder()
                        .relations(relations)
                        .etalonKey(fromEtalonKey != null ? fromEtalonKey.getId() : null)
                        .originKey(fromOriginKey != null ? fromOriginKey.getId() : null)
                        .externalId(fromOriginKey != null ? fromOriginKey.getExternalId() : null)
                        .sourceSystem(fromOriginKey != null ? fromOriginKey.getSourceSystem() : null)
                        .entityName(fromOriginKey != null ? fromOriginKey.getEntityName() : null)
                        .build();
                rCtx.setOperationId(request.getCommon().getOperationId());
            }

            ResponseRelationsUpsert upsert = JaxbUtils.getApiObjectFactory().createResponseRelationsUpsert();
            response.withResponseRelationsUpsert(upsert);

            if (rCtx != null) {
                UpsertRelationsDTO relationsResult = dataRelationsService.upsertRelations(rCtx);
                for (Entry<RelationStateDTO, List<UpsertRelationDTO>> e
                        : relationsResult.getRelations().entrySet()) {
                    upsertedRelationsCount += e.getValue().size();
                }
            }

            if (preparedRelationsCount == upsertedRelationsCount) {
                upsert.withOriginAction(UpsertActionType.UPDATE);
                upsert.setUpsertCount(upsertedRelationsCount);
                createSuccess(response, SoapOperation.REQUEST_UPSERT,
                        request.getCommon().getOperationId());
            } else {
                upsert.withOriginAction(UpsertActionType.NO_ACTION);
                upsert.setUpsertCount(upsertedRelationsCount);
                createWarning(response, SoapOperation.REQUEST_UPSERT,
                        "The number of submitted and upserted relation records don't match.",
                        request.getCommon().getOperationId());
            }

        } catch (ExitException eexc) {
            if (eexc.getExitState() == ExitState.ES_UPSERT_DENIED) {
                response.getResponseUpsert().withOriginAction(UpsertActionType.NO_ACTION);
                createSuccess(response, SoapOperation.REQUEST_UPSERT,
                        request.getCommon().getOperationId());
            } else {
                createError(response, eexc, SoapOperation.REQUEST_UPSERT_REL, request.getCommon().getOperationId());
            }
        } catch (Exception exc) {
            createError(response, exc, SoapOperation.REQUEST_UPSERT_REL, request.getCommon().getOperationId());
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Handle {@link RequestRelationsGet} type request.
     *
     * @param request the request
     * @param response the response
     */
    private void handleRequestRelationsGet(UnidataRequestBody request, UnidataResponseBody response) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_SOAP_RELATIONS_GET);
        MeasurementPoint.start();
        try {

            RequestRelationsGet get = request.getRequestRelationsGet();
            GetRelationsRequestContext ctx = GetRelationsRequestContext.builder()
                    .etalonKey(DumpUtils.from(get.getEtalonKey()))
                    .originKey(DumpUtils.from(get.getOriginKey()))
                    .forDate(JaxbUtils.xmlGregorianCalendarToDate(get.getAsOf()))
                    .relationNames(get.getRelations())
                    .reduceReferences(true)
                    .fetchTimelineData(true)
                    .build();
            ctx.setOperationId(request.getCommon().getOperationId());

            GetRelationsDTO result = dataRelationsService.getRelations(ctx);

            ResponseRelationsGet getResponse = JaxbUtils.getApiObjectFactory().createResponseRelationsGet();
            response.withResponseRelationsGet(getResponse);

            EntityRelations relations = JaxbUtils.getDataObjectFactory().createEntityRelations();
            for (Entry<RelationStateDTO, List<GetRelationDTO>> e : result.getRelations().entrySet()) {
                for (GetRelationDTO dto : e.getValue()) {
                    switch (dto.getRelationType()) {
                        case CONTAINS:
                            relations.getIntegralEntities().add(
                                    DumpUtils.to(
                                            dto.getEtalon(),
                                            dto.getEtalon() != null ? dto.getEtalon().getInfoSection() : null, IntegralRecord.class));
                            break;
                        case REFERENCES:
                        case MANY_TO_MANY:
                            relations.getRelationsTo().add(
                                    DumpUtils.to(
                                            dto.getEtalon(),
                                            dto.getEtalon() != null ? dto.getEtalon().getInfoSection() : null, RelationTo.class));
                            break;
                        default:
                            break;
                    }
                }
            }

            getResponse.withRelations(relations);

            if (MapUtils.isNotEmpty(result.getRelations())) {
                createSuccess(response, SoapOperation.REQUEST_GET_REL, request.getCommon().getOperationId());
            } else {
                createWarning(response,
                        SoapOperation.REQUEST_GET_REL, "No result. Relation(s) not found.",
                        request.getCommon().getOperationId());
            }

        } catch (Exception exc) {
            createError(response, exc, SoapOperation.REQUEST_GET_REL, request.getCommon().getOperationId());
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Handle {@link RequestUpsert} type request.
     *
     * @param request the request
     * @param response the response
     */
    private void handleRequestUpsert(UnidataRequestBody request, UnidataResponseBody response) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_SOAP_UPSERT);
        MeasurementPoint.start();
        RequestUpsert requestUpsert = request.getRequestUpsert();
        UpsertRequestContext ctx = null;

        try {
            UpsertRecordDTO result = null;
            int attempt = 1;
            boolean success = false;
            while (!success && attempt <= maxAttemptCount) {
                try {
                    ctx = convertToUpsertContext(requestUpsert, request.getCommon(), false);
                    result = dataRecordsService.upsertRecord(ctx);
                    attempt++;
                    success = true;
                } catch (DuplicateKeyException exc) {
                    LOGGER.error("Error error was occurred during the upsert record process. Attempt: " + attempt, exc);
                    attempt++;
                    if (attempt > maxAttemptCount) {
                        throw exc;
                    }
                }
            }
            ResponseUpsert upsert = JaxbUtils.getApiObjectFactory().createResponseUpsert();

            UpsertActionType actionType = result.getAction() == null
                    ? UpsertActionType.NO_ACTION
                    : UpsertActionType.valueOf(result.getAction().name());

            // TODO @Modules
//            if (actionType == UpsertActionType.NO_ACTION && MapUtils.isNotEmpty(result.getRelations())) {
//                UpsertRelationDTO upsertRelationDTO = result.getRelations().entrySet().stream()
//                        .map(Entry::getValue)
//                        .filter(Objects::nonNull)
//                        .flatMap(Collection::stream)
//                        .filter(rel -> rel.getAction() == UpsertAction.INSERT || rel.getAction() == UpsertAction.UPDATE)
//                        .findFirst()
//                        .orElse(null);
//                if (upsertRelationDTO != null) {
//                    actionType = UpsertActionType.valueOf(upsertRelationDTO.getAction().name());
//                }
//            }

            upsert.withOriginAction(actionType);


            if (result.isEtalon()) {
                upsert.withEtalonKey(DumpUtils.to(result.getRecordKeys().getEtalonKey()))
                        .withOriginKey(DumpUtils.to(result.getRecordKeys().getOriginKey()));
            }

            response.withResponseUpsert(upsert);
            boolean isSuccess = nonNull(upsert.getEtalonKey()) || nonNull(upsert.getOriginKey());
            if (isSuccess) {
                createSuccess(response, SoapOperation.REQUEST_UPSERT,
                        request.getCommon().getOperationId());
            } else {
                createWarning(response, SoapOperation.REQUEST_UPSERT,
                        "No keys returned by the service or relations upsert failed.",
                        request.getCommon().getOperationId());
            }

            // TODO @Modules
            /*if (CollectionUtils.isNotEmpty(ctx.getDqErrors())) {
                ExecutionMessageDef executionMessageDef = JaxbUtils.getApiObjectFactory().createExecutionMessageDef();
                executionMessageDef.withDqErrors(ctx.getDqErrors().stream().map(DumpUtils::to).collect(Collectors.toList()));

                CommonResponseDef common = null;
                if (response.getCommon() == null) {
                    common = JaxbUtils.getApiObjectFactory().createCommonResponseDef();
                    response.setCommon(common);
                } else {
                    common = response.getCommon();
                }

                common.setMessage(executionMessageDef);
            }*/

        } catch (ExitException eexc) {
            if (eexc.getExitState() == ExitState.ES_UPSERT_DENIED) {
                response.getResponseUpsert().withOriginAction(UpsertActionType.NO_ACTION);
                createSuccess(response, SoapOperation.REQUEST_UPSERT,
                        request.getCommon().getOperationId());
            } else {
                createError(response, eexc, SoapOperation.REQUEST_UPSERT, request.getCommon().getOperationId());
            }
        } catch (Exception exc) {
            createError(response, exc, SoapOperation.REQUEST_UPSERT, request.getCommon().getOperationId());
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * convert to upsert ctx.
     *
     * @param requestUpsert - upsert request
     * @param commonSection - common section
     * @param bulkUpsert will set batch flag
     * @return the upsert request context
     */
    private UpsertRequestContext convertToUpsertContext(RequestUpsert requestUpsert, CommonSectionDef commonSection, boolean bulkUpsert) {
        AsyncSectionDef asyncSection = commonSection.getAsyncOptions();
        String entityName = requestUpsert.getOriginRecord() != null
                ? requestUpsert.getOriginRecord().getOriginKey() != null
                ? requestUpsert.getOriginRecord().getOriginKey().getEntityName()
                : null
                : requestUpsert.getEtalonRecord() != null
                ? requestUpsert.getEntityName()
                : null;

        Date validFrom = requestUpsert.getRange() != null
                ? JaxbUtils.xmlGregorianCalendarToDate(requestUpsert.getRange().getRangeFrom())
                : null;
        Date validTo = requestUpsert.getRange() != null
                ? JaxbUtils.xmlGregorianCalendarToDate(requestUpsert.getRange().getRangeTo())
                : null;

        EtalonKey etalonKey = requestUpsert.getEtalonRecord() == null ? null : requestUpsert.getEtalonRecord().getEtalonKey();
        OriginKey originKey = requestUpsert.getOriginRecord() == null ? null : requestUpsert.getOriginRecord().getOriginKey();
        enrich(requestUpsert.getOriginRecord(), entityName, "");
        enrich(requestUpsert.getEtalonRecord(), entityName, "");

        UpsertRequestContext.UpsertRequestContextBuilder builder = UpsertRequestContext.builder()
                .etalonKey(DumpUtils.from(etalonKey))
                .originKey(DumpUtils.from(originKey))
                .record(DumpUtils.from(requestUpsert.getEtalonRecord() == null ? requestUpsert.getOriginRecord() : requestUpsert.getEtalonRecord()))
                .bypassExtensionPoints(requestUpsert.isBypassExtensionPoints())
                .skipCleanse(requestUpsert.isSkipCleanse())
                .lastUpdate(JaxbUtils.xmlGregorianCalendarToDate(requestUpsert.getLastUpdateDate()))
                .validFrom(validFrom)
                .validTo(validTo)
                .entityName(entityName)
                .mergeWithPrevVersion(requestUpsert.isMergeWithPreviousVersion())
                .codeAttributeAliases(DumpUtils.convertAliasCodeAttrPs(requestUpsert.getAliasCodeAttributePointers()))
                // Wait for etalon calculation to prevent authentication expiration
                // while some calculation threads are still busy working
// @Modules Removed
//                .returnEtalon(true)
                .batchOperation(bulkUpsert);

        for (int i = 0; requestUpsert.getRelations() != null
                && i < requestUpsert.getRelations().getRelation().size(); i++) {
            UpsertRelationDef relDef = requestUpsert.getRelations().getRelation().get(i);
            List<UpsertRelationRequestContext> relations = convertToRelations(relDef, commonSection, validFrom, validTo, bulkUpsert);
            builder.addRelations(relations);
        }
        Map<String, List<DeleteRelationRequestContext>> relationsForDelete =
                convertRelationsForDelete(
                        commonSection.getOperationId(),
                        requestUpsert.getRelationsDelete(),
                        SoftDeleteActionType.SOFT_DELETE_ETALON,
                        false, bulkUpsert);

        if (MapUtils.isNotEmpty(relationsForDelete)) {
            relationsForDelete.values().forEach(builder::addRelationDeletes);
        }

        // TODO @Modules
//        List<ClassifierIdentityContext> classifierRecords = convertToClassifiers(requestUpsert, commonSection, bulkUpsert);
//        List<UpsertClassifierDataRequestContext> upserts = classifierRecords.stream()
//                .filter(clc -> clc instanceof UpsertClassifierDataRequestContext)
//                .map(clc -> (UpsertClassifierDataRequestContext) clc)
//                .collect(Collectors.toList());
//
//        builder.addClassifierUpserts(upserts);
//
//        List<DeleteClassifierDataRequestContext> deletes = classifierRecords.stream()
//                .filter(clc -> clc instanceof DeleteClassifierDataRequestContext)
//                .map(clc -> (DeleteClassifierDataRequestContext) clc)
//                .collect(Collectors.toList());
//
//        builder.addClassifierDeletes(deletes);

        UpsertRequestContext ctx = builder.build();
        ctx.setOperationId(commonSection.getOperationId());
        // TODO @Modules
//        boolean sendNotification = asyncSection != null ? asyncSection.isUseJMS() : true;
//        if (sendNotification) {
//            String destination = asyncSection != null ? asyncSection.getJmsReplyTo() : null;
//            String id = asyncSection != null ? asyncSection.getJmsCorrelationId() : null;
//            ctx.sendNotification(destination, id);
//        } else {
//            ctx.skipNotification();
//        }

        return ctx;
    }

    /**
     * Enrich.
     *
     * @param record the record
     * @param entityName the entity name
     * @param path the path
     */
    private void enrich(NestedRecord record, String entityName, String path) {
        if (record == null) {
            return;
        }
        if (record.getSimpleAttributes() != null) {
            record.getSimpleAttributes().forEach(attr -> enrichSimple(attr, entityName, path));
        }
        if (record.getComplexAttributes() != null) {
            record.getComplexAttributes().forEach(attr -> enrichComplex(attr, entityName, path));
        }

    }

    /**
     * Enrich complex.
     *
     * @param attr the attr
     * @param entityName the entity name
     * @param path the path
     */
    private void enrichComplex(ComplexAttribute attr, String entityName, String path) {
        if (attr == null) {
            return;
        }
        if (attr.getNestedRecord() != null) {
            attr.getNestedRecord()
                    .forEach(record -> enrich(record, entityName, String.join(".", path, attr.getName())));
        }
    }

    /**
     * Enrich simple.
     *
     * @param attr the attr
     * @param entityName the entity name
     * @param path the path
     */
    private void enrichSimple(SimpleAttribute attr, String entityName, String path) {
        if (attr == null) {
            return;
        }
        if (attr.getType() == null) {
            final AbstractAttributeDef attributeByPath = metaModelService.getAttributeByPath(
                    entityName, StringUtils.isEmpty(path) ? attr.getName() : String.join(".", path, attr.getName()));
            if (attributeByPath instanceof AbstractSimpleAttributeDef || attributeByPath == null) {
                AbstractSimpleAttributeDef attributeDef = (AbstractSimpleAttributeDef) attributeByPath;
                attr.setType(attributeDef == null || attributeDef.getSimpleDataType() == null ? ValueDataType.STRING
                        : ValueDataType.valueOf(attributeDef.getSimpleDataType().name()));
            } else {
                throw new IllegalArgumentException(
                        "Attribute \"" + attr.getName() + "\" type is not SimpleAttribute. Entity name: "
                                + entityName + ", path: " + path + ", class: " + attributeByPath.getClass()
                );
            }
        }
    }

    /**
     * Checks authentication.
     *
     * @param request the request
     * @return true if security token is present, false otherwise
     */
    private boolean isAuthenticated(UnidataRequestBody request) {
        boolean isAuthenticated = false;
        SecuritySectionDef securitySection = request.getCommon() == null ? null : request.getCommon().getSecurity();
        if (securitySection != null) {
            String token = securitySection.getSessionToken() != null
                    ? securitySection.getSessionToken().getToken()
                    : null;
            if (!StringUtils.isBlank(token)) {
                try {
                    return securityService.authenticate(token, true);
                } catch (Exception exc) {
                    LOGGER.debug("Authentication failed for token [{}]. Caught exception [{}].", token, exc);
                }
            }
        }

        return isAuthenticated;
    }

    /**
     * Login requested (or not).
     *
     * @param request the request
     * @return true, if login requested, false otherwise
     */
    private boolean doLogin(UnidataRequestBody request) {
        return request.getRequestAuthenticate() != null && request.getRequestAuthenticate().isDoLogin();
    }

    /**
     * Checks, if logout should be done after request completion. TODO redefine
     * xsd, make the condition clearer.
     *
     * @param request the request
     * @return true if logout should be done, flase otherwise
     */
    private boolean doLogout(UnidataRequestBody request) {
        return !doLogin(request)
                && request.getCommon() != null
                && request.getCommon().getSecurity() != null
                && request.getCommon().getSecurity().getCredentials() != null
                && (request.getCommon().getSecurity().getSessionToken() == null
                || request.getCommon().getSecurity().getSessionToken().getToken() == null || request
                .getCommon().getSecurity().getSessionToken().getToken().isEmpty());
    }

    // TODO @Modules
//    /**
//     * Converts classifiers section.
//     *
//     * @param requestUpsert upsert request section
//     * @param commonSection common section
//     * @param bulkUpsert TODO
//     * @return list
//     */
//    private List<ClassifierIdentityContext> convertToClassifiers(
//            RequestUpsert requestUpsert, CommonSectionDef commonSection, boolean bulkUpsert) {
//
//        List<ClassifierIdentityContext> classifierContexts = new ArrayList<>();
//        List<EtalonClassifierRecord> etalonClassifiers = requestUpsert.getEtalonRecord() != null
//                ? requestUpsert.getEtalonRecord().getClassifiers()
//                : Collections.emptyList();
//        List<OriginClassifierRecord> originClassifiers = requestUpsert.getOriginRecord() != null
//                ? requestUpsert.getOriginRecord().getClassifiers()
//                : Collections.emptyList();
//        Map<String, ClassifierPointerDef> pointers = requestUpsert.getClassifierPointers() != null
//                ? requestUpsert.getClassifierPointers().stream().collect(Collectors.toMap(ClassifierPointerDef::getClassifierName, p -> p))
//                : Collections.emptyMap();
//
//        for (EtalonClassifierRecord ecr : etalonClassifiers) {
//
//            ClassifierPointerDef cp = pointers.get(ecr.getClassifierName());
//            if (ecr.getStatus() == com.unidata.mdm.data.v5.RecordStatus.INACTIVE) {
//
//                DeleteClassifierDataRequestContext dCtx = DeleteClassifierDataRequestContext.builder()
//                        .classifierName(ecr.getClassifierName())
//                        .classifierNodeId(ecr.getClassifierNodeId())
//                        .classifierNodeCode(cp != null && cp.getPointerType() == ClassifierPointerType.NODE_CODE ? cp.getClassifierPointer() : null)
//                        .classifierNodeName(cp != null && cp.getPointerType() == ClassifierPointerType.NODE_NAME ? cp.getClassifierPointer() : null)
//                        //.classifierNodeExternalId(ecr.getClassifierNodeId())
//                        .inactivateEtalon(true)
//                        .batchUpsert(bulkUpsert)
//                        .build();
//
//                dCtx.setOperationId(commonSection.getOperationId());
//                classifierContexts.add(dCtx);
//
//            } else {
//
//                UpsertClassifierDataRequestContext uCtx = UpsertClassifierDataRequestContext.builder()
//                        .classifier(DumpUtils.from(ecr))
//                        .classifierName(ecr.getClassifierName())
//                        .classifierNodeId(ecr.getClassifierNodeId())
//                        .classifierNodeCode(cp != null && cp.getPointerType() == ClassifierPointerType.NODE_CODE ? cp.getClassifierPointer() : null)
//                        .classifierNodeName(cp != null && cp.getPointerType() == ClassifierPointerType.NODE_NAME ? cp.getClassifierPointer() : null)
//                        .classifierNodeExternalId(ecr.getClassifierNodeId())
//                        .status(ecr.getStatus() != null ? RecordStatus.valueOf(ecr.getStatus().name()) : null)
//                        .batchUpsert(bulkUpsert)
//                        .build();
//
//                uCtx.setOperationId(commonSection.getOperationId());
//                classifierContexts.add(uCtx);
//            }
//        }
//
//        for (OriginClassifierRecord ocr : originClassifiers) {
//
//            ClassifierPointerDef cp = pointers.get(ocr.getClassifierName());
//            if (ocr.getStatus() == com.unidata.mdm.data.v5.RecordStatus.INACTIVE) {
//
//                DeleteClassifierDataRequestContext dCtx = DeleteClassifierDataRequestContext.builder()
//                        .classifierName(ocr.getClassifierName())
//                        .classifierNodeId(ocr.getClassifierNodeId())
//                        .classifierNodeCode(cp != null && cp.getPointerType() == ClassifierPointerType.NODE_CODE ? cp.getClassifierPointer() : null)
//                        .classifierNodeName(cp != null && cp.getPointerType() == ClassifierPointerType.NODE_NAME ? cp.getClassifierPointer() : null)
//                        //.classifierNodeExternalId(ocr.getClassifierNodeId())
//                        .inactivateEtalon(true)
//                        .batchUpsert(bulkUpsert)
//                        .build();
//
//                dCtx.setOperationId(commonSection.getOperationId());
//                classifierContexts.add(dCtx);
//            } else {
//
//                UpsertClassifierDataRequestContext uCtx = UpsertClassifierDataRequestContext.builder()
//                        .classifier(DumpUtils.from(ocr))
//                        .classifierName(ocr.getClassifierName())
//                        .classifierNodeId(ocr.getClassifierNodeId())
//                        .classifierNodeCode(cp != null && cp.getPointerType() == ClassifierPointerType.NODE_CODE ? cp.getClassifierPointer() : null)
//                        .classifierNodeName(cp != null && cp.getPointerType() == ClassifierPointerType.NODE_NAME ? cp.getClassifierPointer() : null)
//                        .classifierNodeExternalId(ocr.getClassifierNodeId())
//                        .status(ocr.getStatus() != null ? RecordStatus.valueOf(ocr.getStatus().name()) : null)
//                        .batchUpsert(bulkUpsert)
//                        .build();
//
//                uCtx.setOperationId(commonSection.getOperationId());
//                classifierContexts.add(uCtx);
//            }
//        }
//
//        return classifierContexts;
//    }

    /**
     * Process a portion of entity relations.
     *
     * @param er entity relations
     * @param commonSection the common section
     * @param parentValidFrom the parent valid from
     * @param parentValidTo the parent valid to
     * @param bulkUpsert TODO
     * @return list of relations
     */
    @Nonnull
    private List<UpsertRelationRequestContext> convertToRelations(
            UpsertRelationDef er, CommonSectionDef commonSection, Date parentValidFrom, Date parentValidTo, boolean bulkUpsert) {

        RelationDef relDef = metaModelService.getRelationById(er.getName());
        if (relDef == null) {
            throw new IllegalArgumentException("Relation not found by name [" + er.getName() + "]");
        }
        /*
        OverrideRelationRange or = null;
        if (er.getOverride() != null) {

            Date oFrom = er.getOverride().getRange() != null
                    ? ValidityPeriodUtils.ensureValidityPeriodStart(
                            JaxbUtils.xmlGregorianCalendarToDate(er.getOverride().getRange().getRangeFrom()))
                    : parentValidFrom;

            Date oTo = er.getOverride().getRange() != null
                    ? ValidityPeriodUtils.ensureValidityPeriodStart(
                            JaxbUtils.xmlGregorianCalendarToDate(er.getOverride().getRange().getRangeTo()))
                    : parentValidTo;

            or = new OverrideRelationRange(oFrom, oTo);
        }

        UpsertRelationState key = new UpsertRelationState(relDef.getName(), or);
        */

        List<UpsertRelationRequestContext> ctxts = new ArrayList<>();
        for (UpsertRelationRecordDef record : er.getRecords()) {

            RelationBase rel = record.getRecord();
            if (StringUtils.isBlank(rel.getRelName())) {
                throw new IllegalArgumentException("Relation name should be passed");
            }

            Date validFrom =
                    record.getRange() != null
                            ? JaxbUtils.xmlGregorianCalendarToDate(record.getRange().getRangeFrom())
                            : null;
            Date validTo =
                    record.getRange() != null
                            ? JaxbUtils.xmlGregorianCalendarToDate(record.getRange().getRangeTo())
                            : null;

            UpsertRelationRequestContext.UpsertRelationRequestContextBuilder builder = UpsertRelationRequestContext.builder()
                    .relation(DumpUtils.from(rel))
                    .relationName(rel.getRelName())
                    .validFrom(validFrom)
                    .validTo(validTo);

            String sourceSystem;
            String externalId;
            String entityName;
            String originId;
            String etalonId;

            if (relDef.getRelType() != RelType.CONTAINS) {
                ReferenceAliasKey referenceResolver = record.getReferenceAliasKey();
                RelationTo rt = (RelationTo) rel;
                builder.referenceAliasKey(DumpUtils.from(referenceResolver));
                sourceSystem = rt.getToOriginKey() == null ? null : rt.getToOriginKey().getSourceSystem();
                externalId = rt.getToOriginKey() == null ? null : rt.getToOriginKey().getExternalId();
                entityName = rt.getToOriginKey() == null ? null : rt.getToOriginKey().getEntityName();
                originId = rt.getToOriginKey() == null ? null : rt.getToOriginKey().getId();
                etalonId = rt.getToEtalonKey() == null ? null : rt.getToEtalonKey().getId();

            } else {
                IntegralRecord ir = (IntegralRecord) rel;
                sourceSystem = ir.getOriginRecord() == null || ir.getOriginRecord().getOriginKey() == null ? null : ir.getOriginRecord().getOriginKey().getSourceSystem();
                externalId = ir.getOriginRecord() == null || ir.getOriginRecord().getOriginKey() == null ? null : ir.getOriginRecord().getOriginKey().getExternalId();
                entityName = ir.getOriginRecord() == null || ir.getOriginRecord().getOriginKey() == null ? null : ir.getOriginRecord().getOriginKey().getEntityName();
                originId = ir.getOriginRecord() == null || ir.getOriginRecord().getOriginKey() == null ? null : ir.getOriginRecord().getOriginKey().getId();
                etalonId = ir.getEtalonRecord() == null || ir.getEtalonRecord().getEtalonKey() == null ? null : ir.getEtalonRecord().getEtalonKey().getId();
            }

            builder
                    .sourceSystem(sourceSystem)
                    .externalId(externalId)
                    .entityName(entityName)
                    .originKey(originId)
                    .etalonKey(etalonId)
                    .batchOperation(bulkUpsert);

            UpsertRelationRequestContext ctx = builder.build();
            ctx.setOperationId(commonSection.getOperationId());
            ctxts.add(ctx);
        }
        return ctxts;
    }

    /**
     * Handles logout.
     *
     * @param request the request
     * @param authentication the authentication
     */
    public void handleLogout(UnidataRequestBody request, Authentication authentication) {
        User user = securityService.getUserByToken(authentication.getCredentials().toString());
        String userName = user != null ? user.getLogin() : null;

        HttpServletRequest h = (HttpServletRequest) jaxwsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST);
        Map<AuthenticationSystemParameter, Object> params = new EnumMap<>(AuthenticationSystemParameter.class);
        params.put(AuthenticationSystemParameter.PARAM_USER_NAME, userName);
        params.put(AuthenticationSystemParameter.PARAM_HTTP_SERVLET_REQUEST, h);
        params.put(AuthenticationSystemParameter.PARAM_CLIENT_IP, ClientIpUtil.clientIp(h));
        params.put(AuthenticationSystemParameter.PARAM_SERVER_IP, h.getLocalAddr());
        params.put(AuthenticationSystemParameter.PARAM_ENDPOINT, EndpointType.SOAP);
        params.put(AuthenticationSystemParameter.PARAM_DETAILS, "Самостоятельный выход");
        try {
            boolean result = securityService.logout(authentication.getCredentials().toString(), params);
            SecurityContextHolder.getContext().setAuthentication(null);
            LOGGER.info(
                    "Log out request for user [{}], token [{}], principal [{}], authorities [{}] finished "
                            + (result ? "" : "NOT") + " successfully.",
                    request.getCommon().getSecurity().getCredentials().getUsername(), authentication.getCredentials(),
                    authentication.getPrincipal(), authentication.getAuthorities());
        } catch (Exception e) {
            LOGGER.warn("Couldn't log out user. Token [{}], exception [{}]", authentication.getCredentials(), e);
        }
    }

    /**
     * Handles authenticate request.
     *
     * @param request the request
     * @param response the response
     * @return true if successful, false otherwise
     */
    private Authentication handleAuthenticate(UnidataRequestBody request, UnidataResponseBody response) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_SOAP_AUTH);
        MeasurementPoint.start();

        boolean doLogin = doLogin(request);
        CredentialsDef credentials = request.getCommon() == null ? null
                : request.getCommon().getSecurity() == null ? null : request.getCommon().getSecurity().getCredentials();

        if (credentials == null) {
            response.withCommon(JaxbUtils.getApiObjectFactory()
                    .createCommonResponseDef()
                    .withExitCode(ExitCodeType.AUTHENTICATION_ERROR)
                    .withMessage(JaxbUtils.getApiObjectFactory()
                            .createExecutionMessageDef()
                            .withMessageText("No credentials supplied.")));
            return null;
        }

        HttpServletRequest h = (HttpServletRequest) jaxwsContext.getMessageContext().get(MessageContext.SERVLET_REQUEST);
        Map<AuthenticationSystemParameter, Object> params = new EnumMap<>(AuthenticationSystemParameter.class);
        if (StringUtils.isNotBlank(credentials.getExternalToken())) {
            params.put(AuthenticationSystemParameter.PARAM_EXTERNAL_TOKEN, credentials.getExternalToken());
        } else {
            params.put(AuthenticationSystemParameter.PARAM_USER_NAME, credentials.getUsername());
            params.put(AuthenticationSystemParameter.PARAM_USER_PASSWORD, credentials.getPassword());
        }
        params.put(AuthenticationSystemParameter.PARAM_HTTP_SERVLET_REQUEST, h);
        params.put(AuthenticationSystemParameter.PARAM_CLIENT_IP, ClientIpUtil.clientIp(h));
        params.put(AuthenticationSystemParameter.PARAM_SERVER_IP, h.getLocalAddr());
        params.put(AuthenticationSystemParameter.PARAM_ENDPOINT, EndpointType.SOAP);
        try {

            SecurityToken token = securityService.login(params);
            if (token == null || token.getToken() == null) {
                throw new PlatformBusinessException("Login or password not valid!", DataSoapExceptionIds.EX_SECURITY_CANNOT_LOGIN);
            }

            // Sets up spring security context
            securityService.authenticate(token.getToken(), true);

            // Sets granted authorities
            List<RoleRefDef> roles = new ArrayList<>();
            for (final Right right : token.getRightsMap().values()) {
                roles.add(JaxbUtils.getApiObjectFactory().createRoleRefDef()
                        .withName(right.getSecuredResource().getName()));
            }

            ResponseAuthenticate responseAuthenticate = JaxbUtils.getApiObjectFactory().createResponseAuthenticate();
            responseAuthenticate
                    .withIsAdmin(token.getUser().isAdmin())
                    .withRole(roles);

            if (doLogin) {
                SessionTokenDef sessionTokenDef = SecurityJaxbUtils.getSecurityObjectFactory()
                        .createSessionTokenDef()
                        .withToken(token.getToken());
                responseAuthenticate.withSessionToken(sessionTokenDef);
            }
            response.withResponseAuthenticate(responseAuthenticate);

            return SecurityContextHolder.getContext().getAuthentication();
        } catch (Exception exc) {
            response.withCommon(JaxbUtils.getApiObjectFactory()
                    .createCommonResponseDef()
                    .withExitCode(ExitCodeType.AUTHENTICATION_ERROR)
                    .withMessage(JaxbUtils.getApiObjectFactory()
                            .createExecutionMessageDef()
                            .withMessageText(
                                    "Authentication with supplied credentials failed. Please, verify user and password.")));
            LOGGER.debug("Authentication failed for user [{}], password [*****]. Caught exception [{}].",
                    credentials.getUsername(), exc);
            return null;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Creates fault information container and adds it to the response.
     *
     * @param response the response
     * @param exc exception
     * @param op the op
     * @param operationId the operation id
     */
    private void createError(UnidataResponseBody response, Exception exc, SoapOperation op, String operationId) {
        ExecutionMessageDef executionMessageDef = JaxbUtils.getApiObjectFactory().createExecutionMessageDef();
        if (!(exc instanceof SystemRuntimeException)) {
            StringBuilder b = new StringBuilder();
            for (StackTraceElement el : exc.getStackTrace()) {
                b.append(String.format("    %s%n", el.toString()));
            }
            executionMessageDef.withStackTrace(b.toString());
        } else {
            SystemRuntimeException systemRuntimeException = (SystemRuntimeException) exc;
            ExecutionErrorDef errorDef = JaxbUtils.getApiObjectFactory().createExecutionErrorDef();
            errorDef.setInternalMessage(systemRuntimeException.getMessage());
            errorDef.setErrorCode(systemRuntimeException.getId().code());
            errorDef.setUserMessage(
                    messageSource.getMessage(
                            systemRuntimeException.getId().code(),
                            systemRuntimeException.getArgs(),
                            "ххх" + systemRuntimeException.getId().code() + "ххх",
                            RU));
            executionMessageDef.withError(errorDef);
        }

        executionMessageDef.withMessageText(op.name() + ": " + exc.toString());

        CommonResponseDef common = JaxbUtils.getApiObjectFactory().createCommonResponseDef();
        common.setExitCode(ExitCodeType.ERROR);
        common.setOperationId(operationId);
        common.setMessage(executionMessageDef);
        response.setCommon(common);
    }

    /**
     * Creates success information container and adds it to the response.
     *
     * @param response the response
     * @param op the op
     * @param operationId the operation id
     */
    private void createSuccess(UnidataResponseBody response, SoapOperation op, String operationId) {
        CommonResponseDef common = JaxbUtils.getApiObjectFactory().createCommonResponseDef();
        common.setExitCode(ExitCodeType.SUCCESS);
        common.setOperationId(operationId);
        common.setMessage(JaxbUtils.getApiObjectFactory().createExecutionMessageDef()
                .withMessageText(op.name() + ": Completed successfully."));
        response.setCommon(common);
    }

    /**
     * Creates warning information container and adds it to the response.
     *
     * @param response the response
     * @param op the op
     * @param warning the warning
     * @param operationId the operation id
     */
    private void createWarning(UnidataResponseBody response, SoapOperation op, String warning, String operationId) {
        CommonResponseDef common = JaxbUtils.getApiObjectFactory().createCommonResponseDef();
        common.setExitCode(ExitCodeType.WARNING);
        common.setOperationId(operationId);
        common.setMessage(JaxbUtils.getApiObjectFactory().createExecutionMessageDef().withMessageText(op.name() + ": " + warning));
        response.setCommon(common);
    }

    private Map<String, List<DeleteRelationRequestContext>> convertRelationsForDelete(
            String operationId, DeleteRelationsDef del, SoftDeleteActionType actionType, Boolean wipe, boolean bulkUpsert) {

        if (del == null || CollectionUtils.isEmpty(del.getRelation())) {
            return null;
        }
        Map<String, List<DeleteRelationRequestContext>> relations = new HashMap<>();
        for (DeleteRelationDef delRelDef : del.getRelation()) {

            if (relations.get(delRelDef.getName()) == null) {
                relations.put(delRelDef.getName(), new ArrayList<>());
            }

            for (DeleteRelationRecordDef key : delRelDef.getKeys()) {

                DeleteRelationRequestContext ctx = new DeleteRelationRequestContext.DeleteRelationRequestContextBuilder()
                        // Keys
                        .etalonKey(DumpUtils.from(key.getEtalonKey()))
                        .originKey(DumpUtils.from(key.getOriginKey()))
                        .relationName(delRelDef.getName())
                        // Action type
                        .inactivatePeriod(actionType == SoftDeleteActionType.SOFT_DELETE_ETALON_PERIOD)
                        .inactivateEtalon(actionType == SoftDeleteActionType.SOFT_DELETE_ETALON)
                        .inactivateOrigin(actionType == SoftDeleteActionType.SOFT_DELETE_ORIGIN)
                        // Time interval
                        .validFrom(key.getRange() != null ? JaxbUtils.xmlGregorianCalendarToDate(key.getRange().getRangeFrom()) : null)
                        .validTo(key.getRange() != null ? JaxbUtils.xmlGregorianCalendarToDate(key.getRange().getRangeTo()) : null)
                        .wipe(BooleanUtils.toBoolean(wipe))
                        .batchOperation(bulkUpsert)
                        .build();

                ctx.setOperationId(operationId);

                relations.get(delRelDef.getName()).add(ctx);
            }
        }
        return relations;
    }
}
