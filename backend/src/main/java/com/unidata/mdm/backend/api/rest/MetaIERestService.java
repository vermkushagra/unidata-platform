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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.unidata.mdm.backend.api.rest.converter.ExportParamsConverter;
import com.unidata.mdm.backend.api.rest.dto.security.ExportParamsRO;
import com.unidata.mdm.backend.notification.notifiers.MatchingRulesChangesNotifier;
import com.unidata.mdm.backend.service.model.ie.dto.MetaAction;
import com.unidata.mdm.backend.service.model.ie.dto.MetaEdge;
import com.unidata.mdm.backend.service.model.ie.dto.MetaType;
import com.unidata.mdm.backend.service.model.ie.dto.MetaVertex;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaGraphRO;
import com.unidata.mdm.backend.api.rest.dto.meta.ie.converters.MetaGraphDTOToROConverter;
import com.unidata.mdm.backend.api.rest.dto.meta.ie.converters.MetaGraphROToDTOConverter;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.service.model.ie.MetaIEService;
import com.unidata.mdm.backend.service.model.ie.dto.MetaGraph;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class MetaIERestService.
 * @author ilya.bykov
 */
@Path(MetaIERestService.SERVICE_PATH)
@Api(value = "meta_model_ie", description = "Импорт/экспорт метаданных и зависимостей", produces = "application/json")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class MetaIERestService extends AbstractRestService {

    /** The meta IE service. */
    @Autowired
    private MetaIEService metaIEService;
    /**
     * HZ notifyer.
     */
    @Autowired
    private MatchingRulesChangesNotifier matchingRulesChangesNotifier;
    /**
     * Service path.
     */
    public static final String SERVICE_PATH = "meta/model-ie";

    /**
     * Upload.
     *
     * @param modelFile
     *            the model file
     * @param isOverride
     *            is override or not
     * @return the response
     * @throws Exception
     *             the exception
     */
    @POST
    @Path("/upload")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(value = "Загрузить ZIP архив с новой моделью", notes = "", response = MetaGraphRO.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response upload(@Multipart(value = "modelFile") Attachment modelFile,
                           @Multipart(value = "override") boolean isOverride) throws Exception {
        java.nio.file.Path toImport = saveFileToTempFolder(modelFile);
        MetaGraph result = metaIEService.preloadMetaZip(toImport, isOverride);
        return ok(new RestResponse<>(MetaGraphDTOToROConverter.convert(result)));
    }

    /**
     * Apply.
     *
     * @param metaGraph
     *            the meta graph
     * @return the response
     */
    @POST
    @Path("/apply")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Применить ранее загруженные изменения.", notes = "", response = MetaGraphRO.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response apply(MetaGraphRO metaGraph) {
        MetaGraph result = metaIEService.importMetaZip(MetaGraphROToDTOConverter.convert(metaGraph));

        //notify cluster nodes after postgres transaction ended, for update matching rules
        Set<String> entityNames = new HashSet<>();
        result.vertexSet().stream().filter(v -> (v.getType() == MetaType.MATCH_RULE)).forEach(v -> {
            Set<MetaEdge<MetaVertex>> metaSet = result.edgesOf(v);
            metaSet.stream().forEach(me -> {
                if (me.getTo().getType() == MetaType.ENTITY) {
                    entityNames.add(me.getTo().getId());
                }
            });
        });

        if (!entityNames.isEmpty()) {
            matchingRulesChangesNotifier.notifyMatchingRulesChanged(entityNames);
        }

        return ok(new RestResponse<>(MetaGraphDTOToROConverter.convert(result)));
    }

    /**
     * Export meta model to zip file.
     *
     * @param exportParamsRO the export parameters
     * @return the response
     */
    @POST
    @Path("/export")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Экспортировать метамодель и зависимости.", notes = "", response = MetaGraphRO.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response export(final ExportParamsRO exportParamsRO) {
        final String storageId = "default";
        metaIEService.exportMeta(storageId, ExportParamsConverter.exportContextFromRO(exportParamsRO));
        return ok(new UpdateResponse(true, storageId));
    }

    /**
     * Save file to temp folder.
     *
     * @param attachment
     *            the attachment
     * @return the java.nio.file. path
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static final java.nio.file.Path saveFileToTempFolder(Attachment attachment) throws IOException {
        String fileName = attachment.getContentDisposition().getParameter("filename");
        if (StringUtils.isEmpty(fileName) || !StringUtils.endsWithIgnoreCase(fileName, ".zip")) {
            throw new BusinessException("File format not supported. Supported only zip files.",
                    ExceptionId.EX_META_IMPORT_MODEL_INVALID_FILE_FORMAT);
        }

        Files.createDirectories(Paths.get(System.getProperty("catalina.base") + File.separator + "temp" + File.separator
                + "to_import" + File.separator + "model"));
        java.nio.file.Path path = Paths.get(System.getProperty("catalina.base") + File.separator + "temp"
                + File.separator + "to_import" + File.separator + "model" + File.separator + fileName);
        Files.deleteIfExists(path);
        InputStream in = attachment.getObject(InputStream.class);
        Files.copy(in, path);
        return path;
    }
}
