package org.unidata.mdm.system.context;

/**
 * @author Mikhail Mikhailov
 * Interface for contexts, that participate in a pipeline.
 */
public interface PipelineExecutionContext {
    /**
     * Gets the pipeline type this context works with.
     * @return type
     */
    String getStartTypeId();
}
