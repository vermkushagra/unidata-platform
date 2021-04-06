package com.unidata.mdm.backend.service.data.driver;

import java.util.Date;

import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author mikhail
 * Holder for {@link DataRecord} objects.
 */
public class RecordHolder
    implements CalculableHolder<OriginRecord> {

    /**
     * The record.
     */
    private final OriginRecord value;
    /**
     * Constructor.
     * @param data the data
     * @param name type name
     * @param sourceSystem the source system
     * @param externalId object's external id
     * @param status the status
     * @param lastUpdate the last update
     */
    public RecordHolder(OriginRecord data) {
        super();
        this.value = data;
    }

    /**
     * @return the value
     */
    @Override
    public OriginRecord getValue() {
        return value;
    }

    /**
     * @return the type name
     */
    @Override
    public String getTypeName() {
        return value.getInfoSection().getOriginKey().getEntityName();
    }

    /**
     * @return the source system
     */
    @Override
    public String getSourceSystem() {
        return value.getInfoSection().getOriginKey().getSourceSystem();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExternalId() {
        return value.getInfoSection().getOriginKey().getExternalId();
    }

    /**
     * @return the status
     */
    @Override
    public RecordStatus getStatus() {
        return value.getInfoSection().getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastUpdate() {
        return value.getInfoSection().getUpdateDate();
    }

    /**
     * @return the calculable type
     */
    @Override
    public CalculableType getCalculableType() {
        return CalculableType.RECORD;
    }
}
