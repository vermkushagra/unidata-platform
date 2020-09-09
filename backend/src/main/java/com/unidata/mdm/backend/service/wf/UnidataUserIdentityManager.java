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
package com.unidata.mdm.backend.service.wf;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.Picture;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.AbstractManager;
import org.activiti.engine.impl.persistence.entity.IdentityInfoEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.UserIdentityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.unidata.mdm.backend.common.dto.security.UserWithPasswordDTO;
import com.unidata.mdm.backend.common.service.UserService;


/**
 * @author Mikhail Mikhailov
 * Unidata user manager for activiti sessions.
 */
public class UnidataUserIdentityManager extends AbstractManager implements UserIdentityManager {

    /**
     * User service instance.
     */
    @Autowired
    private UserService userService;
    /**
     * Group manager instance.
     */
    @Autowired
    private UnidataGroupIdentityManger groupManager;
    /**
     * Constructor.
     */
    public UnidataUserIdentityManager() {
        super();
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.UserIdentityManager#createNewUser(java.lang.String)
     */
    @Override
    public User createNewUser(String userId) {
        throw new ActivitiException("Unidata user manager doesn't support creating a new user");
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.UserIdentityManager#insertUser(org.activiti.engine.identity.User)
     */
    @Override
    public void insertUser(User user) {
        throw new ActivitiException("Unidata user manager doesn't support inserting a new user");

    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.UserIdentityManager#updateUser(org.activiti.engine.identity.User)
     */
    @Override
    public void updateUser(User updatedUser) {
        throw new ActivitiException("Unidata user manager doesn't support updating of a user");
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.UserIdentityManager#findUserById(java.lang.String)
     */
    @Override
    public User findUserById(String userId) {

        // TODO add support dor external users!!!
        UserWithPasswordDTO dto = userService.getUserByName(userId);
        if (dto != null) {
            UserEntity result = new UserEntity(userId);
            result.setEmail(dto.getEmail());
            result.setFirstName(dto.getFirstName());
            result.setLastName(dto.getLastName());
            result.setPassword(dto.getPassword());
            result.setRevision(0);

            return result;
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.UserIdentityManager#deleteUser(java.lang.String)
     */
    @Override
    public void deleteUser(String userId) {
        throw new ActivitiException("Unidata user manager doesn't support deleting of a user");
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.UserIdentityManager#findUserByQueryCriteria(org.activiti.engine.impl.UserQueryImpl, org.activiti.engine.impl.Page)
     */
    @Override
    public List<User> findUserByQueryCriteria(UserQueryImpl query, Page page) {

        if (query.getId() != null) {
            User u = findUserById(query.getId());
            return u != null ? Collections.emptyList() : Collections.singletonList(u);
        }

        return Collections.emptyList();
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.UserIdentityManager#findUserCountByQueryCriteria(org.activiti.engine.impl.UserQueryImpl)
     */
    @Override
    public long findUserCountByQueryCriteria(UserQueryImpl query) {
        return findUserByQueryCriteria(query, null).size();
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.UserIdentityManager#findGroupsByUser(java.lang.String)
     */
    @Override
    public List<Group> findGroupsByUser(String userId) {
        return groupManager.findGroupsByUser(userId);
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.UserIdentityManager#createNewUserQuery()
     */
    @Override
    public UserQuery createNewUserQuery() {
        return new UserQueryImpl(Context.getProcessEngineConfiguration().getCommandExecutor());
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.UserIdentityManager#findUserInfoByUserIdAndKey(java.lang.String, java.lang.String)
     */
    @Override
    public IdentityInfoEntity findUserInfoByUserIdAndKey(String userId, String key) {
        throw new ActivitiException("Unidata user manager doesn't support quering of a user by key");
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.UserIdentityManager#findUserInfoKeysByUserIdAndType(java.lang.String, java.lang.String)
     */
    @Override
    public List<String> findUserInfoKeysByUserIdAndType(String userId, String type) {
        throw new ActivitiException("Unidata user manager doesn't support quering of a user by type");
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.UserIdentityManager#checkPassword(java.lang.String, java.lang.String)
     */
    @Override
    public Boolean checkPassword(String userId, String password) {
        User u = findUserById(userId);
        return u != null && u.getPassword() != null && password != null && BCrypt.checkpw(password, u.getPassword());
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.UserIdentityManager#findPotentialStarterUsers(java.lang.String)
     */
    @Override
    public List<User> findPotentialStarterUsers(String proceDefId) {
        throw new ActivitiException("Unidata user manager doesn't support quering of a user by process id");
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.UserIdentityManager#findUsersByNativeQuery(java.util.Map, int, int)
     */
    @Override
    public List<User> findUsersByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults) {
        throw new ActivitiException("Unidata user manager doesn't support native quering of a user");
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.UserIdentityManager#findUserCountByNativeQuery(java.util.Map)
     */
    @Override
    public long findUserCountByNativeQuery(Map<String, Object> parameterMap) {
        throw new ActivitiException("Unidata user manager doesn't support native quering of a user");
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.UserIdentityManager#isNewUser(org.activiti.engine.identity.User)
     */
    @Override
    public boolean isNewUser(User user) {
        throw new ActivitiException("Unidata user manager doesn't support creating or updating of a new user");
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.UserIdentityManager#getUserPicture(java.lang.String)
     */
    @Override
    public Picture getUserPicture(String userId) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.persistence.entity.UserIdentityManager#setUserPicture(java.lang.String, org.activiti.engine.identity.Picture)
     */
    @Override
    public void setUserPicture(String userId, Picture picture) {
        throw new ActivitiException("Unidata user manager doesn't support setting of user picture");
    }

}
