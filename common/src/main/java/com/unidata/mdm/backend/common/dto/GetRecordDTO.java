package com.unidata.mdm.backend.common.dto;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.dto.security.ResourceSpecificRightDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTaskDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.TypeOfChange;

/**
 * @author Mikhail Mikhailov
 *         Get record result DTO.
 */
public class GetRecordDTO
implements RecordDTO, OriginRecordsDTO, EtalonRecordDTO, RelationsDTO<GetRelationDTO>, ClassifiersDTO<GetClassifierDTO> {
    /**
     * Record keys.
     */
    private RecordKeys recordKeys;
    /**
     * Rights.
     */
    private ResourceSpecificRightDTO rights;
    /**
     * Minimum lower bound.
     */
    private Date rangeFromMax;
    /**
     * Maximum upper bound.
     */
    private Date rangeToMin;
    /**
     * Golden record.
     */
    private EtalonRecord etalon;
    /**
     * 0 or more origin records.
     */
    private List<OriginRecord> origins;
    /**
     * List of DQ errors.
     */
    private List<DataQualityError> dqErrors;
    /**
     * Accessory map
     */
    private Map<String, String> attributeWinnerMap;
    /**
     * Version field.
     */
    private int version;
    /**
     * Tasks state.
     */
    private List<WorkflowTaskDTO> tasks;
    /**
     * The relations.
     */
    private Map<RelationStateDTO, List<GetRelationDTO>> relations = Collections.emptyMap();
    /**
     * Classifiers data, if requested.
     */
    private Map<String, List<GetClassifierDTO>> classifiers = Collections.emptyMap();
    /**
     * Diff to draft.
     */
    private Map<String, Map<TypeOfChange, Attribute>> diffToDraft = Collections.emptyMap();
    /**
     * Diff to previous state.
     */
    private Map<String, Map<TypeOfChange, Attribute>> diffToPrevious = Collections.emptyMap();
    /**
     * Constructor.
     */
    public GetRecordDTO() {
        super();
    }
    /**
     * Constructor.
     * @param keys the keys
     */
    public GetRecordDTO(RecordKeys keys) {
        super();
        this.recordKeys = keys;
    }
    /**
     * @return the recordKeys
     */
    @Override
    public RecordKeys getRecordKeys() {
        return recordKeys;
    }

    /**
     * @param recordKeys the recordKeys to set
     */
    public void setRecordKeys(RecordKeys recordKeys) {
        this.recordKeys = recordKeys;
    }

    /**
     * @return the rangeFromMax
     */
    public Date getRangeFromMax() {
        return rangeFromMax;
    }

    /**
     * @param rangeFromMax the rangeFromMax to set
     */
    public void setRangeFromMax(Date rangeFromMax) {
        this.rangeFromMax = rangeFromMax;
    }

    /**
     * @return the rangeToMin
     */
    public Date getRangeToMin() {
        return rangeToMin;
    }

    /**
     * @param rangeFromMin the rangeToMin to set
     */
    public void setRangeToMin(Date rangeFromMin) {
        this.rangeToMin = rangeFromMin;
    }

    /**
     * @return the goldenRecord
     */
    @Override
    public EtalonRecord getEtalon() {
        return etalon;
    }

    /**
     * @param goldenRecord the goldenRecord to set
     */
    public void setEtalon(EtalonRecord goldenRecord) {
        this.etalon = goldenRecord;
    }

    /**
     * @return the origins
     */
    @Override
    public List<OriginRecord> getOrigins() {
        return origins == null ? Collections.emptyList() : origins;
    }

    /**
     * @param originRecords the origins to set
     */
    public void setOrigins(List<OriginRecord> originRecords) {
        this.origins = originRecords;
    }

    /**
     * @param relations the relations to set
     */
    public void setRelations(Map<RelationStateDTO, List<GetRelationDTO>> relations) {
        this.relations = relations;
    }

    /**
     * @return the relations
     */
    @Override
    public Map<RelationStateDTO, List<GetRelationDTO>> getRelations() {
        return relations == null ? Collections.emptyMap() : relations;
    }

    /**
     * @param classifiers the classifiers to set
     */
    public void setClassifiers(Map<String, List<GetClassifierDTO>> classifiers) {
        this.classifiers = classifiers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<GetClassifierDTO>> getClassifiers() {
        return classifiers == null ? Collections.emptyMap() : classifiers;
    }

    /**
     * @return the dqErrors
     */
    public List<DataQualityError> getDqErrors() {
        return dqErrors;
    }

    /**
     * @param dqErrors the dqErrors to set
     */
    public void setDqErrors(List<DataQualityError> dqErrors) {
        this.dqErrors = dqErrors;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * @return the tasks
     */
    public List<WorkflowTaskDTO> getTasks() {
        return tasks;
    }

    /**
     * @param tasks the tasks to set
     */
    public void setTasks(List<WorkflowTaskDTO> tasks) {
        this.tasks = tasks;
    }

    /**
     * @return
     */
    public Map<String, String> getAttributeWinnersMap() {
        return attributeWinnerMap;
    }

    /**
     * @param attributeWinnerMap
     */
    public void setAttributeWinnerMap(Map<String, String> attributeWinnerMap) {
        this.attributeWinnerMap = attributeWinnerMap;
    }

    /**
     * @return the rights
     */
    public ResourceSpecificRightDTO getRights() {
        return rights;
    }

    /**
     * @param rights the rights to set
     */
    public void setRights(ResourceSpecificRightDTO rights) {
        this.rights = rights;
    }
    /**
     * @return the diffToDraft
     */
    public Map<String, Map<TypeOfChange, Attribute>> getDiffToDraft() {
        return diffToDraft;
    }
    /**
     * @param diffToDraft the diffToDraft to set
     */
    public void setDiffToDraft(Map<String, Map<TypeOfChange, Attribute>> diffToDraft) {
        this.diffToDraft = diffToDraft;
    }
    /**
     * @return the diffToPrevious
     */
    public Map<String, Map<TypeOfChange, Attribute>> getDiffToPrevious() {
        return diffToPrevious;
    }
    /**
     * @param diffToPrevious the diffToPrevious to set
     */
    public void setDiffToPrevious(Map<String, Map<TypeOfChange, Attribute>> diffToPrevious) {
        this.diffToPrevious = diffToPrevious;
    }
}
