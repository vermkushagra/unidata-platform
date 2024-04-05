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
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.GetClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.service.classifier.ClsfService;
import com.unidata.mdm.backend.service.data.classifiers.ClassifiersCommonComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.data.listener.RequestContextSetup;
import com.unidata.mdm.conf.WorkflowProcessType;


/**
 * @author Dmitry Kopin
 *         Simple context validity checker.
 */
public class ClassifierGetValidateBeforeExecutor
        implements DataRecordBeforeExecutor<GetClassifierDataRequestContext>, RequestContextSetup<GetClassifierDataRequestContext> {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierGetValidateBeforeExecutor.class);
    /**
     * Classifier meta data service.
     */
    @Autowired
    private ClsfService classifierMetaDataService;
    /**
     * Classifiers common component.
     */
    @Autowired
    private ClassifiersCommonComponent classifiersCommonComponent;

    /**
     * Constructor.
     */
    public ClassifierGetValidateBeforeExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(GetClassifierDataRequestContext ctx) {

        ClassifierKeys keys = classifiersCommonComponent.identify(ctx);
        if (keys == null) {
            final String message = "Classifier data record not found.";
            LOGGER.warn(message);
            throw new DataProcessingException(message, ExceptionId.EX_DATA_GET_CLASSIFIER_RECORD_NOT_FOUND);
        }

        if (!classifierMetaDataService.isNodeExist(keys.getNodeId(), keys.getName())) {
            // UN-6227
            // Records will be garbage collected by job later.
            /*
            final String message = "Classifier data record refers an invalid node {} in {}.";
            LOGGER.warn(message, keys.getNodeId(), keys.getName());
            throw new DataProcessingException(message, ExceptionId.EX_DATA_GET_CLASSIFIER_RECORD_INVALID_NODE,
                    keys.getNodeId(), keys.getName());
            */
            return false;
        }

        ensureContextCompleteness(ctx);
        return true;
    }

    private void ensureContextCompleteness(GetClassifierDataRequestContext ctx) {

        ClassifierKeys keys = ctx.classifierKeys();
        if (ctx.getFromStorage(StorageId.COMMON_ACCESS_RIGHTS) == null) {
            putResourceRights(ctx, StorageId.COMMON_ACCESS_RIGHTS, keys.getRecord().getEntityName());
        }

        if (ctx.getFromStorage(StorageId.COMMON_WF_ASSIGNMENTS) == null) {
            putWorkflowAssignments(ctx, StorageId.COMMON_WF_ASSIGNMENTS, keys.getRecord().getEntityName(), WorkflowProcessType.RECORD_EDIT);
        }
    }
}