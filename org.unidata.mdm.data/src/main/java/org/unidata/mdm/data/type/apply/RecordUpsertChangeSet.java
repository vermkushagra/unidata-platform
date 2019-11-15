package org.unidata.mdm.data.type.apply;

import java.util.ArrayList;
import java.util.List;

import org.unidata.mdm.data.po.data.RecordEtalonPO;
import org.unidata.mdm.data.po.data.RecordOriginPO;
import org.unidata.mdm.data.po.keys.RecordExternalKeysPO;

import org.unidata.mdm.core.po.LargeObjectPO;

/**
 * @author Mikhail Mikhailov
 * Upsert specific suff.
 */
public class RecordUpsertChangeSet extends RecordChangeSet {
    /**
     * Etalon record persistant object.
     */
    protected RecordEtalonPO etalonRecordInsertPO;
    /**
     * Origin record persistant objects.
     */
    protected final List<RecordOriginPO> originRecordInsertPOs = new ArrayList<>(2);
    /**
     * External keys PO objects.
     */
    protected final List<RecordExternalKeysPO> externalKeysPOs = new ArrayList<>(2);
    /**
     * Binary data, possibly existing.
     */
    protected final List<LargeObjectPO> largeObjectPOs = new ArrayList<>(2);
    /**
     * Constructor.
     */
    public RecordUpsertChangeSet() {
        super();
    }
    /**
     * @return the etalonRecordPO
     */
    public RecordEtalonPO getEtalonRecordInsertPO() {
        return etalonRecordInsertPO;
    }
    /**
     * @param etalonRecordPO the etalonRecordPO to set
     */
    public void setEtalonRecordInsertPO(RecordEtalonPO etalonRecordPO) {
        this.etalonRecordInsertPO = etalonRecordPO;
    }
    /**
     * @return the originRecordPOs
     */
    public List<RecordOriginPO> getOriginRecordInsertPOs() {
        return originRecordInsertPOs;
    }
    /**
     * @return the recordExternalKeysPOs
     */
    public List<RecordExternalKeysPO> getExternalKeysInsertPOs() {
        return externalKeysPOs;
    }
    /**
     * @return the largeObjectPOs
     */
    public List<LargeObjectPO> getLargeObjectPOs() {
        return largeObjectPOs;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return etalonRecordInsertPO == null
            && originRecordInsertPOs.isEmpty()
            && largeObjectPOs.isEmpty()
            && externalKeysPOs.isEmpty()
            && super.isEmpty();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        etalonRecordInsertPO = null;
        originRecordInsertPOs.clear();
        largeObjectPOs.clear();
        externalKeysPOs.clear();
        super.clear();
    }
}
