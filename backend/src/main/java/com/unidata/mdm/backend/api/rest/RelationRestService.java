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

import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.api.rest.converter.EntitiesDefFilteredByRelationSideConverter;
import com.unidata.mdm.backend.api.rest.converter.RelationDefConverter;
import com.unidata.mdm.backend.api.rest.converter.RelationDefinitionConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.api.rest.dto.meta.EntityDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.RelationDefinition;
import com.unidata.mdm.backend.common.context.DeleteModelRequestContext.DeleteModelRequestContextBuilder;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext.UpdateModelRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.data.model.GetEntitiesByRelationSideDTO;
import com.unidata.mdm.backend.common.service.MetaDraftServiceExt;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.types.RelationSide;
import com.unidata.mdm.meta.RelationDef;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Relation rest service
 */
@Path("meta/relations")
@Api(value = "meta_relations", description = "Список связей", produces = "application/json")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class RelationRestService extends AbstractRestService {

    /** The meta model service. */
    @Autowired
    private MetaModelService metaModelService;
    @Autowired
    private MetaDraftServiceExt metaDraftService;

    /**
     * Find all.
     *
     * @return the response
     */
    @GET
    @ApiOperation(value = "Список связей", notes = "", response = RelationDefinition.class, responseContainer = "List")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response findAll(@QueryParam("draft")@DefaultValue("false") Boolean draft) {
    	List<RelationDef> result;
    	if(draft) {
    		result = metaDraftService.getRelationsList();
    	}else {
    		result = metaModelService.getRelationsList();
    	}
        return ok(RelationDefConverter.convert(result));
    }

    /**
     * Gets entities and their relations view by from side.
     * @param entityName entity name
     * @return result
     * @throws Exception
     */
    @GET
    @Path("from/{name}")
    @ApiOperation(value = "Список связей где сущность - правая сторона (to)",
        notes = "",
        response = RelationDefinition.class,
        responseContainer = "List")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response getFrom(@PathParam("name") String entityName,@QueryParam("draft")@DefaultValue("false") Boolean draft) throws Exception {

        GetEntitiesByRelationSideDTO result;
        if (draft) {
        	result = metaDraftService.getEntitiesFilteredByRelationSide(entityName, RelationSide.FROM);
        } else {
        	result = metaModelService.getEntitiesFilteredByRelationSide(entityName, RelationSide.FROM);
        }
        return ok(new RestResponse<>(EntitiesDefFilteredByRelationSideConverter.convert(result)));
    }

    @GET
    @Path("to/{name}")
    @ApiOperation(value = "Список связей где сущность - левая сторона (from)",
        notes = "",
        response = RelationDefinition.class,
        responseContainer = "List")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response getTo(@PathParam("name") String entityName,@QueryParam("draft")@DefaultValue("false")  Boolean draft) throws Exception {

        GetEntitiesByRelationSideDTO result;
        if(draft) {
        	result= metaDraftService.getEntitiesFilteredByRelationSide(entityName, RelationSide.TO);
        }else {
        	result= metaModelService.getEntitiesFilteredByRelationSide(entityName, RelationSide.TO);
        }

        return ok(new RestResponse<>(EntitiesDefFilteredByRelationSideConverter.convert(result)));
    }

    /**
     * Upsert.
     *
     * @param relations
     *            the relations
     * @param name
     *            the name
     * @return the response
     */
    @PUT
    @Path("{name}")
    @ApiOperation(value = "Добавить новые/обновить существующие", notes = "", response = UpdateResponse.class, responseContainer = "List")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response upsert(List<RelationDefinition> relations, @PathParam("name") String name,@QueryParam("draft")@DefaultValue("false")  Boolean draft){
        metaDraftService.update(new UpdateModelRequestContextBuilder()
                .relationsUpdate(RelationDefinitionConverter.convert(relations))
                .build());
        return ok(new UpdateResponse(true, name));
    }

    /**
     * Delete.
     *
     * @param name
     *            the name
     * @return the response
     */
    @DELETE
    @Path("{name}")
    @ApiOperation(value = "Удалить запись", notes = "")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response delete(@PathParam("name") String name,@QueryParam("draft")@DefaultValue("false")  Boolean draft){
    	metaDraftService.remove(new DeleteModelRequestContextBuilder()
                .relationIds(Collections.singletonList(name)).build());
        return Response.accepted().build();
    }
}
