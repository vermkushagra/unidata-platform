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
 * Matching rules changed event.
 */
public class MatchingRulesEvent implements Serializable {
    /**
     * Make sonar happy.
     */
    private static final long serialVersionUID = 8111668085152290454L;
    /**
     * The entity names.
     */
    private final Collection<String> entityNames;
    /**
     * Constructor.
     * @param entityNames the names to reload rules for
     */
    public MatchingRulesEvent(Collection<String> entityNames) {
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
