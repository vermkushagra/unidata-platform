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

/**
 *
 */
package com.unidata.mdm.backend.service.model.util.wrappers;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.meta.AbstractSimpleAttributeDef;

/**
 * @author Mikhail Mikhailov
 * Base entity wrapper type.
 */
public abstract class AttributesWrapper extends ModelWrapper {

    /**
     * Entity name
     */
    private final String id;
    /**
     * Attributes map.
     */
    private final Map<String, AttributeInfoHolder> attrs;

    /**
     * Constructor.
     */
    public AttributesWrapper(final String id, final Map<String, AttributeInfoHolder> attrs) {
        super();
        this.id = id;
        this.attrs = attrs;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the attrs
     */
    public Map<String, AttributeInfoHolder> getAttributes() {
        return attrs;
    }

    //todo create attribute structure!
    /**
     * Gets first main displayable attribute, if it exists.
     * @return attribute
     */
    public Pair<String, AttributeInfoHolder> getFirstMainDisplayableAttribute() {
        return attrs == null
                ? null
                : attrs.entrySet().stream()
                    .filter(e -> e.getValue().getAttribute() instanceof AbstractSimpleAttributeDef
                        && ((AbstractSimpleAttributeDef) e.getValue().getAttribute()).isMainDisplayable())
                    .findFirst()
                    .map(e -> new ImmutablePair<>(e.getKey(), e.getValue()))
                    .orElse(null);
    }

    /**
     * Gets all main displayable attributes, if it exists.
     *
     * @return attribute
     */
    public Collection<Pair<String, AttributeInfoHolder>> getMainDisplayableAttributes() {
        return attrs == null
                ? Collections.emptyList()
                : attrs.entrySet().stream()
                .filter(e -> e.getValue().getAttribute() instanceof AbstractSimpleAttributeDef
                        && ((AbstractSimpleAttributeDef) e.getValue().getAttribute()).isMainDisplayable())
                .map(e -> new ImmutablePair<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
