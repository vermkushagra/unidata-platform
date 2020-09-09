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

package com.unidata.mdm.backend.service.data.convert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.unidata.mdm.backend.common.data.CalculableHolder;
import com.unidata.mdm.backend.common.data.ClassifierRecordHolder;
import com.unidata.mdm.backend.common.data.DataRecordHolder;
import com.unidata.mdm.backend.common.data.RelationRecordHolder;
import com.unidata.mdm.backend.common.data.TimeIntervalContributorHolder;
import com.unidata.mdm.backend.common.dto.ContributorDTO;
import com.unidata.mdm.backend.common.dto.TimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.keys.Keys;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.types.Calculable;
import com.unidata.mdm.backend.common.types.TimeInterval;
import com.unidata.mdm.backend.common.types.Timeline;
import com.unidata.mdm.backend.common.types.VistoryOperationType;

/**
 * @author Mikhail Mikhailov
 * Timeline internal <-> DTO converter.
 */
public class TimelineConverter {

    /**
     * Constructor.
     */
    private TimelineConverter() {
        super();
    }
    /**
     * Transfers calculable info to contributor view.
     * @param source the source
     * @return contributor DTO
     */
    public static<T extends Calculable> ContributorDTO to(CalculableHolder<T> source) {

        String originId = null;
        String owner = null;
        VistoryOperationType operationType = null;

        switch (source.getCalculableType()) {
        case INFO:
            originId = ((TimeIntervalContributorHolder) source).getValue().getOriginId();
            owner = ((TimeIntervalContributorHolder) source).getValue().getCreatedBy();
            operationType = ((TimeIntervalContributorHolder) source).getValue().getOperationType();
            break;
        case RELATION:
            originId = ((RelationRecordHolder) source).getValue().getInfoSection().getRelationOriginKey();
            owner = ((RelationRecordHolder) source).getValue().getInfoSection().getCreatedBy();
            break;
        case CLASSIFIER:
            originId = ((ClassifierRecordHolder) source).getValue().getInfoSection().getClassifierOriginKey();
            owner = ((ClassifierRecordHolder) source).getValue().getInfoSection().getCreatedBy();
            break;
        case RECORD:
            originId = ((DataRecordHolder) source).getValue().getInfoSection().getOriginKey().getId();
            owner = ((DataRecordHolder) source).getValue().getInfoSection().getCreatedBy();
            break;
        case ATTRIBUTE:
        default:
            break;
        }

        return new ContributorDTO(
                originId,
                source.getRevision(),
                source.getSourceSystem(),
                Objects.nonNull(source.getStatus()) ? source.getStatus().name() : null,
                Objects.nonNull(source.getApproval()) ? source.getApproval().name() : null,
                owner,
                source.getLastUpdate(),
                source.getTypeName(),
                operationType);
    }

    /**
     * Converts given interval to DTO.
     * @param source
     * @return
     */
    public static<T extends Calculable> TimeIntervalDTO to(TimeInterval<T> source) {

        TimeIntervalDTO target = new TimeIntervalDTO(
                source.getValidFrom(),
                source.getValidTo(),
                source.getPeriodId(),
                source.isActive());

        for (CalculableHolder<? extends Calculable> h : source) {
            target.getContributors().add(TimelineConverter.to(h));
        }

        return target;
    }
    /**
     * Converts given interval to DTO.
     * @param source
     * @return
     */
    public static<T extends Calculable> List<TimeIntervalDTO> toIntervalDTOs(List<TimeInterval<T>> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<TimeIntervalDTO> target = new ArrayList<>(source.size());
        for (TimeInterval<T> i : source) {
            target.add(to(i));
        }

        return target;
    }
    /**
     * Returns {@link TimelineDTO}.
     * @param timeline
     * @return
     */
    public static<T extends Calculable> TimelineDTO to(Timeline<T> source) {

        if (Objects.isNull(source)) {
            return null;
        }

        String id = null;
        Keys keys = source.getKeys();
        if (Objects.nonNull(keys)) {
            switch (keys.getType()) {
            case RECORD_KEYS:
                id = ((RecordKeys) keys).getEtalonKey().getId();
                break;
            case CLASSIFIER_KEYS:
                id = ((ClassifierKeys) keys).getEtalonId();
                break;
            case RELATION_KEYS:
                id = ((RelationKeys) keys).getEtalonId();
                break;
            default:
                break;
            }
        }

        TimelineDTO target = new TimelineDTO(id);
        for (TimeInterval<? extends Calculable> i : source) {
            target.getIntervals().add(TimelineConverter.to(i));
        }

        return target;
    }
    /**
     * Returns {@link TimelineDTO}.
     * @param timelines timeline list
     * @return list
     */
    public static<T extends Calculable> List<TimelineDTO> to(List<Timeline<T>> timelines) {

        if (CollectionUtils.isEmpty(timelines)) {
            return Collections.emptyList();
        }

        return timelines.stream()
            .filter(Objects::nonNull)
            .map(TimelineConverter::to)
            .collect(Collectors.toList());
    }
}
