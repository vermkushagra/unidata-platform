/**
 *
 */
package com.unidata.mdm.backend.service.data.driver;

import java.util.Date;

import com.unidata.mdm.backend.common.types.OriginRelation;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.RelationType;

/**
 * @author Mikhail Mikhailov
 * Relation holder suitable for evaluation.
 */
public class RelationHolder
	implements CalculableHolder<OriginRelation> {

    /**
     * Relation version of a particular type to hold.
     */
    private final OriginRelation value;
    /**
     * Constructor.
     * @param to the relation object
     */
    public RelationHolder(OriginRelation to) {
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
        return value.getInfoSection().getRelationName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExternalId() {
        return null; // Not applicable
    }

    /**
     * @return the sourceSystem
     */
    @Override
    public String getSourceSystem() {
        return value.getInfoSection().getRelationSourceSystem();
    }

    /**
     * @return the status
     */
    @Override
    public RecordStatus getStatus() {
        return value.getInfoSection().getStatus();
    }

    /**
     * @return the type
     */
    public RelationType getType() {
        return value.getInfoSection().getType();
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
	public CalculableType getCalculableType() {
		return CalculableType.RELATION_TO;
	}
}
