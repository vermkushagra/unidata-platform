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
import org.unidata.mdm.data.dao.RelationsDao;
import org.unidata.mdm.data.po.EtalonRelationDraftStatePO;
import org.unidata.mdm.data.po.data.RelationEtalonPO;
import org.unidata.mdm.data.po.data.RelationEtalonRemapFromPO;
import org.unidata.mdm.data.po.data.RelationEtalonRemapToPO;
import org.unidata.mdm.data.po.data.RelationOriginPO;
import org.unidata.mdm.data.po.data.RelationOriginRemapPO;
import org.unidata.mdm.data.po.data.RelationVistoryPO;
import org.unidata.mdm.data.po.keys.RelationExternalKeyPO;
import org.unidata.mdm.data.po.keys.RelationKeysPO;
import org.unidata.mdm.data.service.RelationChangeSetProcessor;
import org.unidata.mdm.data.type.apply.RelationDeleteChangeSet;
import org.unidata.mdm.data.type.apply.RelationMergeChangeSet;
import org.unidata.mdm.data.type.apply.RelationUpsertChangeSet;

/**
 * @author Mikhail Mikhailov
 * Applies change sets via DAO instances.
 */
public class RelationChangeSetProcessorImpl extends AbstractChangeSetProcessor implements RelationChangeSetProcessor {
    /**
     * Relations vistory DAO.
     */
    @Autowired
    protected RelationsDao relationsDao;
    /**
     * Constructor.
     */
    public RelationChangeSetProcessorImpl() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void apply(RelationUpsertChangeSet set) {

        if (Objects.isNull(set)) {
            return;
        }

        if (set.getEtalonRelationInsertPO() != null) {
            applyInsertEtalons(Collections.singletonList(set.getEtalonRelationInsertPO()));
        }

        if (CollectionUtils.isNotEmpty(set.getExternalKeyInsertPOs())) {
            applyInsertFromKeys(set.getExternalKeyInsertPOs());
            applyInsertToKeys(set.getExternalKeyInsertPOs());
        }

        if (CollectionUtils.isNotEmpty(set.getEtalonRelationUpdatePOs())) {
            applyUpdateEtalons(set.getEtalonRelationUpdatePOs());
        }

        if (CollectionUtils.isNotEmpty(set.getOriginRelationInsertPOs())) {
            applyInsertOrigins(set.getOriginRelationInsertPOs());
        }

        if (CollectionUtils.isNotEmpty(set.getOriginRelationUpdatePOs())) {
            applyUpdateOrigins(set.getOriginRelationUpdatePOs());
        }

        if (CollectionUtils.isNotEmpty(set.getEtalonRelationDraftStatePOs())) {
            applyDraftStates(set.getEtalonRelationDraftStatePOs());
        }

        if (CollectionUtils.isNotEmpty(set.getOriginsVistoryRelationsPOs())) {
            applyInsertVistory(set.getOriginsVistoryRelationsPOs());
        }

        if (CollectionUtils.isNotEmpty(set.getIndexRequestContexts())) {
            applyIndexUpdates(set.getIndexRequestContexts(), true);
        }

        set.clear();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void apply(RelationDeleteChangeSet set) {

        if (Objects.isNull(set)) {
            return;
        }

        if (CollectionUtils.isNotEmpty(set.getEtalonRelationUpdatePOs())) {
            applyUpdateEtalons(set.getEtalonRelationUpdatePOs());
        }

        if (CollectionUtils.isNotEmpty(set.getOriginRelationUpdatePOs())) {
            applyUpdateOrigins(set.getOriginRelationUpdatePOs());
        }

        if (CollectionUtils.isNotEmpty(set.getEtalonRelationDraftStatePOs())) {
            applyDraftStates(set.getEtalonRelationDraftStatePOs());
        }

        if (CollectionUtils.isNotEmpty(set.getOriginsVistoryRelationsPOs())) {
            applyInsertVistory(set.getOriginsVistoryRelationsPOs());
        }

        if (CollectionUtils.isNotEmpty(set.getWipeRelationKeys())) {
            applyWipeRelationData(set.getWipeRelationKeys());
        }

        if (CollectionUtils.isNotEmpty(set.getWipeExternalKeys())) {
            applyWipeFromExternalKeys(set.getWipeExternalKeys());
            applyWipeToExternalKeys(set.getWipeExternalKeys());
        }

        if (CollectionUtils.isNotEmpty(set.getIndexRequestContexts())) {
            applyIndexUpdates(set.getIndexRequestContexts(), true);
        }

        set.clear();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void apply(RelationMergeChangeSet set) {

        if (Objects.isNull(set)) {
            return;
        }

        if (CollectionUtils.isNotEmpty(set.getEtalonUpdates())) {
            applyUpdateEtalons(set.getEtalonUpdates());
        }

        if (CollectionUtils.isNotEmpty(set.getEtalonFromRemaps())) {
            applyRemapEtalonFromRecords(set.getEtalonFromRemaps());
        }

        if (CollectionUtils.isNotEmpty(set.getEtalonToRemaps())) {
            applyRemapEtalonToRecords(set.getEtalonToRemaps());
        }

        if (CollectionUtils.isNotEmpty(set.getOriginRemaps())) {
            applyRemapOriginRecords(set.getOriginRemaps());
        }

        if (CollectionUtils.isNotEmpty(set.getExternalKeyWipes())) {
            applyWipeFromExternalKeys(set.getExternalKeyWipes());
            applyWipeToExternalKeys(set.getExternalKeyWipes());
        }

        if (CollectionUtils.isNotEmpty(set.getExternalKeyInserts())) {
            applyInsertFromKeys(set.getExternalKeyInserts());
            applyInsertToKeys(set.getExternalKeyInserts());
        }

        if (CollectionUtils.isNotEmpty(set.getIndexRequestContexts())) {
            applyIndexUpdates(set.getIndexRequestContexts(), true);
        }

        set.clear();
    }
    /**
     * Applies bulk etalon to merge.
     * @param shard the target shard number
     * @param pos the PO objects to merge
     */
    protected void applyBulkRemapEtalonToRecords(int shard, List<RelationEtalonRemapToPO> pos) {
        relationsDao.bulkRemapEtalonToRecords(shard, pos);
    }
    /**
     * Applies bulk etalon from merge.
     * @param shard the target shard number
     * @param pos the PO objects to merge
     */
    protected void applyBulkRemapEtalonFromRecords(int shard, List<RelationEtalonRemapFromPO> pos) {
        relationsDao.bulkRemapEtalonFromRecords(shard, pos);
    }
    /**
     * Applies bulk origin remap.
     * @param shard the target shard number
     * @param pos the PO objects to merge
     */
    protected void applyBulkRemapOriginRecords(int shard, List<RelationOriginRemapPO> pos) {
        relationsDao.bulkRemapOriginRecords(shard, pos);
    }
    /**
     * Applies bulk etalon insert.
     * @param shard the shard number
     * @param pos the PO objects to insert
     */
    protected void applyBulkInsertEtalons(int shard, List<RelationEtalonPO> pos) {
        relationsDao.bulkInsertEtalonRecords(shard, pos);
    }
    /**
     * Applies bulk etalon update.
     * @param shard  the shard number
     * @param pos the PO objects to update
     */
    protected void applyBulkUpdateEtalons(int shard, List<RelationEtalonPO> pos) {
        relationsDao.bulkUpdateEtalonRecords(shard, pos);
    }
    /**
     * Applies bulk origin insert.
     * @param shard  the shard number
     * @param pos the PO objects to insert
     */
    protected void applyBulkInsertOrigins(int shard, List<RelationOriginPO> pos) {
        relationsDao.bulkInsertOriginRecords(shard, pos);
    }
    /**
     * Applies bulk origin update.
     * @param shard the shard number
     * @param pos the PO objects to update
     */
    protected void applyBulkUpdateOrigins(int shard, List<RelationOriginPO> pos) {
        relationsDao.bulkUpdateOriginRecords(shard, pos);
    }
    /**
     * Applies bulk from keys.
     * @param shard the shard number
     * @param pos the PO objects to update
     */
    protected void applyBulkInsertFromKeys(int shard, List<RelationExternalKeyPO> pos) {
        relationsDao.bulkInsertFromExternalKeys(shard, pos);
    }
    /**
     * Applies bulk from keys.
     * @param shard the shard number
     * @param pos the PO objects to update
     */
    protected void applyBulkInsertToKeys(int shard, List<RelationExternalKeyPO> pos) {
        relationsDao.bulkInsertToExternalKeys(shard, pos);
    }
    /**
     * Applies bulk vistory update.
     * @param shard the shard number
     * @param pos the PO objects to update
     */
    protected void applyBulkInsertVistory(int shard, List<RelationVistoryPO> pos) {
        relationsDao.bulkInsertVersions(shard, pos);
    }
    /**
     * Applies reldata wipe.
     * @param shard the shard number
     * @param ids the ids
     */
    protected void applyBulkWipeRelationData(int shard, List<RelationKeysPO> ids) {
        relationsDao.bulkWipeRelationData(shard, ids);
    }
    /**
     * Applies from ext. keys wipe.
     * @param shard the shard number
     * @param ids the ids
     */
    protected void applyBulkWipeFromExternalKeys(int shard, List<RelationExternalKeyPO> ids) {
        relationsDao.bulkWipeFromExternalKeys(shard, ids);
    }
    /**
     * Applies to ext. keys wipe.
     * @param shard the shard number
     * @param ids the ids
     */
    protected void applyBulkWipeToExternalKeys(int shard, List<RelationExternalKeyPO> ids) {
        relationsDao.bulkWipeToExternalKeys(shard, ids);
    }
    /**
     * Applies normal batch etalon insert.
     * @param pos the PO objects to insert
     */
    protected void applyInsertEtalons(List<RelationEtalonPO> pos) {
        relationsDao.upsertEtalonRelations(pos, true);
    }
    /**
     * Applies normal batch etalon update.
     * @param pos the PO objects to update
     */
    protected void applyUpdateEtalons(List<RelationEtalonPO> pos) {
        relationsDao.upsertEtalonRelations(pos, false);
    }
    /**
     * Applies etalon from merge.
     * @param pos the PO objects to merge
     */
    protected void applyRemapEtalonFromRecords(List<RelationEtalonRemapFromPO> pos) {
        relationsDao.remapEtalonFromRecords(pos);
    }
    /**
     * Applies etalon to merge.
     * @param pos the PO objects to merge
     */
    protected void applyRemapEtalonToRecords(List<RelationEtalonRemapToPO> pos) {
        relationsDao.remapEtalonToRecords(pos);
    }
    /**
     * Applies origin remap.
     * @param pos the PO objects to merge
     */
    protected void applyRemapOriginRecords(List<RelationOriginRemapPO> pos) {
        relationsDao.remapOriginRecords(pos);
    }
    /**
     * Applies normal batch origin insert.
     * @param pos the PO objects to insert
     */
    protected void applyInsertOrigins(List<RelationOriginPO> pos) {
        relationsDao.upsertOriginRelations(pos, true);
    }
    /**
     * Applies normal batch origin update.
     * @param pos the PO objects to update
     */
    protected void applyUpdateOrigins(List<RelationOriginPO> pos) {
        relationsDao.upsertOriginRelations(pos, false);
    }
    /**
     * Applies normal batch vistory update.
     * @param pos the PO objects to update
     */
    protected void applyInsertVistory(List<RelationVistoryPO> pos) {
        relationsDao.upsertVersions(pos);
    }
    /**
     * Applies normal batch etalon insert.
     * @param pos the PO objects to insert
     */
    protected void applyInsertFromKeys(List<RelationExternalKeyPO> pos) {
        relationsDao.upsertFromExternalKeys(pos, true);
    }
    /**
     * Applies normal batch etalon insert.
     * @param pos the PO objects to insert
     */
    protected void applyInsertToKeys(List<RelationExternalKeyPO> pos) {
        relationsDao.upsertToExternalKeys(pos, true);
    }
    /**
     * Applies etalons updates.
     * @param accumulator the accumulator
     */
    protected void applyWipeRelationData(List<RelationKeysPO> ids) {
        relationsDao.wipeRelationData(ids);
    }
    /**
     * Applies wipe ext. keys.
     * @param ids the ids
     */
    protected void applyWipeFromExternalKeys(List<RelationExternalKeyPO> ids) {
        relationsDao.wipeFromExternalKeys(ids);
    }
    /**
     * Applies wipe ext. keys.
     * @param ids the ids
     */
    protected void applyWipeToExternalKeys(List<RelationExternalKeyPO> ids) {
        relationsDao.wipeToExternalKeys(ids);
    }
    /**
     * Applies draft states.
     * @param drafts the drafts
     */
    protected void applyDraftStates(List<EtalonRelationDraftStatePO> drafts) {
        relationsDao.upsertEtalonStateDraft(drafts);
    }
}
