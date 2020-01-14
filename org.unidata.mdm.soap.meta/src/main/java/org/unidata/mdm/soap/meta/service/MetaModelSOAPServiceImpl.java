package org.unidata.mdm.soap.meta.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.unidata.mdm.meta.service.MetaDraftService;
import org.unidata.mdm.meta.v1.ObjectFactory;
import org.unidata.mdm.core.service.SecurityService;
import org.unidata.mdm.error_handling.v1.ApiFaultType;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.NestedEntityDef;
import org.unidata.mdm.meta.api.v1.Fault;
import org.unidata.mdm.meta.api.v1.MetaImpl;
import org.unidata.mdm.meta.context.DeleteModelRequestContext;
import org.unidata.mdm.meta.context.UpdateModelRequestContext.UpdateModelRequestContextBuilder;
import org.unidata.mdm.meta.dto.GetEntityDTO;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.v1.ApplyDraftRequest;
import org.unidata.mdm.meta.v1.ApplyDraftResponse;
import org.unidata.mdm.meta.v1.DeleteElementRequest;
import org.unidata.mdm.meta.v1.DeleteElementResponse;
import org.unidata.mdm.meta.v1.GetElementRequest;
import org.unidata.mdm.meta.v1.GetElementResponse;
import org.unidata.mdm.meta.v1.GetEntityWithDepsRequest;
import org.unidata.mdm.meta.v1.GetEntityWithDepsResponse;
import org.unidata.mdm.meta.v1.GetModelRequest;
import org.unidata.mdm.meta.v1.GetModelResponse;
import org.unidata.mdm.meta.v1.MetaHeader;
import org.unidata.mdm.meta.v1.UpsertElementRequest;
import org.unidata.mdm.meta.v1.UpsertElementResponse;
import org.unidata.mdm.security.v1.SessionTokenDef;
import org.unidata.mdm.soap.meta.util.ToInternal;
import org.unidata.mdm.soap.meta.util.ToSOAP;
import org.unidata.mdm.system.type.runtime.MeasurementContextName;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

import javax.xml.ws.Holder;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Class MetaModelSOAPServiceImpl.
 */
public class MetaModelSOAPServiceImpl extends MetaImpl {

	/** The metamodel service. */
	@Autowired
	private MetaModelService metamodelService;

	/** The security service. */
	@Autowired
	private SecurityService securityService;

	/** The draft service. */
	@Autowired
	private MetaDraftService draftService;

	private ObjectFactory factory = new ObjectFactory();

	/** The tokens. */
	private LoadingCache<String, Optional<String>> tokens = CacheBuilder.newBuilder()
			.expireAfterWrite(60, TimeUnit.SECONDS).build(new TokenCacheLoader());

