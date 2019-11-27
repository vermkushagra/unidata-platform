package org.unidata.mdm.system.service.impl;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unidata.mdm.system.context.PipelineExecutionContext;
import org.unidata.mdm.system.dto.CompositeResult;
import org.unidata.mdm.system.dto.PipelineExecutionResult;
import org.unidata.mdm.system.dto.ResultFragment;
import org.unidata.mdm.system.dto.ResultFragmentId;
import org.unidata.mdm.system.exception.PipelineException;
import org.unidata.mdm.system.exception.SystemExceptionIds;
import org.unidata.mdm.system.service.ExecutionService;
import org.unidata.mdm.system.service.PipelineService;
import org.unidata.mdm.system.type.pipeline.Connector;
import org.unidata.mdm.system.type.pipeline.Fallback;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Pipeline;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Segment;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov on Oct 1, 2019
 */
@Service
public class ExecutionServiceImpl implements ExecutionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionServiceImpl.class);
    /**
     * Pipeline servivce.
     */
    @Autowired
    private PipelineService pipelineService;
    /**
     * Constructor.
     */
    public ExecutionServiceImpl() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public <C extends PipelineExecutionContext, R extends PipelineExecutionResult> R execute(C ctx) {

        if (Objects.isNull(ctx)) {
            return null;
        }

        String id = ctx.getStartTypeId();
        Start<C> s = pipelineService.start(id);
        if (Objects.isNull(s)) {
            throw new PipelineException("Pipeline start type [{}] not found.",
                    SystemExceptionIds.EX_PIPELINE_START_TYPE_NOT_FOUND,
                    id);
        }

        String subject = s.subject(ctx);

        Pipeline p = null;
        if (Objects.nonNull(subject)) {
            p = pipelineService.getByIdAndSubject(id, subject);
        }

        if (Objects.isNull(p)) {
            p = pipelineService.getById(id);
        }

        if (Objects.isNull(p)) {
            throw new PipelineException("Pipeline not found by id [{}], subject [{}].",
                    SystemExceptionIds.EX_PIPELINE_NOT_FOUND_BY_ID_AND_SUBJECT,
                    id, subject);
        }

        return execute(p, ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <C extends PipelineExecutionContext, R extends PipelineExecutionResult> R execute(Pipeline p, C ctx) {

        if (Objects.isNull(p) || Objects.isNull(ctx)) {
            return null;
        }

        if (!p.isFinished()) {
            throw new PipelineException("Cannot execute the pipeline, the pipeline is not finished yet.",
                    SystemExceptionIds.EX_PIPELINE_IS_NOT_FINISHED);
        }

        R result = null;
        Map<ResultFragmentId<?>, ResultFragment<?>> collected = null;
        boolean resultIsComposite = CompositeResult.class.isAssignableFrom(p.getFinish().getOutputTypeClass());
        final List<Fallback<C>> fallbacks = new ArrayList<>(p.getFallbacks());
        for (int i = 0; i < p.getSegments().size(); i++) {

            Segment s = p.getSegments().get(i);
            try {
                switch (s.getType()) {
                    case START:
                        execStart(s, ctx);
                        break;
                    case POINT:
                        execPoint(s, ctx);
                        break;
                    case CONNECTOR:

                        PipelineExecutionResult intermediate = execConnector(s, ctx, p);

                        // The pipeline is not supposed to return composite result
                        // or returned is null. Break.
                        if (!resultIsComposite || intermediate == null || !ResultFragment.class.isAssignableFrom(intermediate.getClass())) {
                            break;
                        }

                        if (Objects.isNull(collected)) {
                            collected = new IdentityHashMap<>();
                        }

                        ResultFragment<?> fragment = (ResultFragment<?>) intermediate;
                        collected.put(fragment.getFragmentId(), fragment);
                        break;
                    case FINISH:
                        result = execFinish(s, ctx);
                        break;
                    default:
                        break;
                }
            }
            catch (Exception e) {
                executeFallbacks(ctx, e, fallbacks);
                throw new PipelineException("Execution of the pipeline [{} / {}] failed.", e,
                        SystemExceptionIds.EX_PIPELINE_EXECUTION_FAILED, p.getStartId(), p.getSubjectId());
            }
        }

        if (resultIsComposite && collected != null && !collected.isEmpty()) {
            CompositeResult composite = (CompositeResult) result;
            collected.values().forEach(composite::fragment);
        }

        return result;
    }

    /**
     * Executes start segment.
     * @param <C> the context type
     * @param s the start segment
     * @param ctx the context
     */
    @SuppressWarnings("unchecked")
    private <C extends PipelineExecutionContext> void execStart(Segment s, C ctx) {
        ((Start<C>) s).start(ctx);
    }
    /**
     * Executes point segment.
     * @param <C> the context type
     * @param s the point segment
     * @param ctx the context
     */
    @SuppressWarnings("unchecked")
    private <C extends PipelineExecutionContext> void execPoint(Segment s, C ctx) {
        ((Point<C>) s).point(ctx);
    }
    /**
     * Executes connector segment.
     * @param <C> the context type
     * @param s the connector segment
     * @param ctx the context
     * @param p the pipeline, being executed
     * @return result fragment or null
     */
    @SuppressWarnings("unchecked")
    private <C extends PipelineExecutionContext> PipelineExecutionResult execConnector(Segment s, C ctx, Pipeline p) {

        Connector<C, ? extends PipelineExecutionResult> c = (Connector<C, ? extends PipelineExecutionResult>) s;

        Pipeline connected = p.getConnected(c);
        if (Objects.isNull(connected)) {
            return c.connect(ctx);
        } else {
            return c.connect(ctx, connected);
        }
    }
    /**
     * Executes finishing segment.
     * @param <C> the input type
     * @param <R> the output type
     * @param s the finish segment
     * @param ctx the context
     * @return result or null
     */
    @SuppressWarnings("unchecked")
    private <C extends PipelineExecutionContext, R extends PipelineExecutionResult> R execFinish(Segment s, C ctx) {
        return ((Finish<C, R>) s).finish(ctx);
    }

    private<C extends PipelineExecutionContext> void executeFallbacks(
            final C c,
            final Throwable t,
            final List<Fallback<C>> fallbacks
    ) {
        fallbacks.forEach(fb -> {
            try {
                fb.accept(c, t);
            }
            catch (Exception e) {
                LOGGER.error("Error while executin fallback", e);
            }
        });
    }
}
