package com.unidata.mdm.backend.api.rest.dto.cleanse;

/**
 * The Class CFApply.
 */
public class CFApply {

    /** The name. */
    private String name;

    /** The action. */
    private CFUploadAction action;

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name
     *            the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the action.
     *
     * @return the action
     */
    public CFUploadAction getAction() {
        return action;
    }

    /**
     * Sets the action.
     *
     * @param action
     *            the new action
     */
    public void setAction(CFUploadAction action) {
        this.action = action;
    }
}
