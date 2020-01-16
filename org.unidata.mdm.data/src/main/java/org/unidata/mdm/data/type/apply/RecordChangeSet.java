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
import org.unidata.mdm.data.po.EtalonRecordDraftStatePO;
import org.unidata.mdm.data.po.data.RecordEtalonPO;
import org.unidata.mdm.data.po.data.RecordOriginPO;
import org.unidata.mdm.data.po.data.RecordVistoryPO;
import org.unidata.mdm.search.context.IndexRequestContext;

/**
 * @author Mikhail Mikhailov
 * Record batch set base.
 */
public abstract class RecordChangeSet implements ChangeSet {
    /**
     * Etalon record persistant object.
     */
    protected RecordEtalonPO etalonRecordUpdatePO;
    /**
     * Origin record persistant objects.
     */
    protected final List<RecordOriginPO> originRecordUpdatePOs = new ArrayList<>(2);
    /**
     * Data vistory persistent objects.
     */
    protected final List<RecordVistoryPO> originsVistoryRecordPOs = new ArrayList<>(3);
    /**
     * Drafts. WF support.
     */
    protected final List<EtalonRecordDraftStatePO> etalonRecordDraftStatePOs = new ArrayList<>(2);
    /**
     * Data to index afterwards.
     */
    protected IndexRequestContext indexRequestContext;
    /**
     * @return the etalonRecordUpdatePO
     */
    public RecordEtalonPO getEtalonRecordUpdatePO() {
        return etalonRecordUpdatePO;
    }
    /**
     * @param etalonRecordUpdatePO the etalonRecordUpdatePO to set
     */
    public void setEtalonRecordUpdatePO(RecordEtalonPO etalonRecordUpdatePO) {
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
    public List<RecordOriginPO> getOriginRecordUpdatePOs() {
        return originRecordUpdatePOs;
    }
    /**
     * @return the originsVistoryRecordPOs
     */
    public List<RecordVistoryPO> getOriginsVistoryRecordPOs() {
        return originsVistoryRecordPOs;
    }
    /**
     * @return the etalonRelationDraftStatePOs
     */
    public List<EtalonRecordDraftStatePO> getEtalonRecordDraftStatePOs() {
        return etalonRecordDraftStatePOs;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return etalonRecordUpdatePO == null
            && originRecordUpdatePOs.isEmpty()
            && originsVistoryRecordPOs.isEmpty()
            && etalonRecordDraftStatePOs.isEmpty()
            && indexRequestContext == null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        etalonRecordUpdatePO = null;
        originRecordUpdatePOs.clear();
        originsVistoryRecordPOs.clear();
        etalonRecordDraftStatePOs.clear();
        indexRequestContext = null;
    }
}
