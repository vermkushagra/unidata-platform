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

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.GroupIdentityManager;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author Mikhail Mikhailov
 * Custom group management factory.
 */
public class UnidataGroupManagerFactory implements SessionFactory {

    /**
     * Group identity manager.
     */
    @Autowired
    private UnidataGroupIdentityManger unidataGroupIdentityManger;

    /**
     * Constructor.
     */
    public UnidataGroupManagerFactory() {
        super();
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.interceptor.SessionFactory#getSessionType()
     */
    @Override
    public Class<?> getSessionType() {
        return GroupIdentityManager.class;
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.interceptor.SessionFactory#openSession()
     */
    @Override
    public Session openSession() {
        return unidataGroupIdentityManger;
    }

}
