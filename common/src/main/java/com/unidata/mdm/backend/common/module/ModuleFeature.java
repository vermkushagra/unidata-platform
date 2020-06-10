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
