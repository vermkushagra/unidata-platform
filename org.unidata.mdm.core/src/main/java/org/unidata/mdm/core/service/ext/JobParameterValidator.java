package org.unidata.mdm.core.service.ext;

import java.util.Collection;
import java.util.List;

import org.unidata.mdm.core.dto.job.JobParameterDTO;

/**
 * Interface for job parameter validator
 * @author Dmitry Kopin on 09.08.2018.
 */
@FunctionalInterface
public interface JobParameterValidator {

    List<String> validate(String paramName, Collection<JobParameterDTO> jobParameter);
}
