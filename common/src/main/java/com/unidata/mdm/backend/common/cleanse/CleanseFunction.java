package com.unidata.mdm.backend.common.cleanse;

import java.util.Map;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.meta.CleanseFunctionExtendedDef;

/**
 * @author Michael Yashin. Created on 09.06.2015.
 */
public interface CleanseFunction {
    /**
     * Returns CleanseFunction definition in terms of
     * - name
     * - description
     * - list of input ports
     * - list of output ports
     *
     * @return
     */
    CleanseFunctionExtendedDef getDefinition();

    /**
     * Executes CleanseFunction
     *
     * @param input map of input perameters
     * @return map of output parameters
     * @throws CleanseFunctionExecutionException execution level exception.
     */
    Map<String, Object> execute(Map<String, Object> input) throws Exception;

}
