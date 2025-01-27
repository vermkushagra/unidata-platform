/**
 * Date: 11.05.2016
 */

package com.unidata.mdm.backend.service.job.registry;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.GroupAwareJob;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.Assert;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class CustomJobRegistryBeanPostProcessor implements BeanPostProcessor, InitializingBean, DisposableBean {
    private static Log logger = LogFactory.getLog(CustomJobRegistryBeanPostProcessor.class);

    private Collection<String> jobNames = new HashSet<>();

    private JobWithParamsRegistry jobRegistry = null;

    private String groupName = null;

    public void setJobWithParamsRegistry(JobWithParamsRegistry jobRegistry) {
        this.jobRegistry = jobRegistry;
    }

    /**
   	 * The group name for jobs registered by this component. Optional (defaults
   	 * to null, which means that jobs are registered with their bean names).
   	 * Useful where there is a hierarchy of application contexts all
   	 * contributing to the same {@link JobRegistry}: child contexts can then
   	 * define an instance with a unique group name to avoid clashes between job
   	 * names.
   	 *
   	 * @param groupName the groupName to set
   	 */
   	public void setGroupName(String groupName) {
   		this.groupName = groupName;
   	}

    /**
   	 * Make sure the registry is set before use.
   	 *
   	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   	 */
   	@Override
   	public void afterPropertiesSet() throws Exception {
   		Assert.notNull(jobRegistry, "JobRegistry must not be null");
   	}

   	/**
   	 * Unregister all the {@link Job} instances that were registered by this
   	 * post processor.
   	 * @see org.springframework.beans.factory.DisposableBean#destroy()
   	 */
   	@Override
   	public void destroy() throws Exception {
   		for (String name : jobNames) {
   			if (logger.isDebugEnabled()) {
   				logger.debug("Unregistering job: " + name);
   			}
   			jobRegistry.unregister(name);
   		}
   		jobNames.clear();
   	}

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof JobTemplateParameters) {
            JobTemplateParameters parameters = (JobTemplateParameters)bean;

            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Registering job parameters: " + parameters.getJobName());
                }

                jobRegistry.register(parameters);
            }
            catch (DuplicateJobException e) {
                throw new FatalBeanException("Cannot register job parameters configuration", e);
            }
        } else if (bean instanceof Job) {
            jobRegistry.registerTriggerListener((Job) bean);

            Job job = (Job) bean;
            try {
                String groupName = this.groupName;

                job = groupName==null ? job : new GroupAwareJob(groupName, job);
                ReferenceJobFactory jobFactory = new ReferenceJobFactory(job);
                String name = jobFactory.getJobName();
                if (logger.isDebugEnabled()) {
                    logger.debug("Registering job: " + name);
                }
                jobRegistry.register(jobFactory);
                jobNames.add(name);
            }
            catch (DuplicateJobException e) {
                throw new FatalBeanException("Cannot register job configuration", e);
            }
            return job;
        }

        return bean;
    }

    /**
   	 * Determine a group name for the job to be registered. Default
   	 * implementation just returns the {@link #setGroupName(String) groupName}
   	 * configured. Provides an extension point for specialised subclasses.
   	 *
   	 * @param beanDefinition the bean definition for the job
   	 * @param job the job
   	 * @return a group name for the job (or null if not needed)
   	 */
   	protected String getGroupName(BeanDefinition beanDefinition, Job job) {
   		return groupName;
   	}

   	/**
   	 * Do nothing.
   	 *
   	 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object,
   	 * java.lang.String)
   	 */
   	@Override
   	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
   		return bean;
   	}
}
