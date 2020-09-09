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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.unidata.mdm.backend.common.dto.ErrorInfoDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.integration.exits.ExitResult;
import com.unidata.mdm.backend.util.MessageUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.integration.exits.MergeListener;
import com.unidata.mdm.backend.conf.impl.MergeImpl;
import com.unidata.mdm.backend.service.configuration.ConfigurationServiceExt;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.common.types.EtalonRecord;


/**
 * @author Mikhail Mikhailov
 */
public class DataRecordMergeUserExitAfterExecutor implements DataRecordAfterExecutor<MergeRequestContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataRecordMergeUserExitAfterExecutor.class);

    /**
     * Configuration service.
     */
    @Autowired
    private ConfigurationServiceExt configurationService;

    /**
     * Constructor.
     */
    public DataRecordMergeUserExitAfterExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(MergeRequestContext ctx) {
        boolean result = true;
        EtalonRecord master = ctx.getFromStorage(StorageId.DATA_MERGE_ETALON_RECORD);
        List<EtalonRecord> duplicates = ctx.getFromStorage(StorageId.DATA_MERGE_DUPLICATES);
        MergeImpl upsert = configurationService.getMerge();
        if (upsert == null || master == null && duplicates == null) {
            return result;
        }

        Collection<MergeListener> listeners = configurationService.getListeners(
                master.getInfoSection().getEntityName(),
                upsert.getAfterMergeInstances());

        if (CollectionUtils.isEmpty(listeners)) {
            return result;
        }

        if (configurationService.useDeprecateUserExits()) {
            listeners.forEach(listener -> listener.afterMerge(master, duplicates, ctx));
        } else {
            ExitResult exitResult = null;
            for (MergeListener listener : listeners) {
                exitResult = listener.afterMergeWithResult(master, duplicates, ctx);
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
                            ExceptionId.EX_MERGE_AFTER_USER_EXIT_ERROR.getCode(),
                            exitResult.getWarningMessage()));
                    errors.add(errorInfo);
                    ctx.putToStorage(StorageId.PROCESS_ERRORS, errors);
                }

                if (ExitResult.Status.ERROR.equals(exitResult.getStatus())) {
                    throw new DataProcessingException("Error occurred during run after merge records user exit",
                            ExceptionId.EX_MERGE_AFTER_USER_EXIT_ERROR,
                            exitResult.getWarningMessage());
                }
            }
        }
        return result;
    }

}
