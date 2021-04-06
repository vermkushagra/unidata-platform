package com.unidata.mdm.backend.service.wf;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.unidata.mdm.backend.common.context.AssignTaskRequestContext;
import com.unidata.mdm.backend.common.context.CompleteTaskRequestContext;
import com.unidata.mdm.backend.common.context.GetProcessRequestContext;
import com.unidata.mdm.backend.common.context.GetTasksRequestContext;
import com.unidata.mdm.backend.common.context.StartProcessRequestContext;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAssignmentDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAttachContentDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAttachDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowCommentDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowCompletionStateDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowHistoryItemDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowProcessDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowStateDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTaskDTO;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;
import com.unidata.mdm.conf.WorkflowProcessType;

public interface WorkflowService extends AfterContextRefresh {

    /**
     * Activates a process definition, denoted by path.
     * @param resourcePath the path
     */
    void deployProcess(String resourcePath);

    /**
     * Starts a new process instance.
     *
     * @param ctx the context to use
     * @return true upon success, false otherwise
     */
    boolean start(StartProcessRequestContext ctx);

    /**
     * Complete step.
     *
     * @param ctx the context
     * @return bollean upon success, false otherwise
     */
    WorkflowCompletionStateDTO complete(CompleteTaskRequestContext ctx);

    WorkflowCommentDTO addComment(String taskId, String processId, String commentMessage);

    List<WorkflowCommentDTO> getComments(String taskId, String processId, boolean dateAsc);

    WorkflowAttachDTO addAttachment(String taskId, String processId, String type, String name, String description,
            InputStream attachmentInputStream);

    List<WorkflowAttachDTO> getAttachments(String taskId, String processId, boolean sortDateAsc);

    WorkflowAttachContentDTO getAttachmentContent(String attachmentId);

    void assign(AssignTaskRequestContext ctx);

    void unassign(AssignTaskRequestContext ctx);

    /**
     * Returns workflow state for a particular user alone with tasks portion and
     * total count.
     *
     * @param ctx the context
     * @return state
     */
    WorkflowStateDTO state(GetTasksRequestContext ctx);

    /**
     * Gets list of tasks according to supplied criteria.
     *
     * @param ctx the context
     * @return list.
     */
    List<WorkflowTaskDTO> tasks(GetTasksRequestContext ctx);

    /**
     * Gets process instance by id.
     *
     * @param ctx the context
     * @return DTO
     */
    WorkflowProcessDTO process(GetProcessRequestContext ctx);

    /**
     * Suspend process instance by query.
     *
     * @param ctx the context
     * @return true, if suspended, false otherwise
     */
    boolean suspend(GetProcessRequestContext ctx);

    /**
     * Cancel a process.
     * @param ctx the context
     * @param reason reason for cancellation
     * @return true if successful, false otherwise
     */
    boolean cancel(GetProcessRequestContext ctx, String reason);

    /**
     * Gets all assignments.
     * @return assignments
     */
    List<WorkflowAssignmentDTO> getAllAssignments();

    /**
        * Gets assignments, available for entity name 'name'.
        * @param name entity name
        * @return list of assignments
        */
    List<WorkflowAssignmentDTO> getAssignmentsByEntityName(String name);

    /**
     * Gets assignments, available for entity name 'name' and type 'type.
     * @param name the entity name
     * @param type the assignment type
     * @return assignment or null, if nothing assigned
     */
    WorkflowAssignmentDTO getAssignmentsByEntityNameAndType(String name, WorkflowProcessType type);

    /**
     * Gets assignments, available for entity name 'name' and type 'type.
     * @param name the entity name
     * @return assignment or null, if nothing assigned
     */
    Map<WorkflowProcessType, WorkflowAssignmentDTO> getAssignmentsByEntityNameAsMap(String name);

    /**
     * Set updates.
     * @param assignments the assignments
     */
    void updateAssignments(List<WorkflowAssignmentDTO> assignments);

    /**
     * Reads assignments from DB to cache.
     */
    void readAssignments();

    List<WorkflowHistoryItemDTO> getInstanceHistory(String processInstanceId, boolean sortDateAsc, Set<String> types);

    /**
     * Gets PNG diagram othe process, with current step (s) highlightet.
     * @param processInstanceId process instance id
     * @param finished whether the process ahs finished or not
     * @return byte stream
     */
    InputStream generateDiagram(String processInstanceId, boolean finished);

    /**
     * tells whether current user has edit tasks
     * @param etalonId the record etalon id
     * @return true, if has, false otherwise
     */
    boolean hasEditTasks(String etalonId);

}