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

/**
 *
 */
package com.unidata.mdm.backend.api.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.api.rest.converter.UserEventConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.security.UserEventRO;
import com.unidata.mdm.backend.common.runtime.MeasurementContextName;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.service.UserService;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Mikhail Mikhailov
 * User notifications REST service.
 */
@Path("/" + RestConstants.PATH_PARAM_USER)
@Api(value = RestConstants.PATH_PARAM_USER,
     description = "Манипуляции с событиями пользователя",
     produces = MediaType.APPLICATION_JSON)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class UserNotificationsRestService extends AbstractRestService {

    /** The user service. */
    @Autowired
    private UserService userService;

    /**
     * Constructor.
     */
    public UserNotificationsRestService() {
        super();
    }

    /**
     * Gets user notifications.
     * @param dateAsString
     * @return
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_NOTIFICATIONS + "{p:/?}{"
              + RestConstants.DATA_PARAM_DATE  + ": " + RestConstants.DEFAULT_TIMESTAMP_PATTERN +  "}")
    @ApiOperation(value = "Возвращает все возможные user properties", notes = "", response = List.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response getUserNotifications(
            @ApiParam(value = "Дата ограничения") @PathParam(RestConstants.DATA_PARAM_DATE) String dateAsString) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_GET_USER_NOTIFICATIONS);
        MeasurementPoint.start();
        try {

            String login = SecurityUtils.getCurrentUserName();
            Date asOf = ValidityPeriodUtils.parse(dateAsString);
            ArrayList<UserEventRO> result = new ArrayList<>();
            UserEventConverter.to(userService.getUserEvents(login, asOf, -1, -1), result);

            return Response.ok(new RestResponse<>(result)).build();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Count user notifications.
     * @return
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_NOTIFICATIONS + "/count")
    @ApiOperation(value = "Возвращает все возможные user properties", notes = "", response = List.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response getUserNotifications() {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_COUNT_USER_NOTIFICATIONS);
        MeasurementPoint.start();
        try {

            String login = SecurityUtils.getCurrentUserName();
            return Response.ok(new RestResponse<>(userService.countUserEvents(login))).build();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Deletes user notification.
     * @param id the event id
     * @return response
     */
    @DELETE
    @Path("/" + RestConstants.PATH_PARAM_NOTIFICATIONS + "/{" + RestConstants.DATA_PARAM_ID  + "}")
    @ApiOperation(value = "Удаляет возможные user properties по идентификатору", notes = "", response = List.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response deleteUserNotification(@PathParam(RestConstants.DATA_PARAM_ID) String id) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_DELETE_USER_NOTIFICATION);
        MeasurementPoint.start();
        try {
            return Response.ok(new RestResponse<>(userService.deleteUserEvent(id))).build();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Deletes all notifications for current user.
     * @return response
     */
    @DELETE
    @Path("/" + RestConstants.PATH_PARAM_NOTIFICATIONS)
    @ApiOperation(value = "Удаляет все пользовательские события.", notes = "", response = List.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response deleteAllUserNotifications() {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_DELETE_ALL_USER_NOTIFICATIONS);
        MeasurementPoint.start();
        try {
            return Response.ok(new RestResponse<>(userService.deleteAllEventsForCurrentUser(null)))
                    .build();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Deletes notifications by specific IDs.
     * @return response
     */
    @POST
    @Path("/" + RestConstants.PATH_PARAM_NOTIFICATIONS + "/idsdelete")
    @ApiOperation(value = "Удаляет выбранные пользовательские события.", notes = "", response = List.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response deleteUserNotificationsByIds(List<String> ids) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_DELETE_SELECTED_USER_NOTIFICATIONS);
        MeasurementPoint.start();
        try {
            return Response.ok(new RestResponse<>(userService.deleteUserEvents(ids))).build();
        } finally {
            MeasurementPoint.stop();
        }
    }
}
