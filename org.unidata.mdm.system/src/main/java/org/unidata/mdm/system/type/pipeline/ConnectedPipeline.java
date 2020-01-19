package org.unidata.mdm.system.type.pipeline;

import java.util.Objects;

/**
 * @author Mikhail Mikhailov on Jan 17, 2020
 */
public class ConnectedPipeline {
    /**
     * The PL.
     */
    private final Pipeline pipeline;
    /**
     * The connector.
     */
    private final Connector<?, ?> connector;
    /**
     * Constructor.
     * @param pipeline
     * @param connector
     */
    private ConnectedPipeline(Pipeline pipeline, Connector<?, ?> connector) {
        this.pipeline = pipeline;
        this.connector = connector;
    }
    /**
     * @return the pipeline
     */
    public Pipeline getPipeline() {
        return pipeline;
    }
    /**
     * @return the connector
     */
    public Connector<?, ?> getConnector() {
        return connector;
    }
    /**
     * Tells if the pipeline was set.
     * @return true, if set, false otherwise
     */
    public boolean pipelineIsSet() {
        return Objects.nonNull(pipeline);
    }
    /**
     * Creates immutable connected pipeline.
     * @param pipeline the PL
     * @param connector the connector
     * @return connected pipeline
     */
    public static ConnectedPipeline of(Pipeline pipeline, Connector<?, ?> connector) {
        Objects.requireNonNull(connector, "Connector must not be null.");
        return new ConnectedPipeline(pipeline, connector);
    }
}
