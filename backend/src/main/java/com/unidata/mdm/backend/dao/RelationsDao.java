/**
 *
 */
package com.unidata.mdm.backend.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.dto.RelationDigestDTO;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.RelationSide;
import com.unidata.mdm.backend.po.EtalonRelationDraftStatePO;
import com.unidata.mdm.backend.po.EtalonRelationPO;
import com.unidata.mdm.backend.po.OriginRelationPO;
import com.unidata.mdm.backend.po.OriginsVistoryRelationsPO;
import com.unidata.mdm.backend.po.RelationKeysPO;
import com.unidata.mdm.backend.po.TimeIntervalPO;

/**
 * @author Mikhail Mikhailov
 * Relations vistory DAO component.
 */
public interface RelationsDao {
    /**
     * UC: load timeline by relation etalon id.
     * @param etalonId relation etalon id
     * @param includeDraftVersions include draft versions into view or not
     * @return timeline
     */
    public List<TimeIntervalPO> loadContributingRelationTimeline(String etalonId, boolean includeDraftVersions);
    /**
     * UC: load timeline by relation etalon id and as of date.
     * @param etalonId relation etalon id
     * @param asOf the date
     * @param includeDraftVersions include draft versions into view or not
     * @return timeline
     */
    public List<TimeIntervalPO> loadContributingRelationTimeline(String etalonId, Date asOf, boolean includeDraftVersions);
    /**
     * UC: load timeline by relation etalon id.
     * @param etalonId relation etalon id
     * @param from parent from
     * @param to parent to
     * @param includeDraftVersions include draft versions into view or not
     * @return timeline
     */
    public List<TimeIntervalPO> loadContributingRelationTimeline(String etalonId, Date from, Date to, boolean includeDraftVersions);
    /**
     * UC: load complete contributing timeline for all relations. Calculate contributing time line for an etalon 'from'. Just state, no records returned.
     * @param etalonId the etalon id
     * @param includeDraftVersions include draft versions into view or not
     * @return timeline
     */
    public Map<String, Map<String, List<TimeIntervalPO>>> loadCompleteContributingRelationsTimelineByFromSide(String etalonId, boolean includeDraftVersions);
    /**
     * UC: load complete contributing timeline for all relations. Calculate contributing time line for an etalon 'to'. Just state, no records returned.
     * @param etalonId the etalon id
     * @param includeDraftVersions include draft versions into view or not
     * @return timeline
     */
    public Map<String, Map<String, List<TimeIntervalPO>>> loadCompleteContributingRelationsTimelineByToSide(String etalonId, boolean includeDraftVersions);
    /**
     * UC: load contributing timeline for relations. Calculate contributing time line for an etalon 'from'. Just state, no records returned.
     * @param etalonId the etalon id
     * @param name name of the relation type
     * @param includeDraftVersions include draft versions into view or not
     * @return timeline
     */
    public Map<String, List<TimeIntervalPO>> loadContributingRelationsTimeline(String etalonId, String name, boolean includeDraftVersions);
    /**
     * UC: load contributing timeline for relations. Calculate contributing time line for an etalon 'from' and from boundary.
     * @param etalonId the etalon id
     * @param name name of the relation type
     * @param asOf the date for filtering
     * @param includeDraftVersions  include draft versions into view or not
     * @return timeline
     */
    public Map<String, List<TimeIntervalPO>> loadContributingRelationsTimeline(String etalonId, String name, Date asOf, boolean includeDraftVersions);
    /**
     * UC: load contributing timeline for relations. Calculate contributing time line for an etalon 'from' and from boundary.
     * @param etalonId the etalon id
     * @param name name of the relation type
     * @param from valid from for filtering
     * @param to valid to for filtering
     * @param includeDraftVersions  include draft versions into view or not
     * @return timeline
     */
    public Map<String, List<TimeIntervalPO>> loadContributingRelationsTimeline(String etalonId, String name, Date from, Date to, boolean includeDraftVersions);
    /**
     * Does origins relation upsert operation.
     * @param origin origin object
     * @param isNew do either insert or update
     * @return true, if successful, false otherwise
     */
    boolean upsertOriginRelation(OriginRelationPO origin, boolean isNew);
    /**
     * Does origin relations upsert operation.
     * @param origins origin objects
     * @param isNew do either insert or update
     */
    void upsertOriginRelations(List<OriginRelationPO> origins, boolean isNew);
    /**
     * Does etalons relation upsert operation.
     * @param etalon the etalon object
     * @param isNew is new or not
     * @return true, if successful, false otherwise
     */
    boolean upsertEtalonRelation(EtalonRelationPO etalon, boolean isNew);
    /**
     * Does etalon relations upsert operation.
     * @param etalons the etalon object
     * @param isNew is new or not
     */
    void upsertEtalonRelations(List<EtalonRelationPO> etalons, boolean isNew);
    /**
     * Bulk inserts relation etalon records to a target table via BCOPY.
     * @param records the records to insert
     * @param target the target table
     */
    void bulkInsertEtalonRecords(List<EtalonRelationPO> records, String target);
    /**
     * Bulk inserts relation origin records to a target table via BCOPY.
     * @param records the records to insert
     * @param target the target table
     */
    void bulkInsertOriginRecords(List<OriginRelationPO> records, String target);
    /**
     * Bulk inserts relation etalon records to mass update tables.
     * @param records the records
     * @param targetTable the target table
     */
    void bulkUpdateEtalonRecords(List<EtalonRelationPO> records, String targetTable);
    /**
     * Bulk update relation etalon records to mass update tables.
     * @param records the records
     * @param targetTable the target table
     */
    void bulkUpdateOriginRecords(List<OriginRelationPO> records, String targetTable);
    /**
     * Does etalon state cleanup.
     * @param etalonId the relation id
     * @return true if successful
     */
    public boolean cleanupEtalonStateDrafts(String etalonId);
    /**
     * Loads last etalon state draft.
     * @param etalonId the id
     * @return PO object or null
     */
    EtalonRelationDraftStatePO loadLastEtalonStateDraft(String etalonId);
    /**
     * Changes approval flag on the etalon record.
     * @param etalonId the etalon id
     * @param approval approval state
     * @return true, if successful, false otherwise
     */
    boolean changeEtalonApproval(String etalonId, ApprovalState approval);
    /**
     * Puts a single relations version.
     * @param version a version
     * @return true, if successful, false otherwise
     */
    void putVersion(OriginsVistoryRelationsPO version);
    /**
     * Puts a number of relations versions.
     * @param versions list of versions
     * @return true, if successful, false otherwise
     */
    void putVersions(List<OriginsVistoryRelationsPO> versions);
    /**
     * Bulk inserts relation vistory records to a target table via BCOPY.
     * @param versions the records to insert
     * @param target the target table
     */
    void bulkInsertVersions(List<OriginsVistoryRelationsPO> versions, String target);
    /**
     * Loads relations versions of a particular type for an object etalon ID for a given date.
     * @param etalonId object etalon id
     * @param relationName relation name
     * @param asOf given date
     * @param statuses the statuses
     * @param includeDraftVersions include draft versions or not
     * @return list of relation vistory records
     */
    public Map<String, List<OriginsVistoryRelationsPO>> loadRelationsVersions(
            String etalonId, String relationName, Date asOf, List<RecordStatus> statuses, boolean includeDraftVersions);

