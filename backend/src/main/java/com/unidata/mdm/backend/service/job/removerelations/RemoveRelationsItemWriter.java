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

package com.unidata.mdm.backend.service.job.removerelations;

import java.util.Collection;
import java.util.List;

import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

public class RemoveRelationsItemWriter implements ItemWriter<List<DeleteRelationRequestContext>> {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveRelationsItemWriter.class);

    /**
     * Data record service
     */
    @Autowired
    private RelationsServiceComponent relationsServiceComponent;

    @Override
    public void write(List<? extends List<DeleteRelationRequestContext>> items) throws Exception {
        items.stream().flatMap(Collection::stream).forEach(ctx -> {
            try {
                relationsServiceComponent.deleteRelation(ctx);
            } catch (Exception e) {
                LOGGER.error("Error during removing relation :{}", e);
            }
        });
    }
}
