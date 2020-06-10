/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
