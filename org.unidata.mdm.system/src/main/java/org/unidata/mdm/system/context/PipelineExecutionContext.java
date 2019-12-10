package org.unidata.mdm.system.context;

/**
 * @author Mikhail Mikhailov
 * Interface for contexts, that participate in a pipeline.
 */
public interface PipelineExecutionContext {
    /**
     * Gets the pipeline type this context works with.
     * @return type
     */
    String getStartTypeId();
    /**
     * Narrow self to particular context type.
     * Dangerous! The ability must be checked obligatory via 'supports' call.
     * @param <T> the target type
     * @return narrowed object
     */
    @SuppressWarnings("unchecked")
    default <T> T narrow() {
        return (T) this;
    }
}
