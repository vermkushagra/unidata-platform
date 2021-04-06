/**
 *
 */

package com.unidata.mdm.backend.service.job.extractors;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.service.job.JobEnumType;
import com.unidata.mdm.backend.service.job.JobUtil;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class AllEntityNameParameterExtractor implements JobEnumParamExtractor {

    @Autowired
    private JobUtil jobUtil;

    @Override
    public JobEnumType extractParameters() {
        return jobUtil.getAllEntitiesParamsList();
    }
}
