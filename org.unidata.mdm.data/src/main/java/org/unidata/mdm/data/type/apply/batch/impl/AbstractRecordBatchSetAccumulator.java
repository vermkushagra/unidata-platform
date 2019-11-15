package org.unidata.mdm.data.type.apply.batch.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.unidata.mdm.data.po.data.RecordEtalonPO;
import org.unidata.mdm.data.po.data.RecordOriginPO;
import org.unidata.mdm.data.po.data.RecordVistoryPO;
import org.unidata.mdm.data.type.apply.batch.AbstractBatchSetAccumulator;
import org.unidata.mdm.data.util.StorageUtils;
import org.unidata.mdm.search.context.IndexRequestContext;
import org.unidata.mdm.system.context.CommonRequestContext;

/**
 * @author Mikhail Mikhailov
 * Basic stuff.
 */
public abstract class AbstractRecordBatchSetAccumulator<T extends CommonRequestContext>
    extends AbstractBatchSetAccumulator<T> {
    /**
     * Record etalon updates. Key is the target shard number.
     */
    protected final Map<Integer, List<RecordEtalonPO>> etalonUpdates;
    /**
     * Record origin updates. Key is the target shard number.
     */
    protected final Map<Integer, List<RecordOriginPO>> originUpdates;
    /**
     * Record visory records. Key is the target shard number.
     */
    protected final Map<Integer, List<RecordVistoryPO>> vistory;
    /**
     * Index updates.
     */
    protected final List<IndexRequestContext> indexUpdates;
    /**
     * Constructor.
     * @param commitSize the commit size
     */
    protected AbstractRecordBatchSetAccumulator(int commitSize) {
        super(commitSize);
        this.etalonUpdates = new HashMap<>(StorageUtils.numberOfShards());
        this.originUpdates = new HashMap<>(StorageUtils.numberOfShards());
        this.vistory = new HashMap<>(StorageUtils.numberOfShards());
        this.indexUpdates = new ArrayList<>(commitSize);
    }
    /**
     * Adds a single etalon record update.
     * @param po the update
     */
    protected void accumulateEtalonUpdate(RecordEtalonPO po) {
        if (Objects.nonNull(po)) {
            etalonUpdates.computeIfAbsent(po.getShard(), k -> new ArrayList<RecordEtalonPO>(commitSize))
                .add(po);
        }
    }
    /**
     * Adds several etalon record updates. Needed for merge op.
     * @param pos the update
     */
    protected void accumulateEtalonUpdates(List<RecordEtalonPO> pos) {
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
    protected void accumulateOriginUpdate(RecordOriginPO po) {
        if (Objects.nonNull(po)) {
            originUpdates.computeIfAbsent(po.getShard(), k -> new ArrayList<RecordOriginPO>(commitSize))
                .add(po);
        }
    }
    /**
     * Adds several origin record updates.
     * @param pos the update
     */
    protected void accumulateOriginUpdates(List<RecordOriginPO> pos) {
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
    protected void accumulateVistory(RecordVistoryPO po) {
        if (Objects.nonNull(po)) {
            vistory.computeIfAbsent(po.getShard(), k -> new ArrayList<RecordVistoryPO>(commitSize))
                .add(po);
        }
    }
    /**
     * Adds several origin record updates.
     * @param pos the update
     */
    protected void accumulateVistories(List<RecordVistoryPO> pos) {
        if (CollectionUtils.isNotEmpty(pos)) {
            for (int i = 0; i < pos.size(); i++) {
                accumulateVistory(pos.get(i));
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void discharge() {
        super.discharge();
        dischargeOriginsPhase();
        indexUpdates.clear();
    }
    /**
     * Discharges origins collections separately.
     */
    public void dischargeOriginsPhase() {
        etalonUpdates.values().forEach(Collection::clear);
        originUpdates.values().forEach(Collection::clear);
        vistory.values().forEach(Collection::clear);
    }
    /**
     * @return the etalonUpdates
     */
    public Map<Integer, List<RecordEtalonPO>> getEtalonUpdates() {
        return etalonUpdates;
    }
    /**
     * @return the originUpdates
     */
    public Map<Integer, List<RecordOriginPO>> getOriginUpdates() {
        return originUpdates;
    }
    /**
     * @return the vistory
     */
    public Map<Integer, List<RecordVistoryPO>> getVistory() {
        return vistory;
    }
    /**
     * @return the indexUpdates
     */
    public List<IndexRequestContext> getIndexUpdates() {
        return indexUpdates;
    }
}
