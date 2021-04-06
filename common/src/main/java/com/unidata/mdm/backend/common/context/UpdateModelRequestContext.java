/**
 *
 */
package com.unidata.mdm.backend.common.context;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.EntitiesGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.EnumerationDataType;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SourceSystemDef;

/**
 * @author Mikhail Mikhailov
 *         Container for meta model updates.
 */
public class UpdateModelRequestContext extends CommonRequestContext implements ModelStorageSpecificContext, Serializable {
    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = 8533210708984117919L;
    /**
     * Top level entity updates
     */
    private final List<EntityDef> entityUpdate;
    /**
     * Lookup entity updates.
     */
    private final List<LookupEntityDef> lookupEntityUpdate;
    /**
     * Nested entity updates.
     */
    private final List<NestedEntityDef> nestedEntityUpdate;
    /**
     * Enumeration updates.
     */
    private final List<EnumerationDataType> enumerationsUpdate;
    /**
     * Source systems updates.
     */
    private final List<SourceSystemDef> sourceSystemsUpdate;
    /**
     * Relations updates.
     */
    private final List<RelationDef> relationsUpdate;
    /**
     * Entities group updates.
     */
    private final EntitiesGroupDef entitiesGroupsUpdate;
    /**
     * Cleanse functions update.
     */
    private final CleanseFunctionGroupDef cleanseFunctionsUpdate;
    /**
     * Storage ID to apply the updates to.
     */
    private final String storageId;

    /**
     * Drop existing model and create a new one (no merge).
     */
    private final UpsertType upsertType;

    /**
     * Constructor.
     */
    private UpdateModelRequestContext(UpdateModelRequestContextBuilder b) {
        super();
        this.entityUpdate = b.entityUpdate;
        this.lookupEntityUpdate = b.lookupEntityUpdate;
        this.nestedEntityUpdate = b.nestedEntityUpdate;
        this.enumerationsUpdate = b.enumerationsUpdate;
        this.sourceSystemsUpdate = b.sourceSystemsUpdate;
        this.relationsUpdate = b.relationsUpdate;
        this.cleanseFunctionsUpdate = b.cleanseFunctionsUpdate;
        this.storageId = b.storageId;
        this.upsertType = b.upsertType;
        this.entitiesGroupsUpdate = b.entitiesGroupsUpdate;
    }

    /**
     * @return the entityUpdate
     */
    public List<EntityDef> getEntityUpdate() {
        return entityUpdate;
    }

    /**
     * @return the lookupEntityUpdate
     */
    public List<LookupEntityDef> getLookupEntityUpdate() {
        return lookupEntityUpdate;
    }

    /**
     * @return the nestedEntityUpdate
     */
    public List<NestedEntityDef> getNestedEntityUpdate() {
        return nestedEntityUpdate;
    }

    /**
     * @return the enumerationsUpdate
     */
    public List<EnumerationDataType> getEnumerationsUpdate() {
        return enumerationsUpdate;
    }

    /**
     * @return the sourceSystemsUpdate
     */
    public List<SourceSystemDef> getSourceSystemsUpdate() {
        return sourceSystemsUpdate;
    }

    /**
     * @return the relationsUpdate
     */
    public List<RelationDef> getRelationsUpdate() {
        return relationsUpdate;
    }

    /**
     * @return entitiesGroupsUpdate
     */
    public EntitiesGroupDef getEntitiesGroupsUpdate() {
        return entitiesGroupsUpdate;
    }

    /**
     * @return the cleanseFunctionsUpdate
     */
    public CleanseFunctionGroupDef getCleanseFunctionsUpdate() {
        return cleanseFunctionsUpdate;
    }

    /**
     * @return the storageId
     */
    @Override
    public String getStorageId() {
        return storageId;
    }

    /**
     * @return flag responsible for cleaning current DB and cache state
     */
    public UpsertType getUpsertType() {
        return upsertType;
    }

    /**
     * Has entity update.
     *
     * @return true if so false otherwise
     */
    public boolean hasEntityUpdate() {
        return entityUpdate != null && !entityUpdate.isEmpty();
    }

    /**
     * Has lookup entity update.
     *
     * @return true if so false otherwise
     */
    public boolean hasLookupEntityUpdate() {
        return lookupEntityUpdate != null && !lookupEntityUpdate.isEmpty();
    }

