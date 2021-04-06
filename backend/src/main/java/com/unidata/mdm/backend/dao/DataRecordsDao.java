package com.unidata.mdm.backend.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.po.EtalonDraftStatePO;
import com.unidata.mdm.backend.po.EtalonRecordPO;
import com.unidata.mdm.backend.po.OriginRecordPO;
import com.unidata.mdm.backend.po.RecordKeysPO;


/**
 *
 * @author Mikhail Mikhailov
 */
public interface DataRecordsDao {
    // GET
    /**
     * Gets a golden record by ID.
     *
     * @param id the ID to use.
     * @param softDeleted if set to true, returns also soft deleted records
     * @param merged don't cat off merged records for an id
     * @return a {@link EtalonRecordPO} instance or null
     */
    EtalonRecordPO loadEtalonRecord(final String id, boolean softDeleted, boolean merged);
    /**
     * Loads keys state by etalon id and admin source system.
     * @param id etalon id
     * @param sourceSystem admin source system
     * @return keys or empty list
     */
    List<RecordKeysPO> loadRecordKeysByEtalonId(String id, String sourceSystem);
    /**
     * Loads keys state by etalon id.
     * @param id etalon id
     * @return keys or empty list
     */
    List<RecordKeysPO> loadRecordKeysByEtalonId(String id);
    /**
     * Loads keys state by GSN.
     * @param gsn the gsn
     * @return keys or empty list
     */
    List<RecordKeysPO> loadRecordKeysByGSN(long gsn);
    /**
     * Loads keys state by etalon id, external id and admin source system (enrichments).
     * @param id etalon id
     * @param sourceSystem admin source system
     * @param externalId the external id
     * @param isEnrichment enrichment or not
     * @return keys or empty list
     */
    List<RecordKeysPO> loadRecordKeysByEtalonId(String id, String sourceSystem, String externalId, boolean isEnrichment);
    /**
     * Loads keys state by origin id.
     * @param id origin id
     * @return keys or empty list
     */
    List<RecordKeysPO> loadRecordKeysByOriginId(String id);
    /**
     * Loads keys state by external id.
     * @param externalId external id
     * @param sourceSystem source system
     * @param name entity name
     * @return keys or empty list
     */
    List<RecordKeysPO> loadRecordKeysByExternalId(String externalId, String sourceSystem, String name);
    /**
     * Gets an origin record by ID.
     *
     * @param id the ID to use.
     * @return a {@link OriginRecordPO} instance or null
     */
    OriginRecordPO findOriginRecordById(final String id);

    /**
     * Gets an active origin record by foreign ID, source system name and entity name.
     *
     * @param foreignId the foreign ID to use.
     * @param sourceSystem optional source system name
     * @param entityName the entity name
     * @return a {@link OriginRecordPO} instance or null
     */
    OriginRecordPO findOriginRecordByExternalId(final String foreignId, final String sourceSystem, final String entityName);

    /**
     * Gets origin records by golden record ID.
     *
     * @param etalonId ID of a golden record
     * @param sourceSystem optional source system name
     * @param externalId possibly given external ID
     * @return list of origin records
     */
    List<OriginRecordPO> findOriginRecordsByEtalonId(final String etalonId, final String sourceSystem, String externalId);

    // UPSERT
    /**
     * Updates or insert origin records.
     * @param records the records to upsert
     * @param areNew indicates whether objects are new ones
     */
    void upsertOriginRecords(final List<OriginRecordPO> records, boolean areNew);
    /**
     * Updates or inserts etalon records.
     * @param records the records to upsert
     * @param areNew insert for new, update for existing
     */
    void upsertEtalonRecords(final List<EtalonRecordPO> records, boolean areNew);
    /**
     * Updates or inserts etalon records.
     * @param records the records to upsert
     * @param targetTable target table name
     * @param areNew insert for new, update for existing
     */
    void bulkInsertEtalonRecords(final List<EtalonRecordPO> records, String targetTable);
    /**
     * Updates or insert origin records.
     * @param records the records to upsert
     * @param targetTable target table name
     * @param areNew indicates whether objects are new ones
     */
    void bulkInsertOriginRecords(final List<OriginRecordPO> records, String targetTable);
    /**
     * Updates or inserts etalon records.
     * @param records the records to upsert
     * @param targetTable target table name
     * @param areNew insert for new, update for existing
     */
    void bulkUpdateEtalonRecords(final List<EtalonRecordPO> records, String targetTable);
    /**
     * Updates or insert origin records.
     * @param records the records to upsert
     * @param targetTable target table name
     * @param areNew indicates whether objects are new ones
     */
    void bulkUpdateOriginRecords(final List<OriginRecordPO> records, String targetTable);
    /**
     * Does etalon state cleanup.
     * @param etalonId the relation id
     * @return true if successful
     */
    boolean cleanupEtalonStateDrafts(String etalonId);
    /**
     * Puts etalon state draft.
     * @param etalonId etalon id
     * @param status status, may be null
     * @param createdBy created by, may be null
     * @return
     */
    boolean putEtalonStateDraft(String etalonId, RecordStatus status, String createdBy);
    // DELETE
    /**
     * Deletes a golden record.
     * @param etalonId the record to delete
     * @param operationId the operation id
     * @param state the state
     * @param deleteDate delete date
     * @param cascade delete also origin records (or not)
     * @return true, if successful, false otherwise
     */
    boolean deleteEtalonRecord(String etalonId, String operationId, ApprovalState state, Date deleteDate, boolean cascade);

    /**
     * Deletes an origin record.
     * @param originId the record to delete
     * @param deleteDate delete date
     * @return true, if successful, false otherwise
     */
    boolean deleteOriginRecord(String originId, Date deleteDate);

    /**
     * Wipes origin data (char data, binary data, vistory and origin) from the storage.
     * @param originId the origin id
     * @return true, if successful, false otherwise
     */
    boolean wipeOriginRecord(String originId);

    /**
     * Wipes etalon data (char data, binary data and etalon) from the storage.
     * @param etalonId the etalon id
     * @return true, if successful, false otherwise
     */
    boolean wipeEtalonRecord(String etalonId);

    // OTHERS (Merge, Approve, etc.)
    /**
     * Merges several records, denoted by the looserIds, to one winner.
     * @param masterId the winner id
     * @param duplicateIds the looser ids
     * @param operationId the operation id
     * @param isManual tells whether this merge is a manual (deleting) one
     * @return true, if successful, false otherwise
     */
    boolean mergeRecords(String masterId, List<String> duplicateIds, String operationId, boolean isManual);

    /**
     * Changes approval flag on the etalon record.
     * @param etalonId the etalon id
     * @param approval approval state
     * @return true, if successful, false otherwise
     */
    boolean changeEtalonApproval(String etalonId, ApprovalState approval);
    /**
     * Restore record by etalon id.
     *
     * @param etalonId
     *            etalon id.
     * @param operationId operation id
     * @param  restoreDate restore date
     * @return <code>true</code> if successfully finished, otherwise
     *         <code>false</code>
     */
    boolean restoreEtalonRecord(String etalonId, String operationId, Date restoreDate);
    /**
     * Loads last etalon state draft.
     * @param etalonId the id
     * @return PO object or null
     */
    EtalonDraftStatePO loadLastEtalonStateDraft(String etalonId);

    /**
     * Map of etalon id and list of his active origins
     * @param etalonsIds list of etalons ids
     * @return map of origin id and list of active origins
     */
    Map<String, List<OriginRecordPO>> findAllActiveOriginsForEtlaons(List<String> etalonsIds);
}
