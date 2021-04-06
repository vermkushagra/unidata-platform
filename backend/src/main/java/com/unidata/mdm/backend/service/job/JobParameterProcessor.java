package com.unidata.mdm.backend.service.job;

import org.springframework.batch.core.JobParametersBuilder;

import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;

/**
 * @author Mikhail Mikhailov
 * Parameter preprocessor.
 */
@FunctionalInterface
public interface JobParameterProcessor {
    /**
     * Does parameters preprocessing.
     * @param param the parameter DTO
     * @param the builder to add parameter to
     * @return null for default processing or
     */
    void process(JobParameterDTO param, JobParametersBuilder builder);
}
