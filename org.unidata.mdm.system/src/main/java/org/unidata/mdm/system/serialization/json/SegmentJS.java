package org.unidata.mdm.system.serialization.json;

/**
 * @author Mikhail Mikhailov on Nov 25, 2019
 */
public class SegmentJS {

    private String segmentType;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSegmentType() {
        return segmentType;
    }

    public void setSegmentType(String segmentType) {
        this.segmentType = segmentType;
    }
}
