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