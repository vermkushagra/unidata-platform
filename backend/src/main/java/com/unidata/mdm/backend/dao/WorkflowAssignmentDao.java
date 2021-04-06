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
