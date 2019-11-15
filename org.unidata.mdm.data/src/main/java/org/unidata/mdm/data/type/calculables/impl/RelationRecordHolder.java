package org.unidata.mdm.data.type.calculables.impl;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.core.type.calculables.impl.AbstractCalculableHolder;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.keys.OriginKey;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.data.RelationType;

/**
 * @author Mikhail Mikhailov
 * Relation holder suitable for evaluation.
 */
public class RelationRecordHolder extends AbstractCalculableHolder<OriginRelation> {
    /**
     * Relation version of a particular type to hold.
     */
    private final OriginRelation value;
    /**
     * Constructor.
     * @param to the relation object
     */
    public RelationRecordHolder(OriginRelation to) {
        super();
        this.value = to;
    }
    /**
     * @return the relation
     */
    @Override
    public OriginRelation getValue() {
        return value;
    }
    /**
     * @return the name
     */
    @Override
    public String getTypeName() {
        return value.getInfoSection().getRelationType() == RelationType.CONTAINS
                ? value.getInfoSection().getToEntityName()
                : value.getInfoSection().getRelationName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getExternalId() {
        return StringUtils.join(
                Objects.nonNull(value.getInfoSection().getRelationOriginKey().getFrom())
                    ? value.getInfoSection().getRelationOriginKey().getFrom().getExternalId()
                    : StringUtils.EMPTY, "|",
            value.getInfoSection().getRelationOriginKey().getTo().getExternalId());
    }
    /**
     * @return the sourceSystem
     */
    @Override
    public String getSourceSystem() {
        return value.getInfoSection().getRelationOriginKey().getSourceSystem();
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
    public ApprovalState getApproval() {
        return value.getInfoSection().getApproval();
    }
    /**
     * @return the type
     */
    public RelationType getType() {
        return value.getInfoSection().getRelationType();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastUpdate() {
        return value.getInfoSection().getUpdateDate();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int getRevision() {
        return value.getInfoSection().getRevision();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Date getValidFrom() {
        return value.getInfoSection().getValidFrom();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Date getValidTo() {
        return value.getInfoSection().getValidTo();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnrichment() {
        return value.getInfoSection().getRelationOriginKey().isEnrichment();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public OriginKey getOriginKey() {
        return value.getInfoSection().getRelationOriginKey();
    }
}
