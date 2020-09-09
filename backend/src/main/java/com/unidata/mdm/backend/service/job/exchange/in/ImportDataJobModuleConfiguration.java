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

package com.unidata.mdm.backend.service.job.exchange.in;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Configuration;

import com.unidata.mdm.backend.common.license.EditionType;
import com.unidata.mdm.backend.common.license.OperationMode;
import com.unidata.mdm.backend.common.module.ImportDataModuleFeature;
import com.unidata.mdm.backend.common.module.Module;
import com.unidata.mdm.backend.common.module.ModuleConfiguration;
import com.unidata.mdm.backend.common.module.ModuleDescription;
import com.unidata.mdm.backend.common.module.ModuleFeature;
import com.unidata.mdm.backend.util.MessageUtils;

/**
 * @author Mikhail Mikhailov
 * Import data job module configuration.
 */
@Configuration
public class ImportDataJobModuleConfiguration implements ModuleConfiguration {
    /**
     * Constructor.
     */
    public ImportDataJobModuleConfiguration() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Module getModuleId() {
        return Module.MODULE_IMPORT_DATA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModuleDescription getDescription() {
        return new ModuleDescription(Module.MODULE_IMPORT_DATA.getModuleName(),
                MessageUtils.getMessage("app.module." + Module.MODULE_IMPORT_DATA.getModuleName() + ".name"),
                MessageUtils.getMessage("app.module." + Module.MODULE_IMPORT_DATA.getModuleName() + ".description"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ModuleFeature> getFeatures(EditionType edition, OperationMode mode) {
        return Arrays.stream(ImportDataModuleFeature.values())
                .filter(f -> f.isEditionSupported(edition) && f.isOperationModeSupported(mode))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ModuleFeature> getFeatures() {
        return Arrays.asList(ImportDataModuleFeature.values());
    }
}
