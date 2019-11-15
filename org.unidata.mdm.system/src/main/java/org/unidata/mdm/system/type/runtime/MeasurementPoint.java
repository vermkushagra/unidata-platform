package org.unidata.mdm.system.type.runtime;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.javasimon.Manager;
import org.javasimon.SimonManager;
import org.javasimon.Split;

/**
 * @author Mikhail Mikhailov
 * Measurement point.
 */
public class MeasurementPoint {
    /**
     * Collection of steps.
     */
    private final Deque<Pair<String, Split>> steps = new ArrayDeque<>();
    /**
     * Context.
     */
    private final MeasurementContext ctx;
    /**
     * Thread local context.
     */
    private static final ThreadLocal<MeasurementPoint> MEASUREMENT_CONTEXT
        = new ThreadLocal<>();
    /**
     * Measurement generally (enabled or not).
     */
    private static volatile boolean enabled;
    /**
     * Constructor.
     */
    private MeasurementPoint(MeasurementContext ctx) {
        super();
        this.ctx = ctx;
    }

    /**
     * Starts new split.
     * Takes custom label instead of calling method ctx.
     * @param category a possibly given label category
     * @param label the label to take
     */
    public static void start(@Nullable String category, @Nonnull String label) {

        MeasurementPoint point = MEASUREMENT_CONTEXT.get();
        if (point == null) {
            return;
        }

        final boolean isEmpty = point.steps.peek() == null;
        String path = isEmpty
                ? point.ctx.getName()
                : point.steps.peek().getLeft()
                    + Manager.HIERARCHY_DELIMITER
                    + (Objects.nonNull(category) ? ("[" + category + "]") : "[UNSPECIFIED]")
                    + "->"
                    + ("[" + label + "]");

        Split split = SimonManager.getStopwatch(path).start();
        point.steps.push(new ImmutablePair<>(path, split));
    }

    /**
     * Starts new split.
     */
    public static void start() {

        MeasurementPoint point = MEASUREMENT_CONTEXT.get();
        if (point == null) {
            return;
        } else if (!enabled) {
            MEASUREMENT_CONTEXT.set(null);
            return;
        }

        final boolean isEmpty = point.steps.peek() == null;
        final StackTraceElement frame = isEmpty
                ? null
                : new Throwable().getStackTrace()[1];

        String path = Objects.isNull(frame)
                ? point.ctx.getName()
                : point.steps.peek().getLeft()
                    + Manager.HIERARCHY_DELIMITER
                    + StringUtils.substringAfterLast(frame.getClassName(), ".")
                    + "->"
                    + frame.getMethodName();

        Split split = SimonManager.getStopwatch(path).start();
        point.steps.push(new ImmutablePair<>(path, split));
    }

    /**
     * Stops current split.
     */
    public static void stop() {
        MeasurementPoint point = MEASUREMENT_CONTEXT.get();
        if (point == null) {
            return;
        }

        Pair<String, Split> step = point.steps.pop();
        if (step != null) {
            step.getRight().stop();
        }
    }

    /**
     * Sets the context and initializes measurement point.
     * @param ctx the the current measurement context
     */
    public static void init(MeasurementContext ctx) {

        if (!enabled) {
            MEASUREMENT_CONTEXT.set(null);
            return;
        }

        MEASUREMENT_CONTEXT.set(new MeasurementPoint(ctx));
    }

    /**
     * Tells, whether this thread has been initialized with a measurement point.
     * @return true, if so, false otherwise
     */
    public static boolean initialized() {
        return MEASUREMENT_CONTEXT.get() != null;
    }

    /**
     * @return the enabled
     */
    public static boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public static void setEnabled(boolean enabled) {
        MeasurementPoint.enabled = enabled;
    }
}
