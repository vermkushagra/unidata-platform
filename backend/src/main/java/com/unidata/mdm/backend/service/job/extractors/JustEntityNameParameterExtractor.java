package com.unidata.mdm.backend.service.job.extractors;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.service.job.JobEnumType;
import com.unidata.mdm.backend.service.job.JobUtil;

public class JustEntityNameParameterExtractor implements JobEnumParamExtractor {

    @Autowired
    private JobUtil jobUtil;

    @Override
    public JobEnumType extractParameters() {
        return jobUtil.getJustEntitiesParamsList();
    }
}
