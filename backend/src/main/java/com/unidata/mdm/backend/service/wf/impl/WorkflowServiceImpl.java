package com.unidata.mdm.backend.service.wf.impl;

import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.IdentityLinkType;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskInfoQueryWrapper;
import org.activiti.engine.task.TaskQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.common.context.AssignTaskRequestContext;
import com.unidata.mdm.backend.common.context.CompleteTaskRequestContext;
import com.unidata.mdm.backend.common.context.GetProcessRequestContext;
import com.unidata.mdm.backend.common.context.GetProcessRequestContext.GetProcessRequestContextBuilder;
import com.unidata.mdm.backend.common.context.GetTasksRequestContext;
import com.unidata.mdm.backend.common.context.GetTasksRequestContext.GetTasksRequestContextBuilder;
import com.unidata.mdm.backend.common.context.StartProcessRequestContext;
import com.unidata.mdm.backend.common.dto.security.UserWithPasswordDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowActionsDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAssignmentDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAttachContentDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAttachDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowCommentDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowCompletionStateDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowHistoryItemDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowProcessDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowStateDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTaskDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.WorkflowException;
import com.unidata.mdm.backend.common.integration.auth.User;
import com.unidata.mdm.backend.common.integration.wf.EditWorkflowProcessTriggerType;
import com.unidata.mdm.backend.common.integration.wf.WorkflowProcessStartState;
import com.unidata.mdm.backend.common.integration.wf.WorkflowProcessSupport;
import com.unidata.mdm.backend.common.integration.wf.WorkflowTaskCompleteState;
import com.unidata.mdm.backend.common.integration.wf.WorkflowVariables;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.dao.WorkflowAssignmentDao;
import com.unidata.mdm.backend.notification.notifiers.WorkflowAssignmentsNotifier;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import com.unidata.mdm.backend.service.audit.actions.AuditAction;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.security.SecurityServiceExt;
import com.unidata.mdm.backend.service.security.UserService;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.service.wf.WorkflowService;
import com.unidata.mdm.backend.service.wf.po.WorkflowAssignmentPO;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.conf.WorkflowProcessType;

/**
 * @author Mikhail Mikhailov Workflow service.
 */
