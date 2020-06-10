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

package com.unidata.mdm.backend.common.service;

import java.util.List;

import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.dto.data.model.GetEntitiesByRelationSideDTO;
import com.unidata.mdm.backend.common.dto.data.model.GetEntitiesGroupsDTO;
import com.unidata.mdm.backend.common.dto.data.model.GetEntityDTO;
import com.unidata.mdm.backend.common.types.RelationSide;
import com.unidata.mdm.backend.service.measurement.data.MeasurementValue;
import com.unidata.mdm.meta.EntitiesGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.EnumerationDataType;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.Model;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SourceSystemDef;


/**
 * The Interface MetaDraftService.
 */
public interface MetaDraftServiceExt {
	/**
	 * Apply.
	 */
	void apply();

	/**
	 * Removes the draft.
	 */
	void removeDraft();
	/**
	 * Load active draft.
	 */
	void loadActiveDraft();

	/**
	 * Gets the entities groups.
	 *
	 * @return the entities groups
	 */
	GetEntitiesGroupsDTO getEntitiesGroups();

	/**
	 * Update.
	 *
	 * @param ctx the ctx
	 */
	void update(UpdateModelRequestContext ctx);

	/**
	 * Gets the enumerations list.
	 *
	 * @return the enumerations list
	 */
	List<EnumerationDataType> getEnumerationsList();

	/**
	 * Gets the entities list.
	 *
	 * @return the entities list
	 */
	List<EntityDef> getEntitiesList();

	/**
	 * Gets the entity by id.
	 *
	 * @param id the id
	 * @return the entity by id
	 */
	GetEntityDTO getEntityById(String id);

	EntityDef getEntityByIdNoDeps(String id);

	/**
	 * Removes the.
	 *
	 * @param ctx the ctx
	 */
	void remove(DeleteModelRequestContext ctx);

	/**
	 * Gets the lookup entities list.
	 *
	 * @return the lookup entities list
	 */
	List<LookupEntityDef> getLookupEntitiesList();

	/**
	 * Gets the lookup entity by id.
	 *
	 * @param id the id
	 * @return the lookup entity by id
	 */
	LookupEntityDef getLookupEntityById(String id);

	/**
	 * Gets the relations list.
	 *
	 * @return the relations list
	 */
	List<RelationDef> getRelationsList();

	/**
	 * Gets the entities filtered by relation side.
	 *
	 * @param entityName the entity name
	 * @param to the to
	 * @return the entities filtered by relation side
	 */
	GetEntitiesByRelationSideDTO getEntitiesFilteredByRelationSide(String entityName, RelationSide to);

	/**
	 * Gets the source systems list.
	 *
	 * @return the source systems list
	 */
	List<SourceSystemDef> getSourceSystemsList();

	/**
	 * Gets the all values.
	 *
	 * @return the all values
	 */
	List<MeasurementValue> getAllValues();

	/**
	 * Gets the value by id.
	 *
	 * @param id the id
	 * @return the value by id
	 */
	MeasurementValue getValueById(String id);

	/**
	 * Removes the value.
	 *
	 * @param measureValueId the measure value id
	 * @return true, if successful
	 */
	boolean removeValue(String measureValueId);

	/**
	 * Batch remove.
	 *
	 * @param measureValueIds the measure value ids
	 * @param b the b
	 * @param c the c
	 * @return true, if successful
	 */
	boolean batchRemove(List<String> measureValueIds, boolean b, boolean c);

	/**
	 * Save value.
	 *
	 * @param value the value
	 */
	void saveValue(MeasurementValue value);

	/**
	 * Gets the root group.
	 *
	 * @param storageId the storage id
	 * @return the root group
	 */
	EntitiesGroupDef getRootGroup(String storageId);

	/**
	 * Gets the relation by id.
	 *
	 * @param name the name
	 * @return the relation by id
	 */
	RelationDef getRelationById(String name);

	/**
	 * Export model.
	 *
	 * @param storageId the storage id
	 * @return the model
	 */
	Model exportModel(String storageId);

	/**
	 * Gets the enumeration by id.
	 *
	 * @param enumDataType the enum data type
	 * @return the enumeration by id
	 */
	EnumerationDataType getEnumerationById(String enumDataType);

	/**
	 * Gets the source system by id.
	 *
	 * @param sourceSystem the source system
	 * @return the source system by id
	 */
	SourceSystemDef getSourceSystemById(String sourceSystem);

	/**
	 * Gets nested entities list.
	 *
	 * @return the nested entities list
	 */
	List<NestedEntityDef> getNestedEntitiesList();

	/**
	 * Gets the nested entity by id.
	 *
	 * @param elementName the element name
	 * @return the nested entity by id
	 */
	NestedEntityDef getNestedEntityById(String elementName);
	/**
	 * Removes refs to classifier
	 * @param clsfName classifier name
	 */
	void removeRefsToClsf(String clsfName);

}