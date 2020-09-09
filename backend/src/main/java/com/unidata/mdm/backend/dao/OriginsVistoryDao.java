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

package com.unidata.mdm.backend.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.po.OriginKeyPO;
import com.unidata.mdm.backend.po.OriginsVistoryRecordPO;
import com.unidata.mdm.backend.po.TimeIntervalPO;

/**
 * @author Mikhail Mikhailov
 * Origins vistory dao.
 */
public interface OriginsVistoryDao extends BaseDao {
    /**
     * UC: load an origin. Load most recent version for a given date.
     * @param originId the origin id
     * @param date the date
     * @param unpublishedView show unpublished view of the record (include draft versions into view)
     * @return object or null
     */
    OriginsVistoryRecordPO loadVersion(String originId, Date date, boolean unpublishedView);
    /**
     * UC: load versions as of date. Used for display or etalon calculation.
     *
     * @param etalonId the etalon id
     * @param date     the date
     * @param isApproverView - is approver
     * @param  userName userName
     * @return list of contributing versions
     */
    List<OriginsVistoryRecordPO> loadVersions(String etalonId, Date date, boolean isApproverView, String userName);
    /**
     * UC: load versions as of date and as of last update date 'lud'. Used for display or etalon calculation.
     *
     * @param etalonId the etalon id
     * @param date     the date
     * @param lud      lust update date
     * @param isApproverView - is approver
     * @param  userName userName
     * @return list of contributing versions
     */
    List<OriginsVistoryRecordPO> loadVersionsByLastUpdateDate(String etalonId, Date date, Date lud, boolean isApproverView, String userName);
    /**
     * UC: load versions as of date and as of last update date 'lud', but only if one of versions hits had updates after given date. Used for display or etalon calculation.
     *
     * @param etalonId the etalon id
     * @param date     the date
     * @param updatesAfter has updates after this date
     * @param isApproverView - is approver
     * @param userName userName
     * @return list of contributing versions
     */
    List<OriginsVistoryRecordPO> loadVersionsByUpdatesAfter(String etalonId, Date date, Date updatesAfter, boolean isApproverView, String userName);
    /**
     * UC: load versions as of date regatding time interval, restricted by operationId. Used for display or etalon calculation.
     *
     * @param etalonId the etalon id
     * @param date     the date
     * @param operationId      the operation id
     * @param isApproverView - is approver
     * @param userName userName
     * @return list of contributing versions
     */
    List<OriginsVistoryRecordPO> loadVersionsByOperationId(String etalonId, Date date, String operationId, boolean isApproverView, String userName);

    /**
     * UC: load versions without any filters applied.
     * @param etalonId the etalon id
     * @param point date point
     * @return list of top contributing versions
     */
    List<OriginsVistoryRecordPO> loadVersionsUnfilterdByEtalonId(String etalonId, Date point);
    /**
     * UC: load versions without any filters applied.
     * @param originId the origin id
     * @param date the date
     * @return list of top contributing versions
     */
    List<OriginsVistoryRecordPO> loadVersionsUnfilterdByOriginId(String originId, Date date);
    /**
     * UC: loads active pending versions for an origin id.
     * @param originId the id
     * @param point date point
     * @return list of versions
     */
    List<OriginsVistoryRecordPO> loadPendingVersionsByOriginId(String originId, Date point);
    /**
     * UC: loads active pending versions for an etalon id.
     * @param etalonId the id
     * @param point date point
     * @return list of versions
     */
    List<OriginsVistoryRecordPO> loadPendingVersionsByEtalonId(String etalonId, Date point);
    /**
     * UC: load origin history. List of all revisions of an origin, sorted by revision number.
     * @param originId the origin id
     * @return list of all revisions sorted by revision number
     */
    List<OriginsVistoryRecordPO> loadOriginHistory(String originId);
    /**
     * UC: load history. Load all versions, sorted by source system, external id (origin id), revision number.
     * @param etalonId the etalon id
     * @return map with origin key as a key and its revisions as value
     */
    Map<OriginKeyPO, List<OriginsVistoryRecordPO>> loadHistory(String etalonId);
    /**
     * UC: load contributing timeline. Calculate contributing time line for an etalon. Just state, no records returned.
     * @param etalonId the etalon id
     * @param entityName entity name
     * @param isApproverView is approver
     * @return timeline
     */
    List<TimeIntervalPO> loadContributingRecordsTimeline(String etalonId,String entityName, Boolean isApproverView);

    /**
     * UC: Load contributing timeline for several records.
     * @param etalonIds record ids
     * @param isApproverView whether to show approver view or not
     * @return timelines
     */
    Map<String, Map<String, List<TimeIntervalPO>>> loadContributingRecordsTimelines(List<String> etalonIds, Boolean isApproverView);
    /**
     * Loads etalon from <-> to boundary.
     * @param etalonId the etalon id
     * @param point the date point
     * @param isApproverView is approver
     * @return boundary
     */
    TimeIntervalPO loadEtalonBoundary(String etalonId, Date point, Boolean isApproverView);

    /**
     * UC: edit / save etalon record, import origin. Puts a new origin version to the vistory.
     * @param version the version to save
     * @return true, if successful, false otherwise
     */
    boolean putVersion(OriginsVistoryRecordPO version);
    /**
     * Puts several versions at once
     * @param versions the versions to put
     */
    void putVersions(List<OriginsVistoryRecordPO> versions);
    /**
     * Puts several versions at once in COPY fashion.
     * @param versions the versions to put
     * @param target the target table
     */
    void bulkInsertVersions(List<OriginsVistoryRecordPO> versions, String target);
    /**
     * UC: Update approval state for all pending versions of an etalon id.
     * @param etalonId the etalon id
     * @param to the state to apply
     * @return true, if successful, false otherwise
     */
    boolean updateApprovalState(String etalonId, ApprovalState to);
    /**
     * Updates vistory status.
     * @param ids the ids to update
     * @param status the status to set
     * @return true, if successful
     */
    boolean updateVistoryStatus(List<String> ids, RecordStatus status);
    /**
     * UC: Loads last active approved version for a date and origin id.
     * @param originId the id
     * @param date the date
     * @return object or null
     */
    OriginsVistoryRecordPO loadLastActiveApprovedVersion(String originId, Date date);
}
