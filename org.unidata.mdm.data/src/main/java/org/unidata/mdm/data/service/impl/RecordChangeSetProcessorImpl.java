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

package org.unidata.mdm.data.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.unidata.mdm.core.dao.LargeObjectsDao;
import org.unidata.mdm.core.po.LargeObjectPO;
import org.unidata.mdm.data.dao.DataRecordsDao;
import org.unidata.mdm.data.po.EtalonRecordDraftStatePO;
import org.unidata.mdm.data.po.data.RecordEtalonPO;
import org.unidata.mdm.data.po.data.RecordOriginPO;
import org.unidata.mdm.data.po.data.RecordOriginRemapPO;
import org.unidata.mdm.data.po.data.RecordVistoryPO;
import org.unidata.mdm.data.po.keys.RecordExternalKeysPO;
import org.unidata.mdm.data.po.keys.RecordKeysPO;
import org.unidata.mdm.data.service.RecordChangeSetProcessor;
import org.unidata.mdm.data.service.RelationChangeSetProcessor;
import org.unidata.mdm.data.type.apply.RecordDeleteChangeSet;
import org.unidata.mdm.data.type.apply.RecordMergeChangeSet;
import org.unidata.mdm.data.type.apply.RecordUpsertChangeSet;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov
 * Applies change sets via DAO instances.
 */
