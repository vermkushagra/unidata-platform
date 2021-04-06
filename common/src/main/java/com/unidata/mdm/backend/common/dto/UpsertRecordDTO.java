/**
 *
 */
package com.unidata.mdm.backend.common.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.UpsertAction;

/**
 * @author Mikhail Mikhailov
 * Upsert result DTO.
 */
public class UpsertRecordDTO
implements RecordDTO, EtalonRecordDTO, OriginRecordDTO, RelationsDTO<UpsertRelationDTO>, ClassifiersDTO<UpsertClassifierDTO> {
    /**
     * Record keys for short upsert.
     */
    private RecordKeys recordKeys;
    /**
     * Origin record or null.
     */
    private OriginRecord origin;
    /**
     * Golden record or null.
     */
    private EtalonRecord etalon;
    /**
     * DQ errors or null.
     */
    private List<DataQualityError> dqErrors;
    /**
     * Actual action.
     */
    private UpsertAction action;
    /**
     * Number of duplicates.
     */
    private List<String> duplicateIds;
    /**
     * Relations.
     */
    private Map<RelationStateDTO, List<UpsertRelationDTO>> relations;
    /**
     * Classifiers.
     */
    private Map<String, List<UpsertClassifierDTO>> classifiers;
    /**
     * list of errors
     */
    private List<ErrorInfoDTO> errors;
    /**
     * Constructor.
     * @param type
     * @param throwable
     */
    public UpsertRecordDTO(UpsertAction type) {
        super();
        this.action = type;
    }
    /**
     * Constructor.
     * @param origin the origin record to upsert
     * @param type action performed
     * @param dqErrors list of DQ errors
     */
    public UpsertRecordDTO(OriginRecord origin, UpsertAction type, List<DataQualityError> dqErrors) {
        super();
        this.origin = origin;
        this.etalon = null;
        this.action = type;
        this.dqErrors = dqErrors;
        this.duplicateIds = null;
    }

    /**
     * Constructor.
     * @param etalon the etalon record to upsert
     * @param type action performed
     * @param dqErrors list of DQ errors
     * @param duplicateIds list of duplicate ids
     */
    public UpsertRecordDTO(EtalonRecord etalon, UpsertAction type, List<DataQualityError> dqErrors, List<String> duplicateIds) {
        super();
        this.origin = null;
        this.etalon = etalon;
        this.action = type;
        this.dqErrors = dqErrors;
        this.duplicateIds = duplicateIds;
    }

    /**
     * @return the action
     */
    public UpsertAction getAction() {
        return action;
    }

    /**
     * @param action
     *            the action to set
     */
    public void setAction(UpsertAction action) {
        this.action = action;
    }

    /**
     * @return the origin record
     */
    @Override
    public OriginRecord getOrigin() {
        return origin;
    }

    /**
     * @return the golden record
     */
    @Override
    public EtalonRecord getEtalon() {
        return etalon;
    }

    /**
     * @return true, if result is a golden record
     */
    public boolean isEtalon() {
        return etalon != null;
    }

    /**
     * @return true, if result is an origin record
     */
    public boolean isOrigin() {
        return origin != null;
    }

    /**
     * @return the dqErrors
     */
    public List<DataQualityError> getDqErrors() {
        if (dqErrors == null) {
            dqErrors = new ArrayList<>();
        }
        return dqErrors;
    }

    /**
     * @return the duplicateIds
     */
    public List<String> getDuplicateIds() {
        if (duplicateIds == null) {
            duplicateIds = new ArrayList<>();
        }

        return duplicateIds;
    }

    /**
     * @param origin the origin to set
     */
    public void setOrigin(OriginRecord origin) {
        this.origin = origin;
    }

    /**
     * @param golden the golden to set
     */
    public void setEtalon(EtalonRecord golden) {
        this.etalon = golden;
    }

    /**
     * @return the keys
     */
    @Override
    public RecordKeys getRecordKeys() {
        return recordKeys;
    }
    /**
     * Will be replaced by {@link RecordDTO#getRecordKeys()} in 4.6.
     * @return keys
     */
    @Deprecated
    public RecordKeys getKeys() {
        return recordKeys;
    }
    /**
     * @param keys the keys to set
     */
    public void setRecordKeys(RecordKeys keys) {
        this.recordKeys = keys;
    }

    /**
     * @param relations the relations to set
     */
    public void setRelations(Map<RelationStateDTO, List<UpsertRelationDTO>> relations) {
        this.relations = relations;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<RelationStateDTO, List<UpsertRelationDTO>> getRelations() {
        return relations;
    }
    /**
     * @param classifiers the classifiers to set
     */
    public void setClassifiers(Map<String, List<UpsertClassifierDTO>> classifiers) {
        this.classifiers = classifiers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<UpsertClassifierDTO>> getClassifiers() {
        return classifiers;
    }


    public List<ErrorInfoDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorInfoDTO> errors) {
        this.errors = errors;
    }
}
