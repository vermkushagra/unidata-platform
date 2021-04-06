package com.unidata.mdm.dq.api.v5;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import com.unidata.mdm.backend.common.service.ValidationService;
import com.unidata.mdm.util.ClientIpUtil;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.annotations.SchemaValidation;
import org.apache.cxf.annotations.SchemaValidation.SchemaValidationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.unidata.mdm.api.v5.SessionTokenDef;
import com.unidata.mdm.api.wsdl.v5.DumpUtils;
import com.unidata.mdm.backend.common.context.DQContext;
import com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter;
import com.unidata.mdm.backend.common.runtime.MeasurementContextName;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.security.Endpoint;
import com.unidata.mdm.backend.common.service.DataQualityService;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.service.SecurityService;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.dq.v5.ApplyDQRequest;
import com.unidata.mdm.dq.v5.ApplyDQResponse;
import com.unidata.mdm.dq.v5.ApplyDQResponse.Payload;
import com.unidata.mdm.dq.v5.DQApplyInfoType;
import com.unidata.mdm.dq.v5.DQApplyModeType;
import com.unidata.mdm.dq.v5.DQApplyStatusType;
import com.unidata.mdm.dq.v5.DQRecordType;
import com.unidata.mdm.dq.v5.DQRuleType;
import com.unidata.mdm.dq.v5.DataQualityResultType;
import com.unidata.mdm.dq.v5.InfoType;
import com.unidata.mdm.dq.v5.ResultsRequestType;
import com.unidata.mdm.dq.v5.ResultsResponseType;
import com.unidata.mdm.error_handling.v5.ApiFaultType;
import com.unidata.mdm.meta.DQApplicableType;
import com.unidata.mdm.meta.DQRSourceSystemRef;
import com.unidata.mdm.meta.DQRuleDef;
import com.unidata.mdm.meta.PeriodBoundaryDef;


/**
 * The Class DataQualitySOAPServiceImpl.
 */
@SchemaValidation(type = SchemaValidationType.NONE)
public class DataQualitySOAPServiceImpl extends ApplyDQImpl {

	/** The dq service. */
	@Autowired
	private DataQualityService dqService;

	/** The threads. */
	@Value(value = "${com.unidata.mdm.dq.batch.threads:0}")
	private int threads;

	/** The metamodel service. */
	@Autowired
	private MetaModelService metamodelService;

	/** The security service. */
	@Autowired
	private SecurityService securityService;

	@Autowired
	private ValidationService validationService;

	/** The jaxws context. */
	@Resource
	private WebServiceContext jaxwsContext;

	/** The template. */
	@Produce(context = "dq-routes", uri = "vm:SAVE_ERRORS")
	private ProducerTemplate errorRouteSave;

	/** The template. */
	@Produce(context = "dq-routes", uri = "direct-vm:GET_ERRORS")
	private ProducerTemplate errorRouteRetrieve;

