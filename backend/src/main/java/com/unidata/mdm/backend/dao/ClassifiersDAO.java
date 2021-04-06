package com.unidata.mdm.backend.dao;

import java.util.Date;
import java.util.List;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.po.ClassifierKeysPO;
import com.unidata.mdm.backend.po.EtalonClassifierPO;
import com.unidata.mdm.backend.po.OriginClassifierPO;
import com.unidata.mdm.backend.po.OriginsVistoryClassifierPO;

/**
 * @author Mikhail Mikhailov
 * Classifier data DAO.
 */
public interface ClassifiersDAO {
    /**
     * Loads classifier etalon records.
     * @param etalonId record etalon id
     * @param classifierName classifier name
     * @param status statuses
     * @return etalons list
     */
    List<EtalonClassifierPO> loadClassifierEtalons(String etalonId, String classifierName, RecordStatus status);
    /**
     * Loads keys by classifier origin id.
     * @param originId the classifer origin id
     * @return keys or null
     */
    ClassifierKeysPO loadClassifierKeysByClassifierOriginId(String originId);
    /**
     * Loads keys by classifier etalon id and source system.
     * @param sourceSystem admin source system
     * @param etalonId classifier etalon id
     * @return keys or null
     */
    ClassifierKeysPO loadClassifierKeysByClassifierEtalonId(String sourceSystem, String etalonId);
    /**
     * Loads keys by admin source system, record etalon id, classifier name and node id.
     * @param sourceSystem admin source system
     * @param etalonId record etalon id
     * @param classifierName classifier name
     * @return keys or null
     */
    ClassifierKeysPO loadClassifierKeysByRecordEtalonIdAndClassifierName(String sourceSystem, String etalonId, String classifierName);
    /**
     * Loads keys by record origin id, classifier name and node id.
     * @param originId record origin id
     * @param classifierName classifier name
     * @return keys or null
     */
    ClassifierKeysPO loadClassifierKeysByRecordOriginIdAndClassifierName(String originId, String classifierName);
    /**
     * Loads classifier data versions.
     * @param classifierEtalonId the classifier etalon id
     * @param asOf for date
     * @param includeDraftVersions whether to include draft versions
     * @return vistory records
     */
    List<OriginsVistoryClassifierPO> loadClassifierVersions(String classifierEtalonId, Date asOf, boolean includeDraftVersions);
    /**
     * Loads classifier data versions.
     * @param classifierEtalonId the classifier etalon id
     * @param asOf for date
     * @param operationId the operation id
     * @param includeDraftVersions whether to include draft versions
     * @return vistory records
     */
    List<OriginsVistoryClassifierPO> loadClassifierVersions(String classifierEtalonId, Date asOf, String operationId, boolean includeDraftVersions);
    /**
     * Loads origin relations by relation etalon id.
     * @param relationEtalonId the relation etalon record id
     * @return list of origin relation records
     */
    List<OriginClassifierPO> loadClassifierOriginsByEtalonId(String relationEtalonId);
    /**
     * Loads origin by id.
     * @param classifierOriginId the id
     * @param statuses statuses
     * @return origin
     */
    OriginClassifierPO loadClassifierOriginById(String classifierOriginId, List<RecordStatus> statuses);
    /**
     * Loads etalon by id.
     * @param classifierEtalonId the id
     * @return classifier etalon
     */
    EtalonClassifierPO loadClassifierEtalonById(String classifierEtalonId);
    /**
     * Upserts origin classifier data record.
     * @param origin the origin
     * @param isNew new or existing
     * @return true, if successful, false otherwise
     */
    boolean upsertOriginClassifier(OriginClassifierPO origin, boolean isNew);
    /**
     * Upserts origin classifier data records.
     * @param origins the origins
     * @param isNew new or existing
     */
    void upsertOriginClassifiers(List<OriginClassifierPO> origins, boolean isNew);
    /**
     * Upserts etalon classifier data record.
     * @param etalon the etalon
     * @param isNew new or existing
     * @return true, if successful, false otherwise
     */
    boolean upsertEtalonClassifier(EtalonClassifierPO etalon, boolean isNew);
    /**
     * Upserts etalon classifier data records.
     * @param etalons the etalon
     * @param isNew new or existing
     */
    void upsertEtalonClassifiers(List<EtalonClassifierPO> etalons, boolean isNew);
    /**
     * Puts data vistory record.
     * @param version data to put
     * @return true, if successful, false otherwise
     */
    boolean putVersion(OriginsVistoryClassifierPO version);
    /**
     * Puts versions.
     * @param versions list of versions
     */
    void putVersions(List<OriginsVistoryClassifierPO> versions);
    /**
     * Inserts classifier data etalon records.
     * @param records the records to upsert
     * @param targetTable target table name
     * @param areNew insert for new, update for existing
     */
    void bulkInsertEtalonRecords(final List<EtalonClassifierPO> records, String targetTable);
    /**
     * Insert classifier data origin records.
     * @param records the records to upsert
     * @param targetTable target table name
     * @param areNew indicates whether objects are new ones
     */
    void bulkInsertOriginRecords(final List<OriginClassifierPO> records, String targetTable);
    /**
     * Updates classifier data etalon records.
     * @param records the records to upsert
     * @param targetTable target table name
     * @param areNew insert for new, update for existing
     */
    void bulkUpdateEtalonRecords(final List<EtalonClassifierPO> records, String targetTable);
    /**
     * Updates classifier data origin records.
     * @param records the records to upsert
     * @param targetTable target table name
     * @param areNew indicates whether objects are new ones
     */
    void bulkUpdateOriginRecords(final List<OriginClassifierPO> records, String targetTable);
    /**
     * Puts several classifier data versions at once in COPY fashion.
     * @param versions the versions to put
     * @param target the target table
     */
    void bulkInsertVersions(List<OriginsVistoryClassifierPO> versions, String target);
    /**
     * Wipe origin record by id.
     * @param originId the origin id
     * @return true if successful
     */
    public boolean wipeClassifierOrigin(String originId);
    /**
     * Wipe etalon record by id.
     * @param etalonId etalon id
     * @return true if successful
     */
    public boolean wipeClassifierEtalon(String etalonId);
    /**
     * Deactivates etalon classifier by etalon id.
     * @param classifierEtalonId the id
     * @param approvalState approval state to set
     * @return true if successful, false otherwise
     */
    public boolean deactivateClassifierEtalon(String classifierEtalonId, ApprovalState approvalState);
    /**
     * Deactivates origin classifier by origin id.
     * @param classifierOriginId the id
     * @return true if successful, false otherwise
     */
    boolean deactivateClassifierOrigin(String classifierOriginId);
    /**
     * Marks classifier records merged.
     * @param duplicateIds the ids
     * @param operationId the operation id
     * @return updated count
     */
    int markEtalonClassifiersMerged(List<String> duplicateIds, String operationId);

}
