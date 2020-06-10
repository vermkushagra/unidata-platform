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

package com.unidata.mdm.backend.common.service;

import java.time.LocalDateTime;

import com.unidata.mdm.backend.common.license.EditionType;
import com.unidata.mdm.backend.common.license.OperationMode;

/**
 * @author Mikhail Mikhailov
 * Service for various license management related tasks.
 */
public interface LicenseService {
    /**
     * Gets current operation mode.
     * @return the op mode
     */
    OperationMode getOperationMode();
    /**
     * Gets current edition type.
     * @return the edition type
     */
    EditionType getEditionType();
    /**
     * Gets the license owner.
     * @return the owner
     */
    String getOwner();
    /**
     * Gets the license expiration date.
     * @return the expiration date
     */
    LocalDateTime getExpirationDate();
    /**
     * Gets the currently configured modules.
     * @return list of modules
     */
    String[] getModules();
    /**
     * Tells the caller, whether the current license is valid or not.
     * @return true, if the license is valid, false otherwise
     */
    boolean isLicenseValid();
    /**
     * Tells the caller, whether current hardware key matches with the hardware, where the platform is running.
     * @return true, if the hardware is valid, false otherwise
     */
    boolean isHardwareIdValid();
}
