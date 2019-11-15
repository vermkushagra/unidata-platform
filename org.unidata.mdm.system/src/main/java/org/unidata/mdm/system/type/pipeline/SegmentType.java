package org.unidata.mdm.system.type.pipeline;

/**
 * @author Mikhail Mikhailov
 * The pipeline segment type.
 */
public enum SegmentType {
    /**
     * Pipeline's starting point, defining pipeline's input type.
     */
    START,
    /**
     * Pipeline's execution point. Contains code, processing input type.
     */
    POINT,
    /**
     * Connector type segment,
     * connecting another pipeline to this pipeline and returning intermediate result.
     */
    CONNECTOR,
    /**
     * The finalizer type, preparing the pipeline's result.
     */
    FINISH;
}
