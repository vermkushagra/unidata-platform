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
import org.unidata.mdm.core.type.security.Right;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.GetRequestContext;
import org.unidata.mdm.data.context.RecordIdentityContextSupport;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov
 * The former GET SEC executor.
 */
@Component(RecordGetAccessExecutor.SEGMENT_ID)
public class RecordGetAccessExecutor extends Point<GetRequestContext> implements RecordIdentityContextSupport {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_GET_ACCESS]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.get.security.description";

    /**
     * Logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(RecordGetAccessExecutor.class);

    /**
     * Constructor.
     */
    public RecordGetAccessExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void point(GetRequestContext ctx) {

        RecordKeys keys = ctx.keys();
        ctx.accessRight(SecurityUtils.getRightsForResourceWithDefault(selectEntityName(ctx)));

        Right rights = ctx.accessRight();
        if (!rights.isRead()) {
            final String message = "The user '{}' has no or unsufficient read rights for resource '{}'. Read denied.";
            LOGGER.info(message, SecurityUtils.getCurrentUserName(), keys.getEntityName());
            throw new PlatformSecurityException(message,
                    DataExceptionIds.EX_DATA_GET_NO_RIGHTS,
                    SecurityUtils.getCurrentUserName(), keys.getEntityName());
        }
    }

    @Override
    public boolean supports(Start<?> start) {
        return GetRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }

}
