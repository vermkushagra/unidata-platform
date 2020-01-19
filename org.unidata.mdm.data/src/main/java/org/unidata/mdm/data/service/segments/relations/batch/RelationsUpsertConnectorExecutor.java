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

package org.unidata.mdm.data.service.segments.relations.batch;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.data.dto.RelationsBulkResultDTO;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.apply.batch.impl.RelationUpsertBatchSetAccumulator;
import org.unidata.mdm.system.service.ExecutionService;
import org.unidata.mdm.system.type.pipeline.Pipeline;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.pipeline.batch.BatchedConnector;
import org.unidata.mdm.system.type.pipeline.batch.BatchedPipelineInput;
import org.unidata.mdm.system.type.pipeline.fragment.InputFragmentContainer;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov on Jan 16, 2020
 */
@Component(RelationsUpsertConnectorExecutor.SEGMENT_ID)
public class RelationsUpsertConnectorExecutor extends BatchedConnector<BatchedPipelineInput, RelationsBulkResultDTO> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[BATCH_RELATIONS_UPSERT_CONNECTOR]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".batch.relations.upsert.connector.description";
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationsUpsertConnectorExecutor.class);
    /**
     * The ES instance.
     */
    @Autowired
    private ExecutionService executionService;
    /**
     * Constructor.
     */
    public RelationsUpsertConnectorExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public RelationsBulkResultDTO connect(BatchedPipelineInput ctx) {

        InputFragmentContainer target = (InputFragmentContainer) ctx;
        RelationUpsertBatchSetAccumulator payload = target.fragment(RelationUpsertBatchSetAccumulator.ID);
        if (Objects.isNull(payload)) {
            return null;
        }

        return execute(payload, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RelationsBulkResultDTO connect(BatchedPipelineInput ctx, Pipeline p) {

        InputFragmentContainer target = (InputFragmentContainer) ctx;
        RelationUpsertBatchSetAccumulator payload = target.fragment(RelationUpsertBatchSetAccumulator.ID);
        if (Objects.isNull(payload)) {
            return null;
        }

        return execute(payload, p);
    }

    public RelationsBulkResultDTO execute(@Nonnull RelationUpsertBatchSetAccumulator bsa, @Nullable Pipeline p) {

        MeasurementPoint.start();
        try {
            return null;
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return super.supports(start) && start.getInputTypeClass().isAssignableFrom(BatchedPipelineInput.class);
    }
}
