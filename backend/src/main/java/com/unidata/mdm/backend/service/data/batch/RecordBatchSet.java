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
