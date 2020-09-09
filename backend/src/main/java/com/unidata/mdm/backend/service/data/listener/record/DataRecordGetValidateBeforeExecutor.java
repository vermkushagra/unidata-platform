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

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;


/**
 * @author Mikhail Mikhailov
 * Simple context validity checker and key finder.
 */
public class DataRecordGetValidateBeforeExecutor
    implements DataRecordBeforeExecutor<GetRequestContext> {

    /**
     * Common component.
     */
    @Autowired
    private CommonRecordsComponent commonComponent;

    /**
     * Logger for this bean.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(DataRecordGetValidateBeforeExecutor.class);

    /**
     * Constructor.
     */
    public DataRecordGetValidateBeforeExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(GetRequestContext ctx) {

        // Keys already supplied.
        if (Objects.nonNull(ctx.keys())) {
            return true;
        }

        // 1. Check input
        if (!ctx.isValidRecordKey()) {
            final String message = "Ivalid input. Request context is not capable for record identification.";
            LOGGER.warn(message, ctx);
            throw new DataProcessingException(message, ExceptionId.EX_DATA_GET_INVALID_INPUT, ctx);
        }

        // 2. Identify
        RecordKeys keys = commonComponent.identify(ctx);
        if (keys == null) {
            final String message = "Record not found by supplied keys etalon id: [{}], origin id [{}], external id [{}], source system [{}], name [{}]";
            LOGGER.warn(message, ctx.getEtalonKey(), ctx.getOriginKey(), ctx.getExternalId(), ctx.getSourceSystem(), ctx.getEntityName());
            throw new DataProcessingException(message, ExceptionId.EX_DATA_GET_NOT_FOUND_BY_SUPPLIED_KEYS,
                    ctx.getEtalonKey(), ctx.getOriginKey(), ctx.getExternalId(), ctx.getSourceSystem(), ctx.getEntityName());
        }

        return true;
    }

}
