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

import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_MEASUREMENT_MARSHAL_FAILED;
import static com.unidata.mdm.backend.service.measurement.MeasurementValueXmlConverter.convert;
import static com.unidata.mdm.backend.service.measurement.MeasurementValueXmlConverter.convertToByteArray;
import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.util.Objects.isNull;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.api.rest.converter.MeasurementValueConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.api.rest.dto.measurement.MeasurementValueDto;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.service.MetaDraftServiceExt;
import com.unidata.mdm.backend.service.measurement.MeasurementValueXmlConverter;
import com.unidata.mdm.backend.service.measurement.MetaMeasurementService;
import com.unidata.mdm.backend.util.FileUtils;
import com.unidata.mdm.meta.MeasurementValueDef;
import com.unidata.mdm.meta.MeasurementValues;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/measurementValues")
@Api(value = "measure_values", description = "Единицы измерения", produces = "application/json")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class MeasureValuesRestService extends AbstractRestService {

    private static final String VALUE_ID = "valueId";
    private static final String IMPORT_FILE = "file";

    @Autowired
    private MetaMeasurementService metaMeasurementService;
    @Autowired
    private MetaDraftServiceExt metaDraftService;

    private static final Comparator<MeasurementValueDto> VALUE_DTO_COMPARATOR = (o1, o2) -> CASE_INSENSITIVE_ORDER
            .compare(o1.getName(), o2.getName());

    @GET
    @ApiOperation(value = "Получение всех величин", notes = "", response = Response.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response getAllMeasureValues(@QueryParam("draft") @DefaultValue(value = "false") Boolean draft)
            throws Exception {
        Collection<MeasurementValueDto> result;
        if (!draft) {
            result = metaMeasurementService.getAllValues().stream().map(MeasurementValueConverter::convert)
                    .sorted(VALUE_DTO_COMPARATOR).collect(Collectors.toList());
        } else {
            result = metaDraftService.getAllValues().stream().map(MeasurementValueConverter::convert)
                    .sorted(VALUE_DTO_COMPARATOR).collect(Collectors.toList());
        }
        return ok(new RestResponse<>(result));
    }

    @DELETE
    @Path("/{" + VALUE_ID + "}")
    @ApiOperation(value = "Удалить единицы измерения", notes = "", response = UpdateResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response removeValue(@ApiParam(value = "Id величины") @PathParam(VALUE_ID) String measureValueIds,
                                @QueryParam("draft") @DefaultValue(value = "false") Boolean draft) throws Exception {
        boolean result;
        if (!draft) {
            result = metaMeasurementService.removeValue(measureValueIds);
        } else {
            result = metaDraftService.removeValue(measureValueIds);
        }
        return ok(new RestResponse<>(result));
    }

    @GET
    @Path("/batchDelete")
    @ApiOperation(value = "Удалить единицы измерения", notes = "Все или не одной", response = UpdateResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response removeValues(@ApiParam(value = "Id величин") @QueryParam(VALUE_ID) List<String> measureValueIds,
                                 @QueryParam("draft") @DefaultValue(value = "false") Boolean draft) throws Exception {
        if (measureValueIds.isEmpty()) {
            return ok(new RestResponse<>());
        }
        boolean result;
        if (!draft) {
            result = metaMeasurementService.batchRemove(measureValueIds, false, false);
        } else {
            result = metaDraftService.batchRemove(measureValueIds, false, false);
        }
        return ok(new RestResponse<>(result));
    }

    @POST
    @Path("/import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(value = "Загрузить единицы измерения", notes = "Поддерживается только xml", response = UpdateResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response importValues(
            @ApiParam(value = "Импортируемый фаил") @Multipart(required = true, value = IMPORT_FILE) Attachment fileAttachment,
            @QueryParam("draft") @DefaultValue(value = "false") Boolean draft) throws Exception {
        if (isNull(fileAttachment)) {
            return okOrNotFound(null);
        }
        java.nio.file.Path file = FileUtils.saveFileTempFolder(fileAttachment);
        try {
            String fileName = fileAttachment.getDataHandler().getDataSource().getName();
            if (!fileName.endsWith("xml")) {
                String contentType = fileAttachment.getContentType().toString();
                throw new BusinessException("Import measurement values with the media type [{}] is not allowed.",
                        ExceptionId.EX_MEASUREMENT_IMPORT_TYPE_UNSUPPORTED, contentType);
            }
            MeasurementValues values = convert(file.toFile());
            if (CollectionUtils.isEmpty(values.getValue())) {
                throw new BusinessException("Import measurement values failed. Empty definition",
                        ExceptionId.EX_MEASUREMENT_IMPORT_EMPTY);
            }

            if (!draft) {
                for (MeasurementValueDef value : values.getValue()) {
                    metaMeasurementService.saveValue(convert(value));
                }
            } else {
                for (MeasurementValueDef value : values.getValue()) {
                    metaDraftService.saveValue(convert(value));
                }
            }
            return ok(new RestResponse<>());
        } finally {
            file.toFile().delete();
        }
    }

    @GET
    @Path("/export")
    @Produces(MediaType.TEXT_XML)
    @ApiOperation(value = "Выгрузить единицы измерения", notes = "", response = UpdateResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response exportValues(@ApiParam(value = "Id величины") @QueryParam(VALUE_ID) List<String> measureValueIds,
                                 @QueryParam("draft") @DefaultValue(value = "false") Boolean draft) throws Exception {
        if (measureValueIds.isEmpty()) {
            return okOrNotFound(null);
        }

        Collection<MeasurementValueDef> measurementValue = null;
        if (draft) {
            measurementValue = measureValueIds.stream()
                    .map(metaDraftService::getValueById).filter(Objects::nonNull)
                    .map(MeasurementValueXmlConverter::convert).collect(Collectors.toList());
        } else {
            measurementValue = measureValueIds.stream()
                    .map(metaMeasurementService::getValueById).filter(Objects::nonNull)
                    .map(MeasurementValueXmlConverter::convert).collect(Collectors.toList());
        }

        if (measurementValue.isEmpty()) {
            return okOrNotFound(null);
        }
        MeasurementValues measurementValueDef = new MeasurementValues().withValue(measurementValue);
        final String encodedFilename = URLEncoder.encode(
                "values_" + DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd_HH-mm-ss") + ".xml",
                "UTF-8");
        return Response.ok(outPutSteam(measurementValueDef)).encoding("UTF-8")
                .header("Content-Disposition",
                        "attachment; filename=" + encodedFilename + "; filename*=UTF-8''" + encodedFilename)
                .header("Content-Type", MediaType.TEXT_XML).build();
    }

    private StreamingOutput outPutSteam(MeasurementValues measurementValue) throws Exception {
        return output -> {
            try {
                output.write(convertToByteArray(measurementValue));
            } catch (JAXBException e) {
                throw new BusinessException("Marshaling is failed", EX_MEASUREMENT_MARSHAL_FAILED);
            }
        };
    }
}
