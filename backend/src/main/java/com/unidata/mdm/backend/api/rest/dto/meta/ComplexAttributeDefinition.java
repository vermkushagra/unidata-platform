package com.unidata.mdm.backend.api.rest.dto.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Michael Yashin. Created on 25.05.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComplexAttributeDefinition extends AbstractAttributeDefinition {
    /**
     * Nested entity definition.
     */
    protected NestedEntityDefinition nestedEntity;
    /**
     * Minimum appearance count.
     */
    protected Long minCount;
    /**
     * Maximum appearance count.
     */
    protected Long maxCount;
    /**
     * Sub entity key attribute.
     */
    protected String subEntityKeyAttribute;

    protected int order;

    public NestedEntityDefinition getNestedEntity() {
        return nestedEntity;
    }

    public void setNestedEntity(NestedEntityDefinition nestedEntity) {
        this.nestedEntity = nestedEntity;
    }

    public Long getMinCount() {
        return minCount;
    }

    public void setMinCount(Long minCount) {
        this.minCount = minCount;
    }

    public Long getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(Long maxCount) {
        this.maxCount = maxCount;
    }

    public String getSubEntityKeyAttribute() {
        return subEntityKeyAttribute;
    }

    public void setSubEntityKeyAttribute(String subEntityKeyAttribute) {
        this.subEntityKeyAttribute = subEntityKeyAttribute;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
