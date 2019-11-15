package org.unidata.mdm.system.type.runtime;

/**
 * @author Mikhail Mikhailov
 * Denotes measuremnt context for the current thread.
 * Has only one method so far.
 */
public interface MeasurementContext {
    /**
     * Gets the name of this measurement context.
     * @return name
     */
    String getName();
}
