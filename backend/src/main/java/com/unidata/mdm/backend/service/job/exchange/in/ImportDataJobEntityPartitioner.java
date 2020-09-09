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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import com.hazelcast.core.IMap;
import com.unidata.mdm.backend.exchange.def.ExchangeDefinition;
import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeEntity;
import com.unidata.mdm.backend.service.job.ComplexJobParameterHolder;
import com.unidata.mdm.backend.util.IdUtils;

/**
 * Class responsible for making record partitions.
 *
 * @param <T> - table definition class!
 */
@JobScope
public class ImportDataJobEntityPartitioner extends ImportDataJobAbstractPartitioner implements Partitioner {
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
        // Objects map.
        IMap<Object, Object> objectsMap = hazelcastInstance.getMap(ImportDataJobConstants.EXCHANGE_OBJECTS_MAP_NAME);

        Map<String, ExecutionContext> result = new LinkedHashMap<>();
        for (ExchangeEntity entity : def.getEntities()) {
            if (entity.isProcessRecords() || entity.isProcessClassifiers()) {

                String exchangeObjectId = IdUtils.v4String();
                Map<String, ExecutionContext> partitions = collectEntityExecutionContexts(result.size(), (DbExchangeEntity) entity, exchangeObjectId);
                if (!CollectionUtils.isEmpty(partitions)) {
                    result.putAll(partitions);
                    objectsMap.put(ImportDataJobUtils.getObjectReferenceName(runId, exchangeObjectId), entity);
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
