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
import java.util.List;

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

import com.unidata.mdm.backend.api.rest.converter.UsersConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.api.rest.dto.security.UserPropertyRO;
import com.unidata.mdm.backend.api.rest.dto.security.UserRO;
import com.unidata.mdm.backend.api.rest.dto.security.UserWithPasswordRO;
import com.unidata.mdm.backend.common.dto.security.UserDTO;
import com.unidata.mdm.backend.common.dto.security.UserPropertyDTO;
import com.unidata.mdm.backend.common.service.UserService;
import com.unidata.mdm.backend.service.configuration.ConfigurationServiceExt;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class UserRestService.
 */
@Path("/" + RestConstants.PATH_PARAM_SECURITY  +"/" + RestConstants.PATH_PARAM_USER)
@Api(value = "user", description = "Манипуляции с пользователями", produces = "application/json")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class UserRestService extends AbstractRestService {

    /** The user service. */
    @Autowired
    private UserService userService;
    /**
     * Configuration service.
     */
    @Autowired
    private ConfigurationServiceExt configurationService;
    /**
     * Creates the.
     *
     * @param user
     *            the user
     * @return the response
     * @throws Exception
     *             the exception
     */
    @POST
    @ApiOperation(value = "Создание нового пользователя", notes = "", response = UpdateResponse.class)
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response create(final UserWithPasswordRO user) {
        userService.create(UsersConverter.convertUserRO(user));
        return ok(new RestResponse<>(new UpdateResponse(true, user.getLogin())));
    }

    /**
     * Update.
     *
     * @param login
     *            the login
     * @param user
     *            the user
     * @return the response
     * @throws Exception
     *             the exception
     */
    @PUT
    @Path(value = "{login}")
    @ApiOperation(value = "Модификация существующего пользователя", notes = "", response = UpdateResponse.class)
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response update(@PathParam(value = "login") String login, UserWithPasswordRO user) {
        userService.updateUser(login, UsersConverter.convertUserRO(user));
        return ok(new RestResponse<>(new UpdateResponse(true, user.getLogin())));
    }

    /**
     * Read.
     *
     * @param login
     *            the login
     * @return the response
     * @throws Exception
     *             the exception
     */
    @GET
    @Path(value = "{login}")
    @ApiOperation(value = "Возвращает существующего пользователя", notes = "", response = UserRO.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response read(@PathParam(value = "login") String login) throws Exception {
        final UserDTO userDTO = userService.getUserByName(login);
        final UserRO userRO = UsersConverter.convertUserDTO(userDTO);
        return ok(new RestResponse<>(userRO));
    }

    /**
     * Read all.
     *
     * @return the response
     * @throws Exception
     *             the exception
     */
    @GET
    @ApiOperation(value = "Возвращает всех пользователей", notes = "", response = List.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response readAll() {
        final List<UserDTO> userDTOs = userService.getAllUsers();
        final List<UserRO> result = UsersConverter.convertUserDTOs(userDTOs);
        result.forEach(s -> s.setProperties(new ArrayList<>()));
        return ok(new RestResponse<>(result));
    }

    /**
     *
     * @return
     * @throws Exception
     */
    @GET
    @Path("/auth-sources/list")
    @ApiOperation(value = "Возвращает все источники внешней аутентификации/авторизации/профайлов.", notes = "", response = List.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response loadAllAuthenticationSources() {
        return ok(new RestResponse<>(UsersConverter.convertSecurityDataSources(
                configurationService.getSecurityDataSources().values())));
    }

    /**
     *
     * @return
     * @throws Exception
     */
    @GET
    @Path("/user-properties/list")
    @ApiOperation(value = "Возвращает все свойства пользователей", notes = "", response = List.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response loadAllUserProperties() {
        return ok(new RestResponse<>(UsersConverter.convertPropertiesDtoToRo(userService.getAllProperties())));
    }
    /**
     *List with all available user apis(e.g. REST, SOAP, etc)
     * @return List with all available user apis(e.g. REST, SOAP, etc)
     * @throws Exception if something wrong happened.
     */
    @GET
    @Path("/user-api/list")
    @ApiOperation(value = "Возвращает все API пользователей", notes = "", response = List.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response loadAllUserAPIs() {
        return ok(new RestResponse<>(UsersConverter.convertAPIsDtoToRo(userService.getAPIList())));
    }
    /**
     *
     * @param userProperty
     * @return
     * @throws Exception
     */
    @PUT
    @Path("/user-properties/")
    @ApiOperation(value = "Сохранить новое свойство пользователя", notes = "", response = UserPropertyRO.class)
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response createUserProperty(final UserPropertyRO userProperty) {
        userProperty.setId(null);

        final UserPropertyDTO dto = UsersConverter.convertPropertyRoToDto(userProperty);
        userService.saveProperty(dto);
        return ok(new RestResponse<>(UsersConverter.convertPropertyDtoToRo(dto)));
    }

    /**
     *
     * @param userProperty
     * @return
     * @throws Exception
     */
    @PUT
    @Path("/user-properties/{userPropertyId}")
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()")
    @ApiOperation(value = "Редактировать свойство пользователя", notes = "", response = UserPropertyRO.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response saveUserProperty(@PathParam("userPropertyId") final Long userPropertyId,
                                     final UserPropertyRO userProperty) {

        final UserPropertyDTO dto = UsersConverter.convertPropertyRoToDto(userProperty);
        dto.setId(userPropertyId);
        userService.saveProperty(dto);
        return ok(new RestResponse<>(UsersConverter.convertPropertyDtoToRo(dto)));
    }

    /**
     *
     * @param userProperty
     * @return
     * @throws Exception
     */
    @DELETE
    @Path("/user-properties/{userPropertyId}")
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()")
    @ApiOperation(value = "Удалить свойство пользователя", notes = "", response = Boolean.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response removeUserProperty(@PathParam("userPropertyId") final Long userPropertyId) throws Exception {
        userService.deleteProperty(userPropertyId);
        return ok(new RestResponse<>(true));
    }
}
