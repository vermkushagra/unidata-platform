package org.unidata.mdm.system.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unidata.mdm.system.configuration.SystemConfigurationConstants;
import org.unidata.mdm.system.convert.PipelinesConverter;
import org.unidata.mdm.system.dao.PipelinesDAO;
import org.unidata.mdm.system.exception.PipelineException;
import org.unidata.mdm.system.exception.SystemExceptionIds;
import org.unidata.mdm.system.po.PipelinePO;
import org.unidata.mdm.system.serialization.json.PipelineJS;
import org.unidata.mdm.system.service.EventService;
import org.unidata.mdm.system.service.ModuleService;
import org.unidata.mdm.system.service.PipelineService;
import org.unidata.mdm.system.type.event.Event;
import org.unidata.mdm.system.type.event.EventReceiver;
import org.unidata.mdm.system.type.event.impl.PipelineUpdate;
import org.unidata.mdm.system.type.event.impl.PipelineUpdate.PipelineUpdateType;
import org.unidata.mdm.system.type.module.Module;
import org.unidata.mdm.system.type.pipeline.Connector;
import org.unidata.mdm.system.type.pipeline.Fallback;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Pipeline;
import org.unidata.mdm.system.type.pipeline.PipelineInput;
import org.unidata.mdm.system.type.pipeline.PipelineOutput;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Segment;
import org.unidata.mdm.system.type.pipeline.SegmentType;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.util.JsonUtils;

/**
 * {@link PipelineService} implementation.
 * @author Mikhail Mikhailov on Nov 1, 2019
 */
