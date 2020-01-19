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

package org.unidata.mdm.core.type.audit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.unidata.mdm.core.type.security.SecurityToken;
import org.unidata.mdm.core.util.SecurityUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

@Deprecated
public class Event {

    public static final String ETALON_ID = "etalonId";
    public static final String ORIGIN_ID = "originId";
    public static final String EXTERNAL_ID = "externalId";
    public static final String OPERATION_ID = "operationId";
    public static final String SERVER_IP = "serverIp";
    public static final String CLIENT_IP = "clientIp";
    public static final String ENDPOINT = "endpoint";
    public static final String DETAILS = "details";
    public static final String DATE = "date";
    public static final String USER = "user";
    public static final String ENTITY = "entity";
    public static final String SUB_SYSTEM = "subSystem";
    public static final String ACTION = "action";
    public static final String SUCCESS = "success";
    public static final String SOURCE_SYSTEM = "sourceSystem";
    public static final String TASK_ID = "taskId";

    public static final ThreadLocal<DateFormat> DATE_FORMATTER = ThreadLocal.withInitial(
            () -> DateFormat.getDateInstance(SimpleDateFormat.FULL, new Locale("ru")));

    /**
     * weakly typed data structure for collecting audit data
     */
    private Multimap<String, Object> eventMap = HashMultimap.create(10, 2);

    /**
     * @param subSystem - subsystem
     * @param action    -action
     * @param error     - error
     */
    public Event(@Nonnull String subSystem, @Nonnull String action, @Nullable String error) {
        eventMap.put(DATE, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ZonedDateTime.now()));
        eventMap.put(SUB_SYSTEM, subSystem);
        eventMap.put(ACTION, action);
        if (error != null) {
            eventMap.put(DETAILS, error);
        }
        eventMap.put(SUCCESS, error == null);

        // Security audit will enrich details later
        boolean authAuditAction =
                StringUtils.equals("LOGIN", action)
             || StringUtils.equals("LOGOUT", action);
        if (authAuditAction) {
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof SecurityToken) {
            setUserDetails((SecurityToken) authentication.getDetails());
        } else {
            setUserDetails(null);
        }
    }

    public void setUserDetails(SecurityToken securityToken) {
        if(!eventMap.containsKey(USER)){
            if(securityToken == null || securityToken.getUser().getLogin() == null){
                eventMap.put(USER, SecurityUtils.getCurrentUserName());
            } else {
                //todo looks like deprecated
                eventMap.put(USER, securityToken.getUser().getLogin());
                eventMap.put(ENDPOINT, securityToken.getEndpoint() == null ? null : securityToken.getEndpoint().name());
                eventMap.put(SERVER_IP, securityToken.getServerIp());
                eventMap.put(CLIENT_IP, securityToken.getUserIp());
            }
        }
    }

    public void addEtalonId(String etalonId) {
        if (StringUtils.isBlank(etalonId)) {
            return;
        }
        eventMap.put(ETALON_ID, etalonId);
    }

    public void addExternalId(String externalId) {
        if (StringUtils.isBlank(externalId)) {
            return;
        }
        eventMap.put(EXTERNAL_ID, externalId);
    }

    public void addOriginId(String originId) {
        if (StringUtils.isBlank(originId)) {
            return;
        }
        eventMap.put(ORIGIN_ID, originId);
    }

    public void putTaskId(String taskId) {
        if (StringUtils.isBlank(taskId) || eventMap.containsKey(TASK_ID)) {
            return;
        }
        eventMap.put(TASK_ID, taskId);
    }

    public void putOperationId(String operationId) {
        if (StringUtils.isBlank(operationId)) {
            return;
        }
        eventMap.removeAll(OPERATION_ID);
        eventMap.put(OPERATION_ID, operationId);
    }

    public void putDetails(String details) {
        if (StringUtils.isBlank(details) || eventMap.containsKey(DETAILS)) {
            return;
        }
        eventMap.put(DETAILS, details);
    }

    public void putEntity(String entity) {
        if (StringUtils.isBlank(entity)) {
            return;
        }
        eventMap.removeAll(ENTITY);
        eventMap.put(ENTITY, entity);
    }

    public void putSourceSystem(String sourceSystem) {
        if (StringUtils.isBlank(sourceSystem)) {
            return;
        }
        eventMap.removeAll(SOURCE_SYSTEM);
        eventMap.put(SOURCE_SYSTEM, sourceSystem);
    }

    @Nullable
    public Object get(String type) {
        if (!eventMap.containsKey(type) || eventMap.get(type).isEmpty()) {
            return null;
        }
        Collection<Object> result = eventMap.get(type);
        return result.size() == 1 ? result.iterator().next() : result;
    }

    public void reclaim(String type, String val) {
        eventMap.removeAll(type);
        eventMap.put(type, val);
    }

    public void putAction(@Nonnull final String action) {
        eventMap.removeAll(ACTION);
        eventMap.put(ACTION, action);
    }

    @Override
    public String toString() {
        return eventMap.toString();
    }
}
