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

package com.unidata.mdm.backend.service.data.timeline;

import java.util.Date;

import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.RelationIdentityContext;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.OriginRelation;
import com.unidata.mdm.backend.common.types.TimeIntervalContributorInfo;
import com.unidata.mdm.backend.common.types.Timeline;

/**
 * @author Mikhail Mikhailov
 * Methods, related to timeline calculations.
 */
public interface TimelineService {
    /**
     * Loads full record's timeline including versions.
     * @param ctx identity
     * @param viewDrafts load pending versions (true), or not (false)
     * @return timeline
     */
    Timeline<OriginRecord> loadTimeline(RecordIdentityContext ctx, boolean viewDrafts);
    /**
     * Loads full relation's timeline including versions.
     * @param ctx identity
     * @param viewDrafts load pending versions (true), or not (false)
     * @return timeline
     */
    Timeline<OriginRelation> loadTimeline(RelationIdentityContext ctx, boolean viewDrafts);
    /**
     * Loads record's timeline including versions reducing by given boundary.
     * @param ctx identity
     * @param from left boundary
     * @param to right boundary
     * @param viewDrafts load pending versions (true), or not (false)
     * @return timeline
     */
    Timeline<OriginRecord> loadTimeline(RecordIdentityContext ctx, Date from, Date to, boolean viewDrafts);
    /**
     * Loads full relation's timeline including versions reducing by given boundary.
     * @param ctx identity
     * @param from left boundary
     * @param to right boundary
     * @param viewDrafts load pending versions (true), or not (false)
     * @return timeline
     */
    Timeline<OriginRelation> loadTimeline(RelationIdentityContext ctx, Date from, Date to, boolean viewDrafts);
    /**
     * Loads full record's timeline including versions.
     * @param ctx identity
     * @param viewDrafts load pending versions (true), or not (false)
     * @return timeline
     */
    Timeline<TimeIntervalContributorInfo> loadTimelineInfo(RecordIdentityContext ctx, boolean viewDrafts);
    /**
     * Loads full relation's timeline including versions.
     * @param ctx identity
     * @param viewDrafts load pending versions (true), or not (false)
     * @return timeline
     */
    Timeline<TimeIntervalContributorInfo> loadTimelineInfo(RelationIdentityContext ctx, boolean viewDrafts);
}
