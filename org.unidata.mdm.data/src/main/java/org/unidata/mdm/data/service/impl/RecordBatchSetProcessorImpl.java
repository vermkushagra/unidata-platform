package org.unidata.mdm.data.service.impl;

import java.util.List;
import java.util.Map.Entry;

// import com.unidata.mdm.classifier.batch.ClassifierBatchSetProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.unidata.mdm.data.po.data.RecordEtalonPO;
import org.unidata.mdm.data.po.data.RecordOriginPO;
import org.unidata.mdm.data.po.data.RecordOriginRemapPO;
import org.unidata.mdm.data.po.data.RecordVistoryPO;
import org.unidata.mdm.data.po.keys.RecordExternalKeysPO;
import org.unidata.mdm.data.po.keys.RecordKeysPO;
import org.unidata.mdm.data.service.RecordBatchSetProcessor;
import org.unidata.mdm.data.service.RelationBatchSetProcessor;
import org.unidata.mdm.data.type.apply.batch.impl.AbstractRecordBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RecordDeleteBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RecordMergeBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RecordUpsertBatchSetAccumulator;
import org.unidata.mdm.system.type.batch.BatchSetSize;

// import com.unidata.mdm.backend.common.matching.ClusterRecord;

/**
 * @author Mikhail Mikhailov
 * Simple batch set processor.
 */
@Component("recordBatchSetProcessor")
public class RecordBatchSetProcessorImpl extends RecordChangeSetProcessorImpl implements RecordBatchSetProcessor {
    /**
     * Classifier processor.
     */
// @Modules
//    @Autowired
//    protected ClassifierBatchSetProcessor classifierBatchSetProcessor;
    /**
     * Relations processor.
     */
    @Autowired
    protected RelationBatchSetProcessor relationBatchSetProcessor;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(RecordUpsertBatchSetAccumulator bsa) {

        applyInsertEtalons(bsa);
        applyUpdateEtalons(bsa);
        applyInsertOrigins(bsa);
        applyUpdateOrigins(bsa);
        applyInsertVistory(bsa);
        applyInsertExternalKeys(bsa);

        if (!bsa.getIndexUpdates().isEmpty()) {
            applyIndexUpdates(bsa.getIndexUpdates(), false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(RecordDeleteBatchSetAccumulator bsa) {

        applyUpdateEtalons(bsa);
        applyUpdateOrigins(bsa);
        applyInsertVistory(bsa);
        applyWipeRecordKeys(bsa);
        applyWipeExternalKeys(bsa);

        if (!bsa.getIndexUpdates().isEmpty()) {
            applyIndexUpdates(bsa.getIndexUpdates(), false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(RecordMergeBatchSetAccumulator bsa) {

        applyRemapOrigins(bsa);
        applyUpdateExternalKeys(bsa);
        applyUpdateEtalons(bsa);
// @Modules
//        Stream.concat(
//                    bsa.getEtalonWinners().stream(),
//                    bsa.getEtalonUpdates().values().stream().flatMap(Collection::stream))
//                .collect(Collectors.groupingBy(RecordEtalonPO::getName))
//                .forEach((s, etalonRecordPOS) ->
//                        clusterService.excludeFromClusters(s, etalonRecordPOS.stream()
//                                .map(RecordEtalonPO::getId)
//                                .collect(Collectors.toList())));
//
//        if (MapUtils.isNotEmpty(bsa.getPreclustringRecordsForDelete())) {
//            for (Map.Entry<String, List<ClusterRecord>> entry : bsa.getPreclustringRecordsForDelete().entrySet()) {
//                preclusteringService.deleteClusters(entry.getValue(), entry.getKey());
//            }
//        }

        if (!bsa.getIndexUpdates().isEmpty()) {
            applyIndexUpdates(bsa.getIndexUpdates(), false);
        }

        relationBatchSetProcessor.apply(bsa.getRelationBatchSetAccumulator());
// @Modules
//        classifierBatchSetProcessor.apply(bsa.getClassifierBatchSetAccumulator());
    }

    /**
     * Applies etalos inserts
     * @param accumulator the accumulator
     */
    private void applyInsertEtalons(RecordUpsertBatchSetAccumulator accumulator) {

        for (Entry<Integer, List<RecordEtalonPO>> entry : accumulator.getEtalonInserts().entrySet()) {

            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkInsertEtalons(entry.getKey(), entry.getValue());
            } else {
                applyInsertEtalons(entry.getValue());
            }
        }
    }

    /**
     * Applies origins inserts.
     * @param accumulator the accumulator
     */
    private void applyInsertOrigins(RecordUpsertBatchSetAccumulator accumulator) {

        for (Entry<Integer, List<RecordOriginPO>> entry : accumulator.getOriginInserts().entrySet()) {

            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkInsertOrigins(entry.getKey(), entry.getValue());
            } else {
                applyInsertOrigins(entry.getValue());
            }
        }
    }

    /**
     * Applies origins inserts.
     * @param accumulator the accumulator
     */
    private void applyInsertExternalKeys(RecordUpsertBatchSetAccumulator accumulator) {

        for (Entry<Integer, List<RecordExternalKeysPO>> entry : accumulator.getExternalKeysInserts().entrySet()) {

            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkInsertExternalKeys(entry.getKey(), entry.getValue());
            } else {
                applyInsertExternalKeys(entry.getValue());
            }
        }
    }

    /**
     * Applies etalons updates.
     * @param accumulator the accumulator
     */
    private void applyUpdateEtalons(AbstractRecordBatchSetAccumulator<?, ?> accumulator) {

        for (Entry<Integer, List<RecordEtalonPO>> entry : accumulator.getEtalonUpdates().entrySet()) {

            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkUpdateEtalons(entry.getKey(), entry.getValue());
            } else {
                applyUpdateEtalons(entry.getValue());
            }
        }
    }

    /**
     * Applies origins updates.
     * @param accumulator the accumulator
     */
    private void applyUpdateOrigins(AbstractRecordBatchSetAccumulator<?, ?> accumulator) {

        for (Entry<Integer, List<RecordOriginPO>> entry : accumulator.getOriginUpdates().entrySet()) {

            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkUpdateOrigins(entry.getKey(), entry.getValue());
            } else {
                applyUpdateOrigins(entry.getValue());
            }
        }
    }

    /**
     * Applies origins updates.
     * @param accumulator the accumulator
     */
    private void applyRemapOrigins(RecordMergeBatchSetAccumulator accumulator) {

        for (Entry<Integer, List<RecordOriginRemapPO>> entry : accumulator.getOriginRemaps().entrySet()) {

            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkRemapOrigins(entry.getKey(), entry.getValue());
            } else {
                applyRemapOrigins(entry.getValue());
            }
        }
    }

