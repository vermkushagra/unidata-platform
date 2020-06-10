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
     * Now is deprecated. Please use {@link #beforeEtalonDeactivationWithResult}
     * The method is called before delete (deactivate) operation,
     * but after all the system 'before' delete handlers have finished their job.
     * @param etalon the record, that is deactivated
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return true if successful, false otherwise.
     * Note: the method is called inside the main transaction.
     * If the method returns false, the transaction will be rolled back and the whole operation will be aborted. Thus, returning false basically prevents save operation.
     */
    @Deprecated
    boolean beforeEtalonDeactivation(EtalonRecord etalon, ExecutionContext ctx);

    /**
     * The method is called before delete (deactivate) operation,
     * but after all the system 'before' delete handlers have finished their job.
     * @param etalon the record, that is deactivated
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return true if successful, false otherwise.
     * @return Exit result
     * Note: the method is called inside the main transaction.
     * If the method returns false, the transaction will be rolled back and the whole operation will be aborted. Thus, returning false basically prevents save operation.
     */
    default ExitResult beforeEtalonDeactivationWithResult(EtalonRecord etalon, ExecutionContext ctx) {
        return new ExitResult();
    }

    /**
     * Now is deprecated. Please use {@link #afterEtalonDeactivationWithResult}
     * The method is called after etalon record delete (deactivate) operation,
     * but after all the system 'after' etalon deactivate handlers have finished their job.
     * The method is called outside of the transaction.
     * @param etalon the record, that we're going to delete (deactivate)
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     */
    @Deprecated
    void afterEtalonDeactivation(EtalonRecord etalon, ExecutionContext ctx);

    /**
     * The method is called after etalon record delete (deactivate) operation,
     * but after all the system 'after' etalon deactivate handlers have finished their job.
     * The method is called outside of the transaction.
     * @param etalon the record, that we're going to delete (deactivate)
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return Exit result
     */
    default ExitResult afterEtalonDeactivationWithResult(EtalonRecord etalon, ExecutionContext ctx) {
        return new ExitResult();
    }
}
