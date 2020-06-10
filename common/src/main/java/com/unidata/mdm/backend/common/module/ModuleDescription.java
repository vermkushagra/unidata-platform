package com.unidata.mdm.backend.common.module;

/**
 * @author Mikhail Mikhailov
 * Description of a module.
 */
public class ModuleDescription {
    /**
     * Name of the feature.
     */
    private final String name;
    /**
     * Display name of the feature.
     */
    private final String displayName;
    /**
     * Its description.
     */
    private final String description;
    /**
     * Constructor.
     * @param name the name
     * @param displayName the display name
     * @param description the description
     */
    public ModuleDescription(String name, String displayName, String description) {
        super();
        this.name = name;
        this.displayName = displayName;
        this.description = description;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
