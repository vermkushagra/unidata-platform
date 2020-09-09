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
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * @author Mikhail Mikhailov
 * Simple update data mapping writer.
 */
public class ReindexDataJobPrepareItemWriter implements ItemWriter<String> {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReindexDataJobPrepareItemWriter.class);
    /**
     * Prepare per index params.
     * ("index.refresh_interval", "-1"); // Disable refresh
     * ("index.warmer.enabled", Boolean.FALSE); // Disable warmers
     */
    private static final Map<String, Object> PREPARE_INDEX_PARAMS = Collections.singletonMap("index.refresh_interval", "-1");
    /**
     * Search service
     */
    @Autowired
    private SearchServiceExt searchService;
    /**
     * Meta model service
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Constructor.
     */
    public ReindexDataJobPrepareItemWriter() {
        super();
    }

    @Override
    public void write(List<? extends String> items) throws Exception {

        for (String entityName : items) {

            boolean isEntity = metaModelService.isEntity(entityName);
            LOGGER.info(isEntity
                    ? "Setting bulk-optimized options to index for {} (entity)."
                    : "Not setting bulk-optimized options to index for {} (lookup).", entityName);
            if (isEntity) {
                // searchService.closeIndex(entity, SecurityUtils.getCurrentUserStorageId());
                searchService.setIndexSettings(entityName, SecurityUtils.getCurrentUserStorageId(), PREPARE_INDEX_PARAMS);
                // searchService.openIndex(entity, SecurityUtils.getCurrentUserStorageId());
            }
        }
    }
}
