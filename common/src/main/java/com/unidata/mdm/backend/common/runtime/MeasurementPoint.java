/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.common.runtime;

import java.util.ArrayDeque;
import java.util.Deque;

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
     * Name.
     */
    private final MeasurementContextName name;
    /**
     * Thread local context.
     */
    private static final ThreadLocal<MeasurementPoint> MEASUREMENT_CONTEXT
        = new ThreadLocal<>();

    /**
     * Measurement generally (enabled or not).
     */
    private static boolean enabled;

    /**
     * Constructor.
     */
    private MeasurementPoint(MeasurementContextName name) {
        super();
        this.name = name;
    }

    /**
     * Starts new split.
     */
    public static void start() {

        MeasurementPoint point = MEASUREMENT_CONTEXT.get();
        if (point == null) {
            return;
        }

        String path = point.steps.peek() == null
                ? point.name.name()
                : point.steps.peek().getLeft()
                    + Manager.HIERARCHY_DELIMITER
                    + StringUtils.substringAfterLast(
                            Thread.currentThread().getStackTrace()[2].getClassName(),
                            ".")
                    + "->"
                    + Thread.currentThread().getStackTrace()[2].getMethodName();

        Split split = SimonManager.getStopwatch(path).start();
        point.steps.push(new ImmutablePair<String, Split>(path, split));
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
     * Sets the name for the context and initializes measurement context.
     * @param name the name of the current context
     */
    public static void init(MeasurementContextName name) {

        if (!enabled) {
            MEASUREMENT_CONTEXT.set(null);
            return;
        }

        MEASUREMENT_CONTEXT.set(new MeasurementPoint(name));
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
