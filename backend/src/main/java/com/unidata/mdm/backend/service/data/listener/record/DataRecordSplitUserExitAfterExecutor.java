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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.unidata.mdm.backend.common.context.SplitContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.dto.ErrorInfoDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.integration.exits.AfterSplitListener;
import com.unidata.mdm.backend.common.integration.exits.ExitResult;
import com.unidata.mdm.backend.conf.impl.SplitImpl;
import com.unidata.mdm.backend.service.configuration.ConfigurationServiceExt;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.util.MessageUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DataRecordSplitUserExitAfterExecutor implements DataRecordAfterExecutor<SplitContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataRecordSplitUserExitAfterExecutor.class);

    private final ConfigurationServiceExt configurationService;

    @Autowired
    public DataRecordSplitUserExitAfterExecutor(final ConfigurationServiceExt configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public boolean execute(final SplitContext splitContext) {
        SplitImpl split = configurationService.getSplit();
        if (split != null) {
            Collection<AfterSplitListener> listeners = configurationService.getListeners(
                    splitContext.getEntityName(),
                    split.getAfterSplitInstances());
            if(CollectionUtils.isEmpty(listeners)){
                return true;
            }
            for(AfterSplitListener listener : listeners){
                ExitResult exitResult = listener.afterSplit(splitContext);

                if (exitResult == null) {
                    return true;
                }

                if (ExitResult.Status.WARNING.equals(exitResult.getStatus())) {
                    LOGGER.warn("User exit for split listener {} and entity {} has warnings : {}",
                            listener.getClass().getSimpleName(), splitContext.getEntityName(), exitResult.getWarningMessage());
                    List<ErrorInfoDTO> errors = splitContext.getFromStorage(StorageId.PROCESS_ERRORS);
                    if(errors == null){
                        errors = new ArrayList<>();
                    }
                    ErrorInfoDTO errorInfo = new ErrorInfoDTO();
                    errorInfo.setSeverity(ErrorInfoDTO.Severity.LOW);
                    errorInfo.setUserMessage(MessageUtils.getMessage(ExceptionId.EX_SPLIT_USER_EXIT_AFTER_ERROR.getCode(),
                            exitResult.getWarningMessage()));
                    errors.add(errorInfo);
                    splitContext.putToStorage(StorageId.PROCESS_ERRORS, errors);
                }

                if (ExitResult.Status.ERROR.equals(exitResult.getStatus())) {
                    throw new DataProcessingException("Error occurred during run after split data user exit",
                            ExceptionId.EX_SPLIT_USER_EXIT_AFTER_ERROR,
                            exitResult.getWarningMessage());
                }
            }
        }

        return true;
    }
}
