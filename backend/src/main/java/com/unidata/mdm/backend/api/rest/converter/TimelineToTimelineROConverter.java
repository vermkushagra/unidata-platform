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

/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.data.ContributorRO;
import com.unidata.mdm.backend.api.rest.dto.data.TimeIntervalRO;
import com.unidata.mdm.backend.api.rest.dto.data.TimelineRO;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.dto.ContributorDTO;
import com.unidata.mdm.backend.common.dto.TimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author Mikhail Mikhailov
 * Time line converter.
 */
public class TimelineToTimelineROConverter {

    /**
     * Constructor.
     */
    private TimelineToTimelineROConverter() {
        super();
    }

    /**
     * Converts a list of sodurce timeline objects to target.
     * @param source the source
     * @param target the target
     */
    public static void convert(List<TimelineDTO> source, List<TimelineRO> target) {
        for (int i = 0; source != null && i < source.size(); i++) {
            target.add(convert(source.get(i)));
        }
    }

    /**
     * Convert method.
     * @param source the source
     * @return target
     */
    public static TimelineRO convert(TimelineDTO source) {
        TimelineRO target = new TimelineRO();

        target.setEtalonId(source.getEtalonId());
        target.setTimeline(new ArrayList<TimeIntervalRO>());

        copyTimeIntervals(source.getIntervals(), target.getTimeline());

        return target;
    }

    /**
     * Converts time intervals.
     * @param source the source
     * @param target the target
     */
    private static void copyTimeIntervals(List<TimeIntervalDTO> source, List<TimeIntervalRO> target) {
        if (source == null) {
            return;
        }
        for (TimeIntervalDTO sourceInterval : source) {
            if (CollectionUtils.isEmpty(sourceInterval.getContributors())) {
                continue;
            }
            TimeIntervalRO tit = new TimeIntervalRO();
            copyTimeInterval(sourceInterval, tit);
            target.add(tit);
        }
    }

    /**
     * Copy individual interval.
     * @param source the source
     * @param target the target
     */
    private static void copyTimeInterval(TimeIntervalDTO source, TimeIntervalRO target) {

        target.setDateFrom(ConvertUtils.date2LocalDateTime(source.getValidFrom()));
        target.setDateTo(ConvertUtils.date2LocalDateTime(source.getValidTo()));
        target.setActive(source.isActive());
        target.setContributors(new ArrayList<ContributorRO>());

        copyContributors(source.getContributors(), target.getContributors());
    }

    /**
     * Copy contributors list.
     * @param source the source
     * @param target the target
     */
    private static void copyContributors(List<ContributorDTO> source, List<ContributorRO> target) {
        for (int i = 0; source != null && i < source.size(); i++) {

            ContributorRO co = new ContributorRO();

            co.setOriginId(source.get(i).getOriginId());
            co.setSourceSystem(source.get(i).getSourceSystem());
            co.setVersion(source.get(i).getRevision());
            co.setStatus(source.get(i).getStatus() == null ? null : source.get(i).getStatus().toString());
            co.setApproval(source.get(i).getApproval());
            co.setOwner(source.get(i).getOwner());

            target.add(co);
        }
    }
}
