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
import java.util.Map;
import java.util.stream.Collectors;

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

import com.unidata.mdm.backend.api.rest.converter.DataRecordEtalonConverter;
import com.unidata.mdm.backend.api.rest.converter.SearchRequestConverters;
import com.unidata.mdm.backend.api.rest.converter.SearchResultToRestSearchResultConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.data.EtalonRecordRO;
import com.unidata.mdm.backend.api.rest.dto.sandbox.DeleteSandboxRecordRO;
import com.unidata.mdm.backend.api.rest.dto.sandbox.RunDQRulesRO;
import com.unidata.mdm.backend.api.rest.dto.sandbox.RunDQRulesResultRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchComplexRO;
import com.unidata.mdm.backend.common.context.RunDQRulesContext;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRecordInfoSection;
import com.unidata.mdm.backend.common.types.impl.EtalonRecordImpl;
import com.unidata.mdm.backend.service.data.sandbox.DQSandboxService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("dq-sandbox")
@Api(
        value = "dq-sandbox",
        description = "Работа с тестовыми данными для правил качества данных",
        produces = "application/json"
)
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class DQSandboxRestService extends AbstractRestService {

    private static final String RECORD_ID_PATH_PARAM = "recordId";
    @Autowired
    private DQSandboxService dqSandboxService;

    @POST
    @Path("record")
    @ApiOperation(value = "Создать тестовую запись", response = EtalonRecordRO.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response create(final EtalonRecordRO sandboxRecord) {
        sandboxRecord.setEtalonId(null);
        final EtalonRecord etalonRecord = dqSandboxService.upsert(from(sandboxRecord));
        return ok(new RestResponse<>(toEtalonRecordRO(etalonRecord)));
    }

    @PUT
    @Path("record/{" + RECORD_ID_PATH_PARAM + "}")
    @ApiOperation(value = "Обновить тестовую запись", response = EtalonRecordRO.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response update(@PathParam(RECORD_ID_PATH_PARAM) final long recordId, final EtalonRecordRO sandboxRecord) {
        sandboxRecord.setEtalonId(String.valueOf(recordId));
        final EtalonRecord etalonRecord = dqSandboxService.upsert(from(sandboxRecord));
        return ok(new RestResponse<>(toEtalonRecordRO(etalonRecord)));
    }

    private EtalonRecord from(EtalonRecordRO sandboxRecord) {
        EtalonRecordImpl etalonRecord = new EtalonRecordImpl(DataRecordEtalonConverter.from(sandboxRecord));
        final EtalonRecordInfoSection infoSection = new EtalonRecordInfoSection();
        infoSection.setEntityName(sandboxRecord.getEntityName());
        infoSection.setEtalonKey(EtalonKey.builder().id(sandboxRecord.getEtalonId()).build());
        etalonRecord.setInfoSection(infoSection);
        return etalonRecord;
    }

    @GET
    @Path("record/{" + RECORD_ID_PATH_PARAM + "}")
    @ApiOperation(value = "Найти тестовую запись по ID", response = EtalonRecordRO.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response findRecordById(@PathParam(RECORD_ID_PATH_PARAM) final long recordId) {
        return ok(new RestResponse<>(toEtalonRecordRO(dqSandboxService.findRecordById(recordId))));
    }

    @DELETE
    @Path("record/{" + RECORD_ID_PATH_PARAM + "}")
    @ApiOperation(value = "Удалить тестовую запись по ID", response = Boolean.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response deleteRecordById(@PathParam(RECORD_ID_PATH_PARAM) final long recordId) {
        dqSandboxService.deleteRecords(Collections.singletonList(recordId), null);
        return ok(new RestResponse<>(true));
    }

    @POST
    @Path("record/delete")
    @ApiOperation(value = "Удалить тестовые записи по запросу", response = Boolean.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response deleteRecordByRequest(final DeleteSandboxRecordRO deleteSandboxRecordRO) {
        dqSandboxService.deleteRecords(deleteSandboxRecordRO.getIds(), deleteSandboxRecordRO.getEntityName());
        return ok(new RestResponse<>(true));
    }

    @POST
    @Path("record/search")
    @ApiOperation(value = "Поиск записей", response = EtalonRecordRO.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response searchRecords(final SearchComplexRO request) {
        return ok(
                SearchResultToRestSearchResultConverter.convert(
                        dqSandboxService.searchRecords(SearchRequestConverters.from(request)),
                        false
                )
        );
    }

    @POST
    @Path("run")
    @ApiOperation(value = "Запуск правил качества для выбранных записей", response = Map.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response runDQRules(final RunDQRulesRO runDQRulesRO) {
        final RunDQRulesContext runDQRulesContext = new RunDQRulesContext(
                runDQRulesRO.getSelectedByIds(),
                runDQRulesRO.getEntityName(),
                runDQRulesRO.isSandbox(),
                runDQRulesRO.getRules()
        );
        final List<RunDQRulesResultRO> result = dqSandboxService.runDataQualityRules(runDQRulesContext)
                .entrySet().stream()
                .map(e -> new RunDQRulesResultRO(
                        DataRecordEtalonConverter.to(e.getKey(), Collections.emptyList(), Collections.emptyList()),
                        e.getValue().stream()
                                .map(DataRecordEtalonConverter::toDQErrorRO)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
        return ok(new RestResponse<>(result));
    }

    private EtalonRecordRO toEtalonRecordRO(final EtalonRecord etalonRecord) {
        return DataRecordEtalonConverter.to(etalonRecord, Collections.emptyList(), Collections.emptyList());
    }
}