	/** The display names. */
	private LoadingCache<String, Optional<String>> tokens = CacheBuilder.newBuilder()
			.expireAfterWrite(60, TimeUnit.SECONDS).build(new TokenCacheLoader());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.unidata.mdm.dq.api.v5.ApplyDQImpl#getResults(com.unidata.mdm.dq.v5.
	 * GetResultsRequest, javax.xml.ws.Holder, javax.xml.ws.Holder)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultsResponseType getResults(ResultsRequestType request, Holder<SessionTokenDef> security,
			Holder<InfoType> info) throws ApiFault {
		validateRequest(security);
		ResultsResponseType response = new ResultsResponseType();
		List<DataQualityError> errors = errorRouteRetrieve.requestBody((Object) request.getId(), List.class);
		for (DataQualityError er : errors) {
			response.getErrors().add(DumpUtils.to(er));
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.unidata.mdm.dq.api.v5.ApplyDQImpl#applyDQ(com.unidata.mdm.dq.v5.
	 * ApplyDQRequest, javax.xml.ws.Holder, javax.xml.ws.Holder)
	 */
	@Override
	public ApplyDQResponse applyDQ(ApplyDQRequest request, Holder<SessionTokenDef> security, Holder<InfoType> info)
			throws ApiFault {
		MeasurementPoint.init(MeasurementContextName.MEASURE_DQ_SOAP_APPLY);
		MeasurementPoint.start();

		ApplyDQResponse response = new ApplyDQResponse();
		CopyOnWriteArrayList<DataQualityResultType> results = new CopyOnWriteArrayList<>();
		Date defaultStart = defaultStart(info.value.getEntityName());
		Date defaultEnd = defaultEnd(info.value.getEntityName());
		try {
			validateRequest(security);
			List<DQRecordType> records = request.getPayload().getRecord();
			response.setPayload(new Payload());
			List<DQRuleDef> rules = filterRules(info, request.getInfo());
			if (records.size() == 1) {
				// TODO: refactoring
				applyRule(request, info, results, defaultStart, defaultEnd, rules, records.get(0), false);
			} else {
				ForkJoinPool pool = new ForkJoinPool(getThreads());
				pool.submit(() -> records.parallelStream().forEach(r -> {
					// TODO: refactoring
					applyRule(request, info, results, defaultStart, defaultEnd, rules, r, false);

				})).get();
			}

			response.getPayload().withResult(results);
			response.setInfo(info.value);
		} catch (ApiFault e) {
			throw e;
		} catch (Exception e) {
			throw new ApiFault("Exception occured!",
                    new ApiFaultType().withErrorCode("Nested exception").withErrorMessage(e.getMessage()), e);
		} finally {
			MeasurementPoint.stop();
		}
		return response;
	}

	/**
	 * Validate request.
	 *
	 * @param security            the security
	 * @throws ApiFault the api fault
	 */
	private void validateRequest(Holder<SessionTokenDef> security) throws ApiFault {

		if (security.value == null || StringUtils.isEmpty(security.value.getToken())) {
			throw new ApiFault("Token not provided!",
					new ApiFaultType().withErrorCode("INVALID_TOKEN").withErrorMessage("Token not provided!"));
		}
		if (!tokens.getUnchecked(security.value.getToken()).isPresent()) {
			throw new ApiFault("Invalid!", new ApiFaultType().withErrorCode("INVALID_TOKEN")
					.withErrorMessage("Token not valid or already expired."));
		}
	}

	/**
	 * Apply rule.
	 *
	 * @param request            the request
	 * @param info            the info
	 * @param results the results
	 * @param defaultStart            the default start
	 * @param defaultEnd            the default end
	 * @param rules            the rules
	 * @param r            the r
	 * @param isPar            the is par
	 */
	private void applyRule(ApplyDQRequest request, Holder<InfoType> info,
			CopyOnWriteArrayList<DataQualityResultType> results, Date defaultStart, Date defaultEnd,
			List<DQRuleDef> rules, DQRecordType r, boolean isPar) {
		DataRecord record = DumpUtils.from(r);
		String entityName = info.value.getEntityName();

		// validate entity name and  supplied data consistency
		//Temporary disabled by request from RR
//		validationService.checkDataRecord(record, entityName);

		DQContext<DataRecord> context = new DQContext<>()
				.withEntityName(info.value.getEntityName())
				.withRecord(record)
				.withRules(rules)
				.withRecordValidFrom(
						r.getValidFrom() == null ? defaultStart : r.getValidFrom().toGregorianCalendar().getTime())
				.withRecordId(r.getId()).withRecordValidTo(
						r.getValidTo() == null ? defaultEnd : r.getValidTo().toGregorianCalendar().getTime());
		context.setOperationId(info.value.getRequestId());
		dqService.applyRules(context);
		if (request.getInfo().getMode() == DQApplyModeType.BATCH) {
			Map<String, Object> headers = new HashMap<>();
			headers.put("USER_NAME", String.class);
			headers.put("ENTITY_NAME", context.getEntityName());
			headers.put("RECORD_ID", context.getRecordId());
			headers.put("REQUEST_ID", context.getOperationId());
			errorRouteSave.sendBodyAndHeaders(context.getErrors(), headers);
		}
		DataQualityResultType result = new DataQualityResultType();
		if (!CollectionUtils.isEmpty(context.getErrors())) {
			result.getStatus().add(DQApplyStatusType.CONTAINS_ERRORS);
			result.getErrors().addAll(DumpUtils.to(context.getErrors()));

		} else {
			result.getStatus().add(DQApplyStatusType.VALID);
		}
		if (context.getRules().stream().anyMatch(dr -> dr.getType().contains(com.unidata.mdm.meta.DQRuleType.ENRICH))
				&& context.isModified()) {
			result.setRecord(DumpUtils.to(context.getRecord()));
			result.getStatus().add(DQApplyStatusType.ENRICHED);
		}
		result.setId(r.getId());
		results.add(result);
	}

	/**
	 * Filter rules.
	 *
	 * @param info
	 *            the info
	 * @param applyInfo
	 *            the apply info
	 * @return the list
	 */
	private List<DQRuleDef> filterRules(Holder<InfoType> info, DQApplyInfoType applyInfo) {
		List<DQRuleDef> toFilter = metamodelService.getLookupEntityById(info.value.getEntityName()) == null
				? metamodelService.getEntityByIdNoDeps(info.value.getEntityName()).getDataQualities()
				: metamodelService.getLookupEntityById(info.value.getEntityName()).getDataQualities();
		// if no info provided return all rules
		if (CollectionUtils.isEmpty(applyInfo.getRuleName()) && CollectionUtils.isEmpty(applyInfo.getApplicable())
				&& CollectionUtils.isEmpty(applyInfo.getSourceSystem())) {
			return toFilter;
		}
		// filter only by rule names if they are provided
		if (!CollectionUtils.isEmpty(applyInfo.getRuleName())) {
			return toFilter.stream().filter(r -> applyInfo.getRuleName().contains(r.getName()))
					.collect(Collectors.toList());
		}
		if (!CollectionUtils.isEmpty(applyInfo.getApplicable())) {
			return filterFor(applyInfo.getApplicable(), toFilter, applyInfo.getSourceSystem());
		}
		return toFilter;
	}

	/**
	 * Default start.
	 *
	 * @param entityName
	 *            the entity name
	 * @return the date
	 */
	private Date defaultStart(String entityName) {
		return metamodelService.getLookupEntityById(entityName) == null
				? defaultStart(metamodelService.getEntityByIdNoDeps(entityName).getValidityPeriod())
				: defaultStart(metamodelService.getLookupEntityById(entityName).getValidityPeriod());
	}

	/**
	 * Default start.
	 *
	 * @param boundary
	 *            the boundary
	 * @return the date
	 */
	private Date defaultStart(PeriodBoundaryDef boundary) {
		if (boundary == null || boundary.getStart() == null) {
			return null;
		}
		return boundary.getStart().toGregorianCalendar().getTime();
	}

	/**
	 * Default end.
	 *
	 * @param entityName
	 *            the entity name
	 * @return the date
	 */
	private Date defaultEnd(String entityName) {
		return metamodelService.getLookupEntityById(entityName) == null
				? defaultEnd(metamodelService.getEntityByIdNoDeps(entityName).getValidityPeriod())
				: defaultEnd(metamodelService.getLookupEntityById(entityName).getValidityPeriod());
	}

	/**
	 * Default end.
	 *
	 * @param boundary
	 *            the boundary
	 * @return the date
	 */
	private Date defaultEnd(PeriodBoundaryDef boundary) {
		if (boundary == null || boundary.getEnd() == null) {
			return null;
		}
		return boundary.getEnd().toGregorianCalendar().getTime();
	}

	/**
	 * Filter for.
	 *
	 * @param types
	 *            the types
	 * @param toFilter
	 *            the to filter
	 * @param ss
	 *            the ss
	 * @return the list
	 */
	private List<DQRuleDef> filterFor(List<DQRuleType> types, List<DQRuleDef> toFilter, List<String> ss) {
		List<DQRuleDef> result = new ArrayList<>();
		for (DQRuleDef dqr : toFilter) {
			if (types.contains(DQRuleType.ETALON) && dqr.getApplicable().contains(DQApplicableType.ETALON)) {
				result.add(dqr);
			} else if (types.contains(DQRuleType.ORIGIN) && dqr.getApplicable().contains(DQApplicableType.ORIGIN)) {
				List<String> sourceSystems = new ArrayList<>();
				if (dqr.getEnrich() != null && !StringUtils.isEmpty(dqr.getEnrich().getSourceSystem())) {
					sourceSystems.add(dqr.getEnrich().getSourceSystem());

				}

				if (dqr.getOrigins() != null && dqr.getOrigins().getSourceSystem() != null
						&& dqr.getOrigins().getSourceSystem().size() != 0) {
					List<DQRSourceSystemRef> dqssrs = dqr.getOrigins().getSourceSystem();
					for (DQRSourceSystemRef dqssr : dqssrs) {
						if (!StringUtils.isEmpty(dqssr.getName())) {
							sourceSystems.add(dqssr.getName());
						}
					}
				}
				if (dqr.getOrigins().isAll() || ss.contains("ALL") || CollectionUtils.isEmpty(ss)
						|| CollectionUtils.isEmpty(sourceSystems)) {
					result.add(dqr);
				} else {
					if (CollectionUtils.containsAny(sourceSystems, ss)) {
						result.add(dqr);
					}
				}
			}
		}
		return result;

	}

	/**
	 * Fill security params.
	 *
	 * @param token
	 *            the token
	 * @param userName
	 *            the user name
	 * @param password
	 *            the password
	 * @param details
	 *            the details
	 * @return the map
	 */
	private Map<AuthenticationSystemParameter, Object> fillSecurityParams(String token, String userName,
			String password, String details) {
		HttpServletRequest h = (HttpServletRequest) jaxwsContext.getMessageContext()
				.get(MessageContext.SERVLET_REQUEST);
		if (StringUtils.isEmpty(userName)) {
			userName = securityService.getUserByToken(token).getLogin();
		}
		Map<AuthenticationSystemParameter, Object> params = new EnumMap<>(AuthenticationSystemParameter.class);
		params.put(AuthenticationSystemParameter.PARAM_USER_NAME, userName);
		if (!StringUtils.isEmpty(password)) {
			params.put(AuthenticationSystemParameter.PARAM_USER_PASSWORD, password);
		}
		params.put(AuthenticationSystemParameter.PARAM_HTTP_SERVLET_REQUEST, h);
		params.put(AuthenticationSystemParameter.PARAM_CLIENT_IP, ClientIpUtil.clientIp(h));
		params.put(AuthenticationSystemParameter.PARAM_SERVER_IP, h.getLocalAddr());
		params.put(AuthenticationSystemParameter.PARAM_ENDPOINT, Endpoint.SOAP);
		params.put(AuthenticationSystemParameter.PARAM_DETAILS, details);
		return params;
	}

	/**
	 * The Class TokenCacheLoader.
	 */
	private class TokenCacheLoader extends CacheLoader<String, Optional<String>> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.common.cache.CacheLoader#load(java.lang.Object)
		 */
		@Override
		public Optional<String> load(String token) throws Exception {
			boolean isAuth = false;
			try {
				isAuth = securityService.authenticate(token, true);
			} catch (Throwable e) {
				isAuth = false;
			}
			return isAuth ? Optional.ofNullable(securityService.getUserByToken(token).getLogin()) : Optional.empty();
		}

	}

	/**
	 * Gets the threads.
	 *
	 * @return the threads
	 */
	public int getThreads() {
		if (this.threads == 0) {
			this.threads = Runtime.getRuntime().availableProcessors();
		}
		return threads;
	}

	/**
	 * Sets the threads.
	 *
	 * @param threads
	 *            the new threads
	 */
	public void setThreads(int threads) {
		this.threads = threads;
	}
}
