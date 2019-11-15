package org.unidata.mdm.system.context;

import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov
 * Interface for contexts, that participate in a pipeline.
 */
public interface PipelineExecutionContext {
    /**
     * Gets the pipeline type this context works with.
     * @return type
     */
    Start<PipelineExecutionContext> getStartType();
    // TODO Allow execution stop with exit status!
}
