/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.unidata.mdm.system.type.pipeline;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.system.configuration.SystemConfigurationConstants;
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
     * Marks a pipeline as batched pipeline.
     */
    private final boolean batched;
    /**
     * Collected segments.
     */
    private final List<Segment> segments = new ArrayList<>();
    /**
     * Fallbacks.
     */
    private final List<Segment> fallbacks = new ArrayList<>();
    /**
     * Connected non-default specific pipelines.
     */
    private Map<Connector<? extends PipelineInput, ? extends PipelineOutput>, Pipeline> connected;
    /**
     * Just the indicator that .end has already been called and the PL is closed.
     */
    private boolean finished;
    /**
     * Constructor.
     * @param startId the start segment id.
     * @param subjectId the subject id. May be null/blank.
     * @param description the description.
     * @param batched the batched mark
     */
    private Pipeline(String startId, String subjectId, String description, boolean batched) {
        super();
        this.startId = startId;
        this.subjectId = subjectId;
        this.description = description;
        this.batched = batched;
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
     * Gets the pipeline fallback functions.
     * @return fallbacks
     */
    @SuppressWarnings("unchecked")
    public<C extends PipelineInput> List<Fallback<C>> getFallbacks() {
        return fallbacks.stream()
                .map(f -> (Fallback<C>) f)
                .collect(Collectors.toList());
    }
    /**
     * Gets the starting point.
     * @return the starting point
     */
    @SuppressWarnings("unchecked")
    public Start<PipelineInput> getStart() {
        return (Start<PipelineInput>) segments.get(0);
    }
    /**
     * Gets the finishing point.
     * @return the finishing point or null, if the pipeline is not finished yet
     */
    @SuppressWarnings("unchecked")
    public Finish<PipelineInput, PipelineOutput> getFinish() {
        return finished ? (Finish<PipelineInput, PipelineOutput>) segments.get(segments.size() - 1) : null;
    }

    /**
     * Gets an explicitly connected pipeline if any.
     * @param c a connector instance
     * @return pipeline instance or null
     */
    @Nullable
    public Pipeline getConnected(@Nonnull Connector<? extends PipelineInput, ? extends PipelineOutput> c) {
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
     * Returns batched state.
     * @return batched state
     */
    public boolean isBatched() {
        return batched;
    }
    /**
     * Adds a point to this pipeline.
     * @param p the point
     * @return self
     */
    public Pipeline with(@Nonnull Point<? extends PipelineInput> p) {
        Objects.requireNonNull(p, "Point segment is null");
        throwIfPipelineClosed();
        throwIfPipelineBatchedMismatch(p);
        segments.add(p);
        return this;
    }
    /**
     * Adds a connector with pipeline selection at runtime.
     * @param c the connector
     * @return self
     */
    public Pipeline with(@Nonnull Connector<? extends PipelineInput, ? extends PipelineOutput> c) {
        Objects.requireNonNull(c, "Connector segment is null");
        throwIfPipelineClosed();
        throwIfPipelineBatchedMismatch(c);
        segments.add(c);
        return this;
    }
    /**
     * Adds a connector with pipeline selection at runtime.
     * @param c the connector
     * @return self
     */
    public Pipeline with(@Nonnull Connector<? extends PipelineInput, ? extends PipelineOutput> c, @Nonnull Pipeline p) {

        Objects.requireNonNull(c, "Connector segment is null");
        Objects.requireNonNull(p, "Connecting pipeline is null");

        throwIfPipelineClosed();
        throwIfPipelineBatchedMismatch(c);

        segments.add(c);

        if (Objects.isNull(connected)) {
            connected = new IdentityHashMap<>();
        }

        connected.put(c, p);
        return this;
    }

    /**
     * Add a fallback function to pipeline.
     * @param fallback the fallback function
     * @return self
     */
    public Pipeline fallback(@Nonnull Fallback<? extends PipelineInput> fallback) {
        Objects.requireNonNull(fallback, "Fallback segment is null");
        throwIfPipelineBatchedMismatch(fallback);
        fallbacks.add(fallback);
        return this;
    }

    /**
     * Adds a connector with pipeline selection at runtime.
     * @param f the finish segment
     * @return self
     */
    public Pipeline end(@Nonnull Finish<? extends PipelineInput, ? extends PipelineOutput> f) {
        Objects.requireNonNull(f, "Finish segment is null");
        throwIfPipelineClosed();
        throwIfPipelineBatchedMismatch(f);
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
     * Throws if this PL is already closed.
     */
    private void throwIfPipelineBatchedMismatch(Segment s) {
        if (s.isBatched() != batched) {
            throw new PipelineException("Attempt to add a non batched segment to a batched pipeline or vice versa.",
                    SystemExceptionIds.EX_PIPELINE_BATCHED_MISMATCH);
        }
    }
    /**
     * Starts a pipeline from starting point with no subject.
     * Description will be taken from the starting point.
     * @param s the starting point
     * @return a pipeline instance
     */
    public static Pipeline start(@Nonnull Start<? extends PipelineInput> s) {
        return start(s, null, null);
    }
    /**
     * Starts a pipeline from starting point and subject.
     * Description will be taken from the starting point.
     * @param s the starting point
     * @param subjectId the subject id, on which this pipeline overrides the default one
     * @return a pipeline instance
     */
    public static Pipeline start(@Nonnull Start<? extends PipelineInput> s, String subjectId) {
        return start(s, subjectId, null);
    }
    /**
     * Starts a named pipeline from starting point, using subject and description.
     * @param s the starting point
     * @param subjectId the subject id, on which this pipeline overrides the default one
     * @param description the description
     * @return a pipeline instance
     */
    public static Pipeline start(@Nonnull Start<? extends PipelineInput> s, String subjectId, String description) {
        Objects.requireNonNull(s, "Start segment is null");
        Pipeline p = new Pipeline(
                s.getId(),
                StringUtils.isBlank(subjectId) ? SystemConfigurationConstants.NON_SUBJECT : subjectId,
                StringUtils.isBlank(description) ? s.getDescription() : description,
                s.isBatched());
        p.segments.add(s);
        return p;
    }
}
