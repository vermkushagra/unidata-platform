package com.unidata.mdm.backend.service.job.exchange.in;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import com.hazelcast.core.IMap;
import com.unidata.mdm.backend.exchange.def.ExchangeDefinition;
import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.exchange.def.ExchangeRelation;
import com.unidata.mdm.backend.service.job.ComplexJobParameterHolder;
import com.unidata.mdm.backend.util.IdUtils;

/**
 * Class responsible for making partitions.
 *
 * @param <T> - table definition class!
 */
@JobScope
public class ImportDataJobRelationPartitioner extends ImportDataJobAbstractPartitioner implements Partitioner {

    /**
     * Exchange definition, which may be set via param.
     */
    private ExchangeDefinition definition;
    /**
     * Definition key for complex parameter holder.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_DEFINITION + "]}")
    private String definitionKey;
    /**
     * Complex parameters holder.
     */
    @Autowired
    private ComplexJobParameterHolder jobParameterHolder;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        ExchangeDefinition def = this.definition == null ? jobParameterHolder.getComplexParameter(definitionKey) : this.definition;
        if (def == null) {
            return Collections.emptyMap();
        }

        IMap<Object, Object> objectsMap = hazelcastInstance.getMap(ImportDataJobConstants.EXCHANGE_OBJECTS_MAP_NAME);

        Map<String, ExecutionContext> result = new LinkedHashMap<>();
        for (ExchangeEntity entity : def.getEntities()) {

            List<ExchangeRelation> relations = new ArrayList<>();

            // May be null
            if (!CollectionUtils.isEmpty(entity.getContains()) && entity.isProcessRelations()) {
                relations.addAll(entity.getContains());
            }

            // May be null
            if (!CollectionUtils.isEmpty(entity.getRelates()) && entity.isProcessRelations()) {
                relations.addAll(entity.getRelates());
            }

            for (ExchangeRelation relation : relations) {

                String exchangeObjectId = IdUtils.v4String();
                Map<String, ExecutionContext> partitions = collectRelationExecutionContexts(result.size(), relation, exchangeObjectId);
                if (!CollectionUtils.isEmpty(partitions)) {

                    for (Entry<String, ExecutionContext> entry : partitions.entrySet()) {
                        entry.getValue().putString(ImportDataJobConstants.PARAM_FROM_SOURCE_SYSTEM, entity.getSourceSystem());
                        entry.getValue().putString(ImportDataJobConstants.PARAM_FROM_ENTITY_NAME, entity.getName());
                    }

                    objectsMap.put(ImportDataJobUtils.getObjectReferenceName(runId, exchangeObjectId), relation);
                    result.putAll(partitions);
                }
            }
        }

        LOGGER.info("Total number of all partitions is {}", result.size());
        return result;
    }

    /**
     * Sets the definition key.
     * @param definitionKey
     */
    public void setDefinitionKey(String definitionKey) {
        this.definitionKey = definitionKey;
    }
}
