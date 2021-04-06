/**
 *
 */
package com.unidata.mdm.backend.api.rest;

import static com.unidata.mdm.backend.api.rest.constants.SearchConstants.DEFAULT_OBJ_COUNT_VALUE;
import static com.unidata.mdm.backend.api.rest.constants.SearchConstants.DEFAULT_PAGE_NUMBER_VALUE;
import static com.unidata.mdm.backend.api.rest.constants.SearchConstants.SEARCH_PARAM_COUNT;
import static com.unidata.mdm.backend.api.rest.constants.SearchConstants.SEARCH_PARAM_FIELDS;
import static com.unidata.mdm.backend.api.rest.constants.SearchConstants.SEARCH_PARAM_PAGE;
import static com.unidata.mdm.backend.api.rest.constants.SearchConstants.SEARCH_PARAM_TEXT;
import static com.unidata.mdm.backend.api.rest.constants.SearchConstants.SEARCH_PATH_META;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.ext.xml.ElementClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.api.rest.constants.SearchConstants;
import com.unidata.mdm.backend.api.rest.converter.SearchRequestConverters;
import com.unidata.mdm.backend.api.rest.converter.SearchResultToRestSearchResultConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.search.SearchComboRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchComplexRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchFormRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchResultRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchSimpleRO;
import com.unidata.mdm.backend.api.rest.util.SearchResultHitModifier;
import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.runtime.MeasurementContextName;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.FacetName;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.service.search.util.ModelHeaderField;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Mikhail Mikhailov
 *         Search REST service.
 */
@SuppressWarnings("Duplicates")
@Path(SearchConstants.SEARCH_PATH_SEARCH)
@Api(value = "search", description = "Функции поиска", produces = "application/json")
@Consumes({"application/json"})
@Produces({"application/json"})
public class SearchRestService extends AbstractRestService {

    /**
     * Logger.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(SearchRestService.class);

    /**
     * Search service.
     */
    @Autowired
    private SearchService searchService;
    /**
     * Modify search result for ui presentation
     */
    @Autowired
    private SearchResultHitModifier searchResultHitModifier;

    /**
     * Search form request
     * @param request the form
     * @return result
     */
    @POST
    @Path("/" + SearchConstants.SEARCH_PATH_COMBO)
    @ElementClass(response = SearchResultRO.class)
    @ApiOperation(value = "Найти записи, содержащие в заданных полях текст. Подробный поиск через поисковую форму и простой поиск (лгическое AND).", notes = "", response = SearchResultRO.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response search(SearchComboRO request) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_SEARCH_COMBO);
        MeasurementPoint.start();

