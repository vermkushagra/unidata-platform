package org.unidata.mdm.system.service;

import org.unidata.mdm.system.context.PipelineExecutionContext;
import org.unidata.mdm.system.dto.PipelineExecutionResult;
import org.unidata.mdm.system.type.pipeline.Pipeline;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov on Oct 1, 2019
 * Pipeline execution service.
 */
public interface ExecutionService {
    /**
     * Calls {@link Start#init(PipelineExecutionContext)}
     * to select a pre-configured pipeline for the context and than
     * calls the variant below.
     *
     * @param <C> the type of the request
     * @param <R> the type of the result
     * @param ctx the request context
     * @return result
     */
    <C extends PipelineExecutionContext, R extends PipelineExecutionResult> R execute(C ctx);
    /**
     * Calls the pipeline 'p' for the supplied context 'c'.
     *
     * @param <C> the type of the request
     * @param <R> the type of the result
     * @param p a ready to execute pipeline instance
     * @param ctx the context
     * @return result
     */
    <C extends PipelineExecutionContext, R extends PipelineExecutionResult> R execute(Pipeline p, C ctx);
}
