package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.unidata.mdm.backend.common.dto.CustomPropertyDefinition;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author Michael Yashin. Created on 29.05.2015.
 */
public class AbstractEntityDefinition {
    /**
     * List of simple attributes.
     */
    protected List<SimpleAttributeDefinition> simpleAttributes;
    /**
     * List of array attributes.
     */
    protected List<ArrayAttributeDefinitionRO> arrayAttributes;

    protected List<ReferenceInfo> entityDependency;

    protected List<CustomPropertyDefinition> customProperties = new ArrayList<>();

    /**
     * The name - [_a-zA-Z0-9].
     */
    protected String name;
    /**
     * Display name.
     */
    protected String displayName;
    /**
     * Description.
     */
    protected String description;
    /**
     * Display order.
     */
    protected int order;

    /**
     * Classifiers of entity
     */
    private Collection<String> classifiers;

    public List<SimpleAttributeDefinition> getSimpleAttributes() {
        if (simpleAttributes == null) {
            simpleAttributes = new ArrayList<>();
        }

        return simpleAttributes;
    }

    public void setSimpleAttributes(List<SimpleAttributeDefinition> simpleAttribute) {
        this.simpleAttributes = simpleAttribute;
    }

    public List<CustomPropertyDefinition> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Collection<CustomPropertyDefinition> customProperties) {
        if (CollectionUtils.isEmpty(customProperties)) {
            return;
        }
        this.customProperties.clear();
        this.customProperties.addAll(customProperties);
    }

    public void addCustomProperties(Collection<CustomPropertyDefinition> customProperties) {
        if (CollectionUtils.isEmpty(customProperties)) {
            return;
        }
        this.customProperties.addAll(customProperties);
    }

    public List<ArrayAttributeDefinitionRO> getArrayAttributes() {
        if (arrayAttributes == null) {
            arrayAttributes = new ArrayList<>();
        }

        return arrayAttributes;
    }

    public void setArrayAttributes(List<ArrayAttributeDefinitionRO> arrayAttributes) {
        this.arrayAttributes = arrayAttributes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the order
     */
    public int getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(int order) {
        this.order = order;
    }

    public Collection<String> getClassifiers() {
        return classifiers;
    }

    public void setClassifiers(Collection<String> classifiers) {
        this.classifiers = classifiers;
    }

    public List<ReferenceInfo> getEntityDependency() {
        return entityDependency;
    }

    public void setEntityDependency(List<ReferenceInfo> entityDependency) {
        this.entityDependency = entityDependency;
    }
}
