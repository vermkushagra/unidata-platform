package com.unidata.mdm.backend.common.security;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.common.integration.auth.User;

/**
 * The Class SecurityToken.
 */
public class SecurityToken implements Serializable {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The id.
     */
    private int id;
    /**
     * The token.
     */
    private String token;

    /**
     * The created at.
     */
    private Date createdAt;

    /**
     * The last used at.
     */
    private Date lastUsedAt;

    /**
     * The expiring at.
     */
    private Date expiringAt;

    /**
     * The user.
     */
    private User user;
    /**
     * Endpoint, audit related.
     */
    private Endpoint endpoint;
    /**
     * IP, audit related.
     */
    private String userIp;
    /**
     * Server IP, audit related
     */
    private String serverIp;
    /**
     * User rights map.
     */
    private final Map<String, Right> rightsMap = new HashMap<>();
    /**
     * Roles map.
     */
    private final Map<String, Role> rolesMap = new HashMap<>();
    /**
     * Resource to labeles map.
     */
    private final Map<String, List<SecurityLabel>> labelsMap = new HashMap<>();
    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the token.
     *
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Gets the created at.
     *
     * @return the createdAt
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the created at.
     *
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user.
     *
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the rightsMap
     */
    public Map<String, Right> getRightsMap() {
        return rightsMap;
    }


    /**
     * @return the rolesMap
     */
    public Map<String, Role> getRolesMap() {
        return rolesMap;
    }

    /**
     * Tells whether a user has particular role.
     * @param roleName the name
     * @return true, if he has, false, if he hasn't
     */
    public boolean hasRole(String roleName) {
        return rolesMap.containsKey(roleName);
    }

    /**
     * @return the resourcesMap
     */
    public Map<String, List<SecurityLabel>> getLabelsMap() {
        return labelsMap;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
    @Override
    public String toString() {
        return "SecurityToken [id=" + id +
                ", token=" + token +
                ", createdAt=" + createdAt +
                ", lastUsedAt=" + lastUsedAt +
                ", expiringAt=" + expiringAt +
                ", user=" + user + "]";
    }
}
