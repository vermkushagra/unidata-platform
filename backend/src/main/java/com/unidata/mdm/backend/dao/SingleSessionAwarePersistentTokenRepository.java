package com.unidata.mdm.backend.dao;

/**
 * @author Michael Yashin. Created on 29.03.2015.
 */
public interface SingleSessionAwarePersistentTokenRepository {
    /*
     * Delete session token by series - is used to logout only this session for login leaving other active
     */
    void removeUserTokenBySeries(String series);
}