    /**
     * Has nested entity update.
     *
     * @return true if so false otherwise
     */
    public boolean hasNestedEntityUpdate() {
        return nestedEntityUpdate != null && !nestedEntityUpdate.isEmpty();
    }

    /**
     * Has enumeration update.
     *
     * @return true if so false otherwise
     */
    public boolean hasEnumerationUpdate() {
        return enumerationsUpdate != null && !enumerationsUpdate.isEmpty();
    }

    /**
     * Has source systems update.
     *
     * @return true if so false otherwise
     */
    public boolean hasSourceSystemsUpdate() {
        return sourceSystemsUpdate != null && !sourceSystemsUpdate.isEmpty();
    }

    /**
     * Has relations update.
     *
     * @return true if so false otherwise
     */
    public boolean hasRelationsUpdate() {
        return relationsUpdate != null && !relationsUpdate.isEmpty();
    }

    public boolean hasEntitiesGroupUpdate() {
        return entitiesGroupsUpdate != null;
    }

    /**
     * Has cleanse functions update.
     *
     * @return true if so false otherwise
     */
    public boolean hasCleanseFunctionsUpdate() {
        return cleanseFunctionsUpdate != null;
    }

    /**
     * @param predicate - filtering condition
     * @return collection of @link{com.unidata.mdm.meta.SimpleAttributeDef}.
     */
    public Collection<SimpleAttributeDef> getAttributes(Predicate<? super SimpleAttributeDef> predicate) {
        Collection<SimpleAttributeDef> attrsFromLookupEntities = this.getLookupEntityUpdate().stream()
                .map(LookupEntityDef::getSimpleAttribute)
                .flatMap(Collection::stream)
                .filter(predicate).collect(toList());
        Collection<SimpleAttributeDef> attrsFromEntities = this.getEntityUpdate().stream()
                .map(EntityDef::getSimpleAttribute)
                .flatMap(Collection::stream)
                .filter(predicate).collect(Collectors.toList());
        Collection<SimpleAttributeDef> attrsFromNestedEntities = this.getNestedEntityUpdate().stream()
                .map(NestedEntityDef::getSimpleAttribute)
                .flatMap(Collection::stream)
                .filter(predicate).collect(Collectors.toList());
        Collection<SimpleAttributeDef> allAttr = new ArrayList<>(attrsFromEntities.size() + attrsFromLookupEntities.size() + attrsFromNestedEntities.size());
        allAttr.addAll(attrsFromLookupEntities);
        allAttr.addAll(attrsFromEntities);
        allAttr.addAll(attrsFromNestedEntities);
        return allAttr;
    }

    /**
     * @return return names of all included top level model element names
     */
    public Collection<String> getAllTopModelElementNames() {
        Collection<String> allNames = new ArrayList<>();
        if (hasEntitiesGroupUpdate()) {
            allNames.add(getEntitiesGroupsUpdate().getGroupName());
        }
        if (hasCleanseFunctionsUpdate()) {
            allNames.add(getCleanseFunctionsUpdate().getGroupName());
        }
        getNestedEntityUpdate().stream().map(NestedEntityDef::getName).collect(toCollection(() -> allNames));
        getEntityUpdate().stream().map(EntityDef::getName).collect(toCollection(() -> allNames));
        getLookupEntityUpdate().stream().map(LookupEntityDef::getName).collect(toCollection(() -> allNames));
        getRelationsUpdate().stream().map(RelationDef::getName).collect(toCollection(() -> allNames));
        getSourceSystemsUpdate().stream().map(SourceSystemDef::getName).collect(toCollection(() -> allNames));
        return allNames;
    }

    public enum UpsertType {
        /**
         * One element update
         */
        PARTIAL_UPDATE,
        /**
         * Model will be recreated
         */
        FULLY_NEW ,
        /**
         * Current model and existed will be merged
         */
        ADDITION
    }

