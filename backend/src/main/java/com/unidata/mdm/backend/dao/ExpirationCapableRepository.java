package com.unidata.mdm.backend.dao;

/**
 * @author Michael Yashin. Created on 29.03.2015.
 */
public interface ExpirationCapableRepository {

    long removeExpiredRecords(int lifetimeInSeconds);

}