	/* (non-Javadoc)
	 * @see com.unidata.mdm.meta.api.v5.MetaImpl#applyDraft(com.unidata.mdm.meta.v5.ApplyDraftRequest, javax.xml.ws.Holder, javax.xml.ws.Holder)
	 */
	@Override
	public ApplyDraftResponse applyDraft(
			ApplyDraftRequest request,
			Holder<MetaHeader> info,
			Holder<SessionTokenDef> security
	) throws Fault {

	    MeasurementPoint.init(MeasurementContextName.MEASURE_SOAP_APPLY_MODEL_DRAFT);
	    MeasurementPoint.start();
	    try {

	        validateRequest(security);
    		draftService.apply();
    		return new ApplyDraftResponse()
    		        .withDraftId(request.getDraftId());
	    } finally {
            MeasurementPoint.stop();
        }
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.meta.api.v5.MetaImpl#deleteElement(com.unidata.mdm.meta.v5.DeleteElementRequest, javax.xml.ws.Holder, javax.xml.ws.Holder)
	 */
	@Override
	public DeleteElementResponse deleteElement(
			DeleteElementRequest request,
			Holder<MetaHeader> info,
			Holder<SessionTokenDef> security
	) throws Fault {

        MeasurementPoint.init(MeasurementContextName.MEASURE_SOAP_DELETE_MODEL_ELEMENT);
        MeasurementPoint.start();
        try {

            validateRequest(security);
            DeleteElementResponse response = new DeleteElementResponse();
            response.setElementName(request.getElementName());
            response.setElementType(request.getElementType());

            DeleteModelRequestContext.DeleteModelRequestContextBuilder builder =
                    new DeleteModelRequestContext.DeleteModelRequestContextBuilder();

            switch (request.getElementType()) {
                case ENTITY:
                    builder.entitiesIds(Collections.singletonList(request.getElementName()));
                    break;
                case LOOKUP:
                    builder.lookupEntitiesIds(Collections.singletonList(request.getElementName()));
                    break;
                case NESTED_ENTITY:
                    builder.nestedEntiesIds(Collections.singletonList(request.getElementName()));
                    break;
                case RELATION:
                    builder.relationIds(Collections.singletonList(request.getElementName()));
                    break;
                default:
                    break;
            }
            builder.draft(info.value.isDraft());

            DeleteModelRequestContext ctx = builder.build();

            metamodelService.deleteModel(ctx);

            return response;
        } finally {
            MeasurementPoint.stop();
        }
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public GetEntityWithDepsResponse getEntityWithDeps(
    		GetEntityWithDepsRequest request,
			Holder<MetaHeader> info,
			Holder<SessionTokenDef> security
	) throws Fault {

        MeasurementPoint.init(MeasurementContextName.MEASURE_SOAP_GET_ENTITY_WITH_DEPS);
        MeasurementPoint.start();
        try {

            validateRequest(security);

            GetEntityWithDepsResponse response = factory.createGetEntityWithDepsResponse();
            GetEntityDTO dto = null;

            if (info.value.isDraft()) {
                dto = draftService.getEntityById(request.getEntityName());
            } else {
                dto = metamodelService.getEntityById(request.getEntityName());
            }

            if (Objects.nonNull(dto) && Objects.nonNull(dto.getEntity())) {

                response
                    .withEntity(ToSOAP.convert(dto.getEntity()))
                    .withNestedEntity(Stream.of(dto.getRefs())
                            .filter(CollectionUtils::isNotEmpty)
                            .flatMap(Collection::stream)
                            .map(ToSOAP::convert)
                            .collect(Collectors.toList()))
                    .withRelation(Stream.of(dto.getRelations())
                            .filter(CollectionUtils::isNotEmpty)
                            .flatMap(Collection::stream)
                            .map(ToSOAP::convert)
                            .collect(Collectors.toList()));

            }

            return response;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /* (non-Javadoc)
	 * @see com.unidata.mdm.meta.api.v5.MetaImpl#getElement(com.unidata.mdm.meta.v5.GetElementRequest, javax.xml.ws.Holder, javax.xml.ws.Holder)
	 */
	@Override
	public GetElementResponse getElement(
			GetElementRequest request,
			Holder<MetaHeader> info,
			Holder<SessionTokenDef> security
	) throws Fault {

	    MeasurementPoint.init(MeasurementContextName.MEASURE_SOAP_GET_MODEL_ELEMENT);
        MeasurementPoint.start();
        try {

    		validateRequest(security);
    		GetElementResponse response = new GetElementResponse();
    		switch (request.getElementType()) {
    		case ENTITY:
    			EntityDef entity;
    			if(info.value.isDraft()) {
    				entity = draftService.getEntityByIdNoDeps(request.getElementName());
    			}else {
    				entity = metamodelService.getEntityByIdNoDeps(request.getElementName());
    			}
    			if(entity!=null) {
    				response.withEntity(ToSOAP.convert(entity));
    			}
    			break;
    		case NESTED_ENTITY:
    			NestedEntityDef nestedEntity = null;
    			if(info.value.isDraft()) {
    				nestedEntity = draftService.getNestedEntityById(request.getElementName());
    			}else {
    				nestedEntity = metamodelService.getNestedEntityByNoDeps(request.getElementName());
    			}
    			if(nestedEntity!=null) {
    				response.withNestedEntity(ToSOAP.convert(nestedEntity));
    			}
    			break;
    		case LOOKUP:
    			LookupEntityDef lookup = null;
    			if(info.value.isDraft()) {
    				lookup = draftService.getLookupEntityById(request.getElementName());
    			}else {
    				lookup = metamodelService.getLookupEntityById(request.getElementName());
    			}
    			response.withLookupEntity(ToSOAP.convert(lookup));
    			break;
    		case RELATION:
    			response.withRelation(ToSOAP.convert(metamodelService.getRelationById(request.getElementName())));
    			break;
    		default:
    			break;
    		}
    		return response;
        } finally {
            MeasurementPoint.stop();
        }
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.meta.api.v5.MetaImpl#getModel(com.unidata.mdm.meta.v5.GetModelRequest, javax.xml.ws.Holder, javax.xml.ws.Holder)
	 */
	@Override
	public GetModelResponse getModel(GetModelRequest request, Holder<MetaHeader> info, Holder<SessionTokenDef> security)
			throws Fault {

	    MeasurementPoint.init(MeasurementContextName.MEASURE_SOAP_GET_MODEL);
        MeasurementPoint.start();
        try {
    		validateRequest(security);
    		return ToSOAP.convert(metamodelService.exportEmptyModel());
        } finally {
            MeasurementPoint.stop();
        }
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.meta.api.v5.MetaImpl#upsertElement(com.unidata.mdm.meta.v5.UpsertElementRequest, javax.xml.ws.Holder, javax.xml.ws.Holder)
	 */
	@Override
	public UpsertElementResponse upsertElement(
			UpsertElementRequest request,
			Holder<MetaHeader> info,
			Holder<SessionTokenDef> security
	) throws Fault {

	    MeasurementPoint.init(MeasurementContextName.MEASURE_SOAP_UPSERT_MODEL_ELEMENT);
        MeasurementPoint.start();
        try {

    		validateRequest(security);
    		UpdateModelRequestContextBuilder builder = new UpdateModelRequestContextBuilder();
    		if (request.getEntity() != null) {
    			builder.entityUpdate(
    					Collections.singletonList(ToInternal.convert(request.getEntity())));
    		} else if (request.getLookupEntity() != null) {
    			builder.lookupEntityUpdate(Collections
    					.singletonList(ToInternal.convert(request.getLookupEntity())));
    		} else if (request.getRelation() != null) {
    			builder.relationsUpdate(Collections
    					.singletonList(ToInternal.convert(request.getRelation())));
    		} else if (request.getEnumeration() != null) {
    			builder.enumerationsUpdate(Collections
    					.singletonList(ToInternal.convert(request.getEnumeration())));
    		} else if(request.getNestedEntity()!=null) {
    			builder.nestedEntityUpdate(Collections
    					.singletonList(ToInternal.convert(request.getNestedEntity())));
    		}

    		builder.draft(info.value.isDraft());

			metamodelService.upsertModel(builder.build());

    		UpsertElementResponse response = new UpsertElementResponse();
    		response.setEntity(request.getEntity());
    		response.setLookupEntity(request.getLookupEntity());
    		response.setRelation(request.getRelation());
    		return response;
        } finally {
            MeasurementPoint.stop();
        }
	}

	/**
	 * Validate request.
	 *
	 * @param security the security
	 * @throws Fault the fault
	 */
	private void validateRequest(Holder<SessionTokenDef> security) throws Fault {

	    MeasurementPoint.start();
	    try {
    		if (security.value == null || StringUtils.isEmpty(security.value.getToken())) {
    			throw new Fault("Token not provided!",
    					new ApiFaultType().withErrorCode("INVALID_TOKEN").withErrorMessage("Token not provided!"));
    		}
    		if (!tokens.getUnchecked(security.value.getToken()).isPresent()) {
    			throw new Fault("Invalid!", new ApiFaultType().withErrorCode("INVALID_TOKEN")
    					.withErrorMessage("Token not valid or already expired."));

    		}
    		securityService.authenticate(security.value.getToken(), true);
	    } finally {
            MeasurementPoint.stop();
        }
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
}