package com.unidata.mdm.backend.service.data.batch;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.po.EtalonClassifierPO;
import com.unidata.mdm.backend.po.OriginClassifierPO;
import com.unidata.mdm.backend.po.OriginsVistoryClassifierPO;

/**
 * @author Mikhail Mikhailov
 * Classifier batch set.
 */
public final class ClassifierBatchSet {
    /**
     * Etalon record insert PO.
     */
    private EtalonClassifierPO etalonClassifierInsertPO;
    /**
     * Etalon record update PO.
     */
    private EtalonClassifierPO etalonClassifierUpdatePO;
    /**
     * Origin classifier insert POs.
     */
    private List<OriginClassifierPO> originClassifierInsertPOs = new ArrayList<>(2);
    /**
     * Origin classifier update POs.
     */
    private List<OriginClassifierPO> originClassifierUpdatePOs = new ArrayList<>(2);
    /**
     * Vistory classifier record POs.
     */
    private List<OriginsVistoryClassifierPO> originsVistoryClassifierPO = new ArrayList<>(3);
    /**
     * Index context.
     */
    private IndexRequestContext indexRequestContext;
    /**
     * @return the etalonClassifierPO
     */
    public EtalonClassifierPO getEtalonClassifierInsertPO() {
        return etalonClassifierInsertPO;
    }
    /**
     * @param etalonClassifierPO the etalonClassifierPO to set
     */
    public void setEtalonClassifierInsertPO(EtalonClassifierPO etalonClassifierPO) {
        this.etalonClassifierInsertPO = etalonClassifierPO;
    }
    /**
     * @return the etalonClassifierUpdatePO
     */
    public EtalonClassifierPO getEtalonClassifierUpdatePO() {
        return etalonClassifierUpdatePO;
    }
    /**
     * @param etalonClassifierUpdatePO the etalonClassifierUpdatePO to set
     */
    public void setEtalonClassifierUpdatePO(EtalonClassifierPO etalonClassifierUpdatePO) {
        this.etalonClassifierUpdatePO = etalonClassifierUpdatePO;
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
     * @return the originClassifierPOs
     */
    public List<OriginClassifierPO> getOriginClassifierInsertPOs() {
        return originClassifierInsertPOs;
    }
    /**
     * @return the originClassifierUpdatePOs
     */
    public List<OriginClassifierPO> getOriginClassifierUpdatePOs() {
        return originClassifierUpdatePOs;
    }
    /**
     * @return the originsVistoryClassifierPO
     */
    public List<OriginsVistoryClassifierPO> getOriginsVistoryClassifierPO() {
        return originsVistoryClassifierPO;
    }
}
