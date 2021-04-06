package com.unidata.mdm.backend.service.job.common;

/**
 * @author Mikhail Mikhailov
 *
 */
public abstract class JobRuntimeUtils {
    /**
     * Step's static parameters.
     */
    private static ThreadLocal<StepExecutionState> stepStateStorage = new ThreadLocal<>();
    /**
     * Constructor.
     */
    protected JobRuntimeUtils() {
        super();
    }
    /**
     * Gets staep execution state object.
     * @return state object
     */
    @SuppressWarnings("unchecked")
    public static<T extends StepExecutionState> T getStepState() {
        return (T) stepStateStorage.get();
    }
    /**
     * Sets step state object.
     * @param eo the object to set
     */
    public static void setStepState(StepExecutionState state) {
        stepStateStorage.set(state);
    }
    /**
     * Removes current step state object.
     */
    @SuppressWarnings("unchecked")
    public static<T extends StepExecutionState> T removeStepState() {
        T t = (T) stepStateStorage.get();
        stepStateStorage.remove();
        return t;
    }
    /**
     * Reference name constructor.
     * @param runId the run id
     * @param objectName the object name
     * @return name
     */
    public static String getObjectReferenceName(String runId, String objectName) {

        return new StringBuilder()
                .append(runId)
                .append("_")
                .append(objectName)
                .toString();
    }
}
