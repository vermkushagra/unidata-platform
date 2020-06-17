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
package com.unidata.mdm.backend.common.context;

import static java.util.Objects.isNull;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.unidata.mdm.backend.common.integration.exits.AuthenticationToken;
import com.unidata.mdm.backend.common.integration.exits.ExecutionContext;
import com.unidata.mdm.backend.common.service.ServiceUtils;

/**
 * @author Mikhail Mikhailov
 *         Base data manipulation context class.
 */
public abstract class CommonRequestContext implements ExecutionContext, Serializable {

    /*
     * Very Ugly Stuff.
     */
    private static final String CONFIGURATION_SERVICE_FQN = "com.unidata.mdm.backend.service.configuration.ConfigurationService";
    private static final String SECURITY_CONTEXT_HOLDER_FQN = "org.springframework.security.core.context.SecurityContextHolder";
    private static final String SECURITY_CONTEXT_FQN = "org.springframework.security.core.context.SecurityContext";

    private static final String GET_SECURITY_CONTEXT_METHOD = "getContext";
    private static final String GET_SYS_STRING_PROPERTY_METHOD = "getSystemStringProperty";
    private static final String GET_AUTHENTICATION_METHOD = "getAuthentication";

    private static Method SYSTEM_STRING_METHOD;
    private static Method SECURITY_CONTEXT_HOLDER_METHOD;
    private static Method SECURITY_CONTEXT_METHOD;

    /**
     * Reserved key for custom message headers.
     */
    private static final String CUSTOM_HEADERS = "custom_headers";

    /**
     * Default SVUID.
     */
    private static final long serialVersionUID = 5163431222466721757L;

    static {
        try {
            Class<?> csc = Class.forName(CONFIGURATION_SERVICE_FQN);
            SYSTEM_STRING_METHOD = csc.getDeclaredMethod(GET_SYS_STRING_PROPERTY_METHOD, String.class);

            Class<?> sch = Class.forName(SECURITY_CONTEXT_HOLDER_FQN);
            SECURITY_CONTEXT_HOLDER_METHOD = sch.getDeclaredMethod(GET_SECURITY_CONTEXT_METHOD);

            Class<?> sc = Class.forName(SECURITY_CONTEXT_FQN);
            SECURITY_CONTEXT_METHOD = sc.getDeclaredMethod(GET_AUTHENTICATION_METHOD);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            // Nothing.
        }
    }

    /**
     * Operation id, special attribute for grouping actions.
     */
    private String operationId;
    /**
     * Simple system single thread storage.
     */
    private Map<StorageId, Object> systemStorage = new EnumMap<>(StorageId.class);
    /**
     * Simple storage to be used by user exits.
     */
    private Map<String, Object> userStorage = new HashMap<>();
    /**
     * Various boolean flags.
     */
    protected BitSet flags = new BitSet();
    /**
     * Dyn initializer.
     */
    {
        userStorage.put(CUSTOM_HEADERS, new HashMap<>());
    }

    /**
     * Constructor.
     */
    public CommonRequestContext() {
        super();
    }

    /**
     * Puts an object to temp storage.
     *
     * @param id id
     * @param t  object
     */
    public <T extends Object> void putToStorage(StorageId id, T t) {
        systemStorage.put(id, t);
    }
    /**
     * Sets a known flag true.
     * @param flag the flag to set
     */
    public void setFlag(int flag) {
        flags.set(flag);
    }
    /**
     * Sets a known flag false.
     * @param flag the flag to clear
     */
    public void clearFlag(int flag) {
        flags.clear(flag);
    }
    /**
     * Gets an object from temp storage.
     *
     * @param id id
     * @return object
     */
    @SuppressWarnings("unchecked")
    public <T extends Object> T getFromStorage(StorageId id) {
        return (T) systemStorage.get(id);
    }

    /**
     * @return operation id
     */
    public String getOperationId() {
        if (isNull(operationId)) {
            operationId = ServiceUtils.getPlatformConfiguration().v1IdString();
        }
        return operationId;
    }

    /**
     * Note: Can be set once!
     * @param operationId - operation id
     */
    public void setOperationId(String operationId) {
        if (isNull(this.operationId)) {
            this.operationId = operationId;
        }
    }

    /**
     * @see com.unidata.mdm.backend.common.integration.exits.ExecutionContext#putToUserContext(java.lang.String, java.lang.Object)
     */
    @Override
    public <T> void putToUserContext(String name, T t) {
        userStorage.put(name, t);
    }

    void copyAllFromUserContext(CommonRequestContext other) {
        this.userStorage.putAll(other.userStorage);
    }

    /**
     * @see com.unidata.mdm.backend.common.integration.exits.ExecutionContext#getFromUserContext(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getFromUserContext(String name) {
        return (T) userStorage.get(name);
    }

    /**
     * @see com.unidata.mdm.backend.common.integration.exits.ExecutionContext#addCustomMessageHeader(java.lang.String, java.lang.Object)
     */
    @Override
    public void addCustomMessageHeader(String headerKey, Object header) {
        Map<String, Object> headers = getFromUserContext(CUSTOM_HEADERS);
        headers.put(headerKey, header);
    }

    /**
     * @see com.unidata.mdm.backend.common.integration.exits.ExecutionContext#getCustomMessageHeaders()
     */
    @Override
    public Map<String, Object> getCustomMessageHeaders() {
        return getFromUserContext(CUSTOM_HEADERS);
    }

    /**
     * Returns true, if the
     *
     * @param name
     * @return
     */
    public boolean isSetInUserContext(String name) {
        return userStorage.containsKey(name);
    }

    /**
     * @see com.unidata.mdm.backend.common.integration.exits.ExecutionContext#getFromEnvironment(java.lang.String)
     */
    @Override
    public String getFromEnvironment(String key) {

        try {
            return SYSTEM_STRING_METHOD != null ? (String) SYSTEM_STRING_METHOD.invoke(null, key) : null;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // Nothing
        }

        return null;
    }

    /**
     * @see com.unidata.mdm.backend.common.integration.exits.ExecutionContext#getAuthenticationToken()
     */
    @Override
    public AuthenticationToken getAuthenticationToken() {

        try {
            if(SECURITY_CONTEXT_HOLDER_METHOD != null) {

                Object securityContext = SECURITY_CONTEXT_HOLDER_METHOD.invoke(null, (Object[]) null);
                if (securityContext != null) {
                    Object authentication = SECURITY_CONTEXT_METHOD.invoke(securityContext, (Object[]) null);
                    return authentication != null && AuthenticationToken.class.isInstance(authentication)
                            ? (AuthenticationToken) authentication
                            : null;
                }
            }

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // Nothing
        }

        return null;
    }
}
