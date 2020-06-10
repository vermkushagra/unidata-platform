package com.unidata.mdm.backend.common.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import com.unidata.mdm.backend.common.matching.ClusterSet;
import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.common.search.id.ClassifierIndexId;
import com.unidata.mdm.backend.common.search.id.ManagedIndexId;
import com.unidata.mdm.backend.common.search.id.RecordIndexId;
import com.unidata.mdm.backend.common.search.id.RelationIndexId;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.EtalonClassifier;
import com.unidata.mdm.backend.common.types.EtalonClassifierInfoSection;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRecordInfoSection;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.common.types.EtalonRelationInfoSection;

/**
 * @author Mikhail Mikhailov
 * Indexing request.
 */
public class IndexRequestContext extends CommonRequestContext implements SearchContext {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -7544779812424219448L;
    /**
     * The storage id to use. Overrides the system one.
     */
    private final String storageId;
    /**
     * Entity name (name of the index).
     */
    private final String entity;
    /**
     * Routing (usually etalon id).
     */
    private final String routing;
    /**
     * Drop and recreate or update.
     */
    private final boolean drop;
    /**
     * Refresh result of indexing or not.
     */
    private final boolean refresh;
    /**
     * Ids to delete.
     */
    private final Map<EntitySearchType, List<? extends ManagedIndexId>> idsToDelete;
    /**
     * Record records.
     * TODO replace this with plain EtalonRecord collection. Pull all the innfo needed from info section.
     */
    private final Map<EtalonRecord, Map<? extends SearchField, Object>> records;
    /**
     * Classifier updates.
     */
    private final Collection<EtalonClassifier> classifiers;
    /**
     * Relations
     */
    private final Collection<EtalonRelation> relations;
    /**
     * Matching clusters.
     */
    private final Map<EtalonRecord, ClusterSet> clusters;

    /**
     * Data quality errors.
     */
    private final Map<EtalonRecord, List<DataQualityError>> dqErrors = new IdentityHashMap<>();

    /**
     * Record to sys updates (header fields).
     */
    private final List<EtalonRecordInfoSection> recordsToSysUpdate;
    /**
     * Record to sys updates (header fields).
     */
    private final List<EtalonRecordInfoSection> recordsToQueryDelete;
    /**
     * Matching to query delete (header fields).
     */
    private final List<EtalonRecordInfoSection> matchingToQueryDelete;
    /**
     * Classifiers sys update data (header fields).
     */
    private final List<EtalonClassifierInfoSection> classifiersToQueryDelete;
    /**
     * Relation period ids (tss) to sys update (header fields).
     */
    private final List<EtalonRelationInfoSection> relationsToQueryDelete;
    /**
     * Constructor.
     */
    private IndexRequestContext(IndexRequestContextBuilder b) {

        super();
        this.storageId = b.storageId;
        this.entity = b.entity;
        this.routing = b.routing;
        this.drop = b.drop;
        this.refresh = b.refresh;
        this.idsToDelete = b.idsToDelete;
        this.recordsToSysUpdate = b.recordsToSysUpdate;
        this.recordsToQueryDelete = b.recordsToQueryDelete;
        this.matchingToQueryDelete = b.matchingToQueryDelete;
        this.classifiersToQueryDelete = b.classifiersToQueryDelete;
        this.relationsToQueryDelete = b.relationsToQueryDelete;
        this.records = b.records;
        this.classifiers = b.classifiers;
        this.relations = b.relations;
        this.clusters = b.clusters;
        if (MapUtils.isNotEmpty(b.dqErrors)) {
            this.dqErrors.clear();
            this.dqErrors.putAll(b.dqErrors);
        }
    }

