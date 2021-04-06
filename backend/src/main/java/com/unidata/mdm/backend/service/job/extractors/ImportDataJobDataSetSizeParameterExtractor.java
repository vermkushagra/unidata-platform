package com.unidata.mdm.backend.service.job.extractors;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.dto.job.JobParameterType;
import com.unidata.mdm.backend.service.data.batch.BatchSetSize;
import com.unidata.mdm.backend.service.job.JobEnumType;

/**
 * @author Mikhail Mikhailov
 * Data size parameter extractor.
 */
public class ImportDataJobDataSetSizeParameterExtractor implements JobEnumParamExtractor {
    /**
     * {@inheritDoc}
     */
    @Override
    public JobEnumType extractParameters() {

        final JobEnumType params = new JobEnumType();
        params.setParameterType(JobParameterType.STRING);
        params.setParameters(Arrays.stream(BatchSetSize.values()).map(BatchSetSize::name).collect(Collectors.toList()));
        return params;
    }

}
