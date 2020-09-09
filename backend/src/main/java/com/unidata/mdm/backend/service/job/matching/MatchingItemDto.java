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

package com.unidata.mdm.backend.service.job.matching;


import com.unidata.mdm.backend.service.matching.data.MatchingRule;

/**
 * Utility class for transfer information about  match item
 * @author Dmitry Kopin on 26.04.2017.
 */
public class MatchingItemDto {

    private final String etalonId;

    private final MatchingRule matchingRule;

    private final String entityName;

    public MatchingItemDto(String etalonId, String entityName, MatchingRule matchingRule){
        this.etalonId = etalonId;
        this.entityName = entityName;
        this.matchingRule = matchingRule;
    }

    public String getEtalonId() {
        return etalonId;
    }

    public String getEntityName() {
        return entityName;
    }

    public MatchingRule getMatchingRule() {
        return matchingRule;
    }
}
