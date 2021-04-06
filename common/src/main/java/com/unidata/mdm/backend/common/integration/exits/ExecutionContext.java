/**
 *
 */
package com.unidata.mdm.backend.common.integration.exits;

import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Execution context exposed to the user.
 */
public interface ExecutionContext {
    /**
     * Puts a value to the context storage.
     * @param name the key
     * @param t the value
     */
    public<T extends Object> void putToUserContext(String name, T t);
    /**
     * Gets a value from the context storage, using supplied key.
     * @param name the key
     * @return object or null
     */
    public<T extends Object> T getFromUserContext(String name);
    /**
     * Gets a (read only) value from environment.
     * @param key the key
     * @return value or null
     */
    public String getFromEnvironment(String key);
    /**
     * Gets current authentication token.
     * @return token
     */
    public AuthenticationToken getAuthenticationToken();
    /**
     * @param header special header which can be used in routing
     */
    public void addCustomMessageHeader(String headerKey, Object header);
    /**
     * @return custom user message header if exist.
     */
    public Map<String, Object> getCustomMessageHeaders();
}
