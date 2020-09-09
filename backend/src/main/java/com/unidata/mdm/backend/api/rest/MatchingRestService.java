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

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.prepost.PreAuthorize;

import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.api.rest.dto.matching.BaseMatchingRuleRO;
import com.unidata.mdm.backend.api.rest.dto.matching.MatchingAlgorithmRO;
import com.unidata.mdm.backend.api.rest.dto.matching.MatchingRuleRO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.service.matching.MatchingAlgorithmService;
import com.unidata.mdm.backend.service.matching.MatchingMetaFacadeService;
import com.unidata.mdm.backend.service.matching.MatchingRulesService;
import com.unidata.mdm.backend.service.matching.algorithms.Algorithm;
import com.unidata.mdm.backend.service.matching.data.MatchingAlgorithm;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import com.unidata.mdm.backend.service.matching.data.MatchingUserSettings;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("matching")
@Api(value = "match-setting", description = "Настройки правил сопоставления записей", produces = "application/json")
@Consumes({"application/json"})
@Produces({"application/json"})
public class MatchingRestService extends AbstractRestService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingRestService.class);

    /**
     * System resource for matching.
     */
    private static final String MATCHING_MANAGEMENT = "ADMIN_MATCHING_MANAGEMENT";

    /**
     * Collection url paths
     */
    private static final String ENTITY_NAME = "entityName";
    private static final String MATCH_ALGORITHMS = "matchingAlgorithms";
    private static final String RULE_ID = "id";
    private static final String DATA_PARAM_FILE = "file";

    /**
     * Conversion service
     */
    @Autowired
    private ConversionService conversionService;
    /**
     * matching rules service
     */
    @Autowired
    private MatchingRulesService matchingRulesService;

    /**
     * matching service
     */
    @Autowired
    private MatchingAlgorithmService matchingAlgorithmService;

    @Autowired
    private MatchingMetaFacadeService matchingMetaFacadeService;

    @GET
    @Path("/" + MATCH_ALGORITHMS)
    @ApiOperation(value = "Список алгоритмов сапоставления", notes = "", response = List.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getAllMatchRules() {
        Collection<MatchingAlgorithm> algorithms = matchingAlgorithmService.getAllAlgorithms().stream().map(Algorithm::getTemplate).collect(toList());
        return ok(new RestResponse<>(algorithms.stream().map(al -> conversionService.convert(al, MatchingAlgorithmRO.class)).collect(toList())));
    }



    @GET
    @Path("/rules")
    @ApiOperation(value = "Получить список правил для реестра/справочника", notes = "", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getRulesByEntityName(@ApiParam(value = "Имя реестра/справочника") @QueryParam(ENTITY_NAME) String entityName) {
        boolean all = entityName == null || entityName.isEmpty();
        Collection<MatchingRule> matchingRules = all ? matchingRulesService.getAllRules() : matchingRulesService.getMatchingRulesByEntityName(entityName);
        return ok(new RestResponse<>(matchingRules.stream().map(rule -> conversionService.convert(rule, BaseMatchingRuleRO.class)).collect(toList())));
    }

    @GET
    @Path("/rules/{" + RULE_ID + "}")
    @ApiOperation(value = "Получить правило сопоставления по id", notes = "", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getRuleById(@PathParam(RULE_ID) Integer id) {
        MatchingRule rule = matchingRulesService.getMatchingRule(id);
        return ok(new RestResponse<>(conversionService.convert(rule, MatchingRuleRO.class)));
    }

    @POST
    @Path("/rules")
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('" + MATCHING_MANAGEMENT + "').isCreate()")
    @ApiOperation(value = "Добавить правило сопоставления", notes = "", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response addRule(@Nonnull MatchingRuleRO matchingRule) {
        MatchingRule rule = conversionService.convert(matchingRule, MatchingRule.class);
        rule.setId(null);
        rule = matchingRulesService.saveMatchingRule(rule);
        return ok(new RestResponse<>(conversionService.convert(rule, MatchingRuleRO.class)));
    }

    @PUT
    @Path("/rules/{" + RULE_ID + "}")
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('" + MATCHING_MANAGEMENT + "').isUpdate()")
    @ApiOperation(value = "Обновить настроки сопоставления", notes = "", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response updateRule(@PathParam(RULE_ID) Integer id, @Nonnull MatchingRuleRO matchingRule) {
        MatchingRule rule = conversionService.convert(matchingRule, MatchingRule.class);
        rule = matchingRulesService.updateMatchingRule(rule);
        return ok(new RestResponse<>(conversionService.convert(rule, MatchingRuleRO.class)));
    }

    @DELETE
    @Path("/rules/{" + RULE_ID + "}")
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('" + MATCHING_MANAGEMENT + "').isDelete()")
    @ApiOperation(value = "Удалить настройки сопоставления", notes = "", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response deleteRule(@PathParam(RULE_ID) Integer id) {
        matchingRulesService.removeMatchingRule(id);
        return ok(new RestResponse<>());
    }

    /**
     * Saves binary large object.
     *
     * @param fileAttachment attachment object
     * @return ok/nok
     */
    @POST
    @Path("/xml")
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('" + MATCHING_MANAGEMENT + "').isCreate()")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(value = "Загрузить настройки сопоставления", notes = "", response = UpdateResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response importModel(@ApiParam(value = "Импортируемый фаил") @Multipart(required = true, value = DATA_PARAM_FILE) Attachment fileAttachment)
            throws Exception {
        if (isNull(fileAttachment)) {
            return okOrNotFound(null);
        }
        java.nio.file.Path path = conversionService.convert(fileAttachment, java.nio.file.Path.class);
        String fileName = fileAttachment.getDataHandler().getDataSource().getName();

        if (!fileName.endsWith("xml")) {
            String contentType = fileAttachment.getContentType().toString();
            LOGGER.warn("Invalid content type rejected, while importing matching settings.", contentType);
            throw new BusinessException("Import classifier with the media type [{}] is not allowed.",
                    ExceptionId.EX_MATCHING_IMPORT_TYPE_UNSUPPORTED, contentType);
        }

        MatchingUserSettings matchingUserSettings = conversionService.convert(path, MatchingUserSettings.class);
        matchingMetaFacadeService.saveUserSettings(matchingUserSettings);
        return ok(new RestResponse<>());
    }

    /**
     * @return xml matching settings
     * @throws Exception
     */
    @GET
    @Path("/xml")
    @ApiOperation(value = "Получить настройки сопоставления в xml формате", notes = "", response = StreamingOutput.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response exportToXml(@ApiParam(value = "Имя реестра/справочника") @QueryParam(ENTITY_NAME) String entityName) throws Exception {
        boolean all = entityName == null || entityName.isEmpty();
        MatchingUserSettings matchingUserSettings = new MatchingUserSettings();
        matchingUserSettings.setMatchingRules(all ? matchingRulesService.getAllRules() : matchingRulesService.getMatchingRulesByEntityName(entityName));
        final String result = conversionService.convert(matchingUserSettings, String.class);
        String encodedFilename = URLEncoder.encode(entityName + "_"
                + DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd_HH-mm-ss") + ".xml", "UTF-8");
        StreamingOutput outputResult = output -> output.write(result.getBytes(StandardCharsets.UTF_8));
        return Response.ok(outputResult)
                .encoding("UTF-8")
                .header("Content-Disposition", "attachment; filename=" + entityName + ".xml" + "; filename*=UTF-8''" + encodedFilename)
                .header("Content-Type", MediaType.TEXT_XML)
                .build();
    }
}
