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

package com.unidata.mdm.backend.common.module;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.license.EditionType;
import com.unidata.mdm.backend.common.license.OperationMode;

/**
 * @author Mikhail Mikhailov
 * A single feature, supportde by a module.
 */
public interface ModuleFeature {
    /**
     * Gets parent module id, to which it belongs to.
     * @return parent module
     */
    @Nonnull
    Module getModuleId();
    /**
     * Feature's internal name.
     * @return internal name
     */
    @Nonnull
    String getName();
    /**
     * Operation types mask.
     * @return mask
     */
    int getOperationModesMask();
    /**
     * Edition types mask.
     * @return mask
     */
    int getEditionTypesMask();
    /**
     * Tells whether an edition is supported.
     * @param type the type to check
     * @return true, if so, false otherwise
     */
    default boolean isEditionSupported(EditionType type) {
        return (getEditionTypesMask() & type.mask()) != 0;
    }
    /**
     * Tells whether an edition is supported.
     * @param mode operation mode
     * @return true, if so, false otherwise
     */
    default boolean isOperationModeSupported(OperationMode mode) {
        return (getOperationModesMask() & mode.mask()) != 0;
    }
}
