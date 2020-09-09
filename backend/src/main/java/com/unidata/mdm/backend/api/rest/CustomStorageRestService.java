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

import com.unidata.mdm.backend.api.rest.converter.CustomStorageRecordsConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.data.EtalonRecordRO;
import com.unidata.mdm.backend.api.rest.dto.settings.CustomStorageRecordRO;
import com.unidata.mdm.backend.common.dto.CustomStorageRecordDTO;
import com.unidata.mdm.backend.common.runtime.MeasurementContextName;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.service.settings.CustomStorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * rest service for work with custom statistic
 */
@Path("custom-storage")
@Api(value = "custom-storage", description = "Произвольные настройки", produces = "application/json")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class CustomStorageRestService extends AbstractRestService {

    private static final String PATH_KEY = "key";

    private static final String PATH_USER_NAME = "user_name";

    private static final String PARAM_KEY = "key";

    private static final String PARAM_USER_NAME = "user_name";

    /** The stat service. */
    @Autowired
    private CustomStorageService customStorageService;

    /**
     * Create a records
     *
     * @param customStorageRecords records list
     * @return true if success, else false
     */
    @POST
    @ApiOperation(value = "Создать записи", notes = "", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response create(@ApiParam("Список записей для сохранения") List<CustomStorageRecordRO> customStorageRecords) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_CREATE);
        MeasurementPoint.start();
        try {
            boolean result = customStorageService.createRecords(CustomStorageRecordsConverter.convertToDTO(customStorageRecords));
            RestResponse<EtalonRecordRO> response = new RestResponse<>(null, result);
            return ok(response);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Update records
     *
     * @param customStorageRecords records list
     * @return true if success, else false
     */
    @PUT
    @ApiOperation(value = "Обновить записи", notes = "", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response update(@ApiParam("Список записей для обновления")List<CustomStorageRecordRO> customStorageRecords) {
        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_CREATE);
        MeasurementPoint.start();
        try {
            boolean result = customStorageService.updateRecords(CustomStorageRecordsConverter.convertToDTO(customStorageRecords));
            RestResponse<EtalonRecordRO> response = new RestResponse<>(null, result);
            return ok(response);
        } finally {
            MeasurementPoint.stop();
        }
    }



    /**
     * Delete list of custom storage records by user name
     * @param userName user name
     * @return true if success, else false
     * @throws Exception if something went wrong
     */
    @DELETE
    @ApiOperation(value = "Удалить параметры по имени пользователя", notes = "", response = RestResponse.class)
    @Path(PARAM_USER_NAME + "/{" + PARAM_USER_NAME + "}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response deleteByUserName(@ApiParam("Имя пользователя")  @PathParam(PARAM_USER_NAME) String userName) {

        boolean result = customStorageService.deleteRecordsByUserName(userName);
        RestResponse<List<CustomStorageRecordRO>> response = new RestResponse<>(null, result);
        return ok(response);
    }

    /**
     * Delete list of custom storage records by key
     * @param key user name
     * @return true if success, else false
     * @throws Exception if something went wrong
     */
    @DELETE
    @ApiOperation(value = "Удалить параметры по ключу", notes = "", response = RestResponse.class)
    @Path(PATH_KEY + "/{" + PARAM_KEY + "}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response deleteByKey(@ApiParam("Ключ") @PathParam(PARAM_KEY) String key) {

        boolean result = customStorageService.deleteRecordsByKey(key);
        RestResponse<List<CustomStorageRecordRO>> response = new RestResponse<>(null, result);
        return ok(response);
    }

    /**
     * Delete list of custom storage records
     * @param customStorageRecords records for delete
     * @return true if success, else false
     * @throws Exception if something went wrong
     */
    @DELETE
    @ApiOperation(value = "Удалить список заданнных параметров", notes = "", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response deleteRecords(@ApiParam("Список записей для удаления")List<CustomStorageRecordRO> customStorageRecords) {

        boolean result = customStorageService.deleteRecords(CustomStorageRecordsConverter.convertToDTO(customStorageRecords));
        RestResponse<EtalonRecordRO> response = new RestResponse<>(null, result);
        return ok(response);
    }

    /**
     * Get list of custom storage records by key
     * @param key key
     * @return list of custom storage records
     * @throws Exception if something went wrong
     */
    @GET
    @ApiOperation(value = "Получить параметры по ключу", notes = "", response = RestResponse.class)
    @Path(PATH_KEY + "/{" + PARAM_KEY + "}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getParametersByKey(@ApiParam("Ключ") @PathParam(PARAM_KEY) String key) {
        List<CustomStorageRecordDTO> customSettingsList = customStorageService.getRecordsByKey(key);
        RestResponse<List<CustomStorageRecordRO>> response =
                new RestResponse<>(CustomStorageRecordsConverter.convertToRO(customSettingsList), true);
        return ok(response);
    }

    /**
     * Get list of custom storage records by user name
     * @param userName user name
     * @return list of custom storage records
     * @throws Exception if something went wrong
     */
    @GET
    @ApiOperation(value = "Получить параметры по имени пользователя", notes = "", response = RestResponse.class)
    @Path(PATH_USER_NAME + "/{" + PARAM_USER_NAME + "}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getParametersByUserName(@ApiParam("Имя пользователя")  @PathParam(PARAM_USER_NAME) String userName) {
        List<CustomStorageRecordDTO> customSettingsList = customStorageService.getRecordsByUserName(userName);
        RestResponse<List<CustomStorageRecordRO>> response =
                new RestResponse<>(CustomStorageRecordsConverter.convertToRO(customSettingsList), true);
        return ok(response);
    }

    /**
     * Get list of custom storage records by user name
     * @param userName user name
     * @param key key
     * @return list of custom storage records
     * @throws Exception if something went wrong
     */
    @GET
    @ApiOperation(value = "Получить параметры по ключу и имени пользователя", notes = "", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getParametersByKeyAndUserName(@ApiParam("Ключ") @QueryParam(PARAM_KEY) String key,
                                                  @ApiParam("Имя пользователя") @QueryParam(PARAM_USER_NAME) String userName) {
        List<CustomStorageRecordDTO> customSettingsList = customStorageService.getRecordsByKeyAndUser(key, userName);
        RestResponse<List<CustomStorageRecordRO>> response =
                new RestResponse<>(CustomStorageRecordsConverter.convertToRO(customSettingsList), true);
        return ok(response);
    }

}