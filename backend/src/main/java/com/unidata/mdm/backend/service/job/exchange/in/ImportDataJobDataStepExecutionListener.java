package com.unidata.mdm.backend.service.job.exchange.in;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.hazelcast.core.HazelcastInstance;
import com.unidata.mdm.backend.exchange.def.ExchangeDefinition;
import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.service.data.batch.BatchSetSize;
import com.unidata.mdm.backend.service.job.ComplexJobParameterHolder;
import com.unidata.mdm.backend.service.job.JobCommonParameters;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * @author Mikhail Mikhailov
 * Stuff common to all master step listeners.
 */
public abstract class ImportDataJobDataStepExecutionListener implements InitializingBean {
    /**
     * Logger
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(ImportDataJobConstants.IMPORT_JOB_LOGGER_NAME);
    /**
     * Copy block size.
     */
    protected static final long COPY_BLOCK = 1000000;
    /**
     * This run id.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_RUN_ID + "]}")
    protected String runId;
    /**
     * The set size hint.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_DATA_SET_SIZE + "]}")
    protected BatchSetSize dataSetSize;
    /**
     * New objects only.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_INITIAL_LOAD + "]}")
    protected boolean initialLoad;
    /**
     * Rebuild index disabled.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_SKIP_INDEX_REBUILD +  "]}")
    protected boolean skipIndexRebuild;
    /**
     * Index tablespace.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_INDEX_TABLESPACE +  "]}")
    protected String indexTablespace;
    /**
     * HZ instance.
     */
    @Autowired
    protected HazelcastInstance hazelcastInstance;
    /**
     * Search service.
     */
    @Autowired
    protected SearchServiceExt searchService;
    /**
     * MMS.
     */
    @Autowired
    protected MetaModelServiceExt metaModelService;
    /**
     * Complex parameters holder.
     */
    @Autowired
    protected ComplexJobParameterHolder complexParametersHolder;
    /**
     * Unidata data source.
     */
    @Qualifier("unidataDataSource")
    @Autowired
    protected DataSource unidataDataSource;
    /**
     * DDL statements.
     */
    @Qualifier("import-data-job-sql")
    @Autowired
    protected Properties sql;
    /**
     * UD default TX manager.
     */
    @Qualifier("transactionManager")
    @Autowired
    protected PlatformTransactionManager txManager;
    /**
     * TX template.
     */
    protected TransactionTemplate txTemplate;
    /**
     * JDBC template.
     */
    protected JdbcTemplate jdbcTemplate;
    /**
     * Row mapper for index processing.
     */
    private static final RowMapper<Pair<Long, String>> INDEX_ROW_MAPPER = (rs, rowNum)  -> {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        return new ImmutablePair<Long, String>(id, name);
    };
    /**
     * Reindex executor.
     */
    protected ExecutorService fixedExecutor = Executors.newFixedThreadPool(4);
    /**
     * Constructs the table prefix.
     * @return string
     */
    protected String getTablePrefix() {
        return StringUtils.replace(runId, "-", "");
    }
    /**
     * Inserts table data (initial load).
     * @param targetTable target table
     * @param sourceTable source table
     * @param insertQuery the insert query
     * @param switchIndexesOff TODO
     */
    protected void insertTableData(String targetTable, String sourceTable, String insertQuery, boolean switchIndexesOff) {

        // 1. Switch autovacuum off
        long start = System.currentTimeMillis();
        LOGGER.info("Disable autovacuum on source {}.", sourceTable);
        jdbcTemplate.execute(new StringBuilder()
                .append("alter table ")
                .append(sourceTable)
                .append(" set (autovacuum_enabled = false, toast.autovacuum_enabled = false)")
                .toString());

        LOGGER.info("Disable autovacuum on target {}.", targetTable);
        jdbcTemplate.execute(new StringBuilder()
                .append("alter table ")
                .append(targetTable)
                .append(" set (autovacuum_enabled = false, toast.autovacuum_enabled = false)")
                .toString());

        try {

            // 2. Switch index updates off
            if (switchIndexesOff) {
                List<Pair<Long, String>> indexData = jdbcTemplate.query(sql.getProperty("getUnidataIndexesSQL"), INDEX_ROW_MAPPER, targetTable);
                if (!indexData.isEmpty()) {
                    StringBuilder turnOff =  new StringBuilder()
                            .append("update pg_index set indisready = false where indexrelid in (");
                    for (int i = 0; i <  indexData.size(); i++) {
                        Long id = indexData.get(i).getLeft();
                        turnOff
                            .append(id)
                            .append(i < (indexData.size() - 1) ? ", " : ")");
                    }

                    jdbcTemplate.execute(turnOff.toString());
                }
            }

            // 3. Copy blocks
            List<Pair<Integer, Integer>> blocks  = getBlocks(sourceTable);
            List<Future<Boolean>> result = new ArrayList<>(blocks.size());
            for (Pair<Integer, Integer> block : blocks) {
                result.add(
                    fixedExecutor.submit(() -> {
                        try {
                            jdbcTemplate.execute("set work_mem = '2GB'");
                            jdbcTemplate.execute("set maintenance_work_mem = '4GB'");
                            jdbcTemplate.execute("set enable_seqscan = off");
                            jdbcTemplate.query(insertQuery, rs -> {}, block.getLeft(), block.getRight());
                            return true;
                        } catch (Exception e) {
                            LOGGER.warn("Block insert into {}, [offset: {}, size: {}] failed.",
                                    targetTable, block.getLeft(), block.getRight(), e);
                        } finally {
                            jdbcTemplate.execute("set work_mem to default");
                            jdbcTemplate.execute("set maintenance_work_mem to default");
                            jdbcTemplate.execute("set enable_seqscan to default");
                        }

                        return false;
                    }));
            }

            for (Future<Boolean> f : result) {
                try {
                    f.get();
                } catch (Exception e) {
                    LOGGER.warn("Future get failed.", e);
                }
            }
        } finally {

            // 4. Switch autovacuum off
            LOGGER.info("Enable autovacuum on {}.", targetTable);
            jdbcTemplate.execute(new StringBuilder().append("alter table ")
                    .append(targetTable)
                    .append(" set (autovacuum_enabled = true, toast.autovacuum_enabled = true)")
                    .toString());
        }

        LOGGER.info("Finished processing {} in {} millis.", targetTable, System.currentTimeMillis() - start);
    }

