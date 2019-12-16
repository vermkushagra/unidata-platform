package org.unidata.mdm.meta.context;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.unidata.mdm.meta.service.segments.ModelGetStartExecutor;
import org.unidata.mdm.system.context.AbstractCompositeRequestContext;
import org.unidata.mdm.system.type.pipeline.PipelineInput;

/**
 * @author Mikhail Mikhailov on Nov 28, 2019
 */
public class GetModelRequestContext
        extends AbstractCompositeRequestContext
        implements PipelineInput, MayHaveDraft {
    /**
     * GSVUID.
     */
    private static final long serialVersionUID = -6245429588435518790L;
    /**
     * Requested group ids.
     */
    private final List<String> entityGroupIds;
    /**
     * Requested entity ids.
     */
    private final List<String> entityIds;
    /**
     * Requested lookup ids.
     */
    private final List<String> lookupIds;
    /**
     * Requested enumeration ids.
     */
    private final List<String> enumerationIds;
    /**
     * Requested relation ids.
     */
    private final List<String> relationIds;
    /**
     * Requested source system ids.
     */
    private final List<String> sourceSystemIds;
    /**
     * Requested measured value ids.
     */
    private final List<String> measuredValueIds;
    /**
     * Constructor.
     * @param b the builder.
     */
    private GetModelRequestContext(GetModelRequestContextBuilder b) {

        super(b);
        this.entityGroupIds = Objects.isNull(b.entityGroupIds) ? Collections.emptyList() : b.entityGroupIds;
        this.entityIds = Objects.isNull(b.entityIds) ? Collections.emptyList() : b.entityIds;
        this.enumerationIds = Objects.isNull(b.enumerationIds) ? Collections.emptyList() : b.enumerationIds;
        this.lookupIds = Objects.isNull(b.lookupIds) ? Collections.emptyList() : b.lookupIds;
        this.measuredValueIds = Objects.isNull(b.measuredValueIds) ? Collections.emptyList() : b.measuredValueIds;
        this.relationIds = Objects.isNull(b.relationIds) ? Collections.emptyList() : b.relationIds;
        this.sourceSystemIds = Objects.isNull(b.sourceSystemIds) ? Collections.emptyList() : b.sourceSystemIds;

        setFlag(MetaContextFlags.FLAG_DRAFT, b.draft);
        setFlag(MetaContextFlags.FLAG_REDUCED, b.reduced);
        setFlag(MetaContextFlags.FLAG_ADMIN_SOURCE_SYSTEM, b.adminSourceSystem);
        setFlag(MetaContextFlags.FLAG_ALL_ENTITIES, b.allEntities);
        setFlag(MetaContextFlags.FLAG_ALL_ENTITY_GROUPS, b.allEntityGroups);
        setFlag(MetaContextFlags.FLAG_ALL_ENUMERATIONS, b.allEnumerations);
        setFlag(MetaContextFlags.FLAG_ALL_LOOKUPS, b.allLookups);
        setFlag(MetaContextFlags.FLAG_ALL_MEASURED_VALUES, b.allMeasuredValues);
        setFlag(MetaContextFlags.FLAG_ALL_RELATIONS, b.allRelations);
        setFlag(MetaContextFlags.FLAG_ALL_SOURCE_SYSTEMS, b.allSourceSystems);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartTypeId() {
        return ModelGetStartExecutor.SEGMENT_ID;
    }
    /**
     * Is a draft request?
     * @return draft request state
     */
    @Override
    public boolean isDraft() {
        return getFlag(MetaContextFlags.FLAG_DRAFT);
    }
    /**
     * Is a reduced info request?
     * @return reduced request state
     */
    public boolean isReduced() {
        return getFlag(MetaContextFlags.FLAG_REDUCED);
    }
    /**
     * Is admin SS requested?
     * @return request state
     */
    public boolean isAdminSourceSystem() {
        return getFlag(MetaContextFlags.FLAG_ADMIN_SOURCE_SYSTEM);
    }
    /**
     * All entities requested?
     * @return request state
     */
    public boolean isAllEntities() {
        return getFlag(MetaContextFlags.FLAG_ALL_ENTITIES);
    }
    /**
     * All entity groups requested?
     * @return request state
     */
    public boolean isAllEntityGroups() {
        return getFlag(MetaContextFlags.FLAG_ALL_ENTITY_GROUPS);
    }
    /**
     * All enumerations requested?
     * @return request state
     */
    public boolean isAllEnumerations() {
        return getFlag(MetaContextFlags.FLAG_ALL_ENUMERATIONS);
    }
    /**
     * All lookups requested?
     * @return request state
     */
    public boolean isAllLookups() {
        return getFlag(MetaContextFlags.FLAG_ALL_LOOKUPS);
    }
    /**
     * All MV requested?
     * @return request state
     */
    public boolean isAllMeasuredValues() {
        return getFlag(MetaContextFlags.FLAG_ALL_MEASURED_VALUES);
    }
    /**
     * All relations requested?
     * @return request state
     */
    public boolean isAllRelations() {
        return getFlag(MetaContextFlags.FLAG_ALL_RELATIONS);
    }
    /**
     * All source systems requested?
     * @return request state
     */
    public boolean isAllSourceSystems() {
        return getFlag(MetaContextFlags.FLAG_ALL_SOURCE_SYSTEMS);
    }
    /**
     * @return the entityGroupIds
     */
    public List<String> getEntityGroupIds() {
        return entityGroupIds;
    }
    /**
     * @return the entityIds
     */
    public List<String> getEntityIds() {
        return entityIds;
    }
    /**
     * @return the lookupIds
     */
    public List<String> getLookupIds() {
        return lookupIds;
    }
    /**
     * @return the enumerationIds
     */
    public List<String> getEnumerationIds() {
        return enumerationIds;
    }
    /**
     * @return the relationIds
     */
    public List<String> getRelationIds() {
        return relationIds;
    }
    /**
     * @return the sourceSystemIds
     */
    public List<String> getSourceSystemIds() {
        return sourceSystemIds;
    }
    /**
     * @return the measuredValueIds
     */
    public List<String> getMeasuredValueIds() {
        return measuredValueIds;
    }
    /**
     * Gets a builder instance.
     * @return builder instance
     */
    public static GetModelRequestContextBuilder builder() {
        return new GetModelRequestContextBuilder();
    }
    /**
     * The builder for this context.
     * @author Mikhail Mikhailov on Nov 28, 2019
     */
    public static class GetModelRequestContextBuilder extends AbstractCompositeRequestContextBuilder<GetModelRequestContextBuilder> {
        /**
         * Draft.
         */
        private boolean draft;
        /**
         * Gather reduced set of information.
         */
        private boolean reduced;
        /**
         * Requested group ids.
         */
        private List<String> entityGroupIds;
        /**
         * All groups requested.
         */
        private boolean allEntityGroups;
        /**
         * Requested entity ids.
         */
        private List<String> entityIds;
        /**
         * All entities requested.
         */
        private boolean allEntities;
        /**
         * Requested lookup ids.
         */
        private List<String> lookupIds;
        /**
         * All lookups requested.
         */
        private boolean allLookups;
        /**
         * Requested enumeration ids.
         */
        private List<String> enumerationIds;
        /**
         * All enumerations requested.
         */
        private boolean allEnumerations;
        /**
         * Requested relation ids.
         */
        private List<String> relationIds;
        /**
         * All relations requested.
         */
        private boolean allRelations;
        /**
         * Requested source system ids.
         */
        private List<String> sourceSystemIds;
        /**
         * All source systems requested.
         */
        private boolean allSourceSystems;
        /**
         * Admin SS requested.
         */
        private boolean adminSourceSystem;
        /**
         * Requested measured value ids.
         */
        private List<String> measuredValueIds;
        /**
         * All measured values requested.
         */
        private boolean allMeasuredValues;
        /**
         * Constructor.
         */
        protected GetModelRequestContextBuilder() {
            super();
        }
        /**
         * @param draft the draft to set
         */
        public GetModelRequestContextBuilder draft(boolean draft) {
            this.draft = draft;
            return self();
        }
        /**
         * @param reduced the reduced to set
         */
        public GetModelRequestContextBuilder reduced(boolean reduced) {
            this.reduced = reduced;
            return self();
        }
        /**
         * @param entityGroupIds the entityGroupIds to set
         */
        public GetModelRequestContextBuilder entityGroupIds(List<String> entityGroupIds) {
            this.entityGroupIds = entityGroupIds;
            return self();
        }
        /**
         * @param allEntityGroups the allEntityGroups to set
         */
        public GetModelRequestContextBuilder allEntityGroups(boolean allEntityGroups) {
            this.allEntityGroups = allEntityGroups;
            return self();
        }
        /**
         * @param entityIds the entityIds to set
         */
        public GetModelRequestContextBuilder entityIds(List<String> entityIds) {
            this.entityIds = entityIds;
            return self();
        }
        /**
         * @param allEntities the allEntities to set
         */
        public GetModelRequestContextBuilder allEntities(boolean allEntities) {
            this.allEntities = allEntities;
            return self();
        }
        /**
         * @param lookupIds the lookupIds to set
         */
        public GetModelRequestContextBuilder lookupIds(List<String> lookupIds) {
            this.lookupIds = lookupIds;
            return self();
        }
        /**
         * @param allLookups the allLookups to set
         */
        public GetModelRequestContextBuilder allLookups(boolean allLookups) {
            this.allLookups = allLookups;
            return self();
        }
        /**
         * @param enumerationIds the enumerationIds to set
         */
        public GetModelRequestContextBuilder enumerationIds(List<String> enumerationIds) {
            this.enumerationIds = enumerationIds;
            return self();
        }
        /**
         * @param allEnumerations the allEnumerations to set
         */
        public GetModelRequestContextBuilder allEnumerations(boolean allEnumerations) {
            this.allEnumerations = allEnumerations;
            return self();
        }
        /**
         * @param relationIds the relationIds to set
         */
        public GetModelRequestContextBuilder relationIds(List<String> relationIds) {
            this.relationIds = relationIds;
            return self();
        }
        /**
         * @param allRelations the allRelations to set
         */
        public GetModelRequestContextBuilder allRelations(boolean allRelations) {
            this.allRelations = allRelations;
            return self();
        }
        /**
         * @param sourceSystemIds the sourceSystemIds to set
         */
        public GetModelRequestContextBuilder sourceSystemIds(List<String> sourceSystemIds) {
            this.sourceSystemIds = sourceSystemIds;
            return self();
        }
        /**
         * @param allSourceSystems the allSourceSystems to set
         */
        public GetModelRequestContextBuilder allSourceSystems(boolean allSourceSystems) {
            this.allSourceSystems = allSourceSystems;
            return self();
        }
        /**
         * @param adminSourceSystem the adminSourceSystem to set
         */
        public GetModelRequestContextBuilder adminSourceSystem(boolean adminSourceSystem) {
            this.adminSourceSystem = adminSourceSystem;
            return self();
        }
        /**
         * @param measuredValueIds the measuredValueIds to set
         */
        public GetModelRequestContextBuilder measuredValueIds(List<String> measuredValueIds) {
            this.measuredValueIds = measuredValueIds;
            return self();
        }
        /**
         * @param allMeasuredValues the allMeasuredValues to set
         */
        public GetModelRequestContextBuilder allMeasuredValues(boolean allMeasuredValues) {
            this.allMeasuredValues = allMeasuredValues;
            return self();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public GetModelRequestContext build() {
            return new GetModelRequestContext(this);
        }


    }
}
