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
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.keys.Keys;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.po.EtalonRecordPO;
import com.unidata.mdm.backend.po.OriginRecordPO;
import com.unidata.mdm.backend.po.OriginsVistoryRecordPO;

/**
 * @author Mikhail Mikhailov
 * Simple record batch set accumulator.
 */
public class RecordUpsertBatchSetAccumulator extends AbstractRecordBatchSetAccumulator<UpsertRequestContext> {
    /**
     * Record etalon inserts.
     */
    private final List<EtalonRecordPO> etalonInserts;
    /**
     * Record origin inserts.
     */
    private final List<OriginRecordPO> originInserts;
    /**
     * External ids cache.
     */
    private final Map<String, BatchKeyReference<? extends Keys>> ids;
    /**
     * Constructor.
     * @param commitSize chunk size
     * @param targets targte tables
     * @param idsCache id cache. Not null for historical imports
     */
    public RecordUpsertBatchSetAccumulator(int commitSize, Map<BatchTarget, String> targets, Map<String, BatchKeyReference<? extends Keys>> idsCache) {
        super(commitSize, targets);
        this.etalonInserts = new ArrayList<>(commitSize);
        this.originInserts = new ArrayList<>(commitSize);
        this.ids = idsCache;
        this.supportedTypes = Collections.emptyList();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void discharge() {
        super.discharge();
        dischargeOriginsPhase();
    }
    @Override
    public void dischargeOriginsPhase() {
        super.dischargeOriginsPhase();
        etalonInserts.clear();
        originInserts.clear();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public BatchIterator<UpsertRequestContext> iterator(BatchSetIterationType iterationType) {
        return new RecordUpsertBatchIterator(iterationType);
    }
    /**
     * @return the etalonInserts
     */
    public List<EtalonRecordPO> getEtalonInserts() {
        return etalonInserts;
    }
    /**
     * @return the originInserts
     */
    public List<OriginRecordPO> getOriginInserts() {
        return originInserts;
    }
    /**
     * @return the ids
     */
    public Map<String, BatchKeyReference<? extends Keys>> getCachedIds() {
        return ids;
    }
    /**
     * Ugly stuff, made public because of containment relations.
     * Accumulates objects, created during origin upsert.
     * @param batchSet batch set
     */
    public void accumulateOrigin(UpsertRequestContext ctx) {

        RecordUpsertBatchSet batchSet = ctx.getFromStorage(StorageId.DATA_BATCH_RECORDS);
        if (Objects.nonNull(batchSet.getEtalonRecordInsertPO())) {
            etalonInserts.add(batchSet.getEtalonRecordInsertPO());
        } else if (Objects.nonNull(batchSet.getEtalonRecordUpdatePO())) {
            etalonUpdates.add(batchSet.getEtalonRecordUpdatePO());
        }

        originInserts.addAll(batchSet.getOriginRecordInsertPOs());
        originUpdates.addAll(batchSet.getOriginRecordUpdatePOs());

        BatchKeyReference<RecordKeys> cachedKeys = findCachedKeys(ctx);
        if (Objects.isNull(cachedKeys)) {

            cachedKeys = new RecordBatchKeyReference(ctx.keys());

            if (Objects.nonNull(ids)) {
                // We cache origins only to keep defferent origin id tracks separate
                ids.put(toExternalIdCacheString(ctx.keys().getOriginKey()), cachedKeys);
                ids.put(ctx.keys().getOriginKey().getId(), cachedKeys);
            }
        }

        // COPY support, revision must be known beforehand.
        int currentRevision = cachedKeys.getRevision();
        for (OriginsVistoryRecordPO v : batchSet.getOriginsVistoryRecordPOs()) {
            v.setRevision(++currentRevision);
            vistory.add(v);
        }

        if (cachedKeys.getRevision() != currentRevision) {
            cachedKeys.setRevision(currentRevision);
        }
    }
    /**
     * Ugly stuff, made public because of containment relations.
     * Accumulates objects, created during etalon upsert.
     * @param ctx the batch set
     */
    public void accumulateEtalon(UpsertRequestContext ctx) {

        RecordBatchSet batchSet = ctx.getFromStorage(StorageId.DATA_BATCH_RECORDS);
        if (Objects.nonNull(batchSet.getIndexRequestContext())) {
            indexUpdates.add(batchSet.getIndexRequestContext());
        }
    }
    /**
     * Finds cached keys.
     * Resolution by ext id only, to keep right key revision track!
     * New key resolution will be forced for the same record but different externnal id.
     * @param ctx the context
     * @return cached reference
     */
    @SuppressWarnings("unchecked")
    public BatchKeyReference<RecordKeys> findCachedKeys(RecordIdentityContext ctx) {

        if (Objects.nonNull(ids) && Objects.nonNull(ctx)) {
            if (ctx.isOriginRecordKey()) {
                return (BatchKeyReference<RecordKeys>) ids.get(ctx.getOriginKey());
            } else if (ctx.isOriginExternalId() || ctx.isEnrichmentKey()) {
                return (BatchKeyReference<RecordKeys>) ids.get(toExternalIdCacheString(ctx.getExternalId(), ctx.getEntityName(), ctx.getSourceSystem()));
            }
        }

        return null;
    }
    /**
     * Cache string generator.
     * @param externalId
     * @param entityName
     * @param sourceSystem
     * @return
     */
    public static String toExternalIdCacheString(String externalId, String entityName, String sourceSystem) {
        return StringUtils.join(externalId, ":", entityName, ":", sourceSystem);
    }
    /**
     * Origin PO to external id.
     * @param origin
     * @return
     */
    public static String toExternalIdCacheString(OriginKey origin) {
        return StringUtils.join(origin.getExternalId(), ":", origin.getEntityName(), ":", origin.getSourceSystem());
    }
    /**
     * @author Mikhail Mikhailov
     * Simple batch iterator.
     */
    private class RecordUpsertBatchIterator implements BatchIterator<UpsertRequestContext> {
        /**
         * List iterator.
         */
        private ListIterator<UpsertRequestContext> i = workingCopy.listIterator();
        /**
         * Current entry.
         */
        private UpsertRequestContext current = null;
        /**
         * Current iteration type.
         */
        private BatchSetIterationType iterationType;
        /**
         * Constructor.
         * @param iterationType iteration type
         */
        public RecordUpsertBatchIterator(BatchSetIterationType iterationType) {
            super();
            this.iterationType = iterationType;
        }
        /**
         * If there are more elements to iterate.
         * @return true, if so, false otherwise
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
         * Next context for origin upsert
         * @return next context
         */
        @Override
        public UpsertRequestContext next() {

            UpsertRequestContext next = i.next();
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

            if(iterationType == BatchSetIterationType.UPSERT_ETALONS){
                if(isNeedInit(next)){
                    init(next);
                }
            }

            current = next;
            return next;
        }
        /**
         * Removes current element.
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
        private void init(UpsertRequestContext ctx) {

            ctx.putToStorage(StorageId.DATA_BATCH_RECORDS, new RecordUpsertBatchSet(RecordUpsertBatchSetAccumulator.this));
            BatchKeyReference<RecordKeys> cachedKeys = findCachedKeys(ctx);
            if (Objects.nonNull(cachedKeys)) {
                ctx.putToStorage(ctx.keysId(), cachedKeys.getKeys());
            }
        }

        private boolean isNeedInit(UpsertRequestContext ctx){
           return ctx.getFromStorage(StorageId.DATA_BATCH_RECORDS) == null;
        }
    }
}
