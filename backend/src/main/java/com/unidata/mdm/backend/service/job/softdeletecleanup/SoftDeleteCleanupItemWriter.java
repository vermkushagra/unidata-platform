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

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.dto.DeleteRecordDTO;
import com.unidata.mdm.backend.common.service.DataRecordsService;

import java.util.List;

import com.unidata.mdm.backend.service.job.exchange.in.ImportDataJobUtils;
import com.unidata.mdm.backend.service.job.modify.ModifyItemJobStepExecutionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class SoftDeleteCleanupItemWriter implements ItemWriter<List<DeleteRequestContext>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SoftDeleteCleanupItemWriter.class);

    @Autowired
    private DataRecordsService dataRecordsService;

    @Override
    public void write(List<? extends List<DeleteRequestContext>> items) throws Exception {
        SoftDeleteCleanupStepExecutionState state = ImportDataJobUtils.getStepState();
        long success = 0;
        long failed = 0;

        if (!CollectionUtils.isEmpty(items)) {
            for (List<DeleteRequestContext> item : items) {
                LOGGER.debug("Starting to write ids chunk of size: {}.", item.size());
                state.incrementProcessedRecords(item.size());
                for (DeleteRequestContext ctx : item) {
                    try {
                        DeleteRecordDTO result = dataRecordsService.deleteRecord(ctx);
                        if (result.wasSuccess()) {
                            success++;
                        } else {
                            failed++;
                        }
                    } catch (Exception e) {
                        LOGGER.error("Can't delete record", e);
                        failed++;
                    }
                }
            }

            state.incrementDeleteRecords(success);
            state.incrementFailedRecords(failed);

            LOGGER.info("Operation bulk cleanup done.");
        }
    }
}
