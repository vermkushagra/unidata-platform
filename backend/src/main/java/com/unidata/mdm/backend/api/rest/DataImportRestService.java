package com.unidata.mdm.backend.api.rest;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.dataimport.ImportParams;
import com.unidata.mdm.backend.service.data.xlsximport.DataImportService;
import com.unidata.mdm.backend.util.FileUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class DataImportRestService.
 */
@Path("import/data")
@Api(value = "import", description = "Импорт данных", produces = "application/json")
@Consumes({"application/json"})
@Produces({"application/json"})
public class DataImportRestService extends AbstractRestService {

    /**
     * The data import service.
     */
    @Autowired
    private DataImportService dataImportService;

    /**
     * Import xlsx.

     * @param importParams the import params
     * @param attachment   the attachment
     * @return the response
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/xlsx")
    @ApiOperation(value = "Загрузка xlsx файла с данными.", notes = "", response = Object.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response importXLSX(
            @Multipart(required = true, value = RestConstants.DATA_IMPORT_PARAMS) ImportParams importParams,
            @Multipart(required = true, value = RestConstants.DATA_PARAM_FILE) Attachment attachment) {
        java.nio.file.Path path = FileUtils.saveFileTempFolder(attachment);
        dataImportService.importData(path.toFile(), importParams.getEntityName(), importParams.getSourceSystem(),
                new Date().toInstant().toString(), importParams.isMergeWithPreviousVersion());
        return ok(new RestResponse<>());
    }
}
