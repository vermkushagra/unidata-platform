/**
 *
 */
package com.unidata.mdm.backend.common.data;

import java.util.Date;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Single attribute holder.
 */
public class RecordAttributeHolder implements CalculableHolder<Attribute> {
    /**
     * The value.
     */
    private final Attribute value;
    /**
     * Attribute name.
     */
    private final String typeName;
    /**
     * Source system.
     */
    private final String sourceSystem;
    /**
     * External id of the record.
     */
    private final String externalId;
    /**
     * LUD.
     */
    private final Date lastUpdate;
    /**
     * The revision.
     */
    private final int revision;
    /**
     * Constructor.
     * @param value attribute value
     * @param typeName record's type name
     * @param sourceSystem record's source system
     * @param externalId record's external id
     * @param lastUpdate the last update
     */
    public RecordAttributeHolder(Attribute value, String typeName, String sourceSystem, String externalId, Date lastUpdate, int revision) {
        super();
        this.value = value;
        this.typeName = typeName;
        this.sourceSystem = sourceSystem;
        this.externalId = externalId;
        this.lastUpdate = lastUpdate;
        this.revision = revision;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Attribute getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeName() {
        return typeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSourceSystem() {
        return sourceSystem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExternalId() {
        return externalId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecordStatus getStatus() {
        return RecordStatus.ACTIVE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculableType getCalculableType() {
        return CalculableType.ATTRIBUTE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRevision() {
        return revision;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApprovalState getApproval() {
        // NOPE for attributes
        return null;
    }
}
