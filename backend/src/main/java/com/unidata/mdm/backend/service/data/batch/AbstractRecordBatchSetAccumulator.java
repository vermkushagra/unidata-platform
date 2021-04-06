package com.unidata.mdm.backend.service.data.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.context.CommonRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.po.EtalonRecordPO;
import com.unidata.mdm.backend.po.OriginRecordPO;
import com.unidata.mdm.backend.po.OriginsVistoryRecordPO;

/**
 * @author Mikhail Mikhailov
 * Basic stuff.
 */
public abstract class AbstractRecordBatchSetAccumulator<T extends CommonRequestContext>
    extends AbstractBatchSetAccumulator<T> {
    /**
     * Record etalon updates.
     */
    protected final List<EtalonRecordPO> etalonUpdates;
    /**
     * Record origin updates.
     */
    protected final List<OriginRecordPO> originUpdates;
    /**
     * Record visory records.
     */
    protected final List<OriginsVistoryRecordPO> vistory;
    /**
     * Index updates.
     */
    protected final List<IndexRequestContext> indexUpdates;
    /**
     * Constructor.
     * @param commitSize the commit size
     * @param targets target tables.
     */
    protected AbstractRecordBatchSetAccumulator(int commitSize, Map<BatchTarget, String> targets) {
        super(commitSize, targets);
        this.etalonUpdates = new ArrayList<>(commitSize);
        this.originUpdates = new ArrayList<>(commitSize);
        this.vistory = new ArrayList<>(commitSize);
        this.indexUpdates = new ArrayList<>(commitSize);
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
    /**
     * @return the etalonUpdates
     */
    public List<EtalonRecordPO> getEtalonUpdates() {
        return etalonUpdates;
    }
    /**
     * @return the originUpdates
     */
    public List<OriginRecordPO> getOriginUpdates() {
        return originUpdates;
    }
    /**
     * @return the vistory
     */
    public List<OriginsVistoryRecordPO> getVistory() {
        return vistory;
    }
    /**
     * @return the indexUpdates
     */
    public List<IndexRequestContext> getIndexUpdates() {
        return indexUpdates;
    }
}
