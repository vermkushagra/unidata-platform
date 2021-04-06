package com.unidata.mdm.backend.common.service;

import java.util.Map;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
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
     * Execute single cleanse function.
     *
     * @param input
     *            the input
     * @param pathID
     *            the path id
     * @return the map
     * @throws CleanseFunctionExecutionException
     *             the exception
     */
    Map<String, Object> executeSingle(Map<String, Object> input, String pathID)
            throws CleanseFunctionExecutionException;

    /**
     * Returns complete list of cleanse functions.
     *
     * @return the all
     * @throws CleanseFunctionExecutionException
     *             the exception
     */
    CleanseFunctionGroupDef getAll() throws CleanseFunctionExecutionException;

    /**
     * Returns cleanse function definition by id.
     *
     * @param pathID
     *            the path id
     * @return the by id
     */
    CleanseFunctionExtendedDef getByID(String pathID);

    /**
     * Gets the by id.
     *
     * @param pathID
     *            the path id
     * @param compositeCleanseFunctionDef
     *            the composite cleanse function def
     * @return the by id
     * @throws CleanseFunctionExecutionException
     *             Cleanse function exception
     */
    void upsertCompositeCleanseFunction(String pathID, CompositeCleanseFunctionDef compositeCleanseFunctionDef)
            throws CleanseFunctionExecutionException;

    /**
     * Removes the function by id.
     *
     * @param pathID
     *            the path id
     * @throws CleanseFunctionExecutionException
     *             the cleanse function exception
     */
    void removeFunctionById(String pathID) throws CleanseFunctionExecutionException;

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
