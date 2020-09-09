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
public class ReindexDataJobMappingPartitioner extends ReindexDataJobAbstractMappingPartitioner {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReindexDataJobMappingPartitioner.class);
    /**
     * Comma separated reindex types.
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_UPDATE_MAPPINGS + "] ?: false}")
    private Boolean updateMappings;
    /**
     * Comma separated reindex types.
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_CLEAN_INDEXES + "] ?: false}")
    private Boolean cleanIndexes;
    /**
     * Constructor.
     */
    public ReindexDataJobMappingPartitioner() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        if (!updateMappings && !cleanIndexes) {
            LOGGER.info("No mapping update nor index clean required. Mapping step is skipped.");
            return Collections.emptyMap();
        }

        return super.partition(gridSize);
    }
}
