package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.Collection;

public class FlatGroupMapping {

    private Collection<? extends EntityGroupNode> groupNodes;

    public FlatGroupMapping(Collection<? extends EntityGroupNode> groupNodes) {
        this.groupNodes = groupNodes;
    }

    public FlatGroupMapping() {
    }

    public Collection<? extends EntityGroupNode> getGroupNodes() {
        return groupNodes;
    }

    public void setGroupNodes(Collection<? extends EntityGroupNode> groupNodes) {
        this.groupNodes = groupNodes;
    }
}
