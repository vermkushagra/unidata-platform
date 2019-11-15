package org.unidata.mdm.data.type.apply.batch.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.unidata.mdm.data.context.MergeRequestContext;
import org.unidata.mdm.data.po.data.RecordEtalonPO;
import org.unidata.mdm.data.po.data.RecordOriginRemapPO;
import org.unidata.mdm.data.po.keys.RecordExternalKeysPO;
import org.unidata.mdm.data.type.apply.RecordMergeChangeSet;
import org.unidata.mdm.data.type.apply.batch.BatchIterator;
import org.unidata.mdm.data.util.StorageUtils;

/**
 * @author Mikhail Mikhailov
 * Merge accumulator for records.
 */
public class RecordMergeBatchSetAccumulator extends AbstractRecordBatchSetAccumulator<MergeRequestContext> {
    /**
     * Relation merge acc.
     */
    private final RelationMergeBatchSetAccumulator relationBatchSetAccumulator;
    /**
     * Classifier merge acc.
     */
// @Modules
//    private final ClassifierMergeBatchSetAccumulator classifierBatchSetAccumulator;
    /**
     * Record etalon updates.
     */
//    private final Map<String, List<ClusterRecord>> preclustringRecordsForDelete;
    /**
     * Winnders list.
     */
    private final List<RecordEtalonPO> etalonWinners;
    /**
     * Record origin inserts.
     */
    private final Map<Integer, List<RecordOriginRemapPO>> originRemaps;
    /**
     * Record origin external id inserts.
     */
    private final Map<Integer, List<RecordExternalKeysPO>> externalKeysUpdates;
    /**
     * Constructor.
     * @param commitSize the size of the commit interval (number of contexts in a single block)
     */
    public RecordMergeBatchSetAccumulator(int commitSize) {
        super(commitSize);
        relationBatchSetAccumulator = new RelationMergeBatchSetAccumulator(commitSize);
// @Modules
//        classifierBatchSetAccumulator = new ClassifierMergeBatchSetAccumulator(commitSize);
//        preclustringRecordsForDelete = new HashMap<>(1);
        etalonWinners = new ArrayList<>(commitSize);
        originRemaps = new HashMap<>(StorageUtils.numberOfShards());
        externalKeysUpdates = new HashMap<>(StorageUtils.numberOfShards());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BatchIterator<MergeRequestContext> iterator() {
        return new RecordMergeBatchIterator();
    }

    @Override
    public void discharge() {
        super.discharge();
        relationBatchSetAccumulator.discharge();
// @Modules
//        classifierBatchSetAccumulator.discharge();
//        preclustringRecordsForDelete.clear();
        etalonWinners.clear();
        originRemaps.values().forEach(Collection::clear);
        externalKeysUpdates.values().forEach(Collection::clear);
    }

    /**
     * Adds a single origin record update.
     * @param po the update
     */
    protected void accumulateOriginRemap(RecordOriginRemapPO po) {
        if (Objects.nonNull(po)) {
            originRemaps.computeIfAbsent(po.getShard(), k -> new ArrayList<RecordOriginRemapPO>(commitSize))
                .add(po);
        }
    }
    /**
     * Adds several origin record updates.
     * @param pos the update
     */
    protected void accumulateOriginRemaps(List<RecordOriginRemapPO> pos) {
        if (CollectionUtils.isNotEmpty(pos)) {
            for (int i = 0; i < pos.size(); i++) {
                accumulateOriginRemap(pos.get(i));
            }
        }
    }
    /**
     * Adds a single origin external id record update.
     * @param po the update
     */
    protected void accumulateExternalIdUpdate(RecordExternalKeysPO ekpo) {
        if (Objects.nonNull(ekpo)) {
            externalKeysUpdates.computeIfAbsent(ekpo.getShard(), k -> new ArrayList<RecordExternalKeysPO>(commitSize))
                .add(ekpo);
        }
    }
    /**
     * Adds several origin record inserts.
     * @param pos the update
     */
    protected void accumulateExternalIdUpdates(List<RecordExternalKeysPO> pos) {
        if (CollectionUtils.isNotEmpty(pos)) {
            for (int i = 0; i < pos.size(); i++) {
                accumulateExternalIdUpdate(pos.get(i));
            }
        }
    }
    /**
     * Accumulate stuff.
     * @param ctx a merge context to accumulate
     */
    public void accumulateUpdates(MergeRequestContext ctx) {

        // All the delete and reindex stuff together
        RecordMergeChangeSet set = ctx.recordChangeSet();

        accumulateEtalonUpdates(set.getRecordEtalonMergePOs());
        accumulateOriginRemaps(set.getRecordOriginRemapPOs());
        accumulateExternalIdUpdates(set.getRecordExternalKeysUpdatePOs());

        if (set.getRecordEtalonWinnerPO() != null) {
            etalonWinners.add(set.getRecordEtalonWinnerPO());
        }

        // Accumulates relations
        relationBatchSetAccumulator.accumulateUpdates(ctx);
        // Accumulate classifiers
// @Modules
//        classifierBatchSetAccumulator.accumulateUpdates(ctx);

        indexUpdates.addAll(set.getIndexRequestContexts());
// @Modules
//        set.getPreclusteringRecords().forEach((s, clusterRecords) ->
//                preclustringRecordsForDelete.computeIfAbsent(s, k -> new ArrayList<>()).addAll(clusterRecords));
    }

    /**
     * Record etalon updates.
     */
// @Modules
//    public Map<String, List<ClusterRecord>> getPreclustringRecordsForDelete() {
//        return preclustringRecordsForDelete;
//    }

    /**
     * @return the originRemaps
     */
    public Map<Integer, List<RecordOriginRemapPO>> getOriginRemaps() {
        return originRemaps;
    }

    /**
     * @return the externalKeysUpdates
     */
    public Map<Integer, List<RecordExternalKeysPO>> getExternalKeysUpdates() {
        return externalKeysUpdates;
    }

    /**
     * @return the relationBatchSetAccumulator
     */
    public RelationMergeBatchSetAccumulator getRelationBatchSetAccumulator() {
        return relationBatchSetAccumulator;
    }


    public List<RecordEtalonPO> getEtalonWinners() {
        return etalonWinners;
    }

    /**
     * Classifier merge acc.
     */
// @Modules
//    public ClassifierMergeBatchSetAccumulator getClassifierBatchSetAccumulator() {
//        return classifierBatchSetAccumulator;
//    }

    /**
     * @author Mikhail Mikhailov
     * Simple batch iterator.
     */
    private class RecordMergeBatchIterator implements BatchIterator<MergeRequestContext> {
        /**
         * List iterator.
         */
        private ListIterator<MergeRequestContext> i = workingCopy.listIterator();
        /**
         * Current entry.
         */
        private MergeRequestContext current = null;
        /**
         * Constructor.
         */
        public RecordMergeBatchIterator() {
            super();
        }
        /**
         * If there are more elements to iterate.
         * @return true, if so, false otherwise
         */
        @Override
        public boolean hasNext() {

            boolean hasNext = i.hasNext();
            if (!hasNext && current != null) {
                accumulateUpdates(current);
            }

            return hasNext;
        }
        /**
         * Next context for origin upsert
         * @return next context
         */
        @Override
        public MergeRequestContext next() {

            MergeRequestContext next = i.next();
            if (current != null) {
                accumulateUpdates(current);
            }
            init(next);

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
         * Does some preprocessing.
         * @param ctx the upsert context
         */
        private void init(MergeRequestContext ctx) {

            if (Objects.isNull(ctx.recordChangeSet())) {
                ctx.recordChangeSet(new RecordMergeBatchSet());
            }

            if (Objects.isNull(ctx.relationChangeSet())) {
                ctx.relationChangeSet(new RelationMergeBatchSet(relationBatchSetAccumulator));
            }

//            if (Objects.isNull(ctx.classifierChangeSet())) {
//                ctx.classifierChangeSet(new ClassifierMergeBatchSet());
//            }
        }
    }
}