    /**
     * Update block.
     * @param targetTable the target table
     * @param sourceTable source table
     * @param updateQuery the update query
     */
    protected void updateTableData(String targetTable, String sourceTable, String updateQuery) {

        long start = System.currentTimeMillis();

        LOGGER.info("Disable autovacuum on source '{}'.", sourceTable);
        jdbcTemplate.execute(new StringBuilder()
                .append("alter table ")
                .append(sourceTable)
                .append(" set (autovacuum_enabled = false, toast.autovacuum_enabled = false)")
                .toString());

        LOGGER.info("Disable autovacuum on target '{}'.", targetTable);
        jdbcTemplate.execute(new StringBuilder()
                .append("alter table ")
                .append(targetTable)
                .append(" set (autovacuum_enabled = false, toast.autovacuum_enabled = false)")
                .toString());

        try {

            try {
                jdbcTemplate.execute(updateQuery);
            } catch (Exception e) {
                LOGGER.warn("Query failed.", e);
            }

        } finally {
            LOGGER.info("Enable autovacuum on {}.", targetTable);
            jdbcTemplate.execute(new StringBuilder().append("alter table ")
                    .append(targetTable)
                    .append(" set (autovacuum_enabled = true, toast.autovacuum_enabled = true)")
                    .toString());
        }

        LOGGER.info("Finished processing {} in {} millis.", targetTable, System.currentTimeMillis() - start);
    }

    protected void buildIndexes(String tableName) {

        List<Pair<Long, String>> indexData = jdbcTemplate.query(sql.getProperty("getUnidataIndexesSQL"), INDEX_ROW_MAPPER, tableName);
        if (!indexData.isEmpty()) {

            List<Future<Boolean>> result = new ArrayList<>(indexData.size());
            for (Pair<Long, String> index : indexData) {

                final String name = index.getRight();
                result.add(fixedExecutor.submit(() -> {

                    long millis = System.currentTimeMillis();
                    LOGGER.info("Rebuilding index {} on table {}.", name, tableName);

                    jdbcTemplate.execute("set work_mem = '2GB'");
                    jdbcTemplate.execute("set maintenance_work_mem = '4GB'");
                    jdbcTemplate.execute("set enable_seqscan = off");
                    try {
                        String tempName = name + "_" + StringUtils.replace(runId, "-", "");
                        String indexDef
                            = jdbcTemplate.queryForObject(
                                new StringBuilder().append("select pg_get_indexdef as def from pg_get_indexdef('")
                                .append(name)
                                .append("'::regclass)")
                                .toString(),
                            String.class);
                        String createStatement = StringUtils.replace(indexDef, name, tempName);

                        if (StringUtils.isNotBlank(indexTablespace)) {
                            createStatement = new StringBuilder()
                                    .append(createStatement)
                                    .append(" tablespace ")
                                    .append(indexTablespace)
                                    .toString();
                        }

                        jdbcTemplate.execute(createStatement);

                        jdbcTemplate.execute(new StringBuilder().append("drop index if exists ").append(name).toString());
                        jdbcTemplate.execute(new StringBuilder().append("alter index if exists ").append(tempName).append(" rename to ").append(name).toString());

                        LOGGER.info("Finished rebuilding index {} in table {} in {} millis.", name, tableName, System.currentTimeMillis() - millis);

                        return true;
                    } catch (Exception e) {
                        LOGGER.warn("Exception caught.", e);
                    } finally {
                        jdbcTemplate.execute("set work_mem to default");
                        jdbcTemplate.execute("set maintenance_work_mem to default");
                        jdbcTemplate.execute("set enable_seqscan to default");
                    }

                    return false;
                }));
            }

            for (Future<Boolean> f : result) {
                try {
                    f.get();
                } catch (Exception e) {
                    LOGGER.warn("Future get failed.", e);
                }
            }

            LOGGER.info("Finished index building for table {}.", tableName);
        }
    }

