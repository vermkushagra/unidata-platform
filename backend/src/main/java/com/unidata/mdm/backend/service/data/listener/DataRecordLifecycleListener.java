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
