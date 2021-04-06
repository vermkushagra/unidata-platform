package com.unidata.mdm.backend.service.job;

import java.util.Date;

import org.springframework.batch.core.JobParametersBuilder;

import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;

/**
 * @author Mikhail Mikhailov
 * Default implementation.
 */
public class DefaultJobParameterProcessor implements JobParameterProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(JobParameterDTO jobParameter, JobParametersBuilder builder) {
        switch (jobParameter.getType()) {
            case STRING:
                builder.addString(jobParameter.getName(), jobParameter.getStringValue());
                break;
            case DATE:
                builder.addDate(jobParameter.getName(), Date.from(jobParameter.getDateValue().toInstant()));
                break;
            case LONG:
                builder.addLong(jobParameter.getName(), jobParameter.getLongValue());
                break;
            case DOUBLE:
                builder.addDouble(jobParameter.getName(), jobParameter.getDoubleValue());
                break;
            case BOOLEAN:
                final Boolean b = jobParameter.getBooleanValue();
                builder.addString(jobParameter.getName(), b.toString());
                break;
        }
    }
}
