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
import com.unidata.mdm.backend.common.types.RelationType;
import com.unidata.mdm.backend.po.EtalonRelationPO;
import com.unidata.mdm.backend.po.OriginRelationPO;
import com.unidata.mdm.backend.po.OriginsVistoryRelationsPO;

/**
 * @author Mikhail Mikhailov
 * Simple relation batch set - objects to process a relation.
 */
public final class RelationBatchSet {
    /**
     * Relations accumulator.
     */
    private final AbstractRelationBatchSetAccumulator<?> accumulator;
    /**
     * Etalon relation insert PO.
     */
    private EtalonRelationPO etalonRelationInsertPO;
    /**
     * Etalon relation update PO.
     */
    private EtalonRelationPO etalonRelationUpdatePO;
    /**
     * Origin relation insert POs.
     */
    private List<OriginRelationPO> originRelationInsertPOs = new ArrayList<>(2);
    /**
     * Origin relation update POs.
     */
    private List<OriginRelationPO> originRelationUpdatePOs = new ArrayList<>(2);
    /**
     * Origin relation vistory POs.
     */
    private List<OriginsVistoryRelationsPO> originsVistoryRelationsPOs = new ArrayList<>(3);
    /**
     * Index context.
     */
    private IndexRequestContext indexRequestContext;
    /**
     * Relation type.
     */
    private RelationType relationType;
    public RelationBatchSet(AbstractRelationBatchSetAccumulator<?> accumulator) {
        super();
        this.accumulator = accumulator;
    }
    /**
     * @return the etalonRelationPO
     */
    public EtalonRelationPO getEtalonRelationInsertPO() {
        return etalonRelationInsertPO;
    }
    /**
     * @param etalonRelationPO the etalonRelationPO to set
     */
    public void setEtalonRelationInsertPO(EtalonRelationPO etalonRelationPO) {
        this.etalonRelationInsertPO = etalonRelationPO;
    }
    /**
     * @return the etalonRelationUpdatePO
     */
    public EtalonRelationPO getEtalonRelationUpdatePO() {
        return etalonRelationUpdatePO;
    }
    /**
     * @param etalonRelationUpdatePO the etalonRelationUpdatePO to set
     */
    public void setEtalonRelationUpdatePO(EtalonRelationPO etalonRelationUpdatePO) {
        this.etalonRelationUpdatePO = etalonRelationUpdatePO;
    }
    public boolean isEtalonUpdate() {
        return etalonRelationInsertPO == null && etalonRelationUpdatePO != null;
    }
    public boolean isEtalonInsert() {
        return etalonRelationInsertPO != null && etalonRelationUpdatePO == null;
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
     * @return the relationType
     */
    public RelationType getRelationType() {
        return relationType;
    }
    /**
     * @param relationType the relationType to set
     */
    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }
    /**
     * @return the originRelationPOs
     */
    public List<OriginRelationPO> getOriginRelationInsertPOs() {
        return originRelationInsertPOs;
    }
    /**
     * @return the originRelationUpdatePOs
     */
    public List<OriginRelationPO> getOriginRelationUpdatePOs() {
        return originRelationUpdatePOs;
    }
    /**
     * @return the originsVistoryRelationsPOs
     */
    public List<OriginsVistoryRelationsPO> getOriginsVistoryRelationsPOs() {
        return originsVistoryRelationsPOs;
    }
    /**
     * @return the accumulator
     */
    public AbstractRelationBatchSetAccumulator<?> getRelationsAccumulator() {
        return accumulator;
    }
}
