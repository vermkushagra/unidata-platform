/**
 *
 */
package com.unidata.mdm.backend.service.data.etalon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * @author Mikhail Mikhailov
 * Runs interval calculations transaction aware.
 */
@Component("etalonIntervalCalculationExecutor")
public class EtalonIntervalCalculationExecutor implements TaskExecutor {

    /**
     * 'Upsert' record pooling executor.
     */
    public static final String UPSERT_RECORD_POOLING_EXECUTOR_QUALIFIER = "etalonsCalculationPoolingExecutor";

    /**
     * 'Upsert' pooling executor.
     */
    @Autowired
    @Qualifier(value = UPSERT_RECORD_POOLING_EXECUTOR_QUALIFIER)
    private ThreadPoolTaskExecutor etalonCalculationExecuter;

    /**
     * Constructor.
     */
    public EtalonIntervalCalculationExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Runnable task) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()
          || TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            etalonCalculationExecuter.execute(task);
        } else {
            task.run();
        }
    }
}
