package com.unidata.mdm.backend.service.data.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.po.EtalonClassifierPO;
import com.unidata.mdm.backend.po.OriginClassifierPO;
import com.unidata.mdm.backend.po.OriginsVistoryClassifierPO;

/**
 * @author Mikhail Mikhailov
 * Classifiers upsert data accumulator.
 */
public class ClassifierUpsertBatchSetAccumulator extends AbstractClassifierBatchSetAccumulator<UpsertClassifiersDataRequestContext> {
    /**
     * Record etalon inserts.
     */
    private final List<EtalonClassifierPO> etalonInserts;
    /**
     * Record origin inserts.
     */
    private final List<OriginClassifierPO> originInserts;
    /**
     * Constructor.
     * @param commitSize the commit size
     * @param targets target tables map
     */
    public ClassifierUpsertBatchSetAccumulator(int commitSize, Map<BatchTarget, String> targets) {
        super(commitSize, targets);
        this.etalonInserts = new ArrayList<>(commitSize);
        this.originInserts = new ArrayList<>(commitSize);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public BatchIterator<UpsertClassifiersDataRequestContext> iterator(BatchSetIterationType iterationType) {
        return new ClassifiersUpsertBatchIterator(iterationType);
    }
    /**
     * @return the collectedEtalonInserts
     */
    public List<EtalonClassifierPO> getEtalonInserts() {
        return etalonInserts;
    }
    /**
     * @return the collectedOriginInserts
     */
    public List<OriginClassifierPO> getOriginInserts() {
        return originInserts;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void discharge() {
        super.discharge();
        this.etalonInserts.clear();
        this.originInserts.clear();
    }

    /**
     * @author Mikhail Mikhailov
     * Simple batch iterator.
     */
    private class ClassifiersUpsertBatchIterator implements BatchIterator<UpsertClassifiersDataRequestContext> {
        /**
         * List iterator.
         */
        private ListIterator<UpsertClassifiersDataRequestContext> i = workingCopy.listIterator();
        /**
         * Current entry.
         */
        private UpsertClassifiersDataRequestContext current = null;
        /**
         * Current iteration type.
         */
        private BatchSetIterationType iterationType;
        /**
         * Constructor.
         * @param iterationType iteration type
         */
        public ClassifiersUpsertBatchIterator(BatchSetIterationType iterationType) {
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
        public UpsertClassifiersDataRequestContext next() {

            UpsertClassifiersDataRequestContext next = i.next();
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
        private void init(UpsertClassifiersDataRequestContext ctx) {
            ctx.getClassifiers().entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .forEach(uCtx -> uCtx.putToStorage(StorageId.DATA_BATCH_CLASSIFIERS, new ClassifierBatchSet()));
        }
        /**
         * Accumulates a batch set after origin upsert phase.
         * @param batchSet the set to accumulate
         */
        private void accumulateOrigin(UpsertClassifiersDataRequestContext ctx) {

            for (Entry<String, List<UpsertClassifierDataRequestContext>> entry : ctx.getClassifiers().entrySet()) {
                for (UpsertClassifierDataRequestContext uCtx : entry.getValue()) {

                    ClassifierBatchSet batchSet = uCtx.getFromStorage(StorageId.DATA_BATCH_CLASSIFIERS);
                    if (Objects.isNull(batchSet)) {
                        continue;
                    }

                    if (Objects.nonNull(batchSet.getEtalonClassifierInsertPO())) {
                        etalonInserts.add(batchSet.getEtalonClassifierInsertPO());
                    } else if (Objects.nonNull(batchSet.getEtalonClassifierUpdatePO())) {
                        etalonUpdates.add(batchSet.getEtalonClassifierUpdatePO());
                    }

                    originInserts.addAll(batchSet.getOriginClassifierInsertPOs());
                    originUpdates.addAll(batchSet.getOriginClassifierUpdatePOs());

                    ClassifierKeys keys = uCtx.classifierKeys();
                    int currentRevision = keys.getOriginRevision();
                    for (OriginsVistoryClassifierPO po : batchSet.getOriginsVistoryClassifierPO()) {
                        po.setRevision(++currentRevision);
                        vistory.add(po);
                    }
                }
            }
        }

        /**
         * Accumulate a batch set after etalon upsert phase.
         * @param batchSet the batch set to accumulate
         */
        private void accumulateEtalon(UpsertClassifiersDataRequestContext ctx) {

            for (Entry<String,List<UpsertClassifierDataRequestContext>> entry : ctx.getClassifiers().entrySet()) {
                for (UpsertClassifierDataRequestContext uCtx : entry.getValue()) {

                    ClassifierBatchSet batchSet = uCtx.getFromStorage(StorageId.DATA_BATCH_CLASSIFIERS);
                    if (Objects.isNull(batchSet)) {
                        return;
                    }

                    if (Objects.nonNull(batchSet.getIndexRequestContext())) {
                        indexUpdates.add(batchSet.getIndexRequestContext());
                    }
                }
            }
        }
    }
}
