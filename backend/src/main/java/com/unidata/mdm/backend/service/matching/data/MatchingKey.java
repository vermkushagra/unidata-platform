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

package com.unidata.mdm.backend.service.matching.data;

public class MatchingKey {
    private String matchingRuleName;
    private String entityName;
    private String matchingGroupName;

    public String getMatchingRuleName() {
        return matchingRuleName;
    }

    public MatchingKey setMatchingRuleName(String matchingRuleName) {
        this.matchingRuleName = matchingRuleName;
        return this;
    }

    public String getEntityName() {
        return entityName;
    }

    public MatchingKey setEntityName(String entityName) {
        this.entityName = entityName;
        return this;
    }

    public String getMatchingGroupName() {
        return matchingGroupName;
    }

    public MatchingKey setMatchingGroupName(String matchingGroupName) {
        this.matchingGroupName = matchingGroupName;
        return this;
    }
}
