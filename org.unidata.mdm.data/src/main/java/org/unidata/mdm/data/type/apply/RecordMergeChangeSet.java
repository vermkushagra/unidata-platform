/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
