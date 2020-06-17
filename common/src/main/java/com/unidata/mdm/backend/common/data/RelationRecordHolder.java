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

/**
 *
 */
package com.unidata.mdm.backend.common.data;

import java.util.Date;

import com.unidata.mdm.backend.common.types.OriginRelation;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.RelationType;

/**
 * @author Mikhail Mikhailov
 * Relation holder suitable for evaluation.
 */
public class RelationRecordHolder
	implements CalculableHolder<OriginRelation> {
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
    public int getRevision() {
        return value.getInfoSection().getRevision();
    }
    /**
	 * {@inheritDoc}
	 */
	@Override
	public CalculableType getCalculableType() {
		return CalculableType.RELATION_TO;
	}
}