    /**
     * @return the storageId
     */
    @Override
    public String getStorageId() {
        return storageId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntity() {
        return entity;
    }

    /**
     * @return the routing
     */
    public String getRouting() {
        return routing;
    }

    /**
     *
     * @return relations
     */
    public Collection<EtalonRelation> getRelations() {
        return relations == null ? Collections.emptyList() : relations;
    }

    /**
     * @return the drop
     */
    public boolean isDrop() {
        return drop;
    }

    /**
     * @return the refresh
     */
    public boolean isRefresh() {
        return refresh;
    }
    /**
     * Gets delete ids grouped by entity name.
     * @return
     */
    public Map<String, List<ManagedIndexId>> getDeleteIdsGroupedByEntityName() {

        if (MapUtils.isEmpty(idsToDelete)) {
            return Collections.emptyMap();
        }

        return idsToDelete.entrySet().stream()
            .filter(entry -> CollectionUtils.isNotEmpty(entry.getValue()))
            .flatMap(entry -> entry.getValue().stream())
            .collect(Collectors.groupingBy(ManagedIndexId::getEntityName));
    }

    /**
     * @return the oldRecordIds
     */
    @SuppressWarnings("unchecked")
    public List<RecordIndexId> getRecordsToDelete() {
        return CollectionUtils.isEmpty(idsToDelete.get(EntitySearchType.ETALON_DATA))
                ? Collections.emptyList()
                : (List<RecordIndexId>) idsToDelete.get(EntitySearchType.ETALON_DATA);
    }

    /**
     * @return the classifiersToDelete
     */
    @SuppressWarnings("unchecked")
    public List<ClassifierIndexId> getClassifiersToDelete() {
        return CollectionUtils.isEmpty(idsToDelete.get(EntitySearchType.CLASSIFIER))
                ? Collections.emptyList()
                : (List<ClassifierIndexId>) idsToDelete.get(EntitySearchType.CLASSIFIER);
    }

    /**
     * @return the oldRelationIds
     */
    @SuppressWarnings("unchecked")
    public List<RelationIndexId> getRelationsToDelete() {
        return CollectionUtils.isEmpty(idsToDelete.get(EntitySearchType.ETALON_RELATION))
                ? Collections.emptyList()
                : (List<RelationIndexId>) idsToDelete.get(EntitySearchType.ETALON_RELATION);
    }

    /**
     * @return the recordsToSysUpdate
     */
    public List<EtalonRecordInfoSection> getRecordsToSysUpdate() {
        return recordsToSysUpdate;
    }

    /**
     * @return the recordsToSysDelete
     */
    public List<EtalonRecordInfoSection> getRecordsToQueryDelete() {
        return recordsToQueryDelete == null ? Collections.emptyList() : recordsToQueryDelete;
    }

    /**
     * @return the matchingToSysDelete
     */
    public List<EtalonRecordInfoSection> getMatchingToQueryDelete() {
        return matchingToQueryDelete == null ? Collections.emptyList() : matchingToQueryDelete;
    }
    /**
     * @return the classifiersToSysUpdate
     */
    public List<EtalonClassifierInfoSection> getClassifiersToQueryDelete() {
        return classifiersToQueryDelete == null ? Collections.emptyList() : classifiersToQueryDelete;
    }

    /**
     * @return the relationsToSysUpdate
     */
    public List<EtalonRelationInfoSection> getRelationsToQueryDelete() {
        return relationsToQueryDelete == null ? Collections.emptyList() : relationsToQueryDelete;
    }

    /**
     * @return the records
     */
    public Map<EtalonRecord, Map<? extends SearchField, Object>> getRecords() {
        return records == null ? Collections.emptyMap() : records;
    }

    /**
     * Gets all clustes.
     * @return clusters map
     */
    public Map<EtalonRecord, ClusterSet> getClusters() {
        return clusters == null ? Collections.emptyMap() : clusters;
    }

    /**
     * Gets clusters for a particular period.
     * @param record period record
     * @return clusters
     */
    public ClusterSet getClusters(EtalonRecord record) {
        return getClusters().get(record);
    }

    /**
     * @return the classifiers
     */
    public Collection<EtalonClassifier> getClassifiers() {
        return classifiers == null ? Collections.emptyList() : classifiers;
    }

    public Map<EtalonRecord, List<DataQualityError>> getDqErrors() {
        return Collections.unmodifiableMap(dqErrors);
    }

    /**
     * Builder object.
     * @return builder
     */
    public static IndexRequestContextBuilder builder() {
        return new IndexRequestContextBuilder();
    }

    public static IndexRequestContextBuilder builder(IndexRequestContext idx) {
        return new IndexRequestContextBuilder(idx);
    }

    /**
     * Context builder.
     * @author Mikhail Mikhailov
     */
    public static class IndexRequestContextBuilder {
        /**
         * The storage id to use. Overrides the system one.
         */
        private String storageId;
        /**
         * Type to operate on.
         */
        private String entity;
        /**
         * Routing (usually etalon id).
         */
        private String routing;
        /**
         * Drop and recreate or update.
         */
        private boolean drop;
        /**
         * Refresh result of indexing or not.
         */
        private boolean refresh = true;
        /**
         * Record to sys updates (header fields).
         */
        private List<EtalonRecordInfoSection> recordsToSysUpdate;
        /**
         * Record to sys updates (header fields).
         */
        private List<EtalonRecordInfoSection> recordsToQueryDelete;
        /**
         * Record to sys delete (header fields).
         */
        private List<EtalonRecordInfoSection> matchingToQueryDelete;
        /**
         * Classifiers sys update data (header fields).
         */
        private List<EtalonClassifierInfoSection> classifiersToQueryDelete;
        /**
         * Relation period ids (tss) to sys update (header fields).
         */
        private List<EtalonRelationInfoSection> relationsToQueryDelete;
        /**
         * Record records.
         */
        private Map<EtalonRecord, Map<? extends SearchField, Object>> records = Collections.emptyMap();
        /**
         * Classifier updates.
         */
        private Collection<EtalonClassifier> classifiers = Collections.emptyList();
        /**
         * Relations
         */
        private Collection<EtalonRelation> relations;
        /**
         * Matching clusters.
         */
        private Map<EtalonRecord, ClusterSet> clusters;
        /**
         * Data quality errors.
         */
        private final Map<EtalonRecord, List<DataQualityError>> dqErrors = new IdentityHashMap<>();
        /**
         * Ids to delete.
         */
        private Map<EntitySearchType, List<? extends ManagedIndexId>> idsToDelete = new EnumMap<>(EntitySearchType.class);
        /**
         * Constructor.
         */
        private IndexRequestContextBuilder() {
            super();
        }

        public IndexRequestContextBuilder(final IndexRequestContext idx) {
            super();
            storageId = idx.storageId;
            drop = idx.drop;
            refresh = idx.refresh;
            records = idx.records;
            classifiers = idx.classifiers;
            relations = idx.relations;
            idsToDelete = idx.idsToDelete;
            recordsToQueryDelete = idx.recordsToQueryDelete;
            relationsToQueryDelete = idx.relationsToQueryDelete;
            classifiersToQueryDelete = idx.classifiersToQueryDelete;
            entity = idx.entity;
            routing = idx.routing;

        }

        /**
         * Overrides default storage id.
         * @param storageId the storage id to use
         * @return self
         */
        public IndexRequestContextBuilder storageId(String storageId) {
            this.storageId = storageId;
            return this;
        }
        /**
         * @param entityName - entity name
         * @return self
         */
        public IndexRequestContextBuilder entity(String entityName){
            this.entity = entityName;
            return this;
        }
        /**
         * @param routing - routing
         * @return self
         */
        public IndexRequestContextBuilder routing(String routing){
            this.routing = routing;
            return this;
        }
        /**
         * Drop and recreate or update.
         * @param drop drop or update
         * @return self
         */
        public IndexRequestContextBuilder drop(boolean drop) {
            this.drop = drop;
            return this;
        }

        /**
         * Drop and recreate or update.
         * @param refresh drop or update
         * @return self
         */
        public IndexRequestContextBuilder refresh(boolean refresh) {
            this.refresh = refresh;
            return this;
        }
        /**
         * @param records the records to set
         */
        public IndexRequestContextBuilder records(Map<EtalonRecord, Map<? extends SearchField, Object>> records) {
            this.records = records;
            return this;
        }
        /**
         * Sets records to delete.
         * @param ids id to delete
         * @return self
         */
        @SuppressWarnings("unchecked")
        public IndexRequestContextBuilder recordsToDelete(List<RecordIndexId> ids) {

            if (CollectionUtils.isNotEmpty(ids)) {
                ((List<RecordIndexId>) idsToDelete
                    .computeIfAbsent(EntitySearchType.ETALON_DATA, key -> new ArrayList<RecordIndexId>()))
                    .addAll(ids);
            }

            return this;
        }
        /**
         * Sets record to delete.
         * @param id id to delete
         * @return self
         */
        public IndexRequestContextBuilder recordToDelete(RecordIndexId id) {

            if (Objects.isNull(id)) {
                return this;
            }

            return recordsToDelete(Collections.singletonList(id));
        }
        /**
         * @param recordsToQueryDelete the record sys update
         */
        public IndexRequestContextBuilder recordsToQueryDelete(List<EtalonRecordInfoSection> recordsToQueryDelete) {

            if (CollectionUtils.isEmpty(recordsToQueryDelete)) {
                return this;
            }

            if (Objects.isNull(this.recordsToQueryDelete)) {
                this.recordsToQueryDelete = new ArrayList<>(recordsToQueryDelete);
            } else {
                this.recordsToQueryDelete.addAll(recordsToQueryDelete);
            }

            return this;
        }
        /**
         * @param matchingToQueryDelete the record sys update
         */
        public IndexRequestContextBuilder matchingToQueryDelete(List<EtalonRecordInfoSection> matchingToQueryDelete) {

            if (CollectionUtils.isEmpty(matchingToQueryDelete)) {
                return this;
            }

            if (Objects.isNull(this.matchingToQueryDelete)) {
                this.matchingToQueryDelete = new ArrayList<>(matchingToQueryDelete);
            } else {
                this.matchingToQueryDelete.addAll(matchingToQueryDelete);
            }

            return this;
        }
        /**
         * @param recordToQueryDelete the record sys update
         */
        public IndexRequestContextBuilder recordToQueryDelete(EtalonRecordInfoSection recordToQueryDelete) {
            return recordsToQueryDelete(Collections.singletonList(recordToQueryDelete));
        }
        /**
         * @param recordsToSysUpdate the record sys update
         */
        public IndexRequestContextBuilder recordsToSysUpdate(List<EtalonRecordInfoSection> recordsToSysUpdate) {

            if (CollectionUtils.isEmpty(recordsToSysUpdate)) {
                return this;
            }

            if (Objects.isNull(this.recordsToSysUpdate)) {
                this.recordsToSysUpdate = new ArrayList<>(recordsToSysUpdate);
            } else {
                this.recordsToSysUpdate.addAll(recordsToSysUpdate);
            }

            return this;
        }
        /**
         * @param recordToSysUpdate the record sys update
         */
        public IndexRequestContextBuilder recordToSysUpdate(EtalonRecordInfoSection recordToSysUpdate) {
            return recordsToSysUpdate(Collections.singletonList(recordToSysUpdate));
        }
        /**
         * Sets classifier to delete.
         * @param ids id to delete
         * @return self
         */
        @SuppressWarnings("unchecked")
        public IndexRequestContextBuilder classifiersToDelete(List<ClassifierIndexId> ids) {

            if (CollectionUtils.isNotEmpty(ids)) {
                ((List<ClassifierIndexId>) idsToDelete
                    .computeIfAbsent(EntitySearchType.CLASSIFIER, key -> new ArrayList<ClassifierIndexId>()))
                    .addAll(ids);
            }

            return this;
        }
        /**
         * Sets classifier to delete.
         * @param id id to delete
         * @return self
         */
        public IndexRequestContextBuilder classifierToDelete(ClassifierIndexId id) {

            if (Objects.isNull(id)) {
                return this;
            }

            return classifiersToDelete(Collections.singletonList(id));
        }
        /**
         * Sets classifiers to sys update.
         * @param classifiersToQueryDelete
         * @return self
         */
        public IndexRequestContextBuilder classifiersToQueryDelete(List<EtalonClassifierInfoSection> classifiersToQueryDelete) {

            if (CollectionUtils.isEmpty(classifiersToQueryDelete)) {
                return this;
            }

            if (Objects.isNull(this.classifiersToQueryDelete)) {
                this.classifiersToQueryDelete = new ArrayList<>(classifiersToQueryDelete);
            } else {
                this.classifiersToQueryDelete.addAll(classifiersToQueryDelete);
            }

            return this;
        }
        /**
         * Sets classifiers to sys update.
         * @param classifierToQueryDelete
         * @return self
         */
        public IndexRequestContextBuilder classifierToQueryDelete(EtalonClassifierInfoSection classifierToQueryDelete) {
            return classifiersToQueryDelete(Collections.singletonList(classifierToQueryDelete));
        }
        /**
         * @param relationsToQueryDelete the record to sys update
         */
        public IndexRequestContextBuilder relationsToQueryDelete(List<EtalonRelationInfoSection> relationsToQueryDelete) {

            if (CollectionUtils.isEmpty(relationsToQueryDelete)) {
                return this;
            }

            if (Objects.isNull(this.relationsToQueryDelete)) {
                this.relationsToQueryDelete = new ArrayList<>(relationsToQueryDelete);
            } else {
                this.relationsToQueryDelete.addAll(relationsToQueryDelete);
            }

            return this;
        }
        /**
         * Sets relation to sys update.
         * @param relationQueryDelete
         * @return self
         */
        public IndexRequestContextBuilder relationToQueryDelete(EtalonRelationInfoSection relationQueryDelete) {
            return relationsToQueryDelete(Collections.singletonList(relationQueryDelete));
        }
        /**
         * Sets relations to delete.
         * @param ids id to delete
         * @return self
         */
        @SuppressWarnings("unchecked")
        public IndexRequestContextBuilder relationsToDelete(List<RelationIndexId> ids) {

            if (CollectionUtils.isNotEmpty(ids)) {
                ((List<RelationIndexId>) idsToDelete
                    .computeIfAbsent(EntitySearchType.ETALON_RELATION, key -> new ArrayList<RelationIndexId>()))
                    .addAll(ids);
            }

            return this;
        }
        /**
         * Sets relation to delete.
         * @param id id to delete
         * @return self
         */
        public IndexRequestContextBuilder relationToDelete(RelationIndexId id) {

            if (Objects.isNull(id)) {
                return this;
            }

            return relationsToDelete(Collections.singletonList(id));
        }
        /**
         * @param classifiers the classifiers to set
         */
        public IndexRequestContextBuilder classifiers(Collection<EtalonClassifier> classifiers) {
            this.classifiers = classifiers;
            return this;
        }
        /**
         * @param relations relations
         * @return self
         */
        public IndexRequestContextBuilder relations(Collection<EtalonRelation> relations) {

            if (Objects.isNull(this.relations)) {
                this.relations = new ArrayList<>();
            }

            this.relations.addAll(relations);
            return this;
        }
        /**
         * @param clusters matching clusters
         * @return self
         */
        public IndexRequestContextBuilder clusters(Map<EtalonRecord, ClusterSet> clusters) {
            this.clusters = clusters;
            return this;
        }
        /**
         * @param clusters matching clusters
         * @return self
         */
        public IndexRequestContextBuilder clusters(EtalonRecord record, ClusterSet clusters) {

            if (Objects.isNull(this.clusters)) {
                this.clusters = new IdentityHashMap<>();
            }

            this.clusters.put(record, clusters);
            return this;
        }

        public IndexRequestContextBuilder dqErrors(Map<EtalonRecord, List<DataQualityError>> dataQualityErrors) {
            this.dqErrors.clear();
            if (MapUtils.isNotEmpty(dataQualityErrors)) {
                this.dqErrors.putAll(dataQualityErrors);
            }
            return this;
        }
        /**
         * Tells, whether this builder has collected some updates.
         * @return true if has some, false otherwise
         */
        public boolean hasUpdates() {
            return MapUtils.isNotEmpty(this.records)
                || MapUtils.isNotEmpty(this.clusters)
                || MapUtils.isNotEmpty(this.idsToDelete)
                || CollectionUtils.isNotEmpty(this.relations)
                || CollectionUtils.isNotEmpty(this.classifiers);
        }
        /**
         * Builds context.
         * @return context
         */
        public IndexRequestContext build() {
            return new IndexRequestContext(this);
        }
    }
}
