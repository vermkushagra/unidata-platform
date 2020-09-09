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

package com.unidata.mdm.backend.service.job.reindex;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Mikhail Mikhailov
 * Update data mapping partitioner.
 */
@JobScope
public class ReindexDataJobPreparePartitioner extends ReindexDataJobAbstractMappingPartitioner {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReindexDataJobPreparePartitioner.class);
    /**
     * If true, record's data will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_RECORDS + "] ?: false}")
    private Boolean reindexRecords;
    /**
     * If true, rels will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_RELATIONS + "] ?: false}")
    private Boolean reindexRelations;
    /**
     * If true, maching data will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_MATCHING + "] ?: false}")
    private Boolean reindexMatching;
    /**
     * If true, classifiers data will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_CLASSIFIERS + "] ?: false}")
    private Boolean reindexClassifiers;
    /**
     * Constructor.
     */
    public ReindexDataJobPreparePartitioner() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        if (!reindexRecords && !reindexRelations && !reindexMatching && !reindexClassifiers) {
            LOGGER.info("No data kind specified for reindexing [reindexRecords, reindexRelations, reindexMatching, reindexClassifiers are all false]. No prepare executed.");
            return Collections.emptyMap();
        }

        return super.partition(gridSize);
    }
}
