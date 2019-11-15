package org.unidata.mdm.meta.type.info.impl;

import java.util.Collection;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.model.BvtMapModelElement;
import org.unidata.mdm.core.type.model.EntityModelElement;
import org.unidata.mdm.core.type.model.IdentityModelElement;
import org.unidata.mdm.core.type.model.ModelSearchObject;
import org.unidata.mdm.core.type.model.SearchableModelElement;
import org.unidata.mdm.meta.AbstractEntityDef;
import org.unidata.mdm.meta.CustomPropertyDef;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.service.impl.facades.EntitiesGroupModelElementFacade;

/**
 * @author Mikhail Mikhailov
 */
public class EntityInfoHolder extends AbstractBvtMapInfoHolder
    implements IdentityModelElement, SearchableModelElement, EntityModelElement {
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
    private final Map<RelationInfoHolder, EntityInfoHolder> relationsTo;
    /**
     * Lookups that are referenced by the lookup.
     */
    private final Map<LookupInfoHolder, Set<AttributeModelElement>> lookupToReferences;
    /**
     * Outgoing relations.
     */
    private Map<RelationInfoHolder, EntityInfoHolder> relationsFrom;
    /**
     * Origin rules.
     */
    // TODO: Commented out in scope of UN-11834. Reenable ASAP.
    // private Map<String, List<DQRuleDef>> originRules;
    /**
     * Etalon rules.
     */
    // TODO: Commented out in scope of UN-11834. Reenable ASAP.
    // private List<DQRuleDef> etalonRules;
    /**
     * Entity name
     */
    private final String id;
    /**
     * Constructor.
     *
     * @param entity the entity
     * @param id     the id (entity name)
     * @param attrs  collected attributes
     * @param bvtMap the BVT map
     */
    public EntityInfoHolder(final EntityDef entity, final String id, final Map<String, AttributeModelElement> attrs,
                         Map<String, Map<String, Integer>> bvtMap) {

        super(attrs, bvtMap);
        this.id = id;
        this.entity = entity;
        this.relationsFrom = new IdentityHashMap<>();
        this.relationsTo = new IdentityHashMap<>();
        this.lookupToReferences = new IdentityHashMap<>();
        // TODO: Commented out in scope of UN-11834. Reenable ASAP.
        // this.originRules = new HashMap<>();
        // this.etalonRules = new ArrayList<>();
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
    public Map<RelationInfoHolder, EntityInfoHolder> getRelationsFrom() {
        return relationsFrom;
    }

    /**
     * Get incoming relations.
     *
     * @return incoming relations.
     */
    public Map<RelationInfoHolder, EntityInfoHolder> getRelationsTo() {
        return relationsTo;
    }

    /**
     * Get lookups that are referenced by the lookup.
     *
     * @return lookups that are referenced by the lookup.
     */
    public Map<LookupInfoHolder, Set<AttributeModelElement>> getLookupToReferences() {
        return lookupToReferences;
    }

    /**
     * @return collection of nested entities names.
     */
    public Collection<String> getNestedEntitiesNames() {
        return getAttributes().values().stream()
                .filter(AttributeModelElement::isComplex)
                .map(AttributeModelElement::getName)
                .collect(Collectors.toList());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ModelSearchObject getSearchObject() {
        return new ModelSearchObject(this.getId(), entity.getDisplayName());
    }

    @Override
    public Long getVersion() {
        return entity.getVersion();
    }

    @Override
    public ModelSearchObject getModelSearchElement() {

        ModelSearchObject modelSearchObject = getSearchObject();

        modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.entityDescription, entity.getDescription());
        modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.dashboardVisible, String.valueOf(entity.isDashboardVisible()));
        String[] splitGroupName = EntitiesGroupModelElementFacade.getSplitPath(entity.getGroupName());
        modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.group, splitGroupName[splitGroupName.length - 1]);
        modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.type, "Entity");
        // TODO: Commented out in scope of UN-11834. Reenable ASAP.
        // entity.getClassifiers().stream().forEach(classifier -> modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.classifiers, classifier));

        for (RelationInfoHolder relationWrapper : relationsFrom.keySet()) {
            modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.relationFromDisplayName, relationWrapper.getRelation().getDisplayName());
            modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.relationFromName, relationWrapper.getRelation().getName());
            relationWrapper.getAttributes().values().stream()
                    .map(AttributeModelElement::getName)
                    .forEach(name -> modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.relationFromAttributesNames, name));

            relationWrapper.getAttributes().values().stream()
                    .map(AttributeModelElement::getDisplayName)
                    .forEach(name -> modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.relationFromAttributesDisplayNames, name));
        }

        // TODO: Commented out in scope of UN-11834. Reenable ASAP.
        /*
        for (DQRuleDef dqRuleDef : entity.getDataQualities()) {
            modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.dqName, dqRuleDef.getName());
            modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.dqDescription, dqRuleDef.getDescription());
        }
        */

        return modelSearchObject;
    }
    /**
     * @return the validityStart
     */
    @Override
    public Date getValidityStart() {
        return validityStart;
    }
    /**
     * @return the validityEnd
     */
    @Override
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
    public boolean isRelation() {
        return false;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBvtCapable() {
        return true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return entity.getName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayName() {
        return entity.getDisplayName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getCustomProperties() {
        return entity.getCustomProperties().stream()
                .collect(Collectors.toMap(CustomPropertyDef::getName, CustomPropertyDef::getValue));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public BvtMapModelElement getBvt() {
        return this;
    }
    /**
     * {@inheritDoc}
     */
    public AbstractEntityDef getAbstractEntity() {
        return getEntity();
    }
    /**
     * @return the originRules
     */
// TODO: Commented out in scope of UN-11834. Reenable ASAP.
//    @Override
//    public Map<String, List<DQRuleDef>> getOriginRules() {
//        return originRules;
//    }

    /**
     * @return the etalonRules
     */
// TODO: Commented out in scope of UN-11834. Reenable ASAP.
//    @Override
//    public List<DQRuleDef> getEtalonRules() {
//        return etalonRules;
//    }

    /**
     * {@inheritDoc}
     */
 // TODO: Commented out in scope of UN-11834. Reenable ASAP.
//    @Override
//    public List<DQRuleDef> getDataQualities() {
//        return entity.getDataQualities();
//    }
}
