package com.unidata.mdm.backend.po.matching;

import java.util.Collection;

public class MatchingGroupPO {
    /**
     * Id field.
     */
    public static final String FIELD_ID = "id";
    /**
     * Name field.
     */
    public static final String FIELD_NAME = "name";
    /**
     * Entity name field.
     */
    public static final String FIELD_ENTITY_NAME = "entity_name";
    /**
     * Description field.
     */
    public static final String FIELD_DESCRIPTION = "description";
    /**
     * Active field.
     */
    public static final String FIELD_ACTIVE = "active";
    /**
     * Storage field.
     */
    public static final String FIELD_STORAGE_FKEY = "storage_fkey";
    /**
     * Group id field.
     */
    public static final String FIELD_GROUP_ID = "group_id";
    /**
     * Rule id field.
     */
    public static final String FIELD_RULE_ID = "rule_id";
    /**
     * Order number field.
     */
    public static final String FIELD_ORDER_NUMBER = "order_number";
    /**
     * Auto merge field
     */
    public static final String FIELD_AUTO_MERGE = "auto_merge";
    /**
     * Id.
     */
    private Integer id;
    /**
     * Group name.
     */
    private String name;
    /**
     * Entty name.
     */
    private String entityName;
    /**
     * Description.
     */
    private String description;
    /**
     * Rule assignments.
     */
    private Collection<Integer> ruleIds;
    /**
     * Auto merge flag
     */
    private boolean autoMerge;
    /**
     * Activity flag.
     */
    private boolean active;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<Integer> getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(Collection<Integer> ruleIds) {
        this.ruleIds = ruleIds;
    }

    public boolean isAutoMerge() {
        return autoMerge;
    }

    public void setAutoMerge(boolean autoMerge) {
        this.autoMerge = autoMerge;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
