package com.unidata.mdm.backend.api.rest.dto.wf;


/**
 * @author Mikhail Mikhailov
 * Action rest object.
 */
public class WorkflowActionRO {

    /**
     * Action code.
     */
    private String code;

    /**
     * Action (short) name.
     */
    private String name;

    /**
     * Action description.
     */
    private String description;

    /**
     * Constructor.
     */
    public WorkflowActionRO() {
        super();
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


    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }


    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