    /**
     * Applies origins updates.
     * @param accumulator the accumulator
     */
    private void applyUpdateExternalKeys(RecordMergeBatchSetAccumulator accumulator) {

        for (Entry<Integer, List<RecordExternalKeysPO>> entry : accumulator.getExternalKeysUpdates().entrySet()) {

            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkUpdateExternalKeys(entry.getKey(), entry.getValue());
            } else {
                applyUpdateExternalKeys(entry.getValue());
            }
        }
    }

    /**
     * Applies etalons updates.
     * @param accumulator the accumulator
     */
    private void applyWipeExternalKeys(RecordDeleteBatchSetAccumulator accumulator) {

        for (Entry<Integer, List<RecordExternalKeysPO>> entry : accumulator.getWipeExternalKeys().entrySet()) {

            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkWipeExternalKeys(entry.getKey(), entry.getValue());
            } else {
                applyWipeExternalKeys(entry.getValue());
            }
        }
    }

    /**
     * Applies etalons updates.
     * @param accumulator the accumulator
     */
    private void applyWipeRecordKeys(RecordDeleteBatchSetAccumulator accumulator) {

        for (Entry<Integer, List<RecordKeysPO>> entry : accumulator.getWipeRecordKeys().entrySet()) {

            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkWipeRecordData(entry.getKey(), entry.getValue());
            } else {
                applyWipeRecordData(entry.getValue());
            }
        }
    }

    /**
     * Applies vistory updates.
     * @param accumulator the accumulator
     */
    private void applyInsertVistory(AbstractRecordBatchSetAccumulator<?, ?> accumulator) {

        for (Entry<Integer, List<RecordVistoryPO>> entry : accumulator.getVistory().entrySet()) {

            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }

            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                applyBulkInsertVistory(entry.getKey(), entry.getValue());
            } else {
                applyInsertVistory(entry.getValue());
            }
        }
    }
}
