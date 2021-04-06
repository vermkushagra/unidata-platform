package com.unidata.mdm.backend.api.rest.dto.wf;


/**
 * @author Mikhail Mikhailov
 * Supported work flow process type.
 */
public class WorkflowProcessTypeRO {

    /**
     * The code.
     */
    private String code;

    /**
     * Name.
     */
    private String name;

    /**
     * Description.
     */
    private String description;

    /**
     * Constructor.
     */
    public WorkflowProcessTypeRO() {
        super();
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
