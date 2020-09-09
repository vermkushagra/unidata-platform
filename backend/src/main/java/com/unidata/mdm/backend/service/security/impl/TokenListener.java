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

package com.unidata.mdm.backend.service.security.impl;

import java.util.EnumMap;
import java.util.Map;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryExpiredListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter;
import com.unidata.mdm.backend.common.security.SecurityToken;
import com.unidata.mdm.backend.dao.UserDao;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import com.unidata.mdm.backend.service.audit.AuditLocalizationConstants;
import com.unidata.mdm.backend.util.MessageUtils;


/**
 * The listener interface for receiving token events. The class that is
 * interested in processing a token event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addTokenListener<code> method. When the token event occurs,
 * that object's appropriate method is invoked.
 *
 * @see TokenEvent
 * @author ilya.bykov
 */
public class TokenListener implements
    EntryRemovedListener<String, SecurityToken>,
    EntryExpiredListener<String, SecurityToken>,
    EntryEvictedListener<String, SecurityToken> {

    /** The audit event writer. */
    private AuditEventsWriter auditEventsWriter;

    /** The user dao. */
    private UserDao userDao;

    /**
     * Instantiates a new token listener.
     *
     * @param auditEventsWriter the audit event writer
     * @param userDao           the user dao
     */
    public TokenListener(AuditEventsWriter auditEventsWriter, UserDao userDao) {
        this.auditEventsWriter = auditEventsWriter;
        this.userDao = userDao;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hazelcast.map.listener.EntryRemovedListener#entryRemoved(com.
     * hazelcast.core.EntryEvent)
     */
    @Override
    public void entryRemoved(EntryEvent<String, SecurityToken> event) {
        userDao.deleteToken(event.getKey());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void entryEvicted(EntryEvent<String, SecurityToken> event) {
        userDao.deleteToken(event.getKey());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hazelcast.map.listener.EntryEvictedListener#entryEvicted(com.
     * hazelcast.core.EntryEvent)
     */
    @Override
    public void entryExpired(EntryEvent<String, SecurityToken> event) {
        // No need to delete token. It is already done above in entryEvicted()
        String userName = event.getOldValue().getUser().getLogin();
        Map<AuthenticationSystemParameter, Object> params = new EnumMap<>(AuthenticationSystemParameter.class);
        params.put(AuthenticationSystemParameter.PARAM_DETAILS,
                MessageUtils.getMessage(AuditLocalizationConstants.LOGOUT_BY_TIMEOUT));
        params.put(AuthenticationSystemParameter.PARAM_USER_NAME, userName);
        auditEventsWriter.writeSuccessEvent(AuditActions.LOGOUT, params);
    }
}