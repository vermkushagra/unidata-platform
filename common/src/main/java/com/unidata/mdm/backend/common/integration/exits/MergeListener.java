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

import java.util.List;

import com.unidata.mdm.backend.common.types.EtalonRecord;

/**
 * @author Mikhail Mikhailov
 * Merge listener interface.
 */
public interface MergeListener {
    /**
     * The method is called before merge operation,
     * but after all the system 'before' merge handlers have finished their job.
     * @param etalon the record, that remains active (the winner)
     * @param duplicates the records that will be deactivated (duplicates)
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return true if successful, false otherwise.
     * Note: the method is called inside the main transaction.
     * If the method returns false, the transaction will be rolled back and the whole operation will be aborted. Thus, returning false basically prevents save operation.
     */
    public boolean beforeMerge(EtalonRecord etalon, List<EtalonRecord> duplicates, ExecutionContext ctx);
    /**
     * The method is called after merge operation,
     * but after all the system 'after' merge handlers have finished their job.
     * The method is called outside of the saving transaction.
     * @param etalon the record, that remains active
     * @param duplicates the duplicates, that will be deactivated
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     */
    public void afterMerge(EtalonRecord etalon, List<EtalonRecord> duplicates, ExecutionContext ctx);
}
