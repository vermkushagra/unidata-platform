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
import com.unidata.mdm.backend.po.EtalonRelationPO;
import com.unidata.mdm.backend.po.OriginRelationPO;
import com.unidata.mdm.backend.po.OriginsVistoryRelationsPO;

/**
 * @author Mikhail Mikhailov
 * Common classifier part.
 */
public abstract class AbstractRelationBatchSetAccumulator<T extends CommonRequestContext> extends AbstractBatchSetAccumulator<T> {
    /**
     * Collected rel. etalon updates.
     */
    protected final List<EtalonRelationPO> etalonUpdates;
    /**
     * Collected rel. origin updates.
     */
    protected final List<OriginRelationPO> originUpdates;
    /**
     * Collected rel. vistory records.
     */
    protected final List<OriginsVistoryRelationsPO> vistory;
    /**
     * Index updates.
     */
    protected final List<IndexRequestContext> indexUpdates;
    /**
     * Constructor.
     * @param commitSize
     * @param targets
     */
    protected AbstractRelationBatchSetAccumulator(int commitSize, Map<BatchTarget, String> targets) {
        super(commitSize, targets);
        this.etalonUpdates = new ArrayList<>(commitSize);
        this.originUpdates = new ArrayList<>(commitSize);
        this.vistory = new ArrayList<>(commitSize);
        this.indexUpdates = new ArrayList<>(commitSize);
    }
    /**
     * @return the collectedEtalonUpdates
     */
    public List<EtalonRelationPO> getEtalonUpdates() {
        return etalonUpdates;
    }
    /**
     * @return the collectedOriginUpdates
     */
    public List<OriginRelationPO> getOriginUpdates() {
        return originUpdates;
    }
    /**
     * @return the collectedVistory
     */
    public List<OriginsVistoryRelationsPO> getVistory() {
        return vistory;
    }
    /**
     * @return the indexUpdates
     */
    public List<IndexRequestContext> getIndexUpdates() {
        return indexUpdates;
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
}
