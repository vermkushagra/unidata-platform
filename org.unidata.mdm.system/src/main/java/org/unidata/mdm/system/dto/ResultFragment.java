package org.unidata.mdm.system.dto;

/**
 * @author Mikhail Mikhailov on Oct 2, 2019
 */
public interface ResultFragment<R extends ResultFragment<R>> extends PipelineExecutionResult {
    /**
     * Gets fragment id of this context.
     * @return fragment id
     */
    ResultFragmentId<R> getFragmentId();
}
