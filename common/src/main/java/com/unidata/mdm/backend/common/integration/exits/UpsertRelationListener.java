package com.unidata.mdm.backend.common.integration.exits;

import com.unidata.mdm.backend.common.types.OriginRelation;

/**
 * @author Dmitry Kopin
 * Upsert relation listener interface, suitable for implementation by integrating side.
 */
public interface UpsertRelationListener {
    /**
     * The method is called before relation origin record update operation,
     * but after all the system 'before' update handlers have finished their job.
     * @param origin the relation record, that we're going to save
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return {@link ExitResult} with information about execution user exit
     * Note: the method is called inside the saving transaction.
     * If the method returns false, the transaction will be rolled back and the whole operation will be aborted. Thus, returning false basically prevents save operation.
     */
    default ExitResult beforeOriginRelationUpdate(OriginRelation origin, ExecutionContext ctx){
        return null;
    }
    /**
     * The method is called before relation origin record insert operation,
     * but after all the system 'before' insert handlers have finished their job.
     * @param origin the record, that we're going to save
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return {@link ExitResult} with information about execution user exit
     * Note: the method is called inside the saving transaction.
     * If the method returns false, the transaction will be rolled back and the whole operation will be aborted. Thus, returning false basically prevents save operation.
     */
    default ExitResult beforeOriginRelationInsert(OriginRelation origin, ExecutionContext ctx){
        return null;
    }
    /**
     * The method is called after relation origin record update operation,
     * but after all the system 'after' update handlers have finished their job.
     * @param origin the record, that we're going to save
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return {@link ExitResult} with information about execution user exit
     */
    default ExitResult afterOriginRelationUpdate(OriginRelation origin, ExecutionContext ctx){
        return null;
    }
    /**
     * The method is called after relation origin record insert operation,
     * but after all the system 'after' insert handlers have finished their job.
     * @param origin the record, that we're going to save
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return {@link ExitResult} with information about execution user exit
     */
    default ExitResult afterOriginRelationInsert(OriginRelation origin, ExecutionContext ctx){
        return null;
    }
}
