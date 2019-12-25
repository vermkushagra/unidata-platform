/**
 * Date: 16.03.2016
 */

package org.unidata.mdm.core.service.job;

import java.util.Set;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public interface JobWithParamsRegistry extends JobRegistry {
    void register(JobTemplateParameters parameters) throws DuplicateJobException;

   	void unregister(String jobName);

    Set<String> getJobParameterNames();

    JobTemplateParameters getJobTemplateParameters(String name);

    void registerTriggerListener(Job job);
}
