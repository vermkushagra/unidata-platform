package com.unidata.mdm.backend.api.rest;

import java.util.Collection;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.configuration.application.RuntimePropertiesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;

@Path("configuration")
@Api(value = "configuration", description = "API для управления конфигурацие приложения", produces = "application/json")
@Consumes({"application/json"})
@Produces({"application/json"})
public class ConfigurationRestService extends AbstractRestService {

    private final RuntimePropertiesService runtimePropertiesService;

    @Autowired
    public ConfigurationRestService(RuntimePropertiesService runtimePropertiesService) {
        this.runtimePropertiesService = runtimePropertiesService;
    }

    @GET
    @ApiOperation(value = "вернуть список всех настроек", response = Collection.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response availableProperties() {
        return ok(runtimePropertiesService.availableProperties());
    }

    @PUT

    @ApiOperation(value = "Обновить настройки приложения", response = UpdateResponse.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response upsert(Map<String, String> properties) {
        return ok(runtimePropertiesService.updatePropertiesValuesFromExternalPlace(properties));
    }
}
