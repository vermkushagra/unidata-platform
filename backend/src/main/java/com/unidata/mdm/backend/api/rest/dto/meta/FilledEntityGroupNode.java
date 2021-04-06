package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.Collection;

public class FilledEntityGroupNode extends EntityGroupNode {

    private Collection<GroupEntityDefinition> lookupEntities;

    private Collection<GroupEntityDefinition> entities;

    public Collection<GroupEntityDefinition> getLookupEntities() {
        return lookupEntities;
    }

    public FilledEntityGroupNode(String title, String groupName, Collection<GroupEntityDefinition> lookupEntities, Collection<GroupEntityDefinition> entities) {
        super(title, groupName);
        this.lookupEntities = lookupEntities;
        this.entities = entities;
    }

    public FilledEntityGroupNode() {
    }

    public void setLookupEntities(Collection<GroupEntityDefinition> lookupEntities) {
        this.lookupEntities = lookupEntities;
    }

    public Collection<GroupEntityDefinition> getEntities() {
        return entities;
    }

    public void setEntities(Collection<GroupEntityDefinition> entities) {
        this.entities = entities;
    }
}
