package org.unidata.mdm.core.service.ext;

import java.util.List;
import java.util.Locale;

import org.unidata.mdm.core.service.SecurityService;

public interface SecurityServiceExt extends SecurityService {

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

    void updateCurrentTokensWithRole(List<String> roleNames);

    /**
     * Change user locale on token
     * @param login user login
     * @param locale new locale
     */
    void changeLocale(String login, Locale locale);
}