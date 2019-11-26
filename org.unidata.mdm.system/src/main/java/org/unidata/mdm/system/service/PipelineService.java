package org.unidata.mdm.system.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.unidata.mdm.system.context.PipelineExecutionContext;
import org.unidata.mdm.system.dto.PipelineExecutionResult;
import org.unidata.mdm.system.type.pipeline.Connector;
import org.unidata.mdm.system.type.pipeline.Fallback;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Pipeline;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;

import java.util.function.BiConsumer;

/**
 * The service, responsible for pipeline management.
 * @author Mikhail Mikhailov on Nov 1, 2019
 */
public interface PipelineService extends AfterPlatformStartup {
    /**
     * Loads a previously saved {@link Pipeline}, associated with the given id.
     * @param id the pipeline id
     * @return a pipeline instance or null, if nothing found
     */
    @Nullable
    Pipeline getPipeline(String id);
    /**
     * Saves and caches given pipeline, associated with the given id.
     * @param id the id
     * @param subject the subject this pipeline should be run for
     * @param pipeline the pipeline to save
     */
    void savePipeline(@Nonnull String id, @Nullable String subject, @Nonnull Pipeline pipeline);

    <C extends PipelineExecutionContext> Start<C> start(Class<? extends Start<C>> klass);

    <C extends PipelineExecutionContext> Point<C> point(Class<? extends Point<C>> klass);

    <C extends PipelineExecutionContext, R extends PipelineExecutionResult> Connector<C, R> connector(Class<? extends Connector<C, R>> klass);

    <C extends PipelineExecutionContext, R extends PipelineExecutionResult> Finish<C, R> finish(Class<? extends Finish<C, R>> klass);

    <C extends PipelineExecutionContext> Start<C> start(String id);

    <C extends PipelineExecutionContext> Point<C> point(String id);

    <C extends PipelineExecutionContext, R extends PipelineExecutionResult> Connector<C, R> connector(String id);

    Fallback fallback(Class<?> clazz);

    <C extends PipelineExecutionContext, R extends PipelineExecutionResult> Finish<C, R> finish(String id);
}
