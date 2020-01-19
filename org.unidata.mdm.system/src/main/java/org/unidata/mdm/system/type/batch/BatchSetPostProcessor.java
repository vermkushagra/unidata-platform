package org.unidata.mdm.system.type.batch;

import javax.annotation.Nullable;

import org.unidata.mdm.system.context.CommonRequestContext;
import org.unidata.mdm.system.dto.ExecutionResult;
import org.unidata.mdm.system.type.pipeline.ConnectedPipeline;

/**
 * @author Mikhail Mikhailov on Jan 16, 2020
 */
public interface BatchSetPostProcessor<I extends CommonRequestContext, O extends ExecutionResult, S extends BatchSetAccumulator<I, O>> {
    /**
     * Returns a connector, which can be attached to batch pipeline.
     * @return connector or null
     */
    @Nullable
    default ConnectedPipeline connect() {
        return null;
    }
    /**
     * This is called after 'bsa' was charged with new portion of data.
     * @param bsa the batch set
     */
    void accumulate(S bsa);
    /**
     * If the are several post processors for the same job, the order of execution will be ascending order according to the value,
     * returned from this method.
     * @return
     */
    int order();
}
