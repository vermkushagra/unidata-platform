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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.unidata.mdm.backend.util.ClientIpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.logging.LogEntry;
import com.unidata.mdm.backend.service.logging.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Rest service used for logging client errors.
 */
@Path("data/logging")
@Api(value = "data_logging", description = "Логирование ошибок клиента", produces = "application/json")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class LoggingRestService extends AbstractRestService {
    /** The logging service. */
    @Autowired
    @Qualifier(value = "restClientCLogger")
    private LogService loggingService;

    /**
     * Log single client error.
     *
     * @param logEntry
     *            the log entrie
     * @return the response
     */

    @POST
    @Path("logSingle")
    @ApiOperation(value = "Логирование еденичной записи", notes = "Присылается еденичная запись", response = String.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response log(LogEntry logEntry) {
        // set client ip address
        logEntry.setIpAddress(ClientIpUtil.clientIp(getHSR()));
        // log message with delay
        // loggingService.logDelayed(logEntrie,
        // Collections.emptyList().toArray());
        loggingService.logImmediate(logEntry, Collections.emptyList().toArray());
        return Response.accepted().build();
    }

    /**
     * Log client errors bulk mode.
     *
     * @param logEntries
     *            the log entries
     * @return the response
     */
    @POST
    @Path("logMultiple")
    @ApiOperation(value = "Логирование нескольких записей", notes = "Присылается список записей", response = String.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response log(List<LogEntry> logEntries) {
        if (logEntries != null && logEntries.size() != 0) {
            // set client ip address to log entrie
            logEntries.forEach(entrie -> entrie.setIpAddress(ClientIpUtil.clientIp(getHSR())));
            // loggingService.logDelayed(logEntries,
            // Collections.emptyList().toArray());
            loggingService.logImmediate(logEntries, Collections.emptyList().toArray());
        }
        return Response.accepted().build();
    }
}
