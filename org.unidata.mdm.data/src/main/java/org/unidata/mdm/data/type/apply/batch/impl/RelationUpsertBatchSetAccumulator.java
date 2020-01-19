package org.unidata.mdm.data.type.apply.batch.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.data.context.DataContextFlags;
import org.unidata.mdm.data.context.RecordIdentityContext;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.context.UpsertRelationsRequestContext;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.dto.UpsertRelationsDTO;
import org.unidata.mdm.data.po.data.RelationEtalonPO;
import org.unidata.mdm.data.po.data.RelationOriginPO;
import org.unidata.mdm.data.po.data.RelationVistoryPO;
import org.unidata.mdm.data.type.apply.batch.BatchKeyReference;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.data.util.StorageUtils;
import org.unidata.mdm.system.type.batch.BatchIterator;
import org.unidata.mdm.system.type.pipeline.fragment.FragmentId;
import org.unidata.mdm.system.type.pipeline.fragment.InputFragment;

/**
 * @author Mikhail Mikhailov
 * Relation batch set accumulator.
 */
public class RelationUpsertBatchSetAccumulator
    extends AbstractRelationBatchSetAccumulator<UpsertRelationsRequestContext, UpsertRelationsDTO>
    implements InputFragment<RelationUpsertBatchSetAccumulator> {

    public static final FragmentId<RelationUpsertBatchSetAccumulator> ID
        = new FragmentId<>("RELATION_UPSERT_BATCH_SET", () -> new RelationUpsertBatchSetAccumulator(0, false, false));

    /**
     * Collected rel. etalons.
     */
    private final Map<Integer, List<RelationEtalonPO>> etalonInserts;
    /**
     * Collected rel. origin inserts.
     */
    private final Map<Integer, List<RelationOriginPO>> originInserts;
    /**
     * Containments accumulator.
     */
    private final RecordUpsertBatchSetAccumulator recordBatchSetAccumulator;
    /**
     * RelTo id cache for multiVersion relations.
     */
    private final Map<String, BatchKeyReference<RelationKeys>> ids;
    /**
     * This chunk is a containment relation
     */
    private final boolean containmentRelation;
    /**
     * Virtual timelines of type 'REFERENCE'.
     */
    private final Map<String, List<Timeline<OriginRelation>>> collectedReferenceTimelines = new HashMap<>();
    /**
     * Stats / results.
     */
    private final RelationUpsertBatchSetStatistics statistics;
    /**
     * Constructor.
     * @param commitSize chunk size
     * @param isMultiversion true for several updates from the same record id in the same job.
     *  If true, a simple id cache will be build.
     *  The accumulator must exist during the step then.
     * @param isContainment whether this accumulator processes a containment relation
     */
    public RelationUpsertBatchSetAccumulator(int commitSize, boolean isMultiversion, boolean isContainment) {

        super(commitSize);
        this.etalonInserts = new HashMap<>(StorageUtils.numberOfShards());
        this.originInserts = new HashMap<>(StorageUtils.numberOfShards());

        // Containments and keys cache
        containmentRelation = isContainment;
        recordBatchSetAccumulator = isContainment ? new RecordUpsertBatchSetAccumulator(commitSize, isMultiversion) : null;
        statistics = new RelationUpsertBatchSetStatistics();

        if (isMultiversion) {
            ids = new HashMap<>();
        } else {
            ids = null;
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FragmentId<RelationUpsertBatchSetAccumulator> fragmentId() {
        return ID;
    }
    /**
     * Adds a single etalon record update.
     * @param po the update
     */
    protected void accumulateEtalonInsert(RelationEtalonPO po) {
        if (Objects.nonNull(po)) {
            etalonInserts.computeIfAbsent(po.getShard(), k -> new ArrayList<RelationEtalonPO>(commitSize))
                .add(po);
        }
    }
    /**
     * Adds a single origin record update.
     * @param po the update
     */
    protected void accumulateOriginInsert(RelationOriginPO po) {
        if (Objects.nonNull(po)) {
            originInserts.computeIfAbsent(po.getShard(), k -> new ArrayList<RelationOriginPO>(commitSize))
                .add(po);
        }
    }
    /**
     * Adds several origin record inserts.
     * @param pos the update
     */
    protected void accumulateOriginInserts(List<RelationOriginPO> pos) {
        if (CollectionUtils.isNotEmpty(pos)) {
            for (int i = 0; i < pos.size(); i++) {
                accumulateOriginInsert(pos.get(i));
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public BatchIterator<UpsertRelationsRequestContext> iterator() {
        return new RelationUpsertBatchIterator();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void discharge() {
        super.discharge();
        etalonInserts.values().forEach(Collection::clear);
        originInserts.values().forEach(Collection::clear);
        // Containments
        if (Objects.nonNull(recordBatchSetAccumulator)) {
            recordBatchSetAccumulator.discharge();
        }
    }
    /**
     * @return the collectedEtalonInserts
     */
    public Map<Integer, List<RelationEtalonPO>> getEtalonInserts() {
        return etalonInserts;
    }
    /**
     * @return the collectedOriginInserts
     */
    public Map<Integer, List<RelationOriginPO>> getOriginInserts() {
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
    public static String toCacheString(RecordIdentityContext left, RecordIdentityContext right) {

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
            } else if (right.isEtalonRecordKey()) {
                rightSide = right.getEtalonKey();
            }

            if (StringUtils.isNoneBlank(leftSide, rightSide)) {
                return StringUtils.join(leftSide, "|", rightSide);
            }
        }

        return null;
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public RelationUpsertBatchSetStatistics statistics() {
        return statistics;
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
         * Constructor.
         */
        public RelationUpsertBatchIterator() {
            super();
        }
        /**
         * {@inheritDoc}
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
         * {@inheritDoc}
         */
        @Override
        public UpsertRelationsRequestContext next() {

            UpsertRelationsRequestContext next = i.next();
            if (current != null) {
                accumulateUpdates(current);
            }

            init(next);

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
         * Does some preprocessing.
         * @param ctx the upsert context
         */
        private void init(UpsertRelationsRequestContext ctx) {

            for (Entry<String, List<UpsertRelationRequestContext>> entry : ctx.getRelations().entrySet()) {
                for (UpsertRelationRequestContext uCtx : entry.getValue()) {

                    if (Objects.nonNull(uCtx.changeSet())) {
                        continue;
                    }

                    RelationUpsertBatchSet set = new RelationUpsertBatchSet(RelationUpsertBatchSetAccumulator.this);
                    set.setCollectedReferenceTimelines(collectedReferenceTimelines);

                    uCtx.changeSet(set);
                    if (containmentRelation) {
                        BatchKeyReference<RecordKeys> recordKeys = Objects.nonNull(recordBatchSetAccumulator)
                                ? recordBatchSetAccumulator.findCachedKeys(uCtx)
                                : null;

                        if (Objects.nonNull(recordKeys)) {
                            uCtx.keys(recordKeys.getKeys());
                        }
                    } else {
                        BatchKeyReference<RelationKeys> relationKeys = findCachedKeys(ctx, uCtx);
                        if (Objects.nonNull(relationKeys)) {
                            uCtx.relationKeys(relationKeys.getKeys());
                        }
                    }

                    uCtx.setFlag(DataContextFlags.FLAG_BATCH_OPERATION, true);
                }
            }

            ctx.setFlag(DataContextFlags.FLAG_BATCH_OPERATION, true);
        }

        /**
         * Accumulate a batch set after etalon upsert phase.
         * @param ctx the relations context to accumulate
         */
        private void accumulateUpdates(UpsertRelationsRequestContext ctx) {

            for (Entry<String,List<UpsertRelationRequestContext>> entry : ctx.getRelations().entrySet()) {
                for (UpsertRelationRequestContext uCtx : entry.getValue()) {

                    RelationUpsertBatchSet batchSet = uCtx.changeSet();
                    if (Objects.isNull(batchSet)) {
                        return;
                    }

                    accumulateEtalonInsert(batchSet.getEtalonRelationInsertPO());
                    accumulateEtalonUpdates(batchSet.getEtalonRelationUpdatePOs());
                    accumulateOriginInserts(batchSet.getOriginRelationInsertPOs());
                    accumulateOriginUpdates(batchSet.getOriginRelationUpdatePOs());
                    accumulateInsertExternalKeys(batchSet.getExternalKeyInsertPOs());

                    // COPY support, revision must be known beforehand.
                    BatchKeyReference<RelationKeys> cachedKeys = findOrPutCachedKeys(uCtx.relationKeys(), ctx, uCtx);
                    int currentRevision = cachedKeys.getRevision();
                    for (RelationVistoryPO v : batchSet.getOriginsVistoryRelationsPOs()) {
                        v.setRevision(++currentRevision);
                        accumulateVistory(v);
                    }

                    if (cachedKeys.getRevision() != currentRevision) {
                        cachedKeys.setRevision(currentRevision);
                    }

                    // Containment
                    UpsertRequestContext cCtx = uCtx.containmentContext();
                    if (Objects.nonNull(cCtx) && Objects.nonNull(recordBatchSetAccumulator)) {
                        recordBatchSetAccumulator.accumulateUpdates(cCtx);
                    }

                    if (Objects.nonNull(batchSet.getIndexRequestContexts())) {
                        indexUpdates.addAll(batchSet.getIndexRequestContexts());
                    }
                }
            }
        }

        private BatchKeyReference<RelationKeys> findCachedKeys(RecordIdentityContext left, RecordIdentityContext right) {

            if (Objects.nonNull(ids)) {

                String cacheString = toCacheString(left, right);
                if (StringUtils.isNotBlank(cacheString)) {
                    return ids.get(cacheString);
                }
            }

            return null;
        }

        private BatchKeyReference<RelationKeys> findOrPutCachedKeys(RelationKeys keys, RecordIdentityContext left, RecordIdentityContext right) {

            BatchKeyReference<RelationKeys> cachedKeys = null;
            if (Objects.nonNull(ids)) {

                final String cacheString = toCacheString(left, right);
                if (StringUtils.isNotBlank(cacheString)) {
                    cachedKeys = ids.get(cacheString);
                }

                if (Objects.isNull(cachedKeys)) {
                    cachedKeys = new RelationBatchKeyReference(keys);
                    ids.put(cacheString, cachedKeys);
                }
            } else {
                cachedKeys = new RelationBatchKeyReference(keys);
            }

            return cachedKeys;
        }
    }
}
