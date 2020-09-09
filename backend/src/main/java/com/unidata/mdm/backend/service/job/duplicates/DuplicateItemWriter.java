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

package com.unidata.mdm.backend.service.job.duplicates;

import java.util.List;

import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import org.springframework.beans.factory.annotation.Required;

public class DuplicateItemWriter implements ItemWriter<MergeRequestContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateItemWriter.class);

    private String entityName;

    @Autowired
    private DataRecordsService dataService;

    @Autowired
    private SearchServiceExt searchService;

    @Override
    public void write(List<? extends MergeRequestContext> items) throws Exception {
            try {
                dataService.batchMerge((List<MergeRequestContext>)items);
                searchService.refreshIndex(entityName, SecurityUtils.getCurrentUserStorageId(), false);
            } catch (Exception e) {
                //there we can add adition information on audit event!
                LOGGER.error("Something happened duping recognizing winner{}", e);
            }
    }

    @Required
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}