    /**
     * @author Mikhail Mikhailov
     *         Request context builder.
     */
    public static class UpdateModelRequestContextBuilder {
        /**
         * Top level entity updates
         */
        private List<EntityDef> entityUpdate = Collections.emptyList();
        /**
         * Lookup entity updates.
         */
        private List<LookupEntityDef> lookupEntityUpdate = Collections.emptyList();
        /**
         * Nested entity updates.
         */
        private List<NestedEntityDef> nestedEntityUpdate = Collections.emptyList();
        /**
         * Enumeration updates.
         */
        private List<EnumerationDataType> enumerationsUpdate = Collections.emptyList();
        /**
         * Source systems updates.
         */
        private List<SourceSystemDef> sourceSystemsUpdate = Collections.emptyList();
        /**
         * Relations updates.
         */
        private List<RelationDef> relationsUpdate = Collections.emptyList();
        /**
         * Entities group updates
         */
        private EntitiesGroupDef entitiesGroupsUpdate = null;
        /**
         * Cleanse functions.
         */
        private CleanseFunctionGroupDef cleanseFunctionsUpdate;
        /**
         * Storage ID to apply the updates to.
         */
        private String storageId;

        private UpsertType upsertType = UpsertType.PARTIAL_UPDATE;

        /**
         * Constructor.
         */
        public UpdateModelRequestContextBuilder() {
            super();
        }

        /**
         * Sets entity update.
         *
         * @param entityUpdate the update
         * @return self
         */
        public UpdateModelRequestContextBuilder entityUpdate(List<EntityDef> entityUpdate) {
            this.entityUpdate = entityUpdate;
            return this;
        }

        /**
         * Sets lookup entity update.
         *
         * @param lookupEntityUpdate the update
         * @return self
         */
        public UpdateModelRequestContextBuilder lookupEntityUpdate(List<LookupEntityDef> lookupEntityUpdate) {
            this.lookupEntityUpdate = lookupEntityUpdate;
            return this;
        }

        /**
         * Sets nested entity update.
         *
         * @param nestedEntityUpdate the update
         * @return self
         */
        public UpdateModelRequestContextBuilder nestedEntityUpdate(List<NestedEntityDef> nestedEntityUpdate) {
            this.nestedEntityUpdate = nestedEntityUpdate;
            return this;
        }

        /**
         * Sets enumeration update.
         *
         * @param enumerationsUpdate the update
         * @return self
         */
        public UpdateModelRequestContextBuilder enumerationsUpdate(List<EnumerationDataType> enumerationsUpdate) {
            this.enumerationsUpdate = enumerationsUpdate;
            return this;
        }

        /**
         * Sets source systems update.
         *
         * @param sourceSystemsUpdate the update
         * @return self
         */
        public UpdateModelRequestContextBuilder sourceSystemsUpdate(List<SourceSystemDef> sourceSystemsUpdate) {
            this.sourceSystemsUpdate = sourceSystemsUpdate;
            return this;
        }

        /**
         * Sets relations update.
         *
         * @param relationsUpdate the update
         * @return self
         */
        public UpdateModelRequestContextBuilder relationsUpdate(List<RelationDef> relationsUpdate) {
            this.relationsUpdate = relationsUpdate;
            return this;
        }

        /**
         * Sets cleanse functions update.
         *
         * @param cleanseFunctionsUpdate the update
         * @return self
         */
        public UpdateModelRequestContextBuilder cleanseFunctionsUpdate(CleanseFunctionGroupDef cleanseFunctionsUpdate) {
            this.cleanseFunctionsUpdate = cleanseFunctionsUpdate;
            return this;
        }

        /**
         * @param entitiesGroupsUpdate
         * @return
         */
        public UpdateModelRequestContextBuilder entitiesGroupsUpdate(EntitiesGroupDef entitiesGroupsUpdate) {
            this.entitiesGroupsUpdate = entitiesGroupsUpdate;
            return this;
        }

        /**
         * Sets storage ID.
         *
         * @param storageId the ID
         * @return self
         */
        public UpdateModelRequestContextBuilder storageId(String storageId) {
            this.storageId = storageId;
            return this;
        }

        public UpdateModelRequestContextBuilder isForceRecreate(UpsertType upsertType) {
            this.upsertType = upsertType;
            return this;
        }

        /**
         * Builder method.
         *
         * @return new context
         */
        public UpdateModelRequestContext build() {
            return new UpdateModelRequestContext(this);
        }
    }
}
