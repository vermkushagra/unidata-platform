package com.unidata.mdm.backend.service.job.extractors;

import com.unidata.mdm.backend.service.job.JobEnumType;

/**
 * @author Denis Kostovarov
 */
public interface JobEnumParamExtractor {
    JobEnumType extractParameters();
}
