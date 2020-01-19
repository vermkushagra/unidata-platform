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

package org.unidata.mdm.data.type.apply.batch.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.unidata.mdm.data.context.DataContextFlags;
import org.unidata.mdm.data.context.DeleteRelationRequestContext;
import org.unidata.mdm.data.context.DeleteRelationsRequestContext;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.dto.DeleteRelationsDTO;
import org.unidata.mdm.data.po.data.RelationVistoryPO;
import org.unidata.mdm.data.po.keys.RelationKeysPO;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.data.util.StorageUtils;
import org.unidata.mdm.system.type.batch.BatchIterator;

/**
 * @author Mikhail Mikhailov
 * Relation batch set accumulator.
 */
public class RelationDeleteBatchSetAccumulator
    extends AbstractRelationBatchSetAccumulator<DeleteRelationsRequestContext, DeleteRelationsDTO> {
    /**
     * Containments accumulator.
     */
    private final RecordDeleteBatchSetAccumulator recordBatchSetAccumulator;
    /**
     * Relation wipe deletes.
     */
    private final Map<Integer, List<RelationKeysPO>> wipeRelationKeys;
    /**
     * Stats / results.
     */
    private final RelationDeleteBatchSetStatistics statistics;
    /**
     * Constructor.
     * @param commitSize chunk size
     * @param isContainment whether this accumulator processes a containment relation
     * @param skipEtalonPhase skip index updates generation or not
     */
    public RelationDeleteBatchSetAccumulator(int commitSize, boolean isContainment) {

        super(commitSize);

        // Containments
        recordBatchSetAccumulator = isContainment ? new RecordDeleteBatchSetAccumulator(commitSize) : null;
        wipeRelationKeys = new HashMap<>(StorageUtils.numberOfShards());
        statistics = new RelationDeleteBatchSetStatistics();
    }
    /**
     * Adds a single wipe delete update.
     * @param po the update
     */
    protected void accumulateWipeDelete(RelationKeysPO po) {
        if (Objects.nonNull(po)) {
            wipeRelationKeys.computeIfAbsent(po.getShard(), k -> new ArrayList<RelationKeysPO>(commitSize))
                .add(po);
        }
    }
    /**
     * Adds several wipe delete updates.
     * @param pos the update
     */
    protected void accumulateWipeDeletes(List<RelationKeysPO> pos) {
        if (CollectionUtils.isNotEmpty(pos)) {
            for (int i = 0; i < pos.size(); i++) {
                accumulateWipeDelete(pos.get(i));
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public BatchIterator<DeleteRelationsRequestContext> iterator() {
        return new RelationDeleteBatchIterator();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void discharge() {
        super.discharge();
        wipeRelationKeys.values().forEach(Collection::clear);

        // Containments
        if (Objects.nonNull(recordBatchSetAccumulator)) {
            recordBatchSetAccumulator.discharge();
        }
    }
    /**
     * @return the recordBatchSetAccumulator
     */
    public RecordDeleteBatchSetAccumulator getRecordBatchSetAccumulator() {
        return recordBatchSetAccumulator;
    }
    /**
     * @return the wipeDeletes
     */
    public Map<Integer, List<RelationKeysPO>> getWipeRelationKeys() {
        return wipeRelationKeys;
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public RelationDeleteBatchSetStatistics statistics() {
        return statistics;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartTypeId() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @author Mikhail Mikhailov
     * Simple batch iterator.
     */
    private class RelationDeleteBatchIterator implements BatchIterator<DeleteRelationsRequestContext> {
        /**
         * The iterator.
         */
        private ListIterator<DeleteRelationsRequestContext> i = workingCopy.listIterator();
        /**
         * Currently processed contex.
         */
        private DeleteRelationsRequestContext current;
        /**
         * Constructor.
         */
        public RelationDeleteBatchIterator() {
            super();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {

            boolean hasNext = i.hasNext();
            if (!hasNext && current != null) {
                accumulateUpdates(current);
            }

            return hasNext;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public DeleteRelationsRequestContext next() {

            DeleteRelationsRequestContext next = i.next();
            if (current != null) {
                accumulateUpdates(current);
            }

            init(next);

            current = next;
            return next;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void remove() {
            i.remove();
            current = null;
        }

        /**
         * Does some preprocessing.
         * @param ctx the upsert context
         */
        private void init(DeleteRelationsRequestContext ctx) {
            for (Entry<String, List<DeleteRelationRequestContext>> entry : ctx.getRelations().entrySet()) {
                for (DeleteRelationRequestContext dCtx : entry.getValue()) {

                    if (Objects.nonNull(dCtx.changeSet())) {
                        continue;
                    }

                    dCtx.changeSet(new RelationDeleteBatchSet(RelationDeleteBatchSetAccumulator.this));
                    dCtx.setFlag(DataContextFlags.FLAG_BATCH_OPERATION, true);
                }
            }

            ctx.setFlag(DataContextFlags.FLAG_BATCH_OPERATION, true);
        }

        /**
         * Accumulate a batch set after etalon upsert phase.
         * @param ctx the context
         */
        private void accumulateUpdates(DeleteRelationsRequestContext ctx) {

            for (Entry<String,List<DeleteRelationRequestContext>> entry : ctx.getRelations().entrySet()) {
                for (DeleteRelationRequestContext dCtx : entry.getValue()) {

                    RelationDeleteBatchSet batchSet = dCtx.changeSet();
                    if (Objects.isNull(batchSet)) {
                        continue;
                    }

                    accumulateEtalonUpdates(batchSet.getEtalonRelationUpdatePOs());
                    accumulateOriginUpdates(batchSet.getOriginRelationUpdatePOs());

                    RelationKeys keys = dCtx.relationKeys();
                    int currentRevision = keys.getOriginKey().getRevision();
                    for (RelationVistoryPO po : batchSet.getOriginsVistoryRelationsPOs()) {
                        po.setRevision(++currentRevision);
                        accumulateVistory(po);
                    }

                    accumulateWipeDeletes(batchSet.getWipeRelationKeys());
                    accumulateWipeExternalKeys(batchSet.getWipeExternalKeys());

                    DeleteRequestContext containment = dCtx.containmentContext();
                    if (Objects.nonNull(containment) && Objects.nonNull(recordBatchSetAccumulator)) {
                        recordBatchSetAccumulator.accumulateUpdates(containment);
                    }

                    if (Objects.nonNull(batchSet.getIndexRequestContexts())) {
                        indexUpdates.addAll(batchSet.getIndexRequestContexts());
                    }
                }
            }
        }
    }
}
