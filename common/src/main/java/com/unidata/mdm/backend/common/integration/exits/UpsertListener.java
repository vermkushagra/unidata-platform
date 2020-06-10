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
import com.unidata.mdm.backend.common.types.OriginRecord;

/**
 * @author Mikhail Mikhailov
 * Upsert listener interface, suitable for implementation by integrating side.
 */
public interface UpsertListener {
    /**
     * The method is called before origin record update operation,
     * but after all the system 'before' update handlers have finished their job.
     * @param origin the record, that we're going to save
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return true if successful, false otherwise.
     * Note: the method is called inside the saving transaction.
     * If the method returns false, the transaction will be rolled back and the whole operation will be aborted. Thus, returning false basically prevents save operation.
     */
    @Deprecated
    boolean beforeOriginUpdate(OriginRecord origin, ExecutionContext ctx);
    /**
     * Now is deprecated. Please use {@link #beforeOriginUpdateWithResult}
     * The method is called before origin record update operation,
     * but after all the system 'before' update handlers have finished their job.
     * @param origin the record, that we're going to save
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return Exit result
     * Note: the method is called inside the saving transaction.
     * If the method returns false, the transaction will be rolled back and the whole operation will be aborted. Thus, returning false basically prevents save operation.
     */
    default ExitResult beforeOriginUpdateWithResult(OriginRecord origin, ExecutionContext ctx) {
        return new ExitResult();
    }
    /**
     * Now is deprecated. Please use {@link #beforeOriginInsertWithResult}
     * The method is called before origin record insert operation,
     * but after all the system 'before' insert handlers have finished their job.
     * @param origin the record, that we're going to save
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return true if successful, false otherwise.
     * Note: the method is called inside the saving transaction.
     * If the method returns false, the transaction will be rolled back and the whole operation will be aborted. Thus, returning false basically prevents save operation.
     */
    @Deprecated
    boolean beforeOriginInsert(OriginRecord origin, ExecutionContext ctx);
    /**
     * The method is called before origin record insert operation,
     * but after all the system 'before' insert handlers have finished their job.
     * @param origin the record, that we're going to save
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return Exit result
     * Note: the method is called inside the saving transaction.
     * If the method returns false, the transaction will be rolled back and the whole operation will be aborted. Thus, returning false basically prevents save operation.
     */
    default ExitResult beforeOriginInsertWithResult(OriginRecord origin, ExecutionContext ctx) {
        return new ExitResult();
    }
    /**
     * Now is deprecated. Please use {@link #afterOriginUpdateWithResult}
     * The method is called after origin record update operation,
     * but after all the system 'after' update handlers have finished their job.
     * @param origin the record, that we're going to save
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     */
    @Deprecated
    void afterOriginUpdate(OriginRecord origin, ExecutionContext ctx);
    /**
     * The method is called after origin record update operation,
     * but after all the system 'after' update handlers have finished their job.
     * @param origin the record, that we're going to save
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return Exit result
     */
    default ExitResult afterOriginUpdateWithResult(OriginRecord origin, ExecutionContext ctx){
        return new ExitResult();
    }
    /**
     * Now is deprecated. Please use {@link #afterOriginInsertWithResult}
     * The method is called after origin record insert operation,
     * but after all the system 'after' insert handlers have finished their job.
     * @param origin the record, that we're going to save
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     */
    @Deprecated
    void afterOriginInsert(OriginRecord origin, ExecutionContext ctx);
    /**
     * The method is called after origin record insert operation,
     * but after all the system 'after' insert handlers have finished their job.
     * @param origin the record, that we're going to save
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return Exit result
     */
    default ExitResult afterOriginInsertWithResult(OriginRecord origin, ExecutionContext ctx){
        return new ExitResult();
    }
    /**
     * Now is deprecated. Please use {@link #afterUpdateEtalonCompositionWithResult}
     * The method is called after etalon record period update operation,
     * but after all the system 'after' etalon update handlers have finished their job.
     * The method is called outside of the saving transaction.
     * @param etalon the record, that we're going to save
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     */
    @Deprecated
    void afterUpdateEtalonComposition(EtalonRecord etalon, ExecutionContext ctx);
    /**
     * The method is called after etalon record period update operation,
     * but after all the system 'after' etalon update handlers have finished their job.
     * The method is called outside of the saving transaction.
     * @param etalon the record, that we're going to save
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return Exit result
     */
    default ExitResult afterUpdateEtalonCompositionWithResult(EtalonRecord etalon, ExecutionContext ctx){
        return new ExitResult();
    }
    /**
     * Now is deprecated. Please use {@link #afterInsertEtalonCompositionWithResult}
     * The method is called after etalon record period insert operation,
     * but after all the system 'after' etalon insert handlers have finished their job.
     * The method is called outside of the saving transaction.
     * @param etalon the record, that we're going to save
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     */
    @Deprecated
    void afterInsertEtalonComposition(EtalonRecord etalon, ExecutionContext ctx);
    /**
     * The method is called after etalon record period insert operation,
     * but after all the system 'after' etalon insert handlers have finished their job.
     * The method is called outside of the saving transaction.
     * @param etalon the record, that we're going to save
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     */
    default ExitResult afterInsertEtalonCompositionWithResult(EtalonRecord etalon, ExecutionContext ctx){
        return new ExitResult();
    }
    /**
     * The method is called after all operations, when all modifications are done.
     * @param etalon the record, that we've finally got
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     */
    default void afterComplete(EtalonRecord etalon, ExecutionContext ctx) {}
}
