package com.unidata.mdm.backend.dao;

import com.unidata.mdm.backend.po.ErrorPO;

/**
 * Dao for errors
 */
public interface ErrorDao<T extends ErrorPO> {

    /**
     * Log error to DB
     *
     * @param error - error
     */
    void logError(T error);
}
