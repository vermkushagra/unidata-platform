package com.unidata.mdm.backend.service.job.importJob;

import static com.unidata.mdm.backend.jdbc.DataSourceUtil.initSingleDataSource;
import static java.util.Objects.nonNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.exchange.def.ExchangeTemporalFieldTransformer;
import com.unidata.mdm.backend.exchange.def.VersionRange;
import com.unidata.mdm.backend.service.job.ComplexJobParameterHolder;
import com.unidata.mdm.backend.service.job.JobCommonParameters;
import com.unidata.mdm.backend.util.CryptUtils;
import com.unidata.mdm.backend.util.IdUtils;

/**
 * Class responsible for making partitions.
 *
 * @param <T> - table definition class!
 */
public abstract class ImportPartitioner<T> implements Partitioner {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ImportPartitioner.class);

    public static final String LAST_FIELD_INDICATOR = "_" + CryptUtils.toMurmurString(IdUtils.v4String());

    private static final long DEFAULT_BATCH_SIZE = 500;

    private static final String PARTITION_KEY = "partition";

    protected String databaseUrl;

    protected DataSource dataSource;

    private String definitionKey;

    private long batchSize;

    private long quantityOfProcessedRecords;

    private long offset;

    protected T baseDefinition;

    private String operationId;

    @Autowired
    private ComplexJobParameterHolder jobParameterHolder;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        //TODO think because it should be persist
        T definition = baseDefinition == null ? jobParameterHolder.getComplexParameter(definitionKey) : baseDefinition;
        if (definition == null) {
            return Collections.emptyMap();
        }
        long recordsToProcess = quantityOfProcessedRecords <= 0 ? getRecordsCount(definition) : quantityOfProcessedRecords;
        LOGGER.info("Records count for entity {} is {}", getEntityName(definition), recordsToProcess);
        if (recordsToProcess == 0) {
            return Collections.emptyMap();
        }

        long startOffset = offset == -1 ? 0 : offset;
        long bulkSize = getBulkSize();
        long offset = startOffset;
        Map<String, ExecutionContext> result = new LinkedHashMap<>((int) (recordsToProcess / bulkSize) + 1);
        int partition = 0;
        while (recordsToProcess > 0) {
            final ExecutionContext context = new ExecutionContext();
            context.putString("sql", getSql(definition, offset, bulkSize));
            context.putInt("bulkSize", (int) bulkSize);
            context.putString("databaseUrl", databaseUrl);
            context.putString("operationId", operationId);
            context.put("dataSource", this.dataSource);
            context.putInt("partition", partition);
            addSpecialStepParameters(context, definition);
            result.put(PARTITION_KEY + partition, context);

            partition++;
            recordsToProcess -= bulkSize;
            offset += bulkSize;
        }
        LOGGER.info("Number of partitions is {}", result.size());
        return result;
    }

    public long getBulkSize() {
        return batchSize == -1 ? DEFAULT_BATCH_SIZE : batchSize;
    }

    public abstract String getEntityName(T baseDefinition);

    /**
     * Special section for put
     *
     * @param context    - execution context
     * @param definition - table definition
     */
    protected abstract void addSpecialStepParameters(ExecutionContext context, T definition);

    /**
     * Get prepared for select request.
     *
     * @param tableDefinition the table definition
     * @param offset          the offset
     * @param limit           the limit
     * @return SQl request
     */
    @Nonnull
    protected abstract String getSql(@Nonnull T tableDefinition, long offset, long limit);

    /**
     * @param tableDefinition class which contains overall information about table
     * @return list of affected tables
     */
    @Nonnull
    protected abstract List<String> getTables(@Nonnull T tableDefinition);

    /**
     * @param tableDefinition class which contains overall information about table
     * @return list of joins which should be used during request to DB.
     */
    @Nonnull
    protected abstract List<String> getJoins(@Nonnull T tableDefinition);

    /**
     * @param tableDefinition - the table definition
     * @return count of records in the table
     */
    private long getRecordsCount(@Nonnull T tableDefinition) {
        List<String> tables = getTables(tableDefinition);
        List<String> joins = getJoins(tableDefinition);
        String countSql = getCountSql(tables, joins);
        return getRecordsCount(countSql);
    }

    /**
     * @param tables - affected tables
     * @param joins  - applied joins
     * @return SQL request which help found count of rows in table.
     */
    private String getCountSql(@Nonnull List<String> tables, @Nonnull List<String> joins) {
        return "select count(*) as CNT " + getFromSql(tables, joins);
    }

    /**
     * @param tables - affected tables
     * @param joins  - applied joins
     * @return SQL request which contains from path of SQl query
     */
    protected String getFromSql(@Nonnull List<String> tables, @Nonnull List<String> joins) {

        if (tables.isEmpty()) {
            throw new RuntimeException();
        }

        StringBuilder sqlb = new StringBuilder().append(" from ");

        for (int i = 0; i < tables.size(); i++) {
            String tbl = tables.get(i);
            sqlb.append(tbl).append(i < tables.size() - 1 ? ", " : " ");
        }

		for (int i = 0; i < joins.size(); i++) {
			String j = joins.get(i);
			if (j.contains("$$" + JobCommonParameters.PARAM_PREVIOUS_SUCCESS_START_DATE)) {
				if (jobParameterHolder
						.getComplexParameter(JobCommonParameters.PARAM_PREVIOUS_SUCCESS_START_DATE) != null) {
					JobParameterDTO lastDate = jobParameterHolder
							.getComplexParameter(JobCommonParameters.PARAM_PREVIOUS_SUCCESS_START_DATE);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
					String formatedDateTime = lastDate.getDateValue().format(formatter);
					String lastTimestamp = "to_timestamp('" + formatedDateTime + "', 'YYYY-MM-DD HH24:MI:SS.MS')";
					j = j.replace("$$" + JobCommonParameters.PARAM_PREVIOUS_SUCCESS_START_DATE, lastTimestamp);
					sqlb.append(i == 0 ? "where " : "and ").append(j).append(" ");
				}
			} else {
				sqlb.append(i == 0 ? "where " : "and ").append(j).append(" ");
			}
		}

        return sqlb.toString();
    }

    /**
     * @param sql - the sql
     * @return count of records in the table
     */
    private long getRecordsCount(@Nonnull String sql) {
        DataSource dataSource = this.dataSource == null ? initSingleDataSource(databaseUrl) : this.dataSource;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                return rs.getLong("CNT");
            }

        } catch (SQLException sqle) {
            LOGGER.error("SQL exception caught.", sqle);
        }
        return 0L;
    }

    protected void processVersionRange(@Nonnull ExecutionContext context, @Nullable VersionRange versionRange) {
        if (nonNull(versionRange)) {
            if (nonNull(versionRange.getValidFrom()) && nonNull(versionRange.getValidFrom().getValue())) {
                Date from = ExchangeTemporalFieldTransformer.ISO801MillisStringToDate(versionRange.getValidFrom().getValue().toString());
                context.put("from", from);
            }
            if (nonNull(versionRange.getValidTo()) && nonNull(versionRange.getValidTo().getValue())) {
                Date to = ExchangeTemporalFieldTransformer.ISO801MillisStringToDate(versionRange.getValidTo().getValue().toString());
                context.put("to", to);
            }
        }
    }

    public void setDefinitionKey(String definitionKey) {
        this.definitionKey = definitionKey;
    }

    public void setBaseDefinition(T baseDefinition) {
        this.baseDefinition = baseDefinition;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(long batchSize) {
        this.batchSize = batchSize;
    }

    public long getQuantityOfProcessedRecords() {
        return quantityOfProcessedRecords;
    }

    public void setQuantityOfProcessedRecords(long quantityOfProcessedRecords) {
        this.quantityOfProcessedRecords = quantityOfProcessedRecords;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getOperationId() {
        return operationId;
    }
}
