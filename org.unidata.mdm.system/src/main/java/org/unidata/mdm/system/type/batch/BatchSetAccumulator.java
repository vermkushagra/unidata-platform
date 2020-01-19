package org.unidata.mdm.system.type.batch;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import org.unidata.mdm.system.context.CommonRequestContext;
import org.unidata.mdm.system.dto.ExecutionResult;
import org.unidata.mdm.system.type.pipeline.Pipeline;
import org.unidata.mdm.system.type.pipeline.batch.BatchedPipelineInput;

/**
 * @author Mikhail Mikhailov
 * Simple batch set accumulator.
 * If accumulator is used in a job for processing and modifying of multiversion record sets,
 * it *must* exist during the lifetime of the step, since it holds id cache for multiversion batches.
 */
public interface BatchSetAccumulator<I extends CommonRequestContext, O extends ExecutionResult> extends BatchedPipelineInput {
    /**
     * Charge with new block.
     * @param charge the payload
     */
    void charge(Collection<I> charge);
    /**
     * Clear state.
     */
    void discharge();
    /**
     * Get iterator of the underlaying working copy.
     * @return iterator
     */
    BatchIterator<I> iterator();
    /**
     * Gets the working copy.
     * @return list
     */
    List<I> workingCopy();
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
     * This pipeline is intended for execution on input objects, not on the {@link BatchSetAccumulator}.
     * @return pipeline or null
     */
    @Nullable
    Pipeline pipeline();
    /**
     * Returns statistics for this run.
     * Statistics exist until {@link #discharge()} is called.
     * @param <S> the exact statistic type
     * @return statistics
     */
    @Nullable
    <S extends BatchSetStatistics<O>> S statistics();
}
