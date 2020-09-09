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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.api.rest.dto.meta.SourceSystemDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.SourceSystemList;
import com.unidata.mdm.backend.common.context.DeleteModelRequestContext.DeleteModelRequestContextBuilder;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext.UpdateModelRequestContextBuilder;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.service.MetaDraftServiceExt;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.service.model.util.facades.AbstractModelElementFacade;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.MergeAttributeDef;
import com.unidata.mdm.meta.MergeSettingsDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.SourceSystemDef;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
/**
 * Source system service.
 *
 * @author Michael Yashin. Created on 19.05.2015.
 */
@Path("meta/source-systems")
@Api(value = "meta_source-systems", description = "Системы источники данных", produces = "application/json")
@Consumes({"application/json"})
@Produces({"application/json"})
public class SourceSystemRestService extends AbstractRestService {

    /**
     * The conversion service.
     */
    @Autowired
    ConversionService conversionService;
    /**
     * The metamodel service.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Meta draft service.
     */
    @Autowired
    private MetaDraftServiceExt metaDraftService;

    /**
     * Load and return list of all source systems.
     *
     * @return list of source systems. {@see SourceSystemList}.
     */
    @GET
    @ApiOperation(value = "вернуть список всех систем", notes = "", response = SourceSystemList.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response findAll(@QueryParam("draft")@DefaultValue("false") Boolean draft) {
     	List<SourceSystemDef> source;
    	if(draft) {
    		source = metaDraftService.getSourceSystemsList();

    	}else {
    		source = metaModelService.getSourceSystemsList();
    	}
        SourceSystemList target = conversionService.convert(source,
                SourceSystemList.class);
        target.setAdminSystemName(metaModelService.getAdminSourceSystem().getName());
        return ok(target);
    }

    /**
     * Creates new source system.
     *
     * @param req Source system definition.
     * @return HTTP response.
     */
    @POST
    @ApiOperation(value = "Создать новую систему", notes = "", response = UpdateResponse.class, responseContainer = "UpdateResponse")
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response create(final SourceSystemDefinition req, @QueryParam("draft")@DefaultValue("false") Boolean draft) {

        validateSourceSystemBeforeCreate(req);

        UpdateModelRequestContextBuilder updateModelRequestContextBuilder = new UpdateModelRequestContextBuilder();
        SourceSystemDef newSourceSystem = conversionService.convert(req, SourceSystemDef.class);

        AbstractModelElementFacade.validateCustomProperties(newSourceSystem.getCustomProperties());

        updateModelRequestContextBuilder.sourceSystemsUpdate(Collections.singletonList(newSourceSystem));

        // Update entity defs UN-3053
        List<EntityDef> entitiesForUpdate = buildEntitiesForCreateNewSourceSystem(newSourceSystem, metaDraftService.getEntitiesList());
        if (CollectionUtils.isNotEmpty(entitiesForUpdate)) {
            updateModelRequestContextBuilder.entityUpdate(entitiesForUpdate);

            List<NestedEntityDef> nestedEntityDefs = extractNestedEntities(entitiesForUpdate);
            if (CollectionUtils.isNotEmpty(nestedEntityDefs)) {
                updateModelRequestContextBuilder.nestedEntityUpdate(nestedEntityDefs);
            }
        }
        // Update lookup entity defs UN-3053
        List<LookupEntityDef> lookupEntitiesForUpdate = buildEntitiesForCreateNewSourceSystem(newSourceSystem, metaDraftService.getLookupEntitiesList());
        if (CollectionUtils.isNotEmpty(lookupEntitiesForUpdate)) {
            updateModelRequestContextBuilder.lookupEntityUpdate(lookupEntitiesForUpdate);
        }

        metaDraftService.update(updateModelRequestContextBuilder.build());
        // needed for sencha
        return Response.accepted(new UpdateResponse(true, req.getName())).build();
    }

    private List<NestedEntityDef> extractNestedEntities(List<EntityDef> entitiesForUpdate) {
        Map<String, NestedEntityDef> nestedEntitiesMap = new HashMap<>();
        entitiesForUpdate.forEach(entityDef -> {
            List<NestedEntityDef> nestedEntities = metaModelService.getNestedEntitiesByTopLevelId(entityDef.getName());
            if (CollectionUtils.isNotEmpty(nestedEntities)) {
                nestedEntities.forEach(nestedEntity -> nestedEntitiesMap.put(nestedEntity.getName(), nestedEntity));
            }
        });
        return new ArrayList<>(nestedEntitiesMap.values());
    }

    private void validateSourceSystemBeforeCreate(SourceSystemDefinition req) {
        //TODO: move validation from the rest service, currently not possible to distinguish update and create on metamodel level.
        SourceSystemList current = conversionService.convert(metaDraftService.getSourceSystemsList(),
                SourceSystemList.class);

        if (current.getSourceSystem().stream().anyMatch(s -> StringUtils.equals(req.getName(), s.getName()))) {
            throw new BusinessException("Source system with this name already exists!", ExceptionId.EX_META_SOURCE_SYSTEM_ALREADY_EXISTS);
        }
        //end todo
    }

    /**
     * get entity defs with custom merge settings for add new sourceSystem
     *
     * @param newSourceSystem new source system
     * @param entityDefList   current entity defs list
     * @return entity defs list with added new source system
     */
    private <T extends AbstractEntityDef> List<T> buildEntitiesForCreateNewSourceSystem(SourceSystemDef newSourceSystem, List<T> entityDefList) {
        List<T> result = new ArrayList<>();
        for (T entityDef : entityDefList) {
            if (entityDef.getMergeSettings() != null) {
                boolean needUpdate = false;

                MergeSettingsDef mergeSettings = entityDef.getMergeSettings();
                if (CollectionUtils.isNotEmpty(mergeSettings.getBvrSettings().getSourceSystemsConfigs())
                        && mergeSettings.getBvrSettings().getSourceSystemsConfigs()
                        .stream()
                        .noneMatch(sourceSystemDef -> sourceSystemDef.getName().equals(newSourceSystem.getName()))) {
                    entityDef.getMergeSettings().getBvrSettings().getSourceSystemsConfigs().add(newSourceSystem);
                    needUpdate = true;
                }

                if (mergeSettings.getBvtSettings() != null
                        && CollectionUtils.isNotEmpty(mergeSettings.getBvtSettings().getAttributes())) {
                    for (MergeAttributeDef mergeAttributeDef : mergeSettings.getBvtSettings().getAttributes()) {
                        if (CollectionUtils.isNotEmpty(mergeAttributeDef.getSourceSystemsConfigs())
                                && mergeAttributeDef.getSourceSystemsConfigs()
                                .stream()
                                .noneMatch(sourceSystemDef -> sourceSystemDef.getName().equals(newSourceSystem.getName()))) {
                            mergeAttributeDef.getSourceSystemsConfigs().add(newSourceSystem);
                            needUpdate = true;
                        }
                    }
                }

                if (needUpdate) {
                    result.add(entityDef);
                }
            }
        }
        return result;
    }

    /**
     * Delete existing source system.
     *
     * @param sourceSystemName the source system name
     * @return HTTP response.
     */
    @DELETE
    @Path("{sourceSystemName}")
    @ApiOperation(value = "Удалить существующую систему", notes = "", response = String.class, responseContainer = "String")
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response delete(@PathParam(value = "sourceSystemName") String sourceSystemName, @QueryParam("draft")@DefaultValue("false") Boolean draft) {
        DeleteModelRequestContextBuilder deleteModelRequestContextBuilder = new DeleteModelRequestContextBuilder();
        deleteModelRequestContextBuilder.sourceSystemIds(Collections.singletonList(sourceSystemName)).build();
        metaDraftService.remove(deleteModelRequestContextBuilder.build());
        return Response.ok().build();
    }

    /**
     * Updates existing source system.
     *
     * @param sourceSystemName the source system name
     * @param req              Updated source system. {@see SourceSystemDefinition}
     * @return HTTP response.
     */
    @PUT
    @Path("{sourceSystemName}")
    @ApiOperation(value = "Обновить существующую систему", notes = "", response = String.class, responseContainer = "String")
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response update(@PathParam("sourceSystemName") String sourceSystemName, SourceSystemDefinition req, @QueryParam("draft")@DefaultValue("false") Boolean draft) {

        UpdateModelRequestContextBuilder updateModelRequestContextBuilder = new UpdateModelRequestContextBuilder();
        final SourceSystemDef sourceSystemDef = conversionService.convert(req, SourceSystemDef.class);

        AbstractModelElementFacade.validateCustomProperties(sourceSystemDef.getCustomProperties());

        updateModelRequestContextBuilder.sourceSystemsUpdate(Collections.singletonList(sourceSystemDef));

        if (!StringUtils.equals(sourceSystemName, req.getName())) {
            DeleteModelRequestContextBuilder deleteModelRequestContextBuilder = new DeleteModelRequestContextBuilder();
            deleteModelRequestContextBuilder.sourceSystemIds(Collections.singletonList(sourceSystemName)).build();
            metaDraftService.remove(deleteModelRequestContextBuilder.build());
        }

        metaDraftService.update(updateModelRequestContextBuilder.build());
        // needed for sencha
        return Response.accepted(new UpdateResponse(true, req.getName())).build();
    }

    /**
     * Gets existing source system by name.
     *
     * @param sourceSystemName the source system name
     * @return Source system definition. {@see SourceSystemDefinition}
     */
    @GET
    @Path("{sourceSystemName}")
    @ApiOperation(value = "Вернуть существующую систему", notes = "", response = SourceSystemDefinition.class, responseContainer = "String")
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response get(@PathParam(value = "sourceSystemName") String sourceSystemName, @QueryParam("draft")@DefaultValue("false") Boolean draft) {
    	List<SourceSystemDef> source;
    	if(draft) {
    		source = metaDraftService.getSourceSystemsList();
    	}else {
    		source = metaModelService.getSourceSystemsList();
    	}
        SourceSystemList target = conversionService.convert(source,
                SourceSystemList.class);
        SourceSystemDefinition result = new SourceSystemDefinition();
        target.getSourceSystem().stream().filter(e -> StringUtils.equals(sourceSystemName, e.getName())).forEach(e -> {
            result.setDescription(e.getDescription());
            result.setName(e.getName());
            result.setWeight(e.getWeight());
            result.setCustomProperties(e.getCustomProperties());
        });
        return Response.ok(result).build();
    }
}
