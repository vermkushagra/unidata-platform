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

import org.apache.commons.lang3.tuple.Triple;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.po.ClassifierKeysPO;
import com.unidata.mdm.backend.po.EtalonClassifierDraftStatePO;
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
     * @param statuses statuses
     * @return etalons list
     */
    List<EtalonClassifierPO> loadClassifierEtalons(String etalonId, String classifierName, List<RecordStatus> statuses);
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
    List<ClassifierKeysPO> loadPotentialClassifierKeysByRecordEtalonIdAndClassifierName(String sourceSystem, String etalonId, String classifierName);
    /**
     * Loads keys by record origin id, classifier name and node id.
     * @param originId record origin id
     * @param classifierName classifier name
     * @return keys or null
     */
    List<ClassifierKeysPO> loadPotentialClassifierKeysByRecordOriginIdAndClassifierName(String originId, String classifierName);
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
     * @param name the classifier name
     * @return updated count
     */
    int markEtalonClassifiersMerged(List<String> duplicateIds, String name, String operationId);

    /**
     * Remap classifier from one etalon record to another
     * @param fromEtalonRecord owner etalon record id for remap
     * @param toEtalonRecord new etalon record id
     * @param classifierName classifier name
     * @param operationId operation id
     * @return changed classifiers size
     */
    int remapEtalonClassifier(String fromEtalonRecord, String toEtalonRecord,
                              String classifierName, String operationId);
    /**
     * Check usage records in classifiers
     * @param etalonIds records etalon ids
     * @return usage map
     */
    Map<String, List<String>> checkUsageByRecordEtalonIdsSQL(List<String> etalonIds);
    /**
     * Changes approval state of an etalon classifier.
     * @param etalonId the classifier etalon id
     * @param state the approval state
     */
    void changeEtalonApprovalState(String etalonId, ApprovalState state);
    /**
     * Changes approval state of etalon classifier versions.
     * @param etalonId the classifier etalon id
     * @param state the approval state
     */
    void changeVersionsApprovalState(String relationEtalonId, ApprovalState to);
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
    EtalonClassifierDraftStatePO loadLastEtalonStateDraft(String etalonId);
    /**
     * Trimple <origin id, record status, approval state>.
     * @param etalonId the classifier etalon id
     * @return list of version trpples
     */
    List<Triple<String, RecordStatus, ApprovalState>> loadActiveInactiveClassifierVersionsInfo(String etalonId);
}
