/**
 *
 */

package com.unidata.mdm.backend.service.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

/**
 * {@code CommitCallbackExecutor} extends {@code TransactionSynchronizationAdapter} to
 * invoke transaction synchronization callbacks when an existed transaction was committed
 * successfully.
 * <p>
 * {@code TransactionSynchronizationAdapter} contains empty methods implementation of
 * {@code TransactionSynchronization} interface for easier overriding of single methods
 * which processes succeeded transaction commits.
 * <p>
 * Also it implements {@code Executor} interface to execute submitted {@code Runnable} tasks
 * and avoid of explicitly creating threads.
 *
 * @author amagdenko
 */
@Component
public class NotificationAsyncCommitCallbackExecutor extends TransactionSynchronizationAdapter implements Executor {

    // saves {@code Runnable} instances associated with execution threads; all saved
    // commands should be executed when transaction is successfully committed
    private static final ThreadLocal<List<Runnable>> SUBMITTED_COMMANDS = new ThreadLocal<>();

    @Override
    public final void execute(final Runnable command) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            // runs a command immediately, when synchronization of transaction isn't active
            command.run();
        } else {
            // saves submitted command in ThreadLocal variable
            List<Runnable> commands = SUBMITTED_COMMANDS.get();
            if (CollectionUtils.isEmpty(commands)) {
                // when we submit a command at the first time by the current thread, then we
                // register for transaction synchronization for the current thread
                commands = new ArrayList<>();
                SUBMITTED_COMMANDS.set(commands);
                TransactionSynchronizationManager.registerSynchronization(this);
            }
            commands.add(command);
        }
    }

    @Override
    public final void afterCommit() {
        // runs all submitted commands after the current transaction was committed successfully
        final List<Runnable> commands = SUBMITTED_COMMANDS.get();
        commands.stream().forEach(Runnable::run);
    }

    @Override
    public final void afterCompletion(final int status) {
        // cleans up ThreadLocal variable for thread that just completed a transaction
        SUBMITTED_COMMANDS.remove();
    }
}