    /**
     * Loads relations versions by etalon ID, relation name, view side, from and to dates and list of allowed statuses.
     * @param etalonId the etalon id
     * @param relationName relation name
     * @param sourceSystems source systems
     * @param date from date
     * @param count limit the result with the given count
     * @param from start from the given record
     * @return map
     */
    public RelationDigestDTO loadDigestDestinationEtalonIds(
            String etalonId, String relationName, RelationSide viewSide, Map<String, Integer> sourceSystems, Date date, int count, int from);
    /**
     * Loads all versions for a relation etalon id (needed for etalon calculation).
     * @param etalonId _relation_ etalon id
     * @param asOf as of date
     * @param includeDraftVersions include draft versions into view or not
     * @return list of versions
     */
    public List<OriginsVistoryRelationsPO> loadRelationVersions(String etalonId, Date asOf, boolean includeDraftVersions);
    /**
     * Loads versions by operation id.
     * @param relationEtalonId
     * @param asOf
     * @param operationId
     * @param includeDraftVersions
     * @return
     */
    List<OriginsVistoryRelationsPO> loadRelationVersions(String relationEtalonId, Date asOf, String operationId, boolean includeDraftVersions);
    /**
     * UC: loads active pending versions for an etalon id.
     * @param etalonId the id
     * @param point date point
     * @return list of versions
     */
    public List<OriginsVistoryRelationsPO> loadPendingVersionsByEtalonId(String etalonId, Date asOf);
    /**
     * UC: Decline/approve pending versions of all relations of a record etalon id.
     * @param relationEtalonId the record etalon id
     * @param to the state to update to
     * @return
     */
    public boolean updateApprovalState(String relationEtalonId, ApprovalState to);
    /**
     * Returns etalon relations by from etalon id and status.
     * @param etalonId the etalon id
     * @param statuses statuses
     * @param side the side of the given etalon id
     * @return list
     */
    public List<EtalonRelationPO> loadEtalonRelations(
            String etalonId, String relationName, List<RecordStatus> statuses, RelationSide side);
    /**
     * Loads etalon relation by id.
     * @param relationEtalonId the id
     * @return relation
     */
    public EtalonRelationPO loadEtalonRelation(String relationEtalonId);
    /**
     * Deactivates etalon relation by relation id.
     * @param relationEtalonId the id
     * @param approvalState approval state to set
     * @return true if successful, false otherwise
     */
    public boolean deactivateRelationByEtalonId(String relationEtalonId, ApprovalState approvalState);
    /**
     * Deactivates etalon relation by relation origin id.
     * @param relationOriginId the id
     * @return true if successful, false otherwise
     */
    public boolean deactivateRelationByOriginId(String relationOriginId);
    /**
     * Load one single etalon relation.
     * @param keyFrom key from
     * @param keyTo key to
     * @param name relation name
     * @param statuses statuses
     * @return object or null
     */
    public EtalonRelationPO loadEtalonRelation(String keyFrom, String keyTo, String name, List<RecordStatus> statuses);
    /**
     * Load one single origin relation.
     * @param keyFrom key from
     * @param keyTo key to
     * @param name relation name
     * @param statuses statuses
     * @return object or null
     */
    public OriginRelationPO loadOriginRelation(String keyFrom, String keyTo, String name, List<RecordStatus> statuses);
    /**
     * Loads one single origin relation by origin id.
     * @param relationOriginId the relation's origin id
     * @param statuses statuses of an origin relation, may be null (ACTIVE will be used)
     * @return object or null
     */
    public OriginRelationPO loadOriginRelation(String relationOriginId, List<RecordStatus> statuses);
    /**
     * Loads origin relations by relation etalon id.
     * @param relationEtalonId the relation etalon record id
     * @return list of origin relation records
     */
    public List<OriginRelationPO> loadOriginRelationsByEtalonId(String relationEtalonId);
    /**
     * Load one single origin relation.
     * @param relationEtalonId
     * @param sourceSystem
     * @return object or null
     */
    public OriginRelationPO loadOriginRelationByEtalonIdAndSourceSystem(String relationEtalonId, String sourceSystem);
    /**
     * Loads relation etalon boundary by relation etalon id and date.
     * @param etalonId from etalon id
     * @param point date point
     * @param includeDraftVersions include draft versions into view or not
     * @return boundary as interval
     */
    public TimeIntervalPO loadRelationEtalonBoundary(String etalonId, Date point, boolean includeDraftVersions);
    /**
     * Loads relation etalon boundary.
     * @param etalonId from etalon id
     * @param name relation name
     * @param point date point
     * @param includeDraftVersions include draft versions into view or not
     * @return boundary as interval
     */
    public TimeIntervalPO loadRelationsEtalonBoundary(String etalonId, String name, Date point, boolean includeDraftVersions);
    /**
     * Load origins which are different to supplied origins diff.
     * @param originIdFrom
     * @param originIdsDiff
     * @param relationName
     * @param status
     * @return
     */