    private List<Pair<Integer, Integer>> getBlocks(String sourceTable) {

        long rowCount = jdbcTemplate.queryForObject(
            new StringBuilder()
                .append("select coalesce(max(row_id), 0) from ")
                .append(sourceTable).toString(), Long.class);

        if (rowCount == 0L) {
            return Collections.emptyList();
        } else if (rowCount <= COPY_BLOCK) {
            return Collections.singletonList(new ImmutablePair<Integer, Integer>(Integer.valueOf(1), (int) COPY_BLOCK));
        }

        List<Pair<Integer, Integer>> blocks = new ArrayList<>((int) ((rowCount / COPY_BLOCK) + 1));
        int counter = 1;
        while (rowCount > 0) {

            blocks.add(new ImmutablePair<Integer, Integer>(counter, (int) (rowCount > COPY_BLOCK ? COPY_BLOCK : rowCount)));
            counter += rowCount > COPY_BLOCK ? COPY_BLOCK : rowCount;
            rowCount -= COPY_BLOCK;
        }

        return blocks;
    }

    /**
     * Copy data.
     * @param tableName target table
     * @param insertQuery insert query
     * @param updateQuery update query
     * @param relations relations or records
     * @param rebuildIndexes whether to rebuild indexes
     */
    protected void copyTableData(String tableName, String insertQuery, String updateQuery, boolean relations, boolean rebuildIndexes) {

        long start = System.currentTimeMillis();
        LOGGER.info("Disable autovacuum on {}.", tableName);
        jdbcTemplate.execute(new StringBuilder()
                .append("alter table ")
                .append(tableName)
                .append(" set (autovacuum_enabled = false, toast.autovacuum_enabled = false)")
                .toString());

        try {

            if (Objects.nonNull(insertQuery)) {

                // Some data exists
                if (!rebuildIndexes) {
                    jdbcTemplate.execute(insertQuery);
                // Initial
                } else {

                    List<Pair<Long, String>> indexData = jdbcTemplate.query(sql.getProperty("getUnidataIndexesSQL"), INDEX_ROW_MAPPER, tableName);
                    if (!indexData.isEmpty()) {
                        StringBuilder turnOff =  new StringBuilder()
                                .append("update pg_index set indisready = false where indexrelid in (");
                        for (int i = 0; i <  indexData.size(); i++) {
                            Long id = indexData.get(i).getLeft();
                            turnOff
                                .append(id)
                                .append(i < (indexData.size() - 1) ? ", " : ")");
                        }

                        jdbcTemplate.execute(turnOff.toString());
                    }

                    jdbcTemplate.execute(insertQuery);

                    if (!indexData.isEmpty()) {
                        final CountDownLatch latch = new CountDownLatch(indexData.size());
                        for (Pair<Long, String> index : indexData) {

                            final String name = index.getRight();
                            fixedExecutor.execute(() -> {
                                long millis = System.currentTimeMillis();
                                LOGGER.info("Rebuilding index {} on table {}.", name, tableName);

                                String tempName = name + "_" + StringUtils.replace(runId, "-", "");
                                String indexDef
                                    = jdbcTemplate.queryForObject(
                                        new StringBuilder().append("select pg_get_indexdef as def from pg_get_indexdef('")
                                        .append(name)
                                        .append("'::regclass)")
                                        .toString(),
                                    String.class);
                                String createStatement = StringUtils.replace(indexDef, name, tempName);

                                if (StringUtils.isNotBlank(indexTablespace)) {
                                    createStatement = new StringBuilder()
                                            .append(createStatement)
                                            .append(" tablespace ")
                                            .append(indexTablespace)
                                            .toString();
                                }

                                jdbcTemplate.execute(createStatement);
                                jdbcTemplate.execute(new StringBuilder().append("drop index if exists ").append(name).toString());
                                jdbcTemplate.execute(new StringBuilder().append("alter index if exists ").append(tempName).append(" rename to ").append(name).toString());

                                LOGGER.info("Finished rebuilding index {} in table {} in {} millis.", name, tableName, System.currentTimeMillis() - millis);
                                latch.countDown();
                            });
                        }

                        try {
                            latch.await();
                            LOGGER.info("Finished index building for table {}.", tableName);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            LOGGER.error("Interrupted while reindexing {} in parallel.", tableName, e);
                        }
                    }
                }
            }

            // 2. Update
            if (Objects.nonNull(updateQuery)) {
                jdbcTemplate.execute(updateQuery);
            }

        } finally {

            LOGGER.info("Enable autovacuum on {}.", tableName);
            jdbcTemplate.execute(new StringBuilder().append("alter table ")
                    .append(tableName)
                    .append(" set (autovacuum_enabled = true, toast.autovacuum_enabled = true)")
                    .toString());
        }

        LOGGER.info("Finished processing {} in {} millis.", tableName, System.currentTimeMillis() - start);
    }

