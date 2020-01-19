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

package org.unidata.mdm.data.service.segments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.data.type.data.EtalonRecord;
import org.unidata.mdm.data.type.data.EtalonRelation;
import org.unidata.mdm.data.type.data.EtalonRelationInfoSection;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.data.impl.EtalonRelationImpl;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.data.type.timeline.RelationTimeInterval;
import org.unidata.mdm.data.type.timeline.RelationTimeline;

/**
 * @author Mikhail Mikhailov on Dec 9, 2019
 */
public interface ContainmentRelationSupport {
    /**
     * Mirrors containment record's timeline to its relation counterpart.
     * Note, origin records are not converted as of now.
     * @param keys the relation key
     * @param original the original timeline
     * @return mirrored timeline
     */
    default Timeline<OriginRelation> mirrorTimeline(RelationKeys keys, Timeline<OriginRecord> original) {

        Timeline<OriginRelation> mirror = new RelationTimeline(keys);
        if (Objects.nonNull(original)) {

            List<EtalonRelation> relations = new ArrayList<>(original.size());
            for (TimeInterval<OriginRecord> interval : original) {

                EtalonRecord record = interval.getCalculationResult();
                EtalonRelation relation = new EtalonRelationImpl()
                    .withDataRecord(record)
                    .withInfoSection(new EtalonRelationInfoSection()
                            .withCreateDate(record.getInfoSection().getCreateDate())
                            .withUpdateDate(record.getInfoSection().getUpdateDate())
                            .withCreatedBy(record.getInfoSection().getCreatedBy())
                            .withUpdatedBy(record.getInfoSection().getUpdatedBy())
                            .withStatus(record.getInfoSection().getStatus())
                            .withApproval(record.getInfoSection().getApproval())
                            .withPeriodId(record.getInfoSection().getPeriodId())
                            .withValidFrom(record.getInfoSection().getValidFrom())
                            .withValidTo(record.getInfoSection().getValidTo())
                            .withRelationEtalonKey(keys.getEtalonKey().getId())
                            .withRelationName(keys.getRelationName())
                            .withFromEtalonKey(keys.getEtalonKey().getFrom())
                            .withFromEntityName(keys.getFromEntityName())
                            .withToEtalonKey(keys.getEtalonKey().getTo())
                            .withToEntityName(keys.getToEntityName())
                            .withRelationType(keys.getRelationType()));

                relations.add(relation);

                TimeInterval<OriginRelation> i = new RelationTimeInterval(interval.getValidFrom(), interval.getValidTo(), Collections.emptyList());

                i.setCalculationResult(relation);
                i.setActive(interval.isActive());
                i.setPending(interval.isPending());

                mirror.add(i);
            }
        }

        return mirror;
    }
}
