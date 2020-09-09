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
package com.unidata.mdm.backend.service.data.listener.record;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.data.origin.OriginRecordsComponent;

/**
 * @author Mikhail Mikhailov
 * Etalon materializer.
 */
public class DataRecordDeleteEtalonMaterializer implements DataRecordBeforeExecutor<DeleteRequestContext> {
    /**
     * Origin component.
     */
    @Autowired
    private OriginRecordsComponent originComponent;
    /**
     * Constructor.
     */
    public DataRecordDeleteEtalonMaterializer() {
        super();
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.data.listener.DataRecordExecutor#execute(com.unidata.mdm.backend.common.context.CommonRequestContext)
     */
    @Override
    public boolean execute(DeleteRequestContext ctx) {

        // Skip on origin deactivate.
        if (!(ctx.isInactivateEtalon() || ctx.isInactivatePeriod() || ctx.isWipe())) {
            return true;
        }

        originComponent.loadAndSaveWorkflowTimeline(ctx,
                StorageId.DATA_TIMELINE_BEFORE,
                StorageId.DATA_INTERVALS_BEFORE,
                !ctx.isInactivatePeriod(), false, ctx.isInactivatePeriod());

        return true;
    }
}
