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

import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.service.matching.MatchingRulesService;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

public class MatchingItemProcessor implements ItemProcessor<String, MatchingItemDto> {

    /**
     * Matching rules meta service.
     */
    @Autowired
    private MatchingRulesService matchingRulesService;

    /**
     * Matching group info.
     */
    private ClusterMetaData clusterMetaData;
    /**
     * Entity name
     */
    private String entityName;

    private MatchingRule matchingRule = null;


    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }


    @Override
    public MatchingItemDto process(String etalonId) throws Exception {
        return new MatchingItemDto(etalonId, entityName, getMatchingRule());
    }

    private MatchingRule getMatchingRule() {
        if (matchingRule == null) {
            matchingRule = matchingRulesService.getMatchingRule(clusterMetaData.getRuleId());
        }
        return matchingRule;
    }


    @Required
    public void setClusterMetaData(ClusterMetaData clusterMetaData) {
        this.clusterMetaData = clusterMetaData;
    }
}
