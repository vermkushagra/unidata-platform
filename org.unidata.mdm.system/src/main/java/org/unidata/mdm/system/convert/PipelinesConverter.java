package org.unidata.mdm.system.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.unidata.mdm.system.exception.PipelineException;
import org.unidata.mdm.system.exception.SystemExceptionIds;
import org.unidata.mdm.system.serialization.json.PipelineJS;
import org.unidata.mdm.system.serialization.json.SegmentJS;
import org.unidata.mdm.system.type.pipeline.Connector;
import org.unidata.mdm.system.type.pipeline.Fallback;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Pipeline;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Segment;
import org.unidata.mdm.system.type.pipeline.SegmentType;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.util.PipelineUtils;

/**
 * @author Mikhail Mikhailov on Nov 25, 2019
 */
public class PipelinesConverter {

    private PipelinesConverter() {
        super();
    }

    public static List<PipelineJS> toPipelines(Collection<Pipeline> pipelines) {

        if (CollectionUtils.isEmpty(pipelines)) {
            return Collections.emptyList();
        }

        List<PipelineJS> result = new ArrayList<>(pipelines.size());
        for (Pipeline p : pipelines) {

            PipelineJS pro = to(p);
            if (Objects.isNull(pro)) {
                continue;
            }

            result.add(pro);
        }

        return result;
    }

    public static PipelineJS to (Pipeline p) {

        if (Objects.isNull(p)) {
            return null;
        }

        PipelineJS result = new PipelineJS();
        result.setStartId(p.getStartId());
        result.setSubjectId(p.getSubjectId());
        result.setDescription(p.getDescription());
        result.setSegments(toSegments(Stream.concat(p.getSegments().stream(), p.getFallbacks().stream()).collect(Collectors.toList())));

        return result;
    }

    public static List<SegmentJS> toSegments(Collection<Segment> segments) {

        if (CollectionUtils.isEmpty(segments)) {
            return Collections.emptyList();
        }

        List<SegmentJS> result = new ArrayList<>(segments.size());
        for (Segment s : segments) {

            SegmentJS sro = to(s);
            if (Objects.isNull(sro)) {
                continue;
            }

            result.add(sro);
        }

        return result;
    }

    public static SegmentJS to(Segment s) {

        if (Objects.isNull(s)) {
            return null;
        }

        SegmentJS result = new SegmentJS();
        result.setId(s.getId());
        result.setSegmentType(s.getType().name());

        return result;
    }

    public static Pipeline from(PipelineJS js) {

        if (Objects.isNull(js)) {
            return null;
        }

        // Gather segments
        List<Segment> gathered = fromSegments(js.getSegments());
        if (gathered.size() < 2) {
            throw new PipelineException("Invalid number of segments. A pipeline must contain at least 2 points of type 'start' and 'finish'.",
                    SystemExceptionIds.EX_PIPELINE_INVALID_NUMBER_OF_SEGMENTS);
        } else if (gathered.get(0).getType() != SegmentType.START
                || gathered.get(gathered.size() - 1).getType() != SegmentType.FINISH) {
            throw new PipelineException("Invalid pipeline layout. A pipeline must start with a point of type 'start' and end with a point of type 'finish'.",
                    SystemExceptionIds.EX_PIPELINE_HAS_NO_START_OR_FINISH_OR_BOTH);
        }

        Start<?> s = (Start<?>) gathered.get(0);
        Finish<?, ?> f = (Finish<?, ?>) gathered.get(gathered.size() - 1);

        Pipeline p = Pipeline.start(s, js.getSubjectId(), js.getDescription());
        for (int i = 1; i < (gathered.size() - 1); i++) {
            Segment segment = gathered.get(i);
            if (segment.getType() == SegmentType.POINT) {
                p.with((Point<?>) segment);
            } else if (segment.getType() == SegmentType.CONNECTOR) {
                p.with((Connector<?, ?>) segment);
            } else if (segment.getType() == SegmentType.FALLBACK) {
                p.fallback((Fallback<?>) segment);
            }
        }

        p.end(f);
        return p;
    }

    public static List<Segment> fromSegments(List<SegmentJS> ros) {

        if (CollectionUtils.isEmpty(ros)) {
            return Collections.emptyList();
        }

        List<Segment> result = new ArrayList<>(ros.size());
        for (SegmentJS ro : ros) {
            result.add(from(ro));
        }

        return result;
    }

    public static Segment from(SegmentJS js) {

        if (Objects.isNull(js)) {
            return null;
        }

        Segment hit = PipelineUtils.findSegment(js.getId());
        if (Objects.isNull(hit)) {
            throw new PipelineException("Segment not found by id [{}].",
                    SystemExceptionIds.EX_PIPELINE_SEGMENT_NOT_FOUND_BY_ID,
                    js.getId());
        } else if (!hit.getType().name().equals(js.getSegmentType())) {
            throw new PipelineException("Segment found by ID, but is of different type [{}].",
                    SystemExceptionIds.EX_PIPELINE_SEGMENT_OF_WRONG_TYPE, js.getSegmentType());
        }

        return hit;
    }
}