@Service
public class WorkflowServiceImpl implements WorkflowService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowServiceImpl.class);

    /**
     * Workflow assignment DAO.
     */
    @Autowired
    private WorkflowAssignmentDao workflowAssignmentDao;

    /**
     * Process engine instance.
     */
    @Autowired
    private ProcessEngine processEngine;

    /**
     * Repository service instance.
     */
    @Autowired
    private RepositoryService repositoryService;

    /**
     * Runtime service instance.
     */
    @Autowired
    private RuntimeService runtimeService;

    /**
     * Task service instance.
     */
    @Autowired
    private TaskService taskService;

    /**
     * History service instance.
     */
    @Autowired
    private HistoryService historyService;

    /**
     * Management service instance.
     */
    @SuppressWarnings("unused")
    @Autowired
    private ManagementService managementService;

    /**
     * Configuration service.
     */
    @Autowired
    private ConfigurationService configurationService;
    /**
     * User service instance.
     */
    @Autowired
    private UserService userService;
    /**
     * Security service instance.
     */
    @Autowired
    private SecurityServiceExt securityService;

    /**
     * Workflow change listener.
     */
    @Autowired
    @Qualifier("workflowChangeListener")
    private ActivitiEventListener workflowChangeListener;

    @Value("${unidata.activiti.task.mailServerDefaultFrom}")
    private String mailFrom;
    /**
     * Audit event writer
     */
    @Autowired
    private AuditEventsWriter auditEventsWriter;
    /**
     * WA notifier.
     */
    @Autowired(required = false)
    private WorkflowAssignmentsNotifier workflowAssignmentsNotifier;
    /**
     * Local assignment map.
     */
    private ConcurrentHashMap<String, EnumMap<WorkflowProcessType, WorkflowAssignmentDTO>> assignments = new ConcurrentHashMap<>();
    /**
     * Approved label.
     */
    private static final String APPROVED_LABEL = "app.wf.APPROVED";
    /**
     * Declined label.
     */
    private static final String DECLINED_LABEL = "app.wf.DECLINED";
    /**
     * Running label.
     */
    private static final String RUNNING_LABEL = "app.wf.RUNNING";

    private static final Comparator<WorkflowCommentDTO> WF_COMMENT_DATE_COMPARATOR =
            Comparator.comparing(WorkflowCommentDTO::getDateTime);

    private static final Comparator<WorkflowAttachDTO> WF_ATTACH_DATE_COMPARATOR =
            Comparator.comparing(WorkflowAttachDTO::getDateTime);

    private static final Comparator<WorkflowHistoryItemDTO> WF_HISTORY_DATE_COMPARATOR =
            Comparator.comparing(WorkflowHistoryItemDTO::getStartTime);

    /**
     * Constructor.
     */
    public WorkflowServiceImpl() {
        super();
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#afterContextRefresh()
     */
    @Override
    public void afterContextRefresh() {

        runtimeService.addEventListener(workflowChangeListener,
                ActivitiEventType.PROCESS_STARTED,
                ActivitiEventType.PROCESS_COMPLETED,
                ActivitiEventType.PROCESS_CANCELLED);

        readAssignments();
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#deployProcess(java.lang.String)
     */
    @Override
    public void deployProcess(String resourcePath) {
        repositoryService.createDeployment()
            .enableDuplicateFiltering()
            .addClasspathResource(resourcePath)
            .deploy();
    }
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#start(com.unidata.mdm.backend.common.context.StartProcessRequestContext)
     */
    @Override
    public boolean start(StartProcessRequestContext ctx) {

        MeasurementPoint.start();
        try {
            try {

                Map<String, Object> variables = new HashMap<>();
                if (MapUtils.isNotEmpty(ctx.getVariables())) {
                    variables.putAll(ctx.getVariables());
                }

                WorkflowProcessSupport support
                    = configurationService.getProcessSupportByProcessDefinitionId(ctx.getProcessDefinitionId());
                if (support != null) {
                    WorkflowProcessStartState state
                        = support.processStart(ctx.getProcessDefinitionId(), variables);
                    if (!state.isAllowed()) {
                        final String message = "Cannot start process. User support handler denied process start with message: {}.";
                        LOGGER.warn(message, state.getMessage());
                        throw new WorkflowException(message, ExceptionId.EX_WF_CANNOT_START_PROCESS_NOT_ALLOWED,
                                state.getMessage());
                    }

                    if (MapUtils.isNotEmpty(state.getAdditionalProcessVariables())) {
                        variables.putAll(state.getAdditionalProcessVariables());
                    }
                }

                ProcessInstance instance;
                if (ctx.getProcessKey() == null) {
                    instance = runtimeService.startProcessInstanceByKey(ctx.getProcessDefinitionId(), variables);
                } else {
                    instance = runtimeService.startProcessInstanceByKey(ctx.getProcessDefinitionId(), ctx.getProcessKey(),
                            variables);
                }

                if (instance == null || StringUtils.isEmpty(instance.getId())) {
                    final String message = "Cannot start process. Activiti returned inappropriate result.";
                    LOGGER.warn(message);
                    throw new WorkflowException(message, ExceptionId.EX_WF_CANNOT_START_PROCESS_WRONG_RESULT);
                }

                return true;
            } catch (Exception e) {
                final String message = "Failed to start process with id '{}', initiator '{}' and variables '{}'. Caught {}";
                LOGGER.warn(message, ctx.getProcessDefinitionId(), ctx.getInitiator(), ctx.getVariables(), e);
                throw new WorkflowException(message, e, ExceptionId.EX_WF_START_PROCESS_FAILED, ctx.getProcessDefinitionId(),
                                            ctx.getInitiator(), ctx.getVariables());
            }
        } finally {
            MeasurementPoint.stop();
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#complete(com.unidata.mdm.backend.common.context.CompleteTaskRequestContext)
     */
    @Override
    public WorkflowCompletionStateDTO complete(CompleteTaskRequestContext ctx) {

        AuditAction auditAction = "APPROVED".equals(ctx.getAction()) ?
                AuditActions.WORKFLOW_ACCEPT :
                AuditActions.WORKFLOW_DECLINE;

        MeasurementPoint.start();
        try {

            Task task;
            if (ctx.getTaskId() != null) {
                task = taskService.createTaskQuery()
                                  .taskId(ctx.getTaskId())
                                  .includeProcessVariables()
                                  .includeTaskLocalVariables()
                                  .singleResult();
            } else {
                List<Task> tasks = taskService.createTaskQuery()
                                              .processInstanceBusinessKey(ctx.getProcessKey())
                                              .includeProcessVariables()
                                              .includeTaskLocalVariables()
                                              .processDefinitionKey(ctx.getProcessDefinitionKey())
                                              .list();

                if (tasks != null && tasks.size() > 1) {
                    final String message = "More then one task selected for complete ({}).";
                    LOGGER.warn(message, tasks);
                    throw new WorkflowException(message, ExceptionId.EX_WF_CANNOT_COMPLETE_TASK_MORE_THEN_ONE, tasks);
                }

                task = tasks == null || tasks.size() != 1 ? null : tasks.get(0);
            }

            if (task == null) {
                final String message = "Cannot complete task. Task not found!";
                LOGGER.warn(message);
                throw new WorkflowException(message, ExceptionId.EX_WF_CANNOT_COMPLETE_TASK_NOT_FOUND);
            }

            // 5. Actions and process handling information
            WorkflowCompletionStateDTO result = createWorkflowState(ctx, task);
            auditEventsWriter.writeSuccessEvent(auditAction, ctx);
            return result;
        } catch (Exception e) {
            auditEventsWriter.writeUnsuccessfulEvent(auditAction, e, ctx);
            throw e;
        } finally {
            MeasurementPoint.stop();
        }
    }

    private WorkflowCompletionStateDTO createWorkflowState(CompleteTaskRequestContext ctx, Task task) {
        // 5. Actions and process handling information
        String processKey = extractProcessDefinitionKey(task.getProcessDefinitionId());
        WorkflowProcessSupport support = configurationService.getProcessSupportByProcessDefinitionId(processKey);

        Map<String, Object> additionalProcessVariables = null;
        Map<String, Object> additionalTaskVariables = null;
        if (support != null) {

            Map<String, Object> variables = new HashMap<>();
            variables.putAll(task.getProcessVariables());
            variables.putAll(task.getTaskLocalVariables());
            variables.putAll(ctx.getVariables());

            WorkflowTaskCompleteState state = support.complete(task.getTaskDefinitionKey(), variables,
                    ctx.getAction());

            if (!state.isComplete()) {
                return new WorkflowCompletionStateDTO(false, state.getMessage());
            }

            additionalProcessVariables = state.getAdditionalProcessVariables();
            additionalTaskVariables = state.getAdditionalTaskVariables();
        }

        if (additionalTaskVariables == null) {
            additionalTaskVariables = new HashMap<>();
        }

        additionalTaskVariables.put(WorkflowVariables.VAR_TASK_COMPLETED_BY.getValue(),
                SecurityUtils.getCurrentUserName());

        if (additionalProcessVariables == null) {
            additionalProcessVariables = new HashMap<>();
        }

        additionalProcessVariables.put(task.getTaskDefinitionKey(), ctx.getAction());
        // UN-2944
        String token = SecurityUtils.getCurrentUserToken();
        if (Objects.nonNull(token)) {
            User user = securityService.getUserByToken(token);
            additionalProcessVariables.put(
                    task.getTaskDefinitionKey() + WorkflowVariables.VAR_COMPLETED_BY.getValue(), user.getLogin());
            additionalProcessVariables.put(
                    task.getTaskDefinitionKey() + WorkflowVariables.VAR_COMPLETED_BY_NAME.getValue(),
                    user.getName());
            additionalProcessVariables.put(
                    task.getTaskDefinitionKey() + WorkflowVariables.VAR_COMPLETED_BY_EMAIL.getValue(),
                    user.getEmail());
        } else {
            additionalProcessVariables.put(task.getTaskDefinitionKey(), ctx.getAction());
            additionalProcessVariables.put(
                    task.getTaskDefinitionKey() + WorkflowVariables.VAR_COMPLETED_BY.getValue(),
                    SecurityUtils.getCurrentUserName());
        }
        additionalProcessVariables.put(
                task.getTaskDefinitionKey() + WorkflowVariables.VAR_COMPLETED_TIMSETAMP.getValue(), new Date());
        additionalProcessVariables.put(WorkflowVariables.VAR_TASK_NAME.getValue(), task.getName());

        try {
            final ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                                                          .processDefinitionId(task.getProcessDefinitionId())
                                                          .singleResult();
            if (pd != null) {
                additionalProcessVariables.put(WorkflowVariables.VAR_PROCESS_NAME.getValue(), pd.getName());
            }
        } catch (final ActivitiException ae) {
            LOGGER.warn(
                    "Error occurred while querying process definition id: [" + task.getProcessDefinitionId() + "]",
                    ae);
        }

        if (!CollectionUtils.isEmpty(ctx.getVariables())) {
            additionalProcessVariables.putAll(ctx.getVariables());
        }

        taskService.setVariables(task.getId(), additionalProcessVariables);
        taskService.setVariablesLocal(task.getId(), additionalTaskVariables);
        taskService.complete(task.getId());
        return new WorkflowCompletionStateDTO(true, null);
    }


    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#addComment(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public WorkflowCommentDTO addComment(final String taskId, final String processId, String commentMessage) {
        Authentication.setAuthenticatedUserId(SecurityUtils.getCurrentUserName());
        final TaskQuery query = taskService.createTaskQuery();
        Boolean isTask = null;
        if (!StringUtils.isEmpty(taskId)) {
            query.taskId(taskId);
            isTask = true;
        } else if (!StringUtils.isEmpty(processId)) {
            query.processInstanceId(processId);
            isTask = false;
        }

        final List<Task> tasks = query.list();
        if (isTask != null) {
            if (tasks != null && !tasks.isEmpty()) {
                return activitiCommentToDto(taskService.addComment(!StringUtils.isEmpty(taskId) ? taskId : null,
                        !StringUtils.isEmpty(processId) ? processId : null, commentMessage));

            } else {
                if (isTask) {
                    final String message = "Cannot add task comment. Task not found!";
                    LOGGER.warn(message);
                    throw new WorkflowException(message, ExceptionId.EX_WF_CANNOT_ADD_TASK_COMMENT_TASK_NOT_FOUND);
                } else {
                    final String message = "Cannot add process comment. Tasks not found!";
                    LOGGER.warn(message);
                    throw new WorkflowException(message, ExceptionId.EX_WF_CANNOT_ADD_PROCESS_COMMENT_TASK_NOT_FOUND);
                }
            }
        } else {
            final String message = "Cannot add any comments. Both taskId and processInstanceId cannot be null!";
            LOGGER.warn(message);
            throw new WorkflowException(message, ExceptionId.EX_WF_CANNOT_ADD_COMMENTS_NO_TASK_ID_OR_PROCESS_ID);
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#getComments(java.lang.String, java.lang.String, boolean)
     */
    @Override
    public List<WorkflowCommentDTO> getComments(final String taskId, final String processId, final boolean dateAsc) {
        Authentication.setAuthenticatedUserId(SecurityUtils.getCurrentUserName());
        final List<WorkflowCommentDTO> result = new ArrayList<>();

        Boolean isTask = null;
        if (!StringUtils.isEmpty(taskId)) {
            isTask = true;
        } else if (!StringUtils.isEmpty(processId)) {
            isTask = false;
        }

        if (isTask != null) {
            final List<Comment> comments;
            if (isTask) {
                comments = taskService.getTaskComments(taskId, "comment");
            } else {
                comments = taskService.getProcessInstanceComments(processId, "comment");
            }
            if (!CollectionUtils.isEmpty(comments)) {
                result.addAll(comments.stream().map(this::activitiCommentToDto).collect(Collectors.toList()));

                final Comparator<WorkflowCommentDTO> cmp = dateAsc ?
                        WF_COMMENT_DATE_COMPARATOR : WF_COMMENT_DATE_COMPARATOR.reversed();
                Collections.sort(result, cmp);

            }
        } else {
            final String message = "Cannot retrieve any comments. Both taskId and processInstanceId cannot be null!";
            LOGGER.warn(message);
            throw new WorkflowException(message, ExceptionId.EX_WF_CANNOT_GET_PROCESS_COMMENTS_NO_TASK_ID_OR_PROCESS_ID);
        }

        return result;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#addAttachment(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.io.InputStream)
     */
    @Override
    public WorkflowAttachDTO addAttachment(final String taskId,
                                           final String processId,
                                           final String type,
                                           final String name,
                                           final String description,
                                           final InputStream attachmentInputStream) {
        Authentication.setAuthenticatedUserId(SecurityUtils.getCurrentUserName());
        final TaskQuery query = taskService.createTaskQuery();
        Boolean isTask = null;
        if (!StringUtils.isEmpty(taskId)) {
            query.taskId(taskId);
            isTask = true;
        } else if (!StringUtils.isEmpty(processId)) {
            query.processInstanceId(processId);
            isTask = false;
        }

        final List<Task> tasks = query.list();
        if (isTask != null) {
            if (tasks != null && !tasks.isEmpty()) {
                return activitiAttachmentToDto(taskService.createAttachment(type,
                        !StringUtils.isEmpty(taskId) ? taskId : null,
                        !StringUtils.isEmpty(processId) ? processId : null,
                        name, description, attachmentInputStream));
            } else {
                final String message;
                final ExceptionId id;
                if (isTask) {
                    id = ExceptionId.EX_WF_CANNOT_ADD_TASK_ATTACHMENT_TASK_NOT_FOUND;
                    message = "Cannot add task attachment. Task not found!";
                } else {
                    id = ExceptionId.EX_WF_CANNOT_ADD_PROCESS_ATTACHMENT_TASK_NOT_FOUND;
                    message = "Cannot add process attachment. Task not found!";
                }
                LOGGER.warn(message);
                throw new WorkflowException(message, id);
            }
        } else {
            final String message = "Cannot add attachment. Both taskId and processInstanceId cannot be null!";
            LOGGER.warn(message);
            throw new WorkflowException(message, ExceptionId.EX_WF_CANNOT_ADD_ATTACHMENT_NO_TASK_ID_OR_PROCESS_ID);
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#getAttachments(java.lang.String, java.lang.String, boolean)
     */
    @Override
    public List<WorkflowAttachDTO> getAttachments(final String taskId, final String processId, final boolean sortDateAsc) {
        Authentication.setAuthenticatedUserId(SecurityUtils.getCurrentUserName());
        final List<WorkflowAttachDTO> result = new ArrayList<>();

        Boolean isTask = null;
        if (!StringUtils.isEmpty(taskId)) {
            isTask = true;
        } else if (!StringUtils.isEmpty(processId)) {
            isTask = false;
        }

        if (isTask != null) {
            final List<Attachment> attachments;
            if (isTask) {
                attachments = taskService.getTaskAttachments(taskId);
            } else {
                attachments = taskService.getProcessInstanceAttachments(processId);
            }
            if (!CollectionUtils.isEmpty(attachments)) {
                result.addAll(attachments.stream().map(this::activitiAttachmentToDto).collect(Collectors.toList()));

                final Comparator<WorkflowAttachDTO> cmp = sortDateAsc ?
                        WF_ATTACH_DATE_COMPARATOR : WF_ATTACH_DATE_COMPARATOR.reversed();
                Collections.sort(result, cmp);
            }
        } else {
            final String message = "Cannot retrieve any attachments. Both taskId and processInstanceId cannot be null!";
            LOGGER.warn(message);
            throw new WorkflowException(message, ExceptionId.EX_WF_CANNOT_GET_ATTACHMENTS_NO_TASK_ID_OR_PROCESS_ID);
        }

        return result;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#getAttachmentContent(java.lang.String)
     */
    @Override
    public WorkflowAttachContentDTO getAttachmentContent(final String attachmentId) {
        Authentication.setAuthenticatedUserId(SecurityUtils.getCurrentUserName());
        final Attachment a = taskService.getAttachment(attachmentId);
        if (a == null) {
            final String message = "Cannot get attachment content. Attachment not found";
            LOGGER.warn(message);
            throw new WorkflowException(message, ExceptionId.EX_WF_CANNOT_GET_CONTENT_ATTACHMENT_NOT_FOUND);
        }

        final WorkflowAttachContentDTO result = new WorkflowAttachContentDTO();
        result.setName(a.getName());
        result.setId(a.getId());
        result.setInputStream(taskService.getAttachmentContent(attachmentId));

        return result;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#assign(com.unidata.mdm.backend.common.context.AssignTaskRequestContext)
     */
    @Override
    public void assign(final AssignTaskRequestContext ctx) {
        Authentication.setAuthenticatedUserId(SecurityUtils.getCurrentUserName());
        final TaskQuery query = taskService.createTaskQuery().taskId(ctx.getTaskId());
        final Task task = query.singleResult();
        if (task != null) {
            try {
                taskService.claim(ctx.getTaskId(), ctx.getUsername());
            } catch (final ActivitiException e) {
                final String message = "Cannot assign task to " + ctx.getUsername();
                LOGGER.warn(message, e);
                throw new WorkflowException(message, ExceptionId.EX_WF_CANNOT_ASSIGN_ENGINE_ERROR, e.getMessage());
            }
        } else {
            final String message = "Cannot assign task " + ctx.getTaskId() + ". Task not found!";
            LOGGER.warn(message);
            throw new WorkflowException(message, ExceptionId.EX_WF_CANNOT_ASSIGN_TASK_NOT_FOUND);
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#unassign(com.unidata.mdm.backend.common.context.AssignTaskRequestContext)
     */
    @Override
    public void unassign(final AssignTaskRequestContext ctx) {
        Authentication.setAuthenticatedUserId(SecurityUtils.getCurrentUserName());
        final TaskQuery query = taskService.createTaskQuery().taskId(ctx.getTaskId());
        final Task task = query.singleResult();
        if (task != null) {
            try {
                taskService.unclaim(ctx.getTaskId());
            } catch (final ActivitiException e) {
                final String message = "Cannot unassign task to " + ctx.getUsername();
                LOGGER.warn(message, e);
                throw new WorkflowException(message, ExceptionId.EX_WF_CANNOT_UNASSIGN_ENGINE_ERROR, e.getMessage());
            }
        } else {
            final String message = "Cannot unassign task " + ctx.getTaskId() + ". Task not found!";
            LOGGER.warn(message);
            throw new WorkflowException(message, ExceptionId.EX_WF_CANNOT_UNASSIGN_TASK_NOT_FOUND);
        }
    }

    /**
     * Creates a task query wrapper from supplied context.
     *
     * @param ctx the context
     * @return query wrapper
     */
    private TaskInfoQueryWrapper createTaskInfoQueryWrapper(GetTasksRequestContext ctx) {

        TaskInfoQueryWrapper tqw = ctx.isHistorical()
                ? new TaskInfoQueryWrapper(historyService.createHistoricTaskInstanceQuery())
                : new TaskInfoQueryWrapper(taskService.createTaskQuery());

        if (ctx.getTaskId() != null) {
            tqw.getTaskInfoQuery().taskId(ctx.getTaskId());
        } else {

            if (ctx.getInitiator() != null) {
                tqw.getTaskInfoQuery().processVariableValueEquals(WorkflowVariables.VAR_INITIATOR.getValue(),
                        ctx.getInitiator());
            }

            if (ctx.getProcessKey() != null) {
                tqw.getTaskInfoQuery().processInstanceBusinessKey(ctx.getProcessKey());
            }

            if (ctx.getProcessType() != null) {
                tqw.getTaskInfoQuery().processVariableValueEquals(WorkflowVariables.VAR_PROCESS_TYPE.getValue(),
                        ctx.getProcessType().name());
            }

            if (ctx.getProcessDefinitionId() != null) {
                tqw.getTaskInfoQuery().processDefinitionKey(ctx.getProcessDefinitionId());
            }

            if (ctx.getVariables() != null) {
                for (Entry<String, Object> entry : ctx.getVariables().entrySet()) {
                    tqw.getTaskInfoQuery().processVariableValueEquals(entry.getKey(), entry.getValue());
                }
            }

            if (ctx.getCandidateUser() != null) {
                tqw.getTaskInfoQuery().taskCandidateUser(ctx.getCandidateUser());
            }

            if (ctx.getAssignedUser() != null) {
                tqw.getTaskInfoQuery().taskAssignee(ctx.getAssignedUser());
            }

            if (ctx.getTaskStart() != null) {
                if (ctx.getTaskStart().getLeft() != null) {
                    tqw.getTaskInfoQuery().taskCreatedAfter(ctx.getTaskStart().getLeft());
                }
                if (ctx.getTaskStart().getRight() != null) {
                    tqw.getTaskInfoQuery().taskCreatedBefore(ctx.getTaskStart().getRight());
                }
            }

            if (ctx.isHistorical()) {
                HistoricTaskInstanceQuery htiq = (HistoricTaskInstanceQuery) tqw.getTaskInfoQuery();
                if (ctx.getTaskCompletedBy() != null) {
                    htiq.taskVariableValueEquals(WorkflowVariables.VAR_TASK_COMPLETED_BY.getValue(),
                            ctx.getTaskCompletedBy());
                }

                if (ctx.getTaskEnd() != null) {
                    if (ctx.getTaskEnd().getLeft() != null) {
                        htiq.taskCompletedAfter(ctx.getTaskEnd().getLeft());
                    }
                    if (ctx.getTaskEnd().getRight() != null) {
                        htiq.taskCompletedBefore(ctx.getTaskEnd().getRight());
                    }
                }

                if (ctx.getProcessStart() != null) {
                    if (ctx.getProcessStart().getLeft() != null) {
                        htiq.processVariableValueGreaterThanOrEqual(WorkflowVariables.VAR_WF_CREATE_DATE.getValue(), ctx.getProcessStart().getLeft());
                    }
                    if (ctx.getProcessStart().getRight() != null) {
                        htiq.processVariableValueLessThanOrEqual(WorkflowVariables.VAR_WF_CREATE_DATE.getValue(), ctx.getProcessStart().getRight());
                    }
                }

                htiq.includeTaskLocalVariables();
                htiq.includeProcessVariables();
                htiq.finished();
            } else {
                TaskQuery tq = (TaskQuery) tqw.getTaskInfoQuery();
                if (ctx.getCandidateOrAssignee() != null) {
                    tq.taskCandidateOrAssigned(ctx.getCandidateOrAssignee());
                }

                if (ctx.getProcessStart() != null) {
                    if (ctx.getProcessStart().getLeft() != null) {
                        tq.processVariableValueGreaterThanOrEqual(WorkflowVariables.VAR_WF_CREATE_DATE.getValue(), ctx.getProcessStart().getLeft());
                    }
                    if (ctx.getProcessStart().getRight() != null) {
                        tq.processVariableValueLessThanOrEqual(WorkflowVariables.VAR_WF_CREATE_DATE.getValue(), ctx.getProcessStart().getRight());
                    }
                }
                tq.includeProcessVariables();

                tq.active();
            }
        }

        return tqw;
    }

    /**
     * Extracts key portion from the process definition id.
     * @param processDefinitionId the id
     * @return key
     */
    private String extractProcessDefinitionKey(String processDefinitionId) {
        return StringUtils.substringBefore(processDefinitionId, ":");
    }

    private String getUserFullName(final String login) {
        final UserWithPasswordDTO origUser = userService.getUserByName(login);
        return origUser != null ? origUser.getFullName() : null;
    }

    /**
     * Fills task infos.
     *
     * @param tasks query result
     * @param historical historical search or not
     */
    private List<WorkflowTaskDTO> fillTaskInfos(List<? extends TaskInfo> tasks, boolean historical) {

        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        List<WorkflowTaskDTO> result = new ArrayList<>();
        Map<String, WorkflowProcessDTO> processes = new HashMap<>();
        for (TaskInfo ti : tasks) {

            // 1. Task instance
            WorkflowTaskDTO wt = new WorkflowTaskDTO();
            wt.setTaskId(ti.getId());
            wt.setTaskKey(ti.getTaskDefinitionKey());
            wt.setTaskTitle(ti.getName());
            wt.setTaskDescription(ti.getDescription());
            wt.setTaskAssignee(ti.getAssignee());
            wt.setTaskAssigneeName(getUserFullName(ti.getAssignee()));

            // 2. Process info
            WorkflowProcessDTO process = processes.get(ti.getProcessInstanceId());
            if (process == null) {
                process = process(new GetProcessRequestContextBuilder()
                    .historical(historical)
                    .processInstanceId(ti.getProcessInstanceId())
                    .build());
                processes.put(ti.getProcessInstanceId(), process);
            }

            wt.setProcessId(ti.getProcessInstanceId());
            wt.setProcessType(process.getProcessType());
            wt.setTriggerType(process.getTriggerType());
            wt.setProcessFinished(process.isEnded());
            wt.setProcessDefinitionId(process.getProcessDefinitionId());
            wt.setProcessTitle(process.getProcessTitle());

            wt.setCreateDate(ti.getCreateTime());

            // 3. Variables
            Map<String, Object> variables = ti.getProcessVariables();
            if (variables != null && !variables.isEmpty()) {
                final String originatorLogin = (String) variables.get(WorkflowVariables.VAR_INITIATOR.getValue());
                wt.setOriginator(originatorLogin);
                wt.setOriginatorName(getUserFullName(originatorLogin));
                wt.setOriginatorEmail((String) variables.get(WorkflowVariables.VAR_INITIATOR_EMAIL.getValue()));
                wt.getVariables().putAll(variables);

                String approvalState = (String) variables.get(WorkflowVariables.VAR_APPROVAL_STATE.getValue());
                String label = !historical
                    ? RUNNING_LABEL
                    : ApprovalState.APPROVED.name().equals(approvalState)
                        ? APPROVED_LABEL
                        : DECLINED_LABEL;

                wt.setApprovalMessage(MessageUtils.getMessage(label));
            }

            //4. Historical information
            if (historical) {
                HistoricTaskInstance hti = (HistoricTaskInstance) ti;
                variables = hti.getTaskLocalVariables();
                if (variables != null && !variables.isEmpty()) {
                    wt.setTaskCompletedBy((String) variables.get(WorkflowVariables.VAR_TASK_COMPLETED_BY.getValue()));
                }

                wt.setFinishedDate(hti.getEndTime());
                wt.setFinished(true);
            } else {
                List<IdentityLink> il = taskService.getIdentityLinksForTask(ti.getId());
                wt.setTaskCandidate(CollectionUtils.isEmpty(il)
                    ? null
                    : il.stream()
                        .filter(l ->  IdentityLinkType.CANDIDATE.equals(l.getType()))
                        .map(IdentityLink::getGroupId)
                        .findFirst()
                        .orElse(null));
            }

            // 5. Actions and process handling information
            String currentUser = SecurityUtils.getCurrentUserName();
            if (!wt.isFinished() && currentUser.equals(wt.getTaskAssignee())) {
                String processKey = extractProcessDefinitionKey(ti.getProcessDefinitionId());
                WorkflowProcessSupport support
                    = configurationService.getProcessSupportByProcessDefinitionId(processKey);
                if (support != null) {
                    WorkflowActionsDTO actions
                        = new WorkflowActionsDTO(support.getActions(wt.getTaskKey(), wt.getVariables()));
                    wt.setWorkflowActions(actions);
                }
            }

            result.add(wt);
        }

        return result;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#state(com.unidata.mdm.backend.common.context.GetTasksRequestContext)
     */
    @Override
    public WorkflowStateDTO state(GetTasksRequestContext ctx) {
        MeasurementPoint.start();
        try {

            TaskInfoQueryWrapper tiqw = createTaskInfoQueryWrapper(ctx);
            long count = tiqw.getTaskInfoQuery().count();
            if (count == 0) {
                return new WorkflowStateDTO();
            }

            WorkflowStateDTO result = new WorkflowStateDTO();
            result.setTotalCount(count);

            if(!ctx.isCountOnly()){
                List<? extends TaskInfo> tasks = tiqw.getTaskInfoQuery()
                        .includeProcessVariables()
                        .orderByTaskCreateTime()
                        .asc()
                        .listPage(ctx.getPage() * ctx.getCount(), ctx.getCount());

                result.setTasks(fillTaskInfos(tasks, ctx.isHistorical()));
            }

            return result;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#tasks(com.unidata.mdm.backend.common.context.GetTasksRequestContext)
     */
    @Override
    public List<WorkflowTaskDTO> tasks(GetTasksRequestContext ctx) {

        MeasurementPoint.start();
        try {

            TaskInfoQueryWrapper tiqw = createTaskInfoQueryWrapper(ctx);
            List<? extends TaskInfo> tasks = tiqw.getTaskInfoQuery().includeProcessVariables().orderByTaskCreateTime()
                    .asc().list();

            return fillTaskInfos(tasks, ctx.isHistorical());
        } finally {
            MeasurementPoint.stop();
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#process(com.unidata.mdm.backend.common.context.GetProcessRequestContext)
     */
    @Override
    public WorkflowProcessDTO process(GetProcessRequestContext ctx) {

        MeasurementPoint.start();
        try {

            if (ctx.isHistorical()) {
                return historicProcess(ctx);
            }

            ProcessInstanceQuery piq = runtimeService.createProcessInstanceQuery();
            if (ctx.getProcessInstanceId() != null) {
                piq.processInstanceId(ctx.getProcessInstanceId());
            }

            if (ctx.getProcessType() != null) {
                piq.variableValueEquals(WorkflowVariables.VAR_PROCESS_TYPE.getValue(), ctx.getProcessType().name());
            }

            if (ctx.getProcessDefinitionId() != null) {
                piq.processDefinitionKey(ctx.getProcessDefinitionId());
            }

            if (ctx.getProcessKey() != null) {
                piq.processInstanceBusinessKey(ctx.getProcessKey());
            }

            if (ctx.isSuspended()) {
                piq.suspended();
            } else {
                piq.active();
            }

            if (!ctx.isSkipVariables()) {
                piq.includeProcessVariables();
            }

            ProcessInstance instance = piq.singleResult();
            if (instance != null) {
                WorkflowProcessDTO result = new WorkflowProcessDTO();
                result.setProcessInstanceId(instance.getProcessInstanceId());
                result.setProcessDefinitionId(instance.getProcessDefinitionKey());
                result.setProcessTitle(instance.getProcessDefinitionName());

                Object val = instance.getProcessVariables().get(WorkflowVariables.VAR_PROCESS_TYPE.getValue());
                if (val != null) {
                    result.setProcessType(WorkflowProcessType.valueOf(val.toString()));
                }

                val = instance.getProcessVariables().get(WorkflowVariables.VAR_PROCESS_TRIGGER_TYPE.getValue());
                if (val != null && result.getProcessType() == WorkflowProcessType.RECORD_EDIT) {
                    result.setTriggerType(EditWorkflowProcessTriggerType.fromString(val.toString()));
                }

                result.setSuspended(instance.isSuspended());
                result.setEnded(instance.isEnded());

                return result;
            }

            return null;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Searches for a historic finished process.
     * @param ctx the context
     * @return DTO
     */
    private WorkflowProcessDTO historicProcess(GetProcessRequestContext ctx) {

        HistoricProcessInstanceQuery piq = historyService.createHistoricProcessInstanceQuery();
        if (ctx.getProcessInstanceId() != null) {
            piq.processInstanceId(ctx.getProcessInstanceId());
        }

        if (ctx.getProcessType() != null) {
            piq.variableValueEquals(WorkflowVariables.VAR_PROCESS_TYPE.name(), ctx.getProcessType().name());
        }

        if (ctx.getProcessDefinitionId() != null) {
            piq.processDefinitionKey(ctx.getProcessDefinitionId());
        }

        if (ctx.getProcessKey() != null) {
            piq.processInstanceBusinessKey(ctx.getProcessKey());
        }

        HistoricProcessInstance instance = piq.includeProcessVariables().singleResult();
        if (instance != null) {
            WorkflowProcessDTO result = new WorkflowProcessDTO();
            result.setProcessInstanceId(instance.getId());
            result.setProcessTitle(instance.getProcessDefinitionName());
            result.setProcessDefinitionId(instance.getProcessDefinitionKey());

            Object val = instance.getProcessVariables().get(WorkflowVariables.VAR_PROCESS_TYPE.getValue());
            if (val != null) {
                result.setProcessType(WorkflowProcessType.valueOf(val.toString()));
            }

            val = instance.getProcessVariables().get(WorkflowVariables.VAR_PROCESS_TRIGGER_TYPE.getValue());
            if (val != null && result.getProcessType() == WorkflowProcessType.RECORD_EDIT) {
                result.setTriggerType(EditWorkflowProcessTriggerType.fromString(val.toString()));
            }

            result.setSuspended(ctx.isSuspended());
            result.setEnded(instance.getEndTime() != null);

            return result;
        }

        return null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#suspend(com.unidata.mdm.backend.common.context.GetProcessRequestContext)
     */
    @Override
    public boolean suspend(GetProcessRequestContext ctx) {

        MeasurementPoint.start();
        try {

            WorkflowProcessDTO process = process(ctx);
            if (process != null && !process.isSuspended()) {
                runtimeService.suspendProcessInstanceById(process.getProcessInstanceId());
                return true;
            }

            return false;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#cancel(com.unidata.mdm.backend.common.context.GetProcessRequestContext, java.lang.String)
     */
    @Override
    public boolean cancel(GetProcessRequestContext ctx, String reason) {
        MeasurementPoint.start();
        try {

            WorkflowProcessDTO process = process(ctx);
            if (process != null && !process.isSuspended()) {
                runtimeService.deleteProcessInstance(process.getProcessInstanceId(), reason);
                return true;
            }

            return false;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#getAllAssignments()
     */
    @Override
    public List<WorkflowAssignmentDTO> getAllAssignments() {
        return assignments.values().stream()
                .flatMap(m -> m.values().stream())
                .collect(Collectors.toList());
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#getAssignmentsByEntityName(java.lang.String)
     */
   @Override
   public List<WorkflowAssignmentDTO> getAssignmentsByEntityName(String name) {

       Map<WorkflowProcessType, WorkflowAssignmentDTO> entityAssignments = assignments.get(name);
       if (!CollectionUtils.isEmpty(entityAssignments)) {
           return new ArrayList<>(entityAssignments.values());
       }

       return Collections.emptyList();
   }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#getAssignmentsByEntityNameAndType(java.lang.String, com.unidata.mdm.conf.WorkflowProcessType)
     */
    @Override
    public WorkflowAssignmentDTO getAssignmentsByEntityNameAndType(String name, WorkflowProcessType type) {

        Map<WorkflowProcessType, WorkflowAssignmentDTO> entityAssignments = assignments.get(name);
        if (!CollectionUtils.isEmpty(entityAssignments)) {
            return entityAssignments.get(type);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#getAssignmentsByEntityNameAsMap(java.lang.String)
     */
    @Override
    public Map<WorkflowProcessType, WorkflowAssignmentDTO> getAssignmentsByEntityNameAsMap(String name) {
        return assignments.get(name);
    }
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#updateAssignments(java.util.List)
     */
    @Override
    @Transactional
    public void updateAssignments(List<WorkflowAssignmentDTO> newAssignments) {

        if (!CollectionUtils.isEmpty(newAssignments)) {

            List<WorkflowAssignmentPO> updates = new ArrayList<>();
            for (WorkflowAssignmentDTO dto : newAssignments) {
                updates.add(dto2po(dto));
            }

            workflowAssignmentDao.upsert(updates);
            readAssignments();
            workflowAssignmentsNotifier.notifyAssignmetsChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readAssignments() {

        assignments.clear();

        List<WorkflowAssignmentDTO> read = loadAll();
        for (WorkflowAssignmentDTO dto : read) {

            EnumMap<WorkflowProcessType, WorkflowAssignmentDTO> values = assignments.get(dto.getName());
            if (Objects.isNull(values)) {
                values = new EnumMap<>(WorkflowProcessType.class);
                assignments.put(dto.getName(), values);
            }

            values.put(dto.getType(), dto);
        }
    }

    /**
     * Loads all current assignments from the DB.
     * @return
     */
    private List<WorkflowAssignmentDTO> loadAll() {

        List<WorkflowAssignmentPO> fetched = workflowAssignmentDao.loadAll();
        if (fetched != null && !fetched.isEmpty()) {

            List<WorkflowAssignmentDTO> result = new ArrayList<>();
            for (WorkflowAssignmentPO po : fetched) {
                result.add(po2dto(po));
            }

            return result;
        }

        return Collections.emptyList();
    }

    /**
     * PO 2 DTO.
     * @param po persistent object
     * @return DTO
     */
    private WorkflowAssignmentDTO po2dto(WorkflowAssignmentPO po) {

        if (po == null) {
            return null;
        }

        WorkflowAssignmentDTO dto = new WorkflowAssignmentDTO();
        dto.setCreateDate(po.getCreateDate());
        dto.setCreatedBy(po.getCreatedBy());
        dto.setId(po.getId());
        dto.setName(po.getName());
        dto.setProcessName(po.getProcessName());
        dto.setType(po.getType());
        dto.setTriggerType(po.getTriggerType());
        dto.setUpdateDate(po.getUpdateDate());
        dto.setUpdatedBy(po.getUpdatedBy());

        return dto;
    }

    /**
     * DTO 2 PO.
     * @param dto object
     * @return persistent object
     */
    private WorkflowAssignmentPO dto2po(WorkflowAssignmentDTO dto) {

        if (dto == null) {
            return null;
        }

        WorkflowAssignmentPO po = new WorkflowAssignmentPO();
        po.setCreateDate(dto.getCreateDate());
        po.setCreatedBy(dto.getCreatedBy());
        po.setId(dto.getId());
        po.setName(dto.getName());
        po.setProcessName(dto.getProcessName());
        po.setType(dto.getType());
        po.setTriggerType(dto.getTriggerType());
        po.setUpdateDate(dto.getUpdateDate());
        po.setUpdatedBy(dto.getUpdatedBy());

        return po;
    }

    private WorkflowCommentDTO activitiCommentToDto(final Comment c) {
        final WorkflowCommentDTO dto = new WorkflowCommentDTO();

        dto.setId(c.getId());
        dto.setUserLogin(c.getUserId());
        dto.setUsername(getUserFullName(c.getUserId()));
        dto.setTaskId(c.getTaskId());
        dto.setType(c.getType());
        dto.setProcessInstanceId(c.getProcessInstanceId());
        dto.setDateTime(ZonedDateTime.ofInstant(c.getTime().toInstant(), ZoneId.systemDefault()));
        dto.setMessage(c.getFullMessage());

        return dto;
    }

    private WorkflowAttachDTO activitiAttachmentToDto(final Attachment a) {
        final WorkflowAttachDTO dto = new WorkflowAttachDTO();

        dto.setId(a.getId());
        dto.setUserLogin(a.getUserId());
        dto.setUsername(getUserFullName(a.getUserId()));
        dto.setTaskId(a.getTaskId());
        dto.setType(a.getType());
        dto.setProcessInstanceId(a.getProcessInstanceId());
        dto.setDateTime(ZonedDateTime.ofInstant(a.getTime().toInstant(), ZoneId.systemDefault()));
        dto.setName(a.getName());
        dto.setDescription(a.getDescription());
        dto.setUrl(a.getUrl());

        return dto;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#getInstanceHistory(java.lang.String, boolean, java.util.Set)
     */
    @Override
    public List<WorkflowHistoryItemDTO> getInstanceHistory(final String processInstanceId, final boolean sortDateAsc,
                                                           final Set<String> types) {
        Authentication.setAuthenticatedUserId(SecurityUtils.getCurrentUserName());

        final List<WorkflowHistoryItemDTO> result = new ArrayList<>();

        if (!StringUtils.isEmpty(processInstanceId)) {
            if (types.contains(WorkflowHistoryItemDTO.ITEM_TYPE_WORKFLOW)) {
                getProcessInstanceHistoryList(processInstanceId, result);
            }
            if (types.contains(WorkflowHistoryItemDTO.ITEM_TYPE_COMMENT)) {
                getHistoryCommentList(processInstanceId, result);
            }
            if (types.contains(WorkflowHistoryItemDTO.ITEM_TYPE_ATTACH)) {
                getHistoryAttachmentList(processInstanceId, result);
            }
        }

        final Comparator<WorkflowHistoryItemDTO> cmp =
                sortDateAsc ? WF_HISTORY_DATE_COMPARATOR : WF_HISTORY_DATE_COMPARATOR.reversed();
        Collections.sort(result, cmp);

        return result;
    }

    private void getHistoryAttachmentList(final String processInstanceId,
                                          final List<WorkflowHistoryItemDTO> resultQueue) {
        final List<Attachment> attachments = taskService.getProcessInstanceAttachments(processInstanceId);

        for (final Attachment a : attachments) {
            final WorkflowHistoryItemDTO dto = new WorkflowHistoryItemDTO(WorkflowHistoryItemDTO.ITEM_TYPE_ATTACH);

            dto.setId(a.getId());
            dto.setName(a.getType());
            dto.setFilename(a.getName());
            dto.setAssignee(a.getUserId());
            dto.setDescription(a.getDescription());
            if (a.getTime() != null) {
                dto.setStartTime(ZonedDateTime.ofInstant(a.getTime().toInstant(), ZoneId.systemDefault()));
            }

            resultQueue.add(dto);
        }
    }

    private void getHistoryCommentList(final String processInstanceId,
                                       final List<WorkflowHistoryItemDTO> resultQueue) {
        final List<Comment> comments = taskService.getProcessInstanceComments(processInstanceId);

        for (final Comment c : comments) {
            final WorkflowHistoryItemDTO dto = new WorkflowHistoryItemDTO(WorkflowHistoryItemDTO.ITEM_TYPE_COMMENT);

            dto.setId(c.getId());
            dto.setName(c.getType());
            dto.setAssignee(c.getUserId());
            dto.setDescription(c.getFullMessage());
            if (c.getTime() != null) {
                dto.setStartTime(ZonedDateTime.ofInstant(c.getTime().toInstant(), ZoneId.systemDefault()));
            }

            resultQueue.add(dto);
        }
    }

    private void getProcessInstanceHistoryList(final String processInstanceId,
                                               final List<WorkflowHistoryItemDTO> resultQueue) {
        final List<HistoricTaskInstance> historicList =
                historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();

        for (final HistoricTaskInstance hti : historicList) {
            final WorkflowHistoryItemDTO dto = new WorkflowHistoryItemDTO(WorkflowHistoryItemDTO.ITEM_TYPE_WORKFLOW);

            dto.setId(hti.getId());
            if (hti.getTime() != null) {
                dto.setStartTime(ZonedDateTime.ofInstant(hti.getTime().toInstant(), ZoneId.systemDefault()));
            }
            if (hti.getEndTime() != null) {
                dto.setEndTime(ZonedDateTime.ofInstant(hti.getEndTime().toInstant(), ZoneId.systemDefault()));
            }
            if (hti.getClaimTime() != null) {
                dto.setClaimTime(ZonedDateTime.ofInstant(hti.getClaimTime().toInstant(), ZoneId.systemDefault()));
            }
            dto.setName(hti.getName());
            dto.setDescription(hti.getDescription());
            dto.setAssignee(hti.getAssignee());

            resultQueue.add(dto);
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#generateDiagram(java.lang.String)
     */
    @Override
    public InputStream generateDiagram(String processInstanceId, boolean finished) {

        String processDefinitionId;
        if (finished) {
            HistoricProcessInstance pi
                = historyService
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            if (Objects.isNull(pi)) {
                final String message = "Cannot create process diagram. Historical process instance not found for id {}.";
                LOGGER.warn(message, processInstanceId);
                throw new WorkflowException(message,
                        ExceptionId.EX_WF_CANNOT_GENERATE_DIAGRAM_HISTORICAL_PROCESS_NOT_FOUND,
                        processInstanceId);
            }

            processDefinitionId = pi.getProcessDefinitionId();
        } else {
            ProcessInstance pi
                = runtimeService
                    .createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            if (Objects.isNull(pi)) {
                final String message = "Cannot create process diagram. Process instance not found for id {}.";
                LOGGER.warn(message, processInstanceId);
                throw new WorkflowException(message,
                        ExceptionId.EX_WF_CANNOT_GENERATE_DIAGRAM_PROCESS_NOT_FOUND,
                        processInstanceId);
            }

            processDefinitionId = pi.getProcessDefinitionId();
        }

        ProcessDefinition pd = repositoryService.getProcessDefinition(processDefinitionId);
        if (!pd.hasGraphicalNotation()) {
            final String msg = "Process definition [{}] doesn't have graphical representation. Diagram cannot be created.";
            LOGGER.warn(msg, pd.getId());
            return null;
        }

        BpmnModel model = repositoryService.getBpmnModel(pd.getId());
        List<String> activeTasks = finished
                ? Collections.emptyList()
                : runtimeService.getActiveActivityIds(processInstanceId);
        ProcessDiagramGenerator pdg = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();

        return pdg.generateDiagram(model, "png", activeTasks);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.wf.IWorkflowService#hasEditTasks(java.lang.String)
     */
    @Override
    public boolean hasEditTasks(String etalonId) {

        GetTasksRequestContext tCtx = new GetTasksRequestContextBuilder()
                .assignedUser(SecurityUtils.getCurrentUserName())
                .processKey(etalonId)
                .build();

        List<WorkflowTaskDTO> tasks = tasks(tCtx);
        return tasks != null && tasks.stream().anyMatch(r -> r.getProcessType() == WorkflowProcessType.RECORD_EDIT);
    }
}
