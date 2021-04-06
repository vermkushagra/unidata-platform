package com.unidata.mdm.backend.api.rest.dto.wf;

/**
 * @author Mikhail Mikhailov
 * Process definition REST type.
 */
public class WorkflowProcessDefinitionRO {

    /**
     * Process id.
     */
    private String id;
    /**
     * Process name.
     */
    private String name;
    /**
     * Process description.
     */
    private String description;
    /**
     * Process type.
     */
    private String type;
    /**
     * Definition path.
     */
    private String path;
    /**
     * Constructor.
     */
    public WorkflowProcessDefinitionRO() {
        super();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
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

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }
}
