package com.unidata.mdm.backend.api.rest;

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forAuditEvents;
import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createAndGroup;
import static com.unidata.mdm.backend.common.search.SortField.SortOrder.DESC;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.lang.reflect.Array;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.unidata.mdm.backend.api.rest.converter.SearchResultToRestSearchResultConverter;
import com.unidata.mdm.backend.api.rest.dto.search.SearchResultRO;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.search.SortField;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.service.search.Event;
import com.unidata.mdm.backend.service.search.util.AuditHeaderField;
import com.unidata.mdm.meta.SimpleDataType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Denis Kostovarov
 */
@Path("audit/search")
@Api(value = "audit_service", description = "Данные аудита", produces = "application/json")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class AuditLogSearchRestService extends AbstractRestService {

    private static final String SEARCH_PARAM_USERNAME = "username";
    private static final String SEARCH_PARAM_REGISTRY = "registry";
    private static final String SEARCH_PARAM_OPERATION = "operation";
    private static final String SEARCH_PARAM_TYPE = "type";
    private static final String SEARCH_PARAM_ETALON = "etalon";
    private static final String SEARCH_PARAM_COUNT = "count";
    private static final String SEARCH_PARAM_PAGE = "page";
    private static final String SEARCH_PARAM_START_DATE = "startDate";
    private static final String SEARCH_PARAM_END_DATE = "endDate";
    private static final String SEARCH_PARAM_EXTERAL_ID = "externalId";
    private static final int DEFAULT_OBJ_COUNT_VALUE = 10;
    private static final int DEFAULT_PAGE_NUMBER_VALUE = 1;

    @Autowired
    private SearchService searchService;

    /**
     * @param dateTimeStr
     * @return
     */
    private static ZonedDateTime parseDateTime(final String dateTimeStr) {
        final Calendar cal = DatatypeConverter.parseDateTime(dateTimeStr);
        return ZonedDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId());
    }

    //todo replace to common search requests!
    @GET
    @ApiOperation(value = "Поиск записей аудита по параметрам", response = SearchResultRO.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
                          @ApiResponse(code = 500, message = "Error occurred", response = Array.class) })
    public Response search(
            @QueryParam(SEARCH_PARAM_USERNAME) String username, @QueryParam(SEARCH_PARAM_EXTERAL_ID) String externalId,
            @QueryParam(SEARCH_PARAM_REGISTRY) String registry, @QueryParam(SEARCH_PARAM_TYPE) String type,
            @QueryParam(SEARCH_PARAM_OPERATION) String operation, @QueryParam(SEARCH_PARAM_ETALON) String etalon,
            @QueryParam(SEARCH_PARAM_START_DATE) String startDate, @QueryParam(SEARCH_PARAM_END_DATE) String endDate,
            @QueryParam(SEARCH_PARAM_COUNT) String countStr, @QueryParam(SEARCH_PARAM_PAGE) String pageStr) {

        ZonedDateTime startDateObj = null;
        ZonedDateTime endDateObj = null;
        if (!StringUtils.isEmpty(startDate)) {
            startDateObj = parseDateTime(startDate);
        }
        if (!StringUtils.isEmpty(endDate)) {
            endDateObj = parseDateTime(endDate);
        }

        int count;
        try {
            count = Integer.parseInt(countStr);
        } catch (NumberFormatException ignore) {
            count = DEFAULT_OBJ_COUNT_VALUE;
        }

        int page;
        try {
            page = Integer.parseInt(pageStr);
            if (page < DEFAULT_PAGE_NUMBER_VALUE) {
                page = DEFAULT_PAGE_NUMBER_VALUE;
            }
        } catch (NumberFormatException ignore) {
            page = DEFAULT_PAGE_NUMBER_VALUE;
        }

        Collection<FormField> formFields = new ArrayList<>();
        if (!isBlank(username)) {
            FormField user = FormField.strictString(Event.USER, username);
            formFields.add(user);
        }
        if (!isBlank(registry)) {
            FormField entity = FormField.strictString(Event.ENTITY, registry);
            formFields.add(entity);
        }
        if (!isBlank(etalon)) {
            FormField etalonField = FormField.strictString(Event.ETALON_ID, etalon);
            formFields.add(etalonField);
        }
        if (!isBlank(type)) {
            FormField entity = FormField.strictString(Event.ACTION, type);
            formFields.add(entity);
        }
        if (!isBlank(operation)) {
            FormField entity = FormField.strictString(Event.OPERATION_ID, operation);
            formFields.add(entity);
        }
        if (!isBlank(externalId)) {
            FormField entity = FormField.strictString(Event.EXTERNAL_ID, externalId);
            formFields.add(entity);
        }
        if (startDateObj != null || endDateObj != null) {
            Date from = ConvertUtils.zonedDateTime2Date(startDateObj);
            Date to = ConvertUtils.zonedDateTime2Date(endDateObj);
            FormField dateRange = FormField.range(SimpleDataType.DATE, Event.DATE, FormField.FormType.POSITIVE, from,
                    to);
            formFields.add(dateRange);
        }

        List<String> fields = Arrays.stream(AuditHeaderField.values())
                                    .map(AuditHeaderField::getField)
                                    .collect(toList());

        SearchRequestContext ctx = forAuditEvents().form(formFields.isEmpty() ? null : createAndGroup(formFields))
                                                   .returnFields(fields)
                                                   .addSorting(singletonList(new SortField(Event.DATE, DESC, false)))
                                                   .operator(SearchRequestOperator.OP_OR)
                                                   .count(count)
                                                   .fetchAll(formFields.isEmpty())
                                                   .page(page > 0 ? page - 1 : page)
                                                   .totalCount(true)
                                                   .build();
        SearchResultDTO result = searchService.search(ctx);
        return ok(SearchResultToRestSearchResultConverter.convert(result, false));
    }
}
