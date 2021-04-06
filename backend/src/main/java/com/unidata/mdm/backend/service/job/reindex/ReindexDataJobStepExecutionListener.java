package com.unidata.mdm.backend.service.job.reindex;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.core.HazelcastInstance;
import com.unidata.mdm.backend.service.job.common.AbstractJobStepExecutionListener;
import com.unidata.mdm.backend.service.job.common.JobRuntimeUtils;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Mikhail Mikhailov
 * Slave step listener.
 */
@StepScope
public class ReindexDataJobStepExecutionListener extends AbstractJobStepExecutionListener  {
    /**
     * HZ innstance.
     */
    @Autowired
    private HazelcastInstance hazelcastInstance;

    /**
     * Constructor.
     */
    public ReindexDataJobStepExecutionListener() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {

        if (JobRuntimeUtils.getStepState() == null) {
            JobRuntimeUtils.setStepState(new ReindexDataJobStepExecutionState());
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

        /* ReindexDataJobStepExecutionState state = */
        JobRuntimeUtils.removeStepState();
        /*
        if (state.getReindexedRecords() > 0) {
            IAtomicLong fCounter = hazelcastInstance.getAtomicLong(
                    JobRuntimeUtils.getObjectReferenceName(runId, ReindexDataJobConstants.REINDEX_JOB_REINDEXED_RECORDS_COUNTER));
            fCounter.addAndGet(state.getReindexedRecords());
        }

        if (state.getReindexedClassifiers() > 0) {
            IAtomicLong iCounter = hazelcastInstance.getAtomicLong(
                        JobRuntimeUtils.getObjectReferenceName(runId, ReindexDataJobConstants.REINDEX_JOB_REINDEXED_CLASSIFIERS_COUNTER));
            iCounter.addAndGet(state.getReindexedClassifiers());
        }

        if (state.getClassifiedRecords() > 0) {
            IAtomicLong uCounter = hazelcastInstance.getAtomicLong(
                    JobRuntimeUtils.getObjectReferenceName(runId, ReindexDataJobConstants.REINDEX_JOB_CLASSIFIED_RECORDS_COUNTER));
            uCounter.addAndGet(state.getClassifiedRecords());
        }

        if (state.getReindexedRelations() > 0) {
            IAtomicLong sCounter = hazelcastInstance.getAtomicLong(
                    JobRuntimeUtils.getObjectReferenceName(runId, ReindexDataJobConstants.REINDEX_JOB_REINDEXED_RELATIONS_COUNTER));
            sCounter.addAndGet(state.getReindexedRelations());
        }
        */
        super.clearAuthentication();
        return stepExecution.getExitStatus();
    }

}
