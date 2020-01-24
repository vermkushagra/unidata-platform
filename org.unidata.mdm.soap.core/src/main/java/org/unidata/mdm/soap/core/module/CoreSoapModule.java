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

package org.unidata.mdm.soap.core.module;

import org.unidata.mdm.system.type.module.Dependency;
import org.unidata.mdm.system.type.module.Module;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author Alexander Malyshev
 */
public class CoreSoapModule implements Module {

    private static final Set<Dependency> DEPENDENCIES = Collections.singleton(
            new Dependency("org.unidata.mdm.core", "6.0")
    );

    @Override
    public String getId() {
        return "org.unidata.mdm.soap.core";
    }

    @Override
    public String getVersion() {
        return "5.2";
    }

    @Override
    public String getName() {
        return "Core Soap Module";
    }

    @Override
    public String getDescription() {
        return "Core Soap Module";
    }

    @Override
    public Collection<Dependency> getDependencies() {
        return DEPENDENCIES;
    }
}
