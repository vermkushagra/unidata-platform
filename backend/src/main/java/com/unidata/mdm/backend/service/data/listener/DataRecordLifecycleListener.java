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
package com.unidata.mdm.backend.service.data.listener;

import com.unidata.mdm.backend.common.context.CommonRequestContext;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;

import java.util.Collections;
import java.util.List;

/**
 * @author Mikhail Mikhailov
 *         A life cycle listener for data record events/actions.
 */
public class DataRecordLifecycleListener<T extends CommonRequestContext> {
    /**
     * Before execution chain.
     */
    private List<DataRecordBeforeExecutor<T>> beforeExecutors = Collections.emptyList();
    /**
     * After execution chain.
     */
    private List<DataRecordAfterExecutor<T>> afterExecutors = Collections.emptyList();

    /**
     * Entry point for actions chain executed BEFORE persist.
     *
     * @param t instance of a concrete context type, extending {@link CommonRequestContext}
     * @return true if successful, false otherwise
     */
    public boolean before(T t) {
        MeasurementPoint.start();
        try {
            for (DataRecordBeforeExecutor<T> beforeExecutor : beforeExecutors) {
                if (!beforeExecutor.execute(t)) {
                    return false;
                }
            }
            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Entry point for actions chain executed AFTER persist.
     *
     * @param t instance of a concrete context type, extending {@link CommonRequestContext}
     * @return true if successful, false otherwise
     */
    public boolean after(T t) {
        MeasurementPoint.start();
        try {
            for (DataRecordAfterExecutor<T> afterExecutor : afterExecutors) {
                if (!afterExecutor.execute(t)) {
                    return false;
                }
            }
            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * @return the beforeExecutors
     */
    public List<DataRecordBeforeExecutor<T>> getBeforeExecutors() {
        return beforeExecutors;
    }

    /**
     * @param beforeExecutors the beforeExecutors to set
     */
    public void setBeforeExecutors(List<DataRecordBeforeExecutor<T>> beforeExecutors) {
        this.beforeExecutors = beforeExecutors;
    }

    /**
     * @return the afterExecutors
     */
    public List<DataRecordAfterExecutor<T>> getAfterExecutors() {
        return afterExecutors;
    }

    /**
     * @param afterExecutors the afterExecutors to set
     */
    public void setAfterExecutors(List<DataRecordAfterExecutor<T>> afterExecutors) {
        this.afterExecutors = afterExecutors;
    }
}
