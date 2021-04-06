package com.unidata.mdm.backend.common.integration.exits;


import com.unidata.mdm.backend.common.types.EtalonRelation;

/**
 * @author Dmitry Kopin
 * Delete relation listener interface.
 */
public interface DeleteRelationListener {
    /**
     * The method is called before delete (deactivate) relation operation,
     * but after all the system 'before' delete handlers have finished their job.
     * @param etalonRelation etalon relation for remove
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return {@link ExitResult} with information about execution user exit
     * Note: the method is called inside the main transaction.
     * If the method returns false, the transaction will be rolled back and the whole operation will be aborted. Thus, returning false basically prevents save operation.
     */
    default ExitResult beforeRelationDeactivation(EtalonRelation etalonRelation, ExecutionContext ctx){
        return new ExitResult();
    }
    /**
     * The method is called after etalon record delete (deactivate) relation operation,
     * but after all the system 'after' etalon deactivate handlers have finished their job.
     * The method is called outside of the transaction.
     * @param etalonRelation etalon relation for remove
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return {@link ExitResult} with information about execution user exit
     */
    default ExitResult afterRelationDeactivation(EtalonRelation etalonRelation, ExecutionContext ctx){
        return new ExitResult();
    }
}
