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
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.api.rest.dto.configuration.ConfigurationPropertyAvailableValuePO;
import com.unidata.mdm.backend.api.rest.dto.configuration.ConfigurationPropertyMetaPO;
import com.unidata.mdm.backend.api.rest.dto.configuration.ConfigurationPropertyRO;
import com.unidata.mdm.backend.common.dto.configuration.ConfigurationPropertyMetaDTO;
import com.unidata.mdm.backend.common.configuration.application.RuntimePropertiesService;
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
        return ok(
                runtimePropertiesService.availableProperties().stream()
                        .map(p -> {
                            final ConfigurationPropertyMetaDTO<?> meta = p.getMeta();
                            final Collection<ConfigurationPropertyAvailableValuePO> availableValues =
                                    meta.getAvailableValues().stream()
                                            .map(v -> new ConfigurationPropertyAvailableValuePO(v.getValue(), v.getDisplayValue()))
                                            .collect(Collectors.toCollection(ArrayList::new));
                            return new ConfigurationPropertyRO(
                                    meta.getName(),
                                    meta.getDisplayName(),
                                    meta.getGroupCode(),
                                    meta.getGroup(),
                                    meta.getType().value(),
                                    p.getValue(),
                                    new ConfigurationPropertyMetaPO(
                                            availableValues,
                                            meta.isRequired(),
                                            meta.isReadonly()
                                    )
                            );
                        })
                        .collect(Collectors.toList())
        );
    }

    @GET
    @Path("{name}")
    @ApiOperation(value = "вернуть список всех настроек для группы", response = Collection.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getPropertiesByName(@ApiParam(value = "Код группы")
                                        @PathParam("name")
                                                String name) {
        return ok(
                runtimePropertiesService.getPropertiesByGroup(name).stream()
                        .map(p -> {
                            final ConfigurationPropertyMetaDTO<?> meta = p.getMeta();
                            final Collection<ConfigurationPropertyAvailableValuePO> availableValues =
                                    meta.getAvailableValues().stream()
                                            .map(v -> new ConfigurationPropertyAvailableValuePO(v.getValue(), v.getDisplayValue()))
                                            .collect(Collectors.toCollection(ArrayList::new));
                            return new ConfigurationPropertyRO(
                                    meta.getName(),
                                    meta.getDisplayName(),
                                    meta.getGroupCode(),
                                    meta.getGroup(),
                                    meta.getType().value(),
                                    p.getValue(),
                                    new ConfigurationPropertyMetaPO(
                                            availableValues,
                                            meta.isRequired(),
                                            meta.isReadonly()
                                    )
                            );
                        })
                        .collect(Collectors.toList())
        );
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