    public List<OriginRelationPO> loadOriginsRealtionsDiffByStatus(
            String originIdFrom, List<String> originIdsDiff, String relationName, RecordStatus status);

    /**
     * Load relations that have etalon id as a 'To' key.
     *
     * @param etalonId
     *            etalon id.
     * @param status
     *            relation status.
     * @return List with relations.
     */
    public List<EtalonRelationPO> loadCurrentRelationsToEtalon(String etalonId, RecordStatus status);
    /**
     * Count relations that have etalon id as a 'To' key.
     *
     * @param etalonId
     *            etalon id.
     * @param status
     *            relation status.
     * @return number of relations that have provided etalon id as 'To' key.
     */
    public int countCurrentRelationsToEtalon(String etalonId, RecordStatus status);

    /**
     * Check relation usage (has data) by from etalon ids
     * @param etalonIds etalon ids for check
     * @param relationName relation name for check
     * @return list etalon ids, that have links to relation
     */
    List<String> checkUsageByFromEtalonIds(List<String> etalonIds, String relationName);
    /**
     * Count relations that have etalon id as a 'From' key.
     *
     * @param etalonId
     *            etalon id.
     * @param status
     *            relation status.
     * @return number of relations that have provided etalon id as 'From' key.
     */
    public int countCurrentRelationsFromEtalon(String etalonId, RecordStatus status);

