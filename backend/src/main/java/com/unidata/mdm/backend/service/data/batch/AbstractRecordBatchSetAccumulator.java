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
        dischargeOriginsPhase();
        indexUpdates.clear();
    }

    public void dischargeOriginsPhase() {
        etalonUpdates.clear();
        originUpdates.clear();
        vistory.clear();
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
