package org.unidata.mdm.core.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;

import org.springframework.batch.core.configuration.ListableJobLocator;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.AbstractJobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.unidata.mdm.core.configuration.job.CustomJobExecutionContextSerializer;
import org.unidata.mdm.core.configuration.job.CustomJobExplorerFactoryBean;
import org.unidata.mdm.core.configuration.job.CustomJobRepositoryFactoryBean;
import org.unidata.mdm.core.configuration.job.JobWithParamsRegistryImpl;
import org.unidata.mdm.core.configuration.job.UnidataSchedulerFactoryBean;
import org.unidata.mdm.core.service.ext.JobParameterProcessor;
import org.unidata.mdm.core.util.SpringConfigurationUtils;

/**
 * @author Alexander Malyshev
 */
@Configuration
public class JobConfiguration {

    public static final String UNIDATA_BATCH_JOB_BATCH_TABLE_PREFIX = "unidata_batch_job.BATCH_";

    @Bean
    public MapFactoryBean jobParameterProcessorsMap() {
        final MapFactoryBean mapFactoryBean = new MapFactoryBean();
        mapFactoryBean.setSourceMap(new ConcurrentHashMap<String, JobParameterProcessor>());
        mapFactoryBean.setTargetMapClass(ConcurrentHashMap.class);
        return mapFactoryBean;
    }

    @Bean
    public ExecutionContextSerializer executionContextSerializer() {
        return new CustomJobExecutionContextSerializer();
    }

    @Bean
    public DataSource jobDataSource() {
        JndiDataSourceLookup jndiDataSourceLookup = new JndiDataSourceLookup();
        jndiDataSourceLookup.setResourceRef(true);
        return jndiDataSourceLookup.getDataSource("jdbc/UniDataBatchJobDataSource");
    }

    @Bean
    public CustomJobRepositoryFactoryBean jobRepositoryFactoryBean(
            final DataSource jobDataSource,
            final PlatformTransactionManager platformTransactionManager,
            final ExecutionContextSerializer executionContextSerializer
    ) {
        final CustomJobRepositoryFactoryBean customJobRepositoryFactoryBean = new CustomJobRepositoryFactoryBean();
        customJobRepositoryFactoryBean.setDataSource(jobDataSource);
        customJobRepositoryFactoryBean.setTransactionManager(platformTransactionManager);
        customJobRepositoryFactoryBean.setDatabaseType("postgres");
        customJobRepositoryFactoryBean.setTablePrefix(UNIDATA_BATCH_JOB_BATCH_TABLE_PREFIX);
        customJobRepositoryFactoryBean.setSerializer(executionContextSerializer);
        return customJobRepositoryFactoryBean;
    }

    @Bean
    public TaskExecutor jobTaskExecutor(
            @Value("${unidata.job.thread.pool.size:30}") final int corePoolSize,
            @Value("${unidata.job.thread.pool.size:30}") final int maxPoolSize,
            @Value("${unidata.job.queue.size:100}") final int queueCapacity
    ) {
        final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        return threadPoolTaskExecutor;
    }

    @Bean
    public JobLauncher jobLauncher(
            final JobRepository jobRepository,
            final TaskExecutor jobTaskExecutor
    ) {
        final SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
        simpleJobLauncher.setJobRepository(jobRepository);
        simpleJobLauncher.setTaskExecutor(jobTaskExecutor);
        return simpleJobLauncher;
    }

    @Bean
    public AbstractJobExplorerFactoryBean jobExplorerFactoryBean(
            final DataSource jobDataSource,
            final PlatformTransactionManager platformTransactionManager,
            final ExecutionContextSerializer executionContextSerializer
    ) {
        final CustomJobExplorerFactoryBean customJobExplorerFactoryBean = new CustomJobExplorerFactoryBean();
        customJobExplorerFactoryBean.setDataSource(jobDataSource);
        customJobExplorerFactoryBean.setTransactionManager(platformTransactionManager);
        customJobExplorerFactoryBean.setTablePrefix(UNIDATA_BATCH_JOB_BATCH_TABLE_PREFIX);
        customJobExplorerFactoryBean.setSerializer(executionContextSerializer);
        return customJobExplorerFactoryBean;
    }

    @Bean
    public ListableJobLocator jobRegistry(
            final Map<String, JobParameterProcessor> jobParameterProcessorsMap
    ) {
        final JobWithParamsRegistryImpl jobWithParamsRegistry = new JobWithParamsRegistryImpl();
        jobWithParamsRegistry.setJobParameterProcessorsMap(jobParameterProcessorsMap);
        return jobWithParamsRegistry;
    }

    @Bean
    public JobOperator jobOperator(
            final JobExplorer jobExplorer,
            final JobRepository jobRepository,
            final ListableJobLocator jobRegistry,
            final JobLauncher jobLauncher
    ) {
        final SimpleJobOperator simpleJobOperator = new SimpleJobOperator();
        simpleJobOperator.setJobExplorer(jobExplorer);
        simpleJobOperator.setJobRepository(jobRepository);
        simpleJobOperator.setJobRegistry(jobRegistry);
        simpleJobOperator.setJobLauncher(jobLauncher);
        return simpleJobOperator;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        return new UnidataSchedulerFactoryBean();
    }

    @Bean("job-sql")
    public PropertiesFactoryBean jobSql() {
        return SpringConfigurationUtils.classpathPropertiesFactoryBean("/db/job-sql.xml");
    }
}
