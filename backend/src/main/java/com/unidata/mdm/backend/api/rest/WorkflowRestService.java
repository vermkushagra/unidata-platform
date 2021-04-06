package com.unidata.mdm.backend.api.rest;

import static com.unidata.mdm.backend.api.rest.RestConstants.PATH_PARAM_ASSIGN;
import static com.unidata.mdm.backend.api.rest.RestConstants.PATH_PARAM_ATTACHMENT_ID;
import static com.unidata.mdm.backend.api.rest.RestConstants.PATH_PARAM_DOWNLOAD;
import static com.unidata.mdm.backend.api.rest.RestConstants.PATH_PARAM_HISTORY;
import static com.unidata.mdm.backend.api.rest.RestConstants.PATH_PARAM_ITEM_TYPE;
import static com.unidata.mdm.backend.api.rest.RestConstants.PATH_PARAM_PROCESSES;
import static com.unidata.mdm.backend.api.rest.RestConstants.PATH_PARAM_PROCESS_ATTACH;
import static com.unidata.mdm.backend.api.rest.RestConstants.PATH_PARAM_PROCESS_COMMENT;
import static com.unidata.mdm.backend.api.rest.RestConstants.PATH_PARAM_PROCESS_INSTANCE_ID;
import static com.unidata.mdm.backend.api.rest.RestConstants.PATH_PARAM_TASKS;
import static com.unidata.mdm.backend.api.rest.RestConstants.PATH_PARAM_TASK_ID;
import static com.unidata.mdm.backend.api.rest.RestConstants.PATH_PARAM_TASK_STATS;
import static com.unidata.mdm.backend.api.rest.RestConstants.PATH_PARAM_UNASSIGN;
import static com.unidata.mdm.backend.api.rest.RestConstants.PATH_PARAM_USER;
import static com.unidata.mdm.backend.api.rest.RestConstants.QUERY_PARAM_SORT_DATE_ASC;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.converter.AbstractEntityDefToEntityListElementConverter;
import com.unidata.mdm.backend.api.rest.converter.WorkflowAssignmentConverter;
import com.unidata.mdm.backend.api.rest.converter.WorkflowAttachmentConverter;
import com.unidata.mdm.backend.api.rest.converter.WorkflowCommentConverter;
import com.unidata.mdm.backend.api.rest.converter.WorkflowCompletionStateConverter;
import com.unidata.mdm.backend.api.rest.converter.WorkflowHistoryConverter;
import com.unidata.mdm.backend.api.rest.converter.WorkflowProcessDefinitionConverter;
import com.unidata.mdm.backend.api.rest.converter.WorkflowStateConverter;
import com.unidata.mdm.backend.api.rest.converter.WorkflowTaskConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorInfo;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.api.rest.dto.meta.EntityInfoDefinition;
import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowAssignmentRO;
import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowCommentRequestRO;
import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowInstanceHistoryRO;
import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowProcessTypeRO;
import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowShortStateRO;
import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowStateRO;
import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowTaskCompleteRequestRO;
import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowTaskRO;
import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowTaskRequestRO;
import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowTaskStartRequestRO;
import com.unidata.mdm.backend.common.context.AssignTaskRequestContext;
import com.unidata.mdm.backend.common.context.CompleteTaskRequestContext;
import com.unidata.mdm.backend.common.context.CompleteTaskRequestContext.CompleteTaskRequestContextBuilder;
import com.unidata.mdm.backend.common.context.GetTasksRequestContext;
import com.unidata.mdm.backend.common.context.GetTasksRequestContext.GetTasksRequestContextBuilder;
import com.unidata.mdm.backend.common.context.StartProcessRequestContext;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAttachContentDTO;
import com.unidata.mdm.backend.common.integration.wf.WorkflowVariables;
import com.unidata.mdm.backend.common.runtime.MeasurementContextName;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.service.wf.WorkflowService;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;
import com.unidata.mdm.conf.WorkflowProcessType;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Mikhail Mikhailov
 * Workflow related methods.
 */
