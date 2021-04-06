/**
 *
 */
package com.unidata.mdm.backend.service.data.driver;

import java.util.List;

/**
 * TODO The interface is crap! Refactor it!
 * @author Mikhail Mikhailov
 * Composition driver.
 */
public interface EtalonCompositionDriver<T> {
    /**
     * Tells whether the given calculable set denotes an active interval (validity range).
     * @param calculables calculables set
     * @return true, if active, false otherwise
     */
    boolean hasActiveBVR(List<CalculableHolder<T>> calculables);
    /**
     * Tells whether the given calculable set denotes an active interval (validity range).
     * @param calculables calculables set
     * @return true, if active, false otherwise
     */
    boolean hasActiveBVT(List<CalculableHolder<T>> calculables);
    /**
     * Computes BVR etalon object from calculable elements.
     * @param calculables calculable versions
     * @param includeInactive include inactive versions or not
     * @return result
     */
    T composeBVR(List<CalculableHolder<T>> calculables, boolean includeInactive, boolean includeWinners);
    /**
     * Computes BVT etalon object from calculable elements.
     * @param calculables calculable versions
     * @param includeInactive include inactive versions or not
     * @return result
     */
    T composeBVT(List<CalculableHolder<T>> calculables, boolean includeInactive, boolean includeWinners);
}
