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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Mikhail Mikhailov
 * Update data mapping item reader.
 */
@StepScope
public class ReindexDataJobMappingItemReader implements ItemReader<String> {
    /**
     * Entity names.
     */
    @Value("#{stepExecutionContext[" + ReindexDataJobConstants.PARAM_ENTITY_NAME + "]}")
    private String entityNamesAsString;
    /**
     * Work of this reader.
     */
    private List<String> work = new ArrayList<>();
    /**
     * Constructor.
     */
    public ReindexDataJobMappingItemReader() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String read() throws Exception {

        if (work.isEmpty() && StringUtils.isNotBlank(entityNamesAsString)) {
            work.addAll(Arrays.asList(StringUtils.split(entityNamesAsString, "|")));
        }

        if (!work.isEmpty()) {

            String retval = work.remove(work.size() - 1);
            if (work.isEmpty()) {
                entityNamesAsString = null;
            }

            return retval;
        }

        return null;
    }

}
