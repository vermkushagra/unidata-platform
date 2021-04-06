package com.unidata.mdm.backend.api.rest;

import static com.unidata.mdm.backend.api.rest.converter.ReferenceInfoConverter.toRefInfos;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.unidata.mdm.backend.api.rest.constants.Constants;
import com.unidata.mdm.backend.api.rest.converter.LookupEntityDefToLookupEntityDefinitionConverter;
import com.unidata.mdm.backend.api.rest.converter.LookupEntityDefinitionToLookupEntityDefConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestPageRequest;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.meta.EntityInfoDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.LookupEntityDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.ReferenceInfo;
import com.unidata.mdm.backend.common.context.DeleteModelRequestContext.DeleteModelRequestContextBuilder;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.service.MetaDraftService;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.configuration.SwaggerJaxrsFilter;
import com.unidata.mdm.backend.service.registration.RegistrationService;
import com.unidata.mdm.backend.service.registration.keys.LookupEntityRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;
import com.unidata.mdm.backend.util.CollectionUtils;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.LookupEntityDef;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Michael Yashin. Created on 19.05.2015.
 */
@Path("meta/lookup-entities")
@Api(value = "meta_lookup-entities", description = "Метаданные справочников", produces = "application/json")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class LookupEntityRestService extends AbstractRestService {

	/**
	 * Meta model service.
	 */
	@Autowired
	private MetaModelService metaModelService;
	@Autowired
	private MetaDraftService metaDraftService;

	/**
	 * Conversion service.
	 */
	@Autowired
	private ConversionService conversionService;

	/**
	 * Search service.
	 */
	@Autowired
	private SearchService searchService;

	@Autowired
	private RegistrationService registrationService;

	/**
	 * Gets a list of lookup entities.
	 * 
	 * @param pageRequest
	 *            the page request
	 * @return list of entity info
	 */
	@GET
	@ApiOperation(value = "Список справочников", notes = "", response = List.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "Номер страницы, начинается с 1", defaultValue = "1", required = false, paramType = "query", dataType = "int"),
			@ApiImplicitParam(name = "size", value = "Размер страницы", defaultValue = ""
					+ Constants.REST_DEFAULT_PAGE_SIZE, required = false, paramType = "query", dataType = "int"),
			@ApiImplicitParam(name = "sort", value = "Параметры сортировки, URL-encoded JSON", required = false, paramType = "query", dataType = "string") })
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response findAll(
			@ApiParam(access = SwaggerJaxrsFilter.IGNORE_PARAM) @QueryParam("") RestPageRequest pageRequest, @QueryParam("draft") @DefaultValue("false") Boolean draft) {
		int offset = pageRequest.getPageRequest().getOffset();
		int pageSize = pageRequest.getPageRequest().getPageSize();
		return ok(elementsToPage(getLookupEntities(offset, pageSize, draft), pageRequest.getPageRequest()));
	}

	/**
	 * Gets a lookup entity by id (name).
	 * 
	 * @param id
	 *            the id (name)
	 * @return lookup entity
	 */
	@GET
	@Path("{id}")
	@ApiOperation(value = "Получить запись по ID", notes = "", response = LookupEntityRestResponse.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response getById(@PathParam("id") String id, @QueryParam("draft")@DefaultValue("false") Boolean draft) {
		LookupEntityDef entity ;
		if(draft) {
			entity = metaDraftService.getLookupEntityById(id);
		}else {
			entity = metaModelService.getLookupEntityById(id);
		}
		if (entity == null) {
			throw new BusinessException("Lookup entity not found", ExceptionId.EX_META_LOOKUP_ENTITY_NOT_FOUND);
		}
		LookupEntityDefinition response = LookupEntityDefToLookupEntityDefinitionConverter.convert(entity);
		LookupEntityRegistryKey registryKey = new LookupEntityRegistryKey(id);
		Set<UniqueRegistryKey> contains = registrationService.getContains(registryKey);
		List<ReferenceInfo> infos = contains.stream().filter(key -> key.keyType() == UniqueRegistryKey.Type.ATTRIBUTE)
				.map(key -> toRefInfos(registrationService.getReferencesTo(key), key)).flatMap(Collection::stream)
				.collect(Collectors.toList());
		infos.addAll(toRefInfos(registrationService.getReferencesTo(registryKey), registryKey));
		response.setEntityDependency(infos);
		response.setHasData(searchService.countAllIndexedRecords(entity.getName()) > 0);
		return ok(wrapEntity(response));
	}

	/**
	 * Creates a lookup entity.
	 * 
	 * @param lookupEntity
	 *            entity to create
	 * @return created entity
	 */
	@POST
	@ApiOperation(value = "Создать запись", notes = "", response = LookupEntityRestResponse.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response create(LookupEntityDefinition lookupEntity,
			@QueryParam("draft") @DefaultValue("false") Boolean draft) {
		if (draft) {
			metaDraftService.update(LookupEntityDefinitionToLookupEntityDefConverter.convert(lookupEntity));
		} else {
			metaModelService.upsertModel(LookupEntityDefinitionToLookupEntityDefConverter.convert(lookupEntity));
		}
		return ok(wrapEntity(lookupEntity));
	}

	/**
	 * Updates lookup entity.
	 * 
	 * @param lookupEntity
	 *            the entity
	 * @return updated entity
	 */
	@PUT
	@Path("{id}")
	@ApiOperation(value = "Обновить запись", notes = "На обновление присылается полная запись", response = LookupEntityRestResponse.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response update(@PathParam("id") String possiblyOldName, LookupEntityDefinition lookupEntity
			,@QueryParam("draft")@DefaultValue("false") Boolean draft) {
		if (draft) {
			metaDraftService.update(LookupEntityDefinitionToLookupEntityDefConverter.convert(lookupEntity));
		} else {
			metaModelService.upsertModel(LookupEntityDefinitionToLookupEntityDefConverter.convert(lookupEntity));
		}
		return ok(wrapEntity(lookupEntity));
	}

	/**
	 * Delete lookup entity.
	 * 
	 * @param id
	 *            id to delete
	 * @return 200 Ok
	 */
	@DELETE
	@Path("{id}")
	@ApiOperation(value = "Удалить запись", notes = "")
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response delete(@PathParam("id") String id, @QueryParam("draft") @DefaultValue("false") Boolean draft) {
		if (draft) {
			metaDraftService.remove(
					new DeleteModelRequestContextBuilder().lookupEntitiesIds(Collections.singletonList(id)).build());
		} else {
			metaModelService.deleteModel(
					new DeleteModelRequestContextBuilder().lookupEntitiesIds(Collections.singletonList(id)).build());
		}
		return ok("");
	}

	/**
	 * Gets lookup entities as list.
	 * 
	 * @param offset
	 *            offset
	 * @param pageSize
	 *            page size
	 * @return list of entities
	 */
	@SuppressWarnings("unchecked")
	private List<EntityInfoDefinition> getLookupEntities(int offset, int pageSize, boolean draft) {
		List<LookupEntityDef> entities;
		if(draft) {
			entities= metaDraftService.getLookupEntitiesList();
		}else {
			entities= metaModelService.getLookupEntitiesList();	
		}
	
		List<LookupEntityDef> list = CollectionUtils.safeSubList(entities, offset, offset + pageSize);

		List<EntityInfoDefinition> convertedList = (List<EntityInfoDefinition>) conversionService.convert(list,
				TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(AbstractEntityDef.class)),
				TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(EntityInfoDefinition.class)));

		return convertedList;
	}

	/**
	 * Gets a page of entities.
	 * 
	 * @param elements
	 *            the elements to put on page
	 * @param pageRequest
	 *            page request
	 * @return {@link Page}
	 */
	private Page<EntityInfoDefinition> elementsToPage(List<EntityInfoDefinition> elements, PageRequest pageRequest) {
		return new PageImpl<>(elements, pageRequest, elements.size());
	}

	private RestResponse<LookupEntityDefinition> wrapEntity(LookupEntityDefinition entityDefinition) {
		return new RestResponse<>(entityDefinition);
	}

	// DO NOT REMOVE! This crappy workaround required for Swagger to generate API
	// docs
	private static class LookupEntityRestResponse extends RestResponse<LookupEntityDefinition> {
		@Override
		public LookupEntityDefinition getContent() {
			return null;
		}
	}
}
