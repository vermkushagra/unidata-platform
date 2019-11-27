package org.unidata.mdm.system.serialization.json;

import java.util.Collections;
import java.util.List;

/**
 * @author Mikhail Mikhailov on Nov 25, 2019
 */
public class PipelineJS {

    private String startId;

    private String subjectId;

    private String description;

    private List<SegmentJS> segments;

    public String getStartId() {
        return startId;
    }

    public void setStartId(String startId) {
        this.startId = startId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subject) {
        this.subjectId = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SegmentJS> getSegments() {
        return segments == null ? Collections.emptyList() : segments;
    }

    public void setSegments(List<SegmentJS> segments) {
        this.segments = segments;
    }
}
