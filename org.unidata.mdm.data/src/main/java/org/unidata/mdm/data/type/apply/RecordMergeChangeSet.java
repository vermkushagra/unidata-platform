package org.unidata.mdm.data.type.apply;

import java.util.ArrayList;
import java.util.List;

import org.unidata.mdm.core.type.change.ChangeSet;
import org.unidata.mdm.data.po.data.RecordEtalonPO;
import org.unidata.mdm.data.po.data.RecordOriginRemapPO;
import org.unidata.mdm.data.po.keys.RecordExternalKeysPO;
import org.unidata.mdm.search.context.IndexRequestContext;

/**
 * @author Mikhail Mikhailov
 * A slightly different merge change set.
 */
public class RecordMergeChangeSet implements ChangeSet {
    /**
     * Merged stuff.
     */
    protected final List<RecordEtalonPO> recordEtalonMergePOs = new ArrayList<>(2);
    /**
     * Origin record persistant objects.
     */
    protected final List<RecordOriginRemapPO> recordOriginRemapPOs = new ArrayList<>(2);
    /**
     * Update external keys.
     */
    protected final List<RecordExternalKeysPO> recordExternalKeysUpdatePOs = new ArrayList<>(2);
    /**
     * Index contexts.
     */
    protected final List<IndexRequestContext> indexRequestContexts = new ArrayList<>(4);
    /**
     * Winner PO.
     */
    protected RecordEtalonPO recordEtalonWinnerPO;
    /**
     * Constructor.
     */
    public RecordMergeChangeSet() {
        super();
    }
    /**
     * @return the etalonRecordMergePOs
     */
    public List<RecordEtalonPO> getRecordEtalonMergePOs() {
        return recordEtalonMergePOs;
    }
    /**
     * @return the originRecordUpdatePOs
     */
    public List<RecordOriginRemapPO> getRecordOriginRemapPOs() {
        return recordOriginRemapPOs;
    }
    /**
     * @return the recordExternalKeysUpdatePOs
     */
    public List<RecordExternalKeysPO> getRecordExternalKeysUpdatePOs() {
        return recordExternalKeysUpdatePOs;
    }
    /**
     * @return the indexRequestContexts
     */
    public List<IndexRequestContext> getIndexRequestContexts() {
        return indexRequestContexts;
    }

    public RecordEtalonPO getRecordEtalonWinnerPO() {
        return recordEtalonWinnerPO;
    }

    public void setEtalonRecordWinnerPO(RecordEtalonPO etalonRecordWinnerPO) {
        this.recordEtalonWinnerPO = etalonRecordWinnerPO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return recordEtalonMergePOs.isEmpty()
            && recordOriginRemapPOs.isEmpty()
            && recordExternalKeysUpdatePOs.isEmpty()
            && indexRequestContexts.isEmpty()
            && recordEtalonWinnerPO == null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        recordEtalonMergePOs.clear();
        recordOriginRemapPOs.clear();
        recordExternalKeysUpdatePOs.clear();
        indexRequestContexts.clear();
        recordEtalonWinnerPO = null;
    }
}
