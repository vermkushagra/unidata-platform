package com.unidata.mdm.backend.api.rest;

import static com.unidata.mdm.backend.api.rest.constants.SearchConstants.SEARCH_PARAM_TEXT;
import static com.unidata.mdm.backend.common.search.FormField.strictString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import com.unidata.mdm.backend.api.rest.converter.clsf.ClsfDTOToROConverter;
import com.unidata.mdm.backend.api.rest.converter.clsf.ClsfNodeDTOToROConverter;
import com.unidata.mdm.backend.api.rest.converter.clsf.ClsfNodeROToDTOConverter;
import com.unidata.mdm.backend.api.rest.converter.clsf.ClsfROToDTOConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.api.rest.dto.XlsxAttachmentWrapper;
import com.unidata.mdm.backend.api.rest.dto.XlsxClassifierWrapper;
import com.unidata.mdm.backend.api.rest.dto.XmlAttachmentWrapper;
import com.unidata.mdm.backend.api.rest.dto.XmlClassifierWrapper;
import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfEntStatRO;
import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeRO;
import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfRO;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext.SaveLargeObjectRequestContextBuilder;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.UpsertUserEventRequestContext;
import com.unidata.mdm.backend.common.context.UpsertUserEventRequestContext.UpsertUserEventRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.common.dto.security.UserEventDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.configuration.application.UnidataConfigurationProperty;
import com.unidata.mdm.backend.service.classifier.ClsfService;
import com.unidata.mdm.backend.service.search.util.ClassifierDataHeaderField;
import com.unidata.mdm.backend.service.security.UserService;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.FileUtils;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.constants.UserMessageConstants;
import com.unidata.mdm.classifier.ClassifierDef;
import com.unidata.mdm.classifier.FullClassifierDef;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import reactor.core.publisher.Flux;


/**
 * Rest service responsible for CRUD operation over classifiers.
 */
