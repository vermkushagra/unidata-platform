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
package com.unidata.mdm.backend.dao;

import java.util.List;

import com.unidata.mdm.backend.service.wf.po.WorkflowAssignmentPO;
import com.unidata.mdm.conf.WorkflowProcessType;

/**
 * @author Mikhail Mikhailov
 * Work flow assignment DAO interface.
 */
public interface WorkflowAssignmentDao {

    /**
     * Loads all assignments.
     * @return list of assignments
     */
    List<WorkflowAssignmentPO> loadAll();

    /**
     * Loads assignments by entity name.
     * @param name the name
     * @return list of assignments
     */
    List<WorkflowAssignmentPO> loadByEntityName(String name);
    /**
     * Loads assignment by entity name and process type.
     * @param name the name
     * @param type the type
     * @return assignment or null
     */
    WorkflowAssignmentPO loadByEntityNameAndProcessType(String name, WorkflowProcessType type);

    /**
     * Upserts assignment updates.
     * @param update the update
     */
    void upsert(List<WorkflowAssignmentPO> update);
}
