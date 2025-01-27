package com.unidata.mdm.meta.api.v5;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.Holder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.unidata.mdm.api.v5.SessionTokenDef;
import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext.UpdateModelRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.data.model.GetEntityDTO;
import com.unidata.mdm.backend.common.service.MetaDraftService;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.service.SecurityService;
import com.unidata.mdm.error_handling.v5.ApiFaultType;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.v5.ApplyDraftRequest;
import com.unidata.mdm.meta.v5.ApplyDraftResponse;
import com.unidata.mdm.meta.v5.DeleteElementRequest;
import com.unidata.mdm.meta.v5.DeleteElementResponse;
import com.unidata.mdm.meta.v5.GetElementRequest;
import com.unidata.mdm.meta.v5.GetElementResponse;
import com.unidata.mdm.meta.v5.GetModelRequest;
import com.unidata.mdm.meta.v5.GetModelResponse;
import com.unidata.mdm.meta.v5.MetaHeader;
import com.unidata.mdm.meta.v5.UpsertElementRequest;
import com.unidata.mdm.meta.v5.UpsertElementResponse;

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
	
	/** The tokens. */
	private LoadingCache<String, Optional<String>> tokens = CacheBuilder.newBuilder()
			.expireAfterWrite(60, TimeUnit.SECONDS).build(new TokenCacheLoader());

	/* (non-Javadoc)
	 * @see com.unidata.mdm.meta.api.v5.MetaImpl#applyDraft(com.unidata.mdm.meta.v5.ApplyDraftRequest, javax.xml.ws.Holder, javax.xml.ws.Holder)
	 */
	@Override
	public ApplyDraftResponse applyDraft(ApplyDraftRequest request, Holder<MetaHeader> info,
			Holder<SessionTokenDef> security) throws Fault {
		validateRequest(security);
		draftService.apply();
		ApplyDraftResponse response = new ApplyDraftResponse().withDraftId(request.getDraftId());
		return response;
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.meta.api.v5.MetaImpl#deleteElement(com.unidata.mdm.meta.v5.DeleteElementRequest, javax.xml.ws.Holder, javax.xml.ws.Holder)
	 */
	@Override
	public DeleteElementResponse deleteElement(DeleteElementRequest request, Holder<MetaHeader> info,
			Holder<SessionTokenDef> security) throws Fault {
		validateRequest(security);
		DeleteElementResponse response = new DeleteElementResponse();
		response.setElementName(request.getElementName());
		response.setElementType(request.getElementType());

		DeleteModelRequestContext.DeleteModelRequestContextBuilder builder = new DeleteModelRequestContext.DeleteModelRequestContextBuilder();
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
		DeleteModelRequestContext ctx = builder.build();
		if(info.value.isDraft()) {
			draftService.remove(ctx);
		}else {
			metamodelService.deleteModel(ctx);
		}
		
		return response;
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.meta.api.v5.MetaImpl#getElement(com.unidata.mdm.meta.v5.GetElementRequest, javax.xml.ws.Holder, javax.xml.ws.Holder)
	 */
	@Override
	public GetElementResponse getElement(GetElementRequest request, Holder<MetaHeader> info,
			Holder<SessionTokenDef> security) throws Fault {
		validateRequest(security);
		GetElementResponse response = new GetElementResponse();
		switch (request.getElementType()) {
		case ENTITY:
			GetEntityDTO entity = null;
			if(info.value.isDraft()) {
				entity = draftService.getEntityById(request.getElementName());
			}else {
				entity = metamodelService.getEntityById(request.getElementName());
			}
			if(entity!=null) {
				response.withEntity(ToSOAP.convert(entity.getEntity()));
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
			response.withLookupEntity(
					ToSOAP.convert(lookup));
			break;
		case RELATION:
			response.withRelation(
					ToSOAP.convert(metamodelService.getRelationById(request.getElementName())));
			break;
		default:
			break;
		}
		return response;
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.meta.api.v5.MetaImpl#getModel(com.unidata.mdm.meta.v5.GetModelRequest, javax.xml.ws.Holder, javax.xml.ws.Holder)
	 */
	@Override
	public GetModelResponse getModel(GetModelRequest request, Holder<MetaHeader> info, Holder<SessionTokenDef> security)
			throws Fault {
		validateRequest(security);
		GetModelResponse response = ToSOAP.convert(metamodelService.exportModel(null));
		return response;
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.meta.api.v5.MetaImpl#upsertElement(com.unidata.mdm.meta.v5.UpsertElementRequest, javax.xml.ws.Holder, javax.xml.ws.Holder)
	 */
	@Override
	public UpsertElementResponse upsertElement(UpsertElementRequest request, Holder<MetaHeader> info,
			Holder<SessionTokenDef> security) throws Fault {
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
		if(info.value.isDraft()) {
			draftService.update(builder.build());
		}else {
			metamodelService.upsertModel(builder.build());
		}
		UpsertElementResponse response = new UpsertElementResponse();
		response.setEntity(request.getEntity());
		response.setLookupEntity(request.getLookupEntity());
		response.setRelation(request.getRelation());
		return response;
	}

	/**
	 * Validate request.
	 *
	 * @param security the security
	 * @throws Fault the fault
	 */
	private void validateRequest(Holder<SessionTokenDef> security) throws Fault {

		if (security.value == null || StringUtils.isEmpty(security.value.getToken())) {
			throw new Fault("Token not provided!",
					new ApiFaultType().withErrorCode("INVALID_TOKEN").withErrorMessage("Token not provided!"));
		}
		if (!tokens.getUnchecked(security.value.getToken()).isPresent()) {
			throw new Fault("Invalid!", new ApiFaultType().withErrorCode("INVALID_TOKEN")
					.withErrorMessage("Token not valid or already expired."));

		}
		securityService.authenticate(security.value.getToken(), true);
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