package org.unidata.mdm.data.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.unidata.mdm.data.po.data.RelationEtalonPO;
import org.unidata.mdm.data.po.data.RelationEtalonRemapFromPO;
import org.unidata.mdm.data.po.data.RelationEtalonRemapToPO;
import org.unidata.mdm.data.po.data.RelationOriginPO;
import org.unidata.mdm.data.po.data.RelationOriginRemapPO;
import org.unidata.mdm.data.po.data.RelationVistoryPO;
import org.unidata.mdm.data.po.keys.RelationExternalKeyPO;
import org.unidata.mdm.data.po.keys.RelationKeysPO;
import org.unidata.mdm.data.service.RecordBatchSetProcessor;
import org.unidata.mdm.data.service.RelationBatchSetProcessor;
import org.unidata.mdm.data.type.apply.batch.impl.AbstractRelationBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RecordDeleteBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RecordUpsertBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RelationDeleteBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RelationMergeBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RelationUpsertBatchSetAccumulator;
import org.unidata.mdm.search.context.IndexRequestContext;
import org.unidata.mdm.system.type.batch.BatchSetSize;

/**
 * @author Mikhail Mikhailov
 * Relation batch set processor.
 */
@Component("relationBatchSetProcessor")
public class RelationBatchSetProcessorImpl extends RelationChangeSetProcessorImpl implements RelationBatchSetProcessor {
    /**
     * Records batch set processor.
     */
    @Autowired
    private RecordBatchSetProcessor recordBatchSetProcessor;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(RelationUpsertBatchSetAccumulator bsa) {

        RecordUpsertBatchSetAccumulator rbsa = bsa.getRecordBatchSetAccumulator();
        List<IndexRequestContext> updates =  rbsa != null
                ? new ArrayList<>(rbsa.getIndexUpdates())
                : new ArrayList<>();

        Collections.addAll(updates,
                Arrays.copyOf(
                        bsa.getIndexUpdates().toArray(),
                        bsa.getIndexUpdates().size(),
                        IndexRequestContext[].class));

        // 1. Contains records
        if (Objects.nonNull(rbsa)) {
            // we already copy index update on upper level
            rbsa.getIndexUpdates().clear();
            recordBatchSetProcessor.apply(rbsa);
        }

        // 2. Relations
        applyInsertEtalons(bsa);
        applyUpdateEtalons(bsa);
        applyInsertOrigins(bsa);
        applyUpdateOrigins(bsa);
        applyInsertVistory(bsa);
        applyInsertExternalKeys(bsa);

        // 3. Indexes
        applyIndexUpdates(updates, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(RelationDeleteBatchSetAccumulator bsa) {

        RecordDeleteBatchSetAccumulator rbsa = bsa.getRecordBatchSetAccumulator();
        List<IndexRequestContext> updates =  rbsa != null
                ? new ArrayList<>(rbsa.getIndexUpdates())
                : new ArrayList<>();

        Collections.addAll(updates,
                Arrays.copyOf(
                        bsa.getIndexUpdates().toArray(),
                        bsa.getIndexUpdates().size(),
                        IndexRequestContext[].class));

        // 1. Contains records
        if (Objects.nonNull(rbsa)) {
            // we already copy index update on upper level
            rbsa.getIndexUpdates().clear();
            recordBatchSetProcessor.apply(rbsa);
        }

        // 2. Relations
        applyUpdateEtalons(bsa);
        applyUpdateOrigins(bsa);
        applyInsertVistory(bsa);
        applyWipeRelationData(bsa);
        applyWipeExternalKeys(bsa);

        // 3. Indexes
        applyIndexUpdates(updates, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(RelationMergeBatchSetAccumulator bsa) {

        // 1. Relations
        applyUpdateEtalons(bsa);
        applyRemapEtalonRecords(bsa);
        applyRemapOriginRecords(bsa);
        applyWipeExternalKeys(bsa);
        applyInsertExternalKeys(bsa);

        // 2. Indexes
        List<IndexRequestContext> updates = bsa.getIndexUpdates();
        applyIndexUpdates(updates, false);
    }
    /**
     * Applies remapping from | to side requests.
     * @param accumulator the accumulator
     */
    private void applyRemapEtalonRecords(RelationMergeBatchSetAccumulator accumulator) {

        for (Entry<Integer, List<RelationEtalonRemapFromPO>> set : accumulator.getEtalonFromRemaps().entrySet()) {

            if (CollectionUtils.isEmpty(set.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkRemapEtalonFromRecords(set.getKey(), set.getValue());
            } else {
                applyRemapEtalonFromRecords(set.getValue());
            }
        }

        for (Entry<Integer, List<RelationEtalonRemapToPO>> set : accumulator.getEtalonToRemaps().entrySet()) {

            if (CollectionUtils.isEmpty(set.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkRemapEtalonToRecords(set.getKey(), set.getValue());
            } else {
                applyRemapEtalonToRecords(set.getValue());
            }
        }
    }
    /**
     * Applies remapping from | to side requests.
     * @param accumulator the accumulator
     */
    private void applyRemapOriginRecords(RelationMergeBatchSetAccumulator accumulator) {

        for (Entry<Integer, List<RelationOriginRemapPO>> set : accumulator.getOriginRemaps().entrySet()) {

            if (CollectionUtils.isEmpty(set.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkRemapOriginRecords(set.getKey(), set.getValue());
            } else {
                applyRemapOriginRecords(set.getValue());
            }
        }
    }
    /**
     * Applies etalos inserts
     * @param accumulator the accumulator
     */
    private void applyInsertEtalons(RelationUpsertBatchSetAccumulator accumulator) {

        for (Entry<Integer, List<RelationEtalonPO>> set : accumulator.getEtalonInserts().entrySet()) {

            if (CollectionUtils.isEmpty(set.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkInsertEtalons(set.getKey(), set.getValue());
            } else {
                applyInsertEtalons(set.getValue());
            }
        }
    }

    /**
     * Applies origins inserts.
     * @param accumulator the accumulator
     */
    private void applyInsertOrigins(RelationUpsertBatchSetAccumulator accumulator) {

        for (Entry<Integer, List<RelationOriginPO>> set : accumulator.getOriginInserts().entrySet()) {

            if (CollectionUtils.isEmpty(set.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkInsertOrigins(set.getKey(), set.getValue());
            } else {
                applyInsertOrigins(set.getValue());
            }
        }
    }

    /**
     * Applies etalons updates.
     * @param accumulator the accumulator
     */
    private void applyUpdateEtalons(AbstractRelationBatchSetAccumulator<?, ?> accumulator) {

        for (List<RelationEtalonPO> set : accumulator.getEtalonUpdates().values()) {

            if (CollectionUtils.isEmpty(set)) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkUpdateEtalons(0, set);
            } else {
                applyUpdateEtalons(set);
            }
        }
    }

    /**
     * Applies origins updates.
     * @param accumulator the accumulator
     */
    private void applyUpdateOrigins(AbstractRelationBatchSetAccumulator<?, ?> accumulator) {

        for (Entry<Integer, List<RelationOriginPO>> set : accumulator.getOriginUpdates().entrySet()) {

            if (CollectionUtils.isEmpty(set.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkUpdateOrigins(set.getKey(), set.getValue());
            } else {
                applyUpdateOrigins(set.getValue());
            }
        }
    }

    /**
     * Applies vistory updates.
     * @param accumulator the accumulator
     */
    private void applyInsertVistory(AbstractRelationBatchSetAccumulator<?, ?> accumulator) {

        for (Entry<Integer, List<RelationVistoryPO>> set : accumulator.getVistory().entrySet()) {

            if (CollectionUtils.isEmpty(set.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkInsertVistory(set.getKey(), set.getValue());
            } else {
                applyInsertVistory(set.getValue());
            }
        }
    }
    /**
     * Applies origins inserts.
     * @param accumulator the accumulator
     */
    private void applyInsertExternalKeys(AbstractRelationBatchSetAccumulator<?, ?> accumulator) {

        for (Entry<Integer, List<RelationExternalKeyPO>> entry : accumulator.getFromExternalKeysInserts().entrySet()) {

            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkInsertFromKeys(entry.getKey(), entry.getValue());
            } else {
                applyInsertFromKeys(entry.getValue());
            }
        }

        for (Entry<Integer, List<RelationExternalKeyPO>> entry : accumulator.getToExternalKeysInserts().entrySet()) {

            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkInsertToKeys(entry.getKey(), entry.getValue());
            } else {
                applyInsertToKeys(entry.getValue());
            }
        }
    }
    /**
     * Applies etalons updates.
     * @param accumulator the accumulator
     */
    private void applyWipeRelationData(RelationDeleteBatchSetAccumulator accumulator) {

        for (Entry<Integer, List<RelationKeysPO>> set : accumulator.getWipeRelationKeys().entrySet()) {

            if (CollectionUtils.isEmpty(set.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkWipeRelationData(set.getKey(), set.getValue());
            } else {
                applyWipeRelationData(set.getValue());
            }
        }
    }
    /**
     * Applies etalons updates.
     * @param accumulator the accumulator
     */
    private void applyWipeExternalKeys(AbstractRelationBatchSetAccumulator<?, ?> accumulator) {

        for (Entry<Integer, List<RelationExternalKeyPO>> set : accumulator.getFromExternalKeysWipes().entrySet()) {

            if (CollectionUtils.isEmpty(set.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkWipeFromExternalKeys(set.getKey(), set.getValue());
            } else {
                applyWipeFromExternalKeys(set.getValue());
            }
        }

        for (Entry<Integer, List<RelationExternalKeyPO>> set : accumulator.getToExternalKeysWipes().entrySet()) {

            if (CollectionUtils.isEmpty(set.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkWipeToExternalKeys(set.getKey(), set.getValue());
            } else {
                applyWipeToExternalKeys(set.getValue());
            }
        }
    }
}
