package com.unidata.mdm.backend.common.service;

import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.meta.CleanseFunctionExtendedDef;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;

/**
 * @author Mikhail Mikhailov
 * @author ilya.bykov
 * Cleanse function service public interface.
 */
public interface CleanseFunctionService {
    /**
     * Does CF validity check.
     * @param cleanseFunctionName the cleanse function to check
     * @return true, if CF is ok
     */
    boolean isAvailable(String cleanseFunctionName);
    /**
     * Executes a cleanse function in the given execution context.
     * @param cfc the context
     */
    void execute(CleanseFunctionContext cfc);
    /**
     * Returns complete list of cleanse functions.
     *
     * @return the all
     */
    CleanseFunctionGroupDef getAll();

    /**
     * Returns cleanse function definition by id.
     *
     * @param pathID
     *            the path id
     * @return the by id
     */
    CleanseFunctionExtendedDef getFunctionInfoById(String pathID);
    /**
     * Gets the by id.
     *
     * @param pathID
     *            the path id
     * @param compositeCleanseFunctionDef
     *            the composite cleanse function def
     */
    void upsertCompositeCleanseFunction(String pathID, CompositeCleanseFunctionDef compositeCleanseFunctionDef);
    /**
     * Removes the function by id.
     *
     * @param pathID
     *            the path id
     */
    void removeFunctionById(String pathID);
    /**
     * Send init signal.
     *
     * @param tempId
     *            the temp id
     */
    void sendInitSignal(String tempId);
    /**
     * Delete custom or composite function by name.
     * @param name cleanse function name.
     */
    void deleteFunction(String name);
}
