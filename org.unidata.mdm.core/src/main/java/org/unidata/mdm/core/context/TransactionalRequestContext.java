package org.unidata.mdm.core.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.unidata.mdm.system.context.StorageCapableContext;
import org.unidata.mdm.system.context.StorageId;
/**
 * @author Mikhail Mikhailov
 * The _former_ finalize participant context, extracted to interface.
 */
public interface TransactionalRequestContext extends StorageCapableContext {
    /**
     * The transaction finalizers SID.
     */
    StorageId SID_TRANSACTION_FINALIZERS = new StorageId("TRANSACTION_FINALIZERS");
    /**
     * Executes upon transaction commit.
     * @param executor the executor to run
     */
    default void addFinalizeExecutor(Consumer<TransactionalRequestContext> executor) {
        List<Consumer<TransactionalRequestContext>> finalizeExecutors = getFromStorage(SID_TRANSACTION_FINALIZERS);
        if (finalizeExecutors == null) {
            finalizeExecutors = new ArrayList<>();
        }

        finalizeExecutors.add(executor);
    }
    /**
     * Gets the list of collected executors
     * @return list of executors
     */
    default List<Consumer<TransactionalRequestContext>> getFinalizeExecutors() {
        List<Consumer<TransactionalRequestContext>> finalizeExecutors = getFromStorage(SID_TRANSACTION_FINALIZERS);
        return Objects.isNull(finalizeExecutors) ? Collections.emptyList() : finalizeExecutors;
    }
}