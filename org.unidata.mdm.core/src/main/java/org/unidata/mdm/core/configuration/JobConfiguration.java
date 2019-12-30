package org.unidata.mdm.core.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.JobScope;
import org.springframework.batch.core.scope.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.unidata.mdm.core.configuration.job.CustomJobExecutionContextSerializer;
import org.unidata.mdm.core.configuration.job.CustomJobExplorerFactoryBean;
import org.unidata.mdm.core.configuration.job.CustomJobRegistryBeanPostProcessor;
import org.unidata.mdm.core.configuration.job.CustomJobRepositoryFactoryBean;
import org.unidata.mdm.core.configuration.job.JobWithParamsRegistryImpl;
import org.unidata.mdm.core.configuration.job.UnidataSchedulerFactoryBean;
import org.unidata.mdm.core.service.job.JobParameterProcessor;
import org.unidata.mdm.core.service.job.JobWithParamsRegistry;

/**
 * @author Alexander Malyshev
 */
@Configuration
public class JobConfiguration {
    /**
     * Custom table prefix.
     */
    public static final String UNIDATA_BATCH_JOB_BATCH_TABLE_PREFIX = "BATCH_";
    /**
     * TX manager.
     */
    @Autowired
    private PlatformTransactionManager coreTransactionManager;
    /**
     * The 'core' datasource.
     */
    @Autowired
    @Qualifier("coreDataSource")
    private DataSource coreDataSource;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        return new UnidataSchedulerFactoryBean();
    }

    // SB section
    @Bean
    public CustomJobExecutionContextSerializer customJobExecutionContextSerializer() {
        return new CustomJobExecutionContextSerializer();
    }

    @Bean
    public MapFactoryBean jobParameterProcessorsMap() {
        final MapFactoryBean mapFactoryBean = new MapFactoryBean();
        mapFactoryBean.setSourceMap(new ConcurrentHashMap<String, JobParameterProcessor>());
        mapFactoryBean.setTargetMapClass(ConcurrentHashMap.class);
        return mapFactoryBean;
    }
    /**
     * Creates a job repository.
     * @return a job repository
     * @throws Exception
     */
    @Bean
    public JobRepository jobRepository(CustomJobExecutionContextSerializer customJobExecutionContextSerializer) throws Exception {
        final CustomJobRepositoryFactoryBean customJobRepositoryFactoryBean = new CustomJobRepositoryFactoryBean();
        customJobRepositoryFactoryBean.setDataSource(coreDataSource);
        customJobRepositoryFactoryBean.setTransactionManager(coreTransactionManager);
        customJobRepositoryFactoryBean.setDatabaseType("postgres");
        customJobRepositoryFactoryBean.setTablePrefix(UNIDATA_BATCH_JOB_BATCH_TABLE_PREFIX);
        customJobRepositoryFactoryBean.setSerializer(customJobExecutionContextSerializer);
        return customJobRepositoryFactoryBean.getObject();
    }
    /**
     * Creates a job explorer.
     * @return a job explorer
     * @throws Exception
     */
    @Bean
    public JobExplorer jobExplorer(CustomJobExecutionContextSerializer customJobExecutionContextSerializer) throws Exception {
        final CustomJobExplorerFactoryBean customJobExplorerFactoryBean = new CustomJobExplorerFactoryBean();
        customJobExplorerFactoryBean.setDataSource(coreDataSource);
        customJobExplorerFactoryBean.setTransactionManager(coreTransactionManager);
        customJobExplorerFactoryBean.setTablePrefix(UNIDATA_BATCH_JOB_BATCH_TABLE_PREFIX);
        customJobExplorerFactoryBean.setSerializer(customJobExecutionContextSerializer);
        return customJobExplorerFactoryBean.getObject();
    }
    /**
     * The a job registry.
     * @return the job registry
     */
    @Bean
    public JobWithParamsRegistry jobRegistry(@Autowired Map<String, JobParameterProcessor> jobParameterProcessorsMap) {
        final JobWithParamsRegistryImpl jobWithParamsRegistry = new JobWithParamsRegistryImpl();
        jobWithParamsRegistry.setJobParameterProcessorsMap(jobParameterProcessorsMap);
        return jobWithParamsRegistry;
    }
    /**
     * Custom job beans post-processor.
     * @param jobRegistry the custom registry
     * @param jobGroupName the groupd name, if defined
     * @return BPP
     */
    @Bean
    public BeanPostProcessor jobBeansPostProcessor(
            @Autowired JobWithParamsRegistry jobRegistry,
            @Autowired(required = false) String jobGroupName) {
        CustomJobRegistryBeanPostProcessor cjpp = new CustomJobRegistryBeanPostProcessor();
        cjpp.setJobWithParamsRegistry(jobRegistry);
        cjpp.setGroupName(jobGroupName);
        return cjpp;
    }
    /**
     * Thread pool for launching jobs.
     * @param coreJobPoolSize start size param
     * @param maxJobPoolSize max size param
     * @param jobQueueCapacity queue capacity param
     * @return thread pool executor
     */
    @Bean
    public ThreadPoolTaskExecutor jobThreadPoolTaskExecutor(
            @Value("${" + CoreConfigurationConstants.PROP_NAME_MIN_THREAD_POOL_SIZE + ":4}") final int coreJobPoolSize,
            @Value("${" + CoreConfigurationConstants.PROP_NAME_MAX_THREAD_POOL_SIZE + ":32}") final int maxJobPoolSize,
            @Value("${" + CoreConfigurationConstants.PROP_NAME_QUEUE_SIZE + ":100}") final int jobQueueCapacity) {

        final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(coreJobPoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxJobPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(jobQueueCapacity);

        return threadPoolTaskExecutor;
    }
    /**
     * Creates job luncher.
     * @param jobRepository the job repository
     * @return luncher
     */
    @Bean
    public JobLauncher jobLauncher(
            @Autowired final JobRepository jobRepository,
            @Autowired final ThreadPoolTaskExecutor jobThreadPoolTaskExecutor) {

        final SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
        simpleJobLauncher.setJobRepository(jobRepository);
        simpleJobLauncher.setTaskExecutor(jobThreadPoolTaskExecutor);

        return simpleJobLauncher;
    }

    @Bean
    public JobOperator jobOperator(
            @Autowired JobRepository jobRepository,
            @Autowired JobExplorer jobExplorer,
            @Autowired JobWithParamsRegistry jobRegistry,
            @Autowired JobLauncher jobLauncher) {
        SimpleJobOperator sjo = new SimpleJobOperator();
        sjo.setJobExplorer(jobExplorer);
        sjo.setJobRepository(jobRepository);
        sjo.setJobRegistry(jobRegistry);
        sjo.setJobLauncher(jobLauncher);
        return sjo;
    }
    /**
     * Provide job builders, since we do not import @EnableBatchProcessing
     * @param jobRepository the repository
     * @return Job factory
     * @throws Exception
     */
    @Bean
    public JobBuilderFactory jobBuilders(@Autowired final JobRepository jobRepository) {
        return new JobBuilderFactory(jobRepository);
    }
    /**
     * Provide step builders, since we do not import @EnableBatchProcessing
     * @param jobRepository the repository
     * @return Step factory
     * @throws Exception
     */
    @Bean
    public StepBuilderFactory stepBuilders(@Autowired final JobRepository jobRepository) {
        return new StepBuilderFactory(jobRepository, coreTransactionManager);
    }
    // Have to do this in the same fashion, as done in
    // AbstractBatchConfiguration.class, since we do not use it directly
    @Bean
    public static StepScope stepScope() {
        StepScope stepScope = new StepScope();
        stepScope.setAutoProxy(false);
        return stepScope;
    }

    @Bean
    public static JobScope jobScope() {
        JobScope jobScope = new JobScope();
        jobScope.setAutoProxy(false);
        return jobScope;
    }
}
