package org.unidata.mdm.system.type.pipeline;

import org.unidata.mdm.system.dto.PipelineExecutionResult;

/**
 * @author Mikhail Mikhailov on Oct 2, 2019
 */
public final class PipelineConstants {
    /**
     * Special result of type void.
     */
    public static final PipelineExecutionResult VOID_EXECUTION_RESULT = new PipelineExecutionResult(){};
    /**
     * Constructor.
     */
    private PipelineConstants() {
        super();
    }
}
