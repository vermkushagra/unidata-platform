package org.unidata.mdm.data.type.apply.batch.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.DataShift;
import org.unidata.mdm.core.type.timeline.MutableTimeInterval;
import org.unidata.mdm.data.context.DataContextFlags;
import org.unidata.mdm.data.context.RecordIdentityContext;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.dto.UpsertRecordDTO;
import org.unidata.mdm.data.po.data.RecordEtalonPO;
import org.unidata.mdm.data.po.data.RecordOriginPO;
import org.unidata.mdm.data.po.data.RecordVistoryPO;
import org.unidata.mdm.data.po.keys.RecordExternalKeysPO;
import org.unidata.mdm.data.service.segments.records.batch.RecordsUpsertStartExecutor;
import org.unidata.mdm.data.type.apply.RecordUpsertChangeSet;
import org.unidata.mdm.data.type.apply.batch.BatchKeyReference;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.data.OriginRecordInfoSection;
import org.unidata.mdm.data.type.data.impl.OriginRecordImpl;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RecordOriginKey;
import org.unidata.mdm.data.type.timeline.RecordTimeInterval;
import org.unidata.mdm.data.util.StorageUtils;
import org.unidata.mdm.system.type.batch.BatchIterator;

/**
 * @author Mikhail Mikhailov
 * Simple record batch set accumulator.
 * FIXME: remove origin/etalon phases code. Upsert now works in a single step.
 */
