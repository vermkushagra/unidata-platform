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

import com.unidata.mdm.backend.common.dto.ErrorInfoDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.integration.exits.ExitResult;
import com.unidata.mdm.backend.util.MessageUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.integration.exits.ExitConstants;
import com.unidata.mdm.backend.common.integration.exits.UpsertListener;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.conf.impl.UpsertImpl;
import com.unidata.mdm.backend.service.configuration.ConfigurationServiceExt;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author Mikhail Mikhailov
 *         User exit 'after' executor.
 */
public class DataRecordUpsertOriginUserExitAfterExecutor implements DataRecordAfterExecutor<UpsertRequestContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataRecordUpsertOriginUserExitAfterExecutor.class);
    /**
     * Configuration service.
     */
    @Autowired
    private ConfigurationServiceExt configurationService;

    /**
     * Constructor.
     */
    public DataRecordUpsertOriginUserExitAfterExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRequestContext ctx) {

        UpsertAction action = ctx.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION);
        if (ctx.isBypassExtensionPoints() || action == UpsertAction.NO_ACTION) {
            return true;
        }

        MeasurementPoint.start();
        try {

            OriginRecord origin = ctx.getFromStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD);
            UpsertImpl upsert = configurationService.getUpsert();
            RecordKeys keys = ctx.getFromStorage(StorageId.DATA_UPSERT_KEYS);
            if (upsert != null && origin != null && keys != null) {
                String entityName = keys.getEntityName();
                Collection<UpsertListener> listeners = configurationService.getListeners(
                        entityName,
                        upsert.getAfterOriginUpsertInstances());
                if (CollectionUtils.isNotEmpty(listeners)) {
                    if (configurationService.useDeprecateUserExits()) {
                        if (action == UpsertAction.UPDATE) {
                            listeners.forEach(listener -> listener.afterOriginUpdate(origin, ctx));
                        } else if (action == UpsertAction.INSERT) {
                            listeners.forEach(listener -> listener.afterOriginInsert(origin, ctx));
                        }
                        ctx.putToStorage(StorageId.DATA_UPSERT_IS_MODIFIED,
                                ctx.getFromUserContext(ExitConstants.OUT_UPSERT_CURRENT_RECORD_IS_MODIFIED.name()));
                    } else {
                        ExitResult exitResult = null;
                        for (UpsertListener listener : listeners) {
                            if (action == UpsertAction.UPDATE) {
                                exitResult = listener.afterOriginUpdateWithResult(origin, ctx);
                            } else {
                                exitResult = listener.afterOriginInsertWithResult(origin, ctx);
                            }
                            if (exitResult == null) {
                                continue;
                            }

                            if (ExitResult.Status.WARNING.equals(exitResult.getStatus())) {
                                LOGGER.warn("User exit for listener {} and entity {} has warnings : {}",
                                        listener.getClass().getSimpleName(), ctx.getEntityName(), exitResult.getWarningMessage());
                                List<ErrorInfoDTO> errors = ctx.getFromStorage(StorageId.PROCESS_ERRORS);
                                if (errors == null) {
                                    errors = new ArrayList<>();
                                }
                                ErrorInfoDTO errorInfo = new ErrorInfoDTO();
                                errorInfo.setSeverity(ErrorInfoDTO.Severity.LOW);
                                errorInfo.setUserMessage(MessageUtils.getMessage(
                                        ExceptionId.EX_DATA_UPSERT_ORIGIN_RECORD_AFTER_USER_EXIT_ERROR.getCode(),
                                        exitResult.getWarningMessage()));
                                errors.add(errorInfo);
                                ctx.putToStorage(StorageId.PROCESS_ERRORS, errors);
                            }

                            if (ExitResult.Status.ERROR.equals(exitResult.getStatus())) {
                                throw new DataProcessingException("Error occurred during run after upsert record user exit",
                                        ExceptionId.EX_DATA_UPSERT_ORIGIN_RECORD_AFTER_USER_EXIT_ERROR,
                                        exitResult.getWarningMessage());
                            }

                            ctx.putToStorage(StorageId.DATA_UPSERT_IS_MODIFIED, exitResult.isWasModified());
                        }
                    }
                }
            }

            return true;

        } finally {
            MeasurementPoint.stop();
        }
    }

}
