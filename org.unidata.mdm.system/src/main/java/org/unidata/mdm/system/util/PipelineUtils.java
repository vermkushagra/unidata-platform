package org.unidata.mdm.system.util;

import java.util.Objects;

import org.unidata.mdm.system.configuration.SystemConfiguration;
import org.unidata.mdm.system.service.PipelineService;
import org.unidata.mdm.system.type.pipeline.Pipeline;
import org.unidata.mdm.system.type.pipeline.Segment;

/**
 * @author Mikhail Mikhailov on Nov 25, 2019
 */
public class PipelineUtils {
    /**
     * The PS.
     */
    private static PipelineService pipelineService;
    /**
     * Disabling instantiation constructor.
     */
    private PipelineUtils() {
        super();
    }

    public static void init() {
        pipelineService = SystemConfiguration.getBean(PipelineService.class);
    }

    public static Segment findSegment(String id) {
        return pipelineService.segment(id);
    }

    public static Pipeline findPipeline(String id, String subject) {
        return Objects.isNull(subject) ? pipelineService.getById(id) : pipelineService.getByIdAndSubject(id, subject);
    }
}
