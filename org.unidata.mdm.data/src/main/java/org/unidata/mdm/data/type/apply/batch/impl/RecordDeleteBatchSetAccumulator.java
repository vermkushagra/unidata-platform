package org.unidata.mdm.data.type.apply.batch.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.unidata.mdm.data.context.DataContextFlags;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.dto.DeleteRecordDTO;
import org.unidata.mdm.data.po.data.RecordVistoryPO;
import org.unidata.mdm.data.po.keys.RecordExternalKeysPO;
import org.unidata.mdm.data.po.keys.RecordKeysPO;
import org.unidata.mdm.data.service.segments.records.batch.RecordsDeleteStartExecutor;
import org.unidata.mdm.data.type.apply.RecordDeleteChangeSet;
import org.unidata.mdm.data.util.StorageUtils;
import org.unidata.mdm.system.type.batch.BatchIterator;

/**
 * @author Mikhail Mikhailov
 * Delete accumulator.
 */
public class RecordDeleteBatchSetAccumulator extends AbstractRecordBatchSetAccumulator<DeleteRequestContext, DeleteRecordDTO, RecordDeleteBatchSetAccumulator> {
    /**
     * Record wipe deletes.
     */
    private final Map<Integer, List<RecordKeysPO>> wipeRecordKeys;
    /**
     * Specifically external ids, distributed by EXT ID SHARD number.
     */
    private final Map<Integer, List<RecordExternalKeysPO>> wipeExternalKeys;
    /**
     * The stats.
     */
    private final RecordDeleteBatchSetStatistics statistics;
    /**
     * Constructor.
     * @param commitSize commit size
     */
    public RecordDeleteBatchSetAccumulator(int commitSize) {
        super(commitSize);
        wipeRecordKeys = new HashMap<>(StorageUtils.numberOfShards());
        wipeExternalKeys = new HashMap<>(StorageUtils.numberOfShards());
        statistics = new RecordDeleteBatchSetStatistics();
    }

    /**
     * Adds a single wipe delete update.
     * @param po the update
     */
    protected void accumulateWipeExternalKey(RecordExternalKeysPO po) {
        if (Objects.nonNull(po)) {
            wipeExternalKeys.computeIfAbsent(po.getShard(), k -> new ArrayList<RecordExternalKeysPO>())
                .add(po);
        }
    }
    /**
     * Adds several wipe delete updates.
     * @param pos the update
     */
    protected void accumulateWipeExternalKeys(List<RecordExternalKeysPO> pos) {
        if (CollectionUtils.isNotEmpty(pos)) {
            for (int i = 0; i < pos.size(); i++) {
                accumulateWipeExternalKey(pos.get(i));
            }
        }
    }
    /**
     * Adds a single wipe delete update.
     * @param po the update
     */
    protected void accumulateWipeRecordKey(RecordKeysPO po) {

        if (Objects.nonNull(po)) {
            wipeRecordKeys.computeIfAbsent(po.getShard(), k -> new ArrayList<RecordKeysPO>(commitSize))
                .add(po);
        }
    }
    /**
     * Adds several wipe delete updates.
     * @param pos the update
     */
    protected void accumulateWipeRecordKeys(List<RecordKeysPO> pos) {
        if (CollectionUtils.isNotEmpty(pos)) {
            for (int i = 0; i < pos.size(); i++) {
                accumulateWipeRecordKey(pos.get(i));
            }
        }
    }

    @Override
    public void discharge() {
        super.discharge();
        wipeRecordKeys.values().forEach(Collection::clear);
        wipeExternalKeys.values().forEach(Collection::clear);
        statistics.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BatchIterator<DeleteRequestContext> iterator() {
        return new RecordDeleteBatchIterator();
    }

    /**
     * Accumulates indexing data.
     * @param ctx the context
     */
    public void accumulateUpdates(DeleteRequestContext ctx) {

        RecordDeleteChangeSet batchSet = ctx.changeSet();
        accumulateEtalonUpdate(batchSet.getEtalonRecordUpdatePO());
        accumulateOriginUpdates(batchSet.getOriginRecordUpdatePOs());

        // COPY support, revision must be known beforehand.
        int currentRevision = ctx.keys().getOriginKey().getRevision();
        for (RecordVistoryPO v : batchSet.getOriginsVistoryRecordPOs()) {
            v.setRevision(++currentRevision);
            accumulateVistory(v);
        }

        accumulateWipeRecordKeys(batchSet.getWipeRecordKeys());
        accumulateWipeExternalKeys(batchSet.getWipeExternalKeys());

        if (Objects.nonNull(batchSet.getIndexRequestContext())) {
            indexUpdates.add(batchSet.getIndexRequestContext());
        }
    }
    /**
     * Gets collected wipe deletes.
     * @return wipe deletes
     */
    public Map<Integer, List<RecordKeysPO>> getWipeRecordKeys() {
        return wipeRecordKeys;
    }
    /**
     * @return the wipeExternalIds
     */
    public Map<Integer, List<RecordExternalKeysPO>> getWipeExternalKeys() {
        return wipeExternalKeys;
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public RecordDeleteBatchSetStatistics statistics() {
        return statistics;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartTypeId() {
        return RecordsDeleteStartExecutor.SEGMENT_ID;
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
         * Constructor.
         */
        public RecordDeleteBatchIterator() {
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
        public DeleteRequestContext next() {

            DeleteRequestContext next = i.next();
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
        private void init(DeleteRequestContext ctx) {
            // Already initialized
            if (Objects.nonNull(ctx.changeSet())) {
                return;
            }
            ctx.changeSet(new RecordDeleteBatchSet(RecordDeleteBatchSetAccumulator.this));
            ctx.setFlag(DataContextFlags.FLAG_BATCH_OPERATION, true);
        }
    }
}
