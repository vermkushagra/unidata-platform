package com.unidata.mdm.backend.service.job.modify;

import com.unidata.mdm.backend.service.job.exchange.in.ImportDataJobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.unidata.mdm.backend.service.job.common.AbstractJobStepExecutionListener;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Mikhail Mikhailov, Dmitrii Kopin
 * Modify item step execution listener.
 */
@StepScope
public class ModifyItemJobStepExecutionListener extends AbstractJobStepExecutionListener {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ModifyItemJobStepExecutionListener.class);
    /**
     * HZ innstance.
     */
    @Autowired
    private HazelcastInstance hazelcastInstance;

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {
        if (ImportDataJobUtils.getStepState() == null) {
            ModifyItemJobStepExecutionState parameters = new ModifyItemJobStepExecutionState();
            ImportDataJobUtils.setStepState(parameters);
        }

        super.authenticateIfNeeded();
        super.beforeStep(stepExecution);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        super.afterStep(stepExecution);

        // 1. Collect counters
        ModifyItemJobStepExecutionState sp = ImportDataJobUtils.removeStepState();
        if (sp.getModifyRecords() > 0) {
            IAtomicLong counter = hazelcastInstance.getAtomicLong(
                    ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_RECORDS_COUNTER));
            counter.addAndGet(sp.getModifyRecords());
        }

        if (sp.getModifyClassifiers() > 0) {
            IAtomicLong counter = hazelcastInstance.getAtomicLong(
                    ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_CLASSIFIERS_COUNTER));
            counter.addAndGet(sp.getModifyClassifiers());
        }

        if (sp.getDeletedClassifiers() > 0) {
            IAtomicLong counter = hazelcastInstance.getAtomicLong(
                    ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_CLASSIFIERS_DELETED_COUNTER));
            counter.addAndGet(sp.getDeletedClassifiers());
        }

        if (sp.getModifyRelations() > 0) {
            IAtomicLong counter = hazelcastInstance.getAtomicLong(
                    ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_RELATIONS_COUNTER));
            counter.addAndGet(sp.getModifyRelations());
        }

        if (sp.getDeletedRelations() > 0) {
            IAtomicLong counter = hazelcastInstance.getAtomicLong(
                    ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_RELATIONS_DELETED_COUNTER));
            counter.addAndGet(sp.getDeletedRelations());
        }

        if (sp.getFailedRecords() > 0) {
            IAtomicLong counter = hazelcastInstance.getAtomicLong(
                    ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_RECORDS_FAILED_COUNTER));
            counter.addAndGet(sp.getFailedRecords());
        }

        if (sp.getFailedClassifiers() > 0) {
            IAtomicLong counter = hazelcastInstance.getAtomicLong(
                    ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_CLASSIFIERS_FAILED_COUNTER));
            counter.addAndGet(sp.getFailedClassifiers());
        }

        if (sp.getFailedRelations() > 0) {
            IAtomicLong counter = hazelcastInstance.getAtomicLong(
                    ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_RELATIONS_FAILED_COUNTER));
            counter.addAndGet(sp.getFailedRelations());
        }
        if (sp.getSkeptRecords() > 0) {
            IAtomicLong counter = hazelcastInstance.getAtomicLong(
                    ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_RECORDS_SKEPT_COUNTER));
            counter.addAndGet(sp.getSkeptRecords());
        }

        if (sp.getSkeptClassifiers() > 0) {
            IAtomicLong counter = hazelcastInstance.getAtomicLong(
                    ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_CLASSIFIERS_SKEPT_COUNTER));
            counter.addAndGet(sp.getSkeptClassifiers());
        }

        if (sp.getSkeptRelations() > 0) {
            IAtomicLong counter = hazelcastInstance.getAtomicLong(
                    ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_RELATIONS_SKEPT_COUNTER));
            counter.addAndGet(sp.getSkeptRelations());
        }

        super.clearAuthentication();
        return stepExecution.getExitStatus();
    }

}
