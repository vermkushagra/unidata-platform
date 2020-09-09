/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.service.job.exchange.in;

import static com.unidata.mdm.backend.jdbc.DataSourceUtil.initSingleDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import com.unidata.mdm.backend.service.job.JobCommonParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.hazelcast.core.HazelcastInstance;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.JobException;
import com.unidata.mdm.backend.dao.util.DatabaseVendor;
import com.unidata.mdm.backend.exchange.def.ContainmentRelation;
import com.unidata.mdm.backend.exchange.def.ExchangeRelation;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeEntity;
import com.unidata.mdm.backend.exchange.def.db.DbNaturalKey;
import com.unidata.mdm.backend.exchange.def.db.DbRelatesToRelation;
import com.unidata.mdm.backend.exchange.def.db.DbSystemKey;
import com.unidata.mdm.backend.service.job.JobUtil;

/**
 * @author Mikhail Mikhailov
 * Base type for partitioner classes.
 */
public abstract class ImportDataJobAbstractPartitioner {
    /**
     * Logger.
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(ImportDataJobConstants.IMPORT_JOB_LOGGER_NAME);
    /**
     * Import 'foreign' DB URL.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_DATABASE_URL + "]}")
    protected String databaseUrl;
    /**
     * DB vendor.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_IMPORT_DATABASE_VENDOR + "]}")
    protected DatabaseVendor databaseVendor;
    /**
     * The block (portion) size.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_BLOCK_SIZE + "]}")
    protected long blockSize;
    /**
     * Operation id.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_OPERATION_ID + "]}")
    protected String operationId;
    /**
     * The operation id to apply.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_RUN_ID + "]}")
    protected String runId;
    /**
     * Previous successful start date.
     */
    @Value("#{jobParameters[" +  JobCommonParameters.PARAM_PREVIOUS_SUCCESS_START_DATE + "]}")
    protected Date previousSuccessStartDate;
    /**
     * Hazelcast instance.
     */
    @Autowired
    protected HazelcastInstance hazelcastInstance;
    /**
     * Collects contexts for an entity.
     * @param startPartitionNumber
     * @param entity the exchange entity
     * @param exchangeObjectId the id of the exchange object in distributed map
     * @return context map
     */
    protected Map<String, ExecutionContext> collectEntityExecutionContexts(int startPartitionNumber, DbExchangeEntity entity,
            String exchangeObjectId) {

        long recordsInDb = getRecordsCount(entity);
        long recordsToProcess = recordsInDb;
        if (entity.getMaxRecordCount() > 0) {
            recordsToProcess = entity.getMaxRecordCount() > recordsInDb ? recordsInDb : entity.getMaxRecordCount();
        }

        LOGGER.info("Requested records count for entity {} is {}", entity.getName(), recordsToProcess);

        if (recordsToProcess == 0) {
            return Collections.emptyMap();
        }

        int partitionId = startPartitionNumber;
        long offset = 0L;
        long selectedBlockSize = blockSize <= 0 ? ImportDataJobConstants.DEFAULT_BATCH_SIZE : blockSize;
        if (selectedBlockSize > recordsToProcess) {
            selectedBlockSize = recordsToProcess;
        }

        Map<String, ExecutionContext> result = new LinkedHashMap<>((int) (recordsToProcess / selectedBlockSize) + 1);
        while (recordsToProcess > 0) {

            final ExecutionContext context = new ExecutionContext();

            // Item reader will use the same predicate generator
            // in case of historical queries
            context.putInt(ImportDataJobConstants.PARAM_OFFSET, (int) offset);
            context.putInt(ImportDataJobConstants.PARAM_BLOCK_SIZE, (int) selectedBlockSize);
            context.putInt(ImportDataJobConstants.PARAM_PARTITION_ID, partitionId);
            context.putString(ImportDataJobConstants.PARAM_EXCHANGE_OBJECT_ID, exchangeObjectId);
            context.putString(ImportDataJobConstants.PARAM_PARTITION_GROUP, entity.getName());

            result.put(JobUtil.partitionName(partitionId), context);

            partitionId++;
            recordsToProcess -= selectedBlockSize;
            offset += selectedBlockSize;
        }

        return result;
    }
    /**
     * Collect execution contexts for relation elements.
     * @param startPartitionNumber start partition number
     * @param relation the relation
     * @param exchangeObjectId the id of the exchange object in distributed map
     * @return context map
     */
    protected Map<String, ExecutionContext> collectRelationExecutionContexts(int startPartitionNumber, ExchangeRelation relation,
            String exchangeObjectId) {

        if (relation instanceof ContainmentRelation) {

            ContainmentRelation containment = (ContainmentRelation) relation;
            DbExchangeEntity target = (DbExchangeEntity) containment.getEntity();

            return collectEntityExecutionContexts(startPartitionNumber, target, exchangeObjectId);
        }

        DbRelatesToRelation target = (DbRelatesToRelation) relation;
        long recordsInDb = getRecordsCount(target);
        long recordsToProcess = recordsInDb;
        if (target.getMaxRecordCount() > 0) {
            recordsToProcess = target.getMaxRecordCount() > recordsInDb ? recordsInDb : target.getMaxRecordCount();
        }

        LOGGER.info("Requested records count for relation {} is {}", target.getRelation(), recordsToProcess);

        if (recordsToProcess == 0) {
            return Collections.emptyMap();
        }

        int partitionId = startPartitionNumber;
        long offset = 0;
        long selectedBlockSize = this.blockSize <= 0 ? ImportDataJobConstants.DEFAULT_BATCH_SIZE : this.blockSize;
        if (selectedBlockSize > recordsToProcess) {
            selectedBlockSize = recordsToProcess;
        }

        Map<String, ExecutionContext> result = new LinkedHashMap<>((int) (recordsToProcess / selectedBlockSize) + 1);
        while (recordsToProcess > 0) {

            final ExecutionContext context = new ExecutionContext();

            // Item reader will use the same predicate generator
            // in case of historical queries
            context.putInt(ImportDataJobConstants.PARAM_OFFSET, (int) offset);
            context.putInt(ImportDataJobConstants.PARAM_BLOCK_SIZE, (int) selectedBlockSize);
            context.putInt(ImportDataJobConstants.PARAM_PARTITION_ID, partitionId);
            context.putString(ImportDataJobConstants.PARAM_EXCHANGE_OBJECT_ID, exchangeObjectId);
            context.putString(ImportDataJobConstants.PARAM_PARTITION_GROUP, relation.getRelation());

            result.put(JobUtil.partitionName(partitionId), context);

            partitionId++;
            recordsToProcess -= selectedBlockSize;
            offset += selectedBlockSize;
        }

        return result;
    }
    /**
     * @param entity - the table definition
     * @return count of records in the table
     */
    private long getRecordsCount(@Nonnull DbExchangeEntity entity) {

        List<String> tables = entity.getTables() == null ? Collections.emptyList() : entity.getTables();
        if (tables.isEmpty()) {
            final String message = "Tables section for exchange entity {} is empty. Aborting.";
            LOGGER.warn(message, entity.getName());
            throw new JobException(message, ExceptionId.EX_DATA_IMPORT_ENTITIES_NO_TABLES_TO_PROCESS, entity.getName());
        }

        String selectSql;
        if (entity.isMultiVersion()) {
            String keyColumn = entity.getXlsxKey();
            if (Objects.isNull(keyColumn)) {
                keyColumn = Objects.nonNull(entity.getNaturalKey())
                    ? ((DbNaturalKey) entity.getNaturalKey()).getColumn()
                    : ((DbSystemKey) entity.getSystemKey()).getColumn();
            }
            selectSql = "distinct " +  keyColumn;
        } else {
            selectSql = "*";
        }

        List<String> joins = entity.getJoins() == null ? Collections.emptyList() : entity.getJoins();
        String countSql = "select count(" + selectSql + ") as CNT " + ImportDataJobUtils.getFromSql(tables, joins, databaseVendor, previousSuccessStartDate);

        return getRecordsCount(countSql);
    }
    /**
     * @param relation - the table definition
     * @return count of records in the table
     */
    private long getRecordsCount(@Nonnull DbRelatesToRelation relation) {

        List<String> tables = relation.getTables() == null ? Collections.emptyList() : relation.getTables();
        if (tables.isEmpty()) {
            final String message = "Tables section for exchange 'rel to' relation {} is empty. Aborting.";
            LOGGER.warn(message, relation.getRelation());
            throw new JobException(message, ExceptionId.EX_DATA_IMPORT_RELATIONS_NO_TABLES_TO_PROCESS, relation.getRelation());
        }

        String selectSql;
        if (relation.isMultiVersion()) {
            String keyColumn = Objects.nonNull(relation.getFromNaturalKey())
                    ? ((DbNaturalKey) relation.getFromNaturalKey()).getColumn()
                    : ((DbSystemKey) relation.getFromSystemKey()).getColumn();

            selectSql = "distinct " +  keyColumn;
        } else {
            selectSql = "*";
        }

        List<String> joins = relation.getJoins() == null ? Collections.emptyList() : relation.getJoins();
        String countSql = new StringBuilder()
                .append("select count(")
                .append(selectSql)
                .append(") as CNT ")
                .append(ImportDataJobUtils.getFromSql(tables, joins, databaseVendor, previousSuccessStartDate))
                .toString();

        return getRecordsCount(countSql);
    }


    /**
     * @param sql - the sql
     * @return count of records in the table
     */
    private long getRecordsCount(@Nonnull String sql) {

        DataSource ds = initSingleDataSource(databaseUrl);
        try (Connection connection = ds.getConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException sqle) {
            final String message = "Count records/ partition size failed. SQL [{}].";
            LOGGER.error(message, sql, sqle);
            throw new DataProcessingException(message, sqle, ExceptionId.EX_DATA_IMPORT_COUNT_PARITION_SIZE_FAILED, sql);
        }

        return 0L;
    }
    /**
     * Sets the bulk size.
     * @param batchSize
     */
    public void setBlockSize(long batchSize) {
        this.blockSize = batchSize;
    }
    /**
     * Sets the DB URL.
     * @param databaseUrl
     */
    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }
    /**
     * Sets the operationId
     * @param operationId
     */
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }
    /**
     * @param runId the runId to set
     */
    public void setRunId(String runId) {
        this.runId = runId;
    }
}
