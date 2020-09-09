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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import com.unidata.mdm.backend.api.rest.converter.RolesConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.api.rest.dto.security.SecurityLabelRO;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.service.security.RoleServiceExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/security/label")
@Api(value = "label", description = "Манипуляции с метками", produces = "application/json")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class SecurityLabelRestService extends AbstractRestService {
    @Autowired
    private RoleServiceExt roleServiceExt;

    /**
     * Creates the.
     *
     * @param role
     *            the role
     * @return the response
     * @throws Exception
     *             the exception
     */
    @POST
    @ApiOperation(value = "Создание новой метки", notes = "", response = UpdateResponse.class)
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response createLabel(SecurityLabelRO label) throws Exception {
        roleServiceExt.createLabel(RolesConverter.convertSecurityLabelRO(label));

        return ok(new RestResponse<UpdateResponse>(new UpdateResponse(true, label.getName())));
    }

    /**
     * Update.
     *
     * @param roleName
     *            the role name
     * @param role
     *            the role
     * @return the response
     * @throws Exception
     *             the exception
     */
    @PUT
    @Path(value = "{labelName}")
    @ApiOperation(value = "Модификация существующей метки", notes = "", response = UpdateResponse.class)
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response updateLabel(@PathParam(value = "labelName") String labelName, SecurityLabelRO label)
            throws Exception {
        roleServiceExt.updateLabel(RolesConverter.convertSecurityLabelRO(label), labelName);
        return ok(new RestResponse<UpdateResponse>(new UpdateResponse(true, labelName)));
    }

    /**
     * Read.
     *
     * @param roleName
     *            the role name
     * @return the response
     * @throws Exception
     *             the exception
     */
    @GET
    @Path(value = "{labelName}")
    @ApiOperation(value = "Возвращает существующую метку", notes = "", response = SecurityLabelRO.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response read(@PathParam(value = "labelName") String labelName) throws Exception {
        SecurityLabel label = roleServiceExt.findLabel(labelName);
        return ok(new RestResponse<SecurityLabelRO>(RolesConverter.convertSecurityLabelDTO(label)));
    }

    /**
     * Delete.
     *
     * @param roleName
     *            the role name
     * @return the response
     * @throws Exception
     *             the exception
     */
    @DELETE
    @Path(value = "{labelName}")
    @ApiOperation(value = "Удаление метки", notes = "", response = UpdateResponse.class)
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response delete(@PathParam(value = "labelName") String labelName) throws Exception {
        roleServiceExt.deleteLabel(labelName);
        return ok(new RestResponse<UpdateResponse>(new UpdateResponse(true, labelName)));
    }
}
