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

package com.unidata.mdm.backend.service.data.batch;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.context.CommonRequestContext;

/**
 * @author Mikhail Mikhailov
 * Simple batch set accumulator.
 */
public interface BatchSetAccumulator<T extends CommonRequestContext> {
    /**
     * Charge with new block.
     * @param charge the payload
     */
    void charge(Collection<T> charge);
    /**
     * Clear state.
     */
    void discharge();
    /**
     * Get iterator of the underlaying working copy.
     * @param iterationType type of iteration
     * @return iterator
     */
    BatchIterator<T> iterator(BatchSetIterationType iterationType);
    /**
     * Get iteration types, this accumulator can support.
     * @return types
     */
    Collection<BatchSetIterationType> getSupportedIterationTypes();
    /**
     * Gets the working copy.
     * @return list
     */
    List<T> workingCopy();
    /**
     * Gets the size of the batch set.
     * @return {@link BatchSetSize}
     */
    BatchSetSize getBatchSetSize();
    /**
     * Gets the batch target tables.
     * @return targets
     */
    Map<BatchTarget, String> getTargets();

    /**
     * stop processing, if exception was occurred.
     * @return flag 'abourtOnFailure'
     */
    boolean isAbortOnFailure();
}
