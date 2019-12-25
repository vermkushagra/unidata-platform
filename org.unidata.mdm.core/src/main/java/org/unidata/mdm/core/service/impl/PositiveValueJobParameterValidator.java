package org.unidata.mdm.core.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.dto.job.JobParameterDTO;
import org.unidata.mdm.core.service.job.JobParameterValidator;
import org.unidata.mdm.system.util.TextUtils;

/**
 * Job parameter validator for positive values
 *  Error if value <= 0 or null
 * @author Dmitry Kopin on 09.08.2018.
 */
@Component("positiveValueJobParameterValidator")
public class PositiveValueJobParameterValidator implements JobParameterValidator {

    private static final String NOT_POSITIVE_PARAMETER_VALUE = "app.job.parameters.value.notpositive";

    @Override
    public List<String> validate(String paramName, Collection<JobParameterDTO> jobParameters) {

        if (CollectionUtils.isNotEmpty(jobParameters)) {
            JobParameterDTO jobParameter = jobParameters.stream()
                    .filter(param -> param.getName().equals(paramName))
                    .findFirst()
                    .orElse(null);
            if (jobParameter == null) {
                return Collections.emptyList();
            }

            return jobParameter.getLongValue() != null && jobParameter.getLongValue() > 0
                    ? Collections.emptyList()
                    : Collections.singletonList(TextUtils.getText(NOT_POSITIVE_PARAMETER_VALUE, jobParameter.getName()));

        }

        return Collections.emptyList();
    }
}
