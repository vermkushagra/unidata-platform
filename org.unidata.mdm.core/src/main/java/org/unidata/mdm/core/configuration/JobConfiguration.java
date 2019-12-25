package org.unidata.mdm.core.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.unidata.mdm.core.configuration.job.CustomJobExecutionContextSerializer;
import org.unidata.mdm.core.configuration.job.CustomJobExplorerFactoryBean;
import org.unidata.mdm.core.configuration.job.CustomJobRepositoryFactoryBean;
import org.unidata.mdm.core.configuration.job.JobWithParamsRegistryImpl;
import org.unidata.mdm.core.configuration.job.UnidataSchedulerFactoryBean;
import org.unidata.mdm.core.service.job.JobParameterProcessor;

/**
 * @author Alexander Malyshev
 */
@Configuration
// @Import(JobConfigurationBeans.class)
public class JobConfiguration /* extends DefaultBatchConfigurer */ {
    /**
     * Custom table prefix.
     */
    public static final String UNIDATA_BATCH_JOB_BATCH_TABLE_PREFIX = "batch_";
    /**
     * Minimal pool size.
     */
    @Value("${" + CoreConfigurationConstants.PROP_NAME_MIN_THREAD_POOL_SIZE + ":4}")
    private int coreJobPoolSize;
    /**
     * Maximum pool size.
     */
    @Value("${" + CoreConfigurationConstants.PROP_NAME_MAX_THREAD_POOL_SIZE + ":32}")
    private int maxJobPoolSize;
    /**
     * Queue capacity.
     */
    @Value("${" + CoreConfigurationConstants.PROP_NAME_QUEUE_SIZE + ":100}")
    private int jobQueueCapacity;
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
    /**
     * Core app context.
     */
//    @Autowired
//    private ApplicationContext context;

    /*
    @Bean
    public CustomJobRepositoryFactoryBean jobRepositoryFactoryBean(
            final DataSource coreDataSource,
            final PlatformTransactionManager coreTransactionManager
    ) {
        final CustomJobRepositoryFactoryBean customJobRepositoryFactoryBean = new CustomJobRepositoryFactoryBean();
        customJobRepositoryFactoryBean.setDataSource(coreDataSource);
        customJobRepositoryFactoryBean.setTransactionManager(coreTransactionManager);
        customJobRepositoryFactoryBean.setDatabaseType("postgres");
        customJobRepositoryFactoryBean.setTablePrefix(UNIDATA_BATCH_JOB_BATCH_TABLE_PREFIX);
        customJobRepositoryFactoryBean.setSerializer(new CustomJobExecutionContextSerializer());
        return customJobRepositoryFactoryBean;
    }
    */
    /*
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
    */
    /*
    @Bean
    public AbstractJobExplorerFactoryBean jobExplorerFactoryBean(
            final DataSource coreDataSource,
            final PlatformTransactionManager coreTransactionManager,
            final ExecutionContextSerializer executionContextSerializer
    ) {
        final CustomJobExplorerFactoryBean customJobExplorerFactoryBean = new CustomJobExplorerFactoryBean();
        customJobExplorerFactoryBean.setDataSource(coreDataSource);
        customJobExplorerFactoryBean.setTransactionManager(coreTransactionManager);
        customJobExplorerFactoryBean.setTablePrefix(UNIDATA_BATCH_JOB_BATCH_TABLE_PREFIX);
        customJobExplorerFactoryBean.setSerializer(executionContextSerializer);
        return customJobExplorerFactoryBean;
    }
    */
    /*
    @Bean
    public ListableJobLocator jobRegistry(final Map<String, JobParameterProcessor> jobParameterProcessorsMap) {
        final JobWithParamsRegistryImpl jobWithParamsRegistry = new JobWithParamsRegistryImpl();
        jobWithParamsRegistry.setJobParameterProcessorsMap(jobParameterProcessorsMap);
        return jobWithParamsRegistry;
    }
    */

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        return new UnidataSchedulerFactoryBean();
    }

    // Force batch to use the core DS, which otherwise would be memory map.
//    @Override
//    @Autowired(required = false)
//    public void setDataSource(final @Qualifier("coreDataSource") DataSource coreDataSource) {
//        super.setDataSource(coreDataSource);
//    }

    // SB section
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
    public JobRepository jobRepository() throws Exception {
        final CustomJobRepositoryFactoryBean customJobRepositoryFactoryBean = new CustomJobRepositoryFactoryBean();
        customJobRepositoryFactoryBean.setDataSource(coreDataSource);
        customJobRepositoryFactoryBean.setTransactionManager(coreTransactionManager);
        customJobRepositoryFactoryBean.setDatabaseType("postgres");
        customJobRepositoryFactoryBean.setTablePrefix(UNIDATA_BATCH_JOB_BATCH_TABLE_PREFIX);
        customJobRepositoryFactoryBean.setSerializer(new CustomJobExecutionContextSerializer());
        return customJobRepositoryFactoryBean.getObject();
    }
    /**
     * Creates a job explorer.
     * @return a job explorer
     * @throws Exception
     */
    @Bean
    public JobExplorer jobExplorer() throws Exception {
        final CustomJobExplorerFactoryBean customJobExplorerFactoryBean = new CustomJobExplorerFactoryBean();
        customJobExplorerFactoryBean.setDataSource(coreDataSource);
        customJobExplorerFactoryBean.setTransactionManager(coreTransactionManager);
        customJobExplorerFactoryBean.setTablePrefix(UNIDATA_BATCH_JOB_BATCH_TABLE_PREFIX);
        customJobExplorerFactoryBean.setSerializer(new CustomJobExecutionContextSerializer());
        return customJobExplorerFactoryBean.getObject();
    }
    /**
     * The a job registry.
     * @return the job registry
     */
    @Bean
    public JobRegistry jobRegistry(@Autowired Map<String, JobParameterProcessor> jobParameterProcessorsMap) {
        final JobWithParamsRegistryImpl jobWithParamsRegistry = new JobWithParamsRegistryImpl();
        jobWithParamsRegistry.setJobParameterProcessorsMap(jobParameterProcessorsMap);
        return jobWithParamsRegistry;
    }
    /**
     * Creates job luncher.
     * @param jobRepository the job repository
     * @return luncher
     */
    @Bean
    public JobLauncher jobLauncher(@Autowired final JobRepository jobRepository) {

        final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(coreJobPoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxJobPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(jobQueueCapacity);

        final SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
        simpleJobLauncher.setJobRepository(jobRepository);
        simpleJobLauncher.setTaskExecutor(threadPoolTaskExecutor);

        return simpleJobLauncher;
    }

    @Bean
    public JobOperator jobOperator(
            @Autowired JobRepository jobRepository,
            @Autowired JobExplorer jobExplorer,
            @Autowired JobRegistry jobRegistry,
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
}
