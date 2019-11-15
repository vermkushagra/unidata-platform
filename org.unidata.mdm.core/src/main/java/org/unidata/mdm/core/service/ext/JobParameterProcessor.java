package org.unidata.mdm.core.service.ext;

import org.springframework.batch.core.JobParametersBuilder;
import org.unidata.mdm.core.dto.job.JobParameterDTO;

/**
 * @author Mikhail Mikhailov
 * Parameter preprocessor.
 */
public interface JobParameterProcessor {
    /**
     * Does parameters preprocessing.
     * @param param the parameter DTO
     * @param the builder to add parameter to
     * @return null for default processing or
     */
    void process(JobParameterDTO param, JobParametersBuilder builder);
    /**
     * Filters (effectively hides) some parameters if needed.
     * @param parameters the parameters
     * @return parameters filtered parameters
     */
    default JobTemplateParameters filter(JobTemplateParameters parameters) {
        return parameters;
    }
}
