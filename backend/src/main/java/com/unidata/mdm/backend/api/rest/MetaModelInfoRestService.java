package com.unidata.mdm.backend.api.rest;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.SingleValueObject;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.meta.DQRRaiseDef;
import com.unidata.mdm.meta.DQRuleDef;
import com.unidata.mdm.meta.SimpleAttributesHolderEntityDef;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Nikolaev Yaroslav
 *         info meta REST service.
 */
@Path(MetaModelInfoRestService.PATH)
@Api(value = "infoMeta", description = "Входная точка для получения информации о мета данных", produces = "application/json")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class MetaModelInfoRestService extends AbstractRestService {

    /**
     * path
     */
    public static final String PATH = "info/meta";

    /**
     * Meta model service
     */
    @Autowired
    private MetaModelService metaModelService;

    /**
     * Get all user defined dq categories
     *
     * @return categories
     */
    @GET
    @Path("/dq/categories")
    @ApiOperation(value = "Все определенные пользователем dq категории", notes = "", response = RestResponse.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
                          @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response dqCategories(@QueryParam("entityName") String entityName) {
        SimpleAttributesHolderEntityDef entity = metaModelService.getEntityByIdNoDeps(entityName);
        SimpleAttributesHolderEntityDef lookupEntity = metaModelService.getLookupEntityById(entityName);
        List<DQRuleDef> dqRuleDefList = emptyList();
        if (entity != null) {
            dqRuleDefList = entity.getDataQualities();
        }
        if (lookupEntity != null) {
            dqRuleDefList = lookupEntity.getDataQualities();
        }
        Collection<SingleValueObject> categories = dqRuleDefList.stream()
                                                                .filter(Objects::nonNull)
                                                                .filter(dq -> !dq.isSpecial())
                                                                .map(DQRuleDef::getRaise)
                                                                .filter(Objects::nonNull)
                                                                .map(DQRRaiseDef::getCategoryText)
                                                                .filter(Objects::nonNull)
                                                                .distinct()
                                                                .map(SingleValueObject::new)
                                                                .collect(Collectors.toList());
        return Response.ok(new RestResponse<>(categories)).build();
    }

    /**
     * Get all user defined dq categories
     *
     * @return categories
     */
    @GET
    @Path("/dq/names")
    @ApiOperation(value = "Все определенные пользователем dq имена", notes = "", response = RestResponse.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
                          @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response dqNames(@QueryParam("entityName") String entityName) {

        SimpleAttributesHolderEntityDef entity = metaModelService.getEntityByIdNoDeps(entityName);
        SimpleAttributesHolderEntityDef lookupEntity = metaModelService.getLookupEntityById(entityName);
        List<DQRuleDef> dqRuleDefList = emptyList();
        if (entity != null) {
            dqRuleDefList = entity.getDataQualities();
        }
        if (lookupEntity != null) {
            dqRuleDefList = lookupEntity.getDataQualities();
        }
        Collection<SingleValueObject> names = dqRuleDefList.stream()
                                                           .filter(Objects::nonNull)
                                                           .filter(dq -> !dq.isSpecial())
                                                           .map(DQRuleDef::getName)
                                                           .filter(Objects::nonNull)
                                                           .distinct()
                                                           .map(SingleValueObject::new)
                                                           .collect(Collectors.toList());
        return Response.ok(new RestResponse<>(names)).build();
    }

    //todo switch UI to new endpoint for dashboard visible.
}
