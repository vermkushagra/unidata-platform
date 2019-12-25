package org.unidata.mdm.meta.type.info.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.model.BvtMapModelElement;
import org.unidata.mdm.core.type.model.CodeAttributedModelElement;
import org.unidata.mdm.core.type.model.EntityModelElement;
import org.unidata.mdm.core.type.model.IdentityModelElement;
import org.unidata.mdm.core.type.model.ModelSearchObject;
import org.unidata.mdm.core.type.model.SearchableModelElement;
import org.unidata.mdm.meta.AbstractEntityDef;
import org.unidata.mdm.meta.CustomPropertyDef;
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.service.impl.facades.EntitiesGroupModelElementFacade;

/**
 * @author Mikhail Mikhailov
 *         Simple lookup entity entry wrapper.
 */
public class LookupInfoHolder extends AbstractBvtMapInfoHolder
    implements IdentityModelElement, SearchableModelElement, EntityModelElement, CodeAttributedModelElement {
    /**
     * Entity to hold.
     */
    private final LookupEntityDef entity;
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
     * Lookups that refers to the lookup.
     */
    private final Map<LookupInfoHolder, Set<AttributeModelElement>> lookupFromReferences;

    /**
     * Entities that refers to the lookup.
     */
    private final Map<EntityInfoHolder, Set<AttributeModelElement>> entityFromReferences;

    /**
     * Lookups that are referenced by the lookup.
     */
    private final Map<LookupInfoHolder, Set<AttributeModelElement>> lookupToReferences;
    /**
     * Origin rules.
     */
 // TODO: Commented out in scope of UN-11834. Reenable ASAP.
//    private Map<String, List<DQRuleDef>> originRules;
    /**
     * Etalon rules.
     */
 // TODO: Commented out in scope of UN-11834. Reenable ASAP.
//    private List<DQRuleDef> etalonRules;
    /**
     * Constructor.
     *
     * @param entity the entity
     * @param id     entity name (id)
     * @param attrs  collected attributes
     */
    public LookupInfoHolder(LookupEntityDef entity, String id, Map<String, AttributeModelElement> attrs,
                               Map<String, Map<String, Integer>> bvtMap) {
        super(attrs, bvtMap);
        this.entity = entity;
        this.id = id;
        this.lookupFromReferences = new IdentityHashMap<>();
        this.lookupToReferences = new IdentityHashMap<>();
        this.entityFromReferences = new IdentityHashMap<>();
     // TODO: Commented out in scope of UN-11834. Reenable ASAP.
//        this.originRules = new HashMap<>();
//        this.etalonRules = new ArrayList<>();
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
    public Map<LookupInfoHolder, Set<AttributeModelElement>> getLookupFromReferences() {
        return lookupFromReferences;
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
     * Get entities refers to the lookup.
     *
     * @return entities refers to the lookup.
     */
    public Map<EntityInfoHolder, Set<AttributeModelElement>> getEntityFromReferences() {
        return entityFromReferences;
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
        return entity.getVersion();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ModelSearchObject getModelSearchElement() {

        ModelSearchObject modelSearchObject = getSearchObject();

        modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.entityDescription, entity.getDescription());
        modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.dashboardVisible, String.valueOf(entity.isDashboardVisible()));
        modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.type, "LookupEntity");
        String[] splitGroupName = EntitiesGroupModelElementFacade.getSplitPath(entity.getGroupName());
        modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.group, splitGroupName[splitGroupName.length - 1]);
     // TODO: Commented out in scope of UN-11834. Reenable ASAP.
//        entity.getClassifiers().stream().forEach(classifier -> modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.classifiers, classifier));
//        for (DQRuleDef dqRuleDef : entity.getDataQualities()) {
//            modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.dqName, dqRuleDef.getName());
//            modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.dqDescription, dqRuleDef.getDescription());
//        }
        return modelSearchObject;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ModelSearchObject getSearchObject() {
        return new ModelSearchObject(this.getId(), entity.getDisplayName());
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
    public boolean isRelation() {
        return false;
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
    public BvtMapModelElement getBvt() {
        return this;
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
    public AttributeModelElement getCodeAttribute() {
        return getAttributes().get(entity.getCodeAttribute().getName());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<AttributeModelElement> getCodeAliases() {
        List<AttributeModelElement> aliases = new ArrayList<>(entity.getAliasCodeAttributes().size());
        entity.getAliasCodeAttributes().forEach(el -> aliases.add(getAttributes().get(el.getName())));
        return aliases;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CodeAttributedModelElement getCodeAttributed() {
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
