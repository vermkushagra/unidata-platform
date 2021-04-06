package com.unidata.mdm.backend.common.integration.wf;

/**
 * @author Mikhail Mikhailov
 * Edit process type details.
 */
public enum EditWorkflowProcessTriggerType implements WorkflowProcessTriggerType {
    /**
     * Trigger on all events.
     */
    ALL,
    /**
     * Trigger on __data__ conflict from different source systems.
     */
    VERSION_CONFLICT;

    @Override
    public String asString() {
        return this.name();
    }

    public static EditWorkflowProcessTriggerType fromString(String val) {

        for (EditWorkflowProcessTriggerType triggerType : EditWorkflowProcessTriggerType.values()) {
            if (triggerType.asString().equalsIgnoreCase(val)) {
                return triggerType;
            }
        }

        return null;
    }
}
