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

package com.unidata.mdm.backend.service.data.listener.record;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.ContextUtils;
import com.unidata.mdm.backend.common.context.DataQualityContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.data.CalculableHolder;
import com.unidata.mdm.backend.common.dq.DataQualityExecutionMode;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.service.DataQualityService;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import com.unidata.mdm.meta.DQRuleDef;

/**
 * The Class DQBeforeUpsertExecutor.
 */
public class DataRecordUpsertOriginDQBeforeExecutor implements DataRecordBeforeExecutor<UpsertRequestContext> {

    /** The dq service. */
    @Autowired
    private DataQualityService dqService;
    @Autowired
    private MetaModelServiceExt metaModelService;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor
     * #execute(com.unidata.mdm.backend.service.data.DataRequestContext)
     */
    @Override
    public boolean execute(UpsertRequestContext ctx) {

        if (ctx.isSkipCleanse()) {
            return true;
        }

        MeasurementPoint.start();
        try {

            DataQualityContext dqCtx = DataQualityContext.builder(ctx)
                    .modificationBox(ctx.getFromStorage(StorageId.DATA_UPSERT_MODIFICATION_BOX))
                    .rules(selectRules(ctx))
                    .executionMode(DataQualityExecutionMode.MODE_ORIGIN)
                    .build();

            ContextUtils.storageCopy(ctx, dqCtx, ctx.keysId());

            // Temporary stuff
            // Remember number of modifications
            dqCtx.putToStorage(StorageId.DATA_DQ_ORIGINS_COUNT, dqCtx.getModificationBox().count(dqCtx.toBoxKey()));

            dqService.apply(dqCtx);

            ctx.getDqErrors().addAll(dqCtx.getErrors());

            // Temporary stuff
            // Reset to one mod. Origins always modify the same object.
            int current = dqCtx.getModificationBox().count(dqCtx.toBoxKey());
            int saved = dqCtx.getFromStorage(StorageId.DATA_DQ_ORIGINS_COUNT);

            if (current > saved) {

                // Remove intermediate versions and reinsert last modified version
                List<CalculableHolder<DataRecord>> collected = dqCtx.getModificationBox().resetBy(dqCtx.toBoxKey(), current - saved);
                dqCtx.getModificationBox().originState(null);
                dqCtx.getModificationBox().push(collected.get(collected.size() - 1));
            }

            // We report only from local DQ to not to interfere with other cases i. e. with restore
            return dqCtx.getErrors().isEmpty();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Selects rules to execute.
     * @param ctx the upsert context
     * @return collection of rules
     */
    private List<DQRuleDef> selectRules(UpsertRequestContext ctx) {

        RecordKeys keys = ctx.keys();
        String entityName = keys == null ? ctx.getEntityName() : keys.getEntityName();

        EntityWrapper ew = metaModelService.getValueById(entityName, EntityWrapper.class);
        if (Objects.isNull(ew)) {

            LookupEntityWrapper lew = metaModelService.getValueById(entityName, LookupEntityWrapper.class);
            if (Objects.isNull(lew)) {
                return Collections.emptyList();
            }

            return lew.getOriginRules().get(selectSourceSystem(ctx));
        }

        return ew.getOriginRules().get(selectSourceSystem(ctx));
    }
    /**
     * Selects the source system source.
     * @param ctx upsert context
     * @return source system
     */
    private String selectSourceSystem(UpsertRequestContext ctx) {

        RecordKeys keys = ctx.keys();
        if (keys == null || keys.getOriginKey() == null) {
            return keys != null && keys.getEtalonKey() != null
                ? metaModelService.getAdminSourceSystem().getName()
                : ctx.getSourceSystem();
        }

        return keys.getOriginKey().getSourceSystem();
    }
}
