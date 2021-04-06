package com.unidata.mdm.backend.service.data.batch;

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
    /**
     * Constructor.
     * @param commitSize commit size
     * @param targets target tables
     */
    public RecordDeleteBatchSetAccumulator(int commitSize, Map<BatchTarget, String> targets) {
        super(commitSize, targets);
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
