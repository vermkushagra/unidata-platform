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

import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.table.Table;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.context.StatisticRequestContext;
import com.unidata.mdm.backend.common.dto.statistic.ErrorsStatDTO;
import com.unidata.mdm.backend.common.dto.statistic.StatisticResponseDTO;
import com.unidata.mdm.backend.common.dto.statistic.dq.TypedStatisticDTO;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.statistic.GranularityType;
import com.unidata.mdm.backend.common.statistic.StatisticType;
import com.unidata.mdm.backend.common.types.SeverityType;
import com.unidata.mdm.backend.service.statistic.StatServiceExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The Class StatisticRestService.
 */
@Path("data/stat")
@Api(value = "stat", description = "Статистические данные", produces = "application/json")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class StatisticRestService extends AbstractRestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticRestService.class);
    private static final FastDateFormat DQ_DEFAULT_TIMESTAMP = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");//2016-08-18T00:00:00

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

    @GET
    @Path("get/{stType}")
    @ApiOperation(value = "Сервис получения статистики по заданному типу", notes = "", response = TypedStatisticDTO.class, responseContainer = "List")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response getStatistic(@PathParam("stType") String stType,
                                      @QueryParam(value = "startDate") String startDate,
                                      @QueryParam(value = "endDate") String endDate,
                                      @QueryParam(value = "entities") List<String> entities,
                                      @QueryParam(value = "dimension1") List<String> dimension1,
                                      @QueryParam(value = "dimension2") List<String> dimension2,
                                      @QueryParam(value = "dimension3") List<String> dimension3)throws Exception {

        Map<String, List<String>> dimensions = new HashMap<>();
        dimensions.put("dimension1", dimension1);
        dimensions.put("dimension2", dimension2);
        dimensions.put("dimension3", dimension3);

        return ok(new RestResponse<>(statService.getStatistic(stType, formatDqDate(startDate), formatDqDate(endDate), entities, dimensions)));
    }

    private List<SeverityType> getSeverityTypes(List<String> severities){
        if(CollectionUtils.isEmpty(severities)){
            return null;
        }
        // TODO: 23.08.2018 IllegalArgumentException
        return severities.stream().map(SeverityType::fromValue).collect(Collectors.toList());
    }

    private Date formatDqDate(String strDate){
        if (StringUtils.isNotEmpty(strDate)) {
            final Calendar cal = DatatypeConverter.parseDateTime(strDate);

            return ConvertUtils.zonedDateTime2Date(ZonedDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId()));
        } else {
            return null;
        }
    }

}