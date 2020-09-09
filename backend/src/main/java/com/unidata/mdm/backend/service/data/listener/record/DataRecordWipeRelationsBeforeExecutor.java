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

package com.unidata.mdm.backend.service.data.listener.record;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.context.StorageId;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.data.relations.CommonRelationsComponent;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.RelationDef;

public class DataRecordWipeRelationsBeforeExecutor implements DataRecordBeforeExecutor<DeleteRequestContext> {

    @Autowired
    private MetaModelService metaModelService;

    @Autowired
    private RelationsServiceComponent relationsServiceComponent;

    @Autowired
    private CommonRelationsComponent commonRelationsComponent;

    @Override
    public boolean execute(DeleteRequestContext deleteRequestContext) {

        if (deleteRequestContext.isWipe()) {
            final RecordKeys keys = deleteRequestContext.keys();

            final Map<RelationDef, EntityDef> entityRelations =
                    metaModelService.getEntityRelations(keys.getEntityName(), false, true);

            if (MapUtils.isEmpty(entityRelations)) {
                return true;
            }

            Map<RelationDef, List<TimelineDTO>> timelines
                    = commonRelationsComponent.loadCompleteRelationsTimelineByFromSide(keys.getEtalonKey().getId(), false, false);

            final List<DeleteRelationRequestContext> requests = timelines.entrySet().stream()
                    .map(Entry::getValue)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .map(TimelineDTO::getEtalonId)
                    .map(eid -> {
                                final DeleteRelationRequestContext deleteRelationRequestContext = DeleteRelationRequestContext.builder()
                                        .relationEtalonKey(eid)
                                        .wipe(true)
                                        .build();
                                deleteRelationRequestContext.putToStorage(StorageId.DELETE_BY_RELATION, eid);
                                return deleteRelationRequestContext;
                            }
                    )
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(requests)) {
                relationsServiceComponent.deleteRelations(requests);
            }
        }

        return true;
    }
}
