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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.unidata.mdm.backend.api.rest.constants.SearchConstants;
import com.unidata.mdm.backend.api.rest.converter.SearchRequestConverters;
import com.unidata.mdm.backend.api.rest.converter.SearchResultToRestSearchResultConverter;
import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.search.SearchComplexRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchFormFieldRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchFormFieldsGroupRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchResultRO;
import com.unidata.mdm.backend.api.rest.util.SearchResultHitModifier;
import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.runtime.MeasurementContextName;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.FacetName;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.cxf.jaxrs.ext.xml.ElementClass;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.util.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mikhail Mikhailov
 *
 */
@Path(RestConstants.PATH_PARAM_SYSTEM)
@Api(value = "system", description = "Системное администрирование", produces = "application/json")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class SystemRestService extends AbstractRestService {
    /**
     * Logger.
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SystemRestService.class);

    @Autowired
    private SearchServiceExt searchService;

    /**
     * Modify search result for ui presentation
     */
    @Autowired
    private SearchResultHitModifier searchResultHitModifier;
    /**
     * Log appender name.
     */
    private static final String BACKEND_LOG_FILE_APPENDER_NAME = "BACKEND_LOG_FILE";

    private static final Comparator<java.nio.file.Path> LOG_FILE_COMPARATOR = (o1, o2) -> {

        FileTime ft1 = null;
        try { ft1 = Files.getLastModifiedTime(o1); }
        catch (IOException e) {}

        FileTime ft2 = null;
        try { ft2 = Files.getLastModifiedTime(o2); }
        catch (IOException e) {}

        long result = Objects.isNull(ft1) && Objects.isNull(ft2)
                ? 0
                : Objects.nonNull(ft1)
                    ? ft1.toMillis() - (Objects.isNull(ft2) ? 0 : ft2.toMillis())
                    : -1;

        return result < 0 ? -1 : result > 0 ? 1 : 0;
    };

    /**
     * Gets two last log records for this node.
     * @return response
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_LOGS)
    @Produces({"application/zip", "application/octet-stream"})
    @ElementClass(response = Object.class)
    @ApiOperation(
            value = "Загрузить последние логи с ноды.",
            notes = "",
            response = Object.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response logs() {

        String currentLogPath = null;

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        _l : for (Logger logger : context.getLoggerList()) {
            for (Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders(); index.hasNext();) {
                Appender<ILoggingEvent> appender = index.next();
                if (appender instanceof FileAppender) {
                    FileAppender<?> fileAppender = (FileAppender<?>) appender;
                    if (!BACKEND_LOG_FILE_APPENDER_NAME.equals(appender.getName())) {
                        continue;
                    }

                    currentLogPath = new File(fileAppender.getFile()).getAbsolutePath();
                    break _l;
                }
            }
        }

        if (StringUtils.isBlank(currentLogPath)) {
            throw new SystemRuntimeException("System log file appender not found. Check 'logback.xml'",
                    ExceptionId.EX_SYSTEM_LOG_FILE_APPENDER_NOT_FOUND_OR_MISCONFIGURED);
        }

        List<String> paths = new ArrayList<>(2);
        paths.add(currentLogPath);

        String previousLogPath = guessPreviousLogPath(currentLogPath);
        if (Objects.nonNull(previousLogPath)) {
            paths.add(previousLogPath);
        }

        String addressTag = getHSR().getLocalAddr().replace('.', '_') + "_" + Integer.valueOf(getHSR().getLocalPort());
        String fileName = "logs-"
                + addressTag + "-"
                + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ".zip";

        String encodedFilename = FileUtils.encodePath(fileName);

        return Response.ok(createStreamingOutputFoZippedLogs(paths))
                .encoding("UTF-8")
                .header("Content-Disposition", "attachment; filename="
                        + fileName
                        + "; filename*=UTF-8''"
                        + encodedFilename)
                .header("Content-Type", MediaType.APPLICATION_OCTET_STREAM_TYPE)
                .build();
    }

    @POST
    @Path("/incorrectPeriods")
    @ElementClass(response = SearchResultRO.class)
    @ApiOperation(value = "Найти записи с периодами актуальности null (+- бесконечность).", notes = "", response = SearchResultRO.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response search(SearchComplexRO request) {
        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_SEARCH_SIMPLE);
        MeasurementPoint.start();

        try {
            request.setFetchAll(false);

            if(request.getFacets() == null){
                request.setFacets(new ArrayList<>());
            }
            request.getFacets().add(FacetName.FACET_UN_RANGED.getValue());

            if(request.getReturnFields() == null){
                request.setReturnFields(new ArrayList<>());
            }
            request.getReturnFields().add(RecordHeaderField.FIELD_TO.getField());
            request.getReturnFields().add(RecordHeaderField.FIELD_FROM.getField());

            SearchFormFieldsGroupRO periodGroup = SearchFormFieldsGroupRO.createOrGroup();
            Calendar cal = Calendar.getInstance();
            cal.setTime(ValidityPeriodUtils.getGlobalValidityPeriodEnd());
            cal.add(Calendar.SECOND, 1);
            periodGroup.addFormField(new SearchFormFieldRO(SimpleDataType.TIMESTAMP,
                    RecordHeaderField.FIELD_TO.getField(),
                    ImmutablePair.of(cal.getTime(), null),
                    false,
                    SearchFormFieldRO.SearchTypeRO.RANGE));

            cal.setTime(ValidityPeriodUtils.getGlobalValidityPeriodStart());
            cal.add(Calendar.SECOND, -1);
            periodGroup.addFormField(new SearchFormFieldRO(SimpleDataType.TIMESTAMP,
                    RecordHeaderField.FIELD_FROM.getField(),
                    ImmutablePair.of(null, cal.getTime()),
                    false,
                    SearchFormFieldRO.SearchTypeRO.RANGE));

            if(request.getFormGroups() == null){
                request.setFormGroups(new ArrayList<>());
            }

            request.getFormGroups().add(periodGroup);

            SearchRequestContext ctx = SearchRequestConverters.from(request);


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
     * Guess previous log path.
     * @param currentLogPath current path
     * @return previous log path or null
     */
    private String guessPreviousLogPath(String currentLogPath) {

        String fileName = Paths.get(currentLogPath).getFileName().toString();
        String baseName = StringUtils.substringBefore(fileName, ".");

        java.nio.file.Path closed = Paths.get(Paths.get(currentLogPath).getParent().toString(), "closed");
        if (closed.toFile().isDirectory() && Files.isReadable(closed)) {

            try {
                java.nio.file.Path youngest = Files.list(closed)
                        .filter(p -> p.getFileName().toString().startsWith(baseName))
                        .max(LOG_FILE_COMPARATOR)
                        .orElse(null);

                return youngest == null
                        ? null
                        : youngest.toFile().getAbsolutePath();

            } catch (IOException ioe) {
                LOGGER.warn("Exception caught while listing log files in 'closed'", ioe);
            }
        }

        return null;
    }

    /**
     * Gets {@link StreamingOutput} for a context.
     * @param ctx the context
     * @return streaming output
     */
    private StreamingOutput createStreamingOutputFoZippedLogs(final List<String> paths) {

        return new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException {

                try (ZipOutputStream zos = new ZipOutputStream(output, StandardCharsets.UTF_8)) {

                    for (String path : paths) {
                        File f = new File(path);
                        try (InputStream is = new FileInputStream(f)) {

                            ZipEntry entry = new ZipEntry(f.getName());
                            entry.setTime(f.lastModified());
                            entry.setSize(f.length());
                            entry.setMethod(ZipEntry.DEFLATED);

                            zos.putNextEntry(entry);

                            byte[] buf = new byte[FileUtils.DEFAULT_BUFFER_SIZE];
                            int count = -1;
                            while ((count = is.read(buf, 0, buf.length)) != -1) {
                                zos.write(buf, 0, count);
                            }

                            zos.closeEntry();

                        } catch (Exception e) {
                            LOGGER.warn("Exception caught while reading file {}.", path, e);
                        }
                    }

                    zos.finish();

                } catch (Exception exc) {
                    LOGGER.warn("Exception caught while output logs (I/O).", exc);
                }
            }
        };
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
}
