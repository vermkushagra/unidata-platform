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

package org.unidata.mdm.meta.service.job;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.core.type.job.JobEnumType;
import org.unidata.mdm.core.type.job.JobParameterType;
import org.unidata.mdm.core.util.JobUtils;
import org.unidata.mdm.meta.AbstractEntityDef;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.search.util.SearchUtils;

/**
 * @author Mikhail Mikhailov on Dec 19, 2019
 */
public interface ModelJobSupport {

    MetaModelService metaModelService();

    default JobEnumType getAllEntitiesParamsList() {
        return getAllEntitiesParamsList(true);
    }

    default JobEnumType getAllEntitiesMultiselectParamsList() {
        return getAllEntitiesMultiselectParamsList(true);
    }

    /**
     * Getll all entity names
     * @param includeAll include 'ALL' element to list
     * @return
     */
    default JobEnumType getAllEntitiesParamsList(boolean includeAll) {

        List<String> entities = getAllEntitiesList();
        if (includeAll) {
            entities.add(0, JobUtils.JOB_ALL);
        }

        final JobEnumType params = new JobEnumType();
        params.setParameterType(JobParameterType.STRING);
        params.setParameters(entities);
        return params;
    }

    /**
     * Getll all entity names
     * @param includeAll include 'ALL' element to list
     * @return
     */
    default JobEnumType getAllEntitiesMultiselectParamsList(boolean includeAll) {
        final JobEnumType params = getAllEntitiesParamsList(includeAll);
        params.setMultiSelect(true);
        return params;
    }

    default JobEnumType getJustEntitiesParamsList() {
        final JobEnumType params = new JobEnumType();
        params.setParameterType(JobParameterType.STRING);
        params.setParameters(getJustEntitiesList());
        return params;
    }

    default List<String> getAllEntitiesList() {
        return Stream.concat(
                metaModelService().getEntitiesList().stream(),
                metaModelService().getLookupEntitiesList().stream())
            .map(AbstractEntityDef::getName)
            .sorted(String::compareTo)
            .collect(Collectors.toList());
    }

    default List<String> getJustEntitiesList() {
        return metaModelService().getEntitiesList().stream().map(EntityDef::getName).collect(toList());
    }

    default List<String> getJustLookupEntitiesList() {
        return metaModelService().getLookupEntitiesList().stream().map(LookupEntityDef::getName).collect(toList());
    }

    /**
     * @return collection of name of entities
     */
    default List<String> getEntityList(String entityNames) {

        List<String> reindexTypes = new ArrayList<>();
        boolean reindexAll = StringUtils.contains(entityNames, JobUtils.JOB_ALL);
        if (reindexAll) {
            metaModelService().getLookupEntitiesList()
                            .stream()
                            .map(LookupEntityDef::getName)
                            .collect(toCollection(() -> reindexTypes));

            metaModelService().getEntitiesList()
                            .stream()
                            .map(EntityDef::getName)
                            .collect(toCollection(() -> reindexTypes));
        } else {
            if (entityNames != null) {
                String[] tokens = entityNames.split(SearchUtils.COMMA_SEPARATOR);
                Collections.addAll(reindexTypes, tokens);
            }
        }

        return reindexTypes;
    }
}
