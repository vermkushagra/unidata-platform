package org.unidata.mdm.data.type.apply.batch.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.unidata.mdm.data.po.data.RelationEtalonPO;
import org.unidata.mdm.data.po.data.RelationOriginPO;
import org.unidata.mdm.data.po.data.RelationVistoryPO;
import org.unidata.mdm.data.po.keys.RelationExternalKeyPO;
import org.unidata.mdm.data.type.apply.batch.AbstractBatchSetAccumulator;
import org.unidata.mdm.data.util.StorageUtils;
import org.unidata.mdm.search.context.IndexRequestContext;
import org.unidata.mdm.system.context.CommonRequestContext;
import org.unidata.mdm.system.type.pipeline.PipelineOutput;

/**
 * @author Mikhail Mikhailov
 * Common classifier part.
 */
public abstract class AbstractRelationBatchSetAccumulator<T extends CommonRequestContext, O extends PipelineOutput>
    extends AbstractBatchSetAccumulator<T, O> {
    /**
     * Collected rel. etalon updates.
     */
    protected final Map<Integer, List<RelationEtalonPO>> etalonUpdates;
    /**
     * Collected rel. origin updates.
     */
    protected final Map<Integer, List<RelationOriginPO>> originUpdates;
    /**
     * Collected rel. vistory records.
     */
    protected final Map<Integer, List<RelationVistoryPO>> vistory;
    /**
     * Index updates.
     */
    protected final List<IndexRequestContext> indexUpdates;
    /**
     * Relation from ext. keys wipe deletes.
     */
    protected final Map<Integer, List<RelationExternalKeyPO>> fromExternalKeysWipes;
    /**
     * Relation to ext. keys wipe deletes.
     */
    protected final Map<Integer, List<RelationExternalKeyPO>> toExternalKeysWipes;
    /**
     * Record origin external id inserts.
     */
    protected final Map<Integer, List<RelationExternalKeyPO>> fromExternalKeysInserts;
    /**
     * Record origin external id inserts.
     */
    protected final Map<Integer, List<RelationExternalKeyPO>> toExternalKeysInserts;
    /**
     * Constructor.
     * @param commitSize
     */
    protected AbstractRelationBatchSetAccumulator(int commitSize) {
        super(commitSize);
        etalonUpdates = new HashMap<>(StorageUtils.numberOfShards());
        originUpdates = new HashMap<>(StorageUtils.numberOfShards());
        vistory = new HashMap<>(StorageUtils.numberOfShards());
        fromExternalKeysInserts = new HashMap<>(StorageUtils.numberOfShards());
        toExternalKeysInserts = new HashMap<>(StorageUtils.numberOfShards());
        fromExternalKeysWipes = new HashMap<>(StorageUtils.numberOfShards());
        toExternalKeysWipes = new HashMap<>(StorageUtils.numberOfShards());
        indexUpdates = new ArrayList<>(commitSize);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void discharge() {
        super.discharge();
        etalonUpdates.values().forEach(Collection::clear);
        originUpdates.values().forEach(Collection::clear);
        vistory.values().forEach(Collection::clear);
        fromExternalKeysInserts.values().forEach(Collection::clear);
        toExternalKeysInserts.values().forEach(Collection::clear);
        fromExternalKeysWipes.values().forEach(Collection::clear);
        toExternalKeysWipes.values().forEach(Collection::clear);
        indexUpdates.clear();
    }
    /**
     * Adds a single etalon record update.
     * @param po the update
     */
    protected void accumulateEtalonUpdate(RelationEtalonPO po) {
        if (Objects.nonNull(po)) {
            etalonUpdates.computeIfAbsent(po.getShard(), k -> new ArrayList<RelationEtalonPO>(commitSize))
                .add(po);
        }
    }
    /**
     * Adds several etalon record updates. Needed for merge op.
     * @param pos the update
     */
    protected void accumulateEtalonUpdates(List<RelationEtalonPO> pos) {
        if (CollectionUtils.isNotEmpty(pos)) {
            for (int i = 0; i < pos.size(); i++) {
                accumulateEtalonUpdate(pos.get(i));
            }
        }
    }
    /**
     * Adds a single origin record update.
     * @param po the update
     */
    protected void accumulateOriginUpdate(RelationOriginPO po) {
        if (Objects.nonNull(po)) {
            originUpdates.computeIfAbsent(po.getShard(), k -> new ArrayList<RelationOriginPO>(commitSize))
                .add(po);
        }
    }
    /**
     * Adds several origin record updates.
     * @param pos the update
     */
    protected void accumulateOriginUpdates(List<RelationOriginPO> pos) {
        if (CollectionUtils.isNotEmpty(pos)) {
            for (int i = 0; i < pos.size(); i++) {
                accumulateOriginUpdate(pos.get(i));
            }
        }
    }
    /**
     * Adds a single vistory record update.
     * @param po the update
     */
    protected void accumulateVistory(RelationVistoryPO po) {
        if (Objects.nonNull(po)) {
            vistory.computeIfAbsent(po.getShard(), k -> new ArrayList<RelationVistoryPO>(commitSize))
                .add(po);
        }
    }
    /**
     * Adds several origin record updates.
     * @param pos the update
     */
    protected void accumulateVistories(List<RelationVistoryPO> pos) {
        if (CollectionUtils.isNotEmpty(pos)) {
            for (int i = 0; i < pos.size(); i++) {
                accumulateVistory(pos.get(i));
            }
        }
    }
    /**
     * Adds a single wipe delete update.
     * @param po the update
     */
    protected void accumulateInsertFromExternalKey(RelationExternalKeyPO po) {
        if (Objects.nonNull(po)) {
            fromExternalKeysInserts.computeIfAbsent(po.getFromShard(), k -> new ArrayList<RelationExternalKeyPO>())
                .add(po);
        }
    }
    /**
     * Adds a single wipe delete update.
     * @param po the update
     */
    protected void accumulateInsertToExternalKey(RelationExternalKeyPO po) {
        if (Objects.nonNull(po)) {
            toExternalKeysInserts.computeIfAbsent(po.getToShard(), k -> new ArrayList<RelationExternalKeyPO>())
                .add(po);
        }
    }
    /**
     * Adds several wipe delete updates.
     * @param pos the update
     */
    protected void accumulateInsertExternalKeys(List<RelationExternalKeyPO> pos) {
        if (CollectionUtils.isNotEmpty(pos)) {
            for (int i = 0; i < pos.size(); i++) {
                accumulateInsertFromExternalKey(pos.get(i));
                accumulateInsertToExternalKey(pos.get(i));
            }
        }
    }
    /**
     * Adds a single wipe delete update.
     * @param po the update
     */
    protected void accumulateWipeFromExternalKey(RelationExternalKeyPO po) {
        if (Objects.nonNull(po)) {
            fromExternalKeysWipes.computeIfAbsent(po.getFromShard(), k -> new ArrayList<RelationExternalKeyPO>())
                .add(po);
        }
    }
    /**
     * Adds a single wipe delete update.
     * @param po the update
     */
    protected void accumulateWipeToExternalKey(RelationExternalKeyPO po) {
        if (Objects.nonNull(po)) {
            fromExternalKeysWipes.computeIfAbsent(po.getToShard(), k -> new ArrayList<RelationExternalKeyPO>())
                .add(po);
        }
    }
    /**
     * Adds several wipe delete updates.
     * @param pos the update
     */
    protected void accumulateWipeExternalKeys(List<RelationExternalKeyPO> pos) {
        if (CollectionUtils.isNotEmpty(pos)) {
            for (int i = 0; i < pos.size(); i++) {
                accumulateWipeFromExternalKey(pos.get(i));
                accumulateWipeToExternalKey(pos.get(i));
            }
        }
    }
    /**
     * @return the collectedEtalonUpdates
     */
    public Map<Integer, List<RelationEtalonPO>> getEtalonUpdates() {
        return etalonUpdates;
    }
    /**
     * @return the collectedOriginUpdates
     */
    public Map<Integer, List<RelationOriginPO>> getOriginUpdates() {
        return originUpdates;
    }
    /**
     * @return the collectedVistory
     */
    public Map<Integer, List<RelationVistoryPO>> getVistory() {
        return vistory;
    }
    /**
     * @return the fromExternalKeysInserts
     */
    public Map<Integer, List<RelationExternalKeyPO>> getFromExternalKeysInserts() {
        return fromExternalKeysInserts;
    }
    /**
     * @return the toExternalKeysInserts
     */
    public Map<Integer, List<RelationExternalKeyPO>> getToExternalKeysInserts() {
        return toExternalKeysInserts;
    }
    /**
     * @return the wipeExternalKeys
     */
    public Map<Integer, List<RelationExternalKeyPO>> getFromExternalKeysWipes() {
        return fromExternalKeysWipes;
    }
    /**
     * @return the wipeToExternalKeys
     */
    public Map<Integer, List<RelationExternalKeyPO>> getToExternalKeysWipes() {
        return toExternalKeysWipes;
    }
    /**
     * @return the indexUpdates
     */
    public List<IndexRequestContext> getIndexUpdates() {
        return indexUpdates;
    }
}