    /**
     *
     * @param relName relation name
     * @return over all count of relations
     */
    long countRelationByName(String relName);

    /**
     * Check exist data for relation by name
     * @param relName relation name
     * @return true if data exist, else false
     */
    boolean checkExistDataRelationByName(String relName);
    /**
     * Mark relations 'MERGED' by 'FROM' record etalon id side.
     * @param ids etalon record ids
     * @param skipRelNames the relation names to skip
     * @param operationId operation id
     * @return number of updated records
     */
    public int markFromEtalonRelationsMerged(List<String> ids, List<String> skipRelNames, String operationId);
    /**
     * Remap 'TO' relation etalons records without history.
     * @param fromEtalonIds ids to remap
     * @param toEtalonId the new id to map to
     * @param operationId operation id
     * @return updated count
     */
    public int remapToEtalonRelations(List<String> fromEtalonIds, String toEtalonId, String operationId);
    /**
     * Remap 'TO' relation etalons records without history.
     * @param fromEtalonIds ids to remap
     * @param toEtalonId the new id to map to
     * @param operationId operation id
     * @return updated count
     */
    public int remapFromEtalonRelations(List<String> fromEtalonIds, String toEtalonId, List<String> m2mRels, String operationId);
    /**
     * Loads keys (both relation and sides) by relation origin id.
     * @param originId relation origin id
     * @return keys
     */
    public RelationKeysPO loadRelationKeysByRelationOriginId(String originId);
    /**
     * Loads keys (both relation and sides) by relation etalon id.
     * @param sourceSystem system source system name
     * @param etalonId etalon id
     * @return keys
     */
    public RelationKeysPO loadRelationKeysByRelationEtalonId(String sourceSystem, String etalonId);
    /**
     * Loads keys (both relation and sides) by relation origin side ids.
     * @param originIdFrom origin id from
     * @param originIdTo origin id to
     * @param name name of the relation
     * @return keys
     */
    public RelationKeysPO loadRelationKeysBySidesOriginIds(String originIdFrom, String originIdTo, String name);
    /**
     * Loads keys (both relation and sides) by relation etalon side ids.
     * @param sourceSystem system source system
     * @param etalonIdFrom etalon id from
     * @param etalonIdTo etalon id to
     * @param name name of the relation
     * @return keys
     */
    public RelationKeysPO loadRelationKeysBySidesEtalonIds(String sourceSystem, String etalonIdFrom, String etalonIdTo, String name);
    /**
     * Wipe origin record by id.
     * @param originId the origin id
     * @return true if successful
     */
    public boolean wipeOriginRecord(String originId);
    /**
     * Wipe etalon record by id.
     * @param etalonId etalon id
     * @return true if successful
     */
    public boolean wipeEtalonRecord(String etalonId);

    /**
     * Mark all relation with relationName as INACTIVE
     * @param relationName - relation name
     */
    void deactivateRelationsByName(String relationName);
}
