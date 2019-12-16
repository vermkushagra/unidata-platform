package org.unidata.mdm.system.type.pipeline;

/**
 * Base type for pipeline parameter types
 * @author Mikhail Mikhailov on Dec 11, 2019
 */
public interface PipelineInput {
    /**
     * Gets the pipeline start segment type this context works with.
     * @return pipeline start type
     */
    String getStartTypeId();
    /**
     * Narrow self to particular type.
     * Dangerous! The ability must be checked obligatory via 'supports' call.
     * @param <T> the target type
     * @return narrowed object
     */
    @SuppressWarnings("unchecked")
    default <T> T narrow() {
        return (T) this;
    }
}
