package org.unidata.mdm.system.type.pipeline;

import org.unidata.mdm.system.dto.ExecutionResult;

/**
 * Boxing 'void' return method value for PipelineExecutionResult
 *
 * @author maria.chistyakova
 * @since  08.12.2019
 */
public class VoidPipelineOutput implements PipelineOutput, ExecutionResult {

    public static final VoidPipelineOutput INSTANCE = new VoidPipelineOutput();

    private VoidPipelineOutput(){}
}
