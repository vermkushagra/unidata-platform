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
