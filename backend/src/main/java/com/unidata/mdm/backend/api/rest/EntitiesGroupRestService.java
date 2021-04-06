package com.unidata.mdm.backend.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.api.rest.converter.EntitiesGroupToFlatGroupMappingConverter;
import com.unidata.mdm.backend.api.rest.converter.FlatGroupMappingToEntitiesGroupDefConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.meta.FlatGroupMapping;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.dto.data.model.GetEntitiesGroupsDTO;
import com.unidata.mdm.backend.common.service.MetaDraftService;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.meta.EntitiesGroupDef;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Group rest service
 */
@Path("meta/entitiesGroup")
@Api(value = "meta_entities-group", description = "Группы справочников и реестров", produces = "application/json")
@Consumes({"application/json"})
@Produces({"application/json"})
public class EntitiesGroupRestService extends AbstractRestService {

    /**
     * The metamodel service.
     */
    @Autowired
    private MetaModelService metaModelService;
    @Autowired
    private MetaDraftService metaDraftService;

    /**
     * Load and return list of all entities group
     *
     * @return list of source systems. {@see FlatGroupMapping}.
     */
    @GET
    @ApiOperation(value = "Вернуть список всех групп, начиная с корня", notes = "Присылается древовидная структура", response = FlatGroupMapping.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response findAll(@QueryParam("filled") @DefaultValue("false") boolean filled, @QueryParam("draft")@DefaultValue("false") Boolean draft ) {
    	GetEntitiesGroupsDTO result;
    	if(draft) {
    		result = metaDraftService.getEntitiesGroups();
    	}else {
    		result = metaModelService.getEntitiesGroups();
    	}
        if (filled) {
            FlatGroupMapping target = EntitiesGroupToFlatGroupMappingConverter.convertToFullFilledFlatGroup(result);
            return ok(new RestResponse<>(target));
        } else {
            FlatGroupMapping target = EntitiesGroupToFlatGroupMappingConverter.convertToFlatGroup(result);
            return ok(new RestResponse<>(target));
        }
    }


    /**
     * Updates entities group.
     *
     * @param flatGroupMapping all necessary info about group.
     * @return 200 Ok
     */
    @PUT
    @ApiOperation(value = "Обновить группу")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response update(FlatGroupMapping flatGroupMapping, @QueryParam("draft")@DefaultValue("false") Boolean draft ) {
        EntitiesGroupDef entitiesGroupDef = FlatGroupMappingToEntitiesGroupDefConverter.convert(flatGroupMapping);
        UpdateModelRequestContext context = new UpdateModelRequestContext.UpdateModelRequestContextBuilder()
                .entitiesGroupsUpdate(entitiesGroupDef).build();
        metaDraftService.update(context);
        return ok(new RestResponse<>());
    }
}
