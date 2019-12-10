package org.unidata.mdm.data.type.apply.batch;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import org.unidata.mdm.system.context.CommonRequestContext;
import org.unidata.mdm.system.type.pipeline.Pipeline;

/**
 * @author Mikhail Mikhailov
 * Simple batch set accumulator.
 * If accumulator is used in a job for processing and modifying of multiversion record sets,
 * it *must* exist during the lifetime of the step, since it holds id cache for multiversion batches.
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
     * @return iterator
     */
    BatchIterator<T> iterator();
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
     * stop processing, if exception was occurred.
     * @return flag 'abourtOnFailure'
     */
    boolean isAbortOnFailure();
    /**
     * Gets a pipeline, possibly supplied for this batch set.
     * @return pipeline or null
     */
    @Nullable
    Pipeline pipeline();
}
