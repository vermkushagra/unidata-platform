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
