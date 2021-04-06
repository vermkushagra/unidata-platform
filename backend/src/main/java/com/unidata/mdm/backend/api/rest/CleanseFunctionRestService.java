package com.unidata.mdm.backend.api.rest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.xml.ElementClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import com.unidata.mdm.meta.CleanseFunctionExtendedDef;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.Node;
import com.unidata.mdm.backend.api.rest.converter.CleanseFunctionDataConverter;
import com.unidata.mdm.backend.api.rest.converter.CompositeCFConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFApply;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFCompositeResponse;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFCustomUploaderResponse;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFSaveStatus;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionData;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionGroup;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.service.cleanse.CFUtils;
import com.unidata.mdm.backend.service.cleanse.CleanseFunctionServiceExt;
import com.unidata.mdm.backend.service.cleanse.composite.CompositeFunctionMDAGRep;
import com.unidata.mdm.backend.util.JarUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class CleanseFunctionRestService.
 *
 * @author Michael Yashin. Created on 19.05.2015.
 */
@Path("meta/cleanse-functions")
@Api(value = "meta_cleanse-functions", description = "Функции очистки и обогащения данных", produces = "application/json")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class CleanseFunctionRestService extends AbstractRestService {
	/** The conversion service. */
	@Autowired
	ConversionService conversionService;

	/** The cleansefunction service. */
	@Autowired
	CleanseFunctionServiceExt cleansefunctionService;

	/**
	 * Find all.
	 *
	 * @return the response
	 * @throws CleanseFunctionExecutionException
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@ElementClass(response = CleanseFunctionGroup.class)
	@ApiOperation(value = "Список функций", notes = "", response = CleanseFunctionGroup.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response findAll() throws CleanseFunctionExecutionException {

		CleanseFunctionGroupDef cleanseFunctionGroupDef = cleansefunctionService.getAll();
		CleanseFunctionGroup result = (CleanseFunctionGroup) conversionService.convert(cleanseFunctionGroupDef,
				TypeDescriptor.forObject(cleanseFunctionGroupDef), TypeDescriptor.valueOf(CleanseFunctionGroup.class));

		return ok(result);
	}

	/**
	 * Gets the by id.
	 *
	 * @param id
	 *            the id
	 * @return the by id
	 * @throws CleanseFunctionExecutionException
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("{id}")
	@ElementClass(response = CleanseFunctionDefinition.class)
	@ApiOperation(value = "Получить запись по ID", notes = "", response = CleanseFunctionDefinition.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response getById(@PathParam("id") String id) throws CleanseFunctionExecutionException {

		CleanseFunctionExtendedDef functionDefinition = cleansefunctionService.getByID(id);
		CleanseFunctionDefinition function = (CleanseFunctionDefinition) conversionService.convert(functionDefinition,
				TypeDescriptor.forObject(functionDefinition), TypeDescriptor.valueOf(CleanseFunctionDefinition.class));

		return ok(function);
	}

	@DELETE
	@Path("{id}")
	@ElementClass(response = CleanseFunctionDefinition.class)
	@ApiOperation(value = "Удалить функцию по ID", notes = "", response = CleanseFunctionDefinition.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response delete(@PathParam("id") String id) throws Exception {
		cleansefunctionService.deleteFunction(id);
		return Response.accepted().build();
	}

	/**
	 * Execute.
	 *
	 * @param request
	 *            the request
	 * @return the response
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@ApiOperation(value = "Выполнение функции", notes = "", response = CleanseFunctionData.class)
	@Path(value = "/execute")
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 401, message = "Unathorized"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response execute(CleanseFunctionData request) {
		CleanseFunctionData response = new CleanseFunctionData();
		try {
			Map<String, Object> map = cleansefunctionService.executeSingle(
					CleanseFunctionDataConverter.convertRestSimpleToMetaSimpleAttr(request.getSimpleAttributes()),
					request.getFunctionName());
			response.setFunctionName(request.getFunctionName());
			response.setSimpleAttributes(CleanseFunctionDataConverter.convertMetaSimpleAttrToRestSimpleAttr(map));
			response.setResultCode("ok");
		} catch (CleanseFunctionExecutionException ex) {
			response.setResultCode("nok");
			response.setErrorMessage("Переданные параметры неверны!");
		}

		return ok(response);
	}

	/**
	 * Upload jar file.
	 *
	 * @param attachment
	 *            the attachment
	 * @return the response
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/upload")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Загрузить JAR с новыми функциями", notes = "", response = CFCustomUploaderResponse.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response uploadJarFile(@Multipart(value = "jarFile") Attachment attachment) throws Exception {
		if (!JarUtils.validateFileName(attachment)) {
			CFCustomUploaderResponse response = new CFCustomUploaderResponse();
			response.setStatus(CFSaveStatus.ERROR);
			return Response.accepted(response).build();
		}
		CFCustomUploaderResponse response = cleansefunctionService
				.preloadAndValidateCustomFunction(JarUtils.saveFileToLibFolder(attachment), true);
		return Response.accepted(response).build();
	}

	/**
	 * Apply.
	 *
	 * @param temporaryId
	 *            the temporary id
	 * @param request
	 *            the request
	 * @return the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws CleanseFunctionExecutionException
	 */
	@POST
	@Path("/apply/{temporaryId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Применить функцию", notes = "", response = String.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response apply(@PathParam(value = "temporaryId") String temporaryId, List<CFApply> request)
			throws CleanseFunctionExecutionException, IOException {
		cleansefunctionService.loadAndInit(temporaryId);
		cleansefunctionService.sendInitSignal(temporaryId);
		return Response.accepted(new UpdateResponse(true, temporaryId)).build();
	}

	/**
	 * Adds the composite cleanse function.
	 *
	 * @param functionId
	 *            the function id
	 * @param cleanseDef
	 *            the cleanse def
	 * @return the response
	 * @throws CleanseFunctionExecutionException
	 * @throws Exception
	 *             the exception
	 */
	@PUT
	@Path("{functionId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Обновить композитную функцию", notes = "", response = CFCompositeResponse.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response addCompositeCleanseFunction(@PathParam("functionId") String functionId,
			CleanseFunctionDefinition cleanseDef) throws CleanseFunctionExecutionException {
		CompositeCleanseFunctionDef cleanseFunctionDef = CompositeCFConverter.convert(cleanseDef);
		CompositeFunctionMDAGRep mdagRep = CFUtils.convertToGraph(cleanseFunctionDef);
		// 1. validate input, search for cycles
		List<List<Node>> cycles = CFUtils.findCycles(mdagRep);

		CFCompositeResponse response = new CFCompositeResponse();
		// 2. if cycles found return error status and list with cycles
		if (cycles != null && cycles.size() != 0) {
			response.setCycles(CompositeCFConverter.convert(cycles, mdagRep));
			response.setStatus(CFSaveStatus.ERROR);
			return Response.accepted(response).build();
		}
		// 3. If graph valid save custom cleanse function
		cleansefunctionService.removeFunctionById(functionId);
		cleansefunctionService.upsertCompositeCleanseFunction(cleanseDef.getName(),
				CompositeCFConverter.convert(cleanseDef));
		response.setStatus(CFSaveStatus.SUCCESS);

		return Response.accepted(response).build();
	}

	/**
	 * Creates the composite cleanse function.
	 *
	 * @param cleanseDef
	 *            the cleanse def
	 * @return the response
	 * @throws CleanseFunctionExecutionException
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Создать новую композитную функцию", notes = "", response = CFCompositeResponse.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response createCompositeCleanseFunction(CleanseFunctionDefinition cleanseDef)
			throws CleanseFunctionExecutionException {
		CompositeCleanseFunctionDef cleanseFunctionDef = CompositeCFConverter.convert(cleanseDef);
		CompositeFunctionMDAGRep mdagRep = CFUtils.convertToGraph(cleanseFunctionDef);
		// 1. validate input, search for cycles
		List<List<Node>> cycles = CFUtils.findCycles(mdagRep);

		CFCompositeResponse response = new CFCompositeResponse();
		// 2. if cycles found return error status and list with cycles
		if (cycles != null && cycles.size() != 0) {
			response.setCycles(CompositeCFConverter.convert(cycles, mdagRep));
			response.setStatus(CFSaveStatus.ERROR);
			return Response.accepted(response).build();
		} else if (cleansefunctionService.getByID(cleanseDef.getName()) != null) {
			response.setStatus(CFSaveStatus.ERROR);
			return Response.accepted(response).build();
		}
		// 3. If graph valid save custom cleanse function
		cleansefunctionService.upsertCompositeCleanseFunction(cleanseDef.getName(),
				CompositeCFConverter.convert(cleanseDef));
		response.setStatus(CFSaveStatus.SUCCESS);

		return Response.accepted(response).build();
	}

}
