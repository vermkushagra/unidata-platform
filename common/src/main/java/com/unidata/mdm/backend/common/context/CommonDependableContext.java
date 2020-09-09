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

package com.unidata.mdm.backend.common.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public abstract class CommonDependableContext extends CommonSendableContext implements RecordIdentityContext {

    List<Function<CommonDependableContext, Boolean>> finalizeExecutors = null;
    @SuppressWarnings("unchecked")
    public CommonDependableContext(final CommonDependableContext parentContext) {
        if (parentContext != null) {
            final List<CommonSendableContext> contextsList = parentContext.getFromStorage(StorageId.DEPENDED_CONTEXTS);
            if (contextsList == null) {
                parentContext.putToStorage(StorageId.DEPENDED_CONTEXTS, new ArrayList<>());
            }
            ((List<CommonSendableContext>) parentContext.getFromStorage(StorageId.DEPENDED_CONTEXTS)).add(this);
        }
    }

    public void addFinalizeExecutor (Function<CommonDependableContext, Boolean> executor) {
        if (finalizeExecutors == null) {
            finalizeExecutors = new ArrayList<>();
        }
        finalizeExecutors.add(executor);
    }

    public List<Function<CommonDependableContext, Boolean>> getFinalizeExecutors() {
        return finalizeExecutors == null ? Collections.emptyList() : finalizeExecutors;
    }

}
