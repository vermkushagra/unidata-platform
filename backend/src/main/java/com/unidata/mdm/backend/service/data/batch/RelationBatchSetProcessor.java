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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.dao.RelationsDao;
import com.unidata.mdm.backend.service.search.SearchServiceExt;

/**
 * @author Mikhail Mikhailov
 * Relation batch set processor.
 */
@Component("relationBatchSetProcessor")
public class RelationBatchSetProcessor {
    /**
     * Relations vistory DAO.
     */
    @Autowired
    private RelationsDao relationsVistoryDao;
    /**
     * Search service.
     */
    @Autowired
    private SearchServiceExt searchServiceExt;
    /**
     * Records batch set processor.
     */
    @Autowired
    private RecordBatchSetProcessor recordBatchSetProcessor;
    /**
     * Applies accumulated origin updates.
     * @param accumulator the accumulator
     */
    public void applyOrigins(BatchSetAccumulator<?> bsa) {

        if (bsa.getSupportedIterationTypes().contains(BatchSetIterationType.DELETE_ORIGINS)) {

            RelationDeleteBatchSetAccumulator accumulator = (RelationDeleteBatchSetAccumulator) bsa;

            // 1. Containments
            RecordDeleteBatchSetAccumulator rbsa = accumulator.getRecordBatchSetAccumulator();
            if (Objects.nonNull(rbsa)) {
                recordBatchSetProcessor.applyOrigins(rbsa);
            }

            // 2. Relations
            applyDeleteOriginBatchSet(accumulator);
        } else if (bsa.getSupportedIterationTypes().contains(BatchSetIterationType.UPSERT_ORIGINS)) {

            RelationUpsertBatchSetAccumulator accumulator = (RelationUpsertBatchSetAccumulator) bsa;

            // 1. Containments
            RecordUpsertBatchSetAccumulator rbsa = accumulator.getRecordBatchSetAccumulator();
            if (Objects.nonNull(rbsa)) {
                recordBatchSetProcessor.applyOrigins(rbsa);
            }

            // 2. Relations
            applyUpsertOriginBatchSet(accumulator);
        }
    }
    /**
     * Applies accumulated etalon updates.
     * @param accumulator the accumulator.
     */
    public void applyEtalons(BatchSetAccumulator<?> bsa) {

        // 1. Upserts
        List<IndexRequestContext> updates = null;
        if (bsa.getSupportedIterationTypes().contains(BatchSetIterationType.UPSERT_ETALONS)) {
            RelationUpsertBatchSetAccumulator accumulator = (RelationUpsertBatchSetAccumulator) bsa;
            RecordUpsertBatchSetAccumulator rbsa = accumulator.getRecordBatchSetAccumulator();
            updates =  rbsa != null
                    ? new ArrayList<>(rbsa.getIndexUpdates())
                    : new ArrayList<>();

            Collections.addAll(updates,
                    Arrays.copyOf(
                            accumulator.getIndexUpdates().toArray(),
                            accumulator.getIndexUpdates().size(),
                            IndexRequestContext[].class));


        } else if (bsa.getSupportedIterationTypes().contains(BatchSetIterationType.DELETE_ETALONS)) {
            RelationDeleteBatchSetAccumulator accumulator = (RelationDeleteBatchSetAccumulator) bsa;
            RecordDeleteBatchSetAccumulator rbsa = accumulator.getRecordBatchSetAccumulator();
            updates =  rbsa != null
                    ? new ArrayList<>(rbsa.getIndexUpdates())
                    : new ArrayList<>();

            Collections.addAll(updates,
                    Arrays.copyOf(
                            accumulator.getIndexUpdates().toArray(),
                            accumulator.getIndexUpdates().size(),
                            IndexRequestContext[].class));
        }

        if (CollectionUtils.isNotEmpty(updates)) {
            searchServiceExt.index(updates);
        }
    }
    /**
     * Applies upsert contexts.
     * @param accumulator the accumulator, holding the contexts
     */
    private void applyUpsertOriginBatchSet(RelationUpsertBatchSetAccumulator accumulator) {
        applyEtalonsDBInserts(accumulator);
        applyEtalonsDBUpdates(accumulator);
        applyOriginsDBInserts(accumulator);
        applyOriginsDBUpdates(accumulator);
        applyVistoryDBUpdates(accumulator);
    }

    /**
     * Applies delete contexts.
     * @param accumulator the accumaltor, holding the contexts
     */
    private void applyDeleteOriginBatchSet(RelationDeleteBatchSetAccumulator accumulator) {
        applyEtalonsDBUpdates(accumulator);
        applyOriginsDBUpdates(accumulator);
        applyVistoryDBUpdates(accumulator);
    }

    /**
     * Applies etalos inserts
     * @param accumulator the accumulator
     */
    private void applyEtalonsDBInserts(RelationUpsertBatchSetAccumulator accumulator) {

        if (!accumulator.getEtalonInserts().isEmpty()) {
            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                relationsVistoryDao.bulkInsertEtalonRecords(
                        accumulator.getEtalonInserts(),
                        accumulator.getTargets().get(BatchTarget.ETALON_INSERTS));
            } else {
                relationsVistoryDao.upsertEtalonRelations(accumulator.getEtalonInserts(), true);
            }
        }
    }

    /**
     * Applies origins inserts.
     * @param accumulator the accumulator
     */
    private void applyOriginsDBInserts(RelationUpsertBatchSetAccumulator accumulator) {
        if (!accumulator.getOriginInserts().isEmpty()) {
            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                relationsVistoryDao.bulkInsertOriginRecords(
                        accumulator.getOriginInserts(),
                        accumulator.getTargets().get(BatchTarget.ORIGIN_INSERTS));
            } else {
                relationsVistoryDao.upsertOriginRelations(accumulator.getOriginInserts(), true);
            }
        }
    }

    /**
     * Applies etalons updates.
     * @param accumulator the accumulator
     */
    private void applyEtalonsDBUpdates(AbstractRelationBatchSetAccumulator<?> accumulator) {
        if (!accumulator.getEtalonUpdates().isEmpty()) {
            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                relationsVistoryDao.bulkUpdateEtalonRecords(
                        accumulator.getEtalonUpdates(),
                        accumulator.getTargets().get(BatchTarget.ETALON_UPDATES));
            } else {
                relationsVistoryDao.upsertEtalonRelations(accumulator.getEtalonUpdates(), false);
            }
        }
    }

    /**
     * Applies origins updates.
     * @param accumulator the accumulator
     */
    private void applyOriginsDBUpdates(AbstractRelationBatchSetAccumulator<?> accumulator) {
        if (!accumulator.getOriginUpdates().isEmpty()) {
            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                relationsVistoryDao.bulkUpdateOriginRecords(
                        accumulator.getOriginUpdates(),
                        accumulator.getTargets().get(BatchTarget.ORIGIN_UPDATES));
            } else {
                relationsVistoryDao.upsertOriginRelations(accumulator.getOriginUpdates(), false);
            }
        }
    }

    /**
     * Applies vistory updates.
     * @param accumulator the accumulator
     */
    private void applyVistoryDBUpdates(AbstractRelationBatchSetAccumulator<?> accumulator) {
        if (!accumulator.getVistory().isEmpty()) {
            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                relationsVistoryDao.bulkInsertVersions(
                        accumulator.getVistory(),
                        accumulator.getTargets().get(BatchTarget.VISTORY));
            } else {
                relationsVistoryDao.putVersions(accumulator.getVistory());
            }
        }
    }
}
