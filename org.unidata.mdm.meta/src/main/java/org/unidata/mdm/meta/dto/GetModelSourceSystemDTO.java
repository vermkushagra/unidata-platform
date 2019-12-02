package org.unidata.mdm.meta.dto;

import org.unidata.mdm.meta.SourceSystemDef;

/**
 * @author Mikhail Mikhailov on Dec 3, 2019
 */
public class GetModelSourceSystemDTO {
    /**
     * The SS.
     */
    private SourceSystemDef sourceSystem;

    public GetModelSourceSystemDTO() {
        super();
    }

    public GetModelSourceSystemDTO(SourceSystemDef sourceSystem) {
        this();
        this.sourceSystem = sourceSystem;
    }

    /**
     * @return the sourceSystem
     */
    public SourceSystemDef getSourceSystem() {
        return sourceSystem;
    }

    /**
     * @param sourceSystem the sourceSystem to set
     */
    public void setSourceSystem(SourceSystemDef sourceSystem) {
        this.sourceSystem = sourceSystem;
    }
}
