package com.unidata.mdm.backend.common.context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.meta.DQRuleDef;

/**
 * Data quality context.
 *
 * @author ilya.bykov
 * @param <T>
 *            the generic type
 */
public class DQContext<T extends DataRecord> extends CommonRequestContext {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The record. */
    private T record;
    private boolean isModified = false;

    /**
     * Record id.
     */
    private String recordId;

    /**
     * Record valid to
     */
    private Date recordValidTo;

    /**
     * Record valid from
     */
    private Date recordValidFrom;
    /**
     * Entity name.
     */
    private String entityName;
    /**
     * User storage.
     */
    private Map<String, Object> userStorage;

    /** The rules. */
    private List<DQRuleDef> rules;

    /** The errors. */
    private List<DataQualityError> errors;

    /**
     * Instantiates a new DQ context.
     */
    public DQContext() {
        super();
    }

    /**
     * Gets the record.
     *
     * @return the record
     */
    public T getRecord() {
        return record;
    }

    /**
     * With record.
     *
     * @param nestedRecord
     *            the record to set
     * @return the DQ context
     */
    public DQContext<T> withRecord(T nestedRecord) {
        this.record = nestedRecord;
        return this;
    }

    /**
     * Gets the rules.
     *
     * @return the rules
     */
    public List<DQRuleDef> getRules() {
        if (this.rules == null) {
            this.rules = new ArrayList<>();
        }
        return rules;
    }

    /**
     * With rules.
     *
     * @param rules
     *            the rules to set
     * @return the DQ context
     */
    public DQContext<T> withRules(List<DQRuleDef> rules) {
        this.rules = rules;
        return this;
    }

    /**
     * Gets the errors.
     *
     * @return the errors
     */
    public List<DataQualityError> getErrors() {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        return errors;
    }

    /**
     * With errors.
     *
     * @param errors
     *            the errors to set
     * @return the DQ context
     */
    public DQContext<T> withErrors(List<DataQualityError> errors) {
        this.errors = errors;
        return this;
    }

    /**
     * Gets the record id.
     *
     * @return the record id
     */
    public String getRecordId() {
        return recordId;
    }

    /**
     * With record id.
     *
     * @param recordId
     *            the record id
     * @return the DQ context
     */
    public DQContext<T> withRecordId(String recordId){
        this.recordId = recordId;
        return this;
    }

    /**
     * Gets the entity name.
     *
     * @return the entity name
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * With entity name.
     *
     * @param entityName
     *            the entity name
     * @return the DQ context
     */
    public DQContext<T> withEntityName(String entityName){
        this.entityName = entityName;
        return this;
    }

    /**
     * @return record valid to date
     */
    public Date getRecordValidTo() {
        return recordValidTo;
    }

    /**
     * @param recordValidTo record valid to date
     * @return self
     */
    public DQContext<T> withRecordValidTo(Date recordValidTo) {
        this.recordValidTo = recordValidTo;
        return this;
    }

    /**
     * @return record valid from date
     */
    public Date getRecordValidFrom() {
        return recordValidFrom;
    }

    /**
     * @param recordValidFrom record valid from date
     * @return self
     */
    public DQContext<T> withRecordValidFrom(Date recordValidFrom) {
        this.recordValidFrom = recordValidFrom;
        return this;
    }

    
    public Map<String, Object> getUserStorage() {
		return userStorage;
	}

	public DQContext<T> withUserStorage(Map<String, Object> userStorage) {
		this.userStorage = userStorage;
		return this;
	}

	public boolean isModified() {
        return isModified;
    }

    
    public void setModified(boolean isModified) {
        this.isModified = isModified;
    }
}
