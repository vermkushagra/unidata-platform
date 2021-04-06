package com.unidata.mdm.backend.service.data.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.dao.ClassifiersDAO;
import com.unidata.mdm.backend.service.search.SearchServiceExt;

/**
 * @author Mikhail Mikhailov
 * Classifier processor.
 */
@Component("classifierBatchSetProcessor")
public class ClassifierBatchSetProcessor {
    /**
     * Search service.
     */
    @Autowired
    private SearchServiceExt searchServiceExt;
    /**
     * Classifiers DAO.
     */
    @Autowired
    private ClassifiersDAO classifiersDAO;
    /**
     * Applies accumulated origin updates.
     * @param accumulator the accumulator
     */
    @Transactional(rollbackFor = Exception.class)
    public void applyOrigins(BatchSetAccumulator<?> bsa) {

        if (bsa.getSupportedIterationTypes().contains(BatchSetIterationType.DELETE_ORIGINS)) {
            applyDeleteOriginBatchSet((ClassifiersDeleteBatchSetAccumulator) bsa);
        } else if (bsa.getSupportedIterationTypes().contains(BatchSetIterationType.UPSERT_ORIGINS)) {
            applyUpsertOriginBatchSet((ClassifierUpsertBatchSetAccumulator) bsa);
        }
    }
    /**
     * Applies accumulated etalon updates.
     * @param accumulator the accumulator
     */
    public void applyEtalons(BatchSetAccumulator<?> bsa) {
        AbstractClassifierBatchSetAccumulator<?> accumulator = (AbstractClassifierBatchSetAccumulator<?>) bsa;
        if (!accumulator.getIndexUpdates().isEmpty()) {
            searchServiceExt.index(accumulator.getIndexUpdates());
        }
    }
    /**
     * Applies delete contexts.
     * @param accumulator the accumaltor, holding the contexts
     */
    private void applyDeleteOriginBatchSet(ClassifiersDeleteBatchSetAccumulator accumulator) {
        applyEtalonsDBUpdates(accumulator);
        applyOriginsDBUpdates(accumulator);
        applyVistoryDBUpdates(accumulator);
    }

    /**
     * Applies upsert contexts.
     * @param accumulator the accumulator, holding the contexts
     */
    private void applyUpsertOriginBatchSet(ClassifierUpsertBatchSetAccumulator accumulator) {
        applyEtalonsDBInserts(accumulator);
        applyEtalonsDBUpdates(accumulator);
        applyOriginsDBInserts(accumulator);
        applyOriginsDBUpdates(accumulator);
        applyVistoryDBUpdates(accumulator);
    }

    /**
     * Applies etalos inserts
     * @param accumulator the accumulator
     */
    private void applyEtalonsDBInserts(ClassifierUpsertBatchSetAccumulator accumulator) {
        if (!accumulator.getEtalonInserts().isEmpty()) {
            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                classifiersDAO.bulkInsertEtalonRecords(
                        accumulator.getEtalonInserts(),
                        accumulator.getTargets().get(BatchTarget.ETALON_INSERTS));
            } else {
                classifiersDAO.upsertEtalonClassifiers(accumulator.getEtalonInserts(), true);
            }
        }
    }

    /**
     * Applies origins inserts.
     * @param accumulator the accumulator
     */
    private void applyOriginsDBInserts(ClassifierUpsertBatchSetAccumulator accumulator) {
        if (!accumulator.getOriginInserts().isEmpty()) {
            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                classifiersDAO.bulkInsertOriginRecords(
                        accumulator.getOriginInserts(),
                        accumulator.getTargets().get(BatchTarget.ORIGIN_INSERTS));
            } else {
                classifiersDAO.upsertOriginClassifiers(accumulator.getOriginInserts(), true);
            }
        }
    }

    /**
     * Applies etalons updates.
     * @param accumulator the accumulator
     */
    private void applyEtalonsDBUpdates(AbstractClassifierBatchSetAccumulator<?> accumulator) {
        if (!accumulator.getEtalonUpdates().isEmpty()) {
            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                classifiersDAO.bulkUpdateEtalonRecords(
                        accumulator.getEtalonUpdates(),
                        accumulator.getTargets().get(BatchTarget.ETALON_UPDATES));
            } else {
                classifiersDAO.upsertEtalonClassifiers(accumulator.getEtalonUpdates(), false);
            }
        }
    }

    /**
     * Applies origins updates.
     * @param accumulator the accumulator
     */
    private void applyOriginsDBUpdates(AbstractClassifierBatchSetAccumulator<?> accumulator) {
        if (!accumulator.getOriginUpdates().isEmpty()) {
            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                classifiersDAO.bulkUpdateOriginRecords(
                        accumulator.getOriginUpdates(),
                        accumulator.getTargets().get(BatchTarget.ORIGIN_UPDATES));
            } else {
                classifiersDAO.upsertOriginClassifiers(accumulator.getOriginUpdates(), false);
            }
        }
    }

    /**
     * Applies vistory updates.
     * @param accumulator the accumulator
     */
    private void applyVistoryDBUpdates(AbstractClassifierBatchSetAccumulator<?> accumulator) {
        if (!accumulator.getVistory().isEmpty()) {
            if (accumulator.getBatchSetSize() == BatchSetSize.LARGE) {
                classifiersDAO.bulkInsertVersions(
                        accumulator.getVistory(),
                        accumulator.getTargets().get(BatchTarget.VISTORY));
            } else {
                classifiersDAO.putVersions(accumulator.getVistory());
            }
        }
    }
}
