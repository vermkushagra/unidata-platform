package com.unidata.mdm.backend.service.model.util.wrappers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.service.model.ModelSearchObject;
import com.unidata.mdm.backend.service.model.util.facades.EntitiesGroupModelElementFacade;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.DQRuleDef;
import com.unidata.mdm.meta.LookupEntityDef;

/**
 * @author Mikhail Mikhailov
 *         Simple lookup entity entry wrapper.
 */
public class LookupEntityWrapper extends BVTMapWrapper implements AbstractEntityWrapper {
    /**
     * Entity to hold.
     */
    private final LookupEntityDef entity;
    /**
     * This entity validity start.
     */
    private final Date validityStart;
    /**
     * This entity validity end.
     */
    private final Date validityEnd;
    /**
     * Lookups that refers to the lookup.
     */
    private final Map<LookupEntityWrapper, Set<AttributeWrapper>> lookupFromReferences;

    /**
     * Entities that refers to the lookup.
     */
    private final Map<EntityWrapper, Set<AttributeWrapper>> entityFromReferences;

    /**
     * Lookups that are referenced by the lookup.
     */
    private final Map<LookupEntityWrapper, Set<AttributeWrapper>> lookupToReferences;

    /**
     * Constructor.
     *
     * @param entity the entity
     * @param id     entity name (id)
     * @param attrs  collected attributes
     */
    public LookupEntityWrapper(LookupEntityDef entity, String id, Map<String, AttributeInfoHolder> attrs,
                               Map<String, Map<String, Integer>> bvtMap) {
        super(id, attrs, bvtMap);
        this.entity = entity;
        this.lookupFromReferences = new HashMap<>();
        this.lookupToReferences = new HashMap<>();
        this.entityFromReferences = new HashMap<>();
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
    public LookupEntityDef getEntity() {
        return entity;
    }

    /**
     * @return lookups (lookup and attribute ) refers to the lookup.
     */
    public Map<LookupEntityWrapper, Set<AttributeWrapper>> getLookupFromReferences() {
        return lookupFromReferences;
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
     * Get entities refers to the lookup.
     *
     * @return entities refers to the lookup.
     */
    public Map<EntityWrapper, Set<AttributeWrapper>> getEntityFromReferences() {
        return entityFromReferences;
    }

    @Override
    public String getUniqueIdentifier() {
        return getId();
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
        modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.type, "LookupEntity");
        String[] splitGroupName = EntitiesGroupModelElementFacade.getSplitPath(entity.getGroupName());
        modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.group, splitGroupName[splitGroupName.length - 1]);
        entity.getClassifiers().stream().forEach(classifier -> modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.classifiers, classifier));
        for (DQRuleDef dqRuleDef : entity.getDataQualities()) {
            modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.dqName, dqRuleDef.getName());
            modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.dqDescription, dqRuleDef.getDescription());
        }
        return modelSearchObject;
    }

    @Override
    protected ModelSearchObject getSearchObject() {
        return new ModelSearchObject(this.getId(), entity.getDisplayName());
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
        return true;
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
    public AbstractEntityDef getAbstractEntity() {
        return getEntity();
    }
}
