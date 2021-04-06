package com.unidata.mdm.backend.service.job.extractors;

import java.util.Arrays;

import com.unidata.mdm.backend.common.dto.job.JobParameterType;
import com.unidata.mdm.backend.service.job.JobEnumType;

/**
 * @author Mikhail Mikhailov
 * Audit level as enum.
 */
public class AuditLevelParameterExtractor implements JobEnumParamExtractor {

    /**
     * {@inheritDoc}
     */
    @Override
    public JobEnumType extractParameters() {

        final JobEnumType params = new JobEnumType();
        params.setParameterType(JobParameterType.LONG);
        params.setParameters(Arrays.asList(0L, 1L, 2L));
        return params;
    }

}
