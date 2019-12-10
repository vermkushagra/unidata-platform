package org.unidata.mdm.meta.context;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.unidata.mdm.core.context.AbstractCompositeRequestContext;
import org.unidata.mdm.meta.service.segments.ModelDeleteStartExecutor;
import org.unidata.mdm.system.context.PipelineExecutionContext;
import org.unidata.mdm.system.context.StorageSpecificContext;

/**
 * @author Mikhail Mikhailov
 */
public class DeleteModelRequestContext
        extends AbstractCompositeRequestContext
        implements MayHaveDraft, PipelineExecutionContext, StorageSpecificContext, Serializable {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 1636265075513576171L;

    /**
     * Entity IDs to delete..
     */
    private final List<String> entitiesIds;

    /**
     * Nested entities Ids to delete
     */
    private final List<String> nestedEntitiesIds;

    /**
     * Lookup entity IDs to delete..
     */
    private final List<String> lookupEntitiesIds;

    /**
     * Enumeration IDs to delete..
     */
    private final List<String> enumerationIds;

    /**
     * Source system IDs to delete..
     */
    private final List<String> sourceSystemIds;

    /**
     * Relation IDS.
     */
    private final List<String> relationIds;

    /**
     * Storage ID to apply the updates to.
     */
    private final String storageId;

    /**
     * Constructor.
     */
    private DeleteModelRequestContext(DeleteModelRequestContextBuilder b) {
        super(b);
        this.entitiesIds = b.entitiesIds;
        this.lookupEntitiesIds = b.lookupEntitiesIds;
        this.enumerationIds = b.enumerationIds;
        this.sourceSystemIds = b.sourceSystemIds;
        this.relationIds = b.relationIds;
        this.storageId = b.storageId;
        this.nestedEntitiesIds = b.nestedEntitiesIds;

        setFlag(MetaContextFlags.FLAG_DRAFT, b.draft);
    }


    @Override
    public String getStartTypeId() {
        return ModelDeleteStartExecutor.SEGMENT_ID;
    }


    /**
     * @return the entitiesIds
     */
    public List<String> getEntitiesIds() {
        return entitiesIds;
    }

    /**
     * @return the lookupEntitiesIds
     */
    public List<String> getLookupEntitiesIds() {
        return lookupEntitiesIds;
    }

    /**
     * @return the enumerationIds
     */
    public List<String> getEnumerationIds() {
        return enumerationIds;
    }

    /**
     * @return the sourceSystemIds
     */
    public List<String> getSourceSystemIds() {
        return sourceSystemIds;
    }

    /**
     * @return the relationIds
     */
    public List<String> getRelationIds() {
        return relationIds;
    }

    /**
     * @return the nested entities ids
     */
    public List<String> getNestedEntitiesIds() {
        return nestedEntitiesIds;
    }

    /**
     * @return the storageId
     */
    @Override
    public String getStorageId() {
        return storageId;
    }

    /**
     * @return true, if entitiesIds are set
     */
    public boolean hasEntitiesIds() {
        return entitiesIds != null && !entitiesIds.isEmpty();
    }

    /**
     * @return true, if lookupEntitiesIds are set
     */
    public boolean hasLookupEntitiesIds() {
        return lookupEntitiesIds != null && !lookupEntitiesIds.isEmpty();
    }

    /**
     * @return true, if enumerationIds are set
     */
    public boolean hasEnumerationIds() {
        return enumerationIds != null && !enumerationIds.isEmpty();
    }

    /**
     * @return true, if sourceSystemIds are set
     */
    public boolean hasSourceSystemIds() {
        return sourceSystemIds != null && !sourceSystemIds.isEmpty();
    }

    /**
     * @return true, if relationIds are set
     */
    public boolean hasRelationIds() {
        return relationIds != null && !relationIds.isEmpty();
    }

    public boolean hasNestedEntitiesIds() {
        return nestedEntitiesIds != null && !nestedEntitiesIds.isEmpty();
    }

    public boolean isDraft() {
        return getFlag(MetaContextFlags.FLAG_DRAFT);
    }

    /**
     * @author Mikhail Mikhailov
     *         Builder class.
     */
    public static class DeleteModelRequestContextBuilder extends AbstractCompositeRequestContextBuilder<DeleteModelRequestContextBuilder> {

        /**
         * Entity IDs to delete..
         */
        private List<String> entitiesIds = Collections.emptyList();

        /**
         * Lookup entity IDs to delete..
         */
        private List<String> lookupEntitiesIds = Collections.emptyList();

        /**
         * Enumeration IDs to delete..
         */
        private List<String> enumerationIds = Collections.emptyList();

        /**
         * Source system IDs to delete..
         */
        private List<String> sourceSystemIds = Collections.emptyList();

        /**
         * Relation IDs.
         */
        private List<String> relationIds = Collections.emptyList();

        /**
         * Nested entities Ids to delete
         */
        private List<String> nestedEntitiesIds = Collections.emptyList();

        /**
         * Storage ID to apply the updates to.
         */
        private String storageId;
        /**
         * Draft.
         */
        private boolean draft;

        /**
         * Constructor.
         */
        public DeleteModelRequestContextBuilder() {
            super();
        }

        /**
         * Sets entitiesIds.
         *
         * @param entitiesIds the ids to set
         * @return self
         */
        public DeleteModelRequestContextBuilder entitiesIds(List<String> entitiesIds) {
            this.entitiesIds = entitiesIds;
            return this;
        }

        /**
         * Sets lookupEntitiesIds.
         *
         * @param lookupEntitiesIds the ids to set
         * @return self
         */
        public DeleteModelRequestContextBuilder lookupEntitiesIds(List<String> lookupEntitiesIds) {
            this.lookupEntitiesIds = lookupEntitiesIds;
            return this;
        }

        /**
         * Sets enumerationIds.
         *
         * @param enumerationIds the ids to set
         * @return self
         */
        public DeleteModelRequestContextBuilder enumerationIds(List<String> enumerationIds) {
            this.enumerationIds = enumerationIds;
            return this;
        }

        /**
         * Sets sourceSystemIds.
         *
         * @param sourceSystemIds the ids to set
         * @return self
         */
        public DeleteModelRequestContextBuilder sourceSystemIds(List<String> sourceSystemIds) {
            this.sourceSystemIds = sourceSystemIds;
            return this;
        }

        /**
         * @param relationIds
         * @return
         */
        public DeleteModelRequestContextBuilder relationIds(List<String> relationIds) {
            this.relationIds = relationIds;
            return this;
        }

        /**
         * Sets storage ID.
         *
         * @param storageId the ID
         * @return self
         */
        public DeleteModelRequestContextBuilder storageId(String storageId) {
            this.storageId = storageId;
            return this;
        }

        /**
         * @param nestedEntitiesIds the IDs of nested entities
         * @return self
         */
        public DeleteModelRequestContextBuilder nestedEntiesIds(List<String> nestedEntitiesIds) {
            this.nestedEntitiesIds = nestedEntitiesIds;
            return this;
        }

        public DeleteModelRequestContextBuilder draft(boolean draft) {
            this.draft = draft;
            return this;
        }
        /**
         * Builder method.
         *
         * @return context
         */
        @Override
        public DeleteModelRequestContext build() {
            return new DeleteModelRequestContext(this);
        }


    }
}
