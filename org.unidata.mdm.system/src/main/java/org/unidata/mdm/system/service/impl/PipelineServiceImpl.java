package org.unidata.mdm.system.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unidata.mdm.system.context.PipelineExecutionContext;
import org.unidata.mdm.system.dto.PipelineExecutionResult;
import org.unidata.mdm.system.service.ModuleService;
import org.unidata.mdm.system.service.PipelineService;
import org.unidata.mdm.system.type.module.Module;
import org.unidata.mdm.system.type.pipeline.Connector;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Pipeline;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Segment;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * {@link PipelineService} implementation.
 * @author Mikhail Mikhailov on Nov 1, 2019
 */
@Service("pipelineService")
public class PipelineServiceImpl implements PipelineService {
    /**
     * Registered unique segments.
     */
    private final Map<Class<?>, Segment> segments = new IdentityHashMap<>();

    private final Map<String, Start<? extends PipelineExecutionContext>> start = new HashMap<>();

    private final Map<String, Point<? extends PipelineExecutionContext>> point = new HashMap<>();

    private final Map<String, Connector<? extends PipelineExecutionContext, ? extends PipelineExecutionResult>> connector = new HashMap<>();

    private final Map<String, Finish<? extends PipelineExecutionContext, ? extends PipelineExecutionResult>> finish = new HashMap<>();
    /**
     * Module registry.
     */
    @Autowired
    private ModuleService moduleService;
    /**
     * Constructor.
     */
    public PipelineServiceImpl() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPlatformStartup() {

        for (Module m : moduleService.getModules()) {

            Collection<Start<PipelineExecutionContext>> startSegments = m.getStartTypes();
            if (CollectionUtils.isNotEmpty(startSegments)) {
                segments.putAll(startSegments.stream().collect(Collectors.toMap(Object::getClass, Function.identity())));
                start.putAll(startSegments.stream().collect(Collectors.toMap(Segment::getId, Function.identity())));
            }

            Collection<Point<PipelineExecutionContext>> pointSegments = m.getPointTypes();
            if (CollectionUtils.isNotEmpty(pointSegments)) {
                segments.putAll(pointSegments.stream().collect(Collectors.toMap(Object::getClass, Function.identity())));
                point.putAll(pointSegments.stream().collect(Collectors.toMap(Segment::getId, Function.identity())));
            }

            Collection<Connector<PipelineExecutionContext, PipelineExecutionResult>> connectorSegments = m.getConnectorTypes();
            if (CollectionUtils.isNotEmpty(connectorSegments)) {
                segments.putAll(connectorSegments.stream().collect(Collectors.toMap(Object::getClass, Function.identity())));
                connector.putAll(connectorSegments.stream().collect(Collectors.toMap(Segment::getId, Function.identity())));
            }

            Collection<Finish<PipelineExecutionContext, PipelineExecutionResult>> finishSegments = m.getFinishTypes();
            if (CollectionUtils.isNotEmpty(finishSegments)) {
                segments.putAll(finishSegments.stream().collect(Collectors.toMap(Object::getClass, Function.identity())));
                finish.putAll(finishSegments.stream().collect(Collectors.toMap(Segment::getId, Function.identity())));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pipeline getPipeline(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void savePipeline(String id, String subject, Pipeline pipeline) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends PipelineExecutionContext> Start<C> start(Class<? extends Start<C>> klass) {
        return (Start<C>) segments.get(klass);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends PipelineExecutionContext> Point<C> point(Class<? extends Point<C>> klass) {
        return (Point<C>) segments.get(klass);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends PipelineExecutionContext, R extends PipelineExecutionResult> Connector<C, R> connector(Class<? extends Connector<C, R>> klass) {
        return (Connector<C, R>) segments.get(klass);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends PipelineExecutionContext, R extends PipelineExecutionResult> Finish<C, R> finish(Class<? extends Finish<C, R>> klass) {
        return (Finish<C, R>) segments.get(klass);
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends PipelineExecutionContext> Start<C> start(String id) {
        return (Start<C>) start.get(id);
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends PipelineExecutionContext> Point<C> point(String id) {
        return (Point<C>) point.get(id);
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends PipelineExecutionContext, R extends PipelineExecutionResult> Connector<C, R> connector(
            String id) {
        return (Connector<C, R>) connector.get(id);
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends PipelineExecutionContext, R extends PipelineExecutionResult> Finish<C, R> finish(String id) {
        return (Finish<C, R>) finish.get(id);
    }

}
