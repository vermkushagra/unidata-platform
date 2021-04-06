package com.unidata.mdm.backend.service.job.sample;

import com.unidata.mdm.backend.common.dto.job.JobParameterType;
import com.unidata.mdm.backend.service.job.extractors.JobEnumParamExtractor;
import com.unidata.mdm.backend.service.job.JobEnumType;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Kostovarov
 */
public class SampleItemDateEnumParameterExtractor implements JobEnumParamExtractor {
    @Override
    public JobEnumType extractParameters() {
        final JobEnumType params = new JobEnumType();
        params.setParameterType(JobParameterType.DATE);
        final List<ZonedDateTime> stringParams = new ArrayList<>();
        final ZonedDateTime currentDate = ZonedDateTime.now();
        stringParams.add(currentDate);
        stringParams.add(currentDate.plus(1, ChronoUnit.WEEKS));
        stringParams.add(currentDate.plus(2, ChronoUnit.WEEKS));
        stringParams.add(currentDate.plus(3, ChronoUnit.WEEKS));
        stringParams.add(currentDate.plus(4, ChronoUnit.WEEKS));
        stringParams.add(currentDate.plus(5, ChronoUnit.WEEKS));
        stringParams.add(currentDate.plus(6, ChronoUnit.WEEKS));

        params.setParameters(stringParams);

        return params;
    }
}