public class RecordUpsertBatchSetAccumulator extends AbstractRecordBatchSetAccumulator<UpsertRequestContext, UpsertRecordDTO, RecordUpsertBatchSetAccumulator> {
    /**
     * Record etalon inserts.
     */
    private final Map<Integer, List<RecordEtalonPO>> etalonInserts;
    /**
     * Record origin inserts.
     */
    private final Map<Integer, List<RecordOriginPO>> originInserts;
    /**
     * Record origin external id inserts.
     */
    private final Map<Integer, List<RecordExternalKeysPO>> externalKeysInserts;
    /**
     * External ids cache.
     */
    private final Map<String, BatchKeyReference<RecordKeys>> ids;
    /**
     * The run stats.
     */
    private final RecordUpsertBatchSetStatistics statistics;
    /**
     * Constructor.
     * @param commitSize chunk size
     * @param isMultiversion true for several updates from the same record id in the same job.
     *  If true, a simple id cache will be build.
     *  The accumulator must exist during the step then.
     */
    public RecordUpsertBatchSetAccumulator(int commitSize, boolean isMultiversion) {
        super(commitSize);
        this.etalonInserts = new HashMap<>(StorageUtils.numberOfShards());
        this.originInserts = new HashMap<>(StorageUtils.numberOfShards());
        this.externalKeysInserts = new HashMap<>(StorageUtils.numberOfShards());
        this.statistics = new RecordUpsertBatchSetStatistics();

        if (isMultiversion) {
            this.ids = new HashMap<>();
        } else {
            this.ids = null;
        }
    }
    /**
     * Adds a single etalon record update.
     * @param po the update
     */
    protected void accumulateEtalonInsert(RecordEtalonPO po) {
        if (Objects.nonNull(po)) {
            etalonInserts.computeIfAbsent(po.getShard(), k -> new ArrayList<>(commitSize))
                .add(po);
        }
    }
    /**
     * Adds a single origin record update.
     * @param po the update
     */
    protected void accumulateOriginInsert(RecordOriginPO po) {
        if (Objects.nonNull(po)) {
            originInserts.computeIfAbsent(po.getShard(), k -> new ArrayList<>(commitSize))
                .add(po);
        }
    }
    /**
     * Adds a single origin external id record update.
     * @param po the update
     */
    protected void accumulateExternalIdInsert(RecordExternalKeysPO ekpo) {
        if (Objects.nonNull(ekpo)) {
            externalKeysInserts.computeIfAbsent(ekpo.getShard(), k -> new ArrayList<>(commitSize))
                .add(ekpo);
        }
    }
    /**
     * Adds several origin record inserts.
     * @param pos the update
     */
    protected void accumulateOriginInserts(List<RecordOriginPO> pos) {
        if (CollectionUtils.isNotEmpty(pos)) {
            for (int i = 0; i < pos.size(); i++) {
                accumulateOriginInsert(pos.get(i));
            }
        }
    }
    /**
     * Adds several origin record inserts.
     * @param pos the update
     */
    protected void accumulateExternalIdInserts(List<RecordExternalKeysPO> pos) {
        if (CollectionUtils.isNotEmpty(pos)) {
            for (int i = 0; i < pos.size(); i++) {
                accumulateExternalIdInsert(pos.get(i));
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartTypeId() {
        return RecordsUpsertStartExecutor.SEGMENT_ID;
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
        etalonInserts.values().forEach(Collection::clear);
        originInserts.values().forEach(Collection::clear);
        externalKeysInserts.values().forEach(Collection::clear);
        statistics.reset();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public BatchIterator<UpsertRequestContext> iterator() {
        return new RecordUpsertBatchIterator();
    }
    /**
     * @return the etalonInserts
     */
    public Map<Integer, List<RecordEtalonPO>> getEtalonInserts() {
        return etalonInserts;
    }
    /**
     * @return the originInserts
     */
    public Map<Integer, List<RecordOriginPO>> getOriginInserts() {
        return originInserts;
    }
    /**
     * @return the externalIdsInserts
     */
    public Map<Integer, List<RecordExternalKeysPO>> getExternalKeysInserts() {
        return externalKeysInserts;
    }
    /**
     * @return the ids
     */
    public Map<String, BatchKeyReference<RecordKeys>> getCachedIds() {
        return ids;
    }
    /**
     * Ugly stuff, made public because of containment relations.
     * Accumulates objects, created during origin upsert.
     * @param ctx context
     */
    public void accumulateOrigin(UpsertRequestContext ctx) {

        RecordUpsertChangeSet batchSet = ctx.changeSet();
        accumulateEtalonInsert(batchSet.getEtalonRecordInsertPO());
        accumulateEtalonUpdate(batchSet.getEtalonRecordUpdatePO());
        accumulateOriginInserts(batchSet.getOriginRecordInsertPOs());
        accumulateExternalIdInserts(batchSet.getExternalKeysInsertPOs());
        accumulateOriginUpdates(batchSet.getOriginRecordUpdatePOs());
        accumulateVistoryUpdates(ctx, batchSet);
        batchSet.clear();
    }

    /**
     * Ugly stuff, made public because of containment relations.
     * Accumulates objects, created during etalon upsert.
     * @param ctx the batch set
     */
    public void accumulateUpdates(UpsertRequestContext ctx) {

        RecordUpsertChangeSet batchSet = ctx.changeSet();
        accumulateOriginInserts(batchSet.getOriginRecordInsertPOs());
        accumulateExternalIdInserts(batchSet.getExternalKeysInsertPOs());
        accumulateOriginUpdates(batchSet.getOriginRecordUpdatePOs());
        accumulateVistoryUpdates(ctx, batchSet);

        if (Objects.nonNull(batchSet.getIndexRequestContext())) {
            indexUpdates.add(batchSet.getIndexRequestContext());
        }

        batchSet.clear();
    }
    /**
     * Finds cached keys.
     * Resolution by ext id only, to keep right key revision track!
     * New key resolution will be forced for the same record but different externnal id.
     * @param ctx the context
     * @return cached reference
     */
    public BatchKeyReference<RecordKeys> findCachedKeys(RecordIdentityContext ctx) {

        if (Objects.nonNull(ids) && Objects.nonNull(ctx)) {
            if (ctx.isOriginRecordKey()) {
                return ids.get(ctx.getOriginKey());
            } else if (ctx.isOriginExternalId() || ctx.isEnrichmentKey()) {
                return ids.get(toExternalIdCacheString(ctx.getExternalId(), ctx.getEntityName(), ctx.getSourceSystem()));
            }else if(ctx.isEtalonRecordKey()) {
            	return ids.get(ctx.getEtalonKey());
            }
        }

        return null;
    }

    public List<MutableTimeInterval<OriginRecord>> findCachedModifications(RecordIdentityContext ctx) {

        BatchKeyReference<RecordKeys> cachedKeys = findCachedKeys(ctx);
        if (cachedKeys != null) {
            return vistory.values().stream().flatMap(Collection::stream)
                    .filter(v -> v.getOriginId().equals(cachedKeys.getKeys().getOriginKey().getId()))
                    .map(v -> {
                        OriginRecordInfoSection is = new OriginRecordInfoSection()
                                .withCreateDate(v.getCreateDate())
                                .withUpdateDate(v.getUpdateDate() != null ? v.getUpdateDate() : v.getCreateDate())
                                .withCreatedBy(v.getCreatedBy())
                                .withUpdatedBy(v.getUpdatedBy())
                                .withShift(DataShift.PRISTINE)
                                .withStatus(v.getStatus())
                                .withApproval(v.getApproval())
                                .withValidFrom(v.getValidFrom())
                                .withValidTo(v.getValidTo())
                                .withOperationType(v.getOperationType())
                                .withRevision(v.getRevision())
                                .withOriginKey(RecordOriginKey.builder()
                                        .externalId(cachedKeys.getKeys().getOriginKey().getExternalId())
                                        .sourceSystem(cachedKeys.getKeys().getOriginKey().getSourceSystem())
                                        .entityName(cachedKeys.getKeys().getEntityName())
                                        .id(cachedKeys.getKeys().getOriginKey().getId())
                                        .build());
                        return new RecordTimeInterval(new OriginRecordImpl()
                                .withDataRecord(v.getData())
                                .withInfoSection(is));
                    })
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
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
    public static String toExternalIdCacheString(RecordOriginKey origin) {
        return StringUtils.join(origin.getExternalId(), ":", origin.getEntityName(), ":", origin.getSourceSystem());
    }

    private void accumulateVistoryUpdates(UpsertRequestContext ctx, RecordUpsertChangeSet batchSet) {

        BatchKeyReference<RecordKeys> cachedKeys = findCachedKeys(ctx);
        if (Objects.isNull(cachedKeys)) {

            cachedKeys = new RecordBatchKeyReference(ctx.keys());

            if (Objects.nonNull(ids)) {
                // We cache origins only to keep defferent origin id tracks separate
                ids.put(toExternalIdCacheString(ctx.keys().getOriginKey()), cachedKeys);
                ids.put(ctx.keys().getOriginKey().getId(), cachedKeys);
                if(!Objects.isNull(ctx.keys().getEtalonKey())) {
                	ids.put(ctx.keys().getEtalonKey().getId(), cachedKeys);
                }
            }
        }

        // COPY support, revision must be known beforehand.
        int currentRevision = cachedKeys.getRevision();
        boolean published = cachedKeys.getKeys().isPublished();
        for (RecordVistoryPO v : batchSet.getOriginsVistoryRecordPOs()) {

            v.setRevision(++currentRevision);

            accumulateVistory(v);

            if (!published && v.getApproval() == ApprovalState.APPROVED) {
                published = true;
            }
        }

        if (cachedKeys.getRevision() != currentRevision) {
            cachedKeys.setRevision(currentRevision);
        }

        if (cachedKeys.getKeys().isPublished() != published) {
            cachedKeys = new RecordBatchKeyReference(RecordKeys.builder(cachedKeys.getKeys())
                    .published(published)
                    .build());

            if (Objects.nonNull(ids)) {
                // We cache origins only to keep defferent origin id tracks separate
                ids.put(toExternalIdCacheString(ctx.keys().getOriginKey()), cachedKeys);
                ids.put(ctx.keys().getOriginKey().getId(), cachedKeys);
                if(!Objects.isNull(ctx.keys().getEtalonKey())) {
                	ids.put(ctx.keys().getEtalonKey().getId(), cachedKeys);
                }
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public RecordUpsertBatchSetStatistics statistics() {
        return statistics;
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
         * Constructor.
         */
        public RecordUpsertBatchIterator() {
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
        public UpsertRequestContext next() {

            UpsertRequestContext next = i.next();
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
        private void init(UpsertRequestContext ctx) {

            // Already initialized
            if (Objects.nonNull(ctx.changeSet())) {
                return;
            }

            ctx.changeSet(new RecordUpsertBatchSet(RecordUpsertBatchSetAccumulator.this));
            BatchKeyReference<RecordKeys> cachedKeys = findCachedKeys(ctx);
            if (Objects.nonNull(cachedKeys)) {
                ctx.keys(cachedKeys.getKeys());
            }

            // Ensure the flag is set
            ctx.setFlag(DataContextFlags.FLAG_BATCH_OPERATION, true);
        }
    }
}
