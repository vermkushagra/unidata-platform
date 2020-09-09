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

package com.unidata.mdm.backend.api.rest;

import static com.unidata.mdm.backend.api.rest.converter.ReferenceInfoConverter.toRefInfos;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
import com.unidata.mdm.backend.api.rest.converter.EntityDefinitionConverter;
import com.unidata.mdm.backend.api.rest.converter.EntityDefinitionToEntityDefConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestPageRequest;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.meta.EntityDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.EntityInfoDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.ReferenceInfo;
import com.unidata.mdm.backend.common.context.DeleteModelRequestContext.DeleteModelRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.data.model.GetEntityDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.service.MetaDraftServiceExt;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.configuration.SwaggerJaxrsFilter;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import com.unidata.mdm.backend.service.registration.RegistrationService;
import com.unidata.mdm.backend.service.registration.keys.EntityRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;
import com.unidata.mdm.backend.util.CollectionUtils;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.EntityDef;

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
@Path(EntityRestService.SERVICE_PATH)
@Api(value = "meta_entities", description = "Метаданные реестров", produces = "application/json")
@Consumes({"application/json"})
@Produces({"application/json"})
public class EntityRestService extends AbstractRestService {

    /**
     * Service path.
     */
    public static final String SERVICE_PATH = "meta/entities";

    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelService metaModelService;
    @Autowired
    private MetaDraftServiceExt metaDraftService;

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

    @Autowired
    private RelationsServiceComponent relationsServiceComponent;

    /**
     * Gets entities in paged fashion with defaults.
     * @param pageRequest the page request
     * @return list of entities
     * @throws Exception
     */
    @GET
    @ApiOperation(value = "Список справочников", notes = "", response = List.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "Номер страницы, начинается с 1", defaultValue = "1", required = false, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "Размер страницы", defaultValue = "" + Constants.REST_DEFAULT_PAGE_SIZE, required = false, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "Параметры сортировки, URL-encoded JSON", required = false, paramType = "query", dataType = "string")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response findAll(
            @ApiParam(access = SwaggerJaxrsFilter.IGNORE_PARAM)
            @QueryParam("") RestPageRequest pageRequest,
            @QueryParam("draft") @DefaultValue("false") Boolean draft
    ) {

        int offset = pageRequest.getPageRequest().getOffset();
        int pageSize = pageRequest.getPageRequest().getPageSize();
        return ok(elementsToPage(getEntities(offset, pageSize, draft), pageRequest.getPageRequest()));
    }

    /**
     * Gets an entity by id.
     * @param id entity id
     * @return entity
     * @throws Exception if something went wrong
     */
    @GET
    @Path("{id}")
    @ApiOperation(value = "Получить запись по ID", notes = "", response = EntityRestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getById(@PathParam("id") String id, @QueryParam("draft") @DefaultValue("false") Boolean draft ) {
		GetEntityDTO entity;
		if (draft) {
			entity = metaDraftService.getEntityById(id);
		} else {
			entity = metaModelService.getEntityById(id);
		}
		if (Objects.isNull(entity)||entity.getEntity() == null) {
			throw new BusinessException("Entity not found", ExceptionId.EX_META_ENTITY_NOT_FOUND);
		}
        EntityDefinition response = EntityDefinitionConverter.convert(entity);
        EntityRegistryKey registryKey = new EntityRegistryKey(id);
        Set<UniqueRegistryKey> contains = registrationService.getContains(registryKey);
        List<ReferenceInfo> infos = contains.stream()
                                            .filter(key -> key.keyType() == UniqueRegistryKey.Type.ATTRIBUTE)
                                            .map(key -> toRefInfos(registrationService.getReferencesTo(key), key))
                                            .flatMap(Collection::stream)
                                            .collect(Collectors.toList());
        response.setEntityDependency(infos);
        response.setHasData(searchService.countAllIndexedRecords(response.getName()) > 0);
        response.getRelations()
                .forEach(rel -> rel.setHasData(relationsServiceComponent.checkExistDataByRelName(rel.getName())));
        return ok(wrapEntity(response));
    }

    /**
     * Creates a new entity.
     * @param entity new entity
     * @return the entity just created
     * @throws Exception if something went wrong
     */
    @POST
    @ApiOperation(value = "Создать запись", notes = "", response = EntityRestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
	public Response create(EntityDefinition entity, @QueryParam("draft") @DefaultValue("false") Boolean draft) {

        if (draft) {
			metaDraftService.update(EntityDefinitionToEntityDefConverter.convert(entity));
		} else {
			metaModelService.upsertModel(EntityDefinitionToEntityDefConverter.convert(entity));
		}

		return getById(entity.getName(), draft);
	}

    /**
     * Updates an entity definition.
     * @param entity the entity
     * @return updated entity
     * @throws Exception
     */
    @PUT
    @Path("{id}")
    @ApiOperation(value = "Обновить запись", notes = "На обновление присылается полная запись", response = EntityRestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
	public Response update(@PathParam("id") String possiblyOldName, EntityDefinition entity,
			@QueryParam("draft") @DefaultValue("false") Boolean draft) {
		if (draft) {
			metaDraftService.update(EntityDefinitionToEntityDefConverter.convert(entity));
		} else {
			metaModelService.upsertModel(EntityDefinitionToEntityDefConverter.convert(entity));
		}

		return getById(entity.getName(), draft);
	}

    /**
     * Deletes an entity.
     * @param id entity id
     * @return empty string
     * @throws Exception
     */
    @DELETE
    @Path("{id}")
    @ApiOperation(value = "Удалить запись", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
	public Response delete(@PathParam("id") String id, @QueryParam("draft") @DefaultValue("false") Boolean draft) {
		if (draft) {
			metaDraftService
					.remove(new DeleteModelRequestContextBuilder().entitiesIds(Collections.singletonList(id)).build());
		} else {
			metaModelService.deleteModel(
					new DeleteModelRequestContextBuilder().entitiesIds(Collections.singletonList(id)).build());
		}
		return ok("");
	}

    /**
     * Gets entities as list.
     * @param offset offset
     * @param pageSize page size
     * @return list of entities
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private List<EntityInfoDefinition> getEntities(int offset, int pageSize, boolean draft){
        List<EntityDef> entities;
        if(draft) {
        	entities = metaDraftService.getEntitiesList();
        }else {
        	entities = metaModelService.getEntitiesList();
        }
        List<EntityDef> list = CollectionUtils.safeSubList(
                entities,
                offset,
                offset + pageSize);

        List<EntityInfoDefinition> convertedList = (List<EntityInfoDefinition>) conversionService.convert(list,
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(AbstractEntityDef.class)),
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(EntityInfoDefinition.class)));

        return convertedList;
    }

    /**
     * Gets a page of entities.
     * @param elements the elements to put on page
     * @param pageRequest page request
     * @return {@link Page}
     */
    private Page<EntityInfoDefinition> elementsToPage(List<EntityInfoDefinition> elements, PageRequest pageRequest) {
        return new PageImpl<EntityInfoDefinition>(elements, pageRequest, elements.size());
    }

    private RestResponse<EntityDefinition> wrapEntity(EntityDefinition entityDefinition) {
        return new RestResponse<>(entityDefinition);
    }

    //DO NOT REMOVE! This crappy workaround required for Swagger to generate API docs
    private static class EntityRestResponse extends RestResponse<EntityDefinition> {
        @Override
        public EntityDefinition getContent() {
            return null;
        }
    }
}
