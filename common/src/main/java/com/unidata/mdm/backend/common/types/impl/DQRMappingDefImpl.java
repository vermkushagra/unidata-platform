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

package com.unidata.mdm.backend.common.types.impl;

import com.unidata.mdm.backend.common.upath.UPath;
import com.unidata.mdm.meta.DQRMappingDef;

/**
 * @author Mikhail Mikhailov
 * Impl class for DQ rules mapping.
 */
public class DQRMappingDefImpl extends DQRMappingDef {
    /**
     * Compiled UPath.
     */
    private transient UPath upath;
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -8380108177476527552L;
    /**
     * Constructor.
     */
    public DQRMappingDefImpl() {
        super();
    }
    /**
     * @return the upath
     */
    public UPath getUpath() {
        return upath;
    }
    /**
     * @param upath the upath to set
     */
    public void setUpath(UPath upath) {
        this.upath = upath;
    }
}
