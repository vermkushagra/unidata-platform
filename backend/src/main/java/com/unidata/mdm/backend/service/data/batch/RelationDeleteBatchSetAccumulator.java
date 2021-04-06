package com.unidata.mdm.backend.service.data.batch;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationsRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.po.OriginsVistoryRelationsPO;

/**
 * @author Mikhail Mikhailov
 * Relation batch set accumulator.
 */
public class RelationDeleteBatchSetAccumulator extends AbstractRelationBatchSetAccumulator<DeleteRelationsRequestContext> {
    /**
     * Containments accumulator.
     */
    private final RecordDeleteBatchSetAccumulator recordBatchSetAccumulator;
    /**
     * Constructor.
     * @param commitSize chunk size
     * @param targets target tables
     * @param isContainment whether this accumulator processes a containment relation
     * @param containmentTargets target tables for containment records
     * @param skipEtalonPhase skip index updates generation or not
     */
    public RelationDeleteBatchSetAccumulator(
            int commitSize, Map<BatchTarget, String> targets, boolean isContainment, Map<BatchTarget, String> containmentTargets) {

        super(commitSize, targets);

        // Containments
        recordBatchSetAccumulator = isContainment ? new RecordDeleteBatchSetAccumulator(commitSize, containmentTargets) : null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public BatchIterator<DeleteRelationsRequestContext> iterator(BatchSetIterationType iterationType) {
        return new RelationDeleteBatchIterator(iterationType);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void discharge() {
        super.discharge();

        // Containments
        if (Objects.nonNull(recordBatchSetAccumulator)) {
            recordBatchSetAccumulator.discharge();
        }
    }
    /**
     * @return the recordBatchSetAccumulator
     */
    public RecordDeleteBatchSetAccumulator getRecordBatchSetAccumulator() {
        return recordBatchSetAccumulator;
    }
    /**
     * @author Mikhail Mikhailov
     * Simple batch iterator.
     */
    private class RelationDeleteBatchIterator implements BatchIterator<DeleteRelationsRequestContext> {
        /**
         * The iterator.
         */
        private ListIterator<DeleteRelationsRequestContext> i = workingCopy.listIterator();
        /**
         * Currently processed contex.
         */
        private DeleteRelationsRequestContext current;
        /**
         * Current iteration type.
         */
        private BatchSetIterationType iterationType;
        /**
         * Constructor.
         * @param iterationType the iteration type
         */
        public RelationDeleteBatchIterator(BatchSetIterationType iterationType) {
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
                if (iterationType == BatchSetIterationType.DELETE_ORIGINS) {
                    accumulateOrigin(current);
                } else if (iterationType == BatchSetIterationType.DELETE_ETALONS) {
                    accumulateEtalon(current);
                }
            }

            return hasNext;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public DeleteRelationsRequestContext next() {

            DeleteRelationsRequestContext next = i.next();
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
        private void init(DeleteRelationsRequestContext ctx) {
            for (Entry<String, List<DeleteRelationRequestContext>> entry : ctx.getRelations().entrySet()) {
                for (DeleteRelationRequestContext dCtx : entry.getValue()) {
                    dCtx.putToStorage(StorageId.DATA_BATCH_RELATIONS, new RelationBatchSet(RelationDeleteBatchSetAccumulator.this));
                }
            }
        }
        /**
         * Accumulates a batch set after origin upsert phase.
         * @param batchSet the set to accumulate
         */
        private void accumulateOrigin(DeleteRelationsRequestContext ctx) {

            for (Entry<String, List<DeleteRelationRequestContext>> entry : ctx.getRelations().entrySet()) {
                for (DeleteRelationRequestContext dCtx : entry.getValue()) {

                    RelationBatchSet batchSet = dCtx.getFromStorage(StorageId.DATA_BATCH_RELATIONS);
                    if (Objects.isNull(batchSet)) {
                        continue;
                    }

                    if (Objects.nonNull(batchSet.getEtalonRelationUpdatePO())) {
                        etalonUpdates.add(batchSet.getEtalonRelationUpdatePO());
                    }

                    originUpdates.addAll(batchSet.getOriginRelationUpdatePOs());

                    RelationKeys keys = dCtx.relationKeys();
                    int currentRevision = keys.getOriginRevision();
                    for (OriginsVistoryRelationsPO po : batchSet.getOriginsVistoryRelationsPOs()) {
                        po.setRevision(++currentRevision);
                        vistory.add(po);
                    }

                    DeleteRequestContext containment = dCtx.getFromStorage(StorageId.RELATIONS_CONTAINMENT_CONTEXT);
                    if (Objects.nonNull(containment) && Objects.nonNull(recordBatchSetAccumulator)) {
                        recordBatchSetAccumulator.accumulateOrigin(containment);
                    }
                }
            }
        }

        /**
         * Accumulate a batch set after etalon upsert phase.
         * @param batchSet the batch set to accumulate
         */
        private void accumulateEtalon(DeleteRelationsRequestContext ctx) {

            for (Entry<String,List<DeleteRelationRequestContext>> entry : ctx.getRelations().entrySet()) {
                for (DeleteRelationRequestContext uCtx : entry.getValue()) {

                    RelationBatchSet batchSet = uCtx.getFromStorage(StorageId.DATA_BATCH_RELATIONS);
                    if (Objects.isNull(batchSet)) {
                        continue;
                    }

                    DeleteRequestContext containment = uCtx.getFromStorage(StorageId.RELATIONS_CONTAINMENT_CONTEXT);
                    if (Objects.nonNull(containment) && Objects.nonNull(recordBatchSetAccumulator)) {
                        recordBatchSetAccumulator.accumulateEtalon(containment);
                    }

                    if (Objects.nonNull(batchSet.getIndexRequestContext())) {
                        indexUpdates.add(batchSet.getIndexRequestContext());
                    }
                }
            }
        }
    }
}
