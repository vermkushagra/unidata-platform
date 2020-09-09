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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.service.model.ModelSearchObject;
import com.unidata.mdm.backend.service.model.util.facades.EntitiesGroupModelElementFacade;
import com.unidata.mdm.meta.AbstractAttributeDef;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.DQRuleDef;
import com.unidata.mdm.meta.EntityDef;

/**
 * @author Mikhail Mikhailov
 */
public class EntityWrapper extends BVTMapWrapper implements DataQualityWrapper {
    /**
     * The entity hold.
     */
    private final EntityDef entity;
    /**
     * This entity validity start.
     */
    private final Date validityStart;
    /**
     * This entity validity end.
     */
    private final Date validityEnd;
    /**
     * Incoming relations.
     */
    private final Map<RelationWrapper, EntityWrapper> relationsTo;
    /**
     * Lookups that are referenced by the lookup.
     */
    private final Map<LookupEntityWrapper, Set<AttributeWrapper>> lookupToReferences;
    /**
     * Outgoing relations.
     */
    private Map<RelationWrapper, EntityWrapper> relationsFrom;
    /**
     * Origin rules.
     */
    private Map<String, List<DQRuleDef>> originRules;
    /**
     * Etalon rules.
     */
    private List<DQRuleDef> etalonRules;
    /**
     * Constructor.
     *
     * @param entity the entity
     * @param id     the id (entity name)
     * @param attrs  collected attributes
     * @param bvtMap the BVT map
     */
    public EntityWrapper(final EntityDef entity, final String id, final Map<String, AttributeInfoHolder> attrs,
                         Map<String, Map<String, Integer>> bvtMap) {

        super(id, attrs, bvtMap);
        this.entity = entity;
        this.relationsFrom = new IdentityHashMap<>();
        this.relationsTo = new IdentityHashMap<>();
        this.lookupToReferences = new IdentityHashMap<>();
        this.originRules = new HashMap<>();
        this.etalonRules = new ArrayList<>();
        this.validityStart = Objects.nonNull(entity.getValidityPeriod()) && Objects.nonNull(entity.getValidityPeriod().getStart())
                ? entity.getValidityPeriod().getStart().toGregorianCalendar(TimeZone.getDefault(), null, null).getTime()
                : null;
        this.validityEnd = Objects.nonNull(entity.getValidityPeriod()) && Objects.nonNull(entity.getValidityPeriod().getEnd())
                ? entity.getValidityPeriod().getEnd().toGregorianCalendar(TimeZone.getDefault(), null, null).getTime()
                : null;
    }

    /**
     * @return the entity
     */
    public EntityDef getEntity() {
        return entity;
    }


    /**
     * Get outgoing relations.
     *
     * @return outgoing relations.
     */
    public Map<RelationWrapper, EntityWrapper> getRelationsFrom() {
        return relationsFrom;
    }

    /**
     * Get incoming relations.
     *
     * @return incoming relations.
     */
    public Map<RelationWrapper, EntityWrapper> getRelationsTo() {
        return relationsTo;
    }

    /**
     * Get lookups that are referenced by the lookup.
     *
     * @return lookups that are referenced by the lookup.
     */
    public Map<LookupEntityWrapper, Set<AttributeWrapper>> getLookupToReferences() {
        return lookupToReferences;
    }

    /**
     * @return collection names of nested entities names.
     */
    public Collection<String> getNestedEntitiesNames() {
        return getAttributes().values().stream()
                .filter(AttributeInfoHolder::isComplex)
                .map(AttributeInfoHolder::getAttribute)
                .map(AbstractAttributeDef::getName)
                .collect(Collectors.toList());
    }

    @Override
    public String getUniqueIdentifier() {
        return getId();
    }

    @Override
    protected ModelSearchObject getSearchObject() {
        return new ModelSearchObject(this.getId(), entity.getDisplayName());
    }

    @Override
    public Long getVersionOfWrappedElement() {
        return entity.getVersion();
    }

    @Override
    public ModelSearchObject getModelSearchElement() {
        ModelSearchObject modelSearchObject = super.getModelSearchElement();
        modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.entityDescription, entity.getDescription());
        modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.dashboardVisible, String.valueOf(entity.isDashboardVisible()));
        String[] splitGroupName = EntitiesGroupModelElementFacade.getSplitPath(entity.getGroupName());
        modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.group, splitGroupName[splitGroupName.length - 1]);
        modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.type, "Entity");
        entity.getClassifiers().stream().forEach(classifier -> modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.classifiers, classifier));

        for (RelationWrapper relationWrapper : relationsFrom.keySet()) {
            modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.relationFromDisplayName, relationWrapper.getRelation().getDisplayName());
            modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.relationFromName, relationWrapper.getRelation().getName());
            relationWrapper.getAttributes().values().stream()
                    .map(AttributeInfoHolder::getAttribute)
                    .map(AbstractAttributeDef::getName)
                    .forEach(name -> modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.relationFromAttributesNames, name));

            relationWrapper.getAttributes().values().stream()
                    .map(AttributeInfoHolder::getAttribute)
                    .map(AbstractAttributeDef::getDisplayName)
                    .forEach(name -> modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.relationFromAttributesDisplayNames, name));
        }

        for (DQRuleDef dqRuleDef : entity.getDataQualities()) {
            modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.dqName, dqRuleDef.getName());
            modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.dqDescription, dqRuleDef.getDescription());
        }

        return modelSearchObject;
    }

    /**
     * @return the validityStart
     */
    public Date getValidityStart() {
        return validityStart;
    }

    /**
     * @return the validityEnd
     */
    public Date getValidityEnd() {
        return validityEnd;
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
    public boolean isEntity() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractEntityDef getAbstractEntity() {
        return getEntity();
    }

    /**
     * @return the originRules
     */
    @Override
    public Map<String, List<DQRuleDef>> getOriginRules() {
        return originRules;
    }

    /**
     * @return the etalonRules
     */
    @Override
    public List<DQRuleDef> getEtalonRules() {
        return etalonRules;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DQRuleDef> getDataQualities() {
        return entity.getDataQualities();
    }
}
