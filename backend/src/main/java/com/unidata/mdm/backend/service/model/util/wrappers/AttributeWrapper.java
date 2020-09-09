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

package com.unidata.mdm.backend.service.model.util.wrappers;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.meta.DQRuleDef;

import java.util.HashSet;
import java.util.Set;

/**
 * Holds attribute info including its full name name and attribute info holder.
 * @author Ruslan Trachuk
 */
public class AttributeWrapper implements ValueWrapper {
    /**
     * Full attribute name including nested attributes name.
     */
    private final String fullAttributeName;
    /**
     * Attribute info.
     */
    private final AttributeInfoHolder attribute;
    /**
     *
     */
    private final Set<DQRuleDef> relatedDQRules;


    public AttributeWrapper(String fullAttributeName, AttributeInfoHolder attribute) {
        this.fullAttributeName = fullAttributeName;
        this.attribute = attribute;
        relatedDQRules = new HashSet<>();
    }

    /**
     * Get full attribute name.
     * @return full attribute name.
     */
    public String getFullAttributeName() {
        return fullAttributeName;
    }

    /**
     * Get attribute info.
     * @return attribute info
     */
    public AttributeInfoHolder getAttribute() {
        return attribute;
    }

    /**
     * Get set of DQ rules that refer to the attribute.
     * @return set of DQ rules that refer to the attribute
     */
    public Set<DQRuleDef> getRelatedDQRules() {
        return relatedDQRules;
    }

    @Override
    public String toString() {
        return "AttributeWrapper{" +
                "fullAttributeName='" + fullAttributeName + '\'' +
                '}';
    }
}
