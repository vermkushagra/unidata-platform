package com.unidata.mdm.backend.service.job.sample;

import com.unidata.mdm.backend.common.dto.job.JobParameterType;
import com.unidata.mdm.backend.service.job.extractors.JobEnumParamExtractor;
import com.unidata.mdm.backend.service.job.JobEnumType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Kostovarov
 */
public class SampleItemStringEnumParameterExtractor implements JobEnumParamExtractor{
    @Override
    public JobEnumType extractParameters() {
        final JobEnumType params = new JobEnumType();
        params.setParameterType(JobParameterType.STRING);
        final List<String> stringParams = new ArrayList<>();
        stringParams.add("Значение 1");
        stringParams.add("Значение 2");
        stringParams.add("Значение 3");

        params.setParameters(stringParams);

        return params;
    }
}
