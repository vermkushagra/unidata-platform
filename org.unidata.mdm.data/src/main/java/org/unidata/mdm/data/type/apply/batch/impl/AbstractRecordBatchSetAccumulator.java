package org.unidata.mdm.data.type.apply.batch.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.unidata.mdm.data.po.data.RecordEtalonPO;
import org.unidata.mdm.data.po.data.RecordOriginPO;
import org.unidata.mdm.data.po.data.RecordVistoryPO;
import org.unidata.mdm.data.type.apply.batch.AbstractBatchSetAccumulator;
import org.unidata.mdm.data.util.StorageUtils;
import org.unidata.mdm.search.context.IndexRequestContext;
import org.unidata.mdm.system.context.CommonRequestContext;
import org.unidata.mdm.system.context.InputFragmentHolder;
import org.unidata.mdm.system.dto.ExecutionResult;
import org.unidata.mdm.system.type.pipeline.fragment.FragmentId;
import org.unidata.mdm.system.type.pipeline.fragment.InputFragment;
import org.unidata.mdm.system.type.pipeline.fragment.InputFragmentCollector;
import org.unidata.mdm.system.type.pipeline.fragment.InputFragmentContainer;

/**
 * @author Mikhail Mikhailov
 * Basic stuff.
 */
public abstract class AbstractRecordBatchSetAccumulator<T extends CommonRequestContext, O extends ExecutionResult, X extends AbstractRecordBatchSetAccumulator<T, O, X>>
    extends AbstractBatchSetAccumulator<T, O>
    implements InputFragmentCollector<X>, InputFragmentContainer {
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
     * Fragments map.
     */
    protected Map<FragmentId<? extends InputFragment<?>>, InputFragmentHolder> fragments;
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
     * This cast trick.
     * @return self
     */
    @SuppressWarnings("unchecked")
    protected X self() {
        return (X) this;
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
    /**
     * Adds a singleton fragment for an ID using a supplier.
     * @param s the supplier
     * @return self
     */
    @Override
    public X fragment(Supplier<? extends InputFragment<?>> s) {

        InputFragment<?> f = s.get();
        if (Objects.nonNull(f)) {

            if (fragments == null) {
                fragments = new IdentityHashMap<>();
            }

            fragments.put(f.fragmentId(), InputFragmentHolder.of(f));
        }
        return self();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public X fragment(InputFragment<?> f) {
        return fragment(() -> f);
    }
    /**
     * Adds a fragment collection for an ID using a supplier.
     * @param s the supplier
     * @return self
     */
    @Override
    public X fragments(Supplier<Collection<? extends InputFragment<?>>> s) {

        Collection<? extends InputFragment<?>> fs = s.get();
        if (Objects.nonNull(fs) && CollectionUtils.isNotEmpty(fs)) {

            if (fragments == null) {
                fragments = new IdentityHashMap<>();
            }

            fragments.put(fs.iterator().next().fragmentId(), InputFragmentHolder.of(fs));
        }
        return self();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public X fragments(Collection<? extends InputFragment<?>> f) {
        return fragments(() -> f);
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends InputFragment<C>> C fragment(FragmentId<C> f) {

        if (MapUtils.isEmpty(fragments)) {
            return null;
        }

        InputFragmentHolder h = fragments.get(f);
        if (Objects.isNull(h) || !h.isSingle()) {
            return null;
        }

        return (C) h.getSingle();
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends InputFragment<C>> Collection<C> fragments(FragmentId<C> f) {

        if (MapUtils.isEmpty(fragments)) {
            return Collections.emptyList();
        }

        InputFragmentHolder h = fragments.get(f);
        if (Objects.isNull(h) || !h.isMultiple()) {
            return Collections.emptyList();
        }

        return (Collection<C>) h.getMultiple();
    }
}
