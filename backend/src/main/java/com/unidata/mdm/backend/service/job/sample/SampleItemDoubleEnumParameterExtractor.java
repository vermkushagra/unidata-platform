package com.unidata.mdm.backend.service.job.sample;

import com.unidata.mdm.backend.common.dto.job.JobParameterType;
import com.unidata.mdm.backend.service.job.extractors.JobEnumParamExtractor;
import com.unidata.mdm.backend.service.job.JobEnumType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Kostovarov
 */
public class SampleItemDoubleEnumParameterExtractor implements JobEnumParamExtractor{
    @Override
    public JobEnumType extractParameters() {
        final JobEnumType params = new JobEnumType();
        params.setParameterType(JobParameterType.DOUBLE);
        final List<Double> stringParams = new ArrayList<>();
        stringParams.add(3.14159265359);
        stringParams.add(2.7182818284590452353602874713527);
        stringParams.add(5552d);

        params.setParameters(stringParams);

        return params;
    }
}
