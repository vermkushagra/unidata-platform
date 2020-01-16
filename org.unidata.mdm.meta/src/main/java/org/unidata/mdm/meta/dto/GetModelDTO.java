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

package org.unidata.mdm.meta.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.unidata.mdm.system.dto.AbstractCompositeResult;
import org.unidata.mdm.system.type.pipeline.PipelineOutput;

/**
 * @author Mikhail Mikhailov on Nov 28, 2019
 */
public class GetModelDTO extends AbstractCompositeResult implements PipelineOutput {
    /**
     * Entities.
     */
    private List<GetEntityDTO> entities;
    /**
     * Entities.
     */
    private List<GetModelLookupDTO> lookups;
    /**
     * Relations.
     */
    private List<GetModelRelationDTO> relations;
    /**
     * Enumerations.
     */
    private List<GetModelEnumerationDTO> enumerations;
    /**
     * Source systems.
     */
    private List<GetModelSourceSystemDTO> sourceSystems;
    /**
     * MMVs.
     */
    private List<GetModelMeasurementValueDTO> measurementValues;
    /**
     * EGR.
     */
    private GetEntitiesGroupsDTO entityGroups;
    /**
     * Constructor.
     */
    public GetModelDTO() {
        super();
    }
    /**
     * @return the entities
     */
    public List<GetEntityDTO> getEntities() {
        return Objects.isNull(entities) ? Collections.emptyList() : entities;
    }
    /**
     * @param entities the entities to set
     */
    public void setEntities(List<GetEntityDTO> entities) {
        this.entities = entities;
    }
    /**
     * @return the lookups
     */
    public List<GetModelLookupDTO> getLookups() {
        return Objects.isNull(lookups) ? Collections.emptyList() : lookups;
    }
    /**
     * @param lookups the lookups to set
     */
    public void setLookups(List<GetModelLookupDTO> lookups) {
        this.lookups = lookups;
    }
    /**
     * @return the relations
     */
    public List<GetModelRelationDTO> getRelations() {
        return Objects.isNull(relations) ? Collections.emptyList() : relations;
    }
    /**
     * @param relations the relations to set
     */
    public void setRelations(List<GetModelRelationDTO> relations) {
        this.relations = relations;
    }
    /**
     * @return the enumerations
     */
    public List<GetModelEnumerationDTO> getEnumerations() {
        return Objects.isNull(enumerations) ? Collections.emptyList() : enumerations;
    }
    /**
     * @param enumerations the enumerations to set
     */
    public void setEnumerations(List<GetModelEnumerationDTO> enumerations) {
        this.enumerations = enumerations;
    }
    /**
     * @return the sourceSystems
     */
    public List<GetModelSourceSystemDTO> getSourceSystems() {
        return Objects.isNull(sourceSystems) ? Collections.emptyList() : sourceSystems;
    }
    /**
     * @param sourceSystems the sourceSystems to set
     */
    public void setSourceSystems(List<GetModelSourceSystemDTO> sourceSystems) {
        this.sourceSystems = sourceSystems;
    }
    /**
     * @return the measurementValues
     */
    public List<GetModelMeasurementValueDTO> getMeasurementValues() {
        return Objects.isNull(measurementValues) ? Collections.emptyList() : measurementValues;
    }
    /**
     * @param measurementValues the measurementValues to set
     */
    public void setMeasurementValues(List<GetModelMeasurementValueDTO> measurementValues) {
        this.measurementValues = measurementValues;
    }
    /**
     * @return the entityGroups
     */
    public GetEntitiesGroupsDTO getEntityGroups() {
        return entityGroups;
    }
    /**
     * @param entityGroups the entityGroups to set
     */
    public void setEntityGroups(GetEntitiesGroupsDTO entityGroups) {
        this.entityGroups = entityGroups;
    }
}
