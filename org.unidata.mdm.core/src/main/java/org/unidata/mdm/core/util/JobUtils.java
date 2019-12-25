package org.unidata.mdm.core.util;

import org.unidata.mdm.core.type.job.StepExecutionState;

/**
 * @author Mikhail Mikhailov on Dec 19, 2019
 */
public class JobUtils {
     /**
     * All.
     */
    public static final String JOB_ALL = "ALL";
    /**
     * Partition mark.
     */
    public static final String JOB_PARTITION = "partition:";
    /**
     * Member UUID.
     */
    public static final String JOB_CLUSTER_MEMBER_UUID = "memberUUID";
    /**
     * Step's static parameters.
     */
    private static ThreadLocal<StepExecutionState> stepStateStorage = new ThreadLocal<>();
    /**
     * Constructor.
     */
    private JobUtils() {
        super();
    }
    /**
     * Generates partition name. Just an int to string for now.
     * @param i partition number
     * @return name
     */
    public static String partitionName(int i) {
        return JOB_PARTITION + Integer.toString(i);
    }

    /**
     * Generates partition name. Just an int to string for now.
     * @param i partition number
     * @return name
     */
    public static String targetedPartitionName(int i, String targetUUID) {
        return JOB_PARTITION + Integer.toString(i) + ":" + targetUUID;
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
    public static String getObjectReferenceName(String runId, String objectName, String objectDetails) {

        return new StringBuilder()
                .append(runId)
                .append("_")
                .append(objectName)
                .append("_")
                .append(objectDetails)
                .toString();
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
}