@Service("pipelineService")
public class PipelineServiceImpl implements PipelineService, EventReceiver {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineServiceImpl.class);
    /**
     * Pipelines by subjects.
     */
    private final ConcurrentMap<String, AdjacencyInfo> subjects = new ConcurrentHashMap<>();
    /**
     * Registered segments.
     */
    private final ConcurrentMap<String, Segment> segments = new ConcurrentHashMap<>();
    /**
     * ES instance.
     */
    @Autowired
    private EventService eventService;
    /**
     * Module registry.
     */
    @Autowired
    private ModuleService moduleService;
    /**
     * The repo.
     */
    @Autowired
    private PipelinesDAO pipelinesDAO;

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

        // 1. Collect points
        for (Module m : moduleService.getModules()) {

            Collection<Start<PipelineInput>> startSegments = m.getStartTypes();
            if (CollectionUtils.isNotEmpty(startSegments)) {
                segments.putAll(startSegments.stream().collect(Collectors.toMap(Segment::getId, Function.identity())));
            }

            Collection<Point<PipelineInput>> pointSegments = m.getPointTypes();
            if (CollectionUtils.isNotEmpty(pointSegments)) {
                segments.putAll(pointSegments.stream().collect(Collectors.toMap(Segment::getId, Function.identity())));
            }

            Collection<Connector<PipelineInput, PipelineOutput>> connectorSegments = m.getConnectorTypes();
            if (CollectionUtils.isNotEmpty(connectorSegments)) {
                segments.putAll(connectorSegments.stream().collect(Collectors.toMap(Segment::getId, Function.identity())));
            }

            Collection<Finish<PipelineInput, PipelineOutput>> finishSegments = m.getFinishTypes();
            if (CollectionUtils.isNotEmpty(finishSegments)) {
                segments.putAll(finishSegments.stream().collect(Collectors.toMap(Segment::getId, Function.identity())));
            }

            Collection<Fallback<PipelineInput>> fallbackSegments = m.getFallbacks();
            if (CollectionUtils.isNotEmpty(fallbackSegments)) {
                segments.putAll(fallbackSegments.stream().collect(Collectors.toMap(Segment::getId, Function.identity())));
            }
        }

        // 2. Read DB state
        cacheLoad();

        // 3. Register self as event consumer
        eventService.register(this, PipelineUpdate.class);
    }

    private void cacheLoad() {

        List<PipelinePO> pos = pipelinesDAO.loadAll();
        List<Pipeline> defaults = new ArrayList<>();
        for (PipelinePO po : pos) {

            String startId = po.getStartId();
            String subjectId = po.getSubject();

            Start<?> s = start(startId);
            if (Objects.isNull(s)) {
                LOGGER.warn("Start id [{}] does not exists. Skipping.", startId);
            } else {

                Pipeline result;
                try {
                    result = PipelinesConverter.from(JsonUtils.read(po.getContent(), PipelineJS.class));
                } catch (PipelineException e) {
                    LOGGER.warn("Pipeline exception caught. "
                              + "The pipeline with start id [{}], subject [{}] will not be loaded.", startId, subjectId, e);
                    continue;
                }

                if (StringUtils.isNotBlank(subjectId)) {
                    subjects.put(subjectId, AdjacencyInfo.of(result));
                } else {
                    defaults.add(result);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(defaults)) {
            subjects.put(SystemConfigurationConstants.NON_SUBJECT, AdjacencyInfo.of(defaults));
        }
    }

    private void cachePut(Pipeline pipeline) {

        if (StringUtils.isBlank(pipeline.getSubjectId())) {
            subjects.compute(SystemConfigurationConstants.NON_SUBJECT, (k, v) -> {
                if (Objects.isNull(v)) {
                    return AdjacencyInfo.of(Collections.singletonList(pipeline));
                }
                v.multiple.put(pipeline.getStartId(), pipeline);
                return v;
            });
        } else {
            subjects.compute(pipeline.getSubjectId(), (k, v) -> {
                if (Objects.isNull(v)) {
                    return AdjacencyInfo.of(pipeline);
                }
                v.singleton = pipeline;
                return v;
            });
        }
    }

    private void cacheRemove(String startId, String subjectId) {
        if (StringUtils.isBlank(subjectId)) {
            subjects.compute(SystemConfigurationConstants.NON_SUBJECT, (k, v) -> {
                if (Objects.isNull(v)) {
                    return null;
                }
                v.multiple.remove(startId);
                return v;
            });
        } else {
            subjects.compute(subjectId, (k, v) -> {
                if (Objects.isNull(v)) {
                    return null;
                }
                v.singleton = null;
                return v;
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pipeline getById(String id) {

        if (Objects.isNull(id)) {
            return null;
        }

        AdjacencyInfo info = subjects.get(SystemConfigurationConstants.NON_SUBJECT);
        return Objects.isNull(info) ? null : info.get(id);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Pipeline getByIdAndSubject(String id, String subject) {

        if (Objects.isNull(subject) || Objects.isNull(id)) {
            return null;
        }

        AdjacencyInfo info = subjects.get(subject);
        return Objects.isNull(info) ? null : info.get(id);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Pipeline> getAll() {

        Map<String, Pipeline> collected = new HashMap<>();
        for (Entry<String, AdjacencyInfo> entry : subjects.entrySet()) {
            entry.getValue().getAll().forEach(p -> collected.put(p.getStartId(), p));
        }

        return collected.values();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Collection<Pipeline>> getAllWithSubjects() {

        Map<String, Collection<Pipeline>> collected = new HashMap<>();
        for (Entry<String, AdjacencyInfo> entry : subjects.entrySet()) {
            if (SystemConfigurationConstants.NON_SUBJECT.equals(entry.getKey())) {
                collected.put(null, entry.getValue().getAll());
            } else {
                collected.put(entry.getKey(), entry.getValue().getAll());
            }
        }

        return collected;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Pipeline> getBySubject(String subject) {

        if (Objects.isNull(subject)) {
            return Collections.emptyList();
        }

        AdjacencyInfo info = subjects.get(subject);
        return Objects.isNull(info) ? Collections.emptyList() : info.getAll();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Segment> getStartSegments() {
        return segments.values().stream()
                .filter(s -> s.getType() == SegmentType.START)
                .collect(Collectors.toList());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Segment> getPointSegments() {
        return segments.values().stream()
                .filter(s -> s.getType() == SegmentType.POINT)
                .collect(Collectors.toList());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Segment> getConnectorSegments() {
        return segments.values().stream()
                .filter(s -> s.getType() == SegmentType.CONNECTOR)
                .collect(Collectors.toList());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Segment> getFinishSegments() {
        return segments.values().stream()
                .filter(s -> s.getType() == SegmentType.FINISH)
                .collect(Collectors.toList());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Segment> getFallbackSegments() {
        return segments.values().stream()
                .filter(s -> s.getType() == SegmentType.FALLBACK)
                .collect(Collectors.toList());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Segment> getSegmentsForStart(String id) {

        Segment s = segments.get(id);
        if (Objects.isNull(s) || s.getType() != SegmentType.START) {
            throw new PipelineException("Start segment not found by id [{}].",
                    SystemExceptionIds.EX_PIPELINE_START_SEGMENT_NOT_FOUND_BY_ID, id);
        }

        Start<?> start = (Start<?>) s;
        return segments.values().stream()
                .filter(segment -> segment.getType() != SegmentType.START && s.isBatched() == segment.isBatched() && segment.supports(start))
                .collect(Collectors.toList());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Pipeline pipeline) {

        if (Objects.isNull(pipeline)) {
            return;
        }

        PipelinePO po = new PipelinePO();
        po.setStartId(pipeline.getStartId());
        po.setSubject(pipeline.getSubjectId());
        po.setContent(JsonUtils.write(PipelinesConverter.to(pipeline)));

        pipelinesDAO.save(po);

        cachePut(pipeline);

        PipelineUpdate evt = new PipelineUpdate();
        evt.setStartId(pipeline.getStartId());
        evt.setSubjectId(pipeline.getSubjectId());
        evt.setUpdateType(PipelineUpdateType.UPSERT);

        eventService.fire(evt);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(String startId, String subjectId) {

        Objects.requireNonNull(startId, "Start id must not be null");

        pipelinesDAO.delete(startId,
                StringUtils.isBlank(subjectId)
                    ? SystemConfigurationConstants.NON_SUBJECT
                    : subjectId);

        cacheRemove(startId, subjectId);

        PipelineUpdate evt = new PipelineUpdate();
        evt.setStartId(startId);
        evt.setSubjectId(subjectId);
        evt.setUpdateType(PipelineUpdateType.REMOVAL);

        eventService.fire(evt);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Segment segment(String id) {
        return segments.get(id);
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends PipelineInput> Start<C> start(String id) {

        Segment s = segments.get(id);
        if (Objects.nonNull(s) && s.getType() == SegmentType.START) {
            return (Start<C>) s;
        }

        return null;
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends PipelineInput> Point<C> point(String id) {

        Segment s = segments.get(id);
        if (Objects.nonNull(s) && s.getType() == SegmentType.POINT) {
            return (Point<C>) s;
        }

        return null;
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends PipelineInput, R extends PipelineOutput> Connector<C, R> connector(String id) {

        Segment s = segments.get(id);
        if (Objects.nonNull(s) && s.getType() == SegmentType.CONNECTOR) {
            return (Connector<C, R>) s;
        }

        return null;
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends PipelineInput> Fallback<C> fallback(String id) {

        Segment s = segments.get(id);
        if (Objects.nonNull(s) && s.getType() == SegmentType.FALLBACK) {
            return (Fallback<C>) s;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends PipelineInput, R extends PipelineOutput> Finish<C, R> finish(String id) {

        Segment s = segments.get(id);
        if (Objects.nonNull(s) && s.getType() == SegmentType.FINISH) {
            return (Finish<C, R>) s;
        }

        return null;
    }
    @Override
    public void receive(Event event) {

        PipelineUpdate pu = (PipelineUpdate) event;

        if (pu.getUpdateType() == PipelineUpdateType.UPSERT) {

            PipelinePO po = pipelinesDAO.load(pu.getStartId(),
                    StringUtils.isBlank(pu.getSubjectId())
                        ? SystemConfigurationConstants.NON_SUBJECT
                        : pu.getSubjectId());

            if (Objects.isNull(po)) {
                LOGGER.warn("Recived Pipeline update event for start ID [{}], subject ID [{}], but the pipeline is missing in the DB!",
                        pu.getStartId(), pu.getSubjectId());
                return;
            }

            cachePut(PipelinesConverter.from(JsonUtils.read(po.getContent(), PipelineJS.class)));
        } else if (pu.getUpdateType() == PipelineUpdateType.REMOVAL) {

            cacheRemove(pu.getStartId(),
                    StringUtils.isBlank(pu.getSubjectId())
                        ? SystemConfigurationConstants.NON_SUBJECT
                        : pu.getSubjectId());
        }
    }

    @Override
    public void load(final String startId, final String subject, final InputStream inputStream) throws IOException {
        final String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        final PipelinePO pipelinePO = new PipelinePO();
        pipelinePO.setStartId(startId);
        pipelinePO.setSubject(subject);
        pipelinePO.setContent(content);
        pipelinesDAO.save(pipelinePO);
    }

    @Override
    public void load(final String startId, final String subject, final File file) throws IOException {
        try (final FileInputStream fileInputStream = new FileInputStream(file)) {
            load(startId, subject, fileInputStream);
        }
    }

    /**
     * Pipelines for a particular subject.
     * @author Mikhail Mikhailov on Nov 22, 2019
     */
    private static class AdjacencyInfo {
        /**
         * Connected pipelines, keyed by ID.
         */
        private Map<String, Pipeline> multiple;
        /**
         * Singleton pipeline on a real subject.
         */
        private Pipeline singleton;
        /**
         * Constructor.
         */
        public AdjacencyInfo() {
            super();
        }
        /**
         * Gets a pipeline by its id.
         * @param id the pipeline id
         * @return pipeline instance or null
         */
        @Nullable
        public Pipeline get(String id) {

            if (isSingleton()) {
                return singleton.getStartId().equals(id) ? singleton : null;
            } else if (isMultiple()) {
                return multiple.get(id);
            }

            return null;
        }
        /**
         * Gets all currently hold pipelines.
         * @return collection of pipelines
         */
        public Collection<Pipeline> getAll() {

            if (isSingleton()) {
                return Collections.singletonList(singleton);
            } else if (isMultiple()) {
                return multiple.values();
            }

            return Collections.emptyList();
        }
        /**
         * Checks for singleton condition.
         * @return true for singleton, false otherwise
         */
        public boolean isSingleton() {
            return this.singleton != null && this.multiple == null;
        }
        /**
         * Checks for multiple condition.
         * @return true for multiple, false otherwise
         */
        public boolean isMultiple() {
            return this.singleton == null && this.multiple != null;
        }
        /**
         * Creates info instance.
         * @param p the pipeline
         * @return info
         */
        public static AdjacencyInfo of(@Nonnull Pipeline p) {
            Objects.requireNonNull(p, "Pipeline cannot be null.");
            Objects.requireNonNull(p.getStartId(), "Pipeline's ID cannot be null.");
            AdjacencyInfo info = new AdjacencyInfo();
            info.singleton = p;
            return info;
        }
        /**
         * Creates info instance.
         * @param p the pipeline
         * @return info
         */
        public static AdjacencyInfo of(@Nonnull Collection<Pipeline> p) {
            Objects.requireNonNull(p, "Pipeline cannot be null.");
            AdjacencyInfo info = new AdjacencyInfo();
            info.multiple = new HashMap<>();
            p.forEach(pipeline -> info.multiple.put(pipeline.getStartId(), pipeline));
            return info;
        }
    }
}
