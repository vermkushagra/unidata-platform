package org.unidata.mdm.data.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.unidata.mdm.system.type.pipeline.batch.BatchedPipelineOutput;
import org.unidata.mdm.system.type.pipeline.fragment.FragmentId;
import org.unidata.mdm.system.type.pipeline.fragment.OutputFragment;

/**
 * The all-in-one bulk ops result transfer object.
 * @author Dmitry Kopin on 14.02.2019.
 */
public class RelationsBulkResultDTO extends AbstractBulkResultDTO implements BatchedPipelineOutput, OutputFragment<RelationsBulkResultDTO> {
    /**
     * The id.
     */
    public static final FragmentId<RelationsBulkResultDTO> ID = new FragmentId<>("RELATIONS_BULK_RESULT", RelationsBulkResultDTO::new);
    /**
     * Upserted rels info.
     */
    private List<UpsertRelationsDTO> upsertRelations;
    /**
     * Deleted rels info.
     */
    private List<DeleteRelationsDTO> deleteRelations;
    /**
     * Gets rels upserts.
     * @return upserts
     */
    public List<UpsertRelationsDTO> getUpsertRelations() {
        return Objects.isNull(upsertRelations) ? Collections.emptyList() : upsertRelations;
    }
    /**
     * Sets rels upserts.
     * @param relations
     */
    public void setUpsertRelations(List<UpsertRelationsDTO> relations) {
        this.upsertRelations = relations;
    }
    /**
     * Gets rel deletes.
     * @return deletes
     */
    public List<DeleteRelationsDTO> getDeleteRelations() {
        return Objects.isNull(deleteRelations) ? Collections.emptyList() : deleteRelations;
    }
    /**
     * Sets rels deletes.
     * @param deleteRelations
     */
    public void setDeleteRelations(List<DeleteRelationsDTO> deleteRelations) {
        this.deleteRelations = deleteRelations;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FragmentId<RelationsBulkResultDTO> fragmentId() {
        return ID;
    }
}
