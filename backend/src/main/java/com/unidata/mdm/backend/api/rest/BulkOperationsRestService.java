/**
 *
 */
package com.unidata.mdm.backend.api.rest;

import static com.unidata.mdm.backend.common.context.ComplexSearchRequestContext.hierarchical;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.unidata.mdm.backend.api.rest.dto.bulk.ExportRecordsToXlsBulkOperationRO;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.api.rest.converter.BulkOperationConverter;
import com.unidata.mdm.backend.api.rest.converter.BulkOperationInformationConverter;
import com.unidata.mdm.backend.api.rest.converter.GetBulkOperationConverter;
import com.unidata.mdm.backend.api.rest.converter.SearchRequestConverters;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.bulk.BulkOperationBaseRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchComplexRO;
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
            SearchRequestContext main = SearchRequestConverters.from(operation.getSelectedByRequest());
            SearchRequestContext[] ctxs = new SearchRequestContext[0];
            if (operation.getSelectedByRequest() instanceof SearchComplexRO) {
                ctxs = ((SearchComplexRO) operation.getSelectedByRequest()).getSupplementaryRequests()
                                                                           .stream()
                                                                           .map(SearchRequestConverters::from)
                                                                           .toArray(SearchRequestContext[]::new);
            }
            ComplexSearchRequestContext context = main == null ? null : hierarchical(main, ctxs);
            BulkOperationRequestContext ctx = new BulkOperationRequestContextBuilder()
                    .applyBySearchContext(context)
                    .applyBySelectedIds(operation.getSelectedByIds())
                    .configuration(BulkOperationConverter.from(operation))
                    .entityName(operation.getEntityName())
                    .build();

            return Response.ok(service.run(ctx)).build();
        } finally {
            MeasurementPoint.stop();
        }
    }
}
