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

/**
 *
 */
package com.unidata.mdm.backend.service.data.driver;

import java.util.List;

import com.unidata.mdm.backend.common.data.CalculableHolder;

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
