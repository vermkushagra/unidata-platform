package com.unidata.mdm.backend.service.data.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.dao.DataRecordsDao;
import com.unidata.mdm.backend.dao.OriginsVistoryDao;
import com.unidata.mdm.backend.service.search.SearchServiceExt;

/**
 * @author Mikhail Mikhailov
 * Simple batch set processor.
 */
@Component("recordBatchSetProcessor")
public class RecordBatchSetProcessor {
    /**
     * Origins vistory DAO.
     */
    @Autowired
    private OriginsVistoryDao originsVistoryDao;
    /**
     * Data record DAO.
     */
    @Autowired
    private DataRecordsDao dataRecordsDao;
    /**
     * Search service.
     */
    @Autowired
    private SearchServiceExt searchServiceExt;
    /**
     * Applies accumulated origin updates.
     * @param accumulator the accumulator
     */
    @Transactional(rollbackFor = Exception.class)
    public void applyOrigins(BatchSetAccumulator<?> bsa) {

        if (bsa.getSupportedIterationTypes().contains(BatchSetIterationType.DELETE_ORIGINS)) {
            applyDeleteOriginBatchSet((RecordDeleteBatchSetAccumulator) bsa);
        } else if (bsa.getSupportedIterationTypes().contains(BatchSetIterationType.UPSERT_ORIGINS)) {
            applyUpsertOriginBatchSet((RecordUpsertBatchSetAccumulator) bsa);
        }
    }
    /**
     * Applies accumulated etalon updates.
     * @param accumulator the accumulator
     */
    public void applyEtalons(BatchSetAccumulator<?> bsa) {

        // 1. Upserts
        if (bsa.getSupportedIterationTypes().contains(BatchSetIterationType.UPSERT_ETALONS)) {
            RecordUpsertBatchSetAccumulator accumulator = (RecordUpsertBatchSetAccumulator) bsa;
            if (!accumulator.getIndexUpdates().isEmpty()) {
                searchServiceExt.index(accumulator.getIndexUpdates());
            }
        }
        // 2. TODO Support deletes
    }

    /**
     * Applies upsert contexts.
     * @param accumulator the accumulator, holding the contexts
     */
    private void applyUpsertOriginBatchSet(RecordUpsertBatchSetAccumulator accumulator) {
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
    private void applyDeleteOriginBatchSet(RecordDeleteBatchSetAccumulator accumulator) {
        applyEtalonsDBUpdates(accumulator);
        applyOriginsDBUpdates(accumulator);
        applyVistoryDBUpdates(accumulator);
    }

    /**
     * Applies etalos inserts
     * @param accumulator the accumulator
     */
    private void applyEtalonsDBInserts(RecordUpsertBatchSetAccumulator accumulator) {
        if (!accumulator.getEtalonInserts().isEmpty()) {
            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                dataRecordsDao.bulkInsertEtalonRecords(
                        accumulator.getEtalonInserts(),
                        accumulator.getTargets().get(BatchTarget.ETALON_INSERTS));
            } else {
                dataRecordsDao.upsertEtalonRecords(accumulator.getEtalonInserts(), true);
            }
        }
    }

    /**
     * Applies origins inserts.
     * @param accumulator the accumulator
     */
    private void applyOriginsDBInserts(RecordUpsertBatchSetAccumulator accumulator) {
        if (!accumulator.getOriginInserts().isEmpty()) {
            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                dataRecordsDao.bulkInsertOriginRecords(
                        accumulator.getOriginInserts(),
                        accumulator.getTargets().get(BatchTarget.ORIGIN_INSERTS));
            } else {
                dataRecordsDao.upsertOriginRecords(accumulator.getOriginInserts(), true);
            }
        }
    }

    /**
     * Applies etalons updates.
     * @param accumulator the accumulator
     */
    private void applyEtalonsDBUpdates(AbstractRecordBatchSetAccumulator<?> accumulator) {
        if (!accumulator.getEtalonUpdates().isEmpty()) {
            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                dataRecordsDao.bulkUpdateEtalonRecords(
                        accumulator.getEtalonUpdates(),
                        accumulator.getTargets().get(BatchTarget.ETALON_UPDATES));
            } else {
                dataRecordsDao.upsertEtalonRecords(accumulator.getEtalonUpdates(), false);
            }
        }
    }

    /**
     * Applies origins updates.
     * @param accumulator the accumulator
     */
    private void applyOriginsDBUpdates(AbstractRecordBatchSetAccumulator<?> accumulator) {
        if (!accumulator.getOriginUpdates().isEmpty()) {
            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                dataRecordsDao.bulkUpdateOriginRecords(
                        accumulator.getOriginUpdates(),
                        accumulator.getTargets().get(BatchTarget.ORIGIN_UPDATES));
            } else {
                dataRecordsDao.upsertOriginRecords(accumulator.getOriginUpdates(), false);
            }
        }
    }

    /**
     * Applies vistory updates.
     * @param accumulator the accumulator
     */
    private void applyVistoryDBUpdates(AbstractRecordBatchSetAccumulator<?> accumulator) {
        if (!accumulator.getVistory().isEmpty()) {
            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                originsVistoryDao.bulkInsertVersions(
                        accumulator.getVistory(),
                        accumulator.getTargets().get(BatchTarget.VISTORY));
            } else {
                originsVistoryDao.putVersions(accumulator.getVistory());
            }
        }
    }
}
