package com.unidata.mdm.backend.service.security;

import com.unidata.mdm.backend.common.service.SecurityService;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;

public interface SecurityServiceExt extends SecurityService, AfterContextRefresh {

    /**
     * Gets the token ttl.
     *
     * @return the tokenTTL
     */
    long getTokenTTL();

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
}