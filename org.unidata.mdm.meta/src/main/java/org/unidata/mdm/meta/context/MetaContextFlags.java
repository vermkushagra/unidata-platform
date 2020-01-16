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

package org.unidata.mdm.meta.context;

import org.unidata.mdm.system.context.CommonRequestContext;

/**
 * @author Mikhail Mikhailov on Nov 28, 2019
 */
public class MetaContextFlags {
    /**
     * Notification flag.
     */
    public static final int FLAG_DRAFT = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Gather shallow, reduced set of information.
     */
    public static final int FLAG_REDUCED = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * All groups requested.
     */
    public static final int FLAG_ALL_ENTITY_GROUPS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * All entities requested.
     */
    public static final int FLAG_ALL_ENTITIES = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * All lookups requested.
     */
    public static final int FLAG_ALL_LOOKUPS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * All enumerations requested.
     */
    public static final int FLAG_ALL_ENUMERATIONS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * All relations requested.
     */
    public static final int FLAG_ALL_RELATIONS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * All source systems requested.
     */
    public static final int FLAG_ALL_SOURCE_SYSTEMS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Admin SS requested.
     */
    public static final int FLAG_ADMIN_SOURCE_SYSTEM = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * All measured values requested.
     */
    public static final int FLAG_ALL_MEASURED_VALUES = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Do not remove elements
     */
    public static final int FLAG_SKIP_REMOVE_ELEMENTS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Is direct update.
     */
    public static final int FLAG_DIRECT = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Constructor.
     */
    private MetaContextFlags() {
        super();
    }
}
