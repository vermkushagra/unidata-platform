package com.unidata.mdm.backend.service.job.sample;

import com.unidata.mdm.backend.common.dto.job.JobParameterType;
import com.unidata.mdm.backend.service.job.extractors.JobEnumParamExtractor;
import com.unidata.mdm.backend.service.job.JobEnumType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Kostovarov
 */
public class SampleItemLongEnumParameterExtractor implements JobEnumParamExtractor{
    @Override
    public JobEnumType extractParameters() {
        final JobEnumType params = new JobEnumType();
        params.setParameterType(JobParameterType.LONG);
        final List<Long> stringParams = new ArrayList<>();
        stringParams.add(1056L);
        stringParams.add(8901L);
        stringParams.add(555L);

        params.setParameters(stringParams);

        return params;
    }
}
