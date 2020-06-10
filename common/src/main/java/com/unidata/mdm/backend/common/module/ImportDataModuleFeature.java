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

import com.unidata.mdm.backend.common.license.EditionType;
import com.unidata.mdm.backend.common.license.OperationMode;

/**
 * @author Mikhail Mikhailov
 * This module features.
 */
public enum ImportDataModuleFeature implements ModuleFeature {
    /**
     * Base feature.
     */
    FEATURE_BASE("base", OperationMode.maskAll(), EditionType.maskAll()),
    /**
     * Large mode.
     */
    FEATURE_LARGE_MODE("largeMode", OperationMode.maskAll(), EditionType.HP_EDITION.mask()),
    /**
     * Initial load.
     */
    FEATURE_INITIAL_LOAD("initialLoad", OperationMode.maskAll(), EditionType.HP_EDITION.mask()),
    /**
     * Clusters calculation.
     */
    FEATURE_CLUSTERS_CALCULATION("clustersCalculation", OperationMode.maskAll(), EditionType.HP_EDITION.mask());
    /**
     * Constructor.
     * @param name
     * @param opModesMask
     * @param editionsMask
     */
    private ImportDataModuleFeature(String name, int opModesMask, int editionsMask) {
        this.name = name;
        this.operationModesMask = opModesMask;
        this.editionTypesMask = editionsMask;
    }
    /**
     * {@inheritDoc}
     */
    private final String name;
    /**
     * {@inheritDoc}
     */
    private final int operationModesMask;
    /**
     * {@inheritDoc}
     */
    private final int editionTypesMask;
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int getOperationModesMask() {
        return operationModesMask;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int getEditionTypesMask() {
        return editionTypesMask;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Module getModuleId() {
        return Module.MODULE_IMPORT_DATA;
    }
}