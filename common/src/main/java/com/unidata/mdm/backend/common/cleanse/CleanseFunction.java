package com.unidata.mdm.backend.common.cleanse;

import java.util.Collections;
import java.util.Map;

import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
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
     */
    @Deprecated
    default Map<String, Object> execute(Map<String, Object> input) throws Exception { return Collections.emptyMap(); }

    /**
     * Executes a cleanse function in the given context.
     * @param ctx the context
     */
    default void execute(CleanseFunctionContext ctx) {
        // Nothing
    }
    /**
     * Returns true, if supports the second form of execution.
     * @return true, if so, false otherwise
     */
    default boolean isContextAware() {
        return false;
    }
}
