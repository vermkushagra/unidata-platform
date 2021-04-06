package com.unidata.mdm.backend.service.data.batch;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.po.EtalonRecordPO;
import com.unidata.mdm.backend.po.OriginRecordPO;
import com.unidata.mdm.backend.po.OriginsVistoryRecordPO;

/**
 * @author Mikhail Mikhailov
 * Record batch set base.
 */
public abstract class RecordBatchSet {
    /**
     * Etalon record persistant object.
     */
    protected EtalonRecordPO etalonRecordUpdatePO;
    /**
     * Origin record persistant objects.
     */
    protected List<OriginRecordPO> originRecordUpdatePOs = new ArrayList<>(2);
    /**
     * Data vistory persistent objects.
     */
    protected List<OriginsVistoryRecordPO> originsVistoryRecordPOs = new ArrayList<>(3);
    /**
     * Data to index afterwards.
     */
    protected IndexRequestContext indexRequestContext;

    /**
     * @return the etalonRecordUpdatePO
     */
    public EtalonRecordPO getEtalonRecordUpdatePO() {
        return etalonRecordUpdatePO;
    }

    /**
     * @param etalonRecordUpdatePO the etalonRecordUpdatePO to set
     */
    public void setEtalonRecordUpdatePO(EtalonRecordPO etalonRecordUpdatePO) {
        this.etalonRecordUpdatePO = etalonRecordUpdatePO;
    }

    /**
     * @return the indexRequestContext
     */
    public IndexRequestContext getIndexRequestContext() {
        return indexRequestContext;
    }

    /**
     * @param indexRequestContext the indexRequestContext to set
     */
    public void setIndexRequestContext(IndexRequestContext indexRequestContext) {
        this.indexRequestContext = indexRequestContext;
    }

    /**
     * @return the originRecordUpdatePOs
     */
    public List<OriginRecordPO> getOriginRecordUpdatePOs() {
        return originRecordUpdatePOs;
    }

    /**
     * @return the originsVistoryRecordPOs
     */
    public List<OriginsVistoryRecordPO> getOriginsVistoryRecordPOs() {
        return originsVistoryRecordPOs;
    }

}
