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
import org.unidata.mdm.data.po.data.RelationEtalonRemapFromPO;
import org.unidata.mdm.data.po.data.RelationEtalonRemapToPO;
import org.unidata.mdm.data.po.data.RelationOriginRemapPO;
import org.unidata.mdm.data.type.apply.RelationMergeChangeSet;
import org.unidata.mdm.data.util.StorageUtils;
import org.unidata.mdm.system.type.batch.BatchIterator;
import org.unidata.mdm.system.type.batch.BatchSetStatistics;
import org.unidata.mdm.system.type.pipeline.VoidPipelineOutput;

/**
 * @author Mikhail Mikhailov
 * Relation merge support accumulator.
 */
public class RelationMergeBatchSetAccumulator extends AbstractRelationBatchSetAccumulator<MergeRequestContext, VoidPipelineOutput> {
    /**
     * Collected rel. from etalons.
     */
    private final Map<Integer, List<RelationEtalonRemapFromPO>> etalonFromRemaps;
    /**
     * Collected rel. to etalons.
     */
    private final Map<Integer, List<RelationEtalonRemapToPO>> etalonToRemaps;
    /**
     * Collected origin remap (from one eid -> another eid) objects.
     */
    private final Map<Integer, List<RelationOriginRemapPO>> originRemaps;
    /**
     * Constructor.
     */
    public RelationMergeBatchSetAccumulator(int commitSize) {
        super(commitSize);
        etalonFromRemaps = new HashMap<>(StorageUtils.numberOfShards());
        etalonToRemaps = new HashMap<>(StorageUtils.numberOfShards());
        originRemaps = new HashMap<>(StorageUtils.numberOfShards());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends BatchSetStatistics<VoidPipelineOutput>> S statistics() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartTypeId() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Adds a single rel etalon remap record update.
     * @param po the update
     */
    protected void accumulateEtalonRemapFrom(RelationEtalonRemapFromPO po) {
        if (Objects.nonNull(po)) {
            etalonFromRemaps.computeIfAbsent(po.getShard(), k -> new ArrayList<RelationEtalonRemapFromPO>(commitSize))
                .add(po);
        }
    }
    /**
     * Adds several rel etalon remap record updates. Needed for merge op.
     * @param pos the update
     */
    protected void accumulateEtalonsRemapFrom(List<RelationEtalonRemapFromPO> pos) {
        if (CollectionUtils.isNotEmpty(pos)) {
            for (int i = 0; i < pos.size(); i++) {
                accumulateEtalonRemapFrom(pos.get(i));
            }
        }
    }
    /**
     * Adds a single rel etalon remap record update.
     * @param po the update
     */
    protected void accumulateEtalonRemapTo(RelationEtalonRemapToPO po) {
        if (Objects.nonNull(po)) {
            etalonToRemaps.computeIfAbsent(po.getShard(), k -> new ArrayList<RelationEtalonRemapToPO>(commitSize))
                .add(po);
        }
    }
    /**
     * Adds several rel etalon remap record updates. Needed for merge op.
     * @param pos the update
     */
    protected void accumulateEtalonsRemapTo(List<RelationEtalonRemapToPO> pos) {
        if (CollectionUtils.isNotEmpty(pos)) {
            for (int i = 0; i < pos.size(); i++) {
                accumulateEtalonRemapTo(pos.get(i));
            }
        }
    }
    /**
     * Adds a single rel origin remap record update.
     * @param po the update
     */
    protected void accumulateOriginRemap(RelationOriginRemapPO po) {
        if (Objects.nonNull(po)) {
            originRemaps.computeIfAbsent(po.getShard(), k -> new ArrayList<RelationOriginRemapPO>(commitSize))
                .add(po);
        }
    }
    /**
     * Adds several rel origin remap record updates. Needed for merge op.
     * @param pos the update
     */
    protected void accumulateOriginsRemap(List<RelationOriginRemapPO> pos) {
        if (CollectionUtils.isNotEmpty(pos)) {
            for (int i = 0; i < pos.size(); i++) {
                accumulateOriginRemap(pos.get(i));
            }
        }
    }
    /**
     * @return the etalonRemapFromPOs
     */
    public Map<Integer, List<RelationEtalonRemapFromPO>> getEtalonFromRemaps() {
        return etalonFromRemaps;
    }
    /**
     * @return the etalonRemapToPOs
     */
    public Map<Integer, List<RelationEtalonRemapToPO>> getEtalonToRemaps() {
        return etalonToRemaps;
    }
    /**
     * @return the originRemaps
     */
    public Map<Integer, List<RelationOriginRemapPO>> getOriginRemaps() {
        return originRemaps;
    }

    /**
     * Accumulate artifacts.
     * @param ctx the context
     */
    public void accumulateUpdates(MergeRequestContext ctx) {

        RelationMergeChangeSet batchSet = ctx.relationChangeSet();
        if (Objects.isNull(batchSet)) {
            return;
        }

        accumulateEtalonsRemapFrom(batchSet.getEtalonFromRemaps());
        accumulateEtalonsRemapTo(batchSet.getEtalonToRemaps());
        accumulateOriginsRemap(batchSet.getOriginRemaps());
        accumulateEtalonUpdates(batchSet.getEtalonUpdates());
        accumulateWipeExternalKeys(batchSet.getExternalKeyWipes());
        accumulateInsertExternalKeys(batchSet.getExternalKeyInserts());

        if (CollectionUtils.isNotEmpty(batchSet.getIndexRequestContexts())) {
            indexUpdates.addAll(batchSet.getIndexRequestContexts());
        }
    }
    @Override
    public BatchIterator<MergeRequestContext> iterator() {
        return new RelationMergeBatchIterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void discharge() {
        super.discharge();
        etalonFromRemaps.values().forEach(Collection::clear);
        etalonToRemaps.values().forEach(Collection::clear);
        originRemaps.values().forEach(Collection::clear);
    }

    /**
     * @author Mikhail Mikhailov
     * Simple batch iterator.
     */
    private class RelationMergeBatchIterator implements BatchIterator<MergeRequestContext> {
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
        public RelationMergeBatchIterator() {
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

    }
}
