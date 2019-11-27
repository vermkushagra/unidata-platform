package org.unidata.mdm.system.service;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.unidata.mdm.system.context.PipelineExecutionContext;
import org.unidata.mdm.system.dto.PipelineExecutionResult;
import org.unidata.mdm.system.type.pipeline.Connector;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Pipeline;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Segment;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * The service, responsible for pipeline management.
 * @author Mikhail Mikhailov on Nov 1, 2019
 */
public interface PipelineService extends AfterPlatformStartup {
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
    <C extends PipelineExecutionContext> Start<C> start(String id);
    /**
     * Get segment specifically as a point segment by id.
     * @param <C> the context type
     * @param id the id
     * @return point segment or null
     */
    <C extends PipelineExecutionContext> Point<C> point(String id);
    /**
     * Get segment specifically as a connector segment by id.
     * @param <C> the context type
     * @param <R> the result type
     * @param id the id
     * @return connector segment or null
     */
    <C extends PipelineExecutionContext, R extends PipelineExecutionResult> Connector<C, R> connector(String id);
    /**
     * Get segment specifically as a finish segment by id.
     * @param <C> the context type
     * @param <R> the result type
     * @param id the id
     * @return finish segment or null
     */
    <C extends PipelineExecutionContext, R extends PipelineExecutionResult> Finish<C, R> finish(String id);
}
