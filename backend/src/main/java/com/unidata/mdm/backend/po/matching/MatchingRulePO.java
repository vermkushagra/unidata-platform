package com.unidata.mdm.backend.po.matching;

import java.util.Collection;
/**
 * Matchig rule PO.
 */
public class MatchingRulePO {
    /**
     * Id field.
     */
    public static final String FIELD_ID = "id";
    /**
     * Name field.
     */
    public static final String FIELD_NAME = "name";
    /**
     * Settings field.
     */
    public static final String FIELD_SETTINGS = "settings";
    /**
     * Entity name field.
     */
    public static final String FIELD_ENTITY_NAME = "entity_name";
    /**
     * Active field.
     */
    public static final String FIELD_ACTIVE = "active";
    /**
     * Description field.
     */
    public static final String FIELD_DESCRIPTION = "description";
    /**
     * Storage field.
     */
    public static final String FIELD_STORAGE_FKEY = "storage_fkey";
    /**
     * Id.
     */
    private Integer id;
    /**
     * Name.
     */
    private String name;
    /**
     * Settings.
     */
    private String settings;
    /**
     * Entity name.
     */
    private String entityName;
    /**
     * Description.
     */
    private String description;
    /**
     * Active or not.
     */
    private boolean active;
    /**
     * Matching algorithms.
     */
    private Collection<MatchingAlgorithmPO> matchingAlgorithms;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Collection<MatchingAlgorithmPO> getMatchingAlgorithms() {
        return matchingAlgorithms;
    }

    public void setMatchingAlgorithms(Collection<MatchingAlgorithmPO> matchingAlgorithms) {
        this.matchingAlgorithms = matchingAlgorithms;
    }
}
