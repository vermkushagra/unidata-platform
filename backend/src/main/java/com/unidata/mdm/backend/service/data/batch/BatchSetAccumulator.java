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
}
