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

package org.unidata.mdm.meta.type.info.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.model.AttributedModelElement;

/**
 * @author Mikhail Mikhailov
 * Base entity wrapper type.
 */
public abstract class AbstractAttributesInfoHolder implements AttributedModelElement {
    /**
     * Attributes map.
     */
    private final Map<String, AttributeModelElement> attrs;
    /**
     * Constructor.
     */
    public AbstractAttributesInfoHolder(final Map<String, AttributeModelElement> attrs) {
        super();
        this.attrs = attrs;
    }
    /**
     * @return the attrs
     */
    @Override
    public Map<String, AttributeModelElement> getAttributes() {
        return attrs;
    }

    //todo create attribute structure!
    /**
     * Gets first main displayable attribute, if it exists.
     * @return attribute
     */
    public Pair<String, AttributeModelElement> getFirstMainDisplayableAttribute() {
        return attrs == null
                ? null
                : attrs.entrySet().stream()
                    .filter(e -> e.getValue().isMainDisplayable())
                    .findFirst()
                    .map(e -> new ImmutablePair<>(e.getKey(), e.getValue()))
                    .orElse(null);
    }

    /**
     * Gets all main displayable attributes, if it exists.
     *
     * @return attribute
     */
    public Collection<Pair<String, AttributeModelElement>> getMainDisplayableAttributes() {
        return attrs == null
                ? Collections.emptyList()
                : attrs.entrySet().stream()
                .filter(e -> e.getValue().isMainDisplayable())
                .map(e -> new ImmutablePair<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
