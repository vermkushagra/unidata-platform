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

package com.unidata.mdm.backend.notification.events;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Mikhail Mikhailov
 * Matching groups changed event.
 */
public class MatchingGroupsEvent implements Serializable {
    /**
     * Make sonar happy.
     */
    private static final long serialVersionUID = -9113047037837132694L;
    /**
     * The entity names.
     */
    private final Collection<String> entityNames;
    /**
     * Constructor.
     * @param entityNames the names to reload groups for
     */
    public MatchingGroupsEvent(Collection<String> entityNames) {
        super();
        this.entityNames = entityNames;
    }
    /**
     * @return the entityNames
     */
    public Collection<String> getEntityNames() {
        return entityNames;
    }
}