@Path("data/classifier")
@Api(value = "classifier", description = "REST API для работы с класификаторами", produces = "application/json")
public class ClassifierRestService extends AbstractRestService /*implements ConfigurationUpdatesConsumer*/ {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierRestService.class);

    /** The Constant CLASSIFIER_NAME. */
    // batch of defined rest opinions names//
    private static final String CLASSIFIER_NAME = "classifierName";

    /** The Constant OWN_NODE_ID. */
    private static final String OWN_NODE_ID = "ownNodeId";

    /** The Constant VIEW. */
    private static final String VIEW = "view";

    /** The Constant DATA_PARAM_FILE. */
    private static final String DATA_PARAM_FILE = "file";
    /** The Constant TO_XML. */
    // private static final String TO_XML = "TO_XML";
    // /** The Constant TO_XLSX. */
    // private static final String TO_XLSX = "TO_XLSX";
    //
    // /** The Constant EXPORT_TYPE. */
    // private static final String EXPORT_TYPE = "EXPORT_TYPE";
    /** The Constant STRATEGY. */
    private static final String STRATEGY = "resolvingStrategy";


    /**
     * System resource for classifiers.
     */
    private static final String CLASSIFIER_MANAGEMENT = "ADMIN_CLASSIFIER_MANAGEMENT";

    /** Classifier service!. */
    @Autowired
    private ClsfService classifierService;

    /**
     * Search service.
     */
    @Autowired
    private SearchService searchService;

    /** The model service. */
    @Autowired
    private MetaModelService modelService;

    /** The conversion service. */
    @Autowired
    private ConversionService conversionService;

    /** The user service. */
    @Autowired
    private UserService userService;

    /** The data record service. */
    @Autowired
    private DataRecordsService dataRecordService;

    // TODO: Move to service
    /** The executor. */
    private final ThreadPoolExecutor importThreadsExecutor = new ThreadPoolExecutor(
            (Integer) UnidataConfigurationProperty.CLASSIFIER_IMPORT_THREADS_POOL_SIZE.getDefaultValue().get(),
            (Integer) UnidataConfigurationProperty.CLASSIFIER_IMPORT_THREADS_POOL_SIZE.getDefaultValue().get(),
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new CustomizableThreadFactory("classifierImportWorker - ")
    );

    /** The executor. */
    private final ThreadPoolExecutor exportThreadsExecutor = new ThreadPoolExecutor(
            (Integer) UnidataConfigurationProperty.CLASSIFIER_EXPORT_THREADS_POOL_SIZE.getDefaultValue().get(),
            (Integer) UnidataConfigurationProperty.CLASSIFIER_EXPORT_THREADS_POOL_SIZE.getDefaultValue().get(),
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new CustomizableThreadFactory("classifierExportWorker - ")
    );

    @PreDestroy
    public void preDestroy() {
        importThreadsExecutor.shutdown();
        exportThreadsExecutor.shutdown();
    }

    // /** The clsf import. */
    // @Autowired
    // @Qualifier("clsfImport")
    // private DataImportService clsfImport;
    //
    // /** The clsf export. */
    // @Autowired
    // @Qualifier("clsfExport")
    // private DataExportService clsfExport;

    /**
     * Creates the classifier.
     *
     * @param classifier
     *            the classifier
     * @return the response
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + CLASSIFIER_MANAGEMENT + "').isCreate()")
    @ApiOperation(value = "Создать класификатор", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response createClassifier(final ClsfRO classifier) {
        classifierService.createClassifier(ClsfROToDTOConverter.convert(classifier), true);
        return ok(new RestResponse<>(
                ClsfDTOToROConverter.convert(classifierService.getClassifierByName(classifier.getName()))));
    }

    /**
     * Gets the classifiers.
     *
     * @return the classifiers
     */
    @GET
    @Produces({ "application/json" })
    @ApiOperation(value = "Получить плоский список имен классификаторов", response = ClassifiersResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getClassifiers() {
        List<ClsfDTO> classifiers = classifierService.getAllClassifiersWithoutDescendants();
        return ok(ClsfDTOToROConverter.convert(classifiers));
    }

    /**
     * Gets the classifier by name.
     *
     * @param classifierName
     *            the classifier name
     * @return the classifier by name
     */
    @GET
    @Produces({ "application/json" })
    @Path("/{" + CLASSIFIER_NAME + "}")
    @ApiOperation(value = "Получить класификатор по имени", response = ClassifierResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getClassifierByName(
            @Nonnull @ApiParam(value = "Имя классификатора") @PathParam(CLASSIFIER_NAME) String classifierName) {
        ClsfDTO classifier = classifierService.getClassifierByName(classifierName);
        if (Objects.isNull(classifier)) {
            return ok(new RestResponse<>(null));
        }
        return ok(new RestResponse<>(ClsfDTOToROConverter.convert(classifier)));
    }

    /**
     * Gets the classifier by text.
     *
     * @param classifierName
     *            the classifier name
     * @param text
     *            the text
     * @return the classifier by text
     */
    @GET
    @Path("/{" + CLASSIFIER_NAME + "}/tree")
    @Produces({ "application/json" })
    @ApiOperation(value = "Получить класификатор по имени", response = SearchResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getClassifierByText(
            @Nonnull @ApiParam(value = "Имя классификатора") @PathParam(CLASSIFIER_NAME) String classifierName,
            @Nonnull @ApiParam(value = "Поисковый текст") @QueryParam(SEARCH_PARAM_TEXT) String text) {

        ClsfDTO classifier = classifierService.findNodes(classifierName, text);
        return ok(new RestResponse<>(ClsfNodeDTOToROConverter.convert(classifier.getRootNode(), classifierName)));
    }

    /**
     * Update classifier.
     *
     * @param classifierName
     *            the classifier name
     * @param classifier
     *            the classifier
     * @return the response
     */
    @PUT
    @Path("/{" + CLASSIFIER_NAME + "}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + CLASSIFIER_MANAGEMENT + "').isUpdate()")
    @ApiOperation(value = "Обновить параметры классфикикатора", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response updateClassifier(
            @Nonnull @ApiParam(value = "Имя классификатора") @PathParam(CLASSIFIER_NAME) String classifierName,
            @Nonnull final ClsfRO classifier) {
        classifierService.updateClassifier(ClsfROToDTOConverter.convert(classifier));
        return ok(new RestResponse<>(
                ClsfDTOToROConverter.convert(classifierService.getClassifierByName(classifier.getName()))));
    }

    /**
     * Removes the classifier.
     *
     * @param classifierName
     *            the classifier name
     * @return the response
     */
    @DELETE
    @Path("/{" + CLASSIFIER_NAME + "}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + CLASSIFIER_MANAGEMENT + "').isDelete()")
    @ApiOperation(value = "Удалить классификатор", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response removeClassifier(
            @Nonnull @ApiParam(value = "Имя классификатора") @PathParam(CLASSIFIER_NAME) String classifierName) {
        classifierService.removeClassifier(classifierName, true);
        return ok(new RestResponse<>());
    }

    /**
     * Adds the node.
     *
     * @param classifierName
     *            the classifier name
     * @param toAdd
     *            the to add
     * @return the response
     */
    @POST
    @Path("/{" + CLASSIFIER_NAME + "}/node")
    @Consumes({ "application/json", MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ "application/json" })
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource(#classifierName).isCreate()")
    @ApiOperation(value = "Добавление ноды", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response addNode(
            @Nonnull @ApiParam(value = "Имя классификатора") @PathParam(CLASSIFIER_NAME) String classifierName,
            @Nonnull final ClsfNodeRO toAdd) {
        toAdd.setClassifierName(classifierName);
        final ClsfNodeDTO clsfNodeDTO =
                classifierService.addNewNodeToClassifier(classifierName, ClsfNodeROToDTOConverter.convert(toAdd), true);
        return ok(new RestResponse<>(
                ClsfNodeDTOToROConverter.convert(clsfNodeDTO, classifierName)
        ));
    }

    /**
     * Update node.
     *
     * @param classifierName
     *            the classifier name
     * @param ownNodeId
     *            the own node id
     * @param toUpdate
     *            the to update
     * @return the response
     */
    @PUT
    @Path("/{" + CLASSIFIER_NAME + "}/node/{" + OWN_NODE_ID + "}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource(#classifierName).isUpdate()")
    @ApiOperation(value = "Редактирование ноды классификатора", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response updateNode(
            @Nonnull @ApiParam(value = "Имя классификатора") @PathParam(CLASSIFIER_NAME) String classifierName,
            @Nonnull @ApiParam(value = "Уникальный id") @PathParam(OWN_NODE_ID) String ownNodeId,
            @Nonnull final ClsfNodeRO toUpdate) {
        final boolean hasData = getClsStat(classifierName, ownNodeId)
                .stream()
                .anyMatch(clsfEntStatRO -> clsfEntStatRO.getCount() > 0);
        toUpdate.setId(ownNodeId);
        toUpdate.setClassifierName(classifierName);
        classifierService.updateClassifierNode(classifierName, ClsfNodeROToDTOConverter.convert(toUpdate), true, hasData);
        return ok(new RestResponse<>(ClsfNodeDTOToROConverter
                .convert(classifierService.getNodeWithAttrs(ownNodeId, classifierName, false), classifierName)));
    }

    /**
     * Removes the node cascade.
     *
     * @param classifierName
     *            the classifier name
     * @param ownNodeId
     *            the own node id
     * @return the response
     */
    @DELETE
    @Path("/{" + CLASSIFIER_NAME + "}/node/{" + OWN_NODE_ID + "}")
    @Produces({ "application/json" })
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource(#classifierName).isDelete()")
    @ApiOperation(value = "Удаляет ноду и ее детей", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response removeNodeCascade(
            @Nonnull @ApiParam(value = "Имя классификатора") @PathParam(CLASSIFIER_NAME) String classifierName,
            @Nonnull @ApiParam(value = "Уникальный id") @PathParam(OWN_NODE_ID) String ownNodeId
    ) {
        classifierService.removeNode(classifierName, ownNodeId,true);
        return ok(new RestResponse<>());
    }

    /**
     * Gets the classifier node.
     *
     * @param classifierName
     *            the classifier name
     * @param ownNodeId
     *            the own node id
     * @param view
     *            the view
     * @return the classifier node
     */
    @GET
    @Path("/{" + CLASSIFIER_NAME + "}/node/{" + OWN_NODE_ID + "}")
    @Produces({ "application/json" })
    // @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource(#classifierName).isRead()")
    @ApiOperation(value = "Получить все данные ноды", response = NodeResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getClassifierNode(
            @Nonnull @ApiParam(value = "Имя классификатора") @PathParam(CLASSIFIER_NAME) String classifierName,
            @Nonnull @ApiParam(value = "Уникальный id") @PathParam(OWN_NODE_ID) String ownNodeId,
            @ApiParam(value = "Представление") @QueryParam(VIEW) @DefaultValue("EMPTY") View view,
            @ApiParam(value = "Проверять наличие данных") @QueryParam("checkData") @DefaultValue(value = "true") boolean checkData) {
        ClsfNodeDTO result = null;
        switch (view) {
            case EMPTY:
                result = classifierService.getNodeByNodeId(ownNodeId, classifierName);
                break;
            case META:
                result = classifierService.getNodeWithAttrs(ownNodeId, classifierName, false);
                break;
            case DATA:
                result = classifierService.getNodeWithAttrs(ownNodeId, classifierName, true);
                break;
            case PATH:
                result = classifierService.buildBranchToRoot(Collections.singletonList(ownNodeId), classifierName);
                break;
            default:
                break;
        }
        ClsfNodeRO roResult = ClsfNodeDTOToROConverter.convert(result, classifierName);

        if (checkData && roResult != null) {
            roResult.setHasData(getClsStat(classifierName, ownNodeId)
                    .stream()
                    .anyMatch(clsfEntStatRO -> clsfEntStatRO.getCount() > 0));
        }
        return ok(new RestResponse<>(roResult));
    }


    /**
     * Gets the classifier stat.
     *
     * @param classifierName
     *            the classifier name
     * @param ownNodeId
     *            the own node id
     * @return the classifier stat
     */
    @GET
    @Path("entities-stat/{" + CLASSIFIER_NAME + "}/node/{" + OWN_NODE_ID + "}")
    @Produces({ "application/json" })
    @ApiOperation(value = "Получить статистику ноды", response = ClsfEntStatRO.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getClassifierStat(
            @Nonnull @ApiParam(value = "Имя классификатора") @PathParam(CLASSIFIER_NAME) String classifierName,
            @Nonnull @ApiParam(value = "Уникальный id") @PathParam(OWN_NODE_ID) String ownNodeId) {
        List<ClsfEntStatRO> result = getClsStat(classifierName, ownNodeId);
        return ok(new RestResponse<>(result));
    }

	/**
	 * Gets the cls stat.
	 *
	 * @param classifierName the classifier name
	 * @param ownNodeId the own node id
	 * @return the cls stat
	 */
	private List<ClsfEntStatRO> getClsStat(String classifierName, String ownNodeId) {
		List<ClsfEntStatRO> result = new ArrayList<>();
		result.addAll(modelService.getLookupEntitiesList().stream()
				.filter(le -> le.getClassifiers() != null
						&& le.getClassifiers().stream().anyMatch(cl -> StringUtils.equals(classifierName, cl)))
				.map(ClsfEntStatRO::new).collect(Collectors.toList()));
		result.addAll(modelService.getEntitiesList().stream()
				.filter(le -> le.getClassifiers() != null
						&& le.getClassifiers().stream().anyMatch(cl -> StringUtils.equals(classifierName, cl)))
				.map(ClsfEntStatRO::new).collect(Collectors.toList()));
		for (ClsfEntStatRO r : result) {
			//todo replace to complex request (write count only code)
			if (StringUtils.isEmpty(ownNodeId) || StringUtils.equals("null", ownNodeId)) {
				SearchRequestContext ctx = SearchRequestContext.forEtalon(EntitySearchType.CLASSIFIER, r.getName())
					   .search(SearchRequestType.TERM)
					   .searchFields(Collections.singletonList(ClassifierDataHeaderField.FIELD_NAME.getField()))
					   .countOnly(true)
					   .totalCount(true)
					   .onlyQuery(false)  // Skip security filters
					   .values(Collections.singletonList(classifierName))
					   .asOf(new Date())
					   .build();
				r.setCount(searchService.search(ctx).getTotalCount());
			} else {
				String classifierNodeField = String.join(".", classifierName,
				        ClassifierDataHeaderField.FIELD_NODES.getField(),
				        ClassifierDataHeaderField.FIELD_NODE_ID.getField());

				FormField classifierField = strictString(ClassifierDataHeaderField.FIELD_NAME.getField(), classifierName);
				FormField textField = strictString(classifierNodeField, ownNodeId);
				SearchRequestContext ctx = SearchRequestContext.forEtalon(EntitySearchType.CLASSIFIER, r.getName())
						.form(FormFieldsGroup.createAndGroup(classifierField, textField))
						.asOf(new Date())
						.countOnly(true)
						.totalCount(true)
						.onlyQuery(false) // Skip security filters
						.build();
				r.setCount(searchService.search(ctx).getTotalCount());
			}
		}
		return result;
	}

    /**
     * Saves binary large object.
     *
     * @param resolvingStrategy
     *            the resolving strategy
     * @param fileAttachment
     *            attachment object
     * @return ok/nok
     * @throws Exception
     *             the exception
     */
    @POST
    @Path("/import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource('"
            + CLASSIFIER_MANAGEMENT + "').isCreate()")
    @ApiOperation(value = "Загрузить классификатор", notes = "", response = UpdateResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response importModel(
            @ApiParam(value = "Стратегия импорта") @Multipart(required = true, value = STRATEGY) String resolvingStrategy,
            @ApiParam(value = "Импортируемый фаил") @Multipart(required = true, value = DATA_PARAM_FILE) Attachment fileAttachment)
            throws Exception {
        if (Objects.isNull(fileAttachment)) {
            return okOrNotFound(null);
        }
        final String userName = SecurityUtils.getCurrentUserName();
        ResolvingStrategy strategy = ResolvingStrategy.valueOf(resolvingStrategy);
        java.nio.file.Path path = FileUtils.saveFileTempFolder(fileAttachment);
        String fileName = fileAttachment.getDataHandler().getDataSource().getName();
        if (!fileName.endsWith("xml") && !fileName.endsWith("xlsx")) {
            String contentType = fileAttachment.getContentType().toString();
            LOGGER.warn("Invalid content type rejected, while importing classifier.", contentType);
            throw new BusinessException("Import classifier with the media type [{}] is not allowed.",
                    ExceptionId.EX_CLASSIFIER_IMPORT_TYPE_UNSUPPORTED, contentType);
        }
        importThreadsExecutor.submit(() -> {
            try {
                final String userNoticeContent = importClassifier(path, strategy, userName);
                try {
                    UpsertUserEventRequestContext uueCtx = new UpsertUserEventRequestContextBuilder()
                            .login(userName)
                            .type(ClsfService.CLASSIFIERS_IMPORT)
                            .content(userNoticeContent)
                            .build();
                    userService.upsert(uueCtx);
                } catch (Exception e1) {
                    LOGGER.error("Cannot create report file due to an exception", e1);
                }
            } catch (Exception e) {
                LOGGER.error("Cannot import file due to an exception", e);
                if (e.getCause() instanceof SystemRuntimeException) {
                    e = (SystemRuntimeException) e.getCause();
                }
                String eMessage = e instanceof SystemRuntimeException
                        ? formatSystemRuntimeException((SystemRuntimeException) e) : e.getLocalizedMessage();
                List<String> lines = Arrays.asList("Cannot import file" + fileAttachment.getContentType() + "due to: ",
                        eMessage);
                String reportFileName = "report" + IdUtils.v4String() + ".txt";
                try {
                    Files.write(Paths.get(reportFileName), lines, Charset.forName("UTF-8"));
                    UpsertUserEventRequestContext uueCtx = new UpsertUserEventRequestContextBuilder()
                            .login(userName)
                            .type(ClsfService.CLASSIFIERS_IMPORT)
                            .content(MessageUtils.getMessage(UserMessageConstants.CLASSIFIER_IMPORT_UNSUCCESS))
                            .build();
                    UserEventDTO userEventDTO = userService.upsert(uueCtx);
                    // save result and attach it to the early created user event
                    SaveLargeObjectRequestContext slorCTX = new SaveLargeObjectRequestContextBuilder()
                            .eventKey(userEventDTO.getId()).mimeType("text/plain").binary(true)
                            .inputStream(
                                    Files.newInputStream(Paths.get(reportFileName), StandardOpenOption.DELETE_ON_CLOSE))
                            .filename(reportFileName).build();
                    dataRecordService.saveLargeObject(slorCTX);
                } catch (Exception e1) {
                    LOGGER.error("Cannot create report file due to an exception", e1);
                }
            }
        });
        return ok(new RestResponse<>());
    }

    /**
     * Format and retunn exception message.
     * @param sre runtime exception
     * @return formatted message
     */
    private String formatSystemRuntimeException(SystemRuntimeException sre) {
        return "Сообщение [" + MessageUtils.getMessage(sre.getId().getCode(), sre.getArgs())
                + "], Системный код ошибки [" + sre.getId().name() + "]";
    }

    /**
     * Export to xml.
     *
     * @param classifierName
     *            the classifier name
     * @return xml classifier
     * @throws Exception
     *             the exception
     */
    @GET
    @Path("/{" + CLASSIFIER_NAME + "}/export/xml")
    // @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).getRightsForResource(#classifierName).isRead()")
    @ApiOperation(value = "Получить классификатор в xml", notes = "", response = StreamingOutput.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response exportToXml(
            @Nonnull @ApiParam(value = "Имя классификатора") @PathParam(CLASSIFIER_NAME) String classifierName)
            throws Exception {
        ClsfDTO classifier = classifierService.getClassifierByNameWithAllNodes(classifierName);
        if (Objects.isNull(classifier)) {
            return okOrNotFound(null);
        }
        final String userName = SecurityUtils.getCurrentUserName();
        exportThreadsExecutor.submit(() -> {
            String encodedFilename = null;
            try {

                encodedFilename = URLEncoder.encode(classifierName + "_"
                        + DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd_HH-mm-ss") + ".xml", "UTF-8");
                XmlClassifierWrapper xmlClassifierWrapper = new XmlClassifierWrapper(classifier);
                StreamingOutput output = conversionService.convert(xmlClassifierWrapper, StreamingOutput.class);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                output.write(bao);
                InputStream is = new ByteArrayInputStream(bao.toByteArray());
                UpsertUserEventRequestContext uueCtx = new UpsertUserEventRequestContextBuilder()
                        .login(userName)
                        .type("CLASSIFIERS_EXPORT")
                        .content(MessageUtils.getMessage(UserMessageConstants.CLASSIFIER_EXPORT_XML_SUCCESS))
                        .build();
                UserEventDTO userEventDTO = userService.upsert(uueCtx);
                // save result and attach it to the early created user event
                SaveLargeObjectRequestContext slorCTX = new SaveLargeObjectRequestContextBuilder()
                        .eventKey(userEventDTO.getId()).mimeType("text/xml").binary(true).inputStream(is)
                        .filename(encodedFilename).build();
                dataRecordService.saveLargeObject(slorCTX);
            } catch (Exception e) {
                LOGGER.error("Cannot export classifiers due to an exception", e);
                String eMessage = ExceptionUtils.getStackTrace(e);
                List<String> lines = Arrays.asList("Cannot export classifiers to " + encodedFilename + " due to: ",
                        eMessage);
                String reportFileName = "report" + IdUtils.v4String() + ".txt";
                try {
                    Files.write(Paths.get(reportFileName), lines, Charset.forName("UTF-8"));
                    UpsertUserEventRequestContext uueCtx = new UpsertUserEventRequestContextBuilder()
                            .login(userName)
                            .type("CLASSIFIERS_EXPORT")
                            .content(MessageUtils.getMessage(UserMessageConstants.CLASSIFIER_EXPORT_XML_UNSUCCESS))
                            .build();
                    UserEventDTO userEventDTO = userService.upsert(uueCtx);
                    // save result and attach it to the early created user event
                    SaveLargeObjectRequestContext slorCTX = new SaveLargeObjectRequestContextBuilder()
                            .eventKey(userEventDTO.getId()).mimeType("text/plain").binary(true)
                            .inputStream(
                                    Files.newInputStream(Paths.get(reportFileName), StandardOpenOption.DELETE_ON_CLOSE))
                            .filename(reportFileName).build();
                    dataRecordService.saveLargeObject(slorCTX);
                } catch (Exception e1) {
                    LOGGER.error("Cannot create report file due to an exception", e1);
                }
            }
        });

        return ok(new RestResponse<>());
    }

    /**
     * Import classifier from file.
     *
     * @param path the path
     * @param strategy            resolving strategy.
     */
    private String importClassifier(java.nio.file.Path path, ResolvingStrategy strategy, String importFromUser) {
        final Object source = path.toString().endsWith("xml") ?
                new XmlAttachmentWrapper(path) :
                new XlsxAttachmentWrapper(path);
        final FullClassifierDef fullClassifierDef = conversionService.convert(source, FullClassifierDef.class);

        final ClassifierDef classifier = fullClassifierDef.getClassifier();
        if (Objects.isNull(fullClassifierDef) || Objects.isNull(classifier)) {
            LOGGER.warn("Imported data is empty");
            throw new DataProcessingException("Imported data is empty", ExceptionId.EX_CLASSIFIER_IMPORT_DATA_EMPTY);
        }

        if (strategy == ResolvingStrategy.CODE) {
            if (StringUtils.isBlank(classifier.getCodePattern())) {
                throw new DataProcessingException(
                        "Can't import classifier by code without code pattern",
                        ExceptionId.EX_CLASSIFIER_IMPORT_BY_CODE_WITHOUT_PATTERN
                );
            }
        }
        final boolean isUpdate = strategy == ResolvingStrategy.CODE ?
                classifierService.addFullFilledClassifierByCode(fullClassifierDef, importFromUser) :
                classifierService.addFullFilledClassifierByIds(fullClassifierDef, importFromUser);
        return MessageUtils.getMessage(
                isUpdate ? UserMessageConstants.CLASSIFIER_IMPORT_UPDATE_STEP1_SUCCESS :
                        UserMessageConstants.CLASSIFIER_IMPORT_NEW_SUCCESS
        );
    }

    /**
     * Export to xlsx.
     *
     * @param classifierName
     *            the classifier name
     * @return xslx classifier
     * @throws Exception
     *             the exception
     */
    @GET
    @Path("/{" + CLASSIFIER_NAME + "}/export/xlsx")
    @ApiOperation(value = "Получить классификатор в xml", notes = "", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response exportToXlsx(
            @Nonnull @ApiParam(value = "Имя классификатора") @PathParam(CLASSIFIER_NAME) String classifierName)
            throws Exception {
        if (Objects.isNull(classifierName)) {
            return okOrNotFound(null);
        }
        ClsfDTO classifier = classifierService.getClassifierByNameWithAllNodes(classifierName);
        final String userName = SecurityUtils.getCurrentUserName();
        exportThreadsExecutor.submit(() -> {
            String encodedFilename = null;
            try {
                encodedFilename = URLEncoder.encode(classifierName + "_"
                        + DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd_HH-mm-ss") + ".xlsx", "UTF-8");
                XlsxClassifierWrapper xlsxClassifierWrapper = new XlsxClassifierWrapper(classifier);
                StreamingOutput output = conversionService.convert(xlsxClassifierWrapper, StreamingOutput.class);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                output.write(bao);
                InputStream is = new ByteArrayInputStream(bao.toByteArray());
                UpsertUserEventRequestContext uueCtx = new UpsertUserEventRequestContextBuilder()
                        .login(userName)
                        .type("CLASSIFIERS_EXPORT")
                        .content(MessageUtils.getMessage(UserMessageConstants.CLASSIFIER_EXPORT_XLSX_SUCCESS))
                        .build();
                UserEventDTO userEventDTO = userService.upsert(uueCtx);
                // save result and attach it to the early created user event
                SaveLargeObjectRequestContext slorCTX = new SaveLargeObjectRequestContextBuilder()
                        .eventKey(userEventDTO.getId())
                        .mimeType("application/vnd.ms-excel")
                        .binary(true)
                        .inputStream(is)
                        .filename(encodedFilename)
                        .build();
                dataRecordService.saveLargeObject(slorCTX);
            } catch (Exception e) {
                LOGGER.error("Cannot export classifiers due to an exception", e);
                String eMessage = ExceptionUtils.getStackTrace(e);
                List<String> lines = Arrays.asList("Cannot export classifiers to " + encodedFilename + " due to: ",
                        eMessage);
                String reportFileName = "report" + IdUtils.v4String() + ".txt";
                try {
                    Files.write(Paths.get(reportFileName), lines, Charset.forName("UTF-8"));
                    UpsertUserEventRequestContext uueCtx = new UpsertUserEventRequestContextBuilder()
                            .login(userName)
                            .type("CLASSIFIERS_EXPORT")
                            .content(MessageUtils.getMessage(UserMessageConstants.CLASSIFIER_EXPORT_XLSX_UNSUCCESS))
                            .build();
                    UserEventDTO userEventDTO = userService.upsert(uueCtx);
                    // save result and attach it to the early created user event
                    SaveLargeObjectRequestContext slorCTX = new SaveLargeObjectRequestContextBuilder()
                            .eventKey(userEventDTO.getId()).mimeType("text/plain").binary(true)
                            .inputStream(
                                    Files.newInputStream(Paths.get(reportFileName), StandardOpenOption.DELETE_ON_CLOSE))
                            .filename(reportFileName).build();
                    dataRecordService.saveLargeObject(slorCTX);
                } catch (Exception e1) {
                    LOGGER.error("Cannot create report file due to an exception", e1);
                }
            }
        });

        return ok(new RestResponse<>(""));

    }

//    @Override
    public void subscribe(Flux<Map<String, ? extends Serializable>> updates) {
        updates.subscribe(values -> {
            final String importThreadsPoolSize =
                    UnidataConfigurationProperty.CLASSIFIER_IMPORT_THREADS_POOL_SIZE.getKey();
            if (values.containsKey(importThreadsPoolSize)) {
                final Integer poolSize = (Integer) values.get(importThreadsPoolSize);
                importThreadsExecutor.setCorePoolSize(poolSize);
                importThreadsExecutor.setMaximumPoolSize(poolSize);
            }

            final String expotThreadsPoolSize =
                    UnidataConfigurationProperty.CLASSIFIER_IMPORT_THREADS_POOL_SIZE.getKey();
            if (values.containsKey(expotThreadsPoolSize)) {
                final Integer poolSize = (Integer) values.get(expotThreadsPoolSize);
                exportThreadsExecutor.setCorePoolSize(poolSize);
                exportThreadsExecutor.setMaximumPoolSize(poolSize);
            }
        });
    }

    /**
     * The Enum View.
     */
    public enum View {

        /** The meta. */
        META,
        /** The data. */
        DATA,
        /** The path. */
        PATH,
        /** The empty. */
        EMPTY;
    }

    /**
     * The Enum ResolvingStrategy.
     */
    public enum ResolvingStrategy {

        /** The code. */
        CODE,
        /** The id. */
        ID
    }

    /**
     * The Class ClassifierResponse.
     */
    // it is a crutch
    private static class ClassifierResponse extends RestResponse<ClsfRO> {

        /*
         * (non-Javadoc)
         *
         * @see com.unidata.mdm.backend.api.rest.dto.RestResponse#getContent()
         */
        @Override
        public ClsfRO getContent() {
            return null;
        }
    }

    /**
     * The Class ClassifiersResponse.
     */
    private static class ClassifiersResponse extends RestResponse<List<ClsfRO>> {

        /*
         * (non-Javadoc)
         *
         * @see com.unidata.mdm.backend.api.rest.dto.RestResponse#getContent()
         */
        @Override
        public List<ClsfRO> getContent() {
            return null;
        }
    }

    /**
     * The Class NodeResponse.
     */
    private static class NodeResponse extends RestResponse<ClsfNodeRO> {

        /*
         * (non-Javadoc)
         *
         * @see com.unidata.mdm.backend.api.rest.dto.RestResponse#getContent()
         */
        @Override
        public ClsfNodeRO getContent() {
            return null;
        }
    }

    /**
     * The Class SearchResponse.
     */
    private static class SearchResponse extends RestResponse<List<ClsfNodeRO>> {

        /*
         * (non-Javadoc)
         *
         * @see com.unidata.mdm.backend.api.rest.dto.RestResponse#getContent()
         */
        @Override
        public List<ClsfNodeRO> getContent() {
            return null;
        }
    }
}
