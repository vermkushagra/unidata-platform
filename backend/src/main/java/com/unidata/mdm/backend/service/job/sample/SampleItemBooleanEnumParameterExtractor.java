package com.unidata.mdm.backend.service.job.sample;

import com.unidata.mdm.backend.common.dto.job.JobParameterType;
import com.unidata.mdm.backend.service.job.extractors.JobEnumParamExtractor;
import com.unidata.mdm.backend.service.job.JobEnumType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Kostovarov
 */
public class SampleItemBooleanEnumParameterExtractor implements JobEnumParamExtractor{
    @Override
    public JobEnumType extractParameters() {
        final JobEnumType params = new JobEnumType();
        params.setParameterType(JobParameterType.BOOLEAN);
        final List<Boolean> stringParams = new ArrayList<>();
        stringParams.add(true);
        stringParams.add(false);
        stringParams.add(true);

        params.setParameters(stringParams);

        return params;
    }
}
