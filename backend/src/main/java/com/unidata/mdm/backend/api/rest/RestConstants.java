/**
 *
 */
package com.unidata.mdm.backend.api.rest;


/**
 * @author Mikhail Mikhailov
 * String constants common to various rest services.
 */
public interface RestConstants {

    /**
     * ID param.
     */
    String DATA_PARAM_ID = "id";
    /**
     * External ID param.
     */
    String DATA_PARAM_EXT_ID = "externalId";
    /**
     * Operation ID param.
     */
    String DATA_PARAM_OPERATION_ID = "operationId";
    /**
     * Source system param.
     */
    String DATA_PARAM_SOURCE_SYSTEM = "sourceSystem";
    /**
     * Task ID param.
     */
    String DATA_PARAM_TASK_ID = "taskId";
    /**
     * Task data param.
     */
    String DATA_PARAM_TASKS = "tasks";
    /**
     * ID winner param.
     */
    String DATA_PARAM_WINNER_ID = "winnerEtalonId";
    /**
     * Attribute name param.
     */
    String DATA_PARAM_ATTR = "attr";
    /**
     * Name param.
     */
    String DATA_PARAM_NAME = "name";
    /**
     * Type param.
     */
    String DATA_PARAM_TYPE = "type";
    /**
     * Date param.
     */
    String DATA_PARAM_DATE = "date";
    /**
     * Last update date param.
     */
    String DATA_PARAM_LUD = "lud";
    /**
     * Date param from.
     */
    String DATA_PARAM_FROM = "from";
    /**
     * Date param time stamps.
     */
    String DATA_PARAM_TIMESTAMPS = "timestamps";
    /**
     * Date param to.
     */
    String DATA_PARAM_TO = "to";
    /**
     * Upload file attachment param.
     */
    String DATA_PARAM_FILE = "file";
    /**
     * Upload file name attachment param.
     */
    String DATA_PARAM_FILENAME = "filename";
    /**
     * Merged param.
     */
    String DATA_PARAM_MERGED = "merged";
    /**
     * Finished param.
     */
    String DATA_PARAM_FINISHED = "finished";
    /**
     * Include inactive param.
     */
    String DATA_PARAM_INCLUDE_INACTIVE = "inactive";
    /**
     * Include drafts param.
     */
    String DATA_PARAM_INCLUDE_DRAFTS = "drafts";
    /**
     * Include diff to draft param.
     */
    String DATA_PARAM_DIFF_TO_DRAFT = "diffToDraft";
    /**
     * Include diff to previous state param.
     */
    String DATA_PARAM_DIFF_TO_PREVIOUS = "diffToPrevious";
    /**
     * Include drafts param.
     */
    String DATA_PARAM_RETURN_FIELDS = "returnFields";
    /**
     * Data path tag.
     */
    String PATH_PARAM_DATA = "data";
    /**
     * Blob path tag.
     */
    String PATH_PARAM_BLOB = "blob";
    /**
     * Clob path tag.
     */
    String PATH_PARAM_CLOB = "clob";
    /**
     * Path param id.
     */
    String PATH_PARAM_ID = "id";
    /**
     * Path param keys.
     */
    String PATH_PARAM_KEYS = "keys";
    /**
     * Etalon path component.
     */
    String PATH_PARAM_ETALON = "etalon";
    /**
     * Origin path component.
     */
    String PATH_PARAM_ORIGIN = "origin";
    /**
     * Origin id path component.
     */
    String PATH_PARAM_ORIGIN_ID = "originId";
    /**
     * External path component.
     */
    String PATH_PARAM_EXTERNAL = "external";
    /**
     * Version path component.
     */
    String PATH_PARAM_VERSION = "version";
    /**
     * Time line.
     */
    String PATH_PARAM_TIMELINE = "timeline";
    /**
     * Merge path component.
     */
    String PATH_PARAM_MERGE = "merge";
    /**
     * Workflow.
     */
    String PATH_PARAM_WORKFLOW = "workflow";
    /**
     * Complete task.
     */
    String PATH_PARAM_COMPLETE = "complete";
    /**
     * Approve.
     */
    String PATH_PARAM_APPROVE = "approve";
    /**
     * Decline.
     */
    String PATH_PARAM_DECLINE = "decline";
    /**
     * Range.
     */
    String PATH_PARAM_RANGE = "range";
    /**
     * Run.
     */
    String PATH_PARAM_RUN = "run";
    /**
     * Wipe a record.
     */
    String PATH_PARAM_WIPE = "wipe";
    /**
     * Configure.
     */
    String PATH_PARAM_CONFIGURE = "configure";
    /**
     * Notifications.
     */
    String PATH_PARAM_NOTIFICATIONS = "notifications";
    /**
     * Tasks.
     */
    String PATH_PARAM_TASKS = "tasks";
    /**
     * Tasks.
     */
    String PATH_PARAM_TASK_STATS = "stat";
    /**
     * Types.
     */
    String PATH_PARAM_TYPES = "types";
    /**
     * Assignments.
     */
    String PATH_PARAM_ASSIGNMENTS = "assignments";
    /**
     * Assignable entities.
     */
    String PATH_PARAM_ASSIGNABLE_ENTITIES = "assignable-entities";
    /**
     * Processes.
     */
    String PATH_PARAM_PROCESSES = "processes";
    /**
     * Attachments id.
     */
    String PATH_PARAM_ATTACHMENT_ID = "attachmentId";
    /**
     * Download parameter.
     */
    String PATH_PARAM_DOWNLOAD = "download";
    /**
     * Assign.
     */
    String PATH_PARAM_ASSIGN = "assign";
    /**
     * Assign.
     */
    String PATH_PARAM_UNASSIGN = "unassign";
    /**
     * Process comments.
     */
    String PATH_PARAM_PROCESS_INSTANCE_ID = "processInstanceId";
    /**
     * Process comments.
     */
    String PATH_PARAM_TASK_ID = "taskId";
    /**
     * Process comments.
     */
    String PATH_PARAM_PROCESS_COMMENT = "comment";
    /**
     * Process attaches.
     */
    String PATH_PARAM_PROCESS_ATTACH = "attach";
    /**
     * History.
     */
    String PATH_PARAM_HISTORY = "history";
    /**
     * Item types filter fro History.
     */
    String  PATH_PARAM_ITEM_TYPE = "itemType";
    /**
     * Diagram.
     */
    String PATH_PARAM_DIAGRAM = "diagram";
    /**
     * Security.
     */
    String PATH_PARAM_SECURITY = "security";
    /**
     * User.
     */
    String PATH_PARAM_USER = "user";
    /**
     * Role.
     */
    String PATH_PARAM_ROLE = "role";
    /**
     * System.
     */
    String PATH_PARAM_SYSTEM = "system";
    /**
     * Logs.
     */
    String PATH_PARAM_LOGS = "logs";
    /**
     * Input timestamp regex.
     */
    String DEFAULT_TIMESTAMP_PATTERN = "(([0-9T\\-\\:\\.]{23})?)";
    /**
     * Entity name.
     */
	String DATA_IMPORT_PARAMS = "importParams";
    /**
     * Sort by date asc or desc.
     */
    String QUERY_PARAM_SORT_DATE_ASC = "sortDateAsc";
    /**
     * Lookup entity.
     */
    String LOOKUP_ENTITY_TYPE = "LookupEntity";
    /**
     * Lookup entity.
     */
    String REGISTER_ENTITY_TYPE = "Entity";
    /**
     *
     */
    String PATH_PARAM_RELATION_BULK = "relation_bulk";

}
