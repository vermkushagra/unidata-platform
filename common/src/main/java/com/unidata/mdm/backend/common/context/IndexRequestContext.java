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

package com.unidata.mdm.backend.common.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.unidata.mdm.backend.common.types.DataQualityError;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import com.unidata.mdm.backend.common.matching.ClusterSet;
import com.unidata.mdm.backend.common.search.SearchField;
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
     * Records period ids to delete.
     */
    private final List<String> recordsToDelete;

    /**
     * Classifier records ids to delete.
     */
    private final List<String> classifiersToDelete;
    /**
     * Relation period ids (tss) to delete.
     */
    private final Map<String, List<String>> relationsToDelete;
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
        this.recordsToDelete = b.recordsToDelete;
        this.classifiersToDelete = b.classifiersToDelete;
        this.relationsToDelete = b.relationsToDelete;
        this.recordsToSysUpdate = b.recordsToSysUpdate;
        this.recordsToQueryDelete = b.recordsToQueryDelete;
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
     * @return the oldRecordIds
     */
    public List<String> getRecordsToDelete() {
        return recordsToDelete == null ? Collections.emptyList() : recordsToDelete;
    }

    /**
     * @return the classifiersToDelete
     */
    public List<String> getClassifiersToDelete() {
        return classifiersToDelete == null ? Collections.emptyList() : classifiersToDelete;
    }

    /**
     * @return the oldRelationIds
     */
    public Map<String, List<String>> getRelationsToDelete() {
        return relationsToDelete == null ? Collections.emptyMap() : relationsToDelete;
    }

    /**
     * @return the recordsToSysUpdate
     */
    public List<EtalonRecordInfoSection> getRecordsToSysUpdate() {
        return recordsToSysUpdate;
    }

    /**
     * @return the recordsToSysUpdate
     */
    public List<EtalonRecordInfoSection> getRecordsToQueryDelete() {
        return recordsToQueryDelete == null ? Collections.emptyList() : recordsToQueryDelete;
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
         * Records period ids in form {etalonId, periodId} to delete.
         */
        private List<String> recordsToDelete;
        /**
         * Classifier records to delete in form {etalonId, classifierName}.
         */
        private List<String> classifiersToDelete;
        /**
         * Relation period ids in form {etalonId, relationName, periodId} to delete.
         */
        private Map<String, List<String>> relationsToDelete;
        /**
         * Record to sys updates (header fields).
         */
        private List<EtalonRecordInfoSection> recordsToSysUpdate;
        /**
         * Record to sys updates (header fields).
         */
        private List<EtalonRecordInfoSection> recordsToQueryDelete;
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

        private IndexRequestContextBuilder() {
            super();
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
         * @param recordsToDelete the old record ids to set
         */
        public IndexRequestContextBuilder recordsToDelete(List<String> recordsToDelete) {

            if (CollectionUtils.isEmpty(recordsToDelete)) {
                return this;
            }

            if (Objects.isNull(this.recordsToDelete)) {
                this.recordsToDelete = new ArrayList<>(recordsToDelete);
            } else {
                this.recordsToDelete.addAll(recordsToDelete);
            }

            return this;
        }
        /**
         * @param recordToDelete the old record id to set
         */
        public IndexRequestContextBuilder recordToDelete(String recordToDelete) {
            return recordsToDelete(Collections.singletonList(recordToDelete));
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
         * @param recordToQueryDelete the record sys update
         */
        public IndexRequestContextBuilder recordToQueryDelete(EtalonRecordInfoSection recordToQueryDelete) {
            return recordsToQueryDelete(Collections.singletonList(recordToQueryDelete));
        }
        /**
         * @param recordsToQueryDelete the record sys update
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
         * @param recordToQueryDelete the record sys update
         */
        public IndexRequestContextBuilder recordToSysUpdate(EtalonRecordInfoSection recordToSysUpdate) {
            return recordsToSysUpdate(Collections.singletonList(recordToSysUpdate));
        }
        /**
         * Sets classifiers to delete.
         * @param classifiersToDelete
         * @return self
         */
        public IndexRequestContextBuilder classifiersToDelete(List<String> classifiersToDelete) {

            if (CollectionUtils.isEmpty(classifiersToDelete)) {
                return this;
            }

            if (Objects.isNull(this.classifiersToDelete)) {
                this.classifiersToDelete = new ArrayList<>(classifiersToDelete);
            } else {
                this.classifiersToDelete.addAll(classifiersToDelete);
            }

            return this;
        }
        /**
         * Sets classifiers to delete.
         * @param classifiersToDelete
         * @return self
         */
        public IndexRequestContextBuilder classifierToDelete(String classifierToDelete) {
            return classifiersToDelete(Collections.singletonList(classifierToDelete));
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
         * @param ids the record to sys update
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
         * @param ids the old record ids to set
         */
        public IndexRequestContextBuilder relationsToDelete(String sideName, List<String> relationsToDelete) {

            if (CollectionUtils.isEmpty(relationsToDelete)) {
                return this;
            }

            if (Objects.isNull(this.relationsToDelete)) {
                this.relationsToDelete = new HashMap<>();
            }

            this.relationsToDelete.computeIfAbsent(sideName, k -> new ArrayList<>(relationsToDelete.size()))
                .addAll(relationsToDelete);

            return this;
        }
        /**
         * Sets relation to delete.
         * @param classifiersToDelete
         * @return self
         */
        public IndexRequestContextBuilder relationToDelete(String sideName, String relationToDelete) {
            return relationsToDelete(sideName, Collections.singletonList(relationToDelete));
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
                || CollectionUtils.isNotEmpty(this.recordsToDelete)
                || CollectionUtils.isNotEmpty(this.relations)
                || MapUtils.isNotEmpty(this.relationsToDelete)
                || CollectionUtils.isNotEmpty(this.classifiers)
                || CollectionUtils.isNotEmpty(this.classifiersToDelete);
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
