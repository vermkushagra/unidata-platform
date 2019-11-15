package org.unidata.mdm.core.type.model;

import javax.annotation.Nonnull;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Object responsible for collecting all necessary information about things which should be available for searching.
 */
public class ModelSearchObject {
    /**
     * Name of model element
     */
    private final String entityName;
    /**
     * Display name of model element
     */
    private final String displayName;
    /**
     * Collection things which should be available for searching
     */
    private final Multimap<String, String> searchElements = HashMultimap.create();

    public ModelSearchObject(String entityName, String displayName) {
        this.entityName = entityName;
        this.displayName = displayName;
        addSearchElement(SearchElementType.entityName, entityName);
        addSearchElement(SearchElementType.entityDisplayName, displayName);
    }

    public String getEntityName() {
        return entityName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Multimap<String, String> getSearchElements() {
        return searchElements;
    }

    public boolean addSearchElement(@Nonnull SearchElementType searchElementType, @Nonnull String value) {
        return searchElements.put(searchElementType.name(), value);
    }

    public enum SearchElementType {
        type,
        attributeName,
        attributeDisplayName,
        entityName,
        entityDisplayName,
        entityDescription,
        dashboardVisible,
        relationFromName,
        relationFromDisplayName,
        relationFromAttributesNames,
        relationFromAttributesDisplayNames,
        dqName,
        dqDescription,
        group,
        groupDisplayName,
        classifiers;
    }
}
