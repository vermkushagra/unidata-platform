package org.unidata.mdm.core.service.impl;

import org.unidata.mdm.core.service.SecurityService;
import org.unidata.mdm.system.service.AfterContextRefresh;

public interface SecurityServiceExt extends SecurityService, AfterContextRefresh {
    /**
     * Sets the token ttl.
     *
     * @param tokenTTL the tokenTTL to set
     */
    void setTokenTTL(long tokenTTL);

    /**
     * @return the clusterName
     */
    String getClusterName();

    /**
     * @param clusterName the clusterName to set
     */
    void setClusterName(String clusterName);

    /**
     * @return the mapName
     */
    String getMapName();

    /**
     * @param mapName the mapName to set
     */
    void setMapName(String mapName);

    /**
     * update inner token by login
     * @param login
     */
    void updateInnerToken(String login);

    /**
     * update inner token by login
     * @param roleName
     */
    void updateInnerTokensWithRole(String roleName);
}