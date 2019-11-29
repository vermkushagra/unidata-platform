package org.unidata.mdm.meta.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.unidata.mdm.system.dto.AbstractCompositeResult;
import org.unidata.mdm.system.dto.PipelineExecutionResult;

/**
 * @author Mikhail Mikhailov on Nov 28, 2019
 */
public class GetModelDTO extends AbstractCompositeResult implements PipelineExecutionResult {
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
}
