package com.unidata.mdm.backend.common.integration.wf;


/**
 * @author Mikhail Mikhailov
 * Work flow action description.
 */
public class WorkflowAction {

    /**
     * Action code.
     */
    private final String code;

    /**
     * Action (short) name.
     */
    private final String name;

    /**
     * Action description.
     */
    private final String description;

    /**
     * Constructor.
     */
    public WorkflowAction(String code, String name, String description) {
        super();
        this.code = code;
        this.name = name;
        this.description = description;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
