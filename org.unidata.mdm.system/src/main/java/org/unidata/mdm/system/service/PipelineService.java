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

package org.unidata.mdm.system.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.unidata.mdm.system.type.pipeline.Connector;
import org.unidata.mdm.system.type.pipeline.Fallback;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Pipeline;
import org.unidata.mdm.system.type.pipeline.PipelineInput;
import org.unidata.mdm.system.type.pipeline.PipelineOutput;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Segment;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * The service, responsible for pipeline management.
 * @author Mikhail Mikhailov on Nov 1, 2019
 */
public interface PipelineService {
    /**
     * Gets all pipelines, known to the service.
     * @return collection of pipelines
     */
    Collection<Pipeline> getAll();
    /**
     * Gets all pipelines, known to the service.
     * @return collection of pipelines
     */
    Map<String, Collection<Pipeline>> getAllWithSubjects();
    /**
     * Loads a cached or previously saved {@link Pipeline}, associated with the given id and having NO subject.
     * Note, pipeline's id is the ID of its start point if it is not overridden manually.
     * If it is not overridden, it can be autoselected by execution service.
     * @param id the pipeline id.
     * @return a pipeline instance or null, if nothing found
     */
    @Nullable
    Pipeline getById(String id);
    /**
     * Loads a cached or previously saved {@link Pipeline}, associated with the given id and particular subject.
     * Note, pipeline's id is the ID of its start point if it is not overridden manually.
     * If it is not overridden, it can be autoselected by execution service.
     * @param id the pipeline id
     * @param subject the subject to run the pipeline on
     * @return a pipeline instance or null, if nothing found
     */
    @Nullable
    Pipeline getByIdAndSubject(String id, String subject);
    /**
     * Loads cached or previously saved {@link Pipeline} instances, associated with particular subject.
     * @param subject the subject to run the pipeline on
     * @return a pipeline instance or null, if nothing found
     */
    Collection<Pipeline> getBySubject(String subject);
    /**
     * Gets all start segments, registered by the system.
     * @return collection of start segments
     */
    Collection<Segment> getStartSegments();
    /**
     * Gets all point segments, registered by the system.
     * @return collection of point segments
     */
    Collection<Segment> getPointSegments();
    /**
     * Gets all connector segments, registered by the system.
     * @return collection of connector segments
     */
    Collection<Segment> getConnectorSegments();
    /**
     * Gets all finish segments, registered by the system.
     * @return collection of finish segments
     */
    Collection<Segment> getFinishSegments();
    /**
     * Gets all fallback segments, registered on the system.
     * @return collectionf of fallback segments
     */
    Collection<Segment> getFallbackSegments();
    /**
     * Gets segments for the given start ID.
     * @param id the start segment ID
     * @return collection of segments
     */
    Collection<Segment> getSegmentsForStart(String id);
    /**
     * Saves and caches given pipeline with NO subject, associated with it.
     * @param pipeline the pipeline to save
     */
    void save(@Nonnull Pipeline pipeline);
    /**
     * Removes a pipeline.
     * @param startId the start ID
     * @param subjectId the subject ID, may be blank
     */
    void remove(@Nonnull String startId, @Nullable String subjectId);
    /**
     * Get uncpecified segment by id.
     * @param id the id
     * @return segment or null
     */
    @Nullable
    Segment segment(String id);
    /**
     * Get segment specifically as a start segment by id.
     * @param <C> the context type
     * @param id the id
     * @return start segment or null
     */
    @Nullable
    <C extends PipelineInput> Start<C> start(String id);
    /**
     * Get segment specifically as a point segment by id.
     * @param <C> the context type
     * @param id the id
     * @return point segment or null
     */
    @Nullable
    <C extends PipelineInput> Point<C> point(String id);
    /**
     * Get segment specifically as a connector segment by id.
     * @param <C> the context type
     * @param <R> the result type
     * @param id the id
     * @return connector segment or null
     */
    @Nullable
    <C extends PipelineInput, R extends PipelineOutput> Connector<C, R> connector(String id);
    /**
     * Get segment specifically as a finish segment by id.
     * @param <C> the context type
     * @param <R> the result type
     * @param id the id
     * @return finish segment or null
     */
    @Nullable
    <C extends PipelineInput, R extends PipelineOutput> Finish<C, R> finish(String id);
    /**
     * Gets a fallback segment by id.
     * @param <C> The input type
     * @param <T> The throwable type
     * @param id Segment id
     * @return fallback segment or null
     */
    @Nullable
    <C extends PipelineInput> Fallback<C> fallback(String id);

    void load(String startId, String subject, InputStream fileInputStream) throws IOException;

    void load(String startId, String subject, File file) throws IOException;

    /**
     * Load pipelines after modules started
     */
    void loadPipelines();
}
