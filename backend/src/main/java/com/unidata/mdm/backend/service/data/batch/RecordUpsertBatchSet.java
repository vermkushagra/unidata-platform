package com.unidata.mdm.backend.service.data.batch;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.po.EtalonRecordPO;
import com.unidata.mdm.backend.po.OriginRecordPO;

/**
 * @author Mikhail Mikhailov
 * Simple record batch set - objects needed, for a record to be upserted in a batched fashion.
 */
public final class RecordUpsertBatchSet extends RecordBatchSet {
    /**
     * Accumulator link.
     */
    private RecordUpsertBatchSetAccumulator accumulator;
    /**
     * Etalon record persistant object.
     */
    protected EtalonRecordPO etalonRecordInsertPO;
    /**
     * Origin record persistant objects.
     */
    protected List<OriginRecordPO> originRecordInsertPOs = new ArrayList<>(2);
    /**
     * Constructor.
     * @param accumulator link to accumulator.
     */
    public RecordUpsertBatchSet(RecordUpsertBatchSetAccumulator accumulator) {
        super();
        this.accumulator = accumulator;
    }
    /**
     * @return the etalonRecordPO
     */
    public EtalonRecordPO getEtalonRecordInsertPO() {
        return etalonRecordInsertPO;
    }
    /**
     * @param etalonRecordPO the etalonRecordPO to set
     */
    public void setEtalonRecordInsertPO(EtalonRecordPO etalonRecordPO) {
        this.etalonRecordInsertPO = etalonRecordPO;
    }
    /**
     * @return the originRecordPOs
     */
    public List<OriginRecordPO> getOriginRecordInsertPOs() {
        return originRecordInsertPOs;
    }
    /**
     * @return the accumulator
     */
    public RecordUpsertBatchSetAccumulator getRecordsAccumulator() {
        return accumulator;
    }
}
