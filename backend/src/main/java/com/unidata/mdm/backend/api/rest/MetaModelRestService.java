/**
 *
 */
package com.unidata.mdm.backend.api.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.api.rest.dto.meta.MetaDependencyRequestRO;
import com.unidata.mdm.backend.api.rest.dto.meta.ModelVersionRO;
import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaGraphRO;
import com.unidata.mdm.backend.api.rest.dto.meta.ie.converters.MetaGraphDTOToROConverter;
import com.unidata.mdm.backend.api.rest.dto.meta.ie.converters.MetaTypeROToDTOConverter;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext.UpdateModelRequestContextBuilder;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.MetadataException;
import com.unidata.mdm.backend.common.service.MetaDraftService;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.service.model.MetaDependencyService;
import com.unidata.mdm.backend.service.model.ie.dto.MetaGraph;
import com.unidata.mdm.backend.service.model.ie.dto.MetaType;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.CleanseFunctionDef;
import com.unidata.mdm.meta.CleanseFunctionExtendedDef;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;
import com.unidata.mdm.meta.ListOfCleanseFunctions;
import com.unidata.mdm.meta.Model;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Mikhail Mikhailov Meta model management REST interface.
 */
@Path(MetaModelRestService.SERVICE_PATH)
@Api(value = "meta_model", description = "Администрация метаданных", produces = "application/json")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class MetaModelRestService extends AbstractRestService {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(MetaModelRestService.class);

	/**
	 * Service path.
	 */
	public static final String SERVICE_PATH = "meta/model";
	/**
	 * Path param all.
	 */
	public static final String PATH_PARAM_ALL = "all";
	/**
	 * Storage id param.
	 */
	public static final String PATH_PARAM_STORAGE_ID = "id";
	/**
	 * Path param dependency.
	 */
	private static final String PATH_PARAM_DEPENDENCY = "dependency";
	/**
	 * Path param model name.
	 */
	private static final String PATH_PARAM_MODEL_NAME = "model_name";
	private static final String PATH_PARAM_APPLY_DRAFT = "apply_draft";
	private static final String PATH_PARAM_REMOVE_DRAFT = "remove_draft";
	/**
	 * File name (attachment param).
	 */
	public static final String DATA_PARAM_FILE = "file";
	/**
	 * Recreate he model or not.
	 */
	public static final String DATA_PARAM_RECREATE = "recreate";
	/**
	 * Meta model service.
	 */
	@Autowired
	private MetaModelService metaModelService;
	/**
	 * Meta dependency service.
	 */
	@Autowired
	private MetaDependencyService metaDependencyService;
	@Autowired
	private MetaDraftService metaDraftService;
	private String name = "DEFAULT";

	/**
	 * Constructor.
	 */
	public MetaModelRestService() {
		super();
	}

	/**
	 * Gets entities in paged fashion with defaults.
	 * 
	 * @param pageRequest
	 *            the page request
	 * @return list of entities
	 * @throws Exception
	 */
	@GET
	@Path("/" + PATH_PARAM_ALL)
	@ApiOperation(value = "Список идентификаторов моделей.", notes = "", response = List.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response findAll() {
		return ok(new RestResponse<>(metaModelService.getStorageIdsList()));
	}

	@GET
	@Path("/" + PATH_PARAM_MODEL_NAME)
	@ApiOperation(value = "Имя и версия метамодели.", notes = "", response = ModelVersionRO.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response modelName() {
		ModelVersionRO modelVersionRO = new ModelVersionRO();
		modelVersionRO.setStorageId("DEFAULT");
		modelVersionRO.setVersion(metaModelService.getRootGroup(null).getVersion().intValue());
		modelVersionRO.setName(name);
		return ok(new RestResponse<>(modelVersionRO));
	}

	/**
	 * Change or set metamodel name for given storage id.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/" + PATH_PARAM_MODEL_NAME)
	@ApiOperation(value = "Меняет имя метамодели для заданого storage id", notes = "", response = MetaGraphRO.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response modelName(ModelVersionRO modelVersionRO) {
		name = modelVersionRO.getName();
		return ok(new RestResponse<>(modelVersionRO));
	}

	/**
	 * Apply draft for the given storage id.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/" + PATH_PARAM_APPLY_DRAFT)
	@ApiOperation(value = "применяет черновик для заданого storage id", notes = "", response = MetaGraphRO.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response applyDraft(ModelVersionRO modelVersionRO) {
		metaDraftService.apply();
		return ok(new RestResponse<>(modelVersionRO));
	}
	/**
	 * Remove draft for the given storage id.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/" + PATH_PARAM_REMOVE_DRAFT)
	@ApiOperation(value = "удаляет черновик для заданого storage id", notes = "", response = MetaGraphRO.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response removeDraft(ModelVersionRO modelVersionRO) {
		metaDraftService.removeDraft();
		return ok(new RestResponse<>(modelVersionRO));
	}
	/**
	 * Gets model data associated with optional storage ID. Returns current, if no
	 * storage id specified.
	 * 
	 * @param id
	 *            storage id.
	 * @return character stream
	 */
	@GET
	@Path("{p:/?}{" + PATH_PARAM_STORAGE_ID + ": (([_a-zA-Z0-9\\-]{3,})?)}")
	@ApiOperation(value = "Получить модель в XML по опциональному storage ID.", notes = "", response = StreamingOutput.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	@Produces(MediaType.TEXT_XML)
	public Response getById(@PathParam(PATH_PARAM_STORAGE_ID) String id) throws Exception {

		final Model model = metaModelService.exportModel(id);
		final String encodedFilename = URLEncoder.encode(model.getStorageId() + "_"
				+ DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd_HH-mm-ss") + ".xml", "UTF-8");
		return Response.ok(createStreamingOutputFromModel(model)).encoding("UTF-8").header("Content-Disposition",
				"attachment; filename=" + model.getStorageId() + ".xml" + "; filename*=UTF-8''" + encodedFilename)
				.header("Content-Type", MediaType.TEXT_XML).build();
	}

	/**
	 * Saves binary large object.
	 * 
	 * @param id
	 *            golden record id
	 * @param attr
	 *            attribute
	 * @param fileAttachment
	 *            attachment object
	 * @return ok/nok
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("{p:/?}{" + PATH_PARAM_STORAGE_ID + ": (([_a-zA-Z0-9\\-]{3,})?)}")
	@ApiOperation(value = "Загрузить данные модели с опциональным storage ID.", notes = "", response = UpdateResponse.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response importModel(@ApiParam(value = "ID объекта") @PathParam(PATH_PARAM_STORAGE_ID) String id,
			@Multipart(value = DATA_PARAM_RECREATE) Boolean recreate,
			@Multipart(required = true, value = DATA_PARAM_FILE) Attachment fileAttachment) throws Exception {

		if (!MediaType.TEXT_XML_TYPE.equals(fileAttachment.getContentType())) {
			LOGGER.warn("Invalid content type rejected, while importing model.",
					fileAttachment.getContentType().toString());
			throw new MetadataException("Import model with the media type [{}] is not allowed.",
					ExceptionId.EX_META_IMPORT_MODEL_INVALID_CONTENT_TYPE, fileAttachment.getContentType().toString());
		}

		Model model = JaxbUtils.createModelFromInputStream(fileAttachment.getObject(InputStream.class));
		if (id != null) {
			model.setStorageId(id);
		}

		boolean isRecreate = recreate == null ? Boolean.FALSE : recreate;

		UpdateModelRequestContext ctx = new UpdateModelRequestContextBuilder()
				.enumerationsUpdate(model.getEnumerations()).sourceSystemsUpdate(model.getSourceSystems())
				.nestedEntityUpdate(model.getNestedEntities()).lookupEntityUpdate(model.getLookupEntities())
				.entitiesGroupsUpdate(model.getEntitiesGroup()).entityUpdate(model.getEntities())
				.relationsUpdate(model.getRelations())
				.cleanseFunctionsUpdate((model.getCleanseFunctions() == null) ? null
						: enrichDefaultFunctions(model.getCleanseFunctions()).getGroup())
				.storageId(id).isForceRecreate(isRecreate ? UpdateModelRequestContext.UpsertType.FULLY_NEW
						: UpdateModelRequestContext.UpsertType.ADDITION)
				.build();
		ctx.putToStorage(StorageId.DEFAULT_CLASSIFIERS, model.getDefaultClassifiers());
		metaModelService.upsertModel(ctx);
		metaDraftService.removeDraft();
		return ok(new UpdateResponse(true, model.getStorageId()));
	}

	/**
	 * Enrich list with default functions with composite and custom functions from
	 * model.
	 * 
	 * @param listOfCleanseFunctions
	 * @return
	 */
	private ListOfCleanseFunctions enrichDefaultFunctions(ListOfCleanseFunctions listOfCleanseFunctions) {
		CleanseFunctionGroupDef result = ModelUtils.createDefaultCleanseFunctionGroup();
		if (listOfCleanseFunctions == null || listOfCleanseFunctions.getGroup() == null) {
			return new ListOfCleanseFunctions().withGroup(result);
		}
		List<Serializable> list = listOfCleanseFunctions.getGroup()
				.getGroupOrCleanseFunctionOrCompositeCleanseFunction();
		for (Serializable obj : list) {
			if (obj instanceof CompositeCleanseFunctionDef) {
				result.getGroupOrCleanseFunctionOrCompositeCleanseFunction().add(obj);
			} else if (obj instanceof CleanseFunctionDef) {
				result.getGroupOrCleanseFunctionOrCompositeCleanseFunction().add(obj);
			} else if (obj instanceof CleanseFunctionExtendedDef) {
				result.getGroupOrCleanseFunctionOrCompositeCleanseFunction().add(obj);
			}
		}

		return new ListOfCleanseFunctions().withGroup(result);
	}

	/**
	 * Return dependency graph.
	 * 
	 * @param request
	 *            meta elements types.
	 * @return dependency graph.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/" + PATH_PARAM_DEPENDENCY)
	@ApiOperation(value = "Возвращает граф зависимостей между выбранными элементами метамодели", notes = "", response = MetaGraphRO.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response dependency(MetaDependencyRequestRO request) {
		Set<MetaType> forTypes = MetaTypeROToDTOConverter.convert(request.getForTypes());
		Set<MetaType> skipTypes = MetaTypeROToDTOConverter.convert(request.getSkipTypes());
		String storageId = request.getStorageId();
		MetaGraph result = metaDependencyService.calculateDependencies(storageId, forTypes, skipTypes);
		return ok(new RestResponse<>(MetaGraphDTOToROConverter.convert(result)));
	}

	/**
	 * Gets {@link StreamingOutput} for a context.
	 * 
	 * @param ctx
	 *            the context
	 * @return streaming output
	 */
	private StreamingOutput createStreamingOutputFromModel(final Model model) {
		final StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException {
				String modelAsString = JaxbUtils.marshalMetaModel(model);
				output.write(modelAsString.getBytes(StandardCharsets.UTF_8));
			}
		};

		return stream;
	}
}
