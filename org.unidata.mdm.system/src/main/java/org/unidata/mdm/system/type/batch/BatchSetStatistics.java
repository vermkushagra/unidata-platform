package org.unidata.mdm.system.type.batch;

import java.util.List;

import org.unidata.mdm.system.type.pipeline.PipelineOutput;

/**
 * Batch statistics base.
 * @author Mikhail Mikhailov on Dec 13, 2019
 */
public interface BatchSetStatistics<T extends PipelineOutput> {
    /**
     * Clear state.
     */
    void reset();
    /**
     * Returns the result collecting state.
     * @return true, if currently set to collect results
     */
    boolean collectResults();
    /**
     * Sets this statistic collector to collect (or not) execution results.
     * @param collectOutput the state to set
     */
    void collectResults(boolean collectOutput);
    /**
     * Gets the execution results, if they are set to be collected.
     * @return results or empty list
     */
    List<T> getResults();
}
