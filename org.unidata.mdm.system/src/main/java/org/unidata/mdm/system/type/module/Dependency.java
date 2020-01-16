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

package org.unidata.mdm.system.type.module;

/**
 * @author Alexander Malyshev
 */
public class Dependency {
    private final String moduleId;
    private final String version;
    private final String tag;

    public Dependency(String moduleId, String version) {
        this.moduleId = moduleId;
        this.version = version;
        tag = null;
    }

    public Dependency(String moduleId, String version, String tag) {
        this.moduleId = moduleId;
        this.version = version;
        this.tag = tag;
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getVersion() {
        return version;
    }

    public String getTag() {
        return tag;
    }
}
