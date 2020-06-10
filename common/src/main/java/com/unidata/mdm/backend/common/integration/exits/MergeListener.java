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
     * Now is deprecated. Please use {@link #beforeMergeWithResult}
     * The method is called before merge operation,
     * but after all the system 'before' merge handlers have finished their job.
     * @param etalon the record, that remains active (the winner)
     * @param duplicates the records that will be deactivated (duplicates)
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return true if successful, false otherwise.
     * Note: the method is called inside the main transaction.
     * If the method returns false, the transaction will be rolled back and the whole operation will be aborted. Thus, returning false basically prevents save operation.
     */
    @Deprecated
    boolean beforeMerge(EtalonRecord etalon, List<EtalonRecord> duplicates, ExecutionContext ctx);
    /**
     * The method is called before merge operation,
     * but after all the system 'before' merge handlers have finished their job.
     * @param etalon the record, that remains active (the winner)
     * @param duplicates the records that will be deactivated (duplicates)
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return exit result.
     * Note: the method is called inside the main transaction.
     * If the method returns false, the transaction will be rolled back and the whole operation will be aborted. Thus, returning false basically prevents save operation.
     */
    default ExitResult beforeMergeWithResult(EtalonRecord etalon, List<EtalonRecord> duplicates, ExecutionContext ctx) {
        return new ExitResult();
    }
    /**
     * Now is deprecated. Please use {@link #afterMergeWithResult}
     * The method is called after merge operation,
     * but after all the system 'after' merge handlers have finished their job.
     * The method is called outside of the saving transaction.
     * @param etalon the record, that remains active
     * @param duplicates the duplicates, that will be deactivated
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     */
    @Deprecated
    void afterMerge(EtalonRecord etalon, List<EtalonRecord> duplicates, ExecutionContext ctx);
    /**
     * The method is called after merge operation,
     * but after all the system 'after' merge handlers have finished their job.
     * The method is called outside of the saving transaction.
     * @param etalon the record, that remains active
     * @param duplicates the duplicates, that will be deactivated
     * @param ctx execution context local to this operation, which may be used to store and retrieve various information. Contains also global configuration.
     * @return exit result.
     */
    default ExitResult afterMergeWithResult(EtalonRecord etalon, List<EtalonRecord> duplicates, ExecutionContext ctx) {
        return new ExitResult();
    }
}
