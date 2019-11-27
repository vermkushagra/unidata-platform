package org.unidata.mdm.system.type.pipeline;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.system.configuration.SystemConfigurationConstants;
import org.unidata.mdm.system.context.PipelineExecutionContext;
import org.unidata.mdm.system.dto.PipelineExecutionResult;
import org.unidata.mdm.system.exception.PipelineException;
import org.unidata.mdm.system.exception.SystemExceptionIds;

/**
 * @author Mikhail Mikhailov
 * Pipeline instance object.
 */
public final class Pipeline {
    /**
     * The start segment id.
     */
    private final String startId;
    /**
     * The subject id. May be null/empty string.
     */
    private final String subjectId;
    /**
     * The description, either supplied or generated.
     */
    private final String description;
    /**
     * Collected segments.
     */
    private final List<Segment> segments = new ArrayList<>();
    /**
     * Connected non-default specific pipelines.
     */
    private Map<Connector<? extends PipelineExecutionContext, ? extends PipelineExecutionResult>, Pipeline> connected;
    /**
     * Just the indicator that .end has already been called and the PL is closed.
     */
    private boolean finished;
    /**
     * Constructor.
     * @param startId the start segment id.
     * @param subjectId the subject id. May be null/blank.
     * @param description the description.
     */
    private Pipeline(String startId, String subjectId, String description) {
        super();
        this.startId = startId;
        this.subjectId = subjectId;
        this.description = description;
    }
    /**
     * Gets pipeline ID. Must be unique accross the system.
     * @return ID
     */
    public String getStartId() {
        return startId;
    }
    /**
     * @return the subjectId
     */
    public String getSubjectId() {
        return subjectId;
    }
    /**
     * Gets type description.
     * @return description
     */
    public String getDescription() {
        return description;
    }
    /**
     * Gets the participating segments collection.
     * @return segments
     */
    public List<Segment> getSegments() {
        return segments;
    }
    /**
     * Gets the starting point.
     * @return the starting point
     */
    @SuppressWarnings("unchecked")
    public Start<PipelineExecutionContext> getStart() {
        return (Start<PipelineExecutionContext>) segments.get(0);
    }
    /**
     * Gets the finishing point.
     * @return the finishing point or null, if the pipeline is not finished yet
     */
    @SuppressWarnings("unchecked")
    public Finish<PipelineExecutionContext, PipelineExecutionResult> getFinish() {
        return finished ? (Finish<PipelineExecutionContext, PipelineExecutionResult>) segments.get(segments.size() - 1) : null;
    }

    /**
     * Gets an explicitly connected pipeline if any.
     * @param c a connector instance
     * @return pipeline instance or null
     */
    @Nullable
    public Pipeline getConnected(@Nonnull Connector<? extends PipelineExecutionContext, ? extends PipelineExecutionResult> c) {
        if (Objects.isNull(connected)) {
            return null;
        }

        return connected.get(c);
    }
    /**
     * Returns finished state.
     * @return finished state
     */
    public boolean isFinished() {
        return finished;
    }
    /**
     * Adds a point to this pipeline.
     * @param p the point
     * @return self
     */
    public Pipeline with(@Nonnull Point<? extends PipelineExecutionContext> p) {
        Objects.requireNonNull(p, "Point segment is null");
        throwIfPipelineClosed();
        segments.add(p);
        return this;
    }
    /**
     * Adds a connector with pipeline selection at runtime.
     * @param c the connector
     * @return self
     */
    public Pipeline with(@Nonnull Connector<? extends PipelineExecutionContext, ? extends PipelineExecutionResult> c) {
        Objects.requireNonNull(c, "Connector segment is null");
        throwIfPipelineClosed();
        segments.add(c);
        return this;
    }
    /**
     * Adds a connector with pipeline selection at runtime.
     * @param c the connector
     * @return self
     */
    public Pipeline with(@Nonnull Connector<? extends PipelineExecutionContext, ? extends PipelineExecutionResult> c, @Nonnull Pipeline p) {

        Objects.requireNonNull(c, "Connector segment is null");
        Objects.requireNonNull(p, "Connecting pipeline is null");

        throwIfPipelineClosed();
        segments.add(c);

        if (Objects.isNull(connected)) {
            connected = new IdentityHashMap<>();
        }

        connected.put(c, p);
        return this;
    }
    /**
     * Adds a connector with pipeline selection at runtime.
     * @param f the finish segment
     * @return self
     */
    public Pipeline end(@Nonnull Finish<? extends PipelineExecutionContext, ? extends PipelineExecutionResult> f) {
        Objects.requireNonNull(f, "Finish segment is null");
        throwIfPipelineClosed();
        segments.add(f);
        finished = true;
        return this;
    }
    /**
     * Throws if this PL is already closed.
     */
    private void throwIfPipelineClosed() {
        if (finished) {
            throw new PipelineException("This pipeline is already finished.", SystemExceptionIds.EX_PIPELINE_ALREADY_FINISHED);
        }
    }
    /**
     * Starts a pipeline from starting point with no subject.
     * Description will be taken from the starting point.
     * @param s the starting point
     * @return a pipeline instance
     */
    public static Pipeline start(@Nonnull Start<? extends PipelineExecutionContext> s) {
        return start(s, null, null);
    }
    /**
     * Starts a pipeline from starting point and subject.
     * Description will be taken from the starting point.
     * @param s the starting point
     * @param subjectId the subject id, on which this pipeline overrides the default one
     * @return a pipeline instance
     */
    public static Pipeline start(@Nonnull Start<? extends PipelineExecutionContext> s, String subjectId) {
        return start(s, subjectId, null);
    }
    /**
     * Starts a named pipeline from starting point, using subject and description.
     * @param s the starting point
     * @param subjectId the subject id, on which this pipeline overrides the default one
     * @param description the description
     * @return a pipeline instance
     */
    public static Pipeline start(@Nonnull Start<? extends PipelineExecutionContext> s, String subjectId, String description) {
        Objects.requireNonNull(s, "Start segment is null");
        Pipeline p = new Pipeline(
                s.getId(),
                StringUtils.isBlank(subjectId) ? SystemConfigurationConstants.NON_SUBJECT : subjectId,
                StringUtils.isBlank(description) ? s.getDescription() : description);
        p.segments.add(s);
        return p;
    }
}
