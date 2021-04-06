/**
 *
 */

package com.unidata.mdm.backend.service.job;

import static org.apache.commons.collections.CollectionUtils.isEqualCollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.support.ApplicationContextFactory;
import org.springframework.batch.core.configuration.support.JobLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.common.dto.job.JobDTO;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;
import com.unidata.mdm.backend.service.job.batch.core.configuration.support.CustomApplicationContextFactory;
import com.unidata.mdm.backend.service.job.registry.JobTemplateParameters;

/**
 * Component JobExternalLoader used during initialization context for SpringBatch job loading from external
 * integration packages. Component make scan classpath with pattern to load XML files with jobs and loads them.
 * For missed or changed SpringBatch jobs all Unidata jobs with references to certain SpringBatch jobs will be marked
 * with state ERROR.
 *
 * @author amagdenko
 */
public class JobExternalLoader implements AfterContextRefresh, ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(JobExternalLoader.class);
    private static final String PATTERN = "classpath*:com/unidata/mdm/integration/job/**/*-job.xml";

    private JobLoader jobLoader;
    private ApplicationContext applicationContext;

    @Autowired
    private JobServiceExt jobServiceExt;

    public void setJobLoader(JobLoader jobLoader) {
        this.jobLoader = jobLoader;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterContextRefresh() {
        log.debug("Prepare external jobs loader");

        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

            Resource[] resources = resolver.getResources(PATTERN);

            if (resources != null && resources.length > 0) {
                List<String> list = Arrays.asList(resources).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());

                log.info("Found resources: {}", list);

                ApplicationContextFactory factory = createApplicationContextFactory(applicationContext, resources,
                    null);

                Collection<Job> jobs = jobLoader.reload(factory);

                if (!CollectionUtils.isEmpty(jobs)) {
                    String jobsStr = jobs.stream().map(Job::getName).collect(Collectors.joining(", "));

                    log.info("Reloaded jobs: {}", jobsStr);
                }
            } else {
                log.debug("No resources found for outer jobs");
            }

            checkUnidataJobs();
        }
        catch (IOException e) {
            log.error("Failed to load external jobs", e);
        }
    }

    /**
     *
     * @param parent
     * @param resources
     * @return
     */
    private ApplicationContextFactory createApplicationContextFactory(ApplicationContext parent, Resource[] resources,
        ClassLoader classLoader) {
        CustomApplicationContextFactory applicationContextFactory =
            new CustomApplicationContextFactory(classLoader, (Object[])resources) {

            @Override
            protected void prepareContext(ConfigurableApplicationContext parent,
                ConfigurableApplicationContext context) {
                super.prepareContext(parent, context);
            }
        };

        if (parent != null) {
            applicationContextFactory.setApplicationContext(parent);
        }

        return applicationContextFactory;
    }

    /**
     *
     */
    private void checkUnidataJobs() {
        log.debug("Check unidata jobs");

        List<JobDTO> unidataJobs = jobServiceExt.findAllJobsWithParams();
        Collection<String> jobReferences = jobServiceExt.findAllJobReferences();

        List<JobDTO> errorJobs = new ArrayList<>();
        List<JobDTO> restoreJobs = new ArrayList<>();

        if (!CollectionUtils.isEmpty(unidataJobs)) {
            for (JobDTO unidataJob : unidataJobs) {
                String jobNameRef = unidataJob.getJobNameReference();

                if (!jobReferences.contains(jobNameRef)) {
                    log.warn("Failed to find job references for unidata job [jobId={}, jobNameRef={}]",
                        unidataJob.getId(), jobNameRef);

                    errorJobs.add(unidataJob);
                } else {
                    // Check that all Unidata job params exists in declared SpringBatch job params set.
                    JobTemplateParameters sbParams = jobServiceExt.findJobTemplateParameters(jobNameRef);
                    List<JobParameterDTO> unJobParams = unidataJob.getParameters();

                    Set<String> sbParamNames = Collections.<String>emptySet();
                    if (sbParams != null && !CollectionUtils.isEmpty(sbParams.getValueMap())) {
                        sbParamNames = sbParams.getValueMap().keySet();
                    }

                    Set<String> unJobParamNames = Collections.<String>emptySet();
                    if (!CollectionUtils.isEmpty(unJobParams)) {
                        unJobParamNames = unJobParams.stream()
                            .map(JobParameterDTO::getName)
                            .collect(Collectors.toSet());
                    }

                    Set<String> redundantParams = new HashSet<>(unJobParamNames);
                    if (!isEqualCollection(redundantParams, sbParamNames)) {
                        log.warn("Failed to find declared parameter names for unidata job [jobId={}, jobNameRef={}," +
                                " redundantParams={}]", unidataJob.getId(), jobNameRef, redundantParams);

                        errorJobs.add(unidataJob);
                    } else if (unidataJob.isError()) {
                        restoreJobs.add(unidataJob);
                    }
                }
            }
        }

        if (!errorJobs.isEmpty()) {
            log.warn("Incorrect job references for unidata jobs. Jobs will be marked with ERROR state [{}]",
                errorJobs.stream()
                .map(JobDTO::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(",")));

            // Mark with errors and deactivate.
            jobServiceExt.markErrorJobs(errorJobs, true);
        }

        if (!restoreJobs.isEmpty()) {
            log.warn("Unidata jobs restored with job references. Jobs will be marked without ERROR state [{}]",
                restoreJobs.stream()
                .map(JobDTO::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(",")));

            // Mark with errors and deactivate.
            jobServiceExt.markErrorJobs(restoreJobs, false);
        }
    }
}
