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

/**
 *
 */
package com.unidata.mdm.backend.common.integration.exits;

import com.unidata.mdm.backend.common.types.EtalonRecord;

/**
 * @author Mikhail Mikhailov
 * Delete listener interface.
 */
public interface DeleteListener {
    /**
     * The method is called before delete (deactivate) operation,
     * but after all the system 'before' delete handlers have finished their job.
     * @param etalon the record, that is deactivated
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return true if successful, false otherwise.
     * Note: the method is called inside the main transaction.
     * If the method returns false, the transaction will be rolled back and the whole operation will be aborted. Thus, returning false basically prevents save operation.
     */
    public boolean beforeEtalonDeactivation(EtalonRecord etalon, ExecutionContext ctx);
    /**
     * The method is called after etalon record delete (deactivate) operation,
     * but after all the system 'after' etalon deactivate handlers have finished their job.
     * The method is called outside of the transaction.
     * @param etalon the record, that we're going to delete (deactivate)
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     */
    public void afterEtalonDeactivation(EtalonRecord etalon, ExecutionContext ctx);
}
