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