@Path(RestConstants.PATH_PARAM_DATA + "/" + RestConstants.PATH_PARAM_WORKFLOW)
@Api(value = "data_workflow", description = "Поддержка процессов/согласований", produces = "application/json")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class WorkflowRestService extends AbstractRestService {

    /** Attachment type. */
    private static final String ATTACH_TYPE = "type";

    /** Attachment name. */
    private static final String ATTACH_NAME = "name";

    /** Attachment description. */
    private static final String ATTACH_DESCRIPTION = "description";


    /**
     * Workflow service.
     */
    @Autowired
    private WorkflowService workflowService;

    /**
     * Configuration service.
     */
    @Autowired
    private ConfigurationService configurationService;

    /**
     * MMS.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Constructor.
     */
    public WorkflowRestService() {
        super();
    }

    /**
     * Work flow task complete method.
     * @return updated record
     * @throws ParseException
     */
    @POST
    @Path("/" + RestConstants.PATH_PARAM_COMPLETE)
    @ApiOperation(
            value = "Завершить задачу",
            notes = "Ожидается ID задачи + флаги",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response complete(
            WorkflowTaskCompleteRequestRO request){

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_COMPLETE);
        MeasurementPoint.start();
        try {

            Map<String, Object> variables = new HashMap<>();
            variables.put(WorkflowVariables.VAR_DATE.getValue(), request.getAsOf());
            variables.put(WorkflowVariables.VAR_APPROVAL_STATE.getValue(), request.getAction());

            CompleteTaskRequestContext ctx = new CompleteTaskRequestContextBuilder()
                .processDefinitionKey(request.getProcessDefinitionKey())
                .taskId(request.getTaskId())
                .processKey(request.getProcessKey())
                .variables(variables)
                .action(request.getAction())
                .build();

            return Response.ok(new RestResponse<>(WorkflowCompletionStateConverter.to(workflowService.complete(ctx)))).build();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Tasks queries support.
     * @param request the request
     * @return task list
     */
    @POST
    @Path("/" + PATH_PARAM_TASKS)
    @ApiOperation(
            value = "Выводит задачи по заданным параметрам",
            notes = "Выводит задачи по заданным параметрам",
            response = WorkflowStateRestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response tasks(@ApiParam(value = "Параметры") WorkflowTaskRequestRO request) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_TASKS);
        MeasurementPoint.start();
        try {
            // add 1 day to Before dates for user friendly search
            Date processStartBefore = request.getProcessStartBefore();
            if (processStartBefore != null) {
                processStartBefore = DateUtils.addDays(processStartBefore, 1);
            }
            Date taskStartBefore = request.getTaskStartBefore();
            if (taskStartBefore != null) {
                taskStartBefore = DateUtils.addDays(taskStartBefore, 1);
            }
            Date taskEndBeforeTaskEndBefore = request.getTaskEndBefore();
            if (taskEndBeforeTaskEndBefore != null) {
                taskEndBeforeTaskEndBefore = DateUtils.addDays(taskEndBeforeTaskEndBefore, 1);
            }

            Pair<Date, Date> processStart = request.getProcessStartAfter() == null && request.getProcessStartBefore() == null
                    ? null
                    : new ImmutablePair<>(request.getProcessStartAfter(), processStartBefore);
            Pair<Date, Date> taskStart = null;
            Pair<Date, Date> taskEnd = null;
            taskStart = new ImmutablePair<>(request.getTaskStartAfter(), taskStartBefore);
            if (request.isHistorical()) {
                taskEnd = new ImmutablePair<>(request.getTaskEndAfter(), taskEndBeforeTaskEndBefore);
            }

            GetTasksRequestContext ctx = new GetTasksRequestContextBuilder()
                .taskId(request.getTaskId())
                .historical(request.isHistorical())
                .processStart(processStart)
                .taskStart(taskStart)
                .taskEnd(taskEnd)
                .initiator(request.getInitiator())
                .taskCompletedBy(request.getTaskCompletedBy())
                .variables(request.getVariables())
                .candidateUser(request.getCandidateUser())
                .assignedUser(request.getAssignedUser())
                .candidateOrAssignee(request.getCandidateOrAssignee())
                .count(request.getCount())
                .page(request.getPage() > 0 ? request.getPage() - 1 : request.getPage())
                .build();

            return Response.ok(new RestResponse<>(WorkflowStateConverter.to(workflowService.state(ctx)))).build();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Gets all known assignment types.
     * @return types
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_TYPES)
    @ApiOperation(
            value = "Выводит поддерживаемые типы воркфлоу",
            notes = "Выводит поддерживаемые типы воркфлоу",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response processTypes() {

        WorkflowProcessType[] types = WorkflowProcessType.values();
        List<WorkflowProcessTypeRO> result = new ArrayList<>(types.length);
        for (WorkflowProcessType type : types) {

            // Skip for 4.2
            if (type == WorkflowProcessType.RECORD_RESTORE
             || type == WorkflowProcessType.RECORD_MERGE) {
                continue;
            }

            WorkflowProcessTypeRO ro = new WorkflowProcessTypeRO();
            ro.setCode(type.name());
            ro.setName(MessageUtils.getMessage("app.wf."
                    + WorkflowProcessType.class.getSimpleName() + "." + type.name()));
            ro.setDescription(MessageUtils.getMessage("app.wf."
                    + WorkflowProcessType.class.getSimpleName() + "." + type.name() + ".description"));

            result.add(ro);
        }

        return Response.ok(new RestResponse<>(result)).build();
    }

    /**
     * Gets entities as list.
     * @param offset offset
     * @param pageSize page size
     * @return list of entities
     * @throws Exception
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_ASSIGNABLE_ENTITIES)
    @ApiOperation(value = "Список справочников", notes = "", response = RestResponse.class) //
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response  assignables() {

        List<EntityDef> entities = metaModelService.getEntitiesList();
        List<LookupEntityDef> lookups = metaModelService.getLookupEntitiesList();

        List<EntityInfoDefinition> result = new ArrayList<>();
        result.addAll(AbstractEntityDefToEntityListElementConverter.to(lookups));

        entities = entities.stream().filter(e -> {
            Map<RelationDef, EntityDef> rels = metaModelService.getEntityRelationsByType(e.getName(),
                    Collections.singletonList(RelType.CONTAINS),
                    true, false);
            return CollectionUtils.isEmpty(rels);
        }).collect(Collectors.toList());

        result.addAll(AbstractEntityDefToEntityListElementConverter.to(entities));

        return Response.ok(new RestResponse<>(result)).build();
    }


    /**
     * Returns process definitions.
     * @return process definitions list
     */
    @GET
    @Path("/" + PATH_PARAM_PROCESSES)
    @ApiOperation(
            value = "Выводит известные системе процессы",
            notes = "Выводит известные системе процессы",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response processes() {
        return Response.ok(new RestResponse<>(
                WorkflowProcessDefinitionConverter.to(configurationService.getDefinedProcessTypes())))
                .build();
    }

    /**
     * Gets all active assignments.
     * @return assignments
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_ASSIGNMENTS)
    @ApiOperation(
            value = "Выводит схему присвоения процессов реестрам",
            notes = "Выводит схему присвоения процессов реестрам",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response assignments() {
        return Response.ok(new RestResponse<>(WorkflowAssignmentConverter.to(workflowService.getAllAssignments()))).build();
    }

    @PUT
    @Path("/" + RestConstants.PATH_PARAM_ASSIGN)
    @ApiOperation(
            value = "Присваивает типы процессов реестрам",
            notes = "Присваивает типы процессов реестрам",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response assignments(List<WorkflowAssignmentRO> updates) {
        workflowService.updateAssignments(WorkflowAssignmentConverter.from(updates));
        return Response.ok(new UpdateResponse(Boolean.TRUE, null)).build();
    }

    /**
     * Add process comment.
     */
    @POST
    @Path("/" + PATH_PARAM_PROCESS_COMMENT)
    @ApiOperation(
            value = "Добавляет комментарий к задаче или процессу",
            notes = "Добавляет комментарий к задаче или процессу",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response addComment(final WorkflowCommentRequestRO request) {
        return Response.ok(new RestResponse<>(WorkflowCommentConverter.to(
                workflowService.addComment(request.getTaskId(), request.getProcessInstanceId(), request.getMessage()))))
                .build();
    }

    /**
     * Get process comments.
     */
    @GET
    @Path("/" + PATH_PARAM_PROCESS_COMMENT)
    @ApiOperation(
            value = "Возвращает комментарии задачи или процесса",
            notes = "Возвращает комментарии задачи или процесса",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getComments(@QueryParam(PATH_PARAM_TASK_ID) final String taskId,
                                @QueryParam(PATH_PARAM_PROCESS_INSTANCE_ID) final String processInstanceId,
                                @QueryParam(QUERY_PARAM_SORT_DATE_ASC) final Boolean sortDateAsc) {
        return Response.ok(new RestResponse<>(WorkflowCommentConverter.to(
                workflowService.getComments(taskId, processInstanceId, sortDateAsc != null ? sortDateAsc : true))))
                .build();
    }

    @POST
    @Path("/" + PATH_PARAM_PROCESS_ATTACH)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(
            value = "Добавляет атач к задаче или процессу",
            notes = "Добавляет атач к задаче или процессу",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response addAttachment(@Multipart(value = PATH_PARAM_TASK_ID, required = false) final String taskId,
                                  @Multipart(value = PATH_PARAM_PROCESS_INSTANCE_ID, required = false)
                                  final String processInstanceId,
                                  @Multipart(RestConstants.DATA_PARAM_FILE) Attachment attachment,
                                  @Multipart(value = ATTACH_TYPE, required = false) String type,
                                  @Multipart(value = ATTACH_NAME, required = false) String name,
                                  @Multipart(value = ATTACH_DESCRIPTION, required = false) String description) {
        return Response.ok(new RestResponse<>(WorkflowAttachmentConverter.to(
                workflowService.addAttachment(taskId, processInstanceId,
                        type != null ? type : attachment.getContentType().getType(),
                        name != null ? name : attachment.getContentDisposition().getParameter("filename"),
                        description,
                        attachment.getObject(InputStream.class)))))
                .build();
    }

    @GET
    @Path("/" + PATH_PARAM_PROCESS_ATTACH)
    @ApiOperation(
            value = "Возвращает список атачей",
            notes = "Возвращает список атачей",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getAttachments(@QueryParam(PATH_PARAM_TASK_ID) final String taskId,
                                   @QueryParam(PATH_PARAM_PROCESS_INSTANCE_ID) final String processInstanceId,
                                   @QueryParam(QUERY_PARAM_SORT_DATE_ASC) final Boolean sortDateAsc) {
        return Response.ok(new RestResponse<>(WorkflowAttachmentConverter.to(
                workflowService.getAttachments(taskId, processInstanceId, sortDateAsc != null ? sortDateAsc : true))))
                .build();
    }

    @GET
    @Path("/" + PATH_PARAM_PROCESS_ATTACH + "/{" + PATH_PARAM_ATTACHMENT_ID + "}/" + PATH_PARAM_DOWNLOAD)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @ApiOperation(
            value = "Возвращает ранее загруженный файл",
            notes = "Возвращает ранее загруженный файл",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getAttachmentContent(@PathParam(PATH_PARAM_ATTACHMENT_ID) final String attachmentId) {
        final WorkflowAttachContentDTO content = workflowService.getAttachmentContent(attachmentId);
        final Response.ResponseBuilder response = Response.ok(content.getInputStream());
        response.header("Content-Disposition", "attachment; filename=" + content.getName());
        return response.build();
    }

    /**
     * Returns user candidate tasks.
     * @return user candidate tasks
     */
    @GET
    @Path("/" + PATH_PARAM_TASKS + "/" + PATH_PARAM_TASK_STATS)
    @ApiOperation(
            value = "Возвращает статистику по задачам для пользователя",
            notes = "Возвращает статистику по задачам для пользователя",
            response = WorkflowShortStateRestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getTaskShortInformation(@QueryParam("fromDate") String fromDateAsString) {
        WorkflowShortStateRO result = new WorkflowShortStateRO();

        result.setAvailableCount(getAvailableCount());
        result.setTotalUserCount(getTotalUserCount());
        result.setNewCount(getNewAvailableCount(ValidityPeriodUtils.parse(fromDateAsString)));

        return Response.ok(new RestResponse<>(result)).build();
    }

    /**
     * Returns user candidate tasks.
     * @return user candidate tasks
     */
    @GET
    @Path("/" + PATH_PARAM_TASKS + "/{" + PATH_PARAM_USER + "}")
    @ApiOperation(
            value = "Возвращает задачи, подходящие для выполнения пользователю",
            notes = "Возвращает задачи, подходящие для выполнения пользователю",
            response = WorkflowTasksRestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getUserProcessesCandidates(@PathParam(PATH_PARAM_USER) final String user) {
        if (!user.equals(SecurityUtils.getCurrentUserName())) {
            final ErrorResponse response = new ErrorResponse();
            final ErrorInfo error = new ErrorInfo();
            error.setSeverity(ErrorInfo.Severity.LOW);
            error.setInternalMessage("Other than logged user task request is not supported");
            error.setUserMessage("Произошла внутренняя ошибка, обратитесь к администратору системы");
            response.getErrors().add(error);
            return Response.ok(response).build();
        }

        final GetTasksRequestContext ctx = new GetTasksRequestContext.GetTasksRequestContextBuilder()
                .historical(false)
                .candidateUser(SecurityUtils.getCurrentUserName())
                .build();

        return Response.ok(new RestResponse<>(WorkflowTaskConverter.to(workflowService.tasks(ctx)))).build();
    }

    @POST
    @Path("/" + PATH_PARAM_TASKS + "/{" + PATH_PARAM_TASK_ID +"}/" + PATH_PARAM_ASSIGN)
    @ApiOperation(
            value = "Назначить задачу на себя",
            notes = "Назначить задачу на себя",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response assignToMe(@PathParam(PATH_PARAM_TASK_ID) final String taskId){
        final AssignTaskRequestContext ctx = new AssignTaskRequestContext.AssignTaskRequestContextBuilder()
                .taskId(taskId)
                .username(SecurityUtils.getCurrentUserName())
                .build();
        workflowService.assign(ctx);
        return Response.ok(new UpdateResponse(true, null)).build();
    }

    @POST
    @Path("/" + PATH_PARAM_TASKS + "/{" + PATH_PARAM_TASK_ID +"}/" + PATH_PARAM_UNASSIGN)
    @ApiOperation(
            value = "Назначить задачу на себя",
            notes = "Назначить задачу на себя",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response unassign(@PathParam(PATH_PARAM_TASK_ID) final String taskId){
        final AssignTaskRequestContext ctx = new AssignTaskRequestContext.AssignTaskRequestContextBuilder()
                .taskId(taskId)
                .username(SecurityUtils.getCurrentUserName())
                .build();
        workflowService.unassign(ctx);
        return Response.ok(new UpdateResponse(true, null)).build();
    }

    @POST
    @Path("/" + PATH_PARAM_TASKS + "/start")
    @ApiOperation(
            value = "Сартовать задачу",
            notes = "Сартовать задачу",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response startTask(WorkflowTaskStartRequestRO taskStartRequestRO){
        final StartProcessRequestContext ctx = new StartProcessRequestContext.StartProcessRequestContextBuilder()
                .processDefinitionId(taskStartRequestRO.getProcessDefinitionKey())
                .variables(Collections.emptyMap())
                .initiator(SecurityUtils.getCurrentUserName())
                .processKey("13")
                .build();
        return Response.ok(new UpdateResponse(workflowService.start(ctx), null)).build();
    }

    @GET
    @Path("/" + PATH_PARAM_HISTORY)
    @ApiOperation(
            value = "Возвращает историю процесса",
            notes = "Возвращает историю процесса",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getProcessInstanceHistory(@QueryParam(PATH_PARAM_PROCESS_INSTANCE_ID) final String processInstanceId,
                                              @QueryParam(PATH_PARAM_ITEM_TYPE) final List<String> itemType,
                                              @QueryParam(QUERY_PARAM_SORT_DATE_ASC) final Boolean sortDateAsc) {
        Set<String> types;
        if (!CollectionUtils.isEmpty(itemType)) {
            types = new HashSet<>();
            types.addAll(itemType.stream()
                    .map(item -> WorkflowInstanceHistoryRO.WorkflowHistoryItemType.fromString(item).name())
                    .collect(Collectors.toList()));
        } else {
            types = WorkflowInstanceHistoryRO.WorkflowHistoryItemType.toSet();
        }

        return Response.ok(new RestResponse<>(WorkflowHistoryConverter.to(
                workflowService.getInstanceHistory(processInstanceId, sortDateAsc != null ? sortDateAsc : true, types))))
                .build();
    }

    @GET
    @Path("/" + RestConstants.PATH_PARAM_DIAGRAM + "/{" + RestConstants.DATA_PARAM_ID +"}")
    @ApiOperation(
            value = "Возвращает диаграмму процесса",
            notes = "Возвращает диаграмму процесса",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    @Produces("image/png")
    public Response getProcessDiagram(
            @ApiParam(value = "Инстанция процесса") @PathParam(RestConstants.DATA_PARAM_ID) String processInstanceId,
            @ApiParam(value = "Инстанция процесса закончилась или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_FINISHED) String finishedAsString) {
        InputStream is = workflowService.generateDiagram(
                processInstanceId,
                finishedAsString == null ? false : Boolean.valueOf(finishedAsString));
        if (Objects.isNull(is)) {
            return Response.noContent().build();
        }

        return Response.ok(is).build();
    }


    private long getNewAvailableCount(Date fromDate) {
        GetTasksRequestContext ctx = new GetTasksRequestContextBuilder()
                .historical(false)
                .candidateUser(SecurityUtils.getCurrentUserName())
                .taskStart(new ImmutablePair<>(fromDate, null))
                .countOnly(true)
                .build();
        return workflowService.state(ctx).getTotalCount();
    }

    private long getTotalUserCount() {
        GetTasksRequestContext ctx = new GetTasksRequestContextBuilder()
                .historical(false)
                .assignedUser(SecurityUtils.getCurrentUserName())
                .countOnly(true)
                .build();
        return workflowService.state(ctx).getTotalCount();
    }

    private long getAvailableCount() {
        GetTasksRequestContext ctx = new GetTasksRequestContextBuilder()
                .historical(false)
                .candidateUser(SecurityUtils.getCurrentUserName())
                .countOnly(true)
                .build();
        return workflowService.state(ctx).getTotalCount();
    }

    //DO NOT REMOVE! This crappy workaround required for Swagger to generate API docs
    private static class WorkflowShortStateRestResponse extends RestResponse<WorkflowShortStateRO> {
        @Override
        public WorkflowShortStateRO getContent() {
            return null;
        }
    }

    //DO NOT REMOVE! This crappy workaround required for Swagger to generate API docs
    private static class WorkflowTasksRestResponse extends RestResponse<List<WorkflowTaskRO>> {
        @Override
        public List<WorkflowTaskRO> getContent() {
            return null;
        }
    }

    //DO NOT REMOVE! This crappy workaround required for Swagger to generate API docs
    private static class WorkflowStateRestResponse extends RestResponse<WorkflowStateRO> {
        @Override
        public WorkflowStateRO getContent() {
            return null;
        }
    }
}
