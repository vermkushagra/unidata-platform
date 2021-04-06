package com.unidata.mdm.backend.api.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.meta.EnumerationDefinitionRO;
import com.unidata.mdm.backend.common.service.MetaDraftService;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.meta.EnumerationDataType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Michael Yashin. Created on 19.05.2015.
 */
@Path("meta/enumerations")
@Api(value = "meta_enumerations", description = "Перечисления", produces = "application/json")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class EnumerationRestService extends AbstractRestService {

    @Autowired
    private ConversionService conversionService;
    @Autowired
    private MetaDraftService metaDraftService;
    @Autowired
    private MetaModelService metaModelService;

    @GET
    @ApiOperation(value = "Список перечислений", notes = "", response = EnumerationDefinitionRO.class, responseContainer = "List")
    @ApiResponses({
	    @ApiResponse(code = 200, message = "Request processed"),
	    @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response findAll(@QueryParam("draft")@DefaultValue("false") Boolean draft ){
		List<EnumerationDataType> result;
		if (draft) {
			result = metaDraftService.getEnumerationsList();
		} else {
			result = metaModelService.getEnumerationsList();
		}

        @SuppressWarnings("unchecked")
        List<EnumerationDefinitionRO> target = (List<EnumerationDefinitionRO>) conversionService
		    .convert(result, TypeDescriptor.collection(List.class,
		            TypeDescriptor.valueOf(EnumerationDataType.class)),
		            TypeDescriptor.collection(List.class, TypeDescriptor
		                    .valueOf(EnumerationDefinitionRO.class)));
        return ok(target);
    }

}
