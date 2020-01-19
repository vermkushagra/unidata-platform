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

package org.unidata.mdm.core.util;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.core.Ordered;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.unidata.mdm.core.context.TransactionalRequestContext;

/**
 * @author Dmitry Kopin on 06.05.2019.
 */
public class TransactionUtils {

    private static ThreadLocal<Integer> txActionCount = ThreadLocal.withInitial(() -> 0);
    /**
     * Constructor.
     */
    private TransactionUtils() {
        super();
    }
    /**
     *
     * @param r
     */
    public static void executeAfterCommitAction(final Runnable r) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // Order is necessary to execute tx syncs in their add order.
            final int order = txActionCount.get();
            txActionCount.set(order < Ordered.LOWEST_PRECEDENCE ? order + 1 : Ordered.LOWEST_PRECEDENCE);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override public void afterCommit() {
                    r.run();
                }

                @Override public int getOrder() {
                    return order;
                }
            });
        } else {
            r.run();
        }
    }

    public static void executeAfterCommitAction(final List<Consumer<TransactionalRequestContext>> finalizers, TransactionalRequestContext ctx) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // Order is necessary to execute tx syncs in their add order.
            final int order = txActionCount.get();
            txActionCount.set(order < Ordered.LOWEST_PRECEDENCE ? order + 1 : Ordered.LOWEST_PRECEDENCE);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override public void afterCommit() {
                    finalizers.forEach(finalizer -> finalizer.accept(ctx));
                }

                @Override public int getOrder() {
                    return order;
                }
            });
        } else {
            finalizers.forEach(finalizer -> finalizer.accept(ctx));
        }
    }

    /**
     *
     * @param r
     */
    public static void executeAfterRollbackAction(final Runnable r) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // Order is necessary to execute tx syncs in their add order.
            final int order = txActionCount.get();
            txActionCount.set(order < Ordered.LOWEST_PRECEDENCE ? order + 1 : Ordered.LOWEST_PRECEDENCE);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCompletion(int status) {
                    if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                        r.run();
                    }
                }

                @Override public int getOrder() {
                    return order;
                }
            });
        } else {
            r.run();
        }
    }
}
