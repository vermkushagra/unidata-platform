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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang3.StringUtils;
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
}
