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

package org.unidata.mdm.data.service.segments.records;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.calculables.ModificationBox;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.DataShift;
import org.unidata.mdm.core.type.data.OperationType;
import org.unidata.mdm.core.type.data.impl.SerializableDataRecord;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.calculables.impl.DataRecordHolder;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.data.OriginRecordInfoSection;
import org.unidata.mdm.data.type.data.impl.OriginRecordImpl;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.timeline.RecordTimeInterval;
import org.unidata.mdm.system.service.PlatformConfiguration;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov
 * Creates MBox and initial objects, if needed.
 */
@Component(RecordUpsertModboxExecutor.SEGMENT_ID)
public class RecordUpsertModboxExecutor extends Point<UpsertRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_UPSERT_MODBOX]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.upsert.modbox.init.description";
    /**
     * PC.
     */
    @Autowired
    private PlatformConfiguration platformConfiguration;
    /**
     * Constructor.
     */
    public RecordUpsertModboxExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(UpsertRequestContext ctx) {
        MeasurementPoint.start();
        try {
            ensureModificationBox(ctx);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpsertRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
    /**
     * Set up initial origin record and return it inside new box.
     * @param ctx the context
     */
    private void ensureModificationBox(UpsertRequestContext ctx) {

        RecordKeys keys = ctx.keys();
        Date ts = ctx.timestamp();
        OperationType operationType = ctx.operationType() == null
                ? OperationType.DIRECT
                : ctx.operationType();

        String user = SecurityUtils.getCurrentUserName();

        DataRecord data = ctx.getRecord();
        Collection<CalculableHolder<OriginRecord>> input = Collections.emptyList();
        if (Objects.nonNull(data)) {

            OriginRecordInfoSection is = new OriginRecordInfoSection()
                    .withCreateDate(ts)
                    .withUpdateDate(ts)
                    .withCreatedBy(user)
                    .withUpdatedBy(user)
                    .withShift(DataShift.PRISTINE)
                    .withStatus(keys.getOriginKey().getStatus())
                    .withApproval(ApprovalState.APPROVED)
                    .withValidFrom(ctx.getValidFrom())
                    .withValidTo(ctx.getValidTo())
                    .withMajor(platformConfiguration.getPlatformMajor())
                    .withMinor(platformConfiguration.getPlatformMinor())
                    .withOperationType(operationType)
                    .withRevision(0)
                    .withOriginKey(keys.getOriginKey());

            OriginRecord origin = new OriginRecordImpl()
                    .withDataRecord(data)
                    .withInfoSection(is);

            input = Collections.singleton(new DataRecordHolder(origin));
        }

        ModificationBox<OriginRecord> box = new RecordTimeInterval(ctx.getValidFrom(), ctx.getValidTo(), input);
        box.setCalculationState(SerializableDataRecord.of(data));

        ctx.modificationBox(box);
    }
}