        try {
            SearchRequestContext ctx = SearchRequestConverters.from(request);
            SearchResultDTO searchResult = searchService.search(ctx);
            SearchResultRO result = extractResult(ctx, searchResult);
            return ok(result);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Search form request
     * @param request the form
     * @return result
     */
    @POST
    @Path("/" + SearchConstants.SEARCH_PATH_FORM)
    @ElementClass(response = SearchResultRO.class)
    @ApiOperation(value = "Найти записи, содержащие в заданных полях текст. Подробный поиск через поисковую форму.", notes = "", response = SearchResultRO.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response search(SearchFormRO request) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_SEARCH_FORM);
        MeasurementPoint.start();

        try {
            SearchRequestContext ctx = SearchRequestConverters.from(request);
            SearchResultDTO searchResult = searchService.search(ctx);
            SearchResultRO result = extractResult(ctx, searchResult);
            return ok(result);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Search form request
     * @param request the form
     * @return result
     */
    @POST
    @Path("/" + SearchConstants.SEARCH_PATH_SIMPLE)
    @ElementClass(response = SearchResultRO.class)
    @ApiOperation(value = "Найти записи, содержащие в заданных полях текст. Упрощенный поиск.", notes = "", response = SearchResultRO.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response search(SearchSimpleRO request) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_SEARCH_SIMPLE);
        MeasurementPoint.start();

        try {
            request.setSayt(false);
            SearchRequestContext ctx = SearchRequestConverters.from(request);
            SearchResultDTO searchResult = searchService.search(ctx);
            SearchResultRO result = extractResult(ctx, searchResult);
            return ok(result);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Search form request
     * @param request the form
     * @return result
     */
    @POST
    @Path("/" + SearchConstants.SEARCH_PATH_COMPLEX)
    @ElementClass(response = SearchResultRO.class)
    @ApiOperation(value = "Сложный поиск через связи.", notes = "", response = SearchResultRO.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
                          @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response search(SearchComplexRO request) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_SEARCH_COMPLEX);
        MeasurementPoint.start();

        try {
            SearchRequestContext ctx = SearchRequestConverters.from(request);
            if (ctx == null) {
                return notFound();
            }
            SearchRequestContext[] subCtx = request.getSupplementaryRequests()
                                                   .stream()
                                                   .map(SearchRequestConverters::from)
                                                   .filter(Objects::nonNull)
                                                   .toArray(SearchRequestContext[]::new);
            SearchResultRO result = null;
            if (subCtx.length == 0) {
                SearchResultDTO searchResult = searchService.search(ctx);
                result = extractResult(ctx, searchResult);
            } else {
                ComplexSearchRequestContext context = ComplexSearchRequestContext.hierarchical(ctx, subCtx);
                SearchResultDTO searchResult = searchService.search(context).get(ctx);
                result = extractResult(context, searchResult);
            }

            return ok(result);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Simple 'search-as-you-type'.
     * @param request simple search request
     * @return array of strings
     */
    @POST
    @Path("/" + SearchConstants.SEARCH_PATH_SAYT)
    @ApiOperation(value = "Найти записи, содержащие в заданных полях текст непосредственно во время ввода.", notes = "", response = Array.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = Array.class)})
    public Response saytSearch(SearchSimpleRO request) {
        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_SEARCH_SAYT);
        MeasurementPoint.start();
        try {
            request.setSayt(true);
            SearchRequestContext ctx = SearchRequestConverters.from(request);
            SearchResultDTO searchResult = searchService.search(ctx);
            SearchResultRO result = extractResult(ctx, searchResult);
            return ok(result);
        } finally {
            MeasurementPoint.stop();
        }
    }

    private SearchResultRO extractResult(ComplexSearchRequestContext ctx, SearchResultDTO result) {
        // TODO: move status management off the REST layer.
        boolean isDeletedFacetActive =
                ctx.getMainRequest().getFacets() != null
             && ctx.getMainRequest().getFacets().contains(FacetName.FACET_NAME_INACTIVE_ONLY);

        searchResultHitModifier.modifySearchResult(result, ctx);
        return SearchResultToRestSearchResultConverter.convert(result, isDeletedFacetActive);
    }

    private SearchResultRO extractResult(SearchRequestContext ctx, SearchResultDTO result) {
        // TODO: move status management off the REST layer.
        boolean isDeletedFacetActive =
                ctx.getFacets() != null && ctx.getFacets().contains(FacetName.FACET_NAME_INACTIVE_ONLY);

        searchResultHitModifier.modifySearchResult(result, ctx);
        return SearchResultToRestSearchResultConverter.convert(result, isDeletedFacetActive);
    }

    /**
     * Searches meta data.
     *
     * @param fields the fields
     * @param text   the text
     * @param count  the count
     * @return hits
     */
    @GET
    @Path("/" + SEARCH_PATH_META)
    @ElementClass(response = SearchResultRO.class)
    @ApiOperation(value = "Найти реестры и справочники, содержащие в заданных полях текст или вернуть все, если поисковая строка пустая.", notes = "", response = SearchResultRO.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response searchMeta(@QueryParam(SEARCH_PARAM_FIELDS) String fields,
                               @QueryParam(SEARCH_PARAM_TEXT) String text,
                               @QueryParam(SEARCH_PARAM_PAGE) @DefaultValue(DEFAULT_PAGE_NUMBER_VALUE) int page,
                               @QueryParam(SEARCH_PARAM_COUNT) @DefaultValue(DEFAULT_OBJ_COUNT_VALUE) int count) {

        boolean isAllFieldsSearch = StringUtils.isBlank(fields);
        boolean isAllFetchSearch = StringUtils.isBlank(text);

        //because in ES we store un-normalized data
        if (isAllFieldsSearch && isAllFetchSearch) {
            return notFound();
        }

        List<FormFieldsGroup> groupFormFields = new ArrayList<>();
        if (!isAllFieldsSearch) {
            List<String> searchFields = SearchUtils.getFields(fields);
            List<FormField> formFields = new ArrayList<>(searchFields.size());
            for (String field : searchFields) {
                FormField formField = FormField.strictString(ModelHeaderField.SEARCH_OBJECT.getField(), field);
                formFields.add(formField);
            }
            groupFormFields.add(FormFieldsGroup.createOrGroup(formFields));
        }

        if (!isAllFetchSearch) {
            FormField textField = FormField.startWithString(ModelHeaderField.VALUE.getField(), text);
            FormFieldsGroup textGroup = FormFieldsGroup.createAndGroup(textField);
            groupFormFields.add(textGroup);
        }
        List<String> returnFields = Arrays.stream(ModelHeaderField.values())
                .map(ModelHeaderField::getField)
                .collect(Collectors.toList());

        SearchRequestContext scb = SearchRequestContext.forModelElements()
                                                       .form(groupFormFields)
                                                       .operator(SearchRequestOperator.OP_OR)
                                                       .returnFields(returnFields)
                                                       .count(count)
                                                       .page(page > 0 ? page - 1 : page)
                                                       .totalCount(true)
                                                       .source(false)
                                                       .runExits(true)
                                                       .build();

        return ok(SearchResultToRestSearchResultConverter.convert(searchService.search(scb), false));
    }
}
