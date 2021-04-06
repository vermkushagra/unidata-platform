package com.unidata.mdm.backend.service.job.exchange.out;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.unidata.mdm.backend.dao.util.VendorUtils;
import com.unidata.mdm.backend.exchange.def.ExchangeDefinition;
import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.exchange.def.ExchangeObject;
import com.unidata.mdm.backend.exchange.def.ExchangeRelation;
import com.unidata.mdm.backend.service.job.ComplexJobParameterHolder;
import com.unidata.mdm.backend.service.job.JobUtil;

/**
 * @author Mikhail Mikhailov
 * Export (exchange) partitioner.
 */
@Component("exportPartitionerSingleton")
public class ExportDataPartitioner implements Partitioner {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportDataConstants.EXPORT_JOB_LOGGER_NAME);
    /**
     * Exchange definition, which may be set via param.
     */
    private ExchangeDefinition definition;
    /**
     * Definition key for complex parameter holder.
     */
    private String definitionKey;
    /**
     * Block size hint.
     */
    private Long blockSize;
    /**
     * The operation id to apply.
     */
    private String operationId;
    /**
     * The operation id to apply.
     */
    private String runId;
    /**
     * Data source.
     */
    @Qualifier("unidataDataSource")
    @Autowired
    private DataSource unidataDataSource;
    /**
     * Complex parameter holder.
     */
    @Autowired
    private ComplexJobParameterHolder jobParameterHolder;
    /**
     * Hazelcast instance.
     */
    @Autowired
    private HazelcastInstance hazelcastInstance;
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        ExchangeDefinition def = this.definition == null ? jobParameterHolder.getComplexParameter(definitionKey) : this.definition;
        if (def == null) {
            LOGGER.warn("No input. Exchange definition [JSON mapping] was not set. Exiting.");
            return Collections.emptyMap();
        }

        final Map<String, ExecutionContext> result = new HashMap<>();
        final Map<String, ExchangeObject> entities = new HashMap<>();
        if (!CollectionUtils.isEmpty(def.getLookupEntities())) {
            entities.putAll(def.getLookupEntities().stream().collect(Collectors.toMap(ExchangeEntity::getName, le -> le)));
        }

        if (!CollectionUtils.isEmpty(def.getEntities())) {
            entities.putAll(def.getEntities().stream().collect(Collectors.toMap(ExchangeEntity::getName, ee -> ee)));
        }

        List<String> typeNames = entities.values().stream().map(eo -> (ExchangeEntity) eo).map(ExchangeEntity::getName).collect(Collectors.toList());
        String typesAsArray = VendorUtils.textArray(typeNames);
        final StringBuilder sqlb = new StringBuilder()
                .append("select block_num, start_id, start_gsn, end_id, end_gsn, name from ud_calc_records_etalon_batch_blocks(null, ")
                .append(blockSize.intValue())
                .append(", ")
                .append(typesAsArray)
                .append(", null, null) order by block_num");

        result.putAll(collectExecutionContexts(result.size(), sqlb.toString(), entities));
        LOGGER.info("Finished partitioning of {}.", typeNames);

        return result;
    }
    /**
     * Collects blocks.
     * @param startPartitionNumber partition number
     * @param sql the sql
     * @param exchangeObject the entity
     * @return
     */
    private Map<String, ExecutionContext> collectExecutionContexts(
            int startPartitionNumber, String sql, Map<String, ExchangeObject> exchangeObjects) {

        int number = startPartitionNumber;
        final Map<String, ExecutionContext> result = new HashMap<>();
        try (Connection connection = unidataDataSource.getConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
             ResultSet cursor = statement.executeQuery(sql)) {

            while (cursor.next()) {
                final String partitionId = "partition" + number;
                final ExecutionContext value = new ExecutionContext();
                result.put(JobUtil.partitionName(number), value);

                String startId = cursor.getString("start_id");
                String endId = cursor.getString("end_id");
                Long startGSN = cursor.getLong("start_gsn");
                Long endGSN = cursor.getLong("end_gsn");
                String typeName = cursor.getString("name");

                value.put("startId", startId);
                value.put("endId", endId);
                value.put("startGSN", startGSN);
                value.put("endGSN", endGSN);
                value.putString("partition", partitionId);
                value.putString("operationId", operationId);
                value.putString("runId", runId);

                ExchangeObject exchangeObject = exchangeObjects.get(typeName);
                IMap<Object, Object> map = hazelcastInstance.getMap(ExportDataConstants.EXCHANGE_OBJECTS_MAP_NAME);
                map.put(new StringBuilder()
                            .append(ExportDataConstants.EXCHANGE_OBJECTS_PREFIX)
                            .append("_")
                            .append(runId)
                            .append("_")
                            .append(partitionId)
                            .toString(),
                        exchangeObject);

                number++;

                LOGGER.info("Finished block of type {} [{}].",
                        exchangeObject instanceof ExchangeEntity ? "EXCHANGE ENTITY" : "EXCHANGE RELATION",
                        exchangeObject instanceof ExchangeEntity ? ((ExchangeEntity) exchangeObject).getName() : ((ExchangeRelation) exchangeObject).getRelation());
            }

        } catch (SQLException sqle) {
            LOGGER.error("SQL exception caught.", sqle);
        }

        return result;
    }
    /**
     * @return the definition
     */
    public ExchangeDefinition getDefinition() {
        return definition;
    }
    /**
     * @param definition the definition to set
     */
    public void setDefinition(ExchangeDefinition definition) {
        this.definition = definition;
    }
    /**
     * @return the definitionKey
     */
    public String getDefinitionKey() {
        return definitionKey;
    }
    /**
     * @param definitionKey the definitionKey to set
     */
    public void setDefinitionKey(String definitionKey) {
        this.definitionKey = definitionKey;
    }
    /**
     * @return the blockSize
     */
    public Long getBlockSize() {
        return blockSize;
    }
    /**
     * @param blockSize the blockSize to set
     */
    public void setBlockSize(Long blockSize) {
        this.blockSize = blockSize;
    }
    /**
     * @return the operationId
     */
    public String getOperationId() {
        return operationId;
    }
    /**
     * @param operationId the operationId to set
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
