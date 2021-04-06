package com.unidata.mdm.backend.api.rest;

import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.table.Table;
import com.unidata.mdm.backend.common.context.StatisticRequestContext;
import com.unidata.mdm.backend.common.dto.statistic.ErrorsStatDTO;
import com.unidata.mdm.backend.common.dto.statistic.StatisticResponseDTO;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.statistic.GranularityType;
import com.unidata.mdm.backend.service.statistic.StatServiceExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.Date;

/**
 * The Class StatisticRestService.
 */
@Path("data/stat")
@Api(value = "stat", description = "Статистические данные", produces = "application/json")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class StatisticRestService extends AbstractRestService {


    /** The stat service. */
    @Autowired
    private StatServiceExt statService;

    /**
     * search service
     */
    @Autowired
    private SearchService searchService;

    /**
     * Gets the statistic.
     *
     * @param startDate
     *            the start date
     * @param endDate
     *            the end date
     * @param entityName
     *            the entity name
     * @param granularity
     *            the granularity
     * @return the statistic
     * @throws Exception
     *             the exception
     */
    @GET
    @Path("get-stats")
    @ApiOperation(value = "Сервис статистики", notes = "", response = StatisticResponseDTO.class, responseContainer = "List")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response getStatistic(@QueryParam(value = "startDate") String startDate,
            @QueryParam(value = "endDate") String endDate,
            @QueryParam(value = "entityName") String entityName,
            @QueryParam(value = "sourceSystemName") String sourceSystemName,
            @QueryParam(value = "granularity") String granularity) throws Exception {

        GranularityType granulatiry = GranularityType.fromValue(granularity);
        Date from = javax.xml.bind.DatatypeConverter.parseDateTime(startDate).getTime();
        Date to = javax.xml.bind.DatatypeConverter.parseDateTime(endDate).getTime();

        from.setTime(from.getTime() - GranularityType.toTemporalUnit(granulatiry).getDuration().getSeconds());

        StatisticRequestContext request = StatisticRequestContext.builder()
                .startDate(from)
                .endDate(to)
                .entityName(entityName)
                .granularity(granulatiry)
                .sourceSystem(sourceSystemName)
                .build();

        return ok(statService.getStatistic(request));
    }

    /**
     * Get statistic for last available date.
     *
     * @param entityName
     *            the entity name
     * @return the statistic
     * @throws Exception
     *             the exception
     */
    @GET
    @Path("get-last-stats")
    @ApiOperation(value = "Сервис статистики", notes = "", response = StatisticResponseDTO.class, responseContainer = "List")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response getLastStatistic(@QueryParam(value = "entityName") String entityName) throws Exception {
        StatisticRequestContext request = StatisticRequestContext.builder()
                .entityName(entityName)
                .forLastDate(true)
                .build();
        return ok(statService.getStatistic(request));
    }

    /**
     * Export statistic to file
     * @return success status
     */
    @GET
    @Path("export-stats")
    @ApiOperation(value = "Экспорт  статистики", notes = "", response = RestResponse.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response exportStatistic() {
        statService.exportStatistic();
        return ok(new RestResponse());
    }

    /**
     * Gets the errors statistic.
     *
     * @param entityName the entity name
     * @return the errors statistic
     * @throws Exception the exception
     */
    @GET
    @Path("get-error-stats")
    @ApiOperation(value = "Сервис статистики ошибок", notes = "", response = ErrorsStatDTO.class, responseContainer = "List")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response getErrorsStatistic(@QueryParam(value = "entityName") String entityName,
            @QueryParam(value = "sourceSystemName") String sourceSystemName) throws Exception {
        return ok(statService.getErrorsStat(entityName, sourceSystemName));
    }

    /**
     * Gets the aggregated errors statistic.
     *
     * @param entityName the entity name
     * @return the errors statistic
     * @throws Exception the exception
     */
    @GET
    @Path("get-dq-aggregation")
    @ApiOperation(value = "Сервис получение агригированной", notes = "", response = Table.class, responseContainer = "List")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
                          @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response getDqAggregations(@QueryParam(value = "entityName") String entityName) throws Exception {
        return ok(new RestResponse<>(statService.getErrorStatisticAggregation(entityName)));
    }
}