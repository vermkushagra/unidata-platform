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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.exception.PlatformSecurityException;
import org.unidata.mdm.core.type.security.EndpointType;
import org.unidata.mdm.core.type.security.Right;
import org.unidata.mdm.core.type.security.SecurityToken;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.RecordIdentityContextSupport;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.data.UpsertAction;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

@Component(RecordUpsertAccessExecutor.SEGMENT_ID)
public class RecordUpsertAccessExecutor extends Point<UpsertRequestContext>
    implements RecordIdentityContextSupport {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_UPSERT_ACCESS]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.upsert.access.description";
    /**
     * Constructor.
     * @param id
     * @param description
     */
    public RecordUpsertAccessExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordUpsertAccessExecutor.class);

    @Override
    public void point(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {

            RecordKeys keys = ctx.keys();
            String entityName = selectEntityName(ctx);

            ctx.accessRight(SecurityUtils.getRightsForResourceWithDefault(entityName));

            Right rights = ctx.accessRight();
            UpsertAction upsertAction = ctx.upsertAction();
            if (!rights.isCreate() && upsertAction == UpsertAction.INSERT) {
                final String message = "The user '{}' has no or unsufficient insert rights for resource '{}'. Insert denied.";
                LOGGER.info(message, SecurityUtils.getCurrentUserName(), entityName);
                throw new PlatformSecurityException(message,
                        DataExceptionIds.EX_DATA_UPSERT_INSERT_NO_RIGHTS, SecurityUtils.getCurrentUserName(), entityName);
            }

            if (!rights.isUpdate() && upsertAction == UpsertAction.UPDATE) {
                final String message = "The user '{}' has no or unsufficient update rights for resource '{}'. Update denied.";
                LOGGER.info(message, SecurityUtils.getCurrentUserName(), entityName);
                throw new PlatformSecurityException(message,
                        DataExceptionIds.EX_DATA_UPSERT_UPDATE_NO_RIGHTS, SecurityUtils.getCurrentUserName(), entityName);
            }

            // New record
            if (keys == null) {
                return;
            }

            SecurityToken securityToken = SecurityUtils.getSecurityTokenForCurrentUser();
            if (keys.isPending() && securityToken != null && securityToken.getEndpoint() != EndpointType.REST) {
                throw new PlatformSecurityException("Only REST users able to modify records in pending approval state",
                        DataExceptionIds.EX_DATA_UPSERT_NOT_ACCEPTED_HAS_PENDING_RECORD);
            }

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
}
