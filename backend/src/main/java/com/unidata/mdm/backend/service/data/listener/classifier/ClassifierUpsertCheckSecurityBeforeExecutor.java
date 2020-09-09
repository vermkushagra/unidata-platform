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

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertClassifierDataRequestContext;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemSecurityException;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * Executor responsible for modifying relations have an alias key.
 */
public class ClassifierUpsertCheckSecurityBeforeExecutor implements DataRecordBeforeExecutor<UpsertClassifierDataRequestContext> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierUpsertCheckSecurityBeforeExecutor.class);
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertClassifierDataRequestContext uCtx) {

        Right rights = uCtx.getFromStorage(StorageId.COMMON_ACCESS_RIGHTS);
        UpsertAction action = uCtx.getFromStorage(StorageId.CLASSIFIERS_UPSERT_EXACT_ACTION);

        if (!rights.isCreate() && action == UpsertAction.INSERT) {
            final String message = "Insert of classifier data of type {} is denied for user {} due to missing insert rights on the {} entity object";
            LOGGER.info(message, uCtx.getClassifierName(), SecurityUtils.getCurrentUserName(), uCtx.keys().getEntityName());
            throw new SystemSecurityException(message, ExceptionId.EX_DATA_CLASSIFIER_UPSERT_NO_INSERT_RIGHTS,
                    uCtx.getClassifierName(), SecurityUtils.getCurrentUserName(), uCtx.keys().getEntityName());
        }

        if (!rights.isUpdate() && action == UpsertAction.UPDATE) {
            ClassifierKeys ck = uCtx.classifierKeys();
            final String message = "Update of classifier data of type {} is denied for user {} due to missing update rights on the {} entity object";
            LOGGER.info(message, ck.getName(), SecurityUtils.getCurrentUserName(), ck.getRecord().getEntityName());
            throw new SystemSecurityException(message, ExceptionId.EX_DATA_CLASSIFIER_UPSERT_NO_UPDATE_RIGHTS,
                    ck.getName(), SecurityUtils.getCurrentUserName(), ck.getRecord().getEntityName());
        }

        return true;
    }
}
