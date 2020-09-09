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

package com.unidata.mdm.backend.service.data.listener.classifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.context.GetClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemSecurityException;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * Executor responsible for modifying relations have an alias key.
 */
public class ClassifierGetCheckSecurityBeforeExecutor implements DataRecordBeforeExecutor<GetClassifierDataRequestContext> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierGetCheckSecurityBeforeExecutor.class);
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(GetClassifierDataRequestContext gCtx) {

        ClassifierKeys classifierKeys = gCtx.classifierKeys();
        Right rights = gCtx.getFromStorage(StorageId.COMMON_ACCESS_RIGHTS);

        if (!rights.isRead()) {
            final String message = "Read of classifier objects of type {} is denied for user {} due to missing read rights on the {} object.";
            LOGGER.info(message, classifierKeys.getName(), SecurityUtils.getCurrentUserName(), classifierKeys.getRecord().getEntityName());
            throw new SystemSecurityException(message, ExceptionId.EX_DATA_CLASSIFIER_GET_NO_RIGHTS,
                    classifierKeys.getName(), SecurityUtils.getCurrentUserName(), classifierKeys.getRecord().getEntityName());
        }

        return true;
    }
}
