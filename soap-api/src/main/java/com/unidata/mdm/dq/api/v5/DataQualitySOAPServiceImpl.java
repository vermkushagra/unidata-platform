/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.dq.api.v5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

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
import com.unidata.mdm.backend.common.configuration.PlatformConfiguration;
import com.unidata.mdm.backend.common.context.DataQualityContext;
import com.unidata.mdm.backend.common.data.ModificationBox;
import com.unidata.mdm.backend.common.dq.DataQualityExecutionMode;
import com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.runtime.MeasurementContextName;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.security.Endpoint;
import com.unidata.mdm.backend.common.service.DataQualityService;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.service.SecurityService;
import com.unidata.mdm.backend.common.service.ValidationService;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.DataShift;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.OriginRecordInfoSection;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.impl.OriginRecordImpl;
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
import com.unidata.mdm.util.ClientIpUtil;


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

	@Autowired
    private PlatformConfiguration platformConfiguration;

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
				applyRule(request, info, results, defaultStart, defaultEnd, rules, records.get(0));
			} else {
				ForkJoinPool pool = new ForkJoinPool(getThreads());
				pool.submit(() -> records.parallelStream().forEach(r -> {
					// TODO: refactoring
					applyRule(request, info, results, defaultStart, defaultEnd, rules, r);

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
	 */
	private void applyRule(ApplyDQRequest request, Holder<InfoType> info,
			CopyOnWriteArrayList<DataQualityResultType> results, Date defaultStart, Date defaultEnd,
			List<DQRuleDef> rules, DQRecordType r) {

	    Date dt = new Date();
        OriginRecord origin = new OriginRecordImpl()
                .withDataRecord(DumpUtils.from(r))
                .withInfoSection(new OriginRecordInfoSection()
                        .withApproval(ApprovalState.APPROVED)
                        .withCreateDate(dt)
                        .withMajor(platformConfiguration.getPlatformMajor())
                        .withMinor(platformConfiguration.getPlatformMinor())
                        .withOriginKey(OriginKey.builder()
                                .entityName(info.value.getEntityName())
                                .externalId(r.getId())
                                .sourceSystem(metamodelService.getAdminSourceSystem().getName())
                                .status(RecordStatus.ACTIVE)
                                .build())
                        .withShift(DataShift.PRISTINE)
                        .withStatus(RecordStatus.ACTIVE)
                        .withUpdateDate(dt)
                        .withValidFrom(r.getValidFrom() == null ? defaultStart : r.getValidFrom().toGregorianCalendar().getTime())
                        .withValidTo(r.getValidTo() == null ? defaultEnd : r.getValidTo().toGregorianCalendar().getTime()));

        DataQualityContext dCtx = DataQualityContext.builder()
                .rules(rules)
                .entityName(origin.getInfoSection().getOriginKey().getEntityName())
                .sourceSystem(origin.getInfoSection().getOriginKey().getSourceSystem())
                .externalId(origin.getInfoSection().getOriginKey().getExternalId())
                .validFrom(origin.getInfoSection().getValidFrom())
                .validTo(origin.getInfoSection().getValidTo())
                .modificationBox(ModificationBox.of(Collections.emptyList(), origin))
                .executionMode(DataQualityExecutionMode.MODE_ONLINE)
                .build();

        dCtx.setOperationId(info.value.getRequestId());
        dqService.apply(dCtx);

		// validate entity name and  supplied data consistency
		//Temporary disabled by request from RR
//		validationService.checkDataRecord(record, entityName);

		if (request.getInfo().getMode() == DQApplyModeType.BATCH && CollectionUtils.isNotEmpty(dCtx.getErrors())) {
			Map<String, Object> headers = new HashMap<>();
			headers.put("USER_NAME", String.class);
			headers.put("ENTITY_NAME", dCtx.getEntityName());
			headers.put("RECORD_ID", dCtx.getExternalId());
			headers.put("REQUEST_ID", dCtx.getOperationId());
			errorRouteSave.sendBodyAndHeaders(dCtx.getErrors(), headers);
		}

		DataQualityResultType result = new DataQualityResultType();
		result.withSkippedRules(dCtx.getSkippedRules());
		if (!CollectionUtils.isEmpty(dCtx.getErrors())) {
			result.getStatus().add(DQApplyStatusType.CONTAINS_ERRORS);
			result.getErrors().addAll(DumpUtils.to(dCtx.getErrors()));

		} else {
			result.getStatus().add(DQApplyStatusType.VALID);
		}
		
		

		boolean isModified = dCtx.getModificationBox().count(ModificationBox.toBoxKey(dCtx)) > 1;
		if (dCtx.getRules().stream().anyMatch(dr -> dr.getType().contains(com.unidata.mdm.meta.DQRuleType.ENRICH)) && isModified) {
		    result.setRecord(DumpUtils.to(dCtx.getModificationBox().pop(ModificationBox.toBoxKey(dCtx)).getValue()));
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
        List<DQRuleDef> selected = null;
        if (CollectionUtils.isEmpty(applyInfo.getRuleName())
         && CollectionUtils.isEmpty(applyInfo.getApplicable())
         && CollectionUtils.isEmpty(applyInfo.getSourceSystem())) {
            selected = toFilter;
        // filter only by rule names if they are provided
        } else if (CollectionUtils.isNotEmpty(applyInfo.getRuleName())) {
            selected = toFilter.stream().filter(r -> applyInfo.getRuleName().contains(r.getName()))
                    .collect(Collectors.toList());
        } else if (CollectionUtils.isNotEmpty(applyInfo.getApplicable())) {
            selected = filterFor(applyInfo.getApplicable(), toFilter, applyInfo.getSourceSystem());
        }

        // reorder
        if (CollectionUtils.isNotEmpty(selected)) {

            List<DQRuleDef> result = new ArrayList<>(selected.size());
            selected.stream()
                .filter(rule -> rule.getApplicable().contains(DQApplicableType.ORIGIN))
                .sorted(Comparator.comparing(DQRuleDef::getOrder))
                .collect(Collectors.toCollection(() -> result));

            selected.stream()
                .filter(rule -> rule.getApplicable().contains(DQApplicableType.ETALON))
                .sorted(Comparator.comparing(DQRuleDef::getOrder))
                .collect(Collectors.toCollection(() -> result));

            selected = result;
        }

        return selected;
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

	    boolean selectEtalonPhaseRules = types.contains(DQRuleType.ETALON);
        boolean selectOriginPhaseRules = types.contains(DQRuleType.ORIGIN);

        Set<DQRuleDef> result = new LinkedHashSet<>(toFilter.size());
        for (DQRuleDef dqr : toFilter) {

            if (selectEtalonPhaseRules && dqr.getApplicable().contains(DQApplicableType.ETALON)) {
                result.add(dqr);
                continue;
            }

            if (selectOriginPhaseRules && dqr.getApplicable().contains(DQApplicableType.ORIGIN)) {

                if (dqr.getOrigins().isAll() || CollectionUtils.isEmpty(ss) || ss.contains("ALL")) {
                    result.add(dqr);
                    continue;
                }

                Set<String> sourceSystems = Stream.concat(
                    dqr.getEnrich() != null && !StringUtils.isEmpty(dqr.getEnrich().getSourceSystem())
                        ? Stream.of(dqr.getEnrich().getSourceSystem())
                        : Stream.empty(),
                    !dqr.getOrigins().getSourceSystem().isEmpty()
                        ? dqr.getOrigins().getSourceSystem().stream().map(DQRSourceSystemRef::getName).filter(Objects:: nonNull)
                        : Stream.empty())
                        .collect(Collectors.toSet());

                boolean hasMatch = sourceSystems.stream().anyMatch(ss::contains);
                if (hasMatch) {
                    result.add(dqr);
                }
            }
        }

        return new ArrayList<>(result);
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
