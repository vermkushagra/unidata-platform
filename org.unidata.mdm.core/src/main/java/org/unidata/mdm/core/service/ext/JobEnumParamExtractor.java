package org.unidata.mdm.core.service.ext;

import org.unidata.mdm.core.type.job.JobEnumType;

/**
 * @author Denis Kostovarov
 */
public interface JobEnumParamExtractor {
    JobEnumType extractParameters();
}
