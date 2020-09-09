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

/**
 *
 */

package com.unidata.mdm.backend.service.job.softdeletecleanup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

import com.unidata.mdm.backend.service.job.JobUtil;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class SoftDeleteCleanupPartitioner implements Partitioner {
    private static final Logger LOGGER = LoggerFactory.getLogger(SoftDeleteCleanupPartitioner.class);

    @Qualifier("unidataDataSource")
    @Autowired
    private DataSource unidataDataSource;

    @Autowired
    private MetaModelServiceExt metaModelService;

    @Autowired
    private JobUtil jobUtil;

    private Long blockSize;
    private String type;
    private Long overdueDays;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        final Map<String, ExecutionContext> result = new HashMap<>();

        List<String> types = new ArrayList<>();

        if ("ALL".equals(type)) {
            List<EntityDef> entities = metaModelService.getEntitiesList();
            if (entities != null && !entities.isEmpty()) {
                types.addAll(entities.stream()
                        .map(EntityDef::getName)
                        .collect(Collectors.toList()));
            }

            List<LookupEntityDef> lookups = metaModelService.getLookupEntitiesList();
            if (lookups != null && !lookups.isEmpty()) {
                types.addAll(lookups.stream()
                        .map(LookupEntityDef::getName)
                        .collect(Collectors.toList()));
            }

        } else {
            types.add(type);
        }

        LOGGER.info("Settings: [blockSize={}, types={}]", blockSize, types);

        int number = 0;
        for (String type : types) {
            LOGGER.info("Processing {}.", type);

            long count = getRecordsCount(type);
            LOGGER.info("Found {} etalon records in the DB. {}.", count, count == 0 ? "Skipping" : "Starting");
            if (count <= 0) {
                LOGGER.info("Finished processing {}.", type);
                continue;
            }

            long process = count;
            long offset = 0;

            while (process > 0) {
                final List<String> ids = jobUtil.getEtalonIds(type, makeCondition(overdueDays), offset, blockSize);
                if (ids == null || ids.isEmpty()) {
                    LOGGER.warn("No more ids for type {}. Breaking up.", type);
                    break;
                }

                final ExecutionContext value = new ExecutionContext();
                result.put(JobUtil.partitionName(number), value);

                value.put("ids", ids);
                value.putString("entityName", type);
                value.putString("partition", "partition" + number);
                number++;

                if (count == offset) {
                    LOGGER.info("No more records for {}. Breaking up.", type);
                    break;
                }

                process -= blockSize;
                offset += blockSize;
            }

            LOGGER.info("Finished processing {}.", type);
        }

        LOGGER.info("Partitions keys [size={}, keys={}]", result.size(), result.keySet());

        return result;
    }

    /**
     *
     * @param days
     * @return
     */
    private String makeCondition(long days) {
        return
            " AND status='INACTIVE'" +
            " AND date_part('days', current_date - date_trunc('day', update_date)) >= " + days;
    }

    /**
     *
     * @param entityName
     * @return
     */
    private long getRecordsCount(String entityName) {
        StringBuilder sqlb = new StringBuilder()
                .append("select count(*) as CNT from etalons where name = '")
                .append(entityName)
                .append("'")
            .append(makeCondition(overdueDays));

        LOGGER.debug("Executing SQL for entities set size query [{}].", sqlb.toString());

        try (final Connection connection = unidataDataSource.getConnection();
             final Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
             final ResultSet rs = statement.executeQuery(sqlb.toString())) {

            if (rs.next()) {
                return rs.getLong("CNT");
            }
        } catch (SQLException sqle) {
            LOGGER.error("SQL exception caught.", sqle);
        }

        return 0L;
    }

    public void setBlockSize(Long blockSize) {
        this.blockSize = blockSize;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOverdueDays(Long overdueDays) {
        this.overdueDays = overdueDays;
    }
}
