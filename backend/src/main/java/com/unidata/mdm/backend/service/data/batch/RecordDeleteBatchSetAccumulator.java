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
import java.util.Objects;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.po.OriginsVistoryRecordPO;

/**
 * @author Mikhail Mikhailov
 * Delete accumulator.
 */
public class RecordDeleteBatchSetAccumulator extends AbstractRecordBatchSetAccumulator<DeleteRequestContext> {

    private List<String> etalonsForWipeDelete;

    private List<String> originsForWipeDelete;

    /**
     * Constructor.
     * @param commitSize commit size
     * @param targets target tables
     */
    public RecordDeleteBatchSetAccumulator(int commitSize, Map<BatchTarget, String> targets) {
        super(commitSize, targets);
        etalonsForWipeDelete = new ArrayList<>();
        originsForWipeDelete = new ArrayList<>();
    }

    @Override
    public void discharge() {
        super.discharge();
        etalonsForWipeDelete.clear();
        originsForWipeDelete.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BatchIterator<DeleteRequestContext> iterator(BatchSetIterationType iterationType) {
        return new RecordDeleteBatchIterator(iterationType);
    }
    /**
     * Accumulate context.
     * @param ctx the context
     */
    public void accumulateOrigin(DeleteRequestContext ctx) {

        RecordDeleteBatchSet batchSet = ctx.getFromStorage(StorageId.DATA_BATCH_RECORDS);
        if (Objects.nonNull(batchSet.getEtalonRecordUpdatePO())) {
            etalonUpdates.add(batchSet.getEtalonRecordUpdatePO());
        }

        originUpdates.addAll(batchSet.getOriginRecordUpdatePOs());

        // COPY support, revision must be known beforehand.
        int currentRevision = ctx.keys().getOriginKey().getRevision();
        for (OriginsVistoryRecordPO v : batchSet.getOriginsVistoryRecordPOs()) {
            v.setRevision(++currentRevision);
            vistory.add(v);
        }

        if (batchSet.getEtalonsForWipeDelete() != null) {
            etalonsForWipeDelete.addAll(batchSet.getEtalonsForWipeDelete());
        }

        if (batchSet.getOriginsForWipeDelete() != null) {
            originsForWipeDelete.addAll(batchSet.getOriginsForWipeDelete());
        }
    }
    /**
     * Accumulates indexing data.
     * @param ctx the context
     */
    public void accumulateEtalon(DeleteRequestContext ctx) {

        RecordDeleteBatchSet batchSet = ctx.getFromStorage(StorageId.DATA_BATCH_RECORDS);
        if (Objects.nonNull(batchSet.getIndexRequestContext())) {
            indexUpdates.add(batchSet.getIndexRequestContext());
        }
    }

    public List<String> getEtalonsForWipeDelete() {
        return etalonsForWipeDelete;
    }

    public List<String> getOriginsForWipeDelete() {
        return originsForWipeDelete;
    }

    /**
     * @author Mikhail Mikhailov
     * Simple batch iterator.
     */
    private class RecordDeleteBatchIterator implements BatchIterator<DeleteRequestContext> {
        /**
         * List iterator.
         */
        private ListIterator<DeleteRequestContext> i = workingCopy.listIterator();
        /**
         * Current entry.
         */
        private DeleteRequestContext current = null;
        /**
         * Current iteration type.
         */
        private BatchSetIterationType iterationType;
        /**
         * Constructor.
         * @param iterationType iteration type
         */
        public RecordDeleteBatchIterator(BatchSetIterationType iterationType) {
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

                if (iterationType == BatchSetIterationType.DELETE_ORIGINS) {
                    accumulateOrigin(current);
                } else if (iterationType == BatchSetIterationType.DELETE_ETALONS) {
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
        public DeleteRequestContext next() {

            DeleteRequestContext next = i.next();
            if (current != null) {

                if (iterationType == BatchSetIterationType.DELETE_ORIGINS) {
                    accumulateOrigin(current);
                } else if (iterationType == BatchSetIterationType.DELETE_ETALONS) {
                    accumulateEtalon(current);
                }

            }

            if (iterationType == BatchSetIterationType.DELETE_ORIGINS) {
                init(next);
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
        private void init(DeleteRequestContext ctx) {
            ctx.putToStorage(StorageId.DATA_BATCH_RECORDS, new RecordDeleteBatchSet(RecordDeleteBatchSetAccumulator.this));
        }
    }
}
