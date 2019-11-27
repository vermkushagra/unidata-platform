package org.unidata.mdm.meta.type.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.unidata.mdm.meta.AbstractEntityDef;
import org.unidata.mdm.meta.EnumerationDataType;
import org.unidata.mdm.meta.SourceSystemDef;
import org.unidata.mdm.meta.context.DeleteModelRequestContext;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.meta.context.UpdateModelRequestContext.ModelUpsertType;
import org.unidata.mdm.system.type.event.AbstractLocalEvent;
import org.unidata.mdm.system.util.IdUtils;

/**
 * This event is fired to participants on the local node, mainly for metamodel (post) processing.
 * It must be sent after full cache update by the MMS only.
 * @author Mikhail Mikhailov on Oct 28, 2019
 */
public class ModelUpdateEvent extends AbstractLocalEvent {
    /**
     * This type name.
     */
    private static final String TYPE_NAME = "MODEL_UPDATE_EVENT";
    /**
     * GSVUID.
     */
    private static final long serialVersionUID = -1477873024698579520L;

    private ArrayList<String> deletedNestedEntities;
    private ArrayList<String> deletedEntities;
    private ArrayList<String> deletedLookupEntities;
    private ArrayList<String> deletedRelations;
    private ArrayList<String> deletedEnumerations;
    private ArrayList<String> deletedSourceSystems;

    private ArrayList<String> updatedNestedEntities;
    private ArrayList<String> updatedEntities;
    private ArrayList<String> updatedLookupEntities;
    private ArrayList<String> updatedRelations;
    private ArrayList<String> updatedEnumerations;
    private ArrayList<String> updatedSourceSystems;
    private ArrayList<String> updatedGroups;

    private ModelUpsertType modelUpsertType;

    /**
     * Constructor.
     * @param typeName
     * @param id
     */
    public ModelUpdateEvent(String id) {
        super(TYPE_NAME, id);
    }
    /**
     * @return the entitiesAdded
     */
    public List<String> getDeletedNestedEntities() {
        return CollectionUtils.isEmpty(deletedNestedEntities) ? Collections.emptyList() : deletedNestedEntities;
    }
    /**
     * @return the entitiesDeleted
     */
    public List<String> getDeletedEntities() {
        return CollectionUtils.isEmpty(deletedEntities) ? Collections.emptyList() : deletedEntities;
    }
    /**
     * @return the relationsDeleted
     */
    public List<String> getDeletedRelations() {
        return CollectionUtils.isEmpty(deletedRelations) ? Collections.emptyList() : deletedRelations;
    }
    /**
     * @return the relationsAdded
     */
    public List<String> getDeletedLookupEntities() {
        return CollectionUtils.isEmpty(deletedLookupEntities) ? Collections.emptyList() : deletedLookupEntities;
    }
    /**
     * @return the deletedEnumerations
     */
    public List<String> getDeletedEnumerations() {
        return CollectionUtils.isEmpty(deletedEnumerations) ? Collections.emptyList() : deletedEnumerations;
    }
    /**
     * @return the deletedSourceSystems
     */
    public List<String> getDeletedSourceSystems() {
        return CollectionUtils.isEmpty(deletedSourceSystems) ? Collections.emptyList() : deletedSourceSystems;
    }

    /**
     * @return the entitiesUpdated
     */
    public List<String> getUpdatedEntities() {
        return CollectionUtils.isEmpty(updatedEntities) ? Collections.emptyList() : updatedEntities;
    }
    /**
     * @return the relationsUpdated
     */
    public List<String> getUpdatedRelations() {
        return CollectionUtils.isEmpty(updatedRelations) ? Collections.emptyList() : updatedRelations;
    }
    /**
     * @return the updatedNestedEntities
     */
    public List<String> getUpdatedNestedEntities() {
        return CollectionUtils.isEmpty(updatedNestedEntities) ? Collections.emptyList() : updatedNestedEntities;
    }
    /**
     * @return the updatedLookupEntities
     */
    public List<String> getUpdatedLookupEntities() {
        return CollectionUtils.isEmpty(updatedLookupEntities) ? Collections.emptyList() : updatedLookupEntities;
    }
    /**
     * @return the updatedEnumerations
     */
    public List<String> getUpdatedEnumerations() {
        return CollectionUtils.isEmpty(updatedEnumerations) ? Collections.emptyList() : updatedEnumerations;
    }
    /**
     * @return the updatedSourceSystems
     */
    public List<String> getUpdatedSourceSystems() {
        return CollectionUtils.isEmpty(updatedSourceSystems) ? Collections.emptyList() : updatedSourceSystems;
    }
    /**
     * @return the updatedGroups
     */
    public List<String> getUpdatedGroups() {
        return CollectionUtils.isEmpty(updatedGroups) ? Collections.emptyList() : updatedGroups;
    }
    /**
     * @return the modelUpsertType
     */
    public ModelUpsertType getModelUpsertType() {
        return modelUpsertType;
    }

    public static ModelUpdateEvent of(DeleteModelRequestContext context) {
        ModelUpdateEvent event = new ModelUpdateEvent(IdUtils.v1String());
        event.deletedEntities = context.hasEntitiesIds() ? new ArrayList<>(context.getEntitiesIds()) : null;
        event.deletedRelations = context.hasRelationIds() ? new ArrayList<>(context.getRelationIds()) : null;
        event.deletedNestedEntities = context.hasNestedEntitiesIds() ? new ArrayList<>(context.getNestedEntitiesIds()) : null;
        event.deletedLookupEntities = context.hasLookupEntitiesIds() ? new ArrayList<>(context.getLookupEntitiesIds()) : null;
        event.deletedEnumerations = context.hasEnumerationIds() ? new ArrayList<>(context.getEnumerationIds()) : null;
        event.deletedSourceSystems = context.hasSourceSystemIds() ? new ArrayList<>(context.getSourceSystemIds()) : null;
        event.storageId = context.getStorageId();
        return event;
    }

    public static ModelUpdateEvent of(UpdateModelRequestContext context) {
        ModelUpdateEvent event = new ModelUpdateEvent(IdUtils.v1String());
        event.updatedEntities = context.hasEntityUpdate() ? new ArrayList<>(context.getEntityUpdate().stream().map(AbstractEntityDef::getName).collect(Collectors.toList())) : null;
        event.updatedEnumerations = context.hasEnumerationUpdate() ? new ArrayList<>(context.getEnumerationsUpdate().stream().map(EnumerationDataType::getName).collect(Collectors.toList())) : null;
        event.updatedGroups = context.hasEntitiesGroupUpdate() ? new ArrayList<>(Collections.singletonList(context.getEntitiesGroupsUpdate().getGroupName())) : null;
        event.updatedLookupEntities = context.hasLookupEntityUpdate() ? new ArrayList<>(context.getLookupEntityUpdate().stream().map(AbstractEntityDef::getName).collect(Collectors.toList())) : null;
        event.updatedNestedEntities = context.hasNestedEntityUpdate() ? new ArrayList<>(context.getNestedEntityUpdate().stream().map(AbstractEntityDef::getName).collect(Collectors.toList())) : null;
        event.updatedRelations = context.hasRelationsUpdate() ? new ArrayList<>(context.getRelationsUpdate().stream().map(AbstractEntityDef::getName).collect(Collectors.toList())) : null;
        event.updatedSourceSystems = context.hasSourceSystemsUpdate() ? new ArrayList<>(context.getSourceSystemsUpdate().stream().map(SourceSystemDef::getName).collect(Collectors.toList())) : null;
        event.modelUpsertType = context.getUpsertType();
        event.storageId = context.getStorageId();
        return event;
    }
}
