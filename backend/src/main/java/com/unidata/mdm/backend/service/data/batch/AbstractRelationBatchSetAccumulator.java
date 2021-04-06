package com.unidata.mdm.backend.service.data.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.context.CommonRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.po.EtalonRelationPO;
import com.unidata.mdm.backend.po.OriginRelationPO;
import com.unidata.mdm.backend.po.OriginsVistoryRelationsPO;

/**
 * @author Mikhail Mikhailov
 * Common classifier part.
 */
public abstract class AbstractRelationBatchSetAccumulator<T extends CommonRequestContext> extends AbstractBatchSetAccumulator<T> {
    /**
     * Collected rel. etalon updates.
     */
    protected final List<EtalonRelationPO> etalonUpdates;
    /**
     * Collected rel. origin updates.
     */
    protected final List<OriginRelationPO> originUpdates;
    /**
     * Collected rel. vistory records.
     */
    protected final List<OriginsVistoryRelationsPO> vistory;
    /**
     * Index updates.
     */
    protected final List<IndexRequestContext> indexUpdates;
    /**
     * Constructor.
     * @param commitSize
     * @param targets
     */
    protected AbstractRelationBatchSetAccumulator(int commitSize, Map<BatchTarget, String> targets) {
        super(commitSize, targets);
        this.etalonUpdates = new ArrayList<>(commitSize);
        this.originUpdates = new ArrayList<>(commitSize);
        this.vistory = new ArrayList<>(commitSize);
        this.indexUpdates = new ArrayList<>(commitSize);
    }
    /**
     * @return the collectedEtalonUpdates
     */
    public List<EtalonRelationPO> getEtalonUpdates() {
        return etalonUpdates;
    }
    /**
     * @return the collectedOriginUpdates
     */
    public List<OriginRelationPO> getOriginUpdates() {
        return originUpdates;
    }
    /**
     * @return the collectedVistory
     */
    public List<OriginsVistoryRelationsPO> getVistory() {
        return vistory;
    }
    /**
     * @return the indexUpdates
     */
    public List<IndexRequestContext> getIndexUpdates() {
        return indexUpdates;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void discharge() {
        super.discharge();
        etalonUpdates.clear();
        originUpdates.clear();
        vistory.clear();
        indexUpdates.clear();
    }
}
