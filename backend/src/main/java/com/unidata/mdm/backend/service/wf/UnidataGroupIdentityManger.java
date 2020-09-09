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

package com.unidata.mdm.backend.service.wf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.service.RoleService;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.AbstractManager;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupIdentityManager;
import org.activiti.engine.task.IdentityLinkType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.common.integration.auth.Role;

/**
 * @author Mikhail Mikhailov
 * Unidata group manager for activiti sessions.
 */
public class UnidataGroupIdentityManger extends AbstractManager implements GroupIdentityManager {

    /**
     * Role service instance.
     */
    @Autowired
    private RoleService roleService;
    /**
     * Constructor.
     */
    public UnidataGroupIdentityManger() {
        super();
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.GroupIdentityManager#createNewGroup(java.lang.String)
     */
    @Override
    public Group createNewGroup(String groupId) {
        throw new ActivitiException("Unidata group manager doesn't support creating a new group");
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.GroupIdentityManager#insertGroup(org.activiti.engine.identity.Group)
     */
    @Override
    public void insertGroup(Group group) {
        throw new ActivitiException("Unidata group manager doesn't support inserting a group");
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.GroupIdentityManager#updateGroup(org.activiti.engine.identity.Group)
     */
    @Override
    public void updateGroup(Group updatedGroup) {
        throw new ActivitiException("Unidata group manager doesn't support updating a group");
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.GroupIdentityManager#deleteGroup(java.lang.String)
     */
    @Override
    public void deleteGroup(String groupId) {
        throw new ActivitiException("Unidata group manager doesn't support deleting a group");
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.GroupIdentityManager#createNewGroupQuery()
     */
    @Override
    public GroupQuery createNewGroupQuery() {
        return new GroupQueryImpl(Context.getProcessEngineConfiguration().getCommandExecutor());
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.GroupIdentityManager#findGroupByQueryCriteria(org.activiti.engine.impl.GroupQueryImpl, org.activiti.engine.impl.Page)
     */
    @Override
    public List<Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page) {
        if (query.getUserId() != null) {
            return findGroupsByUser(query.getUserId());
        } else {
            throw new ActivitiIllegalArgumentException("This query is not supported by Unidata group manager");
        }
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.GroupIdentityManager#findGroupCountByQueryCriteria(org.activiti.engine.impl.GroupQueryImpl)
     */
    @Override
    public long findGroupCountByQueryCriteria(GroupQueryImpl query) {
        return findGroupByQueryCriteria(query, null).size();
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.GroupIdentityManager#findGroupsByUser(java.lang.String)
     */
    @Override
    public List<Group> findGroupsByUser(String userId) {

        List<Role> dto = roleService.getAllRolesByUserLogin(userId);
        if (CollectionUtils.isEmpty(dto)) {
            return Collections.emptyList();
        }

        List<Group> groups = new ArrayList<>();
        for (int i = 0; dto != null && i < dto.size(); i++) {

            Role r = dto.get(i);
            GroupEntity group = new GroupEntity();

            group.setId(r.getName());
            group.setName(r.getDisplayName());
            group.setRevision(0);
            group.setType(IdentityLinkType.CANDIDATE);

            groups.add(group);
        }

        return groups;
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.GroupIdentityManager#findGroupsByNativeQuery(java.util.Map, int, int)
     */
    @Override
    public List<Group> findGroupsByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults) {
        throw new ActivitiException("Unidata group manager doesn't support native querying");
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.GroupIdentityManager#findGroupCountByNativeQuery(java.util.Map)
     */
    @Override
    public long findGroupCountByNativeQuery(Map<String, Object> parameterMap) {
        throw new ActivitiException("Unidata group manager doesn't support native querying");
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.GroupIdentityManager#isNewGroup(org.activiti.engine.identity.Group)
     */
    @Override
    public boolean isNewGroup(Group group) {
        throw new ActivitiException("Unidata group manager doesn't support inserting or updating a group");
    }

}
