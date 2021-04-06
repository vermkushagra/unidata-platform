package com.unidata.mdm.backend.service.job;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Required;

import com.unidata.mdm.backend.common.runtime.MeasurementContextName;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;

/**
 * Batch step listener which measure step time execution
 */
public class MeasurementStepListener implements StepExecutionListener {

    private MeasurementContextName contextName;

    @Required
    public void setContextName(MeasurementContextName contextName) {
        this.contextName = contextName;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        MeasurementPoint.init(contextName);
        MeasurementPoint.start();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        MeasurementPoint.stop();
        return stepExecution.getExitStatus();
    }
}
