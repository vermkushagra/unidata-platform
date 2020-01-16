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

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.model.BvtMapModelElement;
import org.unidata.mdm.core.type.model.EntityModelElement;
import org.unidata.mdm.core.type.model.IdentityModelElement;
import org.unidata.mdm.meta.AbstractEntityDef;
import org.unidata.mdm.meta.CustomPropertyDef;
import org.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 *         Relation wrapper.
 */
public class RelationInfoHolder extends AbstractAttributesInfoHolder implements EntityModelElement, IdentityModelElement {
    /**
     * Relation.
     */
    private final RelationDef relation;
    /**
     * Entity name
     */
    private final String id;
    /**
     * This entity validity start.
     */
    private final Date validityStart;
    /**
     * This entity validity end.
     */
    private final Date validityEnd;
    /**
     * Constructor.
     */
    public RelationInfoHolder(RelationDef relation, final String id, final Map<String, AttributeModelElement> attrs) {
        super(attrs);
        this.relation = relation;
        this.id = id;
        this.validityStart = Objects.nonNull(relation.getValidityPeriod()) && Objects.nonNull(relation.getValidityPeriod().getStart())
                ? relation.getValidityPeriod().getStart().toGregorianCalendar(TimeZone.getDefault(), null, null).getTime()
                : null;
        this.validityEnd = Objects.nonNull(relation.getValidityPeriod()) && Objects.nonNull(relation.getValidityPeriod().getEnd())
                ? relation.getValidityPeriod().getEnd().toGregorianCalendar(TimeZone.getDefault(), null, null).getTime()
                : null;
    }
    /**
     * @return the relation
     */
    public RelationDef getRelation() {
        return relation;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return id;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Long getVersion() {
        return relation.getVersion();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return relation.getName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayName() {
        return relation.getDisplayName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEntity() {
        return false;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLookup() {
        return false;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRelation() {
        return true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBvtCapable() {
        return false;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public BvtMapModelElement getBvt() {
        return null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getCustomProperties() {
        return relation.getCustomProperties().stream()
                .collect(Collectors.toMap(CustomPropertyDef::getName, CustomPropertyDef::getValue));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Date getValidityStart() {
        return validityStart;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Date getValidityEnd() {
        return validityEnd;
    }
    /**
     * {@inheritDoc}
     */
    public AbstractEntityDef getAbstractEntity() {
        return getRelation();
    }
}
