package com.unidata.mdm.backend.api.rest.dto.bulk;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.common.types.BulkOperationType;
import org.apache.commons.collections4.CollectionUtils;

public class RemoveRelationsFromBulkOperationsRO extends BulkOperationBaseRO {

    private final List<String> relationsNames = new ArrayList<>();

    @Override
    public BulkOperationType getType() {
        return BulkOperationType.REMOVE_RELATIONS_FROM;
    }

    public List<String> getRelationsNames() {
        return relationsNames;
    }

    public void setRelationsNames(final List<String> relationsNames) {
        this.relationsNames.clear();
        if (CollectionUtils.isNotEmpty(relationsNames)) {
            this.relationsNames.addAll(relationsNames);
        }
    }
}
