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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.service.maintenance.MaintenanceService;
import com.unidata.mdm.backend.service.maintenance.dto.SystemMode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Aleksandr Magdenko
 */
@Path("/healthcheck")
@Api(value = "healthcheck", description = "Проверка доступности", produces = "application/json")
@Produces({MediaType.APPLICATION_JSON})
public class HealthCheckRestService extends AbstractRestService {
    /** The maintenance service. */
    @Autowired
    private MaintenanceService maintenanceService;

    @GET
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public HealthStatus healthCheck() {
        SystemMode currentMode = maintenanceService.checkCurrent();

        return new HealthStatus("OK", currentMode);
    }

    private static class HealthStatus {
        private String status;
        private SystemMode systemMode;

        HealthStatus(String status, SystemMode systemMode) {
            this.status = status;
            this.systemMode = systemMode;
        }

        public String getStatus() {
            return status;
        }

        public SystemMode getSystemMode() {
            return systemMode;
        }
    }
}
