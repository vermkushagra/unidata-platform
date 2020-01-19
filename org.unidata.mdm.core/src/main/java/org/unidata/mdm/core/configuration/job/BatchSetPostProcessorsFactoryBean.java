package org.unidata.mdm.core.configuration.job;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;
import org.unidata.mdm.core.type.annotation.JobRef;
import org.unidata.mdm.system.service.ModuleService;
import org.unidata.mdm.system.type.batch.BatchSetPostProcessor;
import org.unidata.mdm.system.type.batch.BatchSetPostProcessors;
import org.unidata.mdm.system.type.module.Module;

/**
 * @author Mikhail Mikhailov on Jan 17, 2020
 */
@SuppressWarnings("rawtypes")
public class BatchSetPostProcessorsFactoryBean extends AbstractFactoryBean<BatchSetPostProcessors> {

    private List<Class<? extends BatchSetPostProcessor>> sourceClasses;

    private String jobName;

    public BatchSetPostProcessorsFactoryBean() {
        super();
        setSingleton(false);
    }
    /**
     * @param jobName the jobName to set
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    /**
     * Set the source classes list.
     */
    public void setClassesList(List<Class<? extends BatchSetPostProcessor>> sourceClasses) {
        this.sourceClasses = CollectionUtils.isEmpty(sourceClasses) ? Collections.emptyList() : sourceClasses;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<BatchSetPostProcessors> getObjectType() {
        return BatchSetPostProcessors.class;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected BatchSetPostProcessors createInstance() {
        Assert.isTrue(getBeanFactory() instanceof AutowireCapableBeanFactory, "Bean factory is not an instance of AutowireCapableBeanFactory.");
        AutowireCapableBeanFactory acbf = (AutowireCapableBeanFactory) getBeanFactory();
        return new BatchSetPostProcessors(sourceClasses.stream()
                .map(acbf::getBean)
                .sorted(Comparator.comparingInt(BatchSetPostProcessor::order))
                .collect(Collectors.toList()));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        // Explicitly set by the user.
        if (Objects.nonNull(sourceClasses)) {
            return;
        }

        // Try to gather
        Assert.notNull(jobName, "Job name must not be null.");

        ModuleService moduleService = getBeanFactory().getBean(ModuleService.class);
        this.sourceClasses = moduleService.getModules().stream()
            .map(Module::getBatchSetPostProcessors)
            .flatMap(Collection::stream)
            .filter(ppc -> {
                JobRef ref = ppc.getAnnotation(JobRef.class);
                return Objects.nonNull(ref) && StringUtils.equalsIgnoreCase(ref.value(), jobName);
            })
            .collect(Collectors.toList());
    }
}