    /**
     * Copy collected data.
     */
    protected abstract void copyCollectedData();

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        txTemplate = new TransactionTemplate(txManager);
        jdbcTemplate = new JdbcTemplate(unidataDataSource);
        jdbcTemplate.afterPropertiesSet();
    }
    /**
     * Do before mass indexing.
     * @param stepExecution the step execution
     */
    public void resetSearchIndexesBeforeInitialLoad(StepExecution stepExecution) {

        final String definitionKey = stepExecution.getJobParameters().getString(JobCommonParameters.PARAM_DEFINITION);
        ExchangeDefinition def = complexParametersHolder.getComplexParameter(definitionKey);

        /*
         * ("index.refresh_interval", "-1"); // Disable refresh
         * ("index.warmer.enabled", Boolean.FALSE); // Disable warmers
         */
        Map<String, Object> indexParams = Collections.singletonMap("index.refresh_interval", "-1");
        for (ExchangeEntity entity : def.getEntities()) {
            if (metaModelService.isEntity(entity.getName())) {
                // searchService.closeIndex(entity.getName(), SecurityUtils.getCurrentUserStorageId());
                searchService.setIndexSettings(entity.getName(), SecurityUtils.getCurrentUserStorageId(), indexParams);
                // searchService.openIndex(entity.getName(), SecurityUtils.getCurrentUserStorageId());
            }
        }

        /*
         * ("indices.memory.index_buffer_size", "40%"); // Increase indexing buffer size
         * ("indices.store.throttle.type", "none"); // None throttling.
         */
        searchService.setClusterSettings(Collections.singletonMap("indices.store.throttle.type", "none"), false);
    }
    /**
     * Do after mass indexing.
     * @param stepExecution the step execution
     */
    public void resetSearchIndexesAfterInitialLoad(StepExecution stepExecution) {

        final String definitionKey = stepExecution.getJobParameters().getString(JobCommonParameters.PARAM_DEFINITION);
        ExchangeDefinition def = complexParametersHolder.getComplexParameter(definitionKey);

        /*
         * ("index.refresh_interval", "1s"); // Enable refresh
         * ("index.warmer.enabled", Boolean.TRUE); // Enable warmers
         */
        Map<String, Object> indexParams = Collections.singletonMap("index.refresh_interval", "1s");
        for (ExchangeEntity entity : def.getEntities()) {
            if (metaModelService.isEntity(entity.getName())) {
                // searchService.closeIndex(entity.getName(), SecurityUtils.getCurrentUserStorageId());
                searchService.setIndexSettings(entity.getName(), SecurityUtils.getCurrentUserStorageId(), indexParams);
                // searchService.openIndex(entity.getName(), SecurityUtils.getCurrentUserStorageId());
                searchService.refreshIndex(entity.getName(), SecurityUtils.getCurrentUserStorageId(), false);
            }
        }

        /*
         * ("indices.memory.index_buffer_size", "10%"); // Decrease indexing buffer size
         * ("indices.store.throttle.type", "merge"); // Merge throttling.
         */
        searchService.setClusterSettings(Collections.singletonMap("indices.store.throttle.type", "merge"), false);
    }
}
