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

package com.unidata.mdm.backend.service.data.listener;

import com.unidata.mdm.backend.common.context.CommonRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.service.ServiceUtils;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.conf.WorkflowProcessType;

/**
 * @author Mikhail Mikhailov
 * Does various enrichments, related to security in the request context scope.
 */
public interface RequestContextSetup<T extends CommonRequestContext> {
    /**
     * Adds security info to context.
     * @param t the context
     * @param id the id
     * @param resourceName name of the resource
     */
    default void putResourceRights(T t, StorageId id, String resourceName) {
        t.putToStorage(id, SecurityUtils.getRightsForResourceWithDefault(resourceName));
    }
    /**
     * Adds workflow information to the given context.
     * @param t the context
     * @param id the id
     * @param resourceName name of the resource
     * @param processType type of the process
     */
    default void putWorkflowAssignments(T t, StorageId id, String resourceName, WorkflowProcessType processType) {
        if (ServiceUtils.getWorkflowService() != null && resourceName != null) {
            t.putToStorage(id, ServiceUtils.getWorkflowService().getAssignmentsByEntityNameAndType(resourceName, processType));
        }
    }
}
