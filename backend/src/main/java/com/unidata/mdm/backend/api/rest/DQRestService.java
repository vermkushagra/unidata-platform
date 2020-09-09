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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("data/dq")
@Api(value = "data_dq", description = "Правила качества", produces = "application/json")
@Consumes({ "application/json" })
@Produces({ "application/json" })
/**
 * Data quality rest service.
 * 
 * @author ilya.bykov
 *
 */
@Deprecated
public class DQRestService extends AbstractRestService {

	/**
	 * Re-apply data quality rules to the all records of the given
	 * registry/lookup.
	 * 
	 * @param entityName
	 *            entity name(registry or lookup)
	 * @return re-apply result.
	 */
	@POST
	@ApiOperation(value = "Переприменение правил качества данных", notes = "", response = Object.class, responseContainer = "Object")
	@Path(value = "/reapply/{entityName}")
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 202, message = "Request accepted"), @ApiResponse(code = 401, message = "Unathorized"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response reapply(@PathParam(value = "entityName") String entityName) {

		return Response.accepted().build();
	}

	/**
	 * Perform duplicates search for the given registry/lookup name.
	 * 
	 * @param entityName
	 *            registry/lookup name.
	 * @return result of search.
	 */
	@POST
	@ApiOperation(value = "Поиск дубликатов", notes = "", response = Object.class, responseContainer = "Object")
	@Path(value = "/duplicates/{entityName}")
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 202, message = "Request accepted"), @ApiResponse(code = 401, message = "Unathorized"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response duplicates(@PathParam(value = "entityName") String entityName) {

		return Response.accepted().build();
	}
}