public class RecordChangeSetProcessorImpl extends AbstractChangeSetProcessor implements RecordChangeSetProcessor {
    /**
     * Data record DAO.
     */
    @Autowired
    protected DataRecordsDao dataRecordsDao;
    /**
     * LOB DAO.
     */
    @Autowired
    protected LargeObjectsDao largeObjectsDao;
    /**
     * The cluster service.
     */
// @Modules Moved to commercial part.
//    @Autowired
//    protected ClusterService clusterService;
    /**
     * Relation processor.
     */
    @Autowired
    protected RelationChangeSetProcessor relationChangeSetProcessor;
    /**
     * Constructor.
     */
    public RecordChangeSetProcessorImpl() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void apply(RecordUpsertChangeSet set) {

        if (Objects.isNull(set)) {
            return;
        }

        MeasurementPoint.start();
        try {
            if (set.getEtalonRecordInsertPO() != null) {
                applyInsertEtalons(Collections.singletonList(set.getEtalonRecordInsertPO()));
            }

            if (set.getEtalonRecordUpdatePO() != null) {
                applyUpdateEtalons(Collections.singletonList(set.getEtalonRecordUpdatePO()));
            }

            if (CollectionUtils.isNotEmpty(set.getOriginRecordInsertPOs())) {
                applyInsertOrigins(set.getOriginRecordInsertPOs());
            }

            if (CollectionUtils.isNotEmpty(set.getExternalKeysInsertPOs())) {
                applyInsertExternalKeys(set.getExternalKeysInsertPOs());
            }

            if (CollectionUtils.isNotEmpty(set.getOriginRecordUpdatePOs())) {
                applyUpdateOrigins(set.getOriginRecordUpdatePOs());
            }

            if (CollectionUtils.isNotEmpty(set.getEtalonRecordDraftStatePOs())) {
                applyDraftStates(set.getEtalonRecordDraftStatePOs());
            }

            if (CollectionUtils.isNotEmpty(set.getOriginsVistoryRecordPOs())) {
                applyInsertVistory(set.getOriginsVistoryRecordPOs());
            }

            if (CollectionUtils.isNotEmpty(set.getLargeObjectPOs())) {
                applyBinaryData(set.getLargeObjectPOs());
            }

            if (set.getIndexRequestContext() != null) {
                applyIndexUpdate(set.getIndexRequestContext());
            }

            set.clear();
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void apply(RecordDeleteChangeSet set) {

        if (Objects.isNull(set)) {
            return;
        }

        MeasurementPoint.start();
        try {
            if (set.getEtalonRecordUpdatePO() != null) {
                applyUpdateEtalons(Collections.singletonList(set.getEtalonRecordUpdatePO()));
            }

            if (CollectionUtils.isNotEmpty(set.getOriginRecordUpdatePOs())) {
                applyUpdateOrigins(set.getOriginRecordUpdatePOs());
            }

            if (CollectionUtils.isNotEmpty(set.getEtalonRecordDraftStatePOs())) {
                applyDraftStates(set.getEtalonRecordDraftStatePOs());
            }

            if (CollectionUtils.isNotEmpty(set.getOriginsVistoryRecordPOs())) {
                applyInsertVistory(set.getOriginsVistoryRecordPOs());
            }

            if (CollectionUtils.isNotEmpty(set.getWipeRecordKeys())) {
                applyWipeRecordData(set.getWipeRecordKeys());
            }

            if (CollectionUtils.isNotEmpty(set.getWipeExternalKeys())) {
                applyWipeExternalKeys(set.getWipeExternalKeys());
            }

            if (set.getIndexRequestContext() != null) {
                applyIndexUpdates(Collections.singletonList(set.getIndexRequestContext()), true);
            }

            set.clear();
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void apply(RecordMergeChangeSet set) {

        if (Objects.isNull(set)) {
            return;
        }

        MeasurementPoint.start();
        try {
// @Modules Moved to commercial part
//            Map<String, List<ClusterRecord>> existingPreclustering = set.getPreclusteringRecords();
//            if (MapUtils.isNotEmpty(existingPreclustering)) {
//                for (Entry<String, List<ClusterRecord>> entry : existingPreclustering.entrySet()) {
//                    preclusteringService.deleteClusters(entry.getValue(), entry.getKey());
//                }
//            }

            if (CollectionUtils.isNotEmpty(set.getRecordOriginRemapPOs())) {
                applyRemapOrigins(set.getRecordOriginRemapPOs());
            }

            if (CollectionUtils.isNotEmpty(set.getRecordExternalKeysUpdatePOs())) {
                applyUpdateExternalKeys(set.getRecordExternalKeysUpdatePOs());
            }

            if (CollectionUtils.isNotEmpty(set.getRecordEtalonMergePOs())) {
// @Modules Moved to commercial
//                set.getRecordEtalonMergePOs().stream()
//                        .collect(Collectors.groupingBy(RecordEtalonPO::getName))
//                        .forEach((s, etalonRecordPOS) ->
//                                clusterService.excludeFromClusters(s, etalonRecordPOS.stream()
//                                        .map(RecordEtalonPO::getId)
//                                        .collect(Collectors.toList())));

                applyUpdateEtalons(set.getRecordEtalonMergePOs());
            }

            if (CollectionUtils.isNotEmpty(set.getIndexRequestContexts())) {
                applyIndexUpdates(set.getIndexRequestContexts(), true);
            }

            set.clear();
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Applies bulk etalon insert.
     * @param shard  the shard number
     * @param pos the PO objects to insert
     */
    protected void applyBulkInsertEtalons(int shard, List<RecordEtalonPO> pos) {
        dataRecordsDao.bulkInsertEtalonRecords(shard, pos);
    }
    /**
     * Applies bulk etalon update.
     * @param shard the shard number
     * @param pos the PO objects to update
     */
    protected void applyBulkUpdateEtalons(int shard, List<RecordEtalonPO> pos) {
        dataRecordsDao.bulkUpdateEtalonRecords(shard, pos);
    }
    /**
     * Applies bulk origin insert.
     * @param shard the shard number
     * @param pos the PO objects to insert
     */
    protected void applyBulkInsertOrigins(int shard, List<RecordOriginPO> pos) {
        dataRecordsDao.bulkInsertOriginRecords(shard, pos);
    }
    /**
     * Applies bulk origin update.
     * @param shard  the shard number
     * @param pos the PO objects to update
     */
    protected void applyBulkUpdateOrigins(int shard, List<RecordOriginPO> pos) {
        dataRecordsDao.bulkUpdateOriginRecords(shard, pos);
    }
    /**
     * Applies bulk origin merge (remap).
     * @param shard  the shard number
     * @param pos the PO objects to update
     */
    protected void applyBulkRemapOrigins(int shard, List<RecordOriginRemapPO> pos) {
        dataRecordsDao.bulkRemapOriginRecords(shard, pos);
    }
    /**
     * Applies bulk origin insert.
     * @param shard the shard number
     * @param pos the PO objects to insert
     */
    protected void applyBulkInsertExternalKeys(int shard, List<RecordExternalKeysPO> pos) {
        dataRecordsDao.bulkInsertExternalKeys(shard, pos);
    }
    /**
     * Applies bulk update external keys.
     * @param shard the shard
     * @param pos the PO objects to update
     */
    protected void applyBulkUpdateExternalKeys(int shard, List<RecordExternalKeysPO> pos) {
        dataRecordsDao.bulkUpdateExternalKeys(shard, pos);
    }
    /**
     * Applies bulk vistory update.
     * @param shard the shard number
     * @param pos the PO objects to update
     */
    protected void applyBulkInsertVistory(int shard, List<RecordVistoryPO> pos) {
        dataRecordsDao.bulkInsertVersions(shard, pos);
    }
    /**
     * Wipes records and records data.
     * @param shard the shard number
     * @param ids the record ids
     */
    protected void applyBulkWipeRecordData(int shard, List<RecordKeysPO> ids) {
        dataRecordsDao.bulkWipeRecordData(shard, ids);
    }
    /**
     * Wipes external ids.
     * @param shard the shard number
     * @param ids the list of ids to wipe
     */
    protected void applyBulkWipeExternalKeys(int shard, List<RecordExternalKeysPO> ids) {
        dataRecordsDao.bulkWipeExternalKeys(shard, ids);
    }
    /**
     * Applies normal batch etalon insert.
     * @param pos the PO objects to insert
     */
    protected void applyInsertEtalons(List<RecordEtalonPO> pos) {
        dataRecordsDao.upsertEtalonRecords(pos, true);
    }
    /**
     * Applies normal batch etalon update.
     * @param pos the PO objects to update
     */
    protected void applyUpdateEtalons(List<RecordEtalonPO> pos) {
        dataRecordsDao.upsertEtalonRecords(pos, false);
    }
    /**
     * Applies normal batch origin insert.
     * @param pos the PO objects to insert
     */
    protected void applyInsertOrigins(List<RecordOriginPO> pos) {
        dataRecordsDao.upsertOriginRecords(pos, true);
    }
    /**
     * Applies normal batch origin update.
     * @param pos the PO objects to update
     */
    protected void applyUpdateOrigins(List<RecordOriginPO> pos) {
        dataRecordsDao.upsertOriginRecords(pos, false);
    }
    /**
     * Applies normal origin merge (remap).
     * @param pos the PO objects to update
     */
    protected void applyRemapOrigins(List<RecordOriginRemapPO> pos) {
        dataRecordsDao.remapOriginRecords(pos);
    }
    /**
     * Applies normal batch ext keys insert.
     * @param pos the PO objects to insert
     */
    protected void applyInsertExternalKeys(List<RecordExternalKeysPO> pos) {
        dataRecordsDao.upsertExternalKeys(pos, true);
    }
    /**
     * Applies normal batch origin update.
     * @param pos the PO objects to update
     */
    protected void applyUpdateExternalKeys(List<RecordExternalKeysPO> pos) {
        dataRecordsDao.upsertExternalKeys(pos, false);
    }
    /**
     * Applies LOB data activations.
     * @param pos the PO objects to update
     */
    protected void applyBinaryData(List<LargeObjectPO> pos) {
        largeObjectsDao.activateLargeObjects(pos);
    }
    /**
     * Applies normal batch vistory update.
     * @param pos the PO objects to update
     */
    protected void applyInsertVistory(List<RecordVistoryPO> pos) {
        dataRecordsDao.upsertVersions(pos);
    }
    /**
     * Applies etalons updates.
     * @param accumulator the accumulator
     */
    protected void applyWipeRecordData(List<RecordKeysPO> ids) {
        dataRecordsDao.wipeRecordData(ids);
    }
    /**
     * Applies etalons updates.
     * @param ids the ids
     */
    protected void applyWipeExternalKeys(List<RecordExternalKeysPO> ids) {
        dataRecordsDao.wipeExternalIds(ids);
    }
    /**
     * Applies draft states.
     * @param drafts the drafts
     */
    protected void applyDraftStates(List<EtalonRecordDraftStatePO> drafts) {
        dataRecordsDao.upsertEtalonStateDraft(drafts);
    }
}
