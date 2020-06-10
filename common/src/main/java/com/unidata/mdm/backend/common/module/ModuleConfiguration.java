package com.unidata.mdm.backend.common.module;

import java.util.List;

import com.unidata.mdm.backend.common.license.EditionType;
import com.unidata.mdm.backend.common.license.OperationMode;

/**
 * @author Mikhail Mikhailov
 * Module info.
 */
public interface ModuleConfiguration {
    /**
     * Gets the module identity.
     * @return module identity
     */
    Module getModuleId();
    /**
     * Gets module description.
     * @return description
     */
    ModuleDescription getDescription();
    /**
     * Gets list of all features, supported by a module.
     * @return list of all features, supported by a module
     */
    List<ModuleFeature> getFeatures();
    /**
     * Gets list of features, supported by a module in particular operation mode for an edition.
     * @param edition the edition
     * @param mode the mode
     * @return list of features
     */
    List<ModuleFeature> getFeatures(EditionType edition, OperationMode mode);
}
