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

package com.unidata.mdm.backend.service.data.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.keys.Keys;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.po.EtalonRelationPO;
import com.unidata.mdm.backend.po.OriginRelationPO;
import com.unidata.mdm.backend.po.OriginsVistoryRelationsPO;

/**
 * @author Mikhail Mikhailov
 * Relation batch set accumulator.
 */
public class RelationUpsertBatchSetAccumulator extends AbstractRelationBatchSetAccumulator<UpsertRelationsRequestContext> {
    /**
     * Collected rel. etalons.
     */
    private final List<EtalonRelationPO> etalonInserts;
    /**
     * Collected rel. origin inserts.
     */
    private final List<OriginRelationPO> originInserts;
    /**
     * Containments accumulator.
     */
    private final RecordUpsertBatchSetAccumulator recordBatchSetAccumulator;
    /**
     * RelTo id cache for multiVersion relations.
     */
    private final Map<String, BatchKeyReference<? extends Keys>> relToIdCache;
    /**
     * This chunk is a containment relation
     */
    private final boolean containmentRelation;
    /**
     * Constructor.
     * @param commitSize chunk size
     * @param targets target tables
     * @param idCache the id cache to use
     * @param isContainment whether this accumulator processes a containment relation
     * @param containmentTargets target tables for containment records
     */
    public RelationUpsertBatchSetAccumulator(
            int commitSize, Map<BatchTarget, String> targets, Map<String,
            BatchKeyReference<? extends Keys>> idCache, boolean isContainment, Map<BatchTarget, String> containmentTargets) {

        super(commitSize, targets);
        this.etalonInserts = new ArrayList<>(commitSize);
        this.originInserts = new ArrayList<>(commitSize);

        // Containments and keys cache
        recordBatchSetAccumulator = isContainment ? new RecordUpsertBatchSetAccumulator(commitSize, containmentTargets, idCache) : null;
        relToIdCache = isContainment ? null : idCache;
        containmentRelation = isContainment;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public BatchIterator<UpsertRelationsRequestContext> iterator(BatchSetIterationType iterationType) {
        return new RelationUpsertBatchIterator(iterationType);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void discharge() {
        super.discharge();
        etalonInserts.clear();
        originInserts.clear();

        // Containments
        if (Objects.nonNull(recordBatchSetAccumulator)) {
            recordBatchSetAccumulator.discharge();
        }
    }
    /**
     * @return the collectedEtalonInserts
     */
    public List<EtalonRelationPO> getEtalonInserts() {
        return etalonInserts;
    }
    /**
     * @return the collectedOriginInserts
     */
    public List<OriginRelationPO> getOriginInserts() {
        return originInserts;
    }
    /**
     * @return the recordBatchSetAccumulator
     */
    public RecordUpsertBatchSetAccumulator getRecordBatchSetAccumulator() {
        return recordBatchSetAccumulator;
    }
    /**
     * Extracts cache string from contexts.
     * @param left left rel side context
     * @param right right rel side context
     * @return string or null
     */
    public static String toChacheString(RecordIdentityContext left, RecordIdentityContext right) {

        if (Objects.nonNull(left) && Objects.nonNull(right)) {

            String leftSide = null;
            String rightSide = null;

            if (left.isOriginExternalId()) {
                leftSide = RecordUpsertBatchSetAccumulator.toExternalIdCacheString(left.getExternalId(), left.getEntityName(), left.getSourceSystem());
            } else if (left.isEtalonRecordKey()) {
                leftSide = left.getEtalonKey();
            }

            if (right.isOriginExternalId()) {
                rightSide = RecordUpsertBatchSetAccumulator.toExternalIdCacheString(right.getExternalId(), right.getEntityName(), right.getSourceSystem());
            } else if (left.isEtalonRecordKey()) {
                rightSide = right.getEtalonKey();
            }

            if (StringUtils.isNoneBlank(leftSide, rightSide)) {
                return StringUtils.join(leftSide, "|", rightSide);
            }
        }

        return null;
    }
    /**
     * @author Mikhail Mikhailov
     * Simple batch iterator.
     */
    private class RelationUpsertBatchIterator implements BatchIterator<UpsertRelationsRequestContext> {
        /**
         * The iterator.
         */
        private ListIterator<UpsertRelationsRequestContext> i = workingCopy.listIterator();
        /**
         * Currently processed contex.
         */
        private UpsertRelationsRequestContext current;
        /**
         * Current iteration type.
         */
        private BatchSetIterationType iterationType;
        /**
         * Constructor.
         * @param iterationType the iteration type
         */
        public RelationUpsertBatchIterator(BatchSetIterationType iterationType) {
            super();
            this.iterationType = iterationType;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {

            boolean hasNext = i.hasNext();
            if (!hasNext && current != null) {
                if (iterationType == BatchSetIterationType.UPSERT_ORIGINS) {
                    accumulateOrigin(current);
                } else if (iterationType == BatchSetIterationType.UPSERT_ETALONS) {
                    accumulateEtalon(current);
                }
            }

            return hasNext;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public UpsertRelationsRequestContext next() {

            UpsertRelationsRequestContext next = i.next();
            if (current != null) {
                if (iterationType == BatchSetIterationType.UPSERT_ORIGINS) {
                    accumulateOrigin(current);
                } else if (iterationType == BatchSetIterationType.UPSERT_ETALONS) {
                    accumulateEtalon(current);
                }
            }

            if (iterationType == BatchSetIterationType.UPSERT_ORIGINS) {
                init(next);
            }

            current = next;
            return next;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void remove() {
            i.remove();
            current = null;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public BatchSetIterationType currentIterationType() {
            return iterationType;
        }
        /**
         * Does some preprocessing.
         * @param ctx the upsert context
         */
        private void init(UpsertRelationsRequestContext ctx) {

            for (Entry<String, List<UpsertRelationRequestContext>> entry : ctx.getRelations().entrySet()) {
                for (UpsertRelationRequestContext uCtx : entry.getValue()) {

                    uCtx.putToStorage(StorageId.DATA_BATCH_RELATIONS, new RelationBatchSet(RelationUpsertBatchSetAccumulator.this));
                    if (containmentRelation) {
                        BatchKeyReference<RecordKeys> recordKeys = Objects.nonNull(recordBatchSetAccumulator)
                            ? recordBatchSetAccumulator.findCachedKeys(uCtx)
                            : null;

                        if (Objects.nonNull(recordKeys)) {
                            uCtx.putToStorage(uCtx.keysId(), recordKeys.getKeys());
                        }
                    } else {
                        BatchKeyReference<RelationKeys> relationKeys = findCachedKeys(ctx, uCtx);
                        if (Objects.nonNull(relationKeys)) {
                            uCtx.putToStorage(uCtx.relationKeysId(), relationKeys.getKeys());
                        }
                    }
                }
            }
        }
        /**
         * Accumulates a batch set after origin upsert phase.
         * @param batchSet the set to accumulate
         */
        private void accumulateOrigin(UpsertRelationsRequestContext ctx) {

            for (Entry<String, List<UpsertRelationRequestContext>> entry : ctx.getRelations().entrySet()) {
                for (UpsertRelationRequestContext uCtx : entry.getValue()) {

                    RelationBatchSet batchSet = uCtx.getFromStorage(StorageId.DATA_BATCH_RELATIONS);
                    if (Objects.isNull(batchSet)) {
                        continue;
                    }

                    if (Objects.nonNull(batchSet.getEtalonRelationInsertPO())) {
                        etalonInserts.add(batchSet.getEtalonRelationInsertPO());
                    } else if (Objects.nonNull(batchSet.getEtalonRelationUpdatePO())) {
                        etalonUpdates.add(batchSet.getEtalonRelationUpdatePO());
                    }

                    originInserts.addAll(batchSet.getOriginRelationInsertPOs());
                    originUpdates.addAll(batchSet.getOriginRelationUpdatePOs());

                    BatchKeyReference<RelationKeys> cachedKeys = findCachedKeys(ctx, uCtx);
                    if (Objects.isNull(cachedKeys)) {

                        cachedKeys = new RelationBatchKeyReference(uCtx.relationKeys());

                        if (Objects.nonNull(relToIdCache)) {
                            relToIdCache.put(toChacheString(ctx, uCtx), cachedKeys);
                        }
                    }

                    // COPY support, revision must be known beforehand.
                    int currentRevision = cachedKeys.getRevision();
                    for (OriginsVistoryRelationsPO v : batchSet.getOriginsVistoryRelationsPOs()) {
                        v.setRevision(++currentRevision);
                        vistory.add(v);
                    }

                    if (cachedKeys.getRevision() != currentRevision) {
                        cachedKeys.setRevision(currentRevision);
                    }
                }
            }
        }

        /**
         * Accumulate a batch set after etalon upsert phase.
         * @param batchSet the batch set to accumulate
         */
        private void accumulateEtalon(UpsertRelationsRequestContext ctx) {

            for (Entry<String,List<UpsertRelationRequestContext>> entry : ctx.getRelations().entrySet()) {
                for (UpsertRelationRequestContext uCtx : entry.getValue()) {

                    RelationBatchSet batchSet = uCtx.getFromStorage(StorageId.DATA_BATCH_RELATIONS);
                    if (Objects.isNull(batchSet)) {
                        return;
                    }

                    UpsertRequestContext containment = uCtx.getFromStorage(StorageId.RELATIONS_CONTAINMENT_CONTEXT);
                    if (Objects.nonNull(containment) && Objects.nonNull(recordBatchSetAccumulator)) {
                        recordBatchSetAccumulator.accumulateEtalon(containment);
                    }

                    if (Objects.nonNull(batchSet.getIndexRequestContext())) {
                        indexUpdates.add(batchSet.getIndexRequestContext());
                    }
                }
            }
        }

        @SuppressWarnings("unchecked")
        private BatchKeyReference<RelationKeys> findCachedKeys(RecordIdentityContext left, RecordIdentityContext right) {

            if (Objects.nonNull(relToIdCache)) {

                String cacheString = toChacheString(left, right);
                if (StringUtils.isNotBlank(cacheString)) {
                    return (BatchKeyReference<RelationKeys>) relToIdCache.get(cacheString);
                }
            }

            return null;
        }
    }
}
