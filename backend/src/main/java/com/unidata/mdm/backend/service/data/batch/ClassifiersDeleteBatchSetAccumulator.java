package com.unidata.mdm.backend.service.data.batch;

import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

import com.unidata.mdm.backend.common.context.DeleteClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.po.OriginsVistoryClassifierPO;

/**
 * @author Mikhail Mikhailov
 * Classifiers data delete accumulator.
 */
public class ClassifiersDeleteBatchSetAccumulator
        extends AbstractClassifierBatchSetAccumulator<DeleteClassifiersDataRequestContext> {
    /**
     * Constructor.
     * @param commitSize
     * @param targets
     */
    public ClassifiersDeleteBatchSetAccumulator(int commitSize, Map<BatchTarget, String> targets) {
        super(commitSize, targets);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public BatchIterator<DeleteClassifiersDataRequestContext> iterator(BatchSetIterationType iterationType) {
        return new ClassifiersDeleteBatchIterator(iterationType);
    }
    /**
     * @author Mikhail Mikhailov
     * Simple batch iterator.
     */
    private class ClassifiersDeleteBatchIterator implements BatchIterator<DeleteClassifiersDataRequestContext> {
        /**
         * List iterator.
         */
        private ListIterator<DeleteClassifiersDataRequestContext> i = workingCopy.listIterator();
        /**
         * Current entry.
         */
        private DeleteClassifiersDataRequestContext current = null;
        /**
         * Current iteration type.
         */
        private BatchSetIterationType iterationType;
        /**
         * Constructor.
         * @param iterationType iteration type
         */
        public ClassifiersDeleteBatchIterator(BatchSetIterationType iterationType) {
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
                    accumulateOrigins(current);
                } else if (iterationType == BatchSetIterationType.DELETE_ETALONS) {
                    accumulateEtalons(current);
                }
            }

            return hasNext;
        }
        /**
         * Next context for origin upsert
         * @return next context
         */
        @Override
        public DeleteClassifiersDataRequestContext next() {

            DeleteClassifiersDataRequestContext next = i.next();
            if (current != null) {
                if (iterationType == BatchSetIterationType.DELETE_ORIGINS) {
                    accumulateOrigins(current);
                } else if (iterationType == BatchSetIterationType.DELETE_ETALONS) {
                    accumulateEtalons(current);
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
        private void init(DeleteClassifiersDataRequestContext ctx) {
            ctx.getClassifiers().entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .forEach(uCtx -> uCtx.putToStorage(StorageId.DATA_BATCH_CLASSIFIERS, new ClassifierBatchSet()));
        }
        /**
         * Accumulate context.
         * @param ctx the context
         */
        private void accumulateOrigins(DeleteClassifiersDataRequestContext ctx) {

            ctx.getClassifiers().entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .forEach(dCtx -> {

                    ClassifierBatchSet batchSet = dCtx.getFromStorage(StorageId.DATA_BATCH_CLASSIFIERS);
                    if (Objects.nonNull(batchSet.getEtalonClassifierUpdatePO())) {
                        etalonUpdates.add(batchSet.getEtalonClassifierUpdatePO());
                    }

                    originUpdates.addAll(batchSet.getOriginClassifierUpdatePOs());

                    ClassifierKeys keys = dCtx.classifierKeys();
                    int currentRevision = keys.getOriginRevision();
                    for (OriginsVistoryClassifierPO po : batchSet.getOriginsVistoryClassifierPO()) {
                        po.setRevision(++currentRevision);
                        vistory.add(po);
                    }
                });
        }

        /**
         * Accumulate context.
         * @param ctx the context
         */
        private void accumulateEtalons(DeleteClassifiersDataRequestContext ctx) {

            ctx.getClassifiers().entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .map(dCtx -> dCtx.getFromStorage(StorageId.DATA_BATCH_CLASSIFIERS))
                .filter(Objects::nonNull)
                .map(set -> (ClassifierBatchSet) set)
                .forEach(batchSet -> {
                    if (Objects.nonNull(batchSet.getIndexRequestContext())) {
                        indexUpdates.add(batchSet.getIndexRequestContext());
                    }
                });
        }
    }
}
