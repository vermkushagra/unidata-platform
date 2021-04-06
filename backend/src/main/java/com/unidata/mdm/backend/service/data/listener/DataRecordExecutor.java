package com.unidata.mdm.backend.service.data.listener;

import com.unidata.mdm.backend.common.context.CommonRequestContext;
/**
 * Data record action support
 * @author ilya.bykov
 *
 * @param <T> 
 */
public interface DataRecordExecutor<T extends CommonRequestContext> {
    /**
     * Executes a specific portion of functionality, before record's persistent state change.
     * @param t the context
     * @return true, if successful, false otherwise
     */
    public boolean execute(T t);
}
