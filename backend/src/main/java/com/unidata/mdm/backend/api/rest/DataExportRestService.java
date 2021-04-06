package com.unidata.mdm.backend.api.rest;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import org.apache.cxf.jaxrs.ext.xml.ElementClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.unidata.mdm.backend.api.rest.constants.SearchConstants;
import com.unidata.mdm.backend.api.rest.converter.SearchRequestConverters;
import com.unidata.mdm.backend.api.rest.converter.SearchResultToRestSearchResultConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.search.SearchFormRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchResultHitRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchResultRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchSimpleRO;
import com.unidata.mdm.backend.api.rest.dto.security.RoleRO;
import com.unidata.mdm.backend.common.context.GetMultipleRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.search.FacetName;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.service.data.export.DataExportService;
import com.unidata.mdm.backend.service.data.xlsximport.DataImportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class DataExportRestService.
 */
@Path("export/data")
@Api(value = "export", description = "Экспорт данных", produces = "application/json")
@Consumes({"application/json"})
@Produces({"application/json"})
public class DataExportRestService extends AbstractRestService {

    /**
     * TODO IT SHOULD BE CONFIGURABLE
     */
    public final static Integer COUNT_LIMIT = 1000;
    /**
     * The search service.
     */
    @Autowired
    private SearchService searchService;
    /**
     * The data export service.
     */
    @Autowired
    @Qualifier(value = "xlsxExportService")
    private DataExportService dataExportService;
    @Autowired
    @Qualifier(value = "xlsxImportService")
    private DataImportService dataImportService;

    /**
     * Search form request for XLS export.
     *
     * @param request the form
     * @return result
     */
    @POST
    @Path("/" + SearchConstants.SEARCH_PATH_XLS_EXPORT_FORM)
    @ElementClass(response = Object.class)
    @ApiOperation(
            value = "Найти записи, содержащие в заданных полях текст. Подробный поиск через поисковую форму с выдачей таблички в эксель.",
            notes = "",
            response = Object.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-excel"})
    public Response searchAndExportFormToXls(@FormParam(SearchConstants.SEARCH_PARAM_EXPORT_REQUEST_CONTENT) String jsonString)
            throws Exception {

        SearchFormRO request = null;
        if (jsonString != null) {
            request = super.unrestInplace(jsonString, SearchFormRO.class);
        } else {
            return Response.noContent().build();
        }
        request.setCount(COUNT_LIMIT);
        boolean isDeletedFacetActive =
                request.getFacets() != null
                        && request.getFacets().contains(FacetName.FACET_NAME_INACTIVE_ONLY.getValue());

        SearchRequestContext ctx = SearchRequestConverters.from(request);

        SearchResultDTO result = searchService.search(ctx);
        return processSearchResultAndResponse(result, request.getEntity());
    }

    /**
     * Search simple request for XLS export.
     *
     * @param request the simple request
     * @return result
     */
    @POST
    @Path("/" + SearchConstants.SEARCH_PATH_XLS_EXPORT_SIMPLE)
    @ElementClass(response = Object.class)
    @ApiOperation(value = "Найти записи, содержащие в заданных полях текст. Упрощенный поиск с выдачей таблички в эксель.", notes = "", response = SearchResultRO.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-excel"})
    public Response searchAndExportSimpleToXls(@FormParam(SearchConstants.SEARCH_PARAM_EXPORT_REQUEST_CONTENT) String jsonString)
            throws Exception {

        SearchSimpleRO request = null;
        if (jsonString != null) {
            request = super.unrestInplace(jsonString, SearchSimpleRO.class);
        } else {
            return Response.noContent().build();
        }
        request.setCount(COUNT_LIMIT);
        boolean isDeletedFacetActive =
                request.getFacets() != null
                        && request.getFacets().contains(FacetName.FACET_NAME_INACTIVE_ONLY.getValue());

        SearchRequestContext ctx = SearchRequestConverters.from(request);

        SearchResultDTO result = searchService.search(ctx);
        return processSearchResultAndResponse(result, request.getEntity());
    }

    /**
     * Process search result and return result.
     *
     * @param searchResult the result
     * @param entity       the name
     * @param deleted      deleted faced value
     * @return response
     * @throws Exception
     */
    private Response processSearchResultAndResponse(SearchResultDTO searchResult, String entity)
            throws Exception {

        if (searchResult.getHits().isEmpty()) {
            return createTemplate(entity);
        }

        final List<String> ids = searchResult.getHits().stream().map(SearchResultHitDTO::getId).collect(Collectors.toList());
        final ByteArrayOutputStream dataFile = dataExportService.exportData(
            GetMultipleRequestContext.builder()
                .entityName(entity)
                .etalonKeys(ids)
                .build());

        final String encodedFilename = URLEncoder.encode("Export-" + entity + ".xlsx", "UTF-8");
        return Response
                .ok(dataFile.toByteArray())
                .encoding("UTF-8")
                .header("Content-Disposition",
                        "attachment; filename=Export-" + entity + ".xlsx; filename*=UTF-8''" + encodedFilename)
                .header("Content-Type", MediaType.APPLICATION_OCTET_STREAM_TYPE)
                .allow("OPTIONS")
                .build();
    }

    /**
     * Creates the template.
     *
     * @param entityName the entity name
     * @return the response
     * @throws Exception the exception
     */
    @GET
    @Path(value = "/template/{entityName}")
    @ApiOperation(value = "Создание темплейта для экспорта", notes = "", response = RoleRO.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response createTemplate(@PathParam(value = "entityName") String entityName) throws Exception {
        final ByteArrayOutputStream templateFile = dataImportService.createTemplateFile(entityName);
        final String encodedFilename = URLEncoder.encode(entityName + ".xlsx", "UTF-8");
        return Response
                .ok(templateFile.toByteArray())
                .encoding("UTF-8")
                .header("Content-Disposition",
                        "attachment; filename=Export-" + entityName + ".xlsx; filename*=UTF-8''" + encodedFilename)
                .header("Content-Type", MediaType.APPLICATION_OCTET_STREAM_TYPE).build();
    }

}
