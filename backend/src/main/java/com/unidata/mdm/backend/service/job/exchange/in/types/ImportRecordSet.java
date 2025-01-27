package com.unidata.mdm.backend.service.job.exchange.in.types;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.context.DeleteClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.UpsertClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.OriginClassifier;

public class ImportRecordSet extends ImportDataSet {
    /**
     * Classifiers to import.
     */
    private Collection<OriginClassifier> classifiers;
    /**
     * The origin key.
     */
    private OriginKey originKey;
    /**
     * Etalon key.
     */
    private EtalonKey etalonKey;
    /**
     * Record upserts.
     */
    private UpsertRequestContext recordUpsert;
    /**
     * Record deletes
     */
    private DeleteRequestContext recordDelete;
    /**
     * Standalone classifier upserts.
     */
    private UpsertClassifiersDataRequestContext classifiersUpsert;
    /**
     * Standalone classifier upserts.
     */
    private DeleteClassifiersDataRequestContext classifiersDelete;
    /**
     * Constructor.
     * @param data the data
     */
    public ImportRecordSet(DataRecord data) {
        super(data);
    }
    /**
     * Gets classifier data.
     * @return classifier data
     */
    @Nonnull
    public Collection<OriginClassifier> getClassifiers() {
        if(classifiers == null){
            classifiers =  new ArrayList<>();
        }
        return classifiers;
    }
    /**
     * @return the originKey
     */
    public OriginKey getOriginKey() {
        return originKey;
    }
    /**
     * @param originKey the originKey to set
     */
    public void setOriginKey(OriginKey originKey) {
        this.originKey = originKey;
    }
    /**
     * @return the etalonKey
     */
    public EtalonKey getEtalonKey() {
        return etalonKey;
    }
    /**
     * @param etalonKey the etalonKey to set
     */
    public void setEtalonKey(EtalonKey etalonKey) {
        this.etalonKey = etalonKey;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRecord() {
        return true;
    }
    /**
     * @return the recordUpserts
     */
    public UpsertRequestContext getRecordUpsert() {
        return recordUpsert;
    }
    /**
     * @return the recordDeletes
     */
    public DeleteRequestContext getRecordDelete() {
        return recordDelete;
    }
    /**
     * @return the classifierUpserts
     */
    public UpsertClassifiersDataRequestContext getClassifiersUpsert() {
        return classifiersUpsert;
    }
    /**
     * @return the classifierDeletes
     */
    public DeleteClassifiersDataRequestContext getClassifiersDelete() {
        return classifiersDelete;
    }
    /**
     * @param recordUpsert the recordUpsert to set
     */
    public void setRecordUpsert(UpsertRequestContext recordUpsert) {
        this.recordUpsert = recordUpsert;
    }
    /**
     * @param recordDelete the recordDelete to set
     */
    public void setRecordDelete(DeleteRequestContext recordDelete) {
        this.recordDelete = recordDelete;
    }
    /**
     * @param classifiersUpsert the classifiersUpsert to set
     */
    public void setClassifiersUpsert(UpsertClassifiersDataRequestContext classifiersUpsert) {
        this.classifiersUpsert = classifiersUpsert;
    }
    /**
     * @param classifiersDelete the classifiersDelete to set
     */
    public void setClassifiersDelete(DeleteClassifiersDataRequestContext classifiersDelete) {
        this.classifiersDelete = classifiersDelete;
    }
}
