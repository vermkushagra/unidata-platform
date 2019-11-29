package org.unidata.mdm.system.type.module;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.unidata.mdm.system.context.PipelineExecutionContext;
import org.unidata.mdm.system.dto.PipelineExecutionResult;
import org.unidata.mdm.system.type.pipeline.Connector;
import org.unidata.mdm.system.type.pipeline.Fallback;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Segment;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov on Nov 27, 2019
 */
public abstract class AbstractModule implements Module {
    /**
     * Local start segments.
     */
    protected final Map<String, Start<PipelineExecutionContext>> starts = new HashMap<>();
    /**
     * Local point segments.
     */
    protected final Map<String, Point<PipelineExecutionContext>> points = new HashMap<>();
    /**
     * Local connector segments.
     */
    protected final Map<String, Connector<PipelineExecutionContext, PipelineExecutionResult>> connectors = new HashMap<>();
    /**
     * Local finish segments.
     */
    protected final Map<String, Finish<PipelineExecutionContext, PipelineExecutionResult>> finishes = new HashMap<>();
    /**
     * Local fallback segments.
     */
    protected final Map<String, Fallback<PipelineExecutionContext>> fallbacks = new HashMap<>();
    /**
     * Adds collection of segments to this module.
     * @param segments the collectin to add
     */
    @SuppressWarnings("unchecked")
    protected void addSegments(Collection<Segment> segments) {

        if (CollectionUtils.isEmpty(segments)) {
            return;
        }

        for (Segment s : segments) {
            switch (s.getType()) {
            case START:
                starts.put(s.getId(), (Start<PipelineExecutionContext>) s);
                break;
            case POINT:
                points.put(s.getId(), (Point<PipelineExecutionContext>) s);
                break;
            case CONNECTOR:
                connectors.put(s.getId(), (Connector<PipelineExecutionContext, PipelineExecutionResult>) s);
                break;
            case FALLBACK:
                fallbacks.put(s.getId(), (Fallback<PipelineExecutionContext>) s);
                break;
            case FINISH:
                finishes.put(s.getId(), (Finish<PipelineExecutionContext, PipelineExecutionResult>) s);
                break;
            default:
                break;
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Start<PipelineExecutionContext>> getStartTypes() {
        return starts.values();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Finish<PipelineExecutionContext, PipelineExecutionResult>> getFinishTypes() {
        return finishes.values();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Point<PipelineExecutionContext>> getPointTypes() {
        return points.values();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Fallback<PipelineExecutionContext>> getFallbacks() {
        return fallbacks.values();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Connector<PipelineExecutionContext, PipelineExecutionResult>> getConnectorTypes() {
        return connectors.values();
    }
}
