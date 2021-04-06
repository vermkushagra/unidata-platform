package com.unidata.mdm.backend.service.wf.po;

import com.unidata.mdm.backend.common.integration.wf.WorkflowProcessTriggerType;
import com.unidata.mdm.backend.po.AbstractPO;
import com.unidata.mdm.conf.WorkflowProcessType;

/**
 * @author Mikhail Mikhailov
 * Work flow assignment persistent object.
 */
public class WorkflowAssignmentPO extends AbstractPO {

    /**
     * Table name.
     */
    public static final String TABLE_NAME = "meta_process_assignment";
    /**
     * ID column.
     */
    public static final String FIELD_ID = "id";
    /**
     * Object (register) name  column.
     */
    public static final String FIELD_NAME = "name";
    /**
     * Process type column.
     */
    public static final String FIELD_TYPE = "type";
    /**
     * Name of the assigned process column.
     */
    public static final String FIELD_PROCESS_NAME = "process_name";
    /**
     * Process trigger type.
     */
    public static final String FIELD_TRIGGER_TYPE = "trigger_type";
    /**
     * ID.
     */
    private Long id;
    /**
     * Object (register) name.
     */
    private String name;
    /**
     * Process type.
     */
    private WorkflowProcessType type;
    /**
     * The trigger type.
     */
    private WorkflowProcessTriggerType triggerType;
    /**
     * Name of the assigned process.
     */
    private String processName;
    /**
     * Constructor.
     */
    public WorkflowAssignmentPO() {
        super();
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public WorkflowProcessType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(WorkflowProcessType type) {
        this.type = type;
    }

    /**
     * @return the processName
     */
    public String getProcessName() {
        return processName;
    }

    /**
     * @param processName the processName to set
     */
    public void setProcessName(String processName) {
        this.processName = processName;
    }

    /**
     * @return the triggerType
     */
    public WorkflowProcessTriggerType getTriggerType() {
        return triggerType;
    }

    /**
     * @param triggerType the triggerType to set
     */
    public void setTriggerType(WorkflowProcessTriggerType triggerType) {
        this.triggerType = triggerType;
    }

}
