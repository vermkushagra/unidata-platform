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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.unidata.mdm.backend.common.context.ComplexSearchRequestContext.hierarchical;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.search.SearchComplexRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchFormFieldRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchFormFieldsGroupRO;
import com.unidata.mdm.backend.common.context.SearchContext;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.api.rest.converter.BulkOperationConverter;
import com.unidata.mdm.backend.api.rest.converter.BulkOperationInformationConverter;
import com.unidata.mdm.backend.api.rest.converter.GetBulkOperationConverter;
import com.unidata.mdm.backend.api.rest.converter.SearchRequestConverters;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.bulk.BulkOperationBaseRO;
import com.unidata.mdm.backend.common.context.BulkOperationRequestContext;
import com.unidata.mdm.backend.common.context.BulkOperationRequestContext.BulkOperationRequestContextBuilder;
import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.runtime.MeasurementContextName;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.BulkOperationType;
import com.unidata.mdm.backend.service.bulk.BulkOperationsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Mikhail Mikhailov
 * Bulk operation REST service.
 */
@Path(BulkOperationsRestService.PATH)
@Api(value = "bulk", description = "Входная точка для массовых (балк) операций.", produces = "application/json")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class BulkOperationsRestService extends AbstractRestService {
    /**
     * Path.
     */
    public static final String PATH = "data/bulk";
    /**
     * Bulk operation service.
     */
    @Autowired
    private BulkOperationsService service;

    @Autowired
    private SearchService searchService;

    /**
     * Constructor.
     */
    public BulkOperationsRestService() {
        super();
    }

    /**
     * Lists all bulk operations.
     * @return operations
     */
    @GET
    @ApiOperation(value = "Вывести все доступные пакетные операции", notes = "", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response list() {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_BULK_LIST);
        MeasurementPoint.start();
        try {
            return Response.ok(new RestResponse<>(GetBulkOperationConverter.to(service.list()))).build();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Gets configuration information specific to a bulk operation.
     * @param type BO type
     * @return information
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_CONFIGURE + "/{" + RestConstants.DATA_PARAM_TYPE + "}")
    @ApiOperation(value = "Получить конфигурацию, специфическую для конкретной пакетной операции", notes = "", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response configure(@ApiParam(value = "Тип пакетной операции") @PathParam(RestConstants.DATA_PARAM_TYPE) String type) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_BULK_CONFIGURE);
        MeasurementPoint.start();
        try {
            return Response.ok(BulkOperationInformationConverter.from(service.configure(BulkOperationType.valueOf(type)))).build();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Runs a specific bulk operation.
     * @param operation the operation's parameters.
     * @return true upon success, false otherwise
     */
    @POST
    @Path("/" + RestConstants.PATH_PARAM_RUN)
    @ApiOperation(value = "Запустить пакетную операцию", notes = "", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response run(BulkOperationBaseRO operation) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_BULK_RUN);
        MeasurementPoint.start();
        try {
            SearchRequestContext main = SearchRequestConverters.from(
                    filterPending(operation.getSelectedByRequest())
            );
            SearchRequestContext[] ctxs = new SearchRequestContext[0];
            if (operation.getSelectedByRequest() != null
                    && CollectionUtils.isNotEmpty(operation.getSelectedByRequest().getSupplementaryRequests())) {
                ctxs = operation.getSelectedByRequest().getSupplementaryRequests()
                        .stream()
                        .map(SearchRequestConverters::from)
                        .toArray(SearchRequestContext[]::new);
            }
            ComplexSearchRequestContext context = main == null ? null : hierarchical(main, ctxs);
            BulkOperationRequestContext ctx = new BulkOperationRequestContextBuilder()
                    .applyBySearchContext(context)
                    .applyBySelectedIds(filterPendingIds(operation.getEntityName(),
                            operation.getSelectedByIds(),
                            main == null ? null : main.getAsOf()))
                    .configuration(BulkOperationConverter.from(operation))
                    .entityName(operation.getEntityName())
                    .build();

            return Response.ok(service.run(ctx)).build();
        } finally {
            MeasurementPoint.stop();
        }
    }

    private List<String> filterPendingIds(String entity, List<String> selectedByIds, Date asOf) {
        if (CollectionUtils.isEmpty(selectedByIds)) {
            return selectedByIds;
        }
        FormField idsField = FormField.strictValues(
                com.unidata.mdm.meta.SimpleDataType.STRING,
                RecordHeaderField.FIELD_ETALON_ID.getField(),
                new ArrayList<>(selectedByIds)
        );
        FormField pendingField = FormField.strictValue(
                com.unidata.mdm.meta.SimpleDataType.BOOLEAN,
                RecordHeaderField.FIELD_PENDING.getField(),
                false
        );
        FormField originatorField = FormField.strictValue(
                com.unidata.mdm.meta.SimpleDataType.STRING,
                RecordHeaderField.FIELD_ORIGINATOR.getField(),
                SecurityUtils.getCurrentUserName()
        );
        final SearchRequestContext searchRequestContext = SearchRequestContext.forEtalonData(entity)
                .operator(SearchRequestOperator.OP_AND)
                .form(
                        FormFieldsGroup.createAndGroup(idsField),
                        FormFieldsGroup.createOrGroup(Arrays.asList(pendingField, originatorField))
                )
                .returnField(RecordHeaderField.FIELD_ETALON_ID.getField())
                .count(Integer.MAX_VALUE)
                .asOf(asOf)
                .build();
        return searchService.search(searchRequestContext).getHits().stream()
                .map(h -> (String) h.getFieldValue(RecordHeaderField.FIELD_ETALON_ID.getField()).getFirstValue())
                .collect(Collectors.toList());
    }

    private SearchComplexRO filterPending(SearchComplexRO selectedByRequest) {
        if (selectedByRequest == null) {
            return null;
        }
        final List<SearchFormFieldsGroupRO> formGroups = CollectionUtils.isNotEmpty(selectedByRequest.getFormGroups()) ?
                selectedByRequest.getFormGroups() :
                new ArrayList<>(2);
        formGroups.add(
                SearchFormFieldsGroupRO.createOrGroup(
                        Arrays.asList(
                                new SearchFormFieldRO(
                                        SimpleDataType.BOOLEAN,
                                        RecordHeaderField.FIELD_PENDING.getField(),
                                        false,
                                        false,
                                        SearchFormFieldRO.SearchTypeRO.EXACT
                                ),
                                new SearchFormFieldRO(
                                        SimpleDataType.STRING,
                                        RecordHeaderField.FIELD_ORIGINATOR.getField(),
                                        SecurityUtils.getCurrentUserName(),
                                        false,
                                        SearchFormFieldRO.SearchTypeRO.EXACT
                                )
                        )
                )
        );
        selectedByRequest.setFormGroups(formGroups);
        return selectedByRequest;
    }
}
