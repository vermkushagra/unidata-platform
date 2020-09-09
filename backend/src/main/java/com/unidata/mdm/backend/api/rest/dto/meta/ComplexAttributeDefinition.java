/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
